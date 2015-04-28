package de.charite.compbio.jannovar.cmd.annotate_vcf;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationCommand;

/**
 * Run annotation steps (read in VCF, write out VCF or Jannovar file format).
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class AnnotateVCFCommand extends JannovarAnnotationCommand {

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
			System.err.println("Annotating VCF...");
			final long startTime = System.nanoTime();
			VCFFileReader parser = new VCFFileReader(new File(vcfPath), false);

			AnnotatedVariantWriter writer = null;
			try {
				// construct the variant writer
				if (this.options.jannovarFormat)
					writer = new AnnotatedJannovarWriter(refDict, chromosomeMap, vcfPath, options);
				else
					writer = new AnnotatedVCFWriter(refDict, parser, chromosomeMap, vcfPath, options, args);

				// annotate and write out all variants
				for (VariantContext vc : parser)
					writer.put(vc);

				// close parser writer again
				parser.close();
				writer.close();
			} catch (IOException e) {
				// convert exception to JannovarException and throw, writer can only be null here
				parser.close();
				throw new JannovarException("Problem with VCF annotation.", e);
			}

			System.err.println("Wrote annotations to \"" + writer.getOutFileName() + "\"");
			final long endTime = System.nanoTime();
			System.err.println(String.format("Annotation and writing took %.2f sec.",
					(endTime - startTime) / 1000.0 / 1000.0 / 1000.0));
		}
	}

	@Override
	protected JannovarOptions parseCommandLine(String[] argv) throws CommandLineParsingException,
	HelpRequestedException {
		AnnotateVCFCommandLineParser parser = new AnnotateVCFCommandLineParser();
		try {
			return parser.parse(argv);
		} catch (ParseException e) {
			throw new CommandLineParsingException("Could not parse the command line.", e);
		}
	}

}
