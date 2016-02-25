package de.charite.compbio.jannovar.reference;

/**
 * Types of genomic variants represented by {@link GenomeVariant}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
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
