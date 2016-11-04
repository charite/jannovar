package de.charite.compbio.jannovar.pedigree;

/**
 * Codes used to denote affection status of a person in a pedigree.
 *
 * @author <a href="mailto:Peter.Robinson@jax.org">Peter N Robinson</a>
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public enum Disease {
	/** corresponds to 0 = unknown disease status in the PED file. */
	UNKNOWN,
	/** corresponds to 1 = unaffected in the PED file. */
	UNAFFECTED,
	/** corresponds to 2 = affected in the PED file. */
	AFFECTED;

	/**
	 * @return <code>int</code> value representation for PED file.
	 */
	public int toInt() {
		switch (this) {
		case AFFECTED:
			return 2;
		case UNAFFECTED:
			return 1;
		default:
			return 0;
		}
	}

	/**
	 * Parse {@link String} into a <code>Disease</code> value.
	 *
	 * @param s
	 *            String to parse
	 * @return resulting <code>Disease</code> object
	 * @throws PedParseException
	 *             if <code>s</code> was not equal to <code>"0"</code>, <code>"1"</code>, or <code>"2"</code>.
	 */
	public static Disease toDisease(String s) throws PedParseException {
		if (s.equals("0"))
			return UNKNOWN;
		else if (s.equals("1"))
			return UNAFFECTED;
		else if (s.equals("2"))
			return AFFECTED;
		else
			throw new PedParseException("Invalid PED disease status value: " + s);
	}

}