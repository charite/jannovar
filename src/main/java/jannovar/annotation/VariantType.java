package jannovar.annotation;

/**
 * These codes reflect the possible types of variants that we call for an exome. Note that the codes have the obvious
 * meanings, but UTR53 means a variant that is in the 3' UTR of one transcript and the 5' UTR of another transcript.
 *
 * Note that the an intergenic variant is considered UPSTREAM or DOWNSTREAM if it is within 1000 nucleotides of a gene,
 * otherwise INTERGENIC. This behavior is controlled by the constant NEARGENE in {@link jannovar.reference.Chromosome
 * Chromosome}. Note that this class implements the assignment of a priority level to the variant classes. See the
 * document for the class {@link jannovar.annotation.AnnotationCollector AnnotatedVariantFactory} for details.
 *
 *
 * @author Peter Robinson <peter.robinson@charite.de>
 * @author Marten Jaeger <marten.jaeger@charite.de>
 */
// TODO(mjaeger): the outputnames for structural variants...
public enum VariantType {
	/** whole exon los, SO:"A feature ablation whereby the deleted region includes a transcript feature */
	TRANSCRIPT_ABLATION,
	/** variant is downstream of a gene */
	DOWNSTREAM,
	/** deletion resulting in a frameshift */
	FS_DELETION,
	/** insertion resulting in a frameshift */
	FS_INSERTION,
	/** nucleotide substitution that does not result in a frameshift */
	NON_FS_SUBSTITUTION,
	/** nucleotide substitution that results in a frameshift */
	FS_SUBSTITUTION,
	/** variant located between two genes (far enough away not to qualify as upstream/downstream) */
	INTERGENIC,
	/** variant located in an intron */
	INTRONIC,
	/**
	 * Variant that leads to the substitution of one amino acid (note this was earlier "NONYSYNONYMOUS" but the term
	 * name was changed to conform with the terminology of the <a href="http://www.sequenceontology.org/">Sequence
	 * Ontology</a>).
	 */
	MISSENSE,
	/** variant located in an exon of a noncoding RNA gene */
	ncRNA_EXONIC,
	/** variant located in an intron of a noncoding RNA gene */
	ncRNA_INTRONIC,
	/** variant located in a splice donor region of a non-coding gene */
	ncRNA_SPLICE_DONOR,
	/** variant located in a splice donor region of a non-coding gene */
	ncRNA_SPLICE_ACCEPTOR,
	/** variant located in a splice donor region of a non-coding gene */
	ncRNA_SPLICE_REGION,
	/** deletion that does not result in a frameshift */
	NON_FS_DELETION,
	/** insertion that does not result in a frameshift */
	NON_FS_INSERTION,
	/** variant located changing the 2 base region at the 5' end of an intron */
	SPLICE_ACCEPTOR,
	/** variant located changing the 2 base region at the 3' end of an intron */
	SPLICE_DONOR,
	/** variant either located in 1-3 bases of an exon or 3-8 bases of an intron */
	SPLICE_REGION,
	/** variant that induces a new stop codon (i.e., nonsense) */
	STOPGAIN,
	/** variant that alters and removes a wildtype stop codon */
	STOPLOSS,
	/** nucleotide substitution that does not alter the encoded amino acid of the affected codon */
	SYNONYMOUS,
	/** variant is upstream of a gene */
	UPSTREAM,
	/** variant is located in the 3' untranslated region */
	UTR3,
	/** variant is located in the 5' untranslated region */
	UTR5,
	/** variant assessed as probably erroneous (may indicate an error in the VCF file) */
	ERROR,
	/** nucleotide duplication that does not result in a frameshift */
	NON_FS_DUPLICATION,
	/** nucleotide duplication that results in a frameshift */
	FS_DUPLICATION,
	/** variation leads to the loss of the start codon */
	START_LOSS,
	/** variant is a structural insertion variant &gt;=1000bp */
	SV_INSERTION,
	/** variant is a structural deletion variant &gt;=1000bp */
	SV_DELETION,
	/** variant is a structural substitution variant &gt;=1000bp */
	SV_SUBSTITUTION,
	/** variant is a structural inversion variant &gt;=1000bp */
	SV_INVERSION;

	/**
	 * The preference level for annotations is
	 * <OL>
	 * <LI><B>exonic (1)</B>: FS_DELETION, FS_INSERTION, NON_FS_SUBSTITUTION, FS_SUBSTITUTION, MISSENSE,
	 * NON_FS_DELETION, NON_FS_INSERTION, STOPGAIN, STOPLOSS, FS_DUPLICATION, NON_FS_DUPLICATION, START_LOSS,
	 * START_GAIN, TRANSCRIPT_ABLATION.
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
	 *
	 * @param vt
	 *            Type of the variant
	 * @return priority level for sorting lists of variants.
	 */
	public static int priorityLevel(VariantType vt) {
		switch (vt) {
		case TRANSCRIPT_ABLATION:
		case FS_DELETION:
		case FS_INSERTION:
		case NON_FS_SUBSTITUTION:
		case FS_SUBSTITUTION:
		case MISSENSE:
		case NON_FS_DELETION:
		case NON_FS_INSERTION:
		case SPLICE_DONOR:
		case SPLICE_ACCEPTOR:
		case SPLICE_REGION:
		case STOPGAIN:
		case STOPLOSS:
		case FS_DUPLICATION:
		case NON_FS_DUPLICATION:
		case START_LOSS:
		case SV_DELETION:
		case SV_INSERTION:
		case SV_SUBSTITUTION:
		case SV_INVERSION:
			return 1;
		case ncRNA_EXONIC:
		case ncRNA_SPLICE_DONOR:
		case ncRNA_SPLICE_ACCEPTOR:
		case ncRNA_SPLICE_REGION:
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
	 * We do not know, actually, whether any given variant is pathogenic if we just judge its pathogenicity class. But
	 * on the whole, the VariantTypes that have been given the priority level one will include the lion's share of true
	 * pathogenic mutations. This function returns true if a variantType has pathogenicity level one, otherwise false.
	 * It is intended to be used by client code to help sort variants by predicted pathogenicity, in the knowledge that
	 * occasionally we will be wrong, e.g., a variant of priority level 3 might actually be the disease causing
	 * mutation.
	 *
	 * @return <code>true</code> if a variantType has pathogenicity level one, otherwise <code>false</code>
	 */
	public boolean isTopPriorityVariant() {
		return VariantType.priorityLevel(this) == 1;
	}

	/**
	 * This returns an array with the VariantTypes arranged according to their priority. It can used to arrange output
	 * of Variants ranked according to presumed pathogenicity.
	 *
	 * @return an array with the VariantTypes priority sorted
	 */
	public static VariantType[] getPrioritySortedList() {
		VariantType[] vta = new VariantType[] { TRANSCRIPT_ABLATION, SV_DELETION, SV_INSERTION, SV_SUBSTITUTION,
				SV_INVERSION, MISSENSE, STOPGAIN, SPLICE_DONOR, SPLICE_ACCEPTOR, SPLICE_REGION, FS_DELETION,
				FS_INSERTION, FS_SUBSTITUTION, NON_FS_DELETION, NON_FS_INSERTION, NON_FS_SUBSTITUTION, STOPLOSS,
				FS_DUPLICATION, NON_FS_DUPLICATION, START_LOSS, ncRNA_EXONIC, ncRNA_SPLICE_DONOR,
				ncRNA_SPLICE_ACCEPTOR, ncRNA_SPLICE_REGION, UTR3, UTR5, SYNONYMOUS, INTRONIC, ncRNA_INTRONIC, UPSTREAM,
				DOWNSTREAM, INTERGENIC, ERROR };
		return vta;
	}

	/**
	 * A string representing the variant type (e.g., missense_variant, stop_gained,...)
	 *
	 * @return Name of this {@link VariantType}
	 */
	public String toDisplayString() {
		switch (this) {
		case TRANSCRIPT_ABLATION:
			return "transcript ablation";
		case FS_DELETION:
			return "frameshift truncation";
		case FS_INSERTION:
			return "frameshift elongation";
		case NON_FS_SUBSTITUTION:
			return "inframe substitution";
		case FS_SUBSTITUTION:
			return "frameshift substitution";
		case MISSENSE:
			return "missense";
		case NON_FS_DELETION:
			return "inframe deletion";
		case NON_FS_INSERTION:
			return "inframe insertion";
		case SPLICE_DONOR:
			return "splice donor";
		case SPLICE_ACCEPTOR:
			return "splice acceptor";
		case SPLICE_REGION:
			return "splice region";
		case STOPGAIN:
			return "stopgain";
		case STOPLOSS:
			return "stoploss";
		case NON_FS_DUPLICATION:
			return "inframe duplication";
		case FS_DUPLICATION:
			return "frameshift duplication";
		case START_LOSS:
			return "startloss";
		case ncRNA_EXONIC:
			return "ncRNA exonic";
		case ncRNA_INTRONIC:
			return "ncRNA intronic";
		case ncRNA_SPLICE_ACCEPTOR:
			return "ncRNA splice acceptor";
		case ncRNA_SPLICE_DONOR:
			return "ncRNA splice donor";
		case ncRNA_SPLICE_REGION:
			return "ncRNA splice region";
		case UTR3:
			return "UTR3";
		case UTR5:
			return "UTR5";
		case SYNONYMOUS:
			return "synonymous";
		case INTRONIC:
			return "intronic";
		case UPSTREAM:
			return "upstream";
		case DOWNSTREAM:
			return "downstream";
		case INTERGENIC:
			return "intergenic";
		case ERROR:
			return "error";
		case SV_DELETION:
			return "1k+ deletion";
		case SV_INSERTION:
			return "1k+ insertion";
		case SV_SUBSTITUTION:
			return "1k+ substitution";
		case SV_INVERSION:
			return "1k+ inversion";
		default:
			return "unknown variant type (error)";
		}
	}

	/**
	 * A Sequence Ontology (SO) term string representing the variant type (e.g., missense_variant, stop_gained,...)
	 *
	 * @return SO-term representation of this {@link VariantType}
	 */
	public String toSequenceOntologyTerm() {
		switch (this) {
		case TRANSCRIPT_ABLATION:
			return "transcript_ablation";
		case FS_DELETION:
			return "frameshift_truncation";
		case FS_INSERTION:
			return "frameshift_elongation";
		case NON_FS_SUBSTITUTION:
			return "inframe_substitution";
		case FS_SUBSTITUTION:
			return "frameshift_substitution";
		case MISSENSE:
			return "missense_variant";
		case NON_FS_DELETION:
			return "inframe_deletion";
		case NON_FS_INSERTION:
			return "inframe_insertion";
		case SPLICE_DONOR:
			return "splice_donor_variant";
		case SPLICE_ACCEPTOR:
			return "splice_acceptor_variant";
		case SPLICE_REGION:
			return "splice_region_variant";
		case STOPGAIN:
			return "stop_gained";
		case STOPLOSS:
			return "stop_lost";
		case NON_FS_DUPLICATION:
			return "inframe_duplication";
		case FS_DUPLICATION:
			return "frameshift_duplication";
		case START_LOSS:
			return "start_lost";
		case ncRNA_EXONIC:
			return "non_coding_exon_variant";
		case ncRNA_INTRONIC:
			return "non_coding_intron_variant";
		case ncRNA_SPLICE_DONOR:
			return "non_coding_splice_donor_variant";
		case ncRNA_SPLICE_ACCEPTOR:
			return "non_coding_splice_acceptor_variant";
		case ncRNA_SPLICE_REGION:
			return "non_coding_splice_region_variant";
		case UTR3:
			return "3_prime_UTR_variant";
		case UTR5:
			return "5_prime_UTR_variant";
		case SYNONYMOUS:
			return "synonymous_variant";
		case INTRONIC:
			return "intron_variant";
		case UPSTREAM:
			return "upstream_gene_variant";
		case DOWNSTREAM:
			return "downstream_gene_variant";
		case INTERGENIC:
			return "intergenic_variant";
		case ERROR:
			return "error";
		case SV_DELETION:
			return "deletion";
		case SV_INSERTION:
			return "insertion";
		case SV_SUBSTITUTION:
			return "substitution";
		case SV_INVERSION:
			return "inversion";
		default:
			return "unknown variant type (error)";
		}
	}

	/**
	 * Return the sequence ontology accession number for the variant class if available, otherwise return the name.
	 *
	 * @return sequence ontology accession number
	 */
	public String toSequenceOntologyID() {
		switch (this) {
		case TRANSCRIPT_ABLATION:
			return "SO:0001893";
		case FS_DELETION:
			return "SO:0001910";
		case FS_INSERTION:
			return "SO:0001909";
		case NON_FS_SUBSTITUTION:
			return "nonframeshift substitution";
		case FS_SUBSTITUTION:
			return "frameshift substitution";
		case MISSENSE:
			return "SO:0001583";
		case NON_FS_DELETION:
			return "SO:0001822";
		case NON_FS_INSERTION:
			return "SO:0001821";
		case SPLICE_DONOR:
			return "SO:0001575";
		case SPLICE_ACCEPTOR:
			return "SO:0001574";
		case SPLICE_REGION:
			return "SO:0001630";
		case STOPGAIN:
			return "SO:0001587";
		case STOPLOSS:
			return "SO:0001578";
		case NON_FS_DUPLICATION:
			return "nonframeshift duplication";
		case FS_DUPLICATION:
			return "frameshift duplication";
		case START_LOSS:
			return "start loss";
		case ncRNA_EXONIC:
			return "SO:0001792";
		case ncRNA_INTRONIC:
			return "noncoding RNA intronic";
		case ncRNA_SPLICE_DONOR:
			return "noncoding RNA splice donor";
		case ncRNA_SPLICE_ACCEPTOR:
			return "noncoding RNA splice acceptor";
		case ncRNA_SPLICE_REGION:
			return "noncoding RNA splice region";
		case UTR3:
			return "SO:0001624";
		case UTR5:
			return "SO:0001623";
		case SYNONYMOUS:
			return "SO:0001819";
		case INTRONIC:
			return "SO:0001627";
		case UPSTREAM:
			return "SO:0001631";
		case DOWNSTREAM:
			return "SO:0001632";
		case INTERGENIC:
			return "SO:0001628";
		case ERROR:
			return "error";
		case SV_DELETION:
			return "SO:0000159";
		case SV_INSERTION:
			return "SO:0000667";
		case SV_SUBSTITUTION:
			return "SO:1000002";
		case SV_INVERSION:
			return "SO:1000036";
		default:
			return "unknown variant type (error)";
		}
	}

	/**
	 * A static constant that returns the number of different values in this enumeration.
	 */
	private static final int size = VariantType.values().length;

	/** @return the number of different values in this enumeration. */
	public static int size() {
		return VariantType.size;
	}

}
