package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.annotation.VariantType;
import jannovar.exception.InvalidGenomeChange;
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
 * Builds {@link Annotation} objects for the SNV {@link GenomeChange}s in the given {@link TranscriptInfo}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class SNVAnnotationBuilder extends AnnotationBuilder {

	/**
	 * Override substitution annotation string in the case of coding change.
	 *
	 * For changes in coding regions, this is necessary since the transcript might not be the same as the reference
	 * (that the VCF file is generated from).
	 */
	private String hgvsSNVOverride = null;

	/**
	 * @param transcript
	 *            {@link TranscriptInfo} to build the annotation for
	 * @param change
	 *            {@link GenomeChange} to build the annotation with
	 * @throws InvalidGenomeChange
	 *             if <code>change</code> did not describe a deletion
	 */
	SNVAnnotationBuilder(TranscriptInfo transcript, GenomeChange change) throws InvalidGenomeChange {
		super(transcript, change);

		// guard against invalid genome change
		if (change.ref.length() != 1 || change.alt.length() != 1)
			throw new InvalidGenomeChange("GenomeChange " + change + " does not describe a SNV.");
	}

	@Override
	public Annotation build() {
		// Go through top-level cases (clustered by how they are handled here) and build annotations for each of them
		// where applicable.

		if (!transcript.isCoding())
			return buildNonCodingAnnotation();

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
			txPos = projector.genomeToTranscriptPos(change.pos).withPositionType(PositionType.ZERO_BASED);
			cdsPos = projector.genomeToCDSPos(change.pos).withPositionType(PositionType.ZERO_BASED);
		} catch (ProjectionException e) {
			throw new Error("Bug: CDS exon position must be translatable to transcript position");
		}

		// Check that the WT nucleotide from the transcript is consistent with change.ref and generate a warning message
		// if this is not the case.
		String warningMsg = null;
		if (transcript.sequence.charAt(txPos.pos) != change.ref.charAt(0))
			warningMsg = String.format("WARNING:_mRNA/genome_discrepancy:_%c/%s_strand=%c",
					transcript.sequence.charAt(txPos.pos), change.ref.charAt(0), transcript.getStrand());

		// Compute the frame shift and codon start position.
		int frameShift = cdsPos.pos % 3;
		// Get the transcript codon. From this, we generate the WT and the variant codon. This is important in the case
		// where the transcript differs from the reference. This inconsistency of the reference and the transcript is
		// not necessarily an error in the data base but can also occur in the case of post-transcriptional changes of
		// the transcript.
		String transcriptCodon = seqDecorator.getCodonAt(txPos, cdsPos);
		String wtCodon = TranscriptSequenceDecorator.codonWithUpdatedBase(transcriptCodon, frameShift,
				change.ref.charAt(0));
		String varCodon = TranscriptSequenceDecorator.codonWithUpdatedBase(transcriptCodon, frameShift,
				change.alt.charAt(0));

		// Construct the HGSV annotation parts for the transcript location and nucleotides (note that HGSV uses 1-based
		// positions).
		char wtNT = wtCodon.charAt(frameShift); // wild type nucleotide
		char varNT = varCodon.charAt(frameShift); // wild type amino acid
		hgvsSNVOverride = String.format("%c>%c", wtNT, varNT);

		// Construct annotation part for the protein.
		String wtAA = Translator.getTranslator().translateDNA3(wtCodon);
		String varAA = Translator.getTranslator().translateDNA3(varCodon);
		String protAnno = String.format("p.%s%d%s", wtAA, cdsPos.pos / 3 + 1, varAA);
		if (wtAA.equals(varAA)) // simplify in the case of synonymous SNV
			protAnno = String.format("p.=", cdsPos.pos / 3 + 1);

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
				int stopCodonPos = varAAString.indexOf('*', cdsPos.pos / 3);
				protAnno = String.format("%sext*%d", protAnno, stopCodonPos - cdsPos.pos / 3);
			}
		} else if (so.overlapsWithSpliceDonorSite(changeInterval)) {
			varType = VariantType.SPLICE_DONOR;
		} else if (so.overlapsWithSpliceAcceptorSite(changeInterval)) {
			varType = VariantType.SPLICE_ACCEPTOR;
		} else if (so.overlapsWithSpliceRegion(changeInterval)) {
			varType = VariantType.SPLICE_REGION;
		}

		// Build the resulting Annotation.
		// Glue together the annotations and warning message in annotation if any, return Annotation.
		String annotationStr = String.format("%s:%s", ncHGVS(), protAnno);
		if (warningMsg != null)
			annotationStr = String.format("%s:[%s]", annotationStr, warningMsg);
		return new Annotation(transcript, annotationStr, varType, cdsPos.pos + 1);
	}

	@Override
	protected String ncHGVS() {
		if (hgvsSNVOverride == null)
			return String.format("%s:%s%s>%s", locAnno, dnaAnno, change.ref, change.alt);
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
