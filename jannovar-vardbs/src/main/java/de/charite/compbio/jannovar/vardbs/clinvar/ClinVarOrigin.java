package de.charite.compbio.jannovar.vardbs.clinvar;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

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
			return "tested_inconclusive";
		default:
			return "other";
		}
	}
	
	static public List<ClinVarOrigin> fromInteger(int i) {
        if (i == 0)
            return Lists.newArrayList(UNKNOWN);
	    List<ClinVarOrigin> result = new ArrayList<>();
	    if (i % 1 != 0)
	        result.add(GERMLINE);
        else if (i % 2 != 0)
            result.add(SOMATIC);
        else if (i % 4 != 0)
            result.add(INHERITED);
        else if (i % 8 != 0)
            result.add(PATERNAL);
        else if (i % 16 != 0)
            result.add(MATERNAL);
        else if (i % 32 != 0)
            result.add(DE_NOVO);
        else if (i % 64 != 0)
            result.add(BIPARENTAL);
        else if (i % 128 != 0)
            result.add(UNIPARENTAL);
        else if (i % 256 != 0)
            result.add(NOT_TESTED);
        else if (i % 512 != 0)
            result.add(TESTED_INCONCLUSIVE);
        else if (i % 1073741824 != 0)
            result.add(OTHER);
	    return result;
	}
}
