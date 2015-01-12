package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.annotation.VariantType;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomeChangeNormalizer;
import jannovar.reference.GenomeInterval;
import jannovar.reference.GenomePosition;
import jannovar.reference.HGVSPositionBuilder;
import jannovar.reference.PositionType;
import jannovar.reference.ProjectionException;
import jannovar.reference.TranscriptInfo;
import jannovar.reference.TranscriptProjectionDecorator;
import jannovar.reference.TranscriptSequenceChangeHelper;
import jannovar.reference.TranscriptSequenceDecorator;
import jannovar.reference.TranscriptSequenceOntologyDecorator;

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
	protected final TranscriptInfo transcript;
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
	protected final String locAnno;
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
	AnnotationBuilder(TranscriptInfo transcript, GenomeChange change) {
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
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		GenomePosition pos = changeInterval.getGenomeBeginPos();
		int txBeginPos = projector.projectGenomeToCDSPosition(pos).pos;

		if (changeInterval.length() == 0) {
			GenomePosition lPos = pos.shifted(-1);
			VariantType varType;
			if ((so.liesInSpliceDonorSite(lPos) && so.liesInSpliceDonorSite(pos)))
				varType = VariantType.ncRNA_SPLICE_DONOR;
			else if ((so.liesInSpliceAcceptorSite(lPos) && so.liesInSpliceAcceptorSite(pos)))
				varType = VariantType.ncRNA_SPLICE_ACCEPTOR;
			else if ((so.liesInSpliceRegion(lPos) && so.liesInSpliceRegion(pos)))
				varType = VariantType.ncRNA_SPLICE_REGION;
			else if (so.liesInExon(lPos) && so.liesInExon(pos))
				varType = VariantType.ncRNA_EXONIC;
			else
				varType = VariantType.ncRNA_INTRONIC;
			return new Annotation(varType, txBeginPos, ncHGVS(), transcript);
		} else {
			VariantType varType;
			if (so.overlapsWithSpliceDonorSite(changeInterval))
				varType = VariantType.ncRNA_SPLICE_DONOR;
			else if (so.overlapsWithSpliceAcceptorSite(changeInterval))
				varType = VariantType.ncRNA_SPLICE_ACCEPTOR;
			else if (so.overlapsWithSpliceRegion(changeInterval))
				varType = VariantType.ncRNA_SPLICE_REGION;
			else if (so.overlapsWithExon(changeInterval))
				varType = VariantType.ncRNA_EXONIC;
			else
				varType = VariantType.ncRNA_INTRONIC;
			return new Annotation(varType, txBeginPos, ncHGVS(), transcript);
		}
	}

	/**
	 * @return intronic anotation, using {@link #ncHGVS} for building the DNA HGVS annotation.
	 */
	protected Annotation buildIntronicAnnotation() {
		// Project genome to CDS position.
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		GenomePosition pos = change.getGenomeInterval().getGenomeBeginPos();
		int txBeginPos = projector.projectGenomeToCDSPosition(pos).pos;

		VariantType varType;
		if (change.getGenomeInterval().length() == 0) {
			GenomePosition lPos = pos.shifted(-1);
			if ((so.liesInSpliceDonorSite(lPos) && so.liesInSpliceDonorSite(pos)))
				varType = VariantType.SPLICE_DONOR;
			else if ((so.liesInSpliceAcceptorSite(lPos) && so.liesInSpliceAcceptorSite(pos)))
				varType = VariantType.SPLICE_ACCEPTOR;
			else if ((so.liesInSpliceRegion(lPos) && so.liesInSpliceRegion(pos)))
				varType = VariantType.SPLICE_REGION;
			else
				varType = VariantType.INTRONIC;
		} else {
			GenomeInterval changeInterval = change.getGenomeInterval();
			if (so.overlapsWithSpliceDonorSite(changeInterval))
				varType = VariantType.SPLICE_DONOR;
			else if (so.overlapsWithSpliceAcceptorSite(changeInterval))
				varType = VariantType.SPLICE_ACCEPTOR;
			else if (so.overlapsWithSpliceRegion(changeInterval))
				varType = VariantType.SPLICE_REGION;
			else
				varType = VariantType.INTRONIC;
		}
		return new Annotation(varType, txBeginPos, ncHGVS(), transcript);
	}

	/**
	 * @return 3'/5' UTR anotation, using {@link #ncHGVS} for building the DNA HGVS annotation.
	 */
	protected Annotation buildUTRAnnotation() {
		// Project genome to CDS position.
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		GenomePosition pos = change.getGenomeInterval().getGenomeBeginPos();
		int txBeginPos = projector.projectGenomeToCDSPosition(pos).pos;

		VariantType varType;
		if (change.getGenomeInterval().length() == 0) {
			GenomePosition lPos = pos.shifted(-1);
			if ((so.liesInSpliceDonorSite(lPos) && so.liesInSpliceDonorSite(pos)))
				varType = VariantType.SPLICE_DONOR;
			else if ((so.liesInSpliceAcceptorSite(lPos) && so.liesInSpliceAcceptorSite(pos)))
				varType = VariantType.SPLICE_ACCEPTOR;
			else if ((so.liesInSpliceRegion(lPos) && so.liesInSpliceRegion(pos)))
				varType = VariantType.SPLICE_REGION;
			else if (so.liesInFivePrimeUTR(lPos))
				varType = VariantType.UTR5;
			else
				// so.liesInThreePrimeUTR(pos)
				varType = VariantType.UTR3;
		} else {
			GenomeInterval changeInterval = change.getGenomeInterval();
			if (so.overlapsWithSpliceDonorSite(changeInterval))
				varType = VariantType.SPLICE_DONOR;
			else if (so.overlapsWithSpliceAcceptorSite(changeInterval))
				varType = VariantType.SPLICE_ACCEPTOR;
			else if (so.overlapsWithSpliceRegion(changeInterval))
				varType = VariantType.SPLICE_REGION;
			else if (so.overlapsWithFivePrimeUTR(change.getGenomeInterval()))
				varType = VariantType.UTR5;
			else
				// so.overlapsWithThreePrimeUTR(change.getGenomeInterval())
				varType = VariantType.UTR3;
		}
		return new Annotation(varType, txBeginPos, ncHGVS(), transcript);
	}

	/**
	 * @return upstream/downstream annotation, using {@link #ncHGVS} for building the DNA HGVS annotation.
	 */
	protected Annotation buildUpOrDownstreamAnnotation() {
		// Project genome to CDS position.
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		GenomePosition pos = change.getGenomeInterval().getGenomeBeginPos();
		int txBeginPos = projector.projectGenomeToCDSPosition(pos).pos;

		String annoString = String.format("dist=%d", distance());
		if (change.getGenomeInterval().length() == 0) {
			// Empty interval, is insertion.
			GenomePosition lPos = pos.shifted(-1);
			if (so.liesInUpstreamRegion(lPos))
				return new Annotation(VariantType.UPSTREAM, txBeginPos, annoString, transcript);
			else
				// so.liesInDownstreamRegion(pos))
				return new Annotation(VariantType.DOWNSTREAM, txBeginPos, annoString, transcript);
		} else {
			// Non-empty interval, at least one reference base changed/deleted.
			GenomeInterval changeInterval = change.getGenomeInterval();
			if (so.overlapsWithUpstreamRegion(changeInterval))
				return new Annotation(VariantType.UPSTREAM, txBeginPos, annoString, transcript);
			else
				// so.overlapsWithDownstreamRegion(changeInterval)
				return new Annotation(VariantType.DOWNSTREAM, txBeginPos, annoString, transcript);
		}
	}

	/**
	 * @return intergenic anotation, using {@link #ncHGVS} for building the DNA HGVS annotation.
	 */
	protected Annotation buildIntergenicAnnotation() {
		return new Annotation(VariantType.INTERGENIC, 0, String.format("dist=%d", distance()), transcript);
	}

	/**
	 * @return base pair distance of transcript and variant
	 */
	private int distance() {
		GenomeInterval changeInterval = change.withStrand('+').getGenomeInterval()
				.withPositionType(PositionType.ZERO_BASED);
		GenomeInterval txInterval = transcript.txRegion.withStrand('+').withPositionType(PositionType.ZERO_BASED);
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
	 * @return String with the HGVS location string
	 */
	private String buildLocAnno(TranscriptInfo transcript, GenomeChange change) {
		TranscriptSequenceOntologyDecorator soDecorator = new TranscriptSequenceOntologyDecorator(transcript);
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);

		final int exonNum;

		if (change.getGenomeInterval().length() == 0) {
			// no base is change => insertion
			GenomePosition changePos = change.getGenomeInterval().withPositionType(PositionType.ZERO_BASED)
					.getGenomeBeginPos();

			// Handle the cases for which no exon number is available.
			if (!soDecorator.liesInExon(changePos))
				return transcript.accession; // no exon information if change pos does not lie in exon
			exonNum = projector.locateExon(changePos);
			if (exonNum == TranscriptProjectionDecorator.INVALID_EXON_ID)
				throw new Error("Bug: position should be in exon if we reach here");
		} else {
			// at least one base is changed
			GenomePosition firstChangePos = change.getGenomeInterval().withPositionType(PositionType.ZERO_BASED)
					.getGenomeBeginPos();
			GenomeInterval firstChangeBase = new GenomeInterval(firstChangePos, 1);
			GenomePosition lastChangePos = change.getGenomeInterval().withPositionType(PositionType.ZERO_BASED)
					.getGenomeEndPos().shifted(-1);
			GenomeInterval lastChangeBase = new GenomeInterval(lastChangePos, 1);

			// Handle the cases for which no exon number is available.
			if (!soDecorator.liesInExon(firstChangeBase) || !soDecorator.liesInExon(lastChangeBase))
				return transcript.accession; // no exon information if either does not lie in exon
			exonNum = projector.locateExon(firstChangePos);
			if (exonNum == TranscriptProjectionDecorator.INVALID_EXON_ID)
				throw new Error("Bug: positions should be in exons if we reach here");
			if (exonNum != projector.locateExon(lastChangePos))
				return transcript.accession; // no exon information if the deletion spans more than one exon
		}

		return String.format("%s:exon%d", transcript.accession, exonNum + 1);
	}

	/**
	 * @param transcript
	 *            {@link TranscriptInfo} to build annotation for
	 * @param change
	 *            {@link GenomeChange} to build annotation for
	 * @return String with the HGVS DNA Annotation string (with coordinates for this transcript).
	 */
	private String buildDNAAnno(TranscriptInfo transcript, GenomeChange change) {
		HGVSPositionBuilder posBuilder = new HGVSPositionBuilder(transcript);

		GenomePosition firstChangePos = change.getGenomeInterval().withPositionType(PositionType.ZERO_BASED)
				.getGenomeBeginPos();
		GenomePosition lastChangePos = change.getGenomeInterval().withPositionType(PositionType.ZERO_BASED)
				.getGenomeEndPos().shifted(-1);
		char prefix = transcript.isCoding() ? 'c' : 'n';
		if (change.getGenomeInterval().length() == 0)
			// case of zero-base change (insertion)
			return String.format("%c.%s_%s", prefix, posBuilder.getCDNAPosStr(lastChangePos),
					posBuilder.getCDNAPosStr(firstChangePos));
		else if (firstChangePos.equals(lastChangePos))
			// case of single-base change (SNV)
			return String.format("%c.%s", prefix, posBuilder.getCDNAPosStr(firstChangePos));
		else
			// case of multi-base change (deletion, block substitution)
			return String.format("%c.%s_%s", prefix, posBuilder.getCDNAPosStr(firstChangePos),
					posBuilder.getCDNAPosStr(lastChangePos));
	}

}
