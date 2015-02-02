package de.charite.compbio.jannovar.annotation;

/**
 * These codes reflect the possible types of variants that we call for an exome.
 *
 * Note that the codes have the obvious meanings, but UTR53 means a variant that is in the 3' UTR of one transcript and
 * the 5' UTR of another transcript.
 *
 * The values in this enum are given in the putative order of severity (more severe to less severe).
 *
 * @author Peter Robinson <peter.robinson@charite.de>
 * @author Marten Jaeger <marten.jaeger@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public enum VariantType {
	/**
         * <code>(SO:1000182)</code> A kind of chromosome variation where the chromosome complement is not an exact multiple
         * of the haploid number (is a chromosome_variation).
	 */
        CHROMOSOME_NUMBER_VARIATION,
        /**
         * <code>(SO:0001572)</code> A sequence variant whereby an exon is lost from the transcript (is a splicing_variant,
         * transcript_variant). EXON_LOSS_VARIANT, /** <code>(SO:0001589)</code> A sequence variant which causes a
         * disruption of the translational reading frame, because the number of nucleotides inserted or deleted is not a
         * multiple of three (is a protein_altering_variant).
         */
        FRAMESHIFT_VARIANT,
        /**
         * <code>(SO:0001587)</code> A sequence variant whereby at least one base of a codon is changed, resulting in a
         * premature stop codon, leading to a shortened transcript (is a: nonsynonymous_variant, feature_truncation).
         * STOP_GAINED, /** <code>SO:0001578</code> A sequence variant where at least one base of the terminator codon
         * (stop) is changed, resulting in an elongated transcript (is a: nonsynonymous variant, terminator_codon_variant,
         * feature_elongation) STOP_LOST, /** <code>SO:0002012</code> A codon variant that changes at least one base of the
         * canonical start codon (is a: initiator_codon_variant).
         */
        START_LOST,
        /**
         * <code>SO:0001574</code> A splice variant that changes the 2 base region at the 3' end of an intron (is a:
         * {@link #SPLICE_SITE_VARIANT})
         */
        SPLICE_ACCEPTOR_VARIANT,
        /**
         * <code>SO:0001575</code> A splice variant that changes the 2 base pair region at the 5' end of an intron (is a:
         * {@link #SPLICE_SITE_VARIANT}).
         */
        SPLICE_DONOR_VARIANT,
        /**
         * <code>SO:0002008</code> A sequence variant whereby at least one base of a codon encoding a rare amino acid is
         * changed, resulting in a different encoded amino acid (children: selenocysteine_loss, pyrrolysine_loss).
         */
        RARE_AMINO_ACID_VARIANT,
        /**
         * <code>SO:0001583</code> A sequence variant, that changes one or more bases, resulting in a different amino acid
         * sequence but where the length is preserved.</code>
         */
        MISSENSE_VARIANT,
        /**
         * <code>SO:0001821</code> An inframe non synonymous variant that inserts bases into in the coding sequence (is a:
         * inframe_indel, internal_feature_elongation).
         */
        INFRAME_INSERTION,
        /**
         * <code>SO:0001824</code> An inframe increase in cds length that inserts one or more codons into the coding
         * sequence within an existing codon (is a: {@link #INFRAME_INSERTION}).
         */
        DISRUPTIVE_INFRAME_INSERTION,
        /**
         * <code>SO:0001822</code> An inframe non synonymous variant that deletes bases from the coding sequence (is a:
         * inframe_indel, feature_truncation).
         */
        INFRAME_DELETION,
        /**
         * <code>SO:0001826</code> An inframe decrease in cds length that deletes bases from the coding sequence starting
         * within an existing codon (is a: {@link #INFRAME_DELETION}).
         */
        DISRUPTIVE_INFRAME_DELETION,
        /**
         * <code>SO:0002013</code> A sequence variant that causes the reduction of a the 5'UTR with regard to the reference
         * sequence (is a: {@link #FIVE_PRIME_UTR_VARIANT})
         */
        FIVE_PRIME_UTR_TRUNCATION,
        /**
         * <code>SO:0001572</code> A sequence variant whereby an exon is lost from the transcript (is a: splicing_variant).
         */
        EXON_LOSS_VARIANT,
        /**
         * <code>SO:0002015</code> A sequence variant that causes the reduction of a the 3' UTR with regard to the reference
         * sequence (is a: {@link #THREE_PRIME_UTR_VARIANT}).
         */
        THREE_PRIME_UTR_TRUNCATION,
        /**
         * <code>SO:0001630</code> A sequence variant in which a change has occurred within the region of the splice site,
         * either within 1-3 bases of the exon or 3-8 bases of the intron (is a: splicing_variant).
         */
        SPLICE_REGION_VARIANT,
        /** <code>SO:0001567</code> (is a: {@link #SYNONYMOUS_VARIANT}, terminator_codon_variant). */
        STOP_RETAINED_VARIANT,
        /**
         * <code>SO:0001582</code> A codon variant that changes at least one base of the first codon of a transcript (is a:
         * coding_sequence_variant, children: start_retained_variant, start_lost).
         */
        INITIATOR_CODON_VARIANT,
        /**
         * <code>SO:0001819</code> A sequence variant where there is no resulting change to the encoded amino acid (is a:
         * coding_sequence_variant, children: start_retained_variant, stop_retained_variant).
         */
        SYNONYMOUS_VARIANT,
        /** <code>SO:0001623</code> A UTR variant of the 5' UTR (is a: UTR_variant). */
        FIVE_PRIME_UTR_VARIANT,
        /** <code>SO:0001624</code> A UTR variant of the 3' UTR (is a: UTR_variant). */
        THREE_PRIME_UTR_VARIANT,
        /**
         * <code>SO:0001983</code> A 5' UTR variant where a premature start codon is introduced, moved or lost (is a:
         * {@link #FIVE_PRIME_UTR_VARIANT}).
         */
        FIVE_PRIME_UTR_PREMATURE_START_CODON_GAIN_VARIANT,
        // TODO(holtgrem): add the 5KB and 2KB upstream/downstream variants.
        /** <code>SO:0001631</code> A sequence variant located 5' of a gene (is a: {@link #INTERGENIC_VARIANT}). */
        UPSTREAM_GENE_VARIANT,
        /** <code>SO:0001632</code> A sequence variant located 3' of a gene (is a: {@link #INTERGENIC_VARIANT}). */
        DOWNSTREAM_GENE_VARIANT,
        /**
         * <code>SO:0001782</code> A sequence variant located within a transcription factor binding site (is a:
         * {@link #REGULATORY_REGION_VARIANT}).
         */
        TF_BINDING_SITE_VARIANT,
        /** <code>SO:0001566</code> A sequence variant located within a regulatory region (is a: feature_variant). */
        REGULATORY_REGION_VARIANT,
        /**
         * <code>SO:0000276</code> Variant affects a miRNA (is a: miRNA_primary_transcript, small_regulatory_ncRNA).
         */
        MIRNA,
        /** Variant in a user-specified custom region. */
        CUSTOM,
        /** <code>SO:</code> (is a: ). */
        CONSERVED_INTRON_VARIANT,
        /** <code>SO:</code> (is a: ). */
        INTRON_VARIANT,
        /** <code>SO:</code> (is a: ). */
        INTRAGENIC_VARIANT,
        /** <code>SO:</code> (is a: ). */
        CONSERVED_INTERGENIC_VARIANT,
        /** <code>SO:</code> (is a: ). */
        INTERGENIC_REGION,
        /** <code>SO:</code> (is a: ). */
        CODING_SEQUENCE_VARIANT,
        /** <code>SO:</code> (is a: ). */
        NON_CODING_TRANSCRIPT_EXON_VARIANT,
        /** <code>SO:</code> (is a: ). */
        NON_CODING_TRANSCRIPT_VARIANT,
        /** <code>SO:</code> (is a: ). */
        GENE_VARIANT,
        /** <code>SO:</code> (is a: ). */
        CHROMOSOME;

	// TODO(holtgrem): changing impact
	/**
	 * @return the {@link PutativeImpact} of this variant type
	 */
	public PutativeImpact getPutativeImpact() {
		switch (this) {
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
		case STOP_RETAINED:
		case STOPGAIN:
		case STOPLOSS:
		case FS_DUPLICATION:
		case NON_FS_DUPLICATION:
		case START_LOSS:
		case SV_DELETION:
		case SV_INSERTION:
		case SV_SUBSTITUTION:
		case SV_INVERSION:
			return PutativeImpact.HIGH;
		case ncRNA_EXONIC:
		case ncRNA_SPLICE_DONOR:
		case ncRNA_SPLICE_ACCEPTOR:
		case ncRNA_SPLICE_REGION:
			return PutativeImpact.HIGH;
		case SYNONYMOUS:
		case INTRONIC:
		case ncRNA_INTRONIC:
			return PutativeImpact.LOW;
		case UPSTREAM:
		case DOWNSTREAM:
			return PutativeImpact.MODIFIER;
		case UTR3:
		case UTR5:
		case INTERGENIC:
			return PutativeImpact.MODIFIER;
		case ERROR:
		default:
			return PutativeImpact.MODIFIER;
		}
	}

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
	public int priorityLevel() {
		switch (this) {
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
		case STOP_RETAINED:
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
		return this.priorityLevel() == 1;
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
		case STOP_RETAINED:
			return "stop retained";
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
		case STOP_RETAINED:
			return "stop_retained";
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
		case STOP_RETAINED:
			return "SO:0001567";
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
	 * @return <code>true</code> if this variant type encodes a structural variant
	 */
	boolean isSV() {
		return (this == SV_DELETION) || (this == SV_INSERTION) || (this == SV_SUBSTITUTION) || (this == SV_INVERSION);
	}

	/**
	 * A static constant that returns the number of different values in this enumeration.
	 */
	public static final int size = VariantType.values().length;

}
