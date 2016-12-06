package de.charite.compbio.jannovar.vardbs.clinvar;

/**
 * Enum for annotating origin from ClinVar
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum ClinVarOrigin {
	/** unknown origin */
	UNKNOWN,
	/** germline variant */
	GERMLINE,
	/** somatic variant */
	SOMATIC,
	/** inherited */
	INHERITED,
	/** paternal */
	PATERNAL,
	/** maternal */
	MATERNAL,
	/** de novo */
	DE_NOVO,
	/** biparental */
	BIPARENTAL,
	/** uniparental */
	UNIPARENTAL,
	/** not-tested */
	NOT_TESTED,
	/** tested-inconclusive */
	TESTED_INCONCLUSIVE,
	/** other */
	OTHER;

	/**
	 * @return Label to be used for printing
	 */
	public String getLabel() {
		switch (this) {
		case UNKNOWN:
			return "unknown";
		case GERMLINE:
			return "germline";
		case SOMATIC:
			return "somatic";
		case INHERITED:
			return "inherited";
		case PATERNAL:
			return "paternal";
		case MATERNAL:
			return "maternal";
		case DE_NOVO:
			return "de novo";
		case BIPARENTAL:
			return "biparental";
		case UNIPARENTAL:
			return "uniparental";
		case NOT_TESTED:
			return "not tested";
		case TESTED_INCONCLUSIVE:
			return "tested-inconclusive";
		default:
			return "other";
		}
	}
}
