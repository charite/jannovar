package de.charite.compbio.jannovar.cmd.annotate_vcf;

import htsjdk.variant.variantcontext.VariantContext;

import java.io.Closeable;
import java.io.IOException;

import de.charite.compbio.jannovar.annotation.AnnotationException;

/**
 * Interface for output writers in Jannovar class.
 *
 * The task of such a writer is to take a HTSJDK annotation, perform annotation with the Jannovar code and then write it
 * out into some output format.
 *
 * Currently, we have to convert HTSJDK VariantContext objects into Jannovar Variant objects but that should be
 * simplified later on.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public abstract class AnnotatedVariantWriter implements Closeable {

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
	public abstract void close();

}
