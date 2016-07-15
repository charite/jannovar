package de.charite.compbio.jannovar.vardbs.exac;

/**
 * Enum type for populations in the ExAC data set
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum ExacPopulation {
	/** African/African American */
	AFR,
	/** American */
	AMR,
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
	/** Pseudo-population meaning "all pooled together" */
	ALL;

	public String getLabel() {
		switch (this) {
		case AFR:
			return "African/African American";
		case AMR:
			return "East Asian";
		case FIN:
			return "Finnish";
		case NFE:
			return "Non-Finnish European";
		case OTH:
			return "Other";
		case SAS:
			return "South Asian";
		case ALL:
			return "All";
		default:
			return "Undefined";
		}
	}
}
