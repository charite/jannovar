package jannovar.reference;

/**
 * Types of genome changes represented by {@link GenomeChange}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public enum GenomeChangeType {
	/** single nucleotide variant */
	SNV,
	/** insertion */
	INSERTION,
	/** deletion */
	DELETION,
	/** block substitution */
	BLOCK_SUBSTITUTION
}
