package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.exception.InvalidGenomeChange;
import jannovar.reference.AminoAcidChange;
import jannovar.reference.AminoAcidChangeNormalizer;
import jannovar.reference.CDSPosition;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomeInterval;
import jannovar.reference.PositionType;
import jannovar.reference.TranscriptInfo;
import jannovar.util.Translator;

// TODO(holtgrem): The block substitution protein annotation generation needs some love in the corner cases.

/**
 * Builds {@link Annotation} objects for the block substitution {@link GenomeChange} in the given {@link TranscriptInfo}
 * .
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class BlockSubstitutionAnnotationBuilder extends AnnotationBuilder {

	/**
	 * @param transcript
	 *            {@link TranscriptInfo} to build the annotation for
	 * @param change
	 *            {@link GenomeChange} to build the annotation with
	 * @throws InvalidGenomeChange
	 *             if <code>change</code> did not describe a block substitution
	 */
	public BlockSubstitutionAnnotationBuilder(TranscriptInfo transcript, GenomeChange change)
			throws InvalidGenomeChange {
		super(transcript, change);

		// Guard against invalid genome change.
		if (change.ref.length() == 0 || change.alt.length() == 0)
			throw new InvalidGenomeChange("GenomeChange " + change + " does not describe a block substitution.");
	}

	@Override
	Annotation build() {
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
	String ncHGVS() {
		return String.format("%s:%sdelins%s", locAnno, dnaAnno, change.alt);
	}

	private Annotation buildFeatureAblationAnnotation() {
		return new Annotation(transcript, ncHGVS(), VariantType.TRANSCRIPT_ABLATION);
	}

	private Annotation buildStartLossAnnotation() {
		return new Annotation(transcript, String.format("%s:p.0?", ncHGVS()), VariantType.START_LOSS);
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

		final CDSPosition refChangeBeginPos;
		final CDSPosition refChangeLastPos;
		final CDSPosition varChangeBeginPos;
		final CDSPosition varChangeLastPos;

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
			this.delFrameShift = (varCDSSeq.length() - wtCDSSeq.length()) % 3;

			// TODO(holtgrem): Not translating in the cases we don't need it might save time
			// Translate the variant CDS sequence.
			this.wtAASeq = t.translateDNA(wtCDSSeq);
			this.varAASeq = t.translateDNA(varCDSSeq);

			// Get the reference change begin position as CDS coordinate, handling introns and positions outside of CDS.
			this.refChangeBeginPos = projector.projectGenomeToCDSPosition(changeInterval.getGenomeBeginPos())
					.withPositionType(PositionType.ZERO_BASED);
			CDSPosition refChangeLastPos = projector.projectGenomeToCDSPosition(
					changeInterval.getGenomeEndPos().shifted(-1)).withPositionType(PositionType.ZERO_BASED);
			if (!transcript.cdsRegion.contains(changeInterval.getGenomeEndPos().shifted(-1)))
				refChangeLastPos = refChangeLastPos.shifted(-1); // shift if projected to end position
			this.refChangeLastPos = refChangeLastPos;
			// Get the variant change begin position as CDS coordinate, handling introns and positions outside of CDS.
			this.varChangeBeginPos = projector.projectGenomeToCDSPosition(changeInterval.getGenomeBeginPos())
					.withPositionType(PositionType.ZERO_BASED);
			CDSPosition varChangeLastPos = projector.projectGenomeToCDSPosition(
					changeInterval.getGenomeBeginPos().shifted(change.alt.length() - 1)).withPositionType(
					PositionType.ZERO_BASED);
			if (!transcript.cdsRegion.contains(changeInterval.getGenomeEndPos().shifted(-1)))
				varChangeLastPos = varChangeLastPos.shifted(-1); // shift if projected to end position
			this.varChangeLastPos = varChangeLastPos;
			// "(...+2)/3" => round up integer division result
			this.aaChange = new AminoAcidChange(refChangeBeginPos.pos / 3, wtAASeq.substring(refChangeBeginPos.pos / 3,
					(refChangeLastPos.pos + 1 + 2) / 3), varAASeq.substring(varChangeBeginPos.pos / 3,
					(varChangeLastPos.pos + 1 + 2) / 3));

			// Look for stop codon, starting at change position.
			this.varAAStopPos = varAASeq.indexOf('*', refChangeBeginPos.pos / 3);
		}

		public Annotation build() {
			if (delFrameShift == 0)
				handleNonFrameShiftCase();
			else
				handleFrameShiftCase();

			return new Annotation(transcript, String.format("%s:%s", ncHGVS(), protAnno), varType);
		}

		private void handleNonFrameShiftCase() {
			// The variant is a non-frameshift block substitution. The substitution could span more than one exon and
			// thus also affect a splice donor or acceptor site, delete a stop codon. Further, the variant might also be
			// a splice region variant but that a lower priority than a non-frameshift deletion.
			if (so.overlapsWithSpliceDonorSite(changeInterval))
				varType = VariantType.SPLICE_DONOR;
			else if (so.overlapsWithSpliceAcceptorSite(changeInterval))
				varType = VariantType.SPLICE_ACCEPTOR;
			else if (so.overlapsWithSpliceRegion(changeInterval))
				varType = VariantType.SPLICE_REGION;
			else if (so.overlapsWithTranslationalStopSite(changeInterval))
				varType = VariantType.STOPLOSS;
			else
				varType = VariantType.NON_FS_SUBSTITUTION;

			if (refChangeBeginPos.getFrameshift() == 0) {
				// TODO(holtgrem): Implement shifting for substitutions for AA string
			}

			// Normalize the amino acid change with the var AA sequence.
			while (aaChange.ref.length() > 0 && aaChange.alt.length() > 0
					&& aaChange.ref.charAt(0) == aaChange.alt.charAt(0))
				aaChange = aaChange.shiftRight();
			// Truncate change.alt after stop codon.
			aaChange = AminoAcidChangeNormalizer.truncateAltAfterStopCodon(aaChange);
			if (aaChange.alt.equals("*"))
				varType = VariantType.STOPGAIN;

			char wtAAFirst = wtAASeq.charAt(aaChange.pos);
			char wtAALast = wtAASeq.charAt(aaChange.getLastPos());
			String insertedAAs = varAASeq.substring(aaChange.pos, aaChange.pos + aaChange.alt.length());
			String wtAAFirstLong = (wtAAFirst == '*') ? "*" : t.toLong(wtAAFirst);
			String wtAALastLong = (wtAALast == '*') ? "*" : t.toLong(wtAALast);

			// We differentiate the case of replacing a single amino acid and replacing multiple ones. Note that
			// when the result starts with the stop codon (the alt truncation step reduces it to the case of
			// "any>*") then we handle it as replacing the first amino acid by the stop codon.
			if (aaChange.pos == aaChange.getLastPos() || aaChange.alt.equals("*"))
				protAnno = String.format("p.%s%d%s", wtAAFirstLong, aaChange.pos + 1, t.toLong(insertedAAs.charAt(0)));
			else
				protAnno = String.format("p.%s%d_%s%ddelins%s", wtAAFirstLong, aaChange.pos + 1, wtAALastLong,
						aaChange.getLastPos() + 1, t.toLong(insertedAAs));

			// In the case of stop loss, we have to add the "ext" suffix to the protein annotation.
			if (so.overlapsWithTranslationalStopSite(changeInterval)) {
				// Differentiate between the variant AA string containing a stop codon or not.
				if (varAAStopPos >= 0)
					protAnno = String.format("%sext*%d", protAnno, varAAStopPos - aaChange.pos + 1);
				else
					protAnno = String.format("%sext*?", protAnno);
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
				varType = VariantType.FS_SUBSTITUTION;

			// Differentiate between the cases where we have a stop codon and those where we don't.
			if (varAAStopPos >= 0) {
				// We have a stop codon, yay!
				char wtAA = wtAASeq.charAt(aaChange.pos);
				char varAA = varAASeq.charAt(aaChange.pos);
				int stopCodonOffset = varAAStopPos - aaChange.pos + 1;
				String wtAALong = (wtAA == '*') ? "*" : t.toLong(wtAA);
				String varAALong = (varAA == '*') ? "*" : t.toLong(varAA);
				String opDesc = "fs"; // operation description
				if (aaChange.ref.indexOf('*') >= 0)
					opDesc = "ext"; // stop codon deleted
				protAnno = String.format("p.%s%d%s%s*%d", wtAALong, aaChange.pos + 1, varAALong, opDesc,
						stopCodonOffset);
			} else {
				// There is no stop codon any more! Create a "probably no protein is produced".
				protAnno = "p.0?";
				varType = VariantType.STOPLOSS;
			}
		}
	}
}
