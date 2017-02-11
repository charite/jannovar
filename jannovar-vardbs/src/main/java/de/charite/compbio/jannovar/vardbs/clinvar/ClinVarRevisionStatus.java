package de.charite.compbio.jannovar.vardbs.clinvar;

/**
 * Enum for describing ClinVar CLNREVSTAT
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum ClinVarRevisionStatus {
    /** No assertion provided */
    NO_ASSERTION,
    /** No assertion criteria provided */
    NO_CRITERIA,
    /** Criteria provided single submitter */
    SINGLE,
    /** Criteria provided multiple submitters no conflicts */
    MULT,
    /** Criteria provided conflicting interpretations */
    CONF,
    /** Reviewed by expert panel */
    EXP,
    /** Practice guideline */
    GUIDELINE;

    public String getLabel() {
        switch (this) {
        case CONF:
            return "conflicting_interpretation";
        case EXP:
            return "expert_panel_reviewed";
        case GUIDELINE:
            return "pratice_guideline";
        case MULT:
            return "multiple_submitters_no_conflict";
        case NO_ASSERTION:
            return "no_assertion";
        case NO_CRITERIA:
            return "no_assertion_criteria";
        case SINGLE:
            return "single_submitter";
        default:
            return "other";
        }
    }

    public static ClinVarRevisionStatus fromString(String s) {
        switch (s) {
        case "conf":
            return CONF;
        case "exp":
            return EXP;
        case "guideline":
            return GUIDELINE;
        case "mult":
            return MULT;
        case "no_assertion":
            return NO_ASSERTION;
        case "no_criteria":
            return NO_CRITERIA;
        case "single":
            return SINGLE;
        default:
            throw new RuntimeException("Invalid clin var revision status: " + s);
        }
    }
}
