package de.charite.compbio.jannovar.annotation.builders;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.AnnotationMessage;
import de.charite.compbio.jannovar.annotation.InvalidGenomeVariant;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideChange;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideSubstitution;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinChange;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinExtension;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinMiscChange;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinMiscChangeType;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinSubstitution;
import de.charite.compbio.jannovar.impl.util.Translator;
import de.charite.compbio.jannovar.reference.CDSPosition;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.InvalidCodonException;
import de.charite.compbio.jannovar.reference.ProjectionException;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptPosition;
import de.charite.compbio.jannovar.reference.TranscriptSequenceDecorator;

/**
 * Builds {@link Annotation} objects for the SNV {@link GenomeVariant}s in the given {@link TranscriptModel}
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class SNVAnnotationBuilder extends AnnotationBuilder {

	/**
	 * Override {@link NucleotideSubstitution} in the case of coding change.
	 *
	 * For changes in coding regions, this is necessary since the transcript might not be the same as the reference
	 * (that the VCF file is generated from).
	 */
	private NucleotideSubstitution ntSubstitutionOverride = null;

	/**
	 * @param transcript
	 *            {@link TranscriptInfo} to build the annotation for
	 * @param change
	 *            {@link GenomeVariant} to build the annotation with
	 * @param options
	 *            the configuration to use for the {@link AnnotationBuilder}
	 * @throws InvalidGenomeVariant
	 *             if <code>change</code> did not describe a deletion
	 */
	SNVAnnotationBuilder(TranscriptModel transcript, GenomeVariant change, AnnotationBuilderOptions options)
			throws InvalidGenomeVariant {
		super(transcript, change, options);

		// guard against invalid genome change
		if (change.getRef().length() != 1 || change.getAlt().length() != 1)
			throw new InvalidGenomeVariant("GenomeChange " + change + " does not describe a SNV.");
	}

	@Override
	public Annotation build() {
		// Go through top-level cases (clustered by how they are handled here) and build annotations for each of them
		// where applicable.

		if (!transcript.isCoding())
			return buildNonCodingAnnotation();

		final GenomeInterval changeInterval = change.getGenomeInterval();
		if (so.liesInCDSExon(changeInterval) && transcript.getCDSRegion().contains(changeInterval))
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
			txPos = projector.genomeToTranscriptPos(change.getGenomePos());
			cdsPos = projector.genomeToCDSPos(change.getGenomePos());
		} catch (ProjectionException e) {
			throw new Error("Bug: CDS exon position must be translatable to transcript position");
		}

		// Check that the WT nucleotide from the transcript is consistent with change.ref and generate a warning message
		// if this is not the case.
		if (txPos.getPos() >= transcript.getSequence().length()
				|| !transcript.getSequence().substring(txPos.getPos(), txPos.getPos() + 1).equals(change.getRef()))
			messages.add(AnnotationMessage.WARNING_REF_DOES_NOT_MATCH_GENOME);

		// Compute the frame shift and codon start position.
		int frameShift = cdsPos.getPos() % 3;
		// Get the transcript codon. From this, we generate the WT and the variant codon. This is important in the case
		// where the transcript differs from the reference. This inconsistency of the reference and the transcript is
		// not necessarily an error in the data base but can also occur in the case of post-transcriptional changes of
		// the transcript.
		String transcriptCodon;
		try {
			transcriptCodon = seqDecorator.getCodonAt(txPos, cdsPos);
		} catch (InvalidCodonException e) {
			// Bail out in the case of invalid codon from sequence
			return new Annotation(transcript, change, new ArrayList<VariantEffect>(), locAnno, getGenomicNTChange(),
					getCDSNTChange(), ProteinMiscChange.build(true, ProteinMiscChangeType.DIFFICULT_TO_PREDICT),
					ImmutableList.of(AnnotationMessage.ERROR_PROBLEM_DURING_ANNOTATION));
		}
		String wtCodon = TranscriptSequenceDecorator.codonWithUpdatedBase(transcriptCodon, frameShift,
				change.getRef().charAt(0));
		String varCodon = TranscriptSequenceDecorator.codonWithUpdatedBase(transcriptCodon, frameShift,
				change.getAlt().charAt(0));

		// Construct the HGSV annotation parts for the transcript location and nucleotides (note that HGSV uses 1-based
		// positions).
		char wtNT = wtCodon.charAt(frameShift); // wild type nucleotide
		char varNT = varCodon.charAt(frameShift); // wild type amino acid
		ntSubstitutionOverride = new NucleotideSubstitution(false, ntChangeRange.getFirstPos(),
				Character.toString(wtNT), Character.toString(varNT));

		// Construct annotation part for the protein.
		String wtAA = Translator.getTranslator().translateDNA(wtCodon);
		String varAA = Translator.getTranslator().translateDNA(varCodon);
		ProteinChange proteinChange = ProteinSubstitution.build(true, wtAA, cdsPos.getPos() / 3, varAA);
		if (wtAA.equals(varAA)) // simplify in the case of synonymous SNV
			proteinChange = ProteinMiscChange.build(true, ProteinMiscChangeType.NO_CHANGE);

		// Compute variant type.
		ArrayList<VariantEffect> varTypes = computeVariantTypes(wtAA, varAA);
		GenomeInterval changeInterval = change.getGenomeInterval();
		if (so.overlapsWithTranslationalStartSite(changeInterval)) {
			varTypes.add(VariantEffect.START_LOST);
			proteinChange = ProteinMiscChange.build(true, ProteinMiscChangeType.NO_PROTEIN);
		} else if (so.overlapsWithTranslationalStopSite(changeInterval)) {
			if (wtAA.equals(varAA)) { // change in stop codon, but no AA change
				varTypes.add(VariantEffect.STOP_RETAINED_VARIANT);
			} else { // change in stop codon, AA change
				varTypes.add(VariantEffect.STOP_LOST);
				String varNTString = seqChangeHelper.getCDSWithGenomeVariant(change);
				String varAAString = Translator.getTranslator().translateDNA(varNTString);
				int stopCodonPos = varAAString.indexOf('*', cdsPos.getPos() / 3);
				int shift = stopCodonPos - cdsPos.getPos() / 3;
				proteinChange = ProteinExtension.build(true, wtAA, cdsPos.getPos() / 3, varAA, shift);
			}
		}
		// Check for being a splice site variant. The splice donor, acceptor, and region intervals are disjoint.
		if (so.overlapsWithSpliceDonorSite(changeInterval))
			varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_DONOR_VARIANT));
		else if (so.overlapsWithSpliceAcceptorSite(changeInterval))
			varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_ACCEPTOR_VARIANT));
		else if (so.overlapsWithSpliceRegion(changeInterval))
			varTypes.addAll(ImmutableList.of(VariantEffect.SPLICE_REGION_VARIANT));

		// Build the resulting Annotation.
		return new Annotation(transcript, change, varTypes, locAnno, getGenomicNTChange(), getCDSNTChange(),
				proteinChange);
	}

	@Override
	protected NucleotideChange getCDSNTChange() {
		if (ntSubstitutionOverride != null)
			return ntSubstitutionOverride;
		else
			return new NucleotideSubstitution(false, ntChangeRange.getFirstPos(), change.getRef(), change.getAlt());
	}

	/**
	 * @param wtAA
	 *            wild type amino acid
	 * @param varAA
	 *            variant amino acid
	 * @return variant types described by single nucleotide change
	 */
	private ArrayList<VariantEffect> computeVariantTypes(String wtAA, String varAA) {
		ArrayList<VariantEffect> result = new ArrayList<VariantEffect>();
		if (wtAA.equals(varAA))
			result.add(VariantEffect.SYNONYMOUS_VARIANT);
		else if (wtAA.equals("*"))
			result.add(VariantEffect.STOP_LOST);
		else if (varAA.equals("*"))
			result.add(VariantEffect.STOP_GAINED);
		else
			result.add(VariantEffect.MISSENSE_VARIANT);
		return result;
	}

}
