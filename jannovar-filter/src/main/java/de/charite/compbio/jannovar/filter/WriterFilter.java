package de.charite.compbio.jannovar.filter;

import htsjdk.variant.variantcontext.writer.VariantContextWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Write results to a {@link VariantContextWriter}
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class WriterFilter implements VariantContextFilter {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(WriterFilter.class);

	/** the {@link VariantContextWriter} to use for writing out */
	private final VariantContextWriter writer;

	/** Initialize with the given {@link VariantContextWriter}. */
	public WriterFilter(VariantContextWriter writer) {
		this.writer = writer;
	}

	public void put(FlaggedVariant fv) throws FilterException {
		LOGGER.trace("Variant added to writer {} => included? {}", new Object[] { fv.getVC(), fv.isIncluded() });
		if (fv.isIncluded())
			writer.add(fv.getVC());
	}

	public void finish() {
		/* no-op */
	}

}
