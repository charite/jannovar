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

   
    
  
  
    /* 4) Index of fields of the DP4 (depth) from the VCF file:
       ref-forward bases, ref-reverse, alt-forward and alt-reverse bases
    */
    
    public static final int N_REF_FORWARD_BASES=0;
    public static final int N_REF_REVERSE_BASES=1;
    public static final int N_ALT_FORWARD_BASES=2;
    public static final int N_ALT_REVERSE_BASES=3;
      

  

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