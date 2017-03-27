package de.charite.compbio.jannovar.mendel;

// TODO: test me!

/**
 * Enum for refined representation of {@link ModeOfInheritance}
 * 
 * In contrast to {@link ModeOfInheritance}, this type can reflect whether autosomal recessive inheritance is compound
 * heterozygous or homozygous alternative.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum SubModeOfInheritance {

	/** autosomal dominant inheritance */
	AUTOSOMAL_DOMINANT,
	/** autosomal recessive inheritance, compound het */
	AUTOSOMAL_RECESSIVE_COMP_HET,
	/** autosomal recessive inheritance, hom alt */
	AUTOSOMAL_RECESSIVE_HOM_ALT,
	/** recessive inheritance on X chromosome, compound het */
	X_RECESSIVE_COMP_HET,
	/** recessive inheritance on X chromosome, hom alt */
	X_RECESSIVE_HOM_ALT,
	/** dominant inheritance on X chromosome */
	X_DOMINANT,
	/** value for encoding uninitialized values */
	ANY;
	
	public boolean isRecessive() {
		return toModeOfInheritance().isRecessive();
	}

	public boolean isDominant() {
		return toModeOfInheritance().isDominant();
	}

	/** @return shortcut for the ModeOfInheritance */
	public String getAbbreviation() {
		switch (this) {
		case AUTOSOMAL_DOMINANT:
			return "AD";
		case AUTOSOMAL_RECESSIVE_COMP_HET:
			return "AR_COMP_HET";
		case AUTOSOMAL_RECESSIVE_HOM_ALT:
			return "AR_HOM_ALT";
		case X_DOMINANT:
			return "XD";
		case X_RECESSIVE_COMP_HET:
			return "XR_COMP_HET";
		case X_RECESSIVE_HOM_ALT:
			return "XR_HOM_ALT";
		case ANY:
		default:
			return null;
		}
	}
	
	/** @return coarsened value from {@link ModeOfInheritance} */
	public ModeOfInheritance toModeOfInheritance() {
		switch (this) {
		case AUTOSOMAL_DOMINANT:
			return ModeOfInheritance.AUTOSOMAL_DOMINANT;
		case AUTOSOMAL_RECESSIVE_COMP_HET:
		case AUTOSOMAL_RECESSIVE_HOM_ALT:
			return ModeOfInheritance.AUTOSOMAL_RECESSIVE;
		case X_DOMINANT:
			return ModeOfInheritance.X_DOMINANT;
		case X_RECESSIVE_COMP_HET:
		case X_RECESSIVE_HOM_ALT:
			return ModeOfInheritance.X_RECESSIVE;
		case ANY:
		default:
			return ModeOfInheritance.ANY;
		}
	}

}
