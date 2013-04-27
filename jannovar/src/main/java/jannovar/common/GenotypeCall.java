package jannovar.common;


/**
 * These codes will be used to denote the genotype of a {@link jannovar.exome.Variant Variant}
 * in an individual sample. Note that this enumeration denotes the call (e.g., 0/1) of a
 * genotype, whereas the classes derived from {@link jannovar.exome.GenotypeI GenotypeI}
 * have data with respect to quality and about single or multiple samples.
 * <P>
 * Note that the constant {@code NOT_OBSERVED} refers to <B>./.</B> in a VCF file with multiple
 * samples means a call cannot be made for the sample at this given locus (e.g., because there were
 * nor reads). The constant {@code UNKNOWN} is used here to refer to some error condition and
 * actually should never be used at all if the Java code performs correctly. The constant
 * {@code UNINITIALIZED} is used to indicate that a software object has been constructed but not been
 * initialized by parsing. Again, this should actually never happen since various Exceptions will be
 * thrown if there is a parse error. But the constants are kept for now for debugging's sake.
 * @author Peter Robinson
 * @version 0.02 (22 April, 2013)
 */
public enum  GenotypeCall {
    /** 0/0 in the VCF genotype field, homozygous reference sequence. */
    HOMOZYGOUS_REF, 
	/** 1/1 in the VCF genotype field, homozygous alternate sequence */
	HOMOZYGOUS_ALT, 
	/** 0/1 in the VCF genotype field, heterozygous sequence. */
	HETEROZYGOUS, 
	/** ./. in the VCF genotype field (not observed, this is usually encoutered in multisample VCF files). */
	NOT_OBSERVED, 
	/** Indicates some error in parsing */
	UNKNOWN, 
	/** Not yet initialized field */
	UNINITIALIZED;
}
