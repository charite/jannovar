package de.charite.compbio.jannovar.reference;

/**
 * Types of genomic variants represented by {@link GenomeVariant}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public enum GenomeVariantType {
	/** single nucleotide variant */
	SNV,
	/** insertion */
	INSERTION,
	/** deletion */
	DELETION,
	/** block substitution */
	BLOCK_SUBSTITUTION,
	/** other, e.g., structural variant from symbolic allele */
	OTHER;
}
