package de.charite.compbio.jannovar.hgvs.bridge;

import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideInversion;
import de.charite.compbio.jannovar.htsjdk.GenomeRegionSequenceExtractor;
import de.charite.compbio.jannovar.impl.util.DNAUtils;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Implementation of converting nucleotide inversions to genome variants.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
class NucleotideInversionToGenomeVariantTranslationImpl extends NucleotideChangeToGenomeVariantTranslationImplBase {

	public NucleotideInversionToGenomeVariantTranslationImpl(GenomeRegionSequenceExtractor seqExtractor) {
		super(seqExtractor);
	}

	/**
	 * Implementation of translation for {@link NucleotideInversion} objects
	 *
	 * @param tm
	 *            {@link TranscriptModel} that <code>ntSub</code> is for
	 * @param sequenceType
	 *            {@link SequenceType} that <code>ntSub</code> is for
	 * @param ntInv
	 *            {@link NucleotideInversion} to convert
	 * @return {@link GenomeVariant} with the translation result, possibly annotated with warning messages
	 * @throws CannotTranslateHGVSVariant
	 *             in case of translation problems
	 */
	public ResultWithWarnings<GenomeVariant> run(TranscriptModel tm, SequenceType sequenceType,
			NucleotideInversion ntInv) throws CannotTranslateHGVSVariant {
		final NucleotideRange range = ntInv.getRange();
		final NucleotideSeqDescription invertedNTDesc = ntInv.getSeq();
		final GenomeInterval gItv = posConverter.translateNucleotideRange(tm, range, sequenceType);

		// obtain deleted sequence, setting inconsistency warnings into warningMsg, if any
		String warningMsg = null;
		String invertedNTs = invertedNTDesc.getNucleotides();
		if (invertedNTs == null) {
			invertedNTs = getGenomeSeq(tm.getStrand(), gItv);
			if (invertedNTDesc.length() != NucleotideSeqDescription.INVALID_NT_COUNT
					&& invertedNTDesc.length() != invertedNTs.length())
				warningMsg = "Invalid nucleotide count in " + ntInv.toHGVSString() + ", expected "
						+ invertedNTs.length();
		} else {
			final String refSeq = getGenomeSeq(tm.getStrand(), gItv);
			if (!refSeq.equals(invertedNTs))
				warningMsg = "Invalid nucleotides in " + ntInv.toHGVSString() + ", expected " + refSeq;
			invertedNTs = refSeq;
		}

		final GenomeVariant result = new GenomeVariant(gItv.withStrand(tm.getStrand()).getGenomeBeginPos(),
				invertedNTs, DNAUtils.reverseComplement(invertedNTs), tm.getStrand()).withStrand(Strand.FWD);
		if (warningMsg != null)
			return ResultWithWarnings.construct(result, warningMsg);
		else
			return ResultWithWarnings.construct(result);
	}

}
