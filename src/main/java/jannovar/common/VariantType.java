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
 * @version 0.07 (7 July, 2013)
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
	ERROR;



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
}
