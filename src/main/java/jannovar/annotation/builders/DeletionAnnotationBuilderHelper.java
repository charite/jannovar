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
			return new CDSExonicAnnotationBuilder(this).build(); // can affect amino acids
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

	/**
	 * Helper class for generating annotations for exonic CDS variants.
	 *
	 * We use this helper class to simplify the access to the parameters such as {@link #wtCDSSeq} etc.
	 */
	private class CDSExonicAnnotationBuilder {
		final DeletionAnnotationBuilderHelper owner;
		final GenomeInterval changeInterval;

		final Translator t = Translator.getTranslator();

		final String wtCDSSeq;
		final String varCDSSeq;
		final int delFrameShift;

		final String wtAASeq;
		final String varAASeq;
		final int varAAStopPos;

		final CDSPosition changeBeginPos;
		final CDSPosition changeLastPos;

		// We keep the following three variables as state of the algorithm since we do not have easy-to-use triples in
		// Java.

		// the variant type, updated in handleFrameShiftCase() and handleNonFrameShiftCase()
		VariantType varType;
		// the amino acid change, updated in handleFrameShiftCase() and handleNonFrameShiftCase()
		AminoAcidChange aaChange;
		// the protein annotation, updated in handleFrameShiftCase() and handleNonFrameShiftCase()
		String protAnno;

		public CDSExonicAnnotationBuilder(DeletionAnnotationBuilderHelper owner) {
			this.owner = owner;

			this.changeInterval = change.getGenomeInterval();
			this.wtCDSSeq = projector.getTranscriptStartingAtCDS();
			this.varCDSSeq = seqChangeHelper.getCDSWithChange(change);
			this.delFrameShift = owner.change.getRef().length() % 3;

			// TODO(holtgrem): Not translating in the cases we don't need it might save time
			// Translate the variant CDS sequence and look for stop codon.
			this.wtAASeq = t.translateDNA(wtCDSSeq);
			this.varAASeq = t.translateDNA(varCDSSeq);
			this.varAAStopPos = varAASeq.indexOf('*');

			// Get the change begin position as CDS coordinate, handling introns and positions outside of CDS.
			this.changeBeginPos = projector.projectGenomeToCDSPosition(changeInterval.getGenomeBeginPos())
					.withPositionType(PositionType.ZERO_BASED);
			this.changeLastPos = projector.projectGenomeToCDSPosition(
					changeInterval.getGenomeEndPos().shifted(-1)).withPositionType(PositionType.ZERO_BASED);
			// "(...+2)/3" => round up integer division result
			this.aaChange = new AminoAcidChange(changeBeginPos.getPos() / 3, wtAASeq.substring(
					changeBeginPos.getPos() / 3, (changeLastPos.getPos() + 1 + 2) / 3), "");
		}

		public Annotation build() {
			if (delFrameShift == 0)
				handleNonFrameShiftCase();
			else
				handleFrameShiftCase();

			return new Annotation(transcript.transcriptModel, String.format("%s:%s", ncHGVS(), protAnno),
					varType);
		}

		private void handleNonFrameShiftCase() {
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
					// We do not have an deletion between two ORFs but instead within the ORFs. This could lead to an
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
		}

		private void handleFrameShiftCase() {
			// The variant is a frameshift deletion. The deletion could span more than one exon and thus also affect a
			// splice donor or acceptor site. Further, the variant might also be stop lost or splice region variant but
			// that has a lower priority than a frameshift deletion.
			if (so.overlapsWithSpliceAcceptorSite(changeInterval) || so.overlapsWithSpliceDonorSite(changeInterval)
					|| so.overlapsWithSpliceRegion(changeInterval))
				varType = VariantType.SPLICING; // TODO(holtgrem): refine which of the cases we have
			else if (so.overlapsWithTranslationalStopSite(changeInterval))
				varType = VariantType.STOPLOSS;
			else
				varType = VariantType.FS_DELETION;

			// Normalize the amino acid change with the var AA sequence.
			while (aaChange.ref.length() > 0 && aaChange.ref.charAt(0) == varAASeq.charAt(aaChange.pos))
				aaChange = aaChange.shiftRight();

			// Differentiate between the cases where we have a stop codon and those where we don't.
			if (varAAStopPos >= 0) {
				// We have a stop codon, yay!
				char wtAA = wtAASeq.charAt(aaChange.pos);
				char varAA = varAASeq.charAt(aaChange.pos);
				int delta = (wtAA == '*') ? 0 : 1;
				int stopCodonOffset = varAAStopPos - aaChange.pos + delta;
				if (varType == VariantType.STOPLOSS)
					protAnno = String.format("p.*%d%sext*%d", aaChange.pos + 1, t.toLong(varAA), stopCodonOffset);
				else
					protAnno = String.format("p.%s%d%sfs*%d", t.toLong(wtAA), aaChange.pos + 1, t.toLong(varAA),
							stopCodonOffset);
			} else {
				// There is no stop codon any more! Create a "probably no protein is produced".
				protAnno = "p.0?";
				varType = VariantType.STOPLOSS;
			}
		}
	}

}
