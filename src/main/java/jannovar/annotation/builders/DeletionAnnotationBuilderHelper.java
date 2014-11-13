package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.reference.AminoAcidChange;
import jannovar.reference.CDSPosition;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomeInterval;
import jannovar.reference.PositionType;
import jannovar.reference.TranscriptInfo;
import jannovar.util.Translator;

/**
 * Helper class for the {@link DeletionAnnotationBuilder}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
class DeletionAnnotationBuilderHelper extends AnnotationBuilderHelper {

	DeletionAnnotationBuilderHelper(TranscriptInfo transcript, GenomeChange change) {
		super(transcript, change);
	}

	@Override
	Annotation build() {
		// Go through top-level cases (clustered by how they are handled here) and build annotations for each of them
		// where applicable.

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

	@Override
	String ncHGVS() {
		return String.format("%s:%sdel", locAnno, dnaAnno);
	}

	private Annotation buildFeatureAblationAnnotation() {
		return new Annotation(transcript.transcriptModel, ncHGVS(), VariantType.TRANSCRIPT_ABLATION);
	}

	private Annotation buildStartLossAnnotation() {
		return new Annotation(transcript.transcriptModel, String.format("%s:p.0?", ncHGVS()), VariantType.START_LOSS);
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

}
