package de.charite.compbio.jannovar.mendel;

/**
 * An enumeration of the four main Mendelian modes of inheritance for prioritizing exome data
 *
 * @author <a href="mailto:Peter.Robinson@jax.org">Peter N Robinson</a>
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
	/** value for encoding uninitialized values */
	ANY;

	/** @return <code>true</code> if is recessive MOI */
	public boolean isRecessive() {
		switch (this) {
		case AUTOSOMAL_RECESSIVE:
		case X_RECESSIVE:
			return true;
		default:
			return false;
		}
	}

	/** @return <code>true</code> if is recessive MOI */
	public boolean isDominant() {
		switch (this) {
		case AUTOSOMAL_DOMINANT:
		case X_DOMINANT:
			return true;
		default:
			return false;
		}
	}

	/** @return two-letter shortcut for the ModeOfInheritance */
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
		case ANY:
		default:
			return null;
		}
	}

}
