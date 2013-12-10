package jannovar.common;


/**
 * These codes reflect the possible types of variants that we call for an exome. Note that the
 * codes have the obvious meanings, but UTR53 means a variant that is in the 3' UTR of one transcript
 * and the 5' UTR of another transcript.
 * <p>
 * Note that the an intergenic variant is considered UPSTREAM or DOWNSTREAM if it is within 1000 nucleotides of 
 * a gene, otherwise INTERGENIC. This behavior is controlled by the constant NEARGENE in 
 * {@link jannovar.reference.Chromosome Chromosome}.
 * Note that this class implements the assignment of a priority level to the variant classes. See
 * the document for the class
  {@link jannovar.annotation.AnnotatedVariantFactory AnnotatedVariantFactory} for details.
 * @author Peter Robinson
 * @version 0.12 (17 November, 2013)
 */
public enum  VariantType { 
    /** Variant is downstream of a gene */
    DOWNSTREAM, 
	/** Deletion resulting in a frameshift. */
	FS_DELETION, 
	/** Insertion resulting in a frameshift. */
	FS_INSERTION, 
	/** Nucleotide substitution that does not result in a frameshift. */
	NON_FS_SUBSTITUTION,
	/** Nucleotide substitution that results in a frameshift. */
	FS_SUBSTITUTION, 
	/** variant located between two genes (far enough away not to qualify as upstream/downstream) */
	INTERGENIC, 
	/** Variant located in an intron. */
	INTRONIC, 
	/** Variant that leads to the subsitution of one amino acid. */
	NONSYNONYMOUS, 
	/** Variant located in an exon of a noncoding RNA gene */
	ncRNA_EXONIC, 
       /** Variant located in an intron of a noncoding RNA gene */
	ncRNA_INTRONIC, 
	/** Variant located in a splice site of a noncoding RNA gene */
	ncRNA_SPLICING,
	/** Deletion that does not result in a frameshift. */
	NON_FS_DELETION , 
	/** Insertion that does not result in a frameshift. */
	NON_FS_INSERTION, 
	/** variant located in a splice site */
	SPLICING, 
	/** Variant that induces a new stop codon (i.e., nonsense) */
	STOPGAIN,
	/** Variant that alters and removes a wildtype stop codon */
	STOPLOSS, 
	/** Nucleotide substution that does not alter the encoded amino acid of the affected codon. */
	SYNONYMOUS, 
	/** Variant is upstream of a gene */
	UPSTREAM, 
	/** Variant is located in the 3' untranslated region */
	UTR3, 
	/** Variant is located in the 5' untranslated region */
	UTR5, 
	/** Variant assesed as probably erroneous (may indicate an error in the VCF file)*/
	ERROR,
	/** Nucleotide duplication that does not result in a frameshift. */
	NON_FS_DUPLICATION,
	/** Nucleotide duplication that results in a frameshift. */
	FS_DUPLICATION,
	/** Variation leads to the loss of the start codon */
	START_LOSS,
	/** Variation leads to the gain of a new the start codon e.g. translation initiation site up- or downstream*/
	START_GAIN;


    /**
     * The preference level for annotations is 
     * <OL>
     * <LI><B>exonic (1)</B>: FS_DELETION, FS_INSERTION, NON_FS_SUBSTITUTION, FS_SUBSTITUTION, 
     * NONSYNONYMOUS, NON_FS_DELETION, NON_FS_INSERTION, STOPGAIN, STOPLOSS.
     * <LI><B>splicing (1)</B>: SPLICING.
     * <LI><B>ncRNA (2)</B>:ncRNA_EXONIC, ncRNA_SPLICING.
     * <LI><B>UTR3 (3)</B>: UTR3
     * <LI><B>UTR5 (4)</B>: UTR5
     * <LI><B>synonymous (5)</B>: SYNONYMOUS
     * <LI><B>intronic (6)</B>: INTRONIC
     * <LI><B>intronic (7)</B>: ncRNA_INTRONIC.
     * <LI><B>upstream (8)</B>: UPSTREAM.
     * <LI><B>downstream (9)</B>: DOWNSTREAM.
     * <LI><B>intergenic (10)</B>: INTERGENIC.
     * <LI><B>error (11)</B>: ERROR.
     * </OL>
     * @param vt Type of the variant
     * @return priority level for sorting lists of variants.
     */
    public static int priorityLevel(VariantType vt) {
	switch (vt) {
	case FS_DELETION:
	case FS_INSERTION:
	case NON_FS_SUBSTITUTION:
	case FS_SUBSTITUTION:
	case NONSYNONYMOUS:
	case NON_FS_DELETION:
	case NON_FS_INSERTION:
	case SPLICING: 
	case STOPGAIN:
	case STOPLOSS:
	case FS_DUPLICATION:
	case NON_FS_DUPLICATION:
	case START_LOSS:
	case START_GAIN:
	    return 1;
	case ncRNA_EXONIC:
	case ncRNA_SPLICING:
	    return 2;
	case UTR3:
	    return 3;
	case UTR5:
	    return 4;
	case SYNONYMOUS:
	    return 5;
	case INTRONIC:
	    return 6;
	case ncRNA_INTRONIC:
	    return 7;
	case UPSTREAM:
	case DOWNSTREAM:
	    return 8;
	case INTERGENIC:
	return 9;
	case ERROR:
	    return 10;
	default:
	    return 10; /* should never get here */
	}
    }

    /**
     * This returns an array with the VariantTypes arranged according to 
     * their priority. It can used to arrange output of Variants ranked
     * accordingto presumed pathogenicity.
     */
    public static VariantType[] getPrioritySortedList() {
	VariantType[] vta = new VariantType[] { NONSYNONYMOUS, STOPGAIN, SPLICING,
						FS_DELETION,FS_INSERTION,FS_SUBSTITUTION,
						NON_FS_DELETION,NON_FS_INSERTION,NON_FS_SUBSTITUTION,
						STOPLOSS,
						ncRNA_EXONIC,ncRNA_SPLICING,
						UTR3, UTR5,
						SYNONYMOUS,INTRONIC,
						ncRNA_INTRONIC,
						UPSTREAM,DOWNSTREAM,INTERGENIC,
						ERROR };
	return vta;

    }



    /**
     * Return a string representation of the variant type
     * passed as an argument.
     * @param vt The variant type of a variant.
     */
    public static String variantTypeAsString(VariantType vt) {
	switch (vt) {
	case FS_DELETION: return "frameshift deletion";
	case FS_INSERTION: return "frameshift insertion";
	case NON_FS_SUBSTITUTION: return "nonframeshift substitution";
	case FS_SUBSTITUTION:  return "frameshift substitution";
	case NONSYNONYMOUS:  return "missense";
	case NON_FS_DELETION: return "nonframeshift deletion";
	case NON_FS_INSERTION:return "nonframeshift insertion";
	case SPLICING: return "splicing";
	case STOPGAIN:return "stopgain";
	case STOPLOSS: return "stoploss";
	case ncRNA_EXONIC: return "noncoding RNA exonic";
	case ncRNA_SPLICING:return "noncoding RNA splicing";
	case UTR3: return "UTR3";
	case UTR5: return "UTR5";
	case SYNONYMOUS: return "synonymous";
	case INTRONIC: return "intronic";
	case ncRNA_INTRONIC:return "noncoding RNA intronic";
	case UPSTREAM:return "upstream";
	case DOWNSTREAM:return "downstream";
	case INTERGENIC:return "intergenic";
	default:
	    return "unknown variant type (error)";
	}
    }
    
    /** A static constant that returns the number of
     * different values in this enumeration.
     */
    private static final int size = VariantType.values().length;

    /** @return the number of different values in this enumeration. */
    public static int size() { return VariantType.size; }

}
