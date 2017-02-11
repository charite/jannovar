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
			return "drug_response";
		case HISTOCOMPATIBILITY:
			return "histocompatibility";
		case LIKELY_BENIGN:
			return "likely_benign";
		case LIKELY_PATHOGENIC:
			return "likely_pathogenic";
		case NOT_PROVIDED:
			return "not_provided";
		case PATHOGENIC:
			return "pathogenic";
		case UNCERTAIN:
			return "uncertain";
		case OTHER:
		default:
			return "other";
		}
	}
	
	public static ClinVarSignificance fromInteger(int i) {
	    switch (i) {
	    case 0:
	        return UNCERTAIN;
        case 1:
            return NOT_PROVIDED;
        case 2:
            return BENIGN;
        case 3:
            return LIKELY_BENIGN;
        case 4:
            return LIKELY_PATHOGENIC;
        case 5:
            return PATHOGENIC;
        case 6:
            return DRUG_RESPONSE;
        case 7:
            return HISTOCOMPATIBILITY;
        case 255:
            return OTHER;
	    default:
	        throw new RuntimeException("Invalid clin var significance number: " + i);
	    }
	}
}
