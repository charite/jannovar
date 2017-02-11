package de.charite.compbio.jannovar.pedigree;

/**
 * Representation of an individual's sex.
 *
 * @author <a href="mailto:Peter.Robinson@jax.org">Peter N Robinson</a>
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public enum Sex {
	/** sex is unknown */
	UNKNOWN,
	/** individual is male */
	MALE,
	/** individual is female */
	FEMALE;

	/**
	 * @return <code>int</code> representation for the pedigree file.
	 */
	public int toInt() {
		if (this == UNKNOWN)
			return 0;
		else if (this == MALE)
			return 1;
		else
			// if (this == FEMALE)
			return 2;
	}

	/**
	 * Parse {@link String} into a <code>Sex</code> value.
	 *
	 * @param s
	 *            String to parse
	 * @return resulting <code>Sex</code> object
	 * @throws PedParseException
	 *             if <code>s</code> was not equal to <code>"0"</code>, <code>"1"</code>, or <code>"2"</code>.
	 */
	public static Sex toSex(String s) throws PedParseException {
		if (s.equals("1"))
			return MALE;
		else if (s.equals("2"))
			return FEMALE;
		else
			return UNKNOWN;
	}

}
