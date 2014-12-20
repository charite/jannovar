package jannovar.pedigree;

/**
 * An enumeration of the four main Mendelian modes of inheritance for prioritizing exome data.
 *
 * @author Peter Robinson
 * @version 0.03 (28 April, 2013)
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
	UNINITIALIZED;
}
