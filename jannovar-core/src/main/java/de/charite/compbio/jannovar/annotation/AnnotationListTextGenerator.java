package de.charite.compbio.jannovar.annotation;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.impl.util.StringUtil;

// TODO(holtgrem): Test me!

/**
 * Generate annotation text (effect and HGVS description) from {@link AnnotationList} object.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public abstract class AnnotationListTextGenerator {

	/** the decorated {@link AnnotationList} */
	public final AnnotationList annotations;

	/** alternative allele ID (1 from VCF is 0 here) */
	public final int alleleID;

	/** total number of alternative alleles */
	public final int altCount;

	/**
	 * Initialize the decorator.
	 *
	 * @param annotations
	 *            {@link AnnotationList} of {@link Annotation} objects
	 * @param alleleID
	 *            the 0-based id of the allele
	 * @param altCount
	 *            total number of alternative alleles
	 */
	public AnnotationListTextGenerator(AnnotationList annotations, int alleleID, int altCount) {
		this.annotations = annotations;
		this.alleleID = alleleID;
		this.altCount = altCount;
	}

	/**
	 * @return String with the effect text, comma separated if {@link #getAnnotations} returns more than one element
	 */
	public String buildEffectText() {
		StringBuilder builder = new StringBuilder();
		for (Annotation anno : getAnnotations().entries) {
			if (builder.length() != 0)
				builder.append(',');
			if (altCount > 1)
				builder.append(StringUtil.concatenate("alt", alleleID + 1, ":"));
			builder.append(Joiner.on("+").join(anno.effects));
		}
		return builder.toString();
	}

	/**
	 * @return String with the effect text, comma separated if {@link #getAnnotations} returns more than one element
	 */
	public String buildHGVSText() {
		StringBuilder builder = new StringBuilder();
		for (Annotation anno : getAnnotations().entries) {
			if (builder.length() != 0)
				builder.append(',');
			if (altCount > 1)
				builder.append(StringUtil.concatenate("alt", alleleID + 1, ":"));
			builder.append(anno.getSymbolAndAnnotation());
		}
		return builder.toString();
	}

	/**
	 * @return {@link AnnotationList} of annotations to generate the annotation text for
	 */
	protected abstract AnnotationList getAnnotations();

}
