package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.exception.ProjectionException;
import jannovar.reference.CDSPosition;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomeInterval;
import jannovar.reference.PositionType;
import jannovar.reference.TranscriptInfo;
import jannovar.reference.TranscriptPosition;
import jannovar.reference.TranscriptSequenceDecorator;
import jannovar.util.Translator;

/**
 * Helper class for the {@link SingleNucleotideSubstitutionBuilder}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
class SingleNucleotideSubstitutionBuilderHelper extends AnnotationBuilderHelper {
	/**
	 * Override substitution annotation string in the case of coding change.
	 *
	 * For changes in coding regions, this is necessary since the transcript might not be the same as the reference
	 * (that the VCF file is generated from).
	 */
	private String hgvsSNVOverride = null;

	SingleNucleotideSubstitutionBuilderHelper(TranscriptInfo transcript, GenomeChange change) {
		super(transcript, change);
	}

	@Override
	Annotation build() {
		// Go through top-level cases (clustered by how they are handled here) and build annotations for each of them
		// where applicable.

		final GenomeInterval changeInterval = change.getGenomeInterval();
		if (so.liesInCDSExon(changeInterval) && transcript.cdsRegion.contains(changeInterval))
			return buildCDSExonicAnnotation(); // lies in coding part of exon
		else if (so.overlapsWithCDSIntron(changeInterval) && so.overlapsWithCDS(changeInterval))
			return buildIntronicAnnotation(); // intron but no exon => intronic variant
		else if (so.overlapsWithFivePrimeUTR(changeInterval) || so.overlapsWithThreePrimeUTR(changeInterval))
			return buildUTRAnnotation();
		else if (so.overlapsWithUpstreamRegion(changeInterval) || so.overlapsWithDownstreamRegion(changeInterval))
			return buildUpOrDownstreamAnnotation();
		else
			return buildIntergenicAnnotation();
	}

	private Annotation buildCDSExonicAnnotation() {
		// Get 0-based transcript and CDS positions.
		TranscriptPosition txPos;
		CDSPosition cdsPos;
		try {
			txPos = projector.genomeToTranscriptPos(change.getPos()).withPositionType(PositionType.ZERO_BASED);
			cdsPos = projector.genomeToCDSPos(change.getPos()).withPositionType(PositionType.ZERO_BASED);
		} catch (ProjectionException e) {
			throw new Error("Bug: CDS exon position must be translatable to transcript position");
		}

		// Check that the WT nucleotide from the transcript is consistent with change.ref and generate a warning message
		// if this is not the case.
		String warningMsg = null;
		if (transcript.sequence.charAt(txPos.getPos()) != change.getRef().charAt(0))
			warningMsg = String.format("WARNING:_mRNA/genome_discrepancy:_%c/%s_strand=%c",
					transcript.sequence.charAt(txPos.getPos()), change.getRef().charAt(0), transcript.getStrand());

		// Compute the frame shift and codon start position.
		int frameShift = cdsPos.getPos() % 3;
		// Get the transcript codon. From this, we generate the WT and the variant codon. This is important in the case
		// where the transcript differs from the reference. This inconsistency of the reference and the transcript is
		// not necessarily an error in the data base but can also occur in the case of post-transcriptional changes of
		// the transcript.
		String transcriptCodon = seqDecorator.getCodonAt(txPos, cdsPos);
		String wtCodon = TranscriptSequenceDecorator.codonWithUpdatedBase(transcriptCodon, frameShift, change.getRef()
				.charAt(0));
		String varCodon = TranscriptSequenceDecorator.codonWithUpdatedBase(transcriptCodon, frameShift, change.getAlt()
				.charAt(0));

		// Construct the HGSV annotation parts for the transcript location and nucleotides (note that HGSV uses 1-based
		// positions).
		char wtNT = wtCodon.charAt(frameShift); // wild type nucleotide
		char varNT = varCodon.charAt(frameShift); // wild type amino acid
		hgvsSNVOverride = String.format("%c>%c", wtNT, varNT);

		// Construct annotation part for the protein.
		String wtAA = Translator.getTranslator().translateDNA3(wtCodon);
		String varAA = Translator.getTranslator().translateDNA3(varCodon);
		String protAnno = String.format("p.%s%d%s", wtAA, cdsPos.getPos() / 3 + 1, varAA);
		if (wtAA.equals(varAA)) // simplify in the case of synonymous SNV
			protAnno = String.format("p.=", cdsPos.getPos() / 3 + 1);

		// Compute variant type.
		VariantType varType = computeVariantType(wtAA, varAA);
		GenomeInterval changeInterval = change.getGenomeInterval();
		if (so.overlapsWithTranslationalStartSite(changeInterval)) {
			varType = VariantType.START_LOSS;
			protAnno = "p.0?";
		} else if (so.overlapsWithTranslationalStopSite(changeInterval)) {
			if (wtAA.equals(varAA)) { // change in stop codon, but no AA change
				varType = VariantType.SYNONYMOUS; // TODO(holtgrem): should be STOP_RETAINED
			} else { // change in stop codon, AA change
				varType = VariantType.STOPLOSS;
				String varNTString = seqChangeHelper.getCDSWithChange(change);
				String varAAString = Translator.getTranslator().translateDNA(varNTString);
				int stopCodonPos = varAAString.indexOf('*');
				protAnno = String.format("%sext*%d", protAnno, stopCodonPos - cdsPos.getPos() / 3);
			}
		} else if (so.overlapsWithSpliceAcceptorSite(changeInterval) || so.overlapsWithSpliceDonorSite(changeInterval)
				|| so.overlapsWithSpliceRegion(changeInterval)) {
			varType = VariantType.SPLICING; // TODO(holtgrem): Differentiate between the three cases
		}

		// Build the resulting Annotation.
		// Glue together the annotations and warning message in annotation if any, return Annotation.
		String annotationStr = String.format("%s:%s", ncHGVS(), protAnno);
		if (warningMsg != null)
			annotationStr = String.format("%s:[%s]", annotationStr, warningMsg);
		return new Annotation(transcript.transcriptModel, annotationStr, varType, cdsPos.getPos() + 1);
	}

	@Override
	String ncHGVS() {
		if (hgvsSNVOverride == null)
			return String.format("%s:%s%s>%s", locAnno, dnaAnno, change.getRef(), change.getAlt());
		else
			return String.format("%s:%s%s", locAnno, dnaAnno, hgvsSNVOverride);
	}

	/**
	 * @param wtAA
	 *            wild type amino acid
	 * @param varAA
	 *            variant amino acid
	 * @return variant type described by single amino acid change
	 */
	private VariantType computeVariantType(String wtAA, String varAA) {
		assert (wtAA.length() == 1 && varAA.length() == 1);
		if (wtAA.equals(varAA))
			return VariantType.SYNONYMOUS;
		else if (wtAA.equals("*"))
			return VariantType.STOPLOSS;
		else if (varAA.equals("*"))
			return VariantType.STOPGAIN;
		else
			return VariantType.MISSENSE;
	}

}