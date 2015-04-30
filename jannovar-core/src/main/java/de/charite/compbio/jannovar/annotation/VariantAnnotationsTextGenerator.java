package de.charite.compbio.jannovar.annotation;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.impl.util.StringUtil;

// TODO(holtgrem): Test me!

/**
 * Generate annotation text (effect and HGVS description) from {@link VariantAnnotations} object.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public abstract class VariantAnnotationsTextGenerator {

	/** the decorated {@link VariantAnnotations} */
	protected final VariantAnnotations annotations;

	/** alternative allele ID (1 from VCF is 0 here) */
	private final int alleleID;

	/** total number of alternative alleles */
	private final int altCount;

	/**
	 * Initialize the decorator.
	 *
	 * @param annotations
	 *            {@link VariantAnnotations} of {@link Annotation} objects
	 * @param alleleID
	 *            the 0-based id of the allele
	 * @param altCount
	 *            total number of alternative alleles
	 */
	public VariantAnnotationsTextGenerator(VariantAnnotations annotations, int alleleID, int altCount) {
		this.annotations = annotations;
		this.alleleID = alleleID;
		this.altCount = altCount;
	}

	/** @return alternative allele ID (1 from VCF is 0 here) */
	public int getAlleleID() {
		return alleleID;
	}

	/** @return total number of alternative alleles */
	public int getAltCount() {
		return altCount;
	}

	/**
	 * @return String with the effect text, comma separated if {@link #getAnnotations} returns more than one element
	 */
	public String buildEffectText() {
		StringBuilder builder = new StringBuilder();
		for (Annotation anno : annotations.getAnnotations()) {
			if (builder.length() != 0)
				builder.append(',');
			if (altCount > 1)
				builder.append(StringUtil.concatenate("alt", alleleID + 1, ":"));
			builder.append(Joiner.on("+").join(anno.getEffects()));
		}
		return builder.toString();
	}

	/**
	 * @return String with the effect text, comma separated if {@link #getAnnotations} returns more than one element
	 */
	public String buildHGVSText() {
		StringBuilder builder = new StringBuilder();
		for (Annotation anno : annotations.getAnnotations()) {
			if (builder.length() != 0)
				builder.append(',');
			if (altCount > 1)
				builder.append(StringUtil.concatenate("alt", alleleID + 1, ":"));
			builder.append(anno.getSymbolAndAnnotation());
		}
		return builder.toString();
	}

	/**
	 * @return {@link VariantAnnotations} of annotations to generate the annotation text for
	 */
	protected abstract VariantAnnotations getAnnotations();

}
