package de.charite.compbio.jannovar.annotation;

import com.google.common.collect.ImmutableList;

// TODO(holtgrem): Test me!

/**
 * Decorator for {@link Annotation} that allows querying for variant types.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
final public class AnnotationVariantTypeDecorator {

	/** the decorated {@link Annotation} */
	final public Annotation annotation;

	AnnotationVariantTypeDecorator(Annotation annotation) {
		this.annotation = annotation;
	}

	/**
	 * This function checks if we have a variant that affects the sequence of a coding exon
	 *
	 * @return <code>true</code> if we have a missense, PTC, splicing, indel variant, or a synonymous change.
	 */
	public boolean isCodingExonic() {
		// TODO(holtgrem): Are the first three ones correct?
		ImmutableList<VariantType> tryMatch = ImmutableList.of(VariantType.STOPLOSS, VariantType.STOPGAIN,
				VariantType.SYNONYMOUS, VariantType.MISSENSE, VariantType.NON_FS_SUBSTITUTION,
				VariantType.NON_FS_INSERTION, VariantType.FS_SUBSTITUTION, VariantType.FS_DELETION,
				VariantType.FS_INSERTION, VariantType.NON_FS_DELETION);
		for (VariantType v : tryMatch)
			if (annotation.varTypes.contains(v))
				return true;
		return false;
	}

	/**
	 * @return <code>true</code> if this annotation is for a 3' or 5' UTR
	 */
	public boolean isUTRVariant() {
		return (annotation.varTypes.contains(VariantType.UTR3) || annotation.varTypes.contains(VariantType.UTR5));
	}

	/**
	 * @return <code>true</code> if the variant affects an exon of an ncRNA
	 */
	public boolean isNonCodingRNA() {
		ImmutableList<VariantType> tryMatch = ImmutableList.of(VariantType.ncRNA_EXONIC, VariantType.ncRNA_INTRONIC,
				VariantType.ncRNA_SPLICE_DONOR, VariantType.ncRNA_SPLICE_ACCEPTOR, VariantType.ncRNA_SPLICE_REGION);
		for (VariantType v : tryMatch)
			if (annotation.varTypes.contains(v))
				return true;
		return false;
	}

}
