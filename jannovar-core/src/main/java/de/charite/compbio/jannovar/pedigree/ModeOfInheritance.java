package de.charite.compbio.jannovar.pedigree;

/**
 * An enumeration of the four main Mendelian modes of inheritance for prioritizing exome data.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
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
	UNINITIALIZED; // TODO(holtgrew): Rename to UNKNOWN or UNDEFINED
}
