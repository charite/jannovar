package de.charite.compbio.jannovar.cmd.annotate_vcf;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationCommand;
import de.charite.compbio.jannovar.mendel.IncompatiblePedigreeException;
import de.charite.compbio.jannovar.mendel.bridge.MendelVCFHeaderExtender;
import de.charite.compbio.jannovar.mendel.filter.ConsumerProcessor;
import de.charite.compbio.jannovar.mendel.filter.CoordinateSortingChecker;
import de.charite.compbio.jannovar.mendel.filter.GeneWiseMendelianAnnotationProcessor;
import de.charite.compbio.jannovar.mendel.filter.VariantContextFilterException;
import de.charite.compbio.jannovar.mendel.filter.VariantContextProcessor;
import de.charite.compbio.jannovar.pedigree.PedFileContents;
import de.charite.compbio.jannovar.pedigree.PedFileReader;
import de.charite.compbio.jannovar.pedigree.PedParseException;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.progress.GenomeRegionListFactoryFromSAMSequenceDictionary;
import de.charite.compbio.jannovar.progress.ProgressReporter;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.facade.DBVariantContextAnnotator;
import de.charite.compbio.jannovar.vardbs.facade.DBVariantContextAnnotatorFactory;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

/**
 * Run annotation steps (read in VCF, write out VCF or Jannovar file format).
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public class AnnotateVCFCommand extends JannovarAnnotationCommand {

	/** Progress reporting */
	private ProgressReporter progressReporter = null;

	public AnnotateVCFCommand(String[] argv) throws CommandLineParsingException, HelpRequestedException {
		super(argv);
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

		for (String vcfPath : options.vcfFilePaths) {
			// initialize the VCF reader
			try (VCFFileReader vcfReader = new VCFFileReader(new File(vcfPath), false)) {
				if (this.options.verbosity >= 1) {
					final SAMSequenceDictionary seqDict = VCFFileReader.getSequenceDictionary(new File(vcfPath));
					final GenomeRegionListFactoryFromSAMSequenceDictionary factory = new GenomeRegionListFactoryFromSAMSequenceDictionary();
					this.progressReporter = new ProgressReporter(factory.construct(seqDict), 60);
					this.progressReporter.printHeader();
					this.progressReporter.start();
				}

				VCFHeader vcfHeader = vcfReader.getFileHeader();

				System.err.println("Annotating VCF...");
				final long startTime = System.nanoTime();

				Stream<VariantContext> stream = vcfReader.iterator().stream();

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

				// If configured, annotate using UK10K VCF file (extend header to use for writing out)
				if (options.pathVCFUK10K != null) {
					DBAnnotationOptions exacOptions = DBAnnotationOptions.createDefaults();
					exacOptions.setIdentifierPrefix(options.prefixUK10K);
					DBVariantContextAnnotator uk10kAnno = new DBVariantContextAnnotatorFactory()
							.constructUK10K(options.pathVCFUK10K, options.pathFASTARef, exacOptions);
					uk10kAnno.extendHeader(vcfHeader);
					stream = stream.map(uk10kAnno::annotateVariantContext);
				}

				// Extend header with INHERITANCE filter
				if (options.pathPedFile != null) {
					System.err.println("Extending header with INHERITANCE...");
					new MendelVCFHeaderExtender().extendHeader(vcfHeader, "");
				}

				// Write result to output file
				try (AnnotatedVCFWriter writer = new AnnotatedVCFWriter(refDict, vcfHeader, chromosomeMap, vcfPath,
						options, args); VariantContextProcessor sink = buildMendelianProcessors(writer);) {
					// Make current VC available to progress printer
					if (this.progressReporter != null)
						stream = stream.peek(vc -> this.progressReporter.setCurrentVC(vc));

					stream.forEachOrdered(sink::put);

					System.err.println("Wrote annotations to \"" + writer.getOutFileName() + "\"");
					final long endTime = System.nanoTime();
					System.err.println(String.format("Annotation and writing took %.2f sec.",
							(endTime - startTime) / 1000.0 / 1000.0 / 1000.0));
				} catch (IOException e) {
					throw new JannovarException("Problem opening file", e);
				}
			} catch (IncompatiblePedigreeException e) {
				System.err
						.println("VCF file " + vcfPath + " is not compatible to pedigree file " + options.pathPedFile);
			} catch (VariantContextFilterException e) {
				System.err.println("There was a problem annotating the VCF file");
				System.err.println("The error message was as follows.  The stack trace below the error "
						+ "message can help the developers debug the problem.\n");
				System.err.println(e.getMessage());
				System.err.println("\n");
				e.printStackTrace(System.err);
				return;
			}
		}
		if (progressReporter != null)
			progressReporter.done();
	}

	/**
	 * Construct the mendelian inheritance annotation processors
	 * 
	 * @param sink
	 *            The place to put put the VariantContext to after filtration
	 * @throws IOException
	 *             in case of problems with opening the pedigree file
	 * @throws PedParseException
	 *             in the case of problems with parsing pedigrees
	 * @throws IncompatiblePedigreeException
	 *             If the pedigree is incompatible with the VCF file
	 */
	private VariantContextProcessor buildMendelianProcessors(AnnotatedVCFWriter writer)
			throws PedParseException, IOException, IncompatiblePedigreeException {
		if (options.pathPedFile != null) {
			final PedFileReader pedReader = new PedFileReader(new File(options.pathPedFile));
			final PedFileContents pedContents = pedReader.read();
			final Pedigree pedigree = new Pedigree(pedContents, pedContents.getIndividuals().get(0).getPedigree());
			checkPedigreeCompatibility(pedigree, writer.getVCFHeader());
			final GeneWiseMendelianAnnotationProcessor mendelProcessor = new GeneWiseMendelianAnnotationProcessor(
					pedigree, jannovarData, writer.getVCFHeader().getSequenceDictionary(), vc -> writer.put(vc));
			return new CoordinateSortingChecker(mendelProcessor);
		} else {
			return new ConsumerProcessor(vc -> writer.put(vc));
		}
	}

	/**
	 * Check pedigree for compatibility
	 * 
	 * @param pedigree
	 *            {@link Pedigree} to check for compatibility
	 * @param vcfHeader
	 *            {@link VCFHeader} to check for compatibility
	 * @throws IncompatiblePedigreeException
	 *             if the VCF file is not compatible with the pedigree
	 */
	private void checkPedigreeCompatibility(Pedigree pedigree, VCFHeader vcfHeader)
			throws IncompatiblePedigreeException {
		if (!pedigree.getNames().containsAll(vcfHeader.getGenotypeSamples()))
			throw new IncompatiblePedigreeException("The VCF file is not compatible with the pedigree!");
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

}
