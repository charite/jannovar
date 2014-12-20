package jannovar.pedigree;

/**
 * Codes used to denote affection status of a {@link jannovar.pedigree.Person Person} in a
 * {@link jannovar.pedigree.Pedigree Pedigree}
 *
 * @author Peter Robinson <peter.robinson@charite.de>
 * @version 0.03 (5 May, 2013)
 */
public enum Disease {
	/** corresponds to 2 = affected in the pedfile. */
	AFFECTED,
	/** corresponds to 1 = unaffected in the pedfile. */
	UNAFFECTED,
	/** corresponds to 0 = unknown disease status in the pedfile. */
	UNKNOWN;
}