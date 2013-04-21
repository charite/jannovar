package jannovar.common;



/**
 * This interface has some numerical contants that are used
 * by various other classes in the Exomizer to refer to 
 * various entities. 
 * Chromosomes 1-22 are refered to by the corresponding ints, and chromosomes
 * X, Y, and M are referred to as below.
 * @author Peter N Robinson
 * @version 0.15 (15 February, 2013)
 */
public interface Constants {
    /* 1) Chromosomes */
    public static final byte X_CHROMOSOME = 23;
    public static final byte Y_CHROMOSOME = 24;
    public static final byte M_CHROMOSOME = 25;

   
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
     */
    public static enum GenotypeCall {HOMOZYGOUS_REF, HOMOZYGOUS_ALT, HETEROZYGOUS, NOT_OBSERVED, UNKNOWN, UNINITIALIZED };

    /**
     * These codes reflect the possible types of variants that we call for an exome. Note that the
     * codes have the obvious meanings, but UTR53 means a variant that is in the 3' UTR of one transcript
     * and the 5' UTR of another transcript.
     */
    public static enum VariantType { DOWNSTREAM, EXONIC, FS_DELETION, FS_INSERTION, NON_FS_SUBSTITUTION,
	    FS_SUBSTITUTION , INTERGENIC, INTRONIC, MISSENSE, ncRNA_EXONIC, ncRNA_INTRONIC, ncRNA_SPLICING,
	    ncRNA_UTR3, ncRNA_UTR5, NON_FS_DELETION , NON_FS_INSERTION, SPLICING, STOPGAIN,
	    STOPLOSS, SYNONYMOUS, UNKNOWN, UPSTREAM, UTR3, UTR5, UTR53,POSSIBLY_ERRONEOUS};

  
    /* 4) Index of fields of the DP4 (depth) from the VCF file:
       ref-forward bases, ref-reverse, alt-forward and alt-reverse bases
    */
    
    public static final int N_REF_FORWARD_BASES=0;
    public static final int N_REF_REVERSE_BASES=1;
    public static final int N_ALT_FORWARD_BASES=2;
    public static final int N_ALT_REVERSE_BASES=3;
      


     /* The following constants are flags that
	cause a specially formated field to be displayed in the HTML table. */
    public static final int GENOMIC_VAR = 101;
   

    /** Flag for an integer value that has not been initialized. */
    public static final int UNINITIALIZED_INT = -10;
    /** Flag for an float value that has not been initialized. */
    public static final float UNINITIALIZED_FLOAT = -10;
    /** Flag for an integer field that could not be parsed correctly */
    public static final int NOPARSE = -5; 
    /** Flag for a float field that could not be parsed correctly */
    public static final float NOPARSE_FLOAT = -5f;
    /** Flag for no rsID for variant */
    public static final int NO_RSID = -1;

    /**
     * An enumeration of the four main Mendelian modes of inheritance for
     * prioritizing exome data.
     */
    public static enum ModeOfInheritance {AUTOSOMAL_DOMINANT, AUTOSOMAL_RECESSIVE, X_RECESSIVE, X_DOMINANT , UNINITIALIZED};


}