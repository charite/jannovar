package de.charite.compbio.jannovar.annotation;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

// TODO(holtgrew): For now, only insert most specific variants by default, add switch that adds transitive changes
// TODO(holtgrew): structural variants need more refinement

/**
 * These codes reflect the possible types of variants that we call for an exome.
 *
 * The values in this enum are given in the putative order of impact (more severe to less severe). The documentation
 * gives the sequence ontology (SO) ID and the SO description. Also, the documentation of each value explains whether
 * Jannovar generates this annotation or not.
 *
 * @author <a href="mailto:Peter.Robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:marten.jaeger@charite.de">Marten Jaeger</a>
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public enum VariantEffect {

	//
	// HIGH Putative Impact
	//

	// change of feature structure or larger units
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:1000182">SO:1000182</a> A kind of chromosome
	 * variation where the chromosome complement is not an exact multiple of the haploid number (is a
	 * chromosome_variation).
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	CHROMOSOME_NUMBER_VARIATION,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001893">SO:0001893</a> A feature ablation
	 * whereby the deleted region includes a transcript feature (is a: feature_ablation)
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	TRANSCRIPT_ABLATION,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001572">SO:0001572</a> A sequence variant
	 * whereby an exon is lost from the transcript (is a (is a: {@link #SPLICING_VARIANT}), {@link #TRANSCRIPT_VARIANT}
	 * ).
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	EXON_LOSS_VARIANT,

	// high impact changes in the coding region
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001909">SO:0001909</a> A frameshift variant
	 * that causes the translational reading frame to be extended relative to the reference feature (is a
	 * {@link #FRAMESHIFT_VARIANT}, internal_feature_elongation).
	 */
	FRAMESHIFT_ELONGATION,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001910">SO:0001910</a> A frameshift variant
	 * that causes the translational reading frame to be shortened relative to the reference feature (is a
	 * {@link #FRAMESHIFT_VARIANT}, internal_feature_truncation).
	 */
	FRAMESHIFT_TRUNCATION,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001589">SO:0001589</a>A sequence variant
	 * which causes a disruption of the translational reading frame, because the number of nucleotides inserted or
	 * deleted is not a multiple of threee (is a: protein_altering_variant).
	 *
	 * Used for frameshift variant for the case where there is no stop codon any more and the rare case in which the
	 * transcript length is retained.
	 */
	FRAMESHIFT_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001908">SO:0001908</a> A sequence variant
	 * that causes the extension of a genomic feature from within the feature rather than from the terminus of the
	 * feature, with regard to the reference sequence.
	 *
	 * In Jannovar, used to annotate a {@link #COMPLEX_SUBSTITUTION} that does not lead to a frameshift and increases
	 * the transcript length.
	 */
	INTERNAL_FEATURE_ELONGATION,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001906">SO:0001906</a> A sequence variant
	 * that causes the reduction of a genomic feature, with regard to the reference sequence (is a: feature_variant).
	 *
	 * The term <a href="http://www.sequenceontology.org/browser/current_svn/term/INTERNAL_FEATURE_TRUNCATION">
	 * INTERNAL_FEATURE_TRUNCATION</a> would be more fitting but is not available in SO.
	 *
	 * In Jannovar, used to annotate a {@link #COMPLEX_SUBSTITUTION} that does not lead to a frameshift and decreases
	 * the transcript length.
	 */
	FEATURE_TRUNCATION,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0002007">SO:0002007</a> An MNV is a multiple
	 * nucleotide variant (substitution) in which the inserted sequence is the same length as the replaced sequence (is
	 * a: substitution).
	 *
	 * In Jannovar, only used for marking MNVs in coding regions.
	 */
	MNV,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:1000005">SO:1000005</a> When no simple or
	 * well defined DNA mutation event describes the observed DNA change, the keyword "complex" should be used. Usually
	 * there are multiple equally plausible explanations for the change (is a: substitution).
	 *
	 * Used together with {@link #INTERNAL_FEATURE_ELONGATION} or {@link #FEATURE_TRUNCATION} to describe an variant
	 * that does not lead to a frameshift but a changed transcript length. Used together with
	 * {@link #FRAMESHIFT_ELONGATION} or {@link #FRAMESHIFT_TRUNCATION} if the substitution leads to a frameshift
	 * variant.
	 */
	COMPLEX_SUBSTITUTION,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001587">SO:0001587</a> A sequence variant
	 * whereby at least one base of a codon is changed, resulting in a premature stop codon, leading to a shortened
	 * transcript (is a: nonsynonymous_variant, feature_truncation).
	 */
	STOP_GAINED,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001578">SO:0001578</a> A sequence variant
	 * where at least one base of the terminator codon (stop) is changed, resulting in an elongated transcript (is a:
	 * nonsynonymous variant, terminator_codon_variant, feature_elongation)
	 */
	STOP_LOST,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0002012">SO:0002012</a> A codon variant that
	 * changes at least one base of the canonical start codon (is a: initiator_codon_variant).
	 */
	START_LOST,

	// splicing changes, might change splicing
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001574">SO:0001574</a> A splice variant
	 * that changes the 2 base region at the 3' end of an intron (is a {@link #SPLICE_REGION_VARIANT}).
	 */
	SPLICE_ACCEPTOR_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001575">SO:0001575</a> A splice variant
	 * that changes the 2 base pair region at the 5' end of an intron (is a {@link #SPLICE_REGION_VARIANT}).
	 */
	SPLICE_DONOR_VARIANT,

	// change in rare amino acids, exotic variant
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0002008">SO:0002008</a> A sequence variant
	 * whereby at least one base of a codon encoding a rare amino acid is changed, resulting in a different encoded
	 * amino acid (children: selenocysteine_loss, pyrrolysine_loss).
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	RARE_AMINO_ACID_VARIANT,
	/**
	 * Marker for smallest {@link VariantEffect} with {@link PutativeImpact#HIGH} impact.
	 */
	_SMALLEST_HIGH_IMPACT,

	//
	// MODERATE Putative Impact
	//

	// moderate impact changes in coding region that
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001583">SO:0001583</a> A sequence variant,
	 * that changes one or more bases, resulting in a different amino acid sequence but where the length is preserved.
	 */
	MISSENSE_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001821">SO:0001821</a> An inframe non
	 * synonymous variant that inserts bases into in the coding sequence (is a: inframe_indel,
	 * internal_feature_elongation).
	 */
	INFRAME_INSERTION,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001824">SO:0001824</a> An inframe increase
	 * in cds length that inserts one or more codons into the coding sequence within an existing codon (is a:
	 * {@link #INFRAME_INSERTION}).
	 */
	DISRUPTIVE_INFRAME_INSERTION,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001822">SO:0001822</a> An inframe non
	 * synonymous variant that deletes bases from the coding sequence (is a: inframe_indel, feature_truncation).
	 */
	INFRAME_DELETION,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001826">SO:0001826</a> An inframe decrease
	 * in cds length that deletes bases from the coding sequence starting within an existing codon (is a:
	 * {@link #INFRAME_DELETION}).
	 */
	DISRUPTIVE_INFRAME_DELETION,

	// changes in the UTR
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0002013">SO:0002013</a> A sequence variant
	 * that causes the reduction of a the 5'UTR with regard to the reference sequence (is a:
	 * {@link #FIVE_PRIME_UTR_EXON_VARIANT} or {@link #FIVE_PRIME_UTR_INTRON_VARIANT})
	 *
	 * Jannovar does <b>not</b> yield use this at the moment.
	 */
	FIVE_PRIME_UTR_TRUNCATION,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0002015">SO:0002015</a> A sequence variant
	 * that causes the reduction of a the 3' UTR with regard to the reference sequence (is a:
	 * {@link #FIVE_PRIME_UTR_EXON_VARIANT} or {@link #FIVE_PRIME_UTR_INTRON_VARIANT}).
	 *
	 * Jannovar does <b>not</b> yield use this at the moment.
	 */
	THREE_PRIME_UTR_TRUNCATION,

	// changes in the splicing region
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001630">SO:0001630</a> A sequence variant
	 * in which a change has occurred within the region of the splice site, either within 1-3 bases of the exon or 3-8
	 * bases of the intron (is a: {@link #SPLICING_VARIANT}).
	 */
	SPLICE_REGION_VARIANT,
	/**
	 * Marker for smallest {@link VariantEffect} with {@link PutativeImpact#MODERATE} impact.
	 */
	_SMALLEST_MODERATE_IMPACT,

	//
	// LOW Putative Impact
	//

	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001567">SO:0001567</a> A sequence variant
	 * where at least one base in the terminator codon is changed, but the terminator remains (is a:
	 * {@link #SYNONYMOUS_VARIANT}, terminator_codon_variant).
	 */
	STOP_RETAINED_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001582">SO:0001582</a> A codon variant that
	 * changes at least one base of the first codon of a transcript (is a: {@link #CODING_SEQUENCE_VARIANT}, children:
	 * start_retained_variant, start_lost).
	 */
	INITIATOR_CODON_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001819">SO:0001819</a> A sequence variant
	 * where there is no resulting change to the encoded amino acid (is a: {@link #CODING_SEQUENCE_VARIANT}, children:
	 * start_retained_variant, stop_retained_variant).
	 */
	SYNONYMOUS_VARIANT,

	// changes in coding transcripts, exons/introns
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001969">SO:0001969</a> A sequence variant
	 * that changes non-coding intro sequence in a non-coding transcript (is a: {@link #CODING_TRANSCRIPT_VARIANT},
	 * {@link #INTRON_VARIANT}).
	 */
	CODING_TRANSCRIPT_INTRON_VARIANT,

	// changes in non-coding transcripts, exons/introns
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001792">SO:0001792</a> A sequence variant
	 * that changes non-coding exon sequence in a non-coding transcript (is a: {@link #NON_CODING_TRANSCRIPT_VARIANT},
	 * {@link #EXON_VARIANT}).
	 */
	NON_CODING_TRANSCRIPT_EXON_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001970">SO:0001970</a> A sequence variant
	 * that changes non-coding intro sequence in a non-coding transcript (is a: {@link #NON_CODING_TRANSCRIPT_VARIANT},
	 * {@link #INTRON_VARIANT}).
	 */
	NON_CODING_TRANSCRIPT_INTRON_VARIANT,

	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001983">SO:0001983</a> A 5' UTR variant
	 * where a premature start codon is introduced, moved or lost (is a: {@link #FIVE_PRIME_UTR_EXON_VARIANT} or
	 * {@link #FIVE_PRIME_UTR_INTRON_VARIANT}).
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	// TODO(holtgrem): use
	FIVE_PRIME_UTR_PREMATURE_START_CODON_GAIN_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0002092">SO:0002092</a> A UTR variant of the
	 * 5' UTR (is a: 5_prime_UTR_variant; is a: UTR_variant).
	 */
	FIVE_PRIME_UTR_EXON_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0002089">SO:0002089</a> A UTR variant of the
	 * 3' UTR (is a: 3_prime_UTR_variant; is a: UTR_variant).
	 */
	THREE_PRIME_UTR_EXON_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0002091">SO:0002091</a> A UTR variant
	 * between 5' UTRs (is a: 5_prime_UTR_variant; is a: UTR_variant).
	 */
	FIVE_PRIME_UTR_INTRON_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0002090">SO:0002090</a> A UTR variant
	 * between 3' UTRs (is a: 3_prime_UTR_variant; is a: UTR_variant).
	 */
	THREE_PRIME_UTR_INTRON_VARIANT,

	/**
	 * Marker for smallest {@link VariantEffect} with {@link PutativeImpact#LOW} impact.
	 */
	_SMALLEST_LOW_IMPACT,

	//
	// MODIFIER Putative Impact
	//

	// duplication marker
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:1000039">SO:1000039</a> A tandem duplication
	 * where the individual regions are in the same orientation (is a: tandem_duplication).
	 *
	 * In Jannovar used, as an additional marker to describe that an insertion is a duplication.
	 */
	DIRECT_TANDEM_DUPLICATION,

	// variant in custom region
	/**
	 * Variant in a user-specified custom region.
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	// TODO(holtgrem): use?
	CUSTOM,

	// variants with distances to genes/transcripts
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001631">SO:0001631</a> A sequence variant
	 * located 5' of a gene (is a: {@link #INTERGENIC_VARIANT}).
	 */
	UPSTREAM_GENE_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001632">SO:0001632</a> A sequence variant
	 * located 3' of a gene (is a: {@link #INTERGENIC_VARIANT}).
	 */
	DOWNSTREAM_GENE_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001628">SO:0001628</a> A sequence variant
	 * located in the intergenic region, between genes (is a: feature_variant).
	 */
	INTERGENIC_VARIANT,

	// regulatory / TFBS variants
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001782">SO:0001782</a> A sequence variant
	 * located within a transcription factor binding site (is a: {@link #REGULATORY_REGION_VARIANT}).
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	TF_BINDING_SITE_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001566">SO:0001566</a> A sequence variant
	 * located within a regulatory region (is a: feature_variant).
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	REGULATORY_REGION_VARIANT,

	// variant in intronic regions
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0002018">SO:0002018</a> A transcript variant
	 * occurring within a conserved region of an intron (is a: {@link #INTRON_VARIANT}).
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	CONSERVED_INTRON_VARIANT,

	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0002011">SO:0002011</a> A variant that
	 * occurs within a gene but falls outside of all transcript features. This occurs when alternate transcripts of a
	 * gene do not share overlapping sequence (is a: {@link #TRANSCRIPT_VARIANT} ).
	 */
	// TODO(holtgrem): use?
	INTRAGENIC_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0002017">SO:0002017</a> A sequence variant
	 * located in a conserved intergenic region, between genes (is a: {@link #INTERGENIC_VARIANT}).
	 */
	CONSERVED_INTERGENIC_VARIANT,

	// general variant types
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001537">SO:0001537</a> A sequence variant
	 * that changes one or more sequence features (is a: sequence variant).
	 */
	STRUCTURAL_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001580">SO:0001580</a> A sequence variant
	 * that changes the coding sequence (is a: {@link #CODING_TRANSCRIPT_VARIANT}, {@link #EXON_VARIANT}).
	 *
	 * Sequence Ontology does <b>not</b> have a term
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/CODING_TRANSCRIPT_EXON_VARIANT" >
	 * CODING_TRANSCRIPT_EXON_VARIANT</a>, so we use this.
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	CODING_SEQUENCE_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001627">SO:0001627</a> A transcript variant
	 * occurring within an intron (is a: {@link #TRANSCRIPT_VARIANT}).
	 *
	 * Jannovar uses {@link #CODING_TRANSCRIPT_INTRON_VARIANT} and {@link #NON_CODING_TRANSCRIPT_INTRON_VARIANT}
	 * instead.
	 */
	INTRON_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001791">SO:0001791</a> A sequence variant
	 * that changes exon sequence (is a: {@link #TRANSCRIPT_VARIANT}).
	 */
	EXON_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001568">SO:0001568</a> A sequence variant
	 * that changes the process of splicing (is a: {@link #GENE_VARIANT}).
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	SPLICING_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0000276">SO:0000276</a> Variant affects a
	 * miRNA (is a: miRNA_primary_transcript, small_regulatory_ncRNA).
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	// TODO(holtgrem): use?
	MIRNA,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001564">SO:0001564</a> A sequence variant
	 * where the structure of the gene is changed (is a: feature_variant).
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	GENE_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001968">SO:0001968</a> A transcript variant
	 * of a protein coding gene (is a: {@link #TRANSCRIPT_VARIANT}).
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	CODING_TRANSCRIPT_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001619">SO:0001619</a> (is a:
	 * {@link #TRANSCRIPT_VARIANT}).
	 *
	 * Used for marking splicing variants as non-coding.
	 */
	NON_CODING_TRANSCRIPT_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001576">SO:0001576</a> A sequence variant
	 * that changes the structure of the transcript (is a: {@link #GENE_VARIANT}). TRANSCRIPT_VARIANT, /**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:">SO:</a> (is a: {@link #GENE_VARIANT})).
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	TRANSCRIPT_VARIANT,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0000605">SO:0000605</a> A region containing
	 * or overlapping no genes that is bounded on either side by a gene, or bounded by a gene and the end of the
	 * chromosome (is a: biological_region).
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	INTERGENIC_REGION,
	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0000340">SO:0000340</a> Structural unit
	 * composed of a nucleic acid molecule which controls its own replication through the interaction of specific
	 * proteins at one or more origins of replication (is a: replicon).
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	CHROMOSOME,

	/**
	 * <a href="http://www.sequenceontology.org/browser/current_svn/term/SO:0001060">SO:0001060</a> Top level term for
	 * variants, can be used for marking "uknown effect".
	 *
	 * <b>Not</b> used in Jannovar annotations.
	 */
	SEQUENCE_VARIANT;

	/**
	 * {@link Predicate} for testing whether a {@link VariantEffect} is related to splicing.
	 */
	public static final Predicate<VariantEffect> IS_SPLICING = new Predicate<VariantEffect>() {
		@Override
		public boolean apply(VariantEffect arg) {
			return arg.isSplicing();
		}
	};

	/**
	 * {@link Predicate} for testing whether a {@link VariantEffect} is intronic.
	 */
	public static final Predicate<VariantEffect> IS_INTRONIC = new Predicate<VariantEffect>() {
		@Override
		public boolean apply(VariantEffect arg) {
			return arg.isIntronic();
		}
	};

	/**
	 * {@link Function} for converting from {@link VariantEffect} to SO term String.
	 */
	public static final Function<VariantEffect, String> TO_SO_TERM = new Function<VariantEffect, String>() {
		@Override
		public String apply(VariantEffect arg) {
			return arg.getSequenceOntologyTerm();
		}
	};

	/**
	 * {@link Function} for converting from {@link VariantEffect} to legacy term.
	 */
	public static final Function<VariantEffect, String> TO_LEGACY_NAME = new Function<VariantEffect, String>() {
		@Override
		public String apply(VariantEffect arg) {
			return arg.getLegacyTerm();
		}
	};

	/**
	 * @return <code>true</code> if the effect type denotes a frameshift variant (can only return <code>true</code> only
	 *         small variants, spanning at most one exon, are considered).
	 */
	boolean isFrameshiftVariant() {
		switch (this) {
		case FRAMESHIFT_ELONGATION:
		case FRAMESHIFT_TRUNCATION:
			return true;
		default:
			return false;
		}
	}

	/**
	 * @return Legacy (old Jannovar) id.
	 */
	public String getLegacyTerm() {
		switch (this) {
		case DIRECT_TANDEM_DUPLICATION:
		case DISRUPTIVE_INFRAME_DELETION:
		case FEATURE_TRUNCATION:
		case INFRAME_DELETION:
			return "NON_FS_DELETION";
		case DOWNSTREAM_GENE_VARIANT:
			return "DOWNSTREAM";
		case FIVE_PRIME_UTR_PREMATURE_START_CODON_GAIN_VARIANT:
		case FIVE_PRIME_UTR_TRUNCATION:
		case FIVE_PRIME_UTR_EXON_VARIANT:
			return "UTR5";
		case FRAMESHIFT_ELONGATION:
			return "FS_INSERTION";
		case FRAMESHIFT_TRUNCATION:
			return "FS_DELETION";
		case FRAMESHIFT_VARIANT:
			return "FS_SUBSTITUTION";
		case INITIATOR_CODON_VARIANT:
			return "STARTLOSS";
		case CONSERVED_INTERGENIC_VARIANT:
		case INTERGENIC_VARIANT:
			return "INTERGENIC";
		case INFRAME_INSERTION:
		case DISRUPTIVE_INFRAME_INSERTION:
		case INTERNAL_FEATURE_ELONGATION:
			return "NON_FS_INSERTION";
		case INTRAGENIC_VARIANT:
			return "INTRAGENIC";
		case CONSERVED_INTRON_VARIANT:
		case CODING_TRANSCRIPT_INTRON_VARIANT:
		case INTRON_VARIANT:
		case FIVE_PRIME_UTR_INTRON_VARIANT:
		case THREE_PRIME_UTR_INTRON_VARIANT:
			return "INTRONIC";
		case MNV:
			return "NON_FS_SUBSTITUTION";
		case NON_CODING_TRANSCRIPT_EXON_VARIANT:
			return "ncRNA_EXONIC";
		case NON_CODING_TRANSCRIPT_INTRON_VARIANT:
			return "ncRNA_INTRONIC";
		case MISSENSE_VARIANT:
		case RARE_AMINO_ACID_VARIANT:
			return "MISSENSE";
		case SPLICE_ACCEPTOR_VARIANT:
		case SPLICE_DONOR_VARIANT:
		case SPLICE_REGION_VARIANT:
		case SPLICING_VARIANT:
			return "SPLICING";
		case START_LOST:
			return "STARTLOSS";
		case STOP_GAINED:
			return "STOPGAIN";
		case STOP_LOST:
			return "STOPLOSS";
		case STOP_RETAINED_VARIANT:
		case SYNONYMOUS_VARIANT:
			return "SYNONYMOUS";
		case THREE_PRIME_UTR_TRUNCATION:
		case THREE_PRIME_UTR_EXON_VARIANT:
			return "UTR3";
		case TRANSCRIPT_ABLATION:
			return "TRANSCRIPT_ABLATION";
		case UPSTREAM_GENE_VARIANT:
			return "UPSTREAM";
		case SEQUENCE_VARIANT:
			return "UNKNOWN";
		case GENE_VARIANT:
		case CHROMOSOME:
		case CHROMOSOME_NUMBER_VARIATION:
		case CODING_SEQUENCE_VARIANT:
		case CODING_TRANSCRIPT_VARIANT:
		case COMPLEX_SUBSTITUTION:
		case CUSTOM:
		case EXON_LOSS_VARIANT:
		case EXON_VARIANT:
		case MIRNA:
		case INTERGENIC_REGION:
		case NON_CODING_TRANSCRIPT_VARIANT:
		case REGULATORY_REGION_VARIANT:
		case STRUCTURAL_VARIANT:
		case TF_BINDING_SITE_VARIANT:
		case TRANSCRIPT_VARIANT:
		case _SMALLEST_HIGH_IMPACT:
		case _SMALLEST_LOW_IMPACT:
		case _SMALLEST_MODERATE_IMPACT:
		default:
			return null;
		}
	}

	/**
	 * @return {@link PutativeImpact} of this effect annotation.
	 */
	public PutativeImpact getImpact() {
		if (this.ordinal() <= _SMALLEST_HIGH_IMPACT.ordinal())
			return PutativeImpact.HIGH;
		else if (this.ordinal() <= _SMALLEST_MODERATE_IMPACT.ordinal())
			return PutativeImpact.MODERATE;
		else if (this.ordinal() <= _SMALLEST_LOW_IMPACT.ordinal())
			return PutativeImpact.LOW;
		else
			return PutativeImpact.MODIFIER;
	}

	/**
	 * @return <a href="http://www.sequenceontology.org/browser/current_svn/term/String">String</a> with the Sequence
	 *         Ontology term.
	 */
	public String getSequenceOntologyTerm() {
		switch (this) {
		case CHROMOSOME:
			return "chromosome";
		case CHROMOSOME_NUMBER_VARIATION:
			return "chromosome_number_variation";
		case CODING_SEQUENCE_VARIANT:
			return "coding_sequence_variant";
		case CODING_TRANSCRIPT_INTRON_VARIANT:
			return "coding_transcript_intron_variant";
		case CODING_TRANSCRIPT_VARIANT:
			return "coding_transcript_variant";
		case COMPLEX_SUBSTITUTION:
			return "complex_substitution";
		case CONSERVED_INTERGENIC_VARIANT:
			return "conserved_intergenic_variant";
		case CONSERVED_INTRON_VARIANT:
			return "conserved_intron_variant";
		case CUSTOM:
			return "<custom>";
		case DIRECT_TANDEM_DUPLICATION:
			return "direct_tandem_duplication";
		case DISRUPTIVE_INFRAME_DELETION:
			return "disruptive_inframe_deletion";
		case DISRUPTIVE_INFRAME_INSERTION:
			return "disruptive_inframe_insertion";
		case DOWNSTREAM_GENE_VARIANT:
			return "downstream_gene_variant";
		case EXON_LOSS_VARIANT:
			return "exon_loss_variant";
		case EXON_VARIANT:
			return "exon_variant";
		case FEATURE_TRUNCATION:
			return "feature_truncation";
		case FIVE_PRIME_UTR_PREMATURE_START_CODON_GAIN_VARIANT:
			return "5_prime_UTR_premature_start_codon_gain_variant";
		case FIVE_PRIME_UTR_TRUNCATION:
			return "5_prime_UTR_truncation";
		case FIVE_PRIME_UTR_EXON_VARIANT:
			return "5_prime_UTR_exon_variant";
		case FIVE_PRIME_UTR_INTRON_VARIANT:
			return "5_prime_UTR_intron_variant";
		case FRAMESHIFT_ELONGATION:
			return "frameshift_elongation";
		case FRAMESHIFT_TRUNCATION:
			return "frameshift_truncation";
		case GENE_VARIANT:
			return "gene_variant";
		case INFRAME_DELETION:
			return "inframe_deletion";
		case INFRAME_INSERTION:
			return "inframe_insertion";
		case INITIATOR_CODON_VARIANT:
			return "initiator_codon_variant";
		case INTERGENIC_REGION:
			return "intergenic_region";
		case INTERGENIC_VARIANT:
			return "intergenic_variant";
		case INTERNAL_FEATURE_ELONGATION:
			return "internal_feature_elongation";
		case INTRAGENIC_VARIANT:
			return "intragenic_variant";
		case INTRON_VARIANT:
			return "intron_variant";
		case MIRNA:
			return "miRNA";
		case MISSENSE_VARIANT:
			return "missense_variant";
		case MNV:
			return "mnv";
		case NON_CODING_TRANSCRIPT_EXON_VARIANT:
			return "non_coding_transcript_exon_variant";
		case NON_CODING_TRANSCRIPT_INTRON_VARIANT:
			return "non_coding_transcript_intron_variant";
		case NON_CODING_TRANSCRIPT_VARIANT:
			return "non_coding_transcript_variant";
		case RARE_AMINO_ACID_VARIANT:
			return "rare_amino_acid_variant";
		case REGULATORY_REGION_VARIANT:
			return "regulatory_region_variant";
		case SEQUENCE_VARIANT:
			return "sequence_variant";
		case SPLICE_ACCEPTOR_VARIANT:
			return "splice_acceptor_variant";
		case SPLICE_DONOR_VARIANT:
			return "splice_donor_variant";
		case SPLICE_REGION_VARIANT:
			return "splice_region_variant";
		case SPLICING_VARIANT:
			return "splicing_variant";
		case START_LOST:
			return "start_lost";
		case STOP_GAINED:
			return "stop_gained";
		case STOP_LOST:
			return "stop_lost";
		case STOP_RETAINED_VARIANT:
			return "stop_retained_variant";
		case STRUCTURAL_VARIANT:
			return "structural_variant";
		case SYNONYMOUS_VARIANT:
			return "synonymous_variant";
		case TF_BINDING_SITE_VARIANT:
			return "tf_binding_site_variant";
		case THREE_PRIME_UTR_TRUNCATION:
			return "3_prime_UTR_truncation";
		case THREE_PRIME_UTR_EXON_VARIANT:
			return "3_prime_UTR_exon_variant";
		case THREE_PRIME_UTR_INTRON_VARIANT:
			return "3_prime_UTR_intron_variant";
		case TRANSCRIPT_ABLATION:
			return "transcript_ablation";
		case TRANSCRIPT_VARIANT:
			return "transcript_variant";
		case UPSTREAM_GENE_VARIANT:
			return "upstream_gene_variant";
		case FRAMESHIFT_VARIANT:
			return "frameshift_variant";
		case _SMALLEST_HIGH_IMPACT:
		case _SMALLEST_LOW_IMPACT:
		case _SMALLEST_MODERATE_IMPACT:
		default:
			return null;
		}
	}

	/**
	 * @return <a href="http://www.sequenceontology.org/browser/current_svn/term/String">String</a> with the Sequence
	 *         Ontology ID.
	 */
	public String getSequenceOID() {
		switch (this) {
		case CHROMOSOME:
			return "SO:0000340";
		case CHROMOSOME_NUMBER_VARIATION:
			return "SO:1000182";
		case CODING_SEQUENCE_VARIANT:
			return "SO:0001580";
		case CODING_TRANSCRIPT_INTRON_VARIANT:
			return "SO:0001969";
		case CODING_TRANSCRIPT_VARIANT:
			return "SO:0001968";
		case COMPLEX_SUBSTITUTION:
			return "SO:1000005";
		case CONSERVED_INTERGENIC_VARIANT:
			return "SO:0002017";
		case CONSERVED_INTRON_VARIANT:
			return "SO:0002018";
		case CUSTOM:
			return "<custom>";
		case DIRECT_TANDEM_DUPLICATION:
			return "SO:1000039";
		case DISRUPTIVE_INFRAME_DELETION:
			return "SO:0001826";
		case DISRUPTIVE_INFRAME_INSERTION:
			return "SO:0001824";
		case DOWNSTREAM_GENE_VARIANT:
			return "SO:0001632";
		case EXON_LOSS_VARIANT:
			return "SO:0001572";
		case EXON_VARIANT:
			return "SO:0001791";
		case FEATURE_TRUNCATION:
			return "SO:0001906";
		case FIVE_PRIME_UTR_PREMATURE_START_CODON_GAIN_VARIANT:
			return "SO:0001983";
		case FIVE_PRIME_UTR_TRUNCATION:
			return "SO:0002013";
		case FIVE_PRIME_UTR_EXON_VARIANT:
			return "SO:0002092";
		case FIVE_PRIME_UTR_INTRON_VARIANT:
			return "SO:0002091";
		case FRAMESHIFT_ELONGATION:
			return "SO:0001909";
		case FRAMESHIFT_TRUNCATION:
			return "SO:0001910";
		case GENE_VARIANT:
			return "SO:0001564";
		case INFRAME_DELETION:
			return "SO:0001822";
		case INFRAME_INSERTION:
			return "SO:0001821";
		case INITIATOR_CODON_VARIANT:
			return "SO:0001582";
		case INTERGENIC_REGION:
			return "SO:0000605";
		case INTERGENIC_VARIANT:
			return "SO:0001628";
		case INTERNAL_FEATURE_ELONGATION:
			return "SO:0001908";
		case INTRAGENIC_VARIANT:
			return "SO:0002011";
		case INTRON_VARIANT:
			return "SO:0001627";
		case MIRNA:
			return "SO:0000276";
		case MISSENSE_VARIANT:
			return "SO:0001583";
		case MNV:
			return "SO:0002007";
		case NON_CODING_TRANSCRIPT_EXON_VARIANT:
			return "SO:0001792";
		case NON_CODING_TRANSCRIPT_INTRON_VARIANT:
			return "SO:0001970";
		case NON_CODING_TRANSCRIPT_VARIANT:
			return "SO:0001619";
		case RARE_AMINO_ACID_VARIANT:
			return "SO:0002008";
		case REGULATORY_REGION_VARIANT:
			return "SO:0001566";
		case SEQUENCE_VARIANT:
			return "SO:0001060";
		case SPLICE_ACCEPTOR_VARIANT:
			return "SO:0001574";
		case SPLICE_DONOR_VARIANT:
			return "SO:0001575";
		case SPLICE_REGION_VARIANT:
			return "SO:0001630";
		case SPLICING_VARIANT:
			return "SO:0001568";
		case START_LOST:
			return "SO:0002012";
		case STOP_GAINED:
			return "SO:0001587";
		case STOP_LOST:
			return "SO:0001578";
		case STOP_RETAINED_VARIANT:
			return "SO:0001567";
		case STRUCTURAL_VARIANT:
			return "SO:0001537";
		case SYNONYMOUS_VARIANT:
			return "SO:0001819";
		case TF_BINDING_SITE_VARIANT:
			return "SO:0001782";
		case THREE_PRIME_UTR_TRUNCATION:
			return "SO:0002015";
		case THREE_PRIME_UTR_EXON_VARIANT:
			return "SO:0002089";
		case THREE_PRIME_UTR_INTRON_VARIANT:
			return "SO:0002090";
		case TRANSCRIPT_ABLATION:
			return "SO:0001893";
		case TRANSCRIPT_VARIANT:
			return "SO:0001576";
		case UPSTREAM_GENE_VARIANT:
			return "SO:0001631";
		case FRAMESHIFT_VARIANT:
			return "SO:0001589";
		case _SMALLEST_HIGH_IMPACT:
		case _SMALLEST_LOW_IMPACT:
		case _SMALLEST_MODERATE_IMPACT:
		default:
			return null;
		}
	}

	/**
	 * Forward to <code>ordinal()</code> member function.
	 *
	 * @return <code>int</code> with the number used for sorting values of type {@link VariantEffect}.
	 */
	public int getNumber() {
		return ordinal();
	}

	/**
	 * @return <code>true</code> if this {@link VariantEffect} annotates structural variants.
	 */
	public boolean isStructural() {
		return (this == STRUCTURAL_VARIANT);
	}

	/**
	 * @return <code>true</code> if this {@link VariantEffect} could affect splicing.
	 */
	public boolean isSplicing() {
		switch (this) {
		case SPLICING_VARIANT:
		case SPLICE_ACCEPTOR_VARIANT:
		case SPLICE_DONOR_VARIANT:
		case SPLICE_REGION_VARIANT:
			return true;
		default:
			return false;
		}
	}

	/**
	 * @return <code>true</code> if equal to {@link #CODING_TRANSCRIPT_INTRON_VARIANT} or
	 *         {@link #NON_CODING_TRANSCRIPT_INTRON_VARIANT}.
	 */
	public boolean isIntronic() {
		return (this == CODING_TRANSCRIPT_INTRON_VARIANT || this == NON_CODING_TRANSCRIPT_INTRON_VARIANT);
	}

	/**
	 * Variant of <code>isOffExome()</code> that allows to specify whether UTR and non-consensus intronic splice
	 * variants count as off-exome or not.
	 *
	 * The parameter-less version counts both as on-exome.
	 *
	 * @param isUtrOffExome
	 *            whether or not UTR exons are considered off-exome (start codon gain is always on-exome)
	 * @param isIntronicSpliceNonConsensusOffExome
	 *            whether or not intronic splice (non consensus) is considered off-exome
	 * @return <code>true</code> if the variant effect describes off-exome variant.
	 */
	public boolean isOffExome(boolean isUtrOffExome, boolean isIntronicSpliceNonConsensusOffExome) {
		switch (this) {
		case FIVE_PRIME_UTR_TRUNCATION:
		case THREE_PRIME_UTR_TRUNCATION:
		case FIVE_PRIME_UTR_EXON_VARIANT:
		case THREE_PRIME_UTR_EXON_VARIANT:
			return isUtrOffExome;
		case SPLICE_REGION_VARIANT:
		case SPLICING_VARIANT:
			return isIntronicSpliceNonConsensusOffExome;
		case CODING_SEQUENCE_VARIANT:
		case COMPLEX_SUBSTITUTION:
		case CUSTOM:
		case DIRECT_TANDEM_DUPLICATION:
		case DISRUPTIVE_INFRAME_DELETION:
		case DISRUPTIVE_INFRAME_INSERTION:
		case DOWNSTREAM_GENE_VARIANT:
		case EXON_LOSS_VARIANT:
		case EXON_VARIANT:
		case FEATURE_TRUNCATION:
		case FIVE_PRIME_UTR_PREMATURE_START_CODON_GAIN_VARIANT:
		case FRAMESHIFT_ELONGATION:
		case FRAMESHIFT_TRUNCATION:
		case FRAMESHIFT_VARIANT:
		case INFRAME_DELETION:
		case INFRAME_INSERTION:
		case INITIATOR_CODON_VARIANT:
		case INTERNAL_FEATURE_ELONGATION:
		case MISSENSE_VARIANT:
		case MNV:
		case NON_CODING_TRANSCRIPT_EXON_VARIANT:
		case RARE_AMINO_ACID_VARIANT:
		case SPLICE_ACCEPTOR_VARIANT:
		case SPLICE_DONOR_VARIANT:
		case START_LOST:
		case STOP_GAINED:
		case STOP_LOST:
		case STOP_RETAINED_VARIANT:
		case STRUCTURAL_VARIANT:
		case SYNONYMOUS_VARIANT:
		case TF_BINDING_SITE_VARIANT:
		case TRANSCRIPT_ABLATION:
		case UPSTREAM_GENE_VARIANT:
			return false;
		default:
			return true;
		}
	}

	/**
	 * @return <code>true</code> if the variant effect does not indicate a change affecting the exome, {@link #CUSTOM}
	 *         is considered on-exome, splice variants are on-exome, UTR is off-exome
	 * @see #isOffTranscript
	 */
	public boolean isOffExome() {
		return isOffExome(true, false);
	}

	/**
	 * @return <code>true</code> if the variant effect does not indicate a change affecting a transcript,
	 *         {@link #CUSTOM} is considered on-transcript
	 * @see #isOffExome
	 */
	public boolean isOffTranscript() {
		// This function first calls isOffExome() to check whether the variant effect is off-exome. Then, this function
		// also allows intronic variants
		if (!isOffExome(false, false))
			return false;

		switch (this) {
		case CODING_TRANSCRIPT_INTRON_VARIANT:
		case FIVE_PRIME_UTR_INTRON_VARIANT:
		case THREE_PRIME_UTR_TRUNCATION:
		case THREE_PRIME_UTR_INTRON_VARIANT:
		case NON_CODING_TRANSCRIPT_INTRON_VARIANT:
		case NON_CODING_TRANSCRIPT_VARIANT:
			return false;
		default:
			return true;
		}
	}

}
