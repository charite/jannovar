package de.charite.compbio.jannovar.vardbs.generic_tsv;

/**
 * Enumeration describing accumulation strategy for annotation with TSV for multiple matches.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum GenericTSVAccumulationStrategy {
	/** Choose first */
	CHOOSE_FIRST,
	/** Use average, only applicable to numbers, fall back to first. */
	AVERAGE,
	/** Use largest value, only applicable to numbers, fall back to first. */
	CHOOSE_MAX,
	/** Use smallest value, only applicable to numbers, fall back to first. */
	CHOOSE_MIN;
}