package de.charite.compbio.jannovar.cmd.annotate_vcf;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

import java.io.File;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.ProgressReporter;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationCommand;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.facade.DBVariantContextAnnotator;
import de.charite.compbio.jannovar.vardbs.facade.DBVariantContextAnnotatorFactory;

/**
 * Run annotation steps (read in VCF, write out VCF or Jannovar file format).
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public class AnnotateVCFCommand extends JannovarAnnotationCommand {

	/** Currently considered {@link VariantContext}, for progress reporting */
	private VariantContext currentVC;
	/** Progress reporting */
	private ProgressReporter progressReporter;

	public AnnotateVCFCommand(String[] argv) throws CommandLineParsingException, HelpRequestedException {
		super(argv);
		if (this.options.verbosity >= 2)
			this.progressReporter = new ProgressReporter(this::getCurrentVC, 60);
		else
			this.progressReporter = null;
	}

	/**
	 * This function inputs a VCF file, and prints the annotated version thereof to a file (name of the original file
	 * with the suffix .de.charite.compbio.jannovar).
	 *
	 * @throws JannovarException
	 *             on problems with the annotation
	 */
	@Override
	public void run() throws JannovarException {
		System.err.println("Options");
		options.print(System.err);

		deserializeTranscriptDefinitionFile();

		if (progressReporter != null)
			progressReporter.start();
		for (String vcfPath : options.vcfFilePaths) {
			// initialize the VCF reader
			try (VCFFileReader vcfReader = new VCFFileReader(new File(vcfPath), false)) {
				VCFHeader vcfHeader = vcfReader.getFileHeader();
				System.err.println("Annotating VCF...");
				final long startTime = System.nanoTime();

				Stream<VariantContext> stream = vcfReader.iterator().stream();

				// Make current VC available to progress printer
				stream = stream.peek(x -> this.currentVC = x);

				// If configured, annotate using dbSNP VCF file (extend header to use for writing out)
				if (options.pathVCFDBSNP != null) {
					DBAnnotationOptions dbSNPOptions = DBAnnotationOptions.createDefaults();
					dbSNPOptions.setIdentifierPrefix(options.prefixDBSNP);
					DBVariantContextAnnotator dbSNPAnno = new DBVariantContextAnnotatorFactory()
							.constructDBSNP(options.pathVCFDBSNP, options.pathFASTARef, dbSNPOptions);
					dbSNPAnno.extendHeader(vcfHeader);
					stream = stream.map(dbSNPAnno::annotateVariantContext);
				}

				// If configured, annotate using ExAC VCF file (extend header to use for writing out)
				if (options.pathVCFExac != null) {
					DBAnnotationOptions exacOptions = DBAnnotationOptions.createDefaults();
					exacOptions.setIdentifierPrefix(options.prefixExac);
					DBVariantContextAnnotator exacAnno = new DBVariantContextAnnotatorFactory()
							.constructExac(options.pathVCFExac, options.pathFASTARef, exacOptions);
					exacAnno.extendHeader(vcfHeader);
					stream = stream.map(exacAnno::annotateVariantContext);
				}

				// Write result to output file
				try (AnnotatedVCFWriter writer = new AnnotatedVCFWriter(refDict, vcfHeader, chromosomeMap, vcfPath,
						options, args)) {
					stream.forEachOrdered(writer::put);

					System.err.println("Wrote annotations to \"" + writer.getOutFileName() + "\"");
					final long endTime = System.nanoTime();
					System.err.println(String.format("Annotation and writing took %.2f sec.",
							(endTime - startTime) / 1000.0 / 1000.0 / 1000.0));
				}
			}
		}
		if (progressReporter != null)
			progressReporter.stop();
	}

	@Override
	protected JannovarOptions parseCommandLine(String[] argv)
			throws CommandLineParsingException, HelpRequestedException {
		AnnotateVCFCommandLineParser parser = new AnnotateVCFCommandLineParser();
		try {
			return parser.parse(argv);
		} catch (ParseException e) {
			throw new CommandLineParsingException("Could not parse the command line.", e);
		}
	}

	/** @return current {@link VariantContext}, for progress reporting */
	private VariantContext getCurrentVC() {
		return currentVC;
	}

}
