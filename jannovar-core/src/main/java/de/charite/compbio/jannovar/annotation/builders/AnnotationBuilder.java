package de.charite.compbio.jannovar.annotation.builders;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.AnnotationLocation;
import de.charite.compbio.jannovar.annotation.AnnotationLocationBuilder;
import de.charite.compbio.jannovar.annotation.AnnotationMessage;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideChange;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinChange;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinMiscChange;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinMiscChangeType;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.GenomeVariantNormalizer;
import de.charite.compbio.jannovar.reference.NucleotidePointLocationBuilder;
import de.charite.compbio.jannovar.reference.ProjectionException;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptProjectionDecorator;
import de.charite.compbio.jannovar.reference.TranscriptSequenceChangeHelper;
import de.charite.compbio.jannovar.reference.TranscriptSequenceDecorator;
import de.charite.compbio.jannovar.reference.TranscriptSequenceOntologyDecorator;

// TODO(holtgrem): Handle case of start gain => ext

/**
 * Base class for the annotation builder helper classes.
 *
 * The helpers subclass this class and and call the superclass constructor in their constructors. This initializes the
 * decorators for {@link #transcript} and initializes protected member such as {@link #locAnno}. The annotation building
 * process is greatly simplified by this.
 *
 * The realizing classes then override {@link #build} and implement their annotation building logic there.
 *
 * At the moment, this has package visibility only since it is not clear yet whether and how client code should extend
 * the builder hierarchy.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
abstract class AnnotationBuilder {

	/** configuration */
	protected final AnnotationBuilderOptions options;

	/** transcript to annotate. */
	protected final TranscriptModel transcript;
	/** genome change to use for annotation */
	protected final GenomeVariant change;

	/** helper for sequence ontology terms */
	protected final TranscriptSequenceOntologyDecorator so;
	/** helper for coordinate transformations */
	protected final TranscriptProjectionDecorator projector;
	/** helper for updating CDS/TX sequence */
	protected final TranscriptSequenceChangeHelper seqChangeHelper;
	/** helper for sequence access */
	protected final TranscriptSequenceDecorator seqDecorator;

	/** location annotation string */
	protected final AnnotationLocation locAnno;
	/** locus of the change, length() == 1 in case of point changes */
	protected NucleotideRange ntChangeRange;
	/** warnings and messages occuring during annotation process */
	protected SortedSet<AnnotationMessage> messages = new TreeSet<AnnotationMessage>();

	/**
	 * Initialize the helper object with the given <code>transcript</code> and <code>change</code>.
	 *
	 * Note that {@link #change} will be initialized with normalized positions (shifted to the left) if possible.
	 *
	 * @param transcript
	 *            the {@link TranscriptModel} to build the annotation for
	 * @param change
	 *            the {@link GenomeVariant} to use for building the annotation
	 * @param options
	 *            the configuration to use for the {@link AnnotationBuilder}
	 */
	AnnotationBuilder(TranscriptModel transcript, GenomeVariant change, AnnotationBuilderOptions options) {
		this.options = options;

		// Project the change to the same strand as transcript, reverse-complementing the REF/ALT strings.
		change = change.withStrand(transcript.getStrand());
		this.transcript = transcript;

		this.so = new TranscriptSequenceOntologyDecorator(transcript);
		this.projector = new TranscriptProjectionDecorator(transcript);
		this.seqChangeHelper = new TranscriptSequenceChangeHelper(transcript);
		this.seqDecorator = new TranscriptSequenceDecorator(transcript);

		// Shift the GenomeChange if lies within precisely one exon.
		if (so.liesInExon(change.getGenomeInterval())) {
			try {
				// normalize amino acid change and add information about this into {@link messages}
				this.change = GenomeVariantNormalizer.normalizeGenomeChange(transcript, change,
						projector.genomeToTranscriptPos(change.getGenomePos()));
				if (!change.equals(this.change))
					messages.add(AnnotationMessage.INFO_REALIGN_3_PRIME);
			} catch (ProjectionException e) {
				throw new Error("Bug: change begin position must be on transcript.");
			}
		} else {
			this.change = change;
		}

		this.locAnno = buildLocAnno(transcript, this.change);
		this.ntChangeRange = buildNTChangeRange(transcript, this.change);
	}

	/**
	 * Build annotation for {@link #transcript} and {@link #change}
	 *
	 * @return {@link Annotation} for the given {@link #transcript} and {@link #change}.
	 */
	public abstract Annotation build();

	/**
	 * @return chromosome/genome-level {@link NucleotideChange}
	 */
	protected NucleotideChange getGenomicNTChange() {
		return new GenomicNucleotideChangeBuilder(change).build();
	}

	/**
	 * @return CDS-level {@link NucleotideChange}
	 */
	protected abstract NucleotideChange getCDSNTChange();

	/**
	 * Build and return annotation for non-coding RNA.
	 *
	 * We can handle these cases quite easily from the location and amino acid change and just have to check the
	 * splicing and intronic/exonic cases.
	 *
	 * @return annotation for ncRNA HGVS annotations
	 */
	protected Annotation buildNonCodingAnnotation() {
		// Handle the upstream/downstream and intergenic case for non-coding transcripts.
		GenomeInterval changeInterval = change.getGenomeInterval();
		if (so.overlapsWithUpstreamRegion(changeInterval) || so.overlapsWithDownstreamRegion(changeInterval))
			return buildUpOrDownstreamAnnotation();
		else if (!changeInterval.overlapsWith(transcript.getTXRegion()))
			return buildIntergenicAnnotation();

		// Project genome to CDS position.
		GenomePosition pos = changeInterval.getGenomeBeginPos();

		ArrayList<VariantEffect> varTypes = new ArrayList<VariantEffect>();
		if (changeInterval.length() == 0) {
			final GenomePosition lPos = pos.shifted(-1);
			// Check for being a splice site variant. The splice donor, acceptor, and region intervals are disjoint.
			if (so.liesInSpliceDonorSite(pos) || so.liesInSpliceDonorSite(lPos))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_DONOR_VARIANT));
			else if (so.liesInSpliceAcceptorSite(lPos) || so.liesInSpliceAcceptorSite(pos))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_ACCEPTOR_VARIANT));
			else if (so.liesInSpliceRegion(pos))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_REGION_VARIANT));
			// Check for being in intron/exon.
			if (so.liesInExon(lPos) && so.liesInExon(pos))
				varTypes.add(VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT);
			else
				varTypes.add(VariantEffect.NON_CODING_TRANSCRIPT_INTRON_VARIANT);
		} else {
			// Check for being a splice site variant. The splice donor, acceptor, and region intervals are disjoint.
			if (so.overlapsWithSpliceDonorSite(changeInterval))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_DONOR_VARIANT));
			else if (so.overlapsWithSpliceAcceptorSite(changeInterval))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_ACCEPTOR_VARIANT));
			else if (so.overlapsWithSpliceRegion(changeInterval))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_REGION_VARIANT));
			// Check for being in intron/exon.
			if (so.overlapsWithExon(changeInterval))
				varTypes.add(VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT);
			else
				varTypes.add(VariantEffect.NON_CODING_TRANSCRIPT_INTRON_VARIANT);
		}
		return new Annotation(transcript, change, varTypes, locAnno, getGenomicNTChange(), getCDSNTChange(), null,
				messages);
	}

	/** @return intronic anotation */
	protected Annotation buildIntronicAnnotation() {
		GenomePosition pos = change.getGenomeInterval().getGenomeBeginPos();

		ArrayList<VariantEffect> varTypes = new ArrayList<VariantEffect>();
		if (transcript.isCoding()) // always include intronic as variant type
			varTypes.add(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT);
		else
			varTypes.add(VariantEffect.NON_CODING_TRANSCRIPT_INTRON_VARIANT);
		if (change.getGenomeInterval().length() == 0) {
			final GenomePosition lPos = pos.shifted(-1);
			// Check for being a splice site variant. The splice donor, acceptor, and region intervals are disjoint.
			if (so.liesInSpliceDonorSite(pos) || so.liesInSpliceDonorSite(lPos))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_DONOR_VARIANT));
			else if (so.liesInSpliceAcceptorSite(lPos) || so.liesInSpliceAcceptorSite(pos))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_ACCEPTOR_VARIANT));
			else if (so.liesInSpliceRegion(pos))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_REGION_VARIANT));
		} else {
			GenomeInterval changeInterval = change.getGenomeInterval();
			// Check for being a splice site variant. The splice donor, acceptor, and region intervals are disjoint.
			if (so.overlapsWithSpliceDonorSite(changeInterval))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_DONOR_VARIANT));
			else if (so.overlapsWithSpliceAcceptorSite(changeInterval))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_ACCEPTOR_VARIANT));
			else if (so.overlapsWithSpliceRegion(changeInterval))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_REGION_VARIANT));
		}
		// intronic variants have no effect on the protein but splice variants lead to "probably no protein produced"
		// annotation, as in Mutalyzer.
		ProteinChange proteinChange = ProteinMiscChange.build(true, ProteinMiscChangeType.NO_CHANGE);
		if (!Sets.intersection(ImmutableSet.copyOf(varTypes), ImmutableSet.of(VariantEffect.SPLICE_DONOR_VARIANT,
				VariantEffect.SPLICE_ACCEPTOR_VARIANT, VariantEffect.SPLICE_REGION_VARIANT)).isEmpty())
			proteinChange = ProteinMiscChange.build(true, ProteinMiscChangeType.DIFFICULT_TO_PREDICT);
		return new Annotation(transcript, change, varTypes, locAnno, getGenomicNTChange(), getCDSNTChange(),
				proteinChange, messages);
	}

	/**
	 * @return 3'/5' UTR anotation
	 */
	protected Annotation buildUTRAnnotation() {
		GenomePosition pos = change.getGenomeInterval().getGenomeBeginPos();

		ArrayList<VariantEffect> varTypes = new ArrayList<VariantEffect>();
		if (change.getGenomeInterval().length() == 0) {
			GenomePosition lPos = pos.shifted(-1);
			// Check for being a splice site variant. The splice donor, acceptor, and region intervals are disjoint.
			if ((so.liesInSpliceDonorSite(lPos) && so.liesInSpliceDonorSite(pos)))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_DONOR_VARIANT));
			else if ((so.liesInSpliceAcceptorSite(lPos) && so.liesInSpliceAcceptorSite(pos)))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_ACCEPTOR_VARIANT));
			else if ((so.liesInSpliceRegion(lPos) && so.liesInSpliceRegion(pos)))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_REGION_VARIANT));
			// Check for being in 5' or 3' UTR.
			if (so.liesInFivePrimeUTR(lPos)) {
				// Check if variant overlaps really with an UTR
				if (so.liesInExon(lPos))
					varTypes.add(VariantEffect.FIVE_PRIME_UTR_EXON_VARIANT);
				else {
					// between two UTRs. check for coding or non-coding transcript.
					if (transcript.isCoding())
						varTypes.add(VariantEffect.FIVE_PRIME_UTR_INTRON_VARIANT);
					else
						varTypes.add(VariantEffect.FIVE_PRIME_UTR_INTRON_VARIANT);
				}
			} else {
				// Check if variant overlaps really with an UTR
				if (so.liesInExon(lPos))
					varTypes.add(VariantEffect.THREE_PRIME_UTR_EXON_VARIANT);
				else {
					// between two UTRs. check for coding or non-coding transcript.
					if (transcript.isCoding())
						varTypes.add(VariantEffect.THREE_PRIME_UTR_INTRON_VARIANT);
					else
						varTypes.add(VariantEffect.THREE_PRIME_UTR_INTRON_VARIANT);
				}
			}
		} else {
			GenomeInterval changeInterval = change.getGenomeInterval();
			// Check for being a splice site variant. The splice donor, acceptor, and region intervals are disjoint.
			if (so.overlapsWithSpliceDonorSite(changeInterval))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_DONOR_VARIANT));
			else if (so.overlapsWithSpliceAcceptorSite(changeInterval))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_ACCEPTOR_VARIANT));
			else if (so.overlapsWithSpliceRegion(changeInterval))
				varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_REGION_VARIANT));
			// Check for being in 5' or 3' UTR.
			if (so.overlapsWithFivePrimeUTR(changeInterval)) {
				// Check if variant overlaps really with an UTR
				if (so.overlapsWithExon(changeInterval))
					varTypes.add(VariantEffect.FIVE_PRIME_UTR_EXON_VARIANT);
				else {
					// between two UTRs. check for coding or non-coding transcript.
					if (transcript.isCoding())
						varTypes.add(VariantEffect.FIVE_PRIME_UTR_INTRON_VARIANT);
					else
						varTypes.add(VariantEffect.FIVE_PRIME_UTR_INTRON_VARIANT);
				}
			} else {
				// Check if variant overlaps really with an UTR
				if (so.overlapsWithExon(changeInterval))
					varTypes.add(VariantEffect.THREE_PRIME_UTR_EXON_VARIANT);
				else {
					// between two UTRs. check for coding or non-coding transcript.
					if (transcript.isCoding())
						varTypes.add(VariantEffect.THREE_PRIME_UTR_INTRON_VARIANT);
					else
						varTypes.add(VariantEffect.THREE_PRIME_UTR_INTRON_VARIANT);
				}
			}
		}
		return new Annotation(transcript, change, varTypes, locAnno, getGenomicNTChange(), getCDSNTChange(),
				ProteinMiscChange.build(true, ProteinMiscChangeType.NO_CHANGE));
	}

	/** @return upstream/downstream annotation */
	protected Annotation buildUpOrDownstreamAnnotation() {
		GenomePosition pos = change.getGenomeInterval().getGenomeBeginPos();

		if (change.getGenomeInterval().length() == 0) {
			// Empty interval, is insertion.
			GenomePosition lPos = pos.shifted(-1);
			if (so.liesInUpstreamRegion(lPos))
				return new Annotation(transcript, change, ImmutableList.of(VariantEffect.UPSTREAM_GENE_VARIANT), null,
						null, null, null, messages);
			else
				// so.liesInDownstreamRegion(pos))
				return new Annotation(transcript, change, ImmutableList.of(VariantEffect.DOWNSTREAM_GENE_VARIANT), null,
						null, null, null, messages);
		} else {
			// Non-empty interval, at least one reference base changed/deleted.
			GenomeInterval changeInterval = change.getGenomeInterval();
			if (so.overlapsWithUpstreamRegion(changeInterval))
				return new Annotation(transcript, change, ImmutableList.of(VariantEffect.UPSTREAM_GENE_VARIANT), null,
						null, null, null, messages);
			else
				// so.overlapsWithDownstreamRegion(changeInterval)
				return new Annotation(transcript, change, ImmutableList.of(VariantEffect.DOWNSTREAM_GENE_VARIANT), null,
						null, null, null, messages);
		}
	}

	/** @return intergenic anotation */
	protected Annotation buildIntergenicAnnotation() {
		return new Annotation(transcript, change, ImmutableList.of(VariantEffect.INTERGENIC_VARIANT), null, null, null,
				null, messages);
	}

	/**
	 * @param transcript
	 *            {@link TranscriptModel} to build annotation for
	 * @param change
	 *            {@link GenomeVariant} to build annotation for
	 * @return AnnotationLocation with location annotation
	 */
	private AnnotationLocation buildLocAnno(TranscriptModel transcript, GenomeVariant change) {
		// System.err.println("ACCESSION\t" + transcript.accession);
		TranscriptSequenceOntologyDecorator soDecorator = new TranscriptSequenceOntologyDecorator(transcript);
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);

		AnnotationLocationBuilder locBuilder = new AnnotationLocationBuilder();
		locBuilder.setTranscript(transcript);
		// System.err.println("CHANGE\t" + change.getGenomeInterval());
		// System.err.println("TX REGION\t" + transcript.txRegion);
		// System.err.println("PROJECTED CHANGE\t" + projector.projectGenomeToTXInterval(change.getGenomeInterval()));
		locBuilder.setTXLocation(projector.projectGenomeToTXInterval(change.getGenomeInterval()));

		if (change.getGenomeInterval().length() == 0) {
			// no base is changed => insertion
			GenomePosition changePos = change.getGenomeInterval().getGenomeBeginPos();
			GenomePosition lPos = changePos.shifted(-1);

			// Handle the cases for which no exon and no intron number is available.
			if (!soDecorator.liesInExon(changePos) && !soDecorator.liesInIntron(changePos))
				return locBuilder.build(); // no exon information if change pos does not lie in exon

			final int exonNum = projector.locateExon(changePos);
			final int lExonNum = projector.locateExon(lPos);
			if (exonNum != TranscriptProjectionDecorator.INVALID_EXON_ID
					|| lExonNum != TranscriptProjectionDecorator.INVALID_EXON_ID) {
				locBuilder.setRankType(AnnotationLocation.RankType.EXON);
				if (exonNum != TranscriptProjectionDecorator.INVALID_EXON_ID)
					locBuilder.setRank(exonNum);
				else
					locBuilder.setRank(lExonNum);
				return locBuilder.build();
			}

			final int intronNum = projector.locateIntron(changePos);
			if (intronNum != TranscriptProjectionDecorator.INVALID_EXON_ID) {
				locBuilder.setRankType(AnnotationLocation.RankType.INTRON);
				locBuilder.setRank(intronNum);
				return locBuilder.build();
			}

			throw new Error("Bug: position should be in exon if we reach here");
		} else {
			// at least one base is changed
			GenomePosition firstChangePos = change.getGenomeInterval().getGenomeBeginPos();
			GenomeInterval firstChangeBase = new GenomeInterval(firstChangePos, 1);
			GenomePosition lastChangePos = change.getGenomeInterval().getGenomeEndPos().shifted(-1);
			GenomeInterval lastChangeBase = new GenomeInterval(lastChangePos, 1);

			// Handle the cases for which no exon and no intron number is available.
			if ((!soDecorator.liesInExon(firstChangeBase) || !soDecorator.liesInExon(lastChangeBase))
					&& (!soDecorator.liesInIntron(firstChangeBase) || !soDecorator.liesInIntron(lastChangeBase)))
				return locBuilder.build(); // no exon/intron information if change pos does not lie in exon
			final int intronNum = projector.locateIntron(firstChangePos);
			if (intronNum != TranscriptProjectionDecorator.INVALID_EXON_ID) {
				locBuilder.setRankType(AnnotationLocation.RankType.INTRON);
				locBuilder.setRank(intronNum);
				return locBuilder.build();
			}
			final int exonNum = projector.locateExon(firstChangePos);
			if (exonNum == TranscriptProjectionDecorator.INVALID_EXON_ID)
				throw new Error("Bug: positions should be in exons if we reach here");
			if (exonNum != projector.locateExon(lastChangePos))
				return locBuilder.build(); // no exon information if the deletion spans more than one

			locBuilder.setRankType(AnnotationLocation.RankType.EXON);
			locBuilder.setRank(exonNum);
			return locBuilder.build();
		}
	}

	/**
	 * @param transcript
	 *            {@link TranscriptModel} to build annotation for
	 * @param change
	 *            {@link GenomeVariant} to build annotation for
	 * @return {@link NucleotideRange} describing the CDS-level position of the change (or transcript-level in the case
	 *         of non-coding transcripts)
	 */
	private NucleotideRange buildNTChangeRange(TranscriptModel transcript, GenomeVariant change) {
		NucleotidePointLocationBuilder posBuilder = new NucleotidePointLocationBuilder(transcript);

		GenomePosition firstChangePos = change.getGenomeInterval().getGenomeBeginPos();
		GenomePosition lastChangePos = change.getGenomeInterval().getGenomeEndPos().shifted(-1);
		if (change.getGenomeInterval().length() == 0)
			// case of zero-base change (insertion)
			return new NucleotideRange(posBuilder.getNucleotidePointLocation(lastChangePos),
					posBuilder.getNucleotidePointLocation(firstChangePos));
		else
			// case of single-base change (SNV) or multi-base change
			return new NucleotideRange(posBuilder.getNucleotidePointLocation(firstChangePos),
					posBuilder.getNucleotidePointLocation(lastChangePos));
	}

}
