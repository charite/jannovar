package de.charite.compbio.jannovar.hgvs;

// TODO(holtgrew): need to talk to a geneticist for improving the naming

/**
 * Variant configuration of changes in the same gene.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public enum VariantConfiguration {
	/** change resulting from one nucleotide change */
	SINGLE_ORIGIN,
	/** change resulting from independent changes on the same chromosome */
	IN_CIS,
	/** it is unknown whether the protein change is on the same or different alleles */
	UNKNOWN_CIS_TRANS,
	/** mosaic change */
	MOSAIC,
	/** chimeric change */
	CHIMERIC;

	/** @return VariantConfiguration matching to s */
	public static VariantConfiguration fromString(String s) {
		switch (s) {
		case ",":
			return SINGLE_ORIGIN;
		case "//":
			return CHIMERIC;
		case ";":
			return IN_CIS;
		case "(;)":
			return UNKNOWN_CIS_TRANS;
		case "/":
			return MOSAIC;
		default:
			throw new RuntimeException("No VariantConfiguration known for string " + s);
		}
	}

	/** @return separator to use in HGVS strings */
	public String toHGVSSeparator() {
		switch (this) {
		case SINGLE_ORIGIN:
			return ",";
		case CHIMERIC:
			return "//";
		case IN_CIS:
			return ";";
		case UNKNOWN_CIS_TRANS:
			return "(;)";
		case MOSAIC:
			return "/";
		default:
			throw new RuntimeException("Unhandled VariantConfiguration " + this);
		}
	}

}
