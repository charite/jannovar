package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.exception.ProjectionException;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomeInterval;
import jannovar.reference.GenomePosition;
import jannovar.reference.HGVSPositionBuilder;
import jannovar.reference.PositionType;
import jannovar.reference.TranscriptInfo;
import jannovar.reference.TranscriptProjectionDecorator;
import jannovar.reference.TranscriptSequenceChangeHelper;
import jannovar.reference.TranscriptSequenceDecorator;
import jannovar.reference.TranscriptSequenceOntologyDecorator;

// TODO(holtgrem): We could collect more than one variant type.

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
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
abstract class AnnotationBuilderHelper {
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
	 * @param change
	 */
	AnnotationBuilderHelper(TranscriptInfo transcript, GenomeChange change) {
		this.transcript = transcript;

		this.so = new TranscriptSequenceOntologyDecorator(transcript);
		this.projector = new TranscriptProjectionDecorator(transcript);
		this.seqChangeHelper = new TranscriptSequenceChangeHelper(transcript);
		this.seqDecorator = new TranscriptSequenceDecorator(transcript);

		// Shift the GenomeChange if lies within precisely one exon.
		if (so.liesInExon(change.getGenomeInterval())) {
			try {
				this.change = GenomeChangeNormalizer.normalizeGenomeChange(transcript, change,
						projector.genomeToTranscriptPos(change.getPos()));
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
	abstract Annotation build();

	/**
	 * @return HGVS string for change in non-coding part of transcript.
	 */
	abstract String ncHGVS();

	/**
	 * @return intronic anotation, using {@link #ncHGVS} for building the DNA HGVS annotation.
	 */
	protected Annotation buildIntronicAnnotation() {
		// TODO(holtgrem): Differentiate case of splice donor/acceptor/region variants
		GenomeInterval changeInterval = change.getGenomeInterval();
		if (so.overlapsWithSpliceDonorSite(changeInterval) || so.overlapsWithSpliceAcceptorSite(changeInterval)
				|| so.overlapsWithSpliceRegion(changeInterval))
			return new Annotation(transcript.transcriptModel, ncHGVS(), VariantType.SPLICING);
		else
			return new Annotation(transcript.transcriptModel, ncHGVS(), VariantType.INTRONIC);
	}

	/**
	 * @return 3'/5' UTR anotation, using {@link #ncHGVS} for building the DNA HGVS annotation.
	 */
	protected Annotation buildUTRAnnotation() {
		// TODO(holtgrem): differentiate splicing cases further!
		if (so.overlapsWithSpliceAcceptorSite(change.getGenomeInterval())
				|| so.overlapsWithSpliceDonorSite(change.getGenomeInterval())
				|| so.overlapsWithSpliceRegion(change.getGenomeInterval()))
			return new Annotation(transcript.transcriptModel, ncHGVS(), VariantType.SPLICING);
		else if (so.overlapsWithFivePrimeUTR(change.getGenomeInterval()))
			return new Annotation(transcript.transcriptModel, ncHGVS(), VariantType.UTR5);
		else
			// so.overlapsWithThreePrimeUTR(change.getGenomeInterval())
			return new Annotation(transcript.transcriptModel, ncHGVS(), VariantType.UTR3);
	}

	/**
	 * @return upstream/downstream anotation, using {@link #ncHGVS} for building the DNA HGVS annotation.
	 */
	protected Annotation buildUpOrDownstreamAnnotation() {
		if (so.overlapsWithUpstreamRegion(change.getGenomeInterval()))
			return new Annotation(transcript.transcriptModel, ncHGVS(), VariantType.UPSTREAM);
		else
			// so.overlapsWithDownstreamRegion(changeInterval)
			return new Annotation(transcript.transcriptModel, ncHGVS(), VariantType.DOWNSTREAM);
	}

	/**
	 * @return intergenic anotation, using {@link #ncHGVS} for building the DNA HGVS annotation.
	 */
	protected Annotation buildIntergenicAnnotation() {
		return new Annotation(transcript.transcriptModel, ncHGVS(), VariantType.INTERGENIC);
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

		GenomePosition firstChangePos = change.getGenomeInterval().withPositionType(PositionType.ZERO_BASED)
				.getGenomeBeginPos();
		GenomePosition lastChangePos = change.getGenomeInterval().withPositionType(PositionType.ZERO_BASED)
				.getGenomeEndPos().shifted(-1);

		// Handle the cases for which no exon number is available.
		if (!soDecorator.liesInExon(firstChangePos) || !soDecorator.liesInExon(lastChangePos))
			return transcript.accession; // no exon information if either does not lie in exon
		int exonNum;
		try {
			exonNum = projector.locateExon(firstChangePos);
			if (exonNum != projector.locateExon(lastChangePos))
				return transcript.accession; // no exon information if the deletion spans more than one exon
		} catch (ProjectionException e) {
			throw new Error("Bug: positions should be in CDS if we reach here");
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
