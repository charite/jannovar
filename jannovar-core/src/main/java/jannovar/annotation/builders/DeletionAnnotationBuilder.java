package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.annotation.InvalidGenomeChange;
import jannovar.annotation.VariantType;
import jannovar.impl.util.StringUtil;
import jannovar.impl.util.Translator;
import jannovar.reference.AminoAcidChange;
import jannovar.reference.AminoAcidChangeNormalizer;
import jannovar.reference.CDSPosition;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomeInterval;
import jannovar.reference.GenomePosition;
import jannovar.reference.PositionType;
import jannovar.reference.TranscriptModel;
import jannovar.reference.TranscriptProjectionDecorator;

/**
 * Builds {@link Annotation} objects for the deletion {@link GenomeChange}s in the given {@link TranscriptInfo}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class DeletionAnnotationBuilder extends AnnotationBuilder {

	/**
	 * @param transcript
	 *            {@link TranscriptInfo} to build the annotation for
	 * @param change
	 *            {@link GenomeChange} to build the annotation with
	 * @throws InvalidGenomeChange
	 *             if <code>change</code> did not describe a deletion
	 */
	DeletionAnnotationBuilder(TranscriptModel transcript, GenomeChange change) throws InvalidGenomeChange {
		super(transcript, change);

		// Guard against invalid genome change.
		if (change.ref.length() == 0 || change.alt.length() != 0)
			throw new InvalidGenomeChange("GenomeChange " + change + " does not describe a deletion.");
	}

	@Override
	public Annotation build() {
		// Go through top-level cases (clustered by how they are handled here) and build annotations for each of them
		// where applicable.

		if (!transcript.isCoding())
			return buildNonCodingAnnotation();

		final GenomeInterval changeInterval = change.getGenomeInterval();
		if (so.containsExon(changeInterval)) // deletion of whole exon
			return buildFeatureAblationAnnotation();
		else if (so.overlapsWithTranslationalStartSite(changeInterval))
			return buildStartLossAnnotation();
		else if (so.overlapsWithCDSExon(changeInterval) && so.overlapsWithCDS(changeInterval))
			return new CDSExonicAnnotationBuilder().build(); // can affect amino acids
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
	protected String ncHGVS() {
		return StringUtil.concatenate(locAnno, ":", dnaAnno, "del");
	}

	private Annotation buildFeatureAblationAnnotation() {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		GenomePosition pos = change.getGenomeInterval().getGenomeBeginPos();
		int txBeginPos = projector.projectGenomeToCDSPosition(pos).pos;

		return new Annotation(VariantType.TRANSCRIPT_ABLATION, txBeginPos, ncHGVS(), transcript);
	}

	private Annotation buildStartLossAnnotation() {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		GenomePosition pos = change.getGenomeInterval().getGenomeBeginPos();
		int txBeginPos = projector.projectGenomeToCDSPosition(pos).pos;

		return new Annotation(VariantType.START_LOSS, txBeginPos, StringUtil.concatenate(ncHGVS(), ":p.0?"), transcript);
	}

	/**
	 * Helper class for generating annotations for exonic CDS variants.
	 *
	 * We use this helper class to simplify the access to the parameters such as {@link #wtCDSSeq} etc.
	 */
	private class CDSExonicAnnotationBuilder {
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

		public CDSExonicAnnotationBuilder() {
			this.changeInterval = change.getGenomeInterval();
			this.wtCDSSeq = projector.getTranscriptStartingAtCDS();
			this.varCDSSeq = seqChangeHelper.getCDSWithChange(change);
			this.delFrameShift = DeletionAnnotationBuilder.this.change.ref.length() % 3;

			// Get the change begin position as CDS coordinate, handling introns and positions outside of CDS.
			this.changeBeginPos = projector.projectGenomeToCDSPosition(changeInterval.getGenomeBeginPos())
					.withPositionType(PositionType.ZERO_BASED);
			this.changeLastPos = projector.projectGenomeToCDSPosition(changeInterval.getGenomeEndPos().shifted(-1))
					.withPositionType(PositionType.ZERO_BASED);

			// TODO(holtgrem): Not translating in the cases we don't need it might save time
			// Translate the variant CDS sequence and look for stop codon.
			this.wtAASeq = t.translateDNA(wtCDSSeq);
			this.varAASeq = t.translateDNA(varCDSSeq);
			this.varAAStopPos = varAASeq.indexOf('*', this.changeBeginPos.pos / 3);

			// protect against going behind transcript
			// "(...+2)/3" => round up integer division result
			final int wtAAEndPos = Math.min((changeLastPos.pos + 1 + 2) / 3, wtAASeq.length());
			final String delAA = wtAASeq.substring(changeBeginPos.pos / 3, wtAAEndPos);
			final int delta = (changeBeginPos.getFrameshift() == 0 ? 0 : 1);
			// protect against going behind transcript
			final int varAAEndPos = Math.min(changeBeginPos.pos / 3 + delta, varAASeq.length());
			final String insAA = varAASeq.substring(changeBeginPos.pos / 3, varAAEndPos);
			this.aaChange = new AminoAcidChange(changeBeginPos.pos / 3, delAA, insAA);
			this.aaChange = AminoAcidChangeNormalizer.truncateBothSides(this.aaChange);
			this.aaChange = AminoAcidChangeNormalizer.normalizeDeletion(wtAASeq, this.aaChange);
		}

		public Annotation build() {
			if (delFrameShift == 0)
				handleNonFrameShiftCase();
			else
				handleFrameShiftCase();

			TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
			GenomePosition pos = change.getGenomeInterval().getGenomeBeginPos();
			int txBeginPos = projector.projectGenomeToCDSPosition(pos).pos;

			return new Annotation(varType, txBeginPos, StringUtil.concatenate(ncHGVS(), ":", protAnno), transcript);
		}

		private void handleNonFrameShiftCase() {
			// The variant is a non-frameshift deletion. The deletion could span more than one exon and thus also affect
			// a splice donor or acceptor site, delete a stop codon. Further, the variant might also be a splice region
			// variant but that a lower priority than a non-frameshift deletion.
			if (so.overlapsWithSpliceDonorSite(changeInterval))
				varType = VariantType.SPLICE_DONOR;
			else if (so.overlapsWithSpliceAcceptorSite(changeInterval))
				varType = VariantType.SPLICE_ACCEPTOR;
			else if (so.overlapsWithSpliceRegion(changeInterval))
				varType = VariantType.SPLICE_REGION;
			else if (so.overlapsWithTranslationalStopSite(changeInterval))
				varType = VariantType.STOPLOSS;
			else
				varType = VariantType.NON_FS_DELETION;

			// Differentiate between the cases where we have a stop codon and those where we don't.
			if (varAAStopPos >= 0) {
				String suffix = ""; // for "ins${aminoAcidCode}" in case of "delins" on AA level
				if (aaChange.alt.length() > 0)
					suffix = StringUtil.concatenate("ins", t.toLong(aaChange.alt));

				char wtAAFirst = wtAASeq.charAt(aaChange.pos);
				char wtAALast = wtAASeq.charAt(aaChange.getLastPos());
				if (aaChange.pos == aaChange.getLastPos())
					protAnno = StringUtil.concatenate("p.", t.toLong(wtAAFirst), aaChange.pos + 1, "del", suffix);
				else
					protAnno = StringUtil.concatenate("p.", t.toLong(wtAAFirst), aaChange.pos + 1, "_",
							t.toLong(wtAALast), aaChange.getLastPos() + 1, "del", suffix);
			} else {
				// There is no stop codon any more! Create a "probably no protein is produced".
				protAnno = "p.0?";
			}
		}

		private void handleFrameShiftCase() {
			// The variant is a frameshift deletion. The deletion could span more than one exon and thus also affect a
			// splice donor or acceptor site. Further, the variant might also be stop lost or splice region variant but
			// that has a lower priority than a frameshift deletion.
			if (so.overlapsWithSpliceDonorSite(changeInterval))
				varType = VariantType.SPLICE_DONOR;
			else if (so.overlapsWithSpliceAcceptorSite(changeInterval))
				varType = VariantType.SPLICE_ACCEPTOR;
			else if (so.overlapsWithSpliceRegion(changeInterval))
				varType = VariantType.SPLICE_REGION;
			else if (so.overlapsWithTranslationalStopSite(changeInterval))
				varType = VariantType.STOPLOSS;
			else
				varType = VariantType.FS_DELETION;

			// Normalize the amino acid change, shifting to the right as long as change ref char equals var ref char.
			while (aaChange.ref.length() > 0 && aaChange.pos < varAASeq.length()
					&& aaChange.ref.charAt(0) == varAASeq.charAt(aaChange.pos))
				aaChange = aaChange.shiftRight();

			// Handle the case of deleting a stop codon at the very last entry of the translated amino acid string and
			// short-circuit.
			if (varType == VariantType.STOPLOSS && aaChange.pos == varAASeq.length()) {
				protAnno = StringUtil.concatenate("p.*", aaChange.pos + 1, "del?");
				return;
			}
			// Handle the case of deleting up to the end of the sequence.
			if (aaChange.pos >= varAASeq.length()) {
				if (aaChange.ref.length() == 1)
					protAnno = StringUtil.concatenate("p.", t.toLong(wtAASeq.charAt(aaChange.pos)), aaChange.pos + 1,
							"del");
				else
					protAnno = StringUtil.concatenate("p.", t.toLong(wtAASeq.charAt(aaChange.pos)), aaChange.pos + 1,
							"_", t.toLong(wtAASeq.charAt(aaChange.getLastPos())), aaChange.getLastPos() + 1, "del");
				return;
			}

			char wtAA = wtAASeq.charAt(aaChange.pos);
			char varAA = varAASeq.charAt(aaChange.pos);
			int delta = (wtAA == '*') ? 0 : 1;

			// Compute suffix for HGVS protein annotation.
			String suffix = "*?"; // "?" or "*${distance}" in case of extension/frameshift
			if (varAAStopPos >= 0) {
				int stopCodonOffset = varAAStopPos - aaChange.pos + delta;
				suffix = StringUtil.concatenate("*", stopCodonOffset);
			}
			if (varType == VariantType.STOPLOSS)
				protAnno = StringUtil.concatenate("p.*", aaChange.pos + 1, t.toLong(varAA), "ext", suffix);
			else
				protAnno = StringUtil
						.concatenate("p.", t.toLong(wtAA), aaChange.pos + 1, t.toLong(varAA), "fs", suffix);
		}
	}

}
