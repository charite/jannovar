package de.charite.compbio.jannovar.pedigree;

// TODO(holtgrew): Move the extensive description below to the tutorial?

/**
 * Enumeration of genotype kinds.
 *
 * Note that this enumeration denotes the call (e.g., <tt>0/1</tt>) of a genotype, and is used together with data in the
 * PED file to decide whether the set of genotypes in a VCF file is compatible with the indicated mode of inheritance
 * and pedigree structure.
 *
 * Note that the constant {@link #NOT_OBSERVED} refers to <tt>./.</tt> in a VCF file with multiple samples means a call
 * cannot be made for the sample at this given locus (e.g., because there were nor reads).
 *
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 */
public enum Genotype {
	/** <tt>0/0</tt> in the VCF genotype field, homozygous reference sequence. */
	HOMOZYGOUS_REF,
	/** <tt>1/1</tt> in the VCF genotype field, homozygous alternate sequence */
	HOMOZYGOUS_ALT,
	/** <tt>0/1</tt> in the VCF genotype field, heterozygous sequence. */
	HETEROZYGOUS,
	/** <tt>./.</tt> in the VCF genotype field (not observed, this is usually encoutered in multisample VCF files). */
	NOT_OBSERVED;
}
