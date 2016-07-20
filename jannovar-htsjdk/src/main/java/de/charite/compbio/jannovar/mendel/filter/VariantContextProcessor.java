package de.charite.compbio.jannovar.mendel.filter;

import java.io.Closeable;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * Step in a {@link VariantContext} processing pipeline, designed as a sink
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public interface VariantContextProcessor extends Closeable {

	/**
	 * Put a Variant Context into the filter.
	 *
	 * @param vc
	 *            {@link VariantContext} to put into the filter.
	 * @throws VariantContextFilterException
	 *             on problems during the filtration
	 */
	public void put(VariantContext vc) throws VariantContextFilterException;

	/**
	 * Mark processing as done, no more variants will come in.
	 */
	public void close();

}
