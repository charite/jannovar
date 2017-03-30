package de.charite.compbio.jannovar.vardbs.gnomad;

/**
 * Enum type for populations in the gnomAD data set
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum GnomadPopulation {
	/** African/African American */
	AFR,
	/** American */
	AMR,
	/** Ashkenazi Jewish */
	ASJ,
	/** East Asian */
	EAS,
	/** Finish */
	FIN,
	/** Non-Finnish European */
	NFE,
	/** Other population */
	OTH,
	/** South asian population */
	SAS,
	/** Pseudo-population meaning "with maximal allele frequency" */
	POPMAX,
	/** Pseudo-population meaning "all pooled together" */
	ALL;

	public String getLabel() {
		switch (this) {
		case AFR:
			return "African/African American";
		case AMR:
			return "East Asian";
		case ASJ:
			return "Ashkenazi Jewish";
		case FIN:
			return "Finnish";
		case NFE:
			return "Non-Finnish European";
		case OTH:
			return "Other";
		case SAS:
			return "South Asian";
		case POPMAX:
			return "Population with max. AF";
		case ALL:
			return "All";
		default:
			return "Undefined";
		}
	}
}
