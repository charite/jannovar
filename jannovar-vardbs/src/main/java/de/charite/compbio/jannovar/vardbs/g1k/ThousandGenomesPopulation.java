package de.charite.compbio.jannovar.vardbs.g1k;

/**
 * Enum type for populations in the thousand genomes data set
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum ThousandGenomesPopulation {
	/**
	 * African/African American
	 */
	AFR,
	/**
	 * American
	 */
	AMR,
	/**
	 * Asian
	 */
	ASN,
	/**
	 * European
	 */
	EUR,
	/**
	 * Pseudo-population meaning "with maximal allele frequency"
	 */
	POPMAX,
	/**
	 * Pseudo-population meaning "all pooled together"
	 */
	ALL;

	public String getLabel() {
		switch (this) {
			case AFR:
				return "African/African American";
			case AMR:
				return "American";
			case ASN:
				return "Asian";
			case EUR:
				return "European";
			case POPMAX:
				return "Population with max. AF";
			case ALL:
				return "All";
			default:
				return "Undefined";
		}
	}
}
