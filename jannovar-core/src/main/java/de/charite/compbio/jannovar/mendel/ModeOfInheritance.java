package de.charite.compbio.jannovar.mendel;

/**
 * An enumeration of the four main Mendelian modes of inheritance for prioritizing exome data
 *
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 */
public enum ModeOfInheritance {
	/** autosomal dominant inheritance */
	AUTOSOMAL_DOMINANT,
	/** autosomal recessive inheritance */
	AUTOSOMAL_RECESSIVE,
	/** recessive inheritance on X chromosome */
	X_RECESSIVE,
	/** dominant inheritance on X chromosome */
	X_DOMINANT,
	/** all affected carry variant; not really a mode of inheritance, rather an inclusive filter */
	ALL_AFFECTED,
	/** value for encoding uninitialized values */
	ANY;

	/** @return shortcut for the ModeOfInheritance */
	public String getAbbreviation() {
		switch (this) {
		case AUTOSOMAL_DOMINANT:
			return "AD";
		case AUTOSOMAL_RECESSIVE:
			return "AR";
		case X_DOMINANT:
			return "XD";
		case X_RECESSIVE:
			return "XR";
		case ALL_AFFECTED:
			return "ALL_AFFECTED";
		case ANY:
		default:
			return null;
		}
	}

}
