package jannovar.common;


/**
 * These codes will be used to denote the genotype of a Variant identified in a
 * line of a VCF file in an individual sample.
 * <P>
 * Note that this enumeration denotes the call (e.g., 0/1) of a
 * genotype, and is used together with data in the PED file to decide whether
 * the set of genotypes in a VCF file is compatible with the indicated mode of
 * inheritance and pedigree structure.
 * <P>
 * Note that the constant {@code NOT_OBSERVED} refers to <B>./.</B> in a VCF file with multiple
 * samples means a call cannot be made for the sample at this given locus (e.g., because there were
 * nor reads). The constant {@code ERROR} is used here to refer to some error condition and
 * actually should never be used at all if the Java code performs correctly. The constant
 * {@code UNINITIALIZED} is used to indicate that a software object has been constructed but not been
 * initialized by parsing. Again, this should actually never happen since various Exceptions will be
 * thrown if there is a parse error. But the constants are kept for now for debugging's sake.
 * @author Peter Robinson
 * @version 0.03 (5 May, 2013)
 */
public enum  Genotype {
    /** 0/0 in the VCF genotype field, homozygous reference sequence. */
    HOMOZYGOUS_REF, 
	/** 1/1 in the VCF genotype field, homozygous alternate sequence */
	HOMOZYGOUS_ALT, 
	/** 0/1 in the VCF genotype field, heterozygous sequence. */
	HETEROZYGOUS, 
	/** ./. in the VCF genotype field (not observed, this is usually encoutered in multisample VCF files). */
	NOT_OBSERVED, 
	/** Indicates some error in parsing */
	ERROR, 
	/** Not yet initialized field */
	UNINITIALIZED;
}
