package de.charite.compbio.jannovar.filter;

import java.util.ArrayList;
import java.util.List;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * keep variants
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class WriterFilterHelper implements VariantContextFilter {

	private List<VariantContext> variants = new ArrayList<VariantContext>();

	public WriterFilterHelper() {
	}

	public void put(FlaggedVariant fv) throws FilterException {
		variants.add(fv.getVC());
	}

	public List<VariantContext> getVariants() {
		return variants;
	}

	public void finish() {
		/* no-op */
	}

}
