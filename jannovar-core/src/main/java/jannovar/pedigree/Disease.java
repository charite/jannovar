package jannovar.pedigree;

/**
 * Codes used to denote affection status of a {@link Person} in a {@link Pedigree}.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public enum Disease {
	/** corresponds to 2 = affected in the pedfile. */
	AFFECTED,
	/** corresponds to 1 = unaffected in the pedfile. */
	UNAFFECTED,
	/** corresponds to 0 = unknown disease status in the pedfile. */
	UNKNOWN;

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
}