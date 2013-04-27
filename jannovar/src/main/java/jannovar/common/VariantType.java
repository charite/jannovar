package jannovar.common;


/**
 * These codes reflect the possible types of variants that we call for an exome. Note that the
 * codes have the obvious meanings, but UTR53 means a variant that is in the 3' UTR of one transcript
 * and the 5' UTR of another transcript.
 * <p>
 * Note that the an intergenic variant is considered UPSTREAM or DOWNSTREAM if it is within 1000 nucleotides of 
 * a gene, otherwise INTERGENIC. This behavior is controlled by the constant NEARGENE in 
 * {@link jannovar.reference.Chromosome Chromosome} (maybe make this adjustable in future versions),
 * @author Peter Robinson
 * @version 0.03 (28 April, 2013)
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
	/** Probaby remove (Preferred terminology is NONSYNONYMOUS TODO */
	MISSENSE, 
	/** Variant located in an exon of a noncoding RNA gene */
	ncRNA_EXONIC, 
       /** Variant located in an intron of a noncoding RNA gene */
	ncRNA_INTRONIC, 
	/** Variant located in a splice site of a noncoding RNA gene */
	ncRNA_SPLICING,
	/** Category from annovar: TODO-does not make sense */
	ncRNA_UTR3, 
	/** Category from annovar: TODO-does not make sense */
	ncRNA_UTR5, 
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
	/** Variant with unknown effect (this indicates some error in the code)*/
	UNKNOWN, 
	 /** Variant is upstream of a gene */
	UPSTREAM, 
	/** Variant is located in the 3' untranslated region */
	UTR3, 
	/** Variant is located in the 5' untranslated region */
	UTR5, 
	/** Variant is located in the 3' untranslated region of one gene and the 5' UTR of an overlapping gene*/
	UTR53,
	/** Variant assesed as probabily erroneous (this indicates some error in the VCF file)*/
	ERROR;

}
