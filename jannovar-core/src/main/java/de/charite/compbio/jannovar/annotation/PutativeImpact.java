package de.charite.compbio.jannovar.annotation;

/**
 * Putative impact of an annotation.
 */
public enum PutativeImpact {
	/** high impact */
	HIGH,
	/** moderate impact */
	MODERATE,
	/** low impact */
	LOW,
	/** modifier of other {@link PutativeImpact} values */
	MODIFIER
}