package de.charite.compbio.jannovar.annotation.builders;

import java.util.ArrayList;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.AnnotationLocation;
import de.charite.compbio.jannovar.annotation.AnnotationLocationBuilder;
import de.charite.compbio.jannovar.annotation.VariantType;
import de.charite.compbio.jannovar.impl.util.StringUtil;
import de.charite.compbio.jannovar.reference.GenomeChange;
import de.charite.compbio.jannovar.reference.GenomeChangeNormalizer;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.HGVSPositionBuilder;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.ProjectionException;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptProjectionDecorator;
import de.charite.compbio.jannovar.reference.TranscriptSequenceChangeHelper;
import de.charite.compbio.jannovar.reference.TranscriptSequenceDecorator;
import de.charite.compbio.jannovar.reference.TranscriptSequenceOntologyDecorator;

// TODO(holtgrem): We could collect more than one variant type.
// TODO(holtgrem): Handle case of start gain => ext
// TODO(holtgrem): Give intron number for intronic variants?

/**
 * Base class for the annotation builder helper classes.
 *
 * The helpers subclass this class and and call the superclass constructor in their constructors. This initializes the
 * decorators for {@link #transcript} and initializes {@link #locAnno} and {@link #dnaAnno}. The annotation building
 * process is greatly simplified by this.
 *
 * The realizing classes then override {@link #build} and implement their annotation building logic there. Override
 * {@link #ncHGVS} for defining the non-coding HGVS string.
 *
 * At the moment, this has package visibility only since it is not clear yet whether and how client code should extend
 * the builder hierarchy.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
abstract class AnnotationBuilder {

	/** transcript to annotate. */
	protected final TranscriptModel transcript;
	/** genome change to use for annotation */
	protected final GenomeChange change;

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
	/** cDNA/ncDNA annotation string */
	protected String dnaAnno;

	/**
	 * Initialize the helper object with the given <code>transcript</code> and <code>change</code>.
	 *
	 * Note that {@link #change} will be initialized with normalized positions (shifted to the left) if possible.
	 *
	 * @param transcript
	 *            the {@link TranscriptInfo} to build the annotation for
	 * @param change
	 *            the {@link GenomeChange} to use for building the annotation
	 */
	AnnotationBuilder(TranscriptModel transcript, GenomeChange change) {
		// Project the change to the same strand as transcript, reverse-complementing the REF/ALT strings.
		change = change.withPositionType(PositionType.ZERO_BASED).withStrand(transcript.getStrand());
		this.transcript = transcript;

		this.so = new TranscriptSequenceOntologyDecorator(transcript);
		this.projector = new TranscriptProjectionDecorator(transcript);
		this.seqChangeHelper = new TranscriptSequenceChangeHelper(transcript);
		this.seqDecorator = new TranscriptSequenceDecorator(transcript);

		// Shift the GenomeChange if lies within precisely one exon.
		if (so.liesInExon(change.getGenomeInterval())) {
			try {
				this.change = GenomeChangeNormalizer.normalizeGenomeChange(transcript, change,
						projector.genomeToTranscriptPos(change.pos));
			} catch (ProjectionException e) {
				throw new Error("Bug: change begin position must be on transcript.");
			}
		} else {
			this.change = change;
		}

		this.locAnno = buildLocAnno(transcript, this.change);
		this.dnaAnno = buildDNAAnno(transcript, this.change);
	}

	/**
	 * Build annotation for {@link #transcript} and {@link #change}
	 *
	 * @return {@link Annotation} for the given {@link #transcript} and {@link #change}.
	 */
	public abstract Annotation build();

	/**
	 * @return HGVS string for change in non-coding part of transcript.
	 */
	protected abstract String ncHGVS();

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
		else if (!changeInterval.overlapsWith(transcript.txRegion))
			return buildIntergenicAnnotation();

		// Project genome to CDS position.
		GenomePosition pos = changeInterval.getGenomeBeginPos();

		ArrayList<VariantType> varTypes = new ArrayList<VariantType>();
		if (changeInterval.length() == 0) {
			GenomePosition lPos = pos.shifted(-1);
			// Check for being a splice site variant. The splice donor, acceptor, and region intervals are disjoint.
			if ((so.liesInSpliceDonorSite(lPos) && so.liesInSpliceDonorSite(pos)))
				varTypes.add(VariantType.ncRNA_SPLICE_DONOR);
			else if ((so.liesInSpliceAcceptorSite(lPos) && so.liesInSpliceAcceptorSite(pos)))
				varTypes.add(VariantType.ncRNA_SPLICE_ACCEPTOR);
			else if ((so.liesInSpliceRegion(lPos) && so.liesInSpliceRegion(pos)))
				varTypes.add(VariantType.ncRNA_SPLICE_REGION);
			// Check for being in intron/exon.
			if (so.liesInExon(lPos) && so.liesInExon(pos))
				varTypes.add(VariantType.ncRNA_EXONIC);
			else
				varTypes.add(VariantType.ncRNA_INTRONIC);
		} else {
			// Check for being a splice site variant. The splice donor, acceptor, and region intervals are disjoint.
			if (so.overlapsWithSpliceDonorSite(changeInterval))
				varTypes.add(VariantType.ncRNA_SPLICE_DONOR);
			else if (so.overlapsWithSpliceAcceptorSite(changeInterval))
				varTypes.add(VariantType.ncRNA_SPLICE_ACCEPTOR);
			else if (so.overlapsWithSpliceRegion(changeInterval))
				varTypes.add(VariantType.ncRNA_SPLICE_REGION);
			// Check for being in intron/exon.
			if (so.overlapsWithExon(changeInterval))
				varTypes.add(VariantType.ncRNA_EXONIC);
			else
				varTypes.add(VariantType.ncRNA_INTRONIC);
		}
		return new Annotation(transcript, varTypes, locAnno, ncHGVS());
	}

	/**
	 * @return intronic anotation, using {@link #ncHGVS} for building the DNA HGVS annotation.
	 */
	protected Annotation buildIntronicAnnotation() {
		GenomePosition pos = change.getGenomeInterval().getGenomeBeginPos();

		ArrayList<VariantType> varTypes = new ArrayList<VariantType>();
		varTypes.add(VariantType.INTRONIC); // always include intronic as variant type
		if (change.getGenomeInterval().length() == 0) {
			GenomePosition lPos = pos.shifted(-1);
			// Check for being a splice site variant. The splice donor, acceptor, and region intervals are disjoint.
			if ((so.liesInSpliceDonorSite(lPos) && so.liesInSpliceDonorSite(pos)))
				varTypes.add(VariantType.SPLICE_DONOR);
			else if ((so.liesInSpliceAcceptorSite(lPos) && so.liesInSpliceAcceptorSite(pos)))
				varTypes.add(VariantType.SPLICE_ACCEPTOR);
			else if ((so.liesInSpliceRegion(lPos) && so.liesInSpliceRegion(pos)))
				varTypes.add(VariantType.SPLICE_REGION);
		} else {
			GenomeInterval changeInterval = change.getGenomeInterval();
			// Check for being a splice site variant. The splice donor, acceptor, and region intervals are disjoint.
			if (so.overlapsWithSpliceDonorSite(changeInterval))
				varTypes.add(VariantType.SPLICE_DONOR);
			else if (so.overlapsWithSpliceAcceptorSite(changeInterval))
				varTypes.add(VariantType.SPLICE_ACCEPTOR);
			else if (so.overlapsWithSpliceRegion(changeInterval))
				varTypes.add(VariantType.SPLICE_REGION);
		}
		return new Annotation(transcript, varTypes, locAnno, ncHGVS());
	}

	/**
	 * @return 3'/5' UTR anotation, using {@link #ncHGVS} for building the DNA HGVS annotation.
	 */
	protected Annotation buildUTRAnnotation() {
		GenomePosition pos = change.getGenomeInterval().getGenomeBeginPos();

		ArrayList<VariantType> varTypes = new ArrayList<VariantType>();
		if (change.getGenomeInterval().length() == 0) {
			GenomePosition lPos = pos.shifted(-1);
			// Check for being a splice site variant. The splice donor, acceptor, and region intervals are disjoint.
			if ((so.liesInSpliceDonorSite(lPos) && so.liesInSpliceDonorSite(pos)))
				varTypes.add(VariantType.SPLICE_DONOR);
			else if ((so.liesInSpliceAcceptorSite(lPos) && so.liesInSpliceAcceptorSite(pos)))
				varTypes.add(VariantType.SPLICE_ACCEPTOR);
			else if ((so.liesInSpliceRegion(lPos) && so.liesInSpliceRegion(pos)))
				varTypes.add(VariantType.SPLICE_REGION);
			// Check for being in 5' or 3' UTR.
			if (so.liesInFivePrimeUTR(lPos))
				varTypes.add(VariantType.UTR5);
			else
				// so.liesInThreePrimeUTR(pos)
				varTypes.add(VariantType.UTR3);
		} else {
			GenomeInterval changeInterval = change.getGenomeInterval();
			// Check for being a splice site variant. The splice donor, acceptor, and region intervals are disjoint.
			if (so.overlapsWithSpliceDonorSite(changeInterval))
				varTypes.add(VariantType.SPLICE_DONOR);
			else if (so.overlapsWithSpliceAcceptorSite(changeInterval))
				varTypes.add(VariantType.SPLICE_ACCEPTOR);
			else if (so.overlapsWithSpliceRegion(changeInterval))
				varTypes.add(VariantType.SPLICE_REGION);
			// Check for being in 5' or 3' UTR.
			if (so.overlapsWithFivePrimeUTR(change.getGenomeInterval()))
				varTypes.add(VariantType.UTR5);
			else
				// so.overlapsWithThreePrimeUTR(change.getGenomeInterval())
				varTypes.add(VariantType.UTR3);
		}
		return new Annotation(transcript, varTypes, locAnno, ncHGVS());
	}

	/**
	 * @return upstream/downstream annotation, using {@link #ncHGVS} for building the DNA HGVS annotation.
	 */
	protected Annotation buildUpOrDownstreamAnnotation() {
		GenomePosition pos = change.getGenomeInterval().getGenomeBeginPos();

		String annoString = StringUtil.concatenate("dist=", distance());
		if (change.getGenomeInterval().length() == 0) {
			// Empty interval, is insertion.
			GenomePosition lPos = pos.shifted(-1);
			if (so.liesInUpstreamRegion(lPos))
				return new Annotation(transcript, VariantType.UPSTREAM, locAnno, annoString);
			else
				// so.liesInDownstreamRegion(pos))
				return new Annotation(transcript, VariantType.DOWNSTREAM, locAnno, annoString);
		} else {
			// Non-empty interval, at least one reference base changed/deleted.
			GenomeInterval changeInterval = change.getGenomeInterval();
			if (so.overlapsWithUpstreamRegion(changeInterval))
				return new Annotation(transcript, VariantType.UPSTREAM, locAnno, annoString);
			else
				// so.overlapsWithDownstreamRegion(changeInterval)
				return new Annotation(transcript, VariantType.DOWNSTREAM, locAnno, annoString);
		}
	}

	/**
	 * @return intergenic anotation, using {@link #ncHGVS} for building the DNA HGVS annotation.
	 */
	protected Annotation buildIntergenicAnnotation() {
		return new Annotation(transcript, VariantType.INTERGENIC, locAnno, StringUtil.concatenate("dist=", distance()));
	}

	/**
	 * @return base pair distance of transcript and variant
	 */
	private int distance() {
		GenomeInterval changeInterval = change.withStrand('+').getGenomeInterval();
		GenomeInterval txInterval = transcript.txRegion.withStrand('+');
		if (changeInterval.overlapsWith(txInterval))
			return 0;
		else if (changeInterval.isLeftOf(txInterval.getGenomeBeginPos()))
			return txInterval.getGenomeBeginPos().differenceTo(changeInterval.getGenomeEndPos());
		else
			return changeInterval.getGenomeBeginPos().differenceTo(txInterval.getGenomeEndPos());
	}

	/**
	 * @param transcript
	 *            {@link TranscriptInfo} to build annotation for
	 * @param change
	 *            {@link GenomeChange} to build annotation for
	 * @return AnnotationLocation with location annotation
	 */
	private AnnotationLocation buildLocAnno(TranscriptModel transcript, GenomeChange change) {
		TranscriptSequenceOntologyDecorator soDecorator = new TranscriptSequenceOntologyDecorator(transcript);
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);

		AnnotationLocationBuilder locBuilder = new AnnotationLocationBuilder();
		locBuilder.setTranscript(transcript);
		locBuilder.setTxLocation(projector.projectGenomeToTXInterval(change.getGenomeInterval()));

		if (change.getGenomeInterval().length() == 0) {
			// no base is change => insertion
			GenomePosition changePos = change.getGenomeInterval().getGenomeBeginPos();

			// Handle the cases for which no exon number is available.
			if (!soDecorator.liesInExon(changePos))
				return locBuilder.build(); // no exon information if change pos does not lie in exon
			final int exonNum = projector.locateExon(changePos);
			if (exonNum == TranscriptProjectionDecorator.INVALID_EXON_ID)
				throw new Error("Bug: position should be in exon if we reach here");

			locBuilder.setRankType(AnnotationLocation.RankType.EXON);
			locBuilder.setRank(exonNum);
		} else {
			// at least one base is changed
			GenomePosition firstChangePos = change.getGenomeInterval().getGenomeBeginPos();
			GenomeInterval firstChangeBase = new GenomeInterval(firstChangePos, 1);
			GenomePosition lastChangePos = change.getGenomeInterval().getGenomeEndPos().shifted(-1);
			GenomeInterval lastChangeBase = new GenomeInterval(lastChangePos, 1);

			// Handle the cases for which no exon number is available.
			if (!soDecorator.liesInExon(firstChangeBase) || !soDecorator.liesInExon(lastChangeBase))
				return locBuilder.build(); // no exon information if change pos does not lie in exon
			final int exonNum = projector.locateExon(firstChangePos);
			if (exonNum == TranscriptProjectionDecorator.INVALID_EXON_ID)
				throw new Error("Bug: positions should be in exons if we reach here");
			if (exonNum != projector.locateExon(lastChangePos))
				return locBuilder.build(); // no exon information if the deletion spans more than one

			locBuilder.setRankType(AnnotationLocation.RankType.EXON);
			locBuilder.setRank(exonNum);
		}

		return locBuilder.build();
	}

	/**
	 * @param transcript
	 *            {@link TranscriptInfo} to build annotation for
	 * @param change
	 *            {@link GenomeChange} to build annotation for
	 * @return String with the HGVS DNA Annotation string (with coordinates for this transcript).
	 */
	private String buildDNAAnno(TranscriptModel transcript, GenomeChange change) {
		HGVSPositionBuilder posBuilder = new HGVSPositionBuilder(transcript);

		GenomePosition firstChangePos = change.getGenomeInterval().getGenomeBeginPos();
		GenomePosition lastChangePos = change.getGenomeInterval().getGenomeEndPos().shifted(-1);
		char prefix = transcript.isCoding() ? 'c' : 'n';
		if (change.getGenomeInterval().length() == 0)
			// case of zero-base change (insertion)
			return StringUtil.concatenate(prefix, ".", posBuilder.getCDNAPosStr(lastChangePos), "_",
					posBuilder.getCDNAPosStr(firstChangePos));
		else if (firstChangePos.equals(lastChangePos))
			// case of single-base change (SNV)
			return StringUtil.concatenate(prefix, ".", posBuilder.getCDNAPosStr(firstChangePos));
		else
			// case of multi-base change (deletion, block substitution)
			return StringUtil.concatenate(prefix, ".", posBuilder.getCDNAPosStr(firstChangePos), "_",
					posBuilder.getCDNAPosStr(lastChangePos));
	}

}
