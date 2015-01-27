package de.charite.compbio.jannovar.filter;

import htsjdk.variant.variantcontext.writer.VariantContextWriter;

/**
 * Write results to a {@link VariantContextWriter}
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class WriterFilter implements VariantContextFilter {

	/** the {@link VariantContextWriter} to use for writing out */
	private final VariantContextWriter writer;

	/** Initialize with the given {@link VariantContextWriter}. */
	public WriterFilter(VariantContextWriter writer) {
		this.writer = writer;
	}

	@Override
	public void put(FlaggedVariant fv) throws FilterException {
		System.err.println("WRITING\t" + fv.vc.getChr() + ":" + fv.vc.getStart() + "\tINCLUDED?\t" + fv.isIncluded());
		if (fv.isIncluded())
			writer.add(fv.vc);
	}

	@Override
	public void finish() {
		/* no-op */
	}

}
