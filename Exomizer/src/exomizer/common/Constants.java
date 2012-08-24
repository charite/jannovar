package exomizer.common;



/**
 * This interface has some numerical contants that are used
 * by various other classes in the Exomizer to refer to 
 * various entities. 
 * Chromosomes 1-22 are refered to by the corresponding ints, and chromosomes
 * X, Y, and M are referred to as below.
 */
public interface Constants {
    /* 1) Chromosomes */
    public static final byte X_CHROMOSOME = 23;
    public static final byte Y_CHROMOSOME = 24;
    public static final byte M_CHROMOSOME = 25;

    /* 2) Genotypes */
    public static final byte GENOTYPE_HOMOZYGOUS_REF  = 1;
    public static final byte GENOTYPE_HOMOZYGOUS_ALT  = 2;
    public static final byte GENOTYPE_HETEROZYGOUS    = 3;
    public static final byte GENOTYPE_UNKNOWN         = 4;
    public static final byte GENOTYPE_NOT_INITIALIZED = 5;

    /* 3) Mutation types (from annovar) */
    public static final byte DOWNSTREAM = 1;
    public static final byte EXONIC = 2;
    public static final byte FS_DELETION = 3;
    public static final byte FS_INSERTION = 4;
    public static final byte NON_FS_SUBSTITUTION = 5;
    public static final byte FS_SUBSTITUTION = 6;
    public static final byte INTERGENIC = 7;
    public static final byte INTRONIC = 8;
    public static final byte MISSENSE = 9;
    public static final byte ncRNA_EXONIC = 10;
    public static final byte ncRNA_BYTERONIC = 11;
    public static final byte ncRNA_SPLICING = 12;
    public static final byte ncRNA_UTR3 = 13;
    public static final byte ncRNA_UTR5 = 14;
    public static final byte NON_FS_DELETION = 15;
    public static final byte NON_FS_INSERTION = 16;
    public static final byte NONSENSE = 17;
    public static final byte SPLICING= 18;
    public static final byte STOPGAIN = 19;
    public static final byte STOPLOSS = 20;
    public static final byte SYNONYMOUS = 21;
    public static final byte VARIANT_TYPE_UNKNOWN = 22;
    public static final byte UPSTREAM = 23;
    public static final byte UTR3 = 24;
    public static final byte UTR5 = 25;

    


}