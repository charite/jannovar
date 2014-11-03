package jannovar.cmd.annotate_vcf;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import jannovar.JannovarOptions;
import jannovar.cmd.JannovarAnnotationCommand;
import jannovar.exception.JannovarException;

import java.io.File;
import java.io.IOException;

/**
 * Run annotation steps (read in VCF, write out VCF or Jannovar file format).
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class AnnotateVCFCommand extends JannovarAnnotationCommand {
	public AnnotateVCFCommand(JannovarOptions options) {
		super(options);
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
		if (!deserialize())
			throw new JannovarException("You have to pass in a model file for deserialization.");

		// initialize the VCF reader
		VCFFileReader parser = new VCFFileReader(new File(this.options.VCFfilePath), false);

		AnnotatedVariantWriter writer = null;
		try {
			// construct the variant writer
			if (this.options.jannovarFormat)
				writer = new AnnotatedJannovarWriter(chromosomeMap, options);
			else
				writer = new AnnotatedVCFWriter(parser, chromosomeMap, options);

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

		// TODO(holtgrem): use logger
		System.err.println("[INFO] Wrote annotations to \"" + writer.getOutFileName() + "\"");
	}
}
