package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.exception.ProjectionException;
import jannovar.reference.AminoAcidChange;
import jannovar.reference.CDSPosition;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomeInterval;
import jannovar.reference.GenomePosition;
import jannovar.reference.HGVSPositionBuilder;
import jannovar.reference.PositionType;
import jannovar.reference.TranscriptInfo;
import jannovar.reference.TranscriptProjectionDecorator;
import jannovar.reference.TranscriptSequenceChangeHelper;
import jannovar.reference.TranscriptSequenceOntologyDecorator;
import jannovar.util.Translator;

// TODO(holtgrem): We could collect more than one variant type.

/**
 * This class provides static methods to generate annotations for insertions in exons.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Marten Jäger <marten.jaeger@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
class DeletionAnnotationBuilderHelper {
	/** transcript to annotate. */
	private final TranscriptInfo transcript;
	/** genome change to use for annotation */
	private final GenomeChange change;
	/** helper for sequence ontology terms */
	private final TranscriptSequenceOntologyDecorator so;
	/** helper for coordinate transformations */
	private final TranscriptProjectionDecorator projector;
	/** helper for updating CDS/TX sequence */
	private final TranscriptSequenceChangeHelper seqChangeHelper;

	/** location annotation string */
	private final String locAnno;
	/** cDNA/ncDNA annotation string */
	private final String dnaAnno;

	/**
	 * Initialize the helper object with the given <code>transcript</code> and <code>change</code>.
	 *
	 * Note that {@link #change} will be initialized with normalized positions (shifted to the left) if possible.
	 *
	 * @param transcript
	 * @param change
	 */
	DeletionAnnotationBuilderHelper(TranscriptInfo transcript, GenomeChange change) {
		this.transcript = transcript;

		this.so = new TranscriptSequenceOntologyDecorator(transcript);
		this.projector = new TranscriptProjectionDecorator(transcript);
		this.seqChangeHelper = new TranscriptSequenceChangeHelper(transcript);

		// Shift the GenomeChange if lies within precisely one exon.
		if (so.liesInExon(change.getGenomeInterval())) {
			try {
				this.change = GenomeChangeNormalizer.normalizeDeletion(transcript, change,
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
	Annotation build() {
		// Go through top-level cases (clustered by how they are handled here) and build annotations for each of them
		// where applicable.
		//
		// We forward to many small private functions to make the structure very clear here. Alternatively, we could
		// also compress things further by having getVariantType(), getPastedAnnotationString() etc. but that would make
		// the different cases less clear.

		final GenomeInterval changeInterval = change.getGenomeInterval();
		if (so.containsExon(changeInterval)) // deletion of whole exon
			return buildFeatureAblationAnnotation();
		else if (so.overlapsWithTranslationalStartSite(changeInterval))
			return buildStartLossAnnotation();
		else if (so.overlapsWithCDSExon(changeInterval) && so.overlapsWithCDS(changeInterval))
			return buildCDSExonicAnnotation(); // can affect amino acids
		else if (so.overlapsWithCDSIntron(changeInterval) && so.overlapsWithCDS(changeInterval))
			return buildIntronicAnnotation(); // intron but no exon => intronic variant
		else if (so.overlapsWithFivePrimeUTR(changeInterval) || so.overlapsWithThreePrimeUTR(changeInterval))
			return buildUTRAnnotation();
		else if (so.overlapsWithUpstreamRegion(changeInterval) || so.overlapsWithDownstreamRegion(changeInterval))
			return buildUpOrDownstreamAnnotation();
		else
			return buildIntergenicAnnotation();
	}

	private Annotation buildFeatureAblationAnnotation() {
		return new Annotation(transcript.transcriptModel, String.format("%s:%sdel", locAnno, dnaAnno),
				VariantType.TRANSCRIPT_ABLATION);
	}

	private Annotation buildStartLossAnnotation() {
		return new Annotation(transcript.transcriptModel, String.format("%s:%sdel:p.?", locAnno, dnaAnno),
				VariantType.START_LOSS);
	}

	private Annotation buildCDSExonicAnnotation() {
		String protAnno = null;
		VariantType varType = null;

		final GenomeInterval changeInterval = change.getGenomeInterval();
		final Translator t = Translator.getTranslator();
		final String wtCDSSeq = projector.getTranscriptStartingAtCDS();
		final String varCDSSeq = seqChangeHelper.getCDSWithChange(change);
		final int delFrameShift = (varCDSSeq.length() - wtCDSSeq.length()) % 3;

		// TODO(holtgrem): Not translating in the cases we don't need it might save time
		// Translate the variant CDS sequence and look for stop codon.
		String wtAASeq = t.translateDNA(wtCDSSeq);
		String varAASeq = t.translateDNA(varCDSSeq);
		int varAAStopPos = varAASeq.indexOf('*');

		// Get the change begin position as CDS coordinate, handling introns and positions outside of CDS.
		CDSPosition changeBeginPos = projector.projectGenomeToCDSPosition(changeInterval.getGenomeBeginPos())
				.withPositionType(PositionType.ZERO_BASED);
		CDSPosition changeLastPos = projector.projectGenomeToCDSPosition(changeInterval.getGenomeEndPos().shifted(-1))
				.withPositionType(PositionType.ZERO_BASED);
		AminoAcidChange aaChange = new AminoAcidChange(changeBeginPos.getPos() / 3, wtAASeq.substring(
				changeBeginPos.getPos() / 3, (changeLastPos.getPos() + 1 + 2) / 3), ""); // "(...+2)/3" => round up div

		if (delFrameShift == 0) {
			// The variant is a non-frameshift deletion. The deletion could span more than one exon and thus also affect
			// a splice donor or acceptor site, delete a stop codon. Further, the variant might also be a splice region
			// variant but that a lower priority than a non-frameshift deletion.
			if (so.overlapsWithSpliceAcceptorSite(changeInterval) || so.overlapsWithSpliceDonorSite(changeInterval)
					|| so.overlapsWithSpliceRegion(changeInterval))
				varType = VariantType.SPLICING; // TODO(holtgrem): refine which of both cases we have
			else if (so.overlapsWithTranslationalStopSite(changeInterval))
				varType = VariantType.STOPLOSS;
			else
				varType = VariantType.NON_FS_DELETION;

			// Differentiate between the cases where we have a stop codon and those where we don't.
			if (varAAStopPos >= 0) {
				String suffix = ""; // for "ins${aminoAcidCode}" in case of "delins" on AA level
				if (changeBeginPos.getFrameshift() == 0) {
					// Is clean deletion on the amino acid level. There might be ambiguities, thus we have to shift.
					aaChange = AminoAcidChangeNormalizer.normalizeDeletion(wtAASeq, aaChange);
				} else {
					// We do not have an insertion between to ORFs but instead within one ORF. This could lead to an
					// "delins" case or not (if the AA change is truncated by one from left or right).
					String insCandidate = varAASeq.substring(aaChange.pos, aaChange.pos + 1);
					if (aaChange.ref.startsWith(insCandidate))
						aaChange = AminoAcidChangeNormalizer.normalizeDeletion(wtAASeq, aaChange.shiftRight());
					else if (aaChange.ref.endsWith(insCandidate))
						aaChange = AminoAcidChangeNormalizer.normalizeDeletion(wtAASeq, aaChange.shiftLeft());
					else
						suffix = String.format("ins%s", t.toLong(varAASeq.charAt(aaChange.pos)));
				}

				char wtAAFirst = wtAASeq.charAt(aaChange.pos);
				char wtAALast = wtAASeq.charAt(aaChange.getLastPos());
				if (aaChange.pos == aaChange.getLastPos())
					protAnno = String.format("p.%s%ddel%s", t.toLong(wtAAFirst), aaChange.pos + 1, suffix);
				else
					protAnno = String.format("p.%s%d_%s%ddel%s", t.toLong(wtAAFirst), aaChange.pos + 1,
							t.toLong(wtAALast), aaChange.getLastPos() + 1, suffix);
			} else {
				// There is no stop codon any more! Create a "probably no protein is produced".
				protAnno = "p.0?";
			}
		} else {
			// The variant is a frameshift deletion. The deletion could span more than one exon and thus also affect a
			// splice donor or acceptor site. Further, the variant might also be stop lost or splice region variant but
			// that has a lower priority than a frameshift deletion.
			if (so.overlapsWithSpliceAcceptorSite(changeInterval) || so.overlapsWithSpliceDonorSite(changeInterval))
				varType = VariantType.SPLICING; // TODO(holtgrem): refine which of both cases we have
			else
				varType = VariantType.FS_DELETION;

			// Differentiate between the cases where we have a stop codon and those where we don't.
			if (varAAStopPos >= 0) {
				// We have a stop codon, yay!
				char wtAA = wtAASeq.charAt(aaChange.pos);
				char varAA = varAASeq.charAt(aaChange.pos);
				int stopCodonOffset = varAAStopPos - aaChange.pos + 1;
				protAnno = String.format("p.%s%d%sfs*%d", t.toLong(wtAA), aaChange.pos + 1, t.toLong(varAA),
						stopCodonOffset);
			} else {
				// There is no stop codon any more! Create a "probably no protein is produced".
				protAnno = "p.0?";
				varType = VariantType.STOPLOSS;
			}
		}

		return new Annotation(transcript.transcriptModel, String.format("%s:%sdel:%s", locAnno, dnaAnno, protAnno),
				varType);
	}

	private Annotation buildIntronicAnnotation() {
		// TODO(holtgrem): Differentiate case of splice donor/acceptor/region variants
		GenomeInterval changeInterval = change.getGenomeInterval();
		if (so.overlapsWithSpliceDonorSite(changeInterval) || so.overlapsWithSpliceAcceptorSite(changeInterval)
				|| so.overlapsWithSpliceRegion(changeInterval))
			return new Annotation(transcript.transcriptModel, String.format("%s:%sdel", locAnno, dnaAnno),
					VariantType.SPLICING);
		else
			return new Annotation(transcript.transcriptModel, String.format("%s:%sdel", locAnno, dnaAnno),
					VariantType.INTRONIC);
	}

	private Annotation buildUTRAnnotation() {
		if (so.overlapsWithFivePrimeUTR(change.getGenomeInterval()))
			return new Annotation(transcript.transcriptModel, String.format("%s:%sdel", locAnno, dnaAnno),
					VariantType.UTR5);
		else
			// so.overlapsWithThreePrimeUTR(change.getGenomeInterval())
			return new Annotation(transcript.transcriptModel, String.format("%s:%sdel", locAnno, dnaAnno),
					VariantType.UTR3);
	}

	private Annotation buildUpOrDownstreamAnnotation() {
		if (so.overlapsWithUpstreamRegion(change.getGenomeInterval()))
			return new Annotation(transcript.transcriptModel, String.format("%s:%sdel", locAnno, dnaAnno),
					VariantType.UPSTREAM);
		else
			// so.overlapsWithDownstreamRegion(changeInterval)
			return new Annotation(transcript.transcriptModel, String.format("%s:%sdel", locAnno, dnaAnno),
					VariantType.DOWNSTREAM);
	}

	private Annotation buildIntergenicAnnotation() {
		return new Annotation(transcript.transcriptModel, String.format("%s:%sdel", locAnno, dnaAnno),
				VariantType.INTERGENIC);
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
		if (firstChangePos.equals(lastChangePos))
			return String.format("%c.%s", prefix, posBuilder.getCDNAPosStr(firstChangePos));
		else
			return String.format("%c.%s_%s", prefix, posBuilder.getCDNAPosStr(firstChangePos),
					posBuilder.getCDNAPosStr(lastChangePos));
	}
}
