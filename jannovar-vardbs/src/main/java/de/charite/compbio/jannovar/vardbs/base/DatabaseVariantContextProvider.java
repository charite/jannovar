package de.charite.compbio.jannovar.vardbs.base;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Allow to query database with contig, (1-based) begin, and end position to produce a
 * {@link VariantContext} with annotating information.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface DatabaseVariantContextProvider {

	/**
	 * Query for {@link VariantContext}s describing the DB variant annotation between begin and end
	 * position on the given contig.
	 * 
	 * @param contig
	 *            Name of the contig to perform query on.
	 * @param beginPos
	 *            1-based start position
	 * @param endPos
	 *            end position
	 * @return {@link CloseableIterator} of {@link VariantContext} objects for annotation.
	 */
	CloseableIterator<VariantContext> query(String contig, int beginPos, int endPos);

}
