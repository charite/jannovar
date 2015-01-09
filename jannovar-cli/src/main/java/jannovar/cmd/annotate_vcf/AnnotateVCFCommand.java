package jannovar.cmd.annotate_vcf;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import jannovar.JannovarException;
import jannovar.JannovarOptions;
import jannovar.cmd.CommandLineParsingException;
import jannovar.cmd.HelpRequestedException;
import jannovar.cmd.JannovarAnnotationCommand;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.ParseException;

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
	 * with the suffix .jannovar).
	 *
	 * @throws JannovarException
	 *             on problems with the annotation
	 */
	@Override
	public void run() throws JannovarException {
		System.err.println("Deserializing transcripts...");
		deserializeTranscriptDefinitionFile();

		// initialize the VCF reader
		System.err.println("Annotating VCF...");
		final long startTime = System.nanoTime();
		VCFFileReader parser = new VCFFileReader(new File(this.options.vcfFilePath), false);

		AnnotatedVariantWriter writer = null;
		try {
			// construct the variant writer
			if (this.options.jannovarFormat)
				writer = new AnnotatedJannovarWriter(refDict, chromosomeMap, options);
			else
				writer = new AnnotatedVCFWriter(refDict, parser, chromosomeMap, options);

			// annotate and write out all variants
			for (VariantContext vc : parser)
				writer.put(vc);

			// close parser writer again
			parser.close();
			writer.close();
		} catch (IOException e) {
			// convert exception to JannovarException and throw, writer can only be null here
			parser.close();
			throw new JannovarException(e.getMessage());
		}
		System.err.println("Wrote annotations to \"" + writer.getOutFileName() + "\"");

		final long endTime = System.nanoTime();
		System.err.println(String.format("Annotation and writing took %.2f sec.",
				(endTime - startTime) / 1000.0 / 1000.0 / 1000.0));
	}

	@Override
	protected JannovarOptions parseCommandLine(String[] argv) throws CommandLineParsingException,
			HelpRequestedException {
		AnnotateVCFCommandLineParser parser = new AnnotateVCFCommandLineParser();
		try {
			return parser.parse(argv);
		} catch (ParseException e) { // TODO(holtgrem): do not translate?
			throw new CommandLineParsingException(e.getMessage());
		}
	}

}
