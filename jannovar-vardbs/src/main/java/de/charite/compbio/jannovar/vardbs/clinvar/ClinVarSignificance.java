package de.charite.compbio.jannovar.vardbs.clinvar;

/**
 * Enum for describing ClinVar CLNSIG
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum ClinVarSignificance {
	/** uncertain significance */
	UNCERTAIN,
	/** not provided */
	NOT_PROVIDED,
	/** benign */
	BENIGN,
	/** likely benign */
	LIKELY_BENIGN,
	/** likely pathogenic */
	LIKELY_PATHOGENIC,
	/** pathogenic */
	PATHOGENIC,
	/** drug response */
	DRUG_RESPONSE,
	/** histocompatbility */
	HISTOCOMPATIBILITY,
	/** other */
	OTHER;
	
	public String getLabel() {
		switch (this) {
		case BENIGN:
			return "benign";
		case DRUG_RESPONSE:
			return "drug response";
		case HISTOCOMPATIBILITY:
			return "histocompatibility";
		case LIKELY_BENIGN:
			return "likely benign";
		case LIKELY_PATHOGENIC:
			return "likely pathogenic";
		case NOT_PROVIDED:
			return "not provided";
		case PATHOGENIC:
			return "pathogenic";
		case UNCERTAIN:
			return "uncertain";
		case OTHER:
		default:
			return "other";
		}
	}
}
