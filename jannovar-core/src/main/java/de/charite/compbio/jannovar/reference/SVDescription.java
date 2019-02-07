package de.charite.compbio.jannovar.reference;

/**
 * Minimal description of a variant as tuple {@code (position, position2, type)}.
 * <p>
 * The reference and alternative allele string are returned as trimmed, first stripping common suffixes then common
 * prefixes. Note that this is not the same as normalized variants (see the link below) but allows for easier querying
 * in programs.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @see <a href="http://genome.sph.umich.edu/wiki/Variant_Normalization">
 * http://genome.sph.umich.edu/wiki/Variant_Normalization</a>
 */
public interface SVDescription {

	/**
	 * @return String with the canonical chromosome name
	 */
	String getChrName();

	/**
	 * @return integer identifying the second chromosome
	 */
	int getChr();

	/**
	 * @return zero-based position of the variant on the chromosome
	 */
	int getPos();

	/**
	 * @return String with the canonical chromosome name of the second chromosome
	 */
	String getChr2Name();

	/**
	 * @return integer identifying the second chromosome
	 */
	int getChr2();

	/**
	 * @return zero-based second position of the variant on the second chromosome
	 */
	int getPos2();

	/**
	 * @return the type of the structural variant.
	 */
	Type getType();

	/**
	 * Enumeration for the type of the structural variant.
	 */
	enum Type {
		/**
		 * generic deletion
		 */
		DEL,
		/**
		 * mobile element deletion
		 */
		DEL_ME,
		/**
		 * insertion
		 */
		INS,
		/**
		 * mobile element insertion
		 */
		INS_ME,
		/**
		 * generic duplication
		 */
		DUP,
		/**
		 * tandem duplication
		 */
		DUP_TANDEM,
		/**
		 * inversion
		 */
		INV,
		/**
		 * copy number variant
		 */
		CNV,
		/**
		 * breakend
		 */
		BND,
		/**
		 * unknown type
		 */
		UNKNOWN,
	}

}
