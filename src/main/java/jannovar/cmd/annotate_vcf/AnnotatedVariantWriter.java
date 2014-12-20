package jannovar.cmd.annotate_vcf;

import htsjdk.variant.variantcontext.VariantContext;
import jannovar.exception.AnnotationException;

import java.io.IOException;

/**
 * Interface for output writers in Jannovar class.
 *
 * The task of such a writer is to take a HTSJDK annotation, perform annotation with the Jannovar code and then write it
 * out into some output format.
 *
 * Currently, we have to convert HTSJDK VariantContext objects into Jannovar Variant objects but that should be
 * simplified later on.
 *
 * Although public, this class is not meant to be part of the public Jannovar intervace. It can be changed or removed at
 * any point.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
// TODO(holtgrew): Update comment above once we use VariantContext everywhere.
public abstract class AnnotatedVariantWriter {

	/**
	 * Write out the given VariantContext with additional annotation.
	 *
	 * @throws AnnotationException
	 *             when a problem with annotation occurs
	 * @throws IOException
	 *             when problem with I/O occurs
	 */
	public abstract void put(VariantContext vc) throws AnnotationException, IOException;

	/** Returns output path */
	public abstract String getOutFileName();

	/** Close writer, free resources */
	abstract void close();

}
