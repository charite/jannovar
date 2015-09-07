package de.charite.compbio.jannovar.hgvs.legacy;

/**
 * Enum describing either intronic or exonic locations.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public enum LegacyLocationType {

	/** intronic locations */
	INTRONIC,
	/** exonic locations */
	EXONIC;

	/** @return string to use in the legacy notation, <code>"IVS"</code> or <code>"EX"</code> */
	public String getLegacyString() {
		switch (this) {
		case INTRONIC:
			return "IVS";
		case EXONIC:
			return "EX";
		default:
			throw new RuntimeException("Unexpected type " + this);
		}
	}

	/** @return {@link LegacyLocationType} for the given legacy string ("IVS, "EX", or "E") */
	public static LegacyLocationType getTypeForLegacyString(String str) {
		switch (str) {
		case "IVS":
			return INTRONIC;
		case "EX":
		case "E":
			return EXONIC;
		default:
			throw new IllegalArgumentException("Invalid legacy string " + str);
		}
	}

}
