package de.charite.compbio.jannovar.hgvs.bridge;

import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideDuplication;
import de.charite.compbio.jannovar.htsjdk.GenomeRegionSequenceExtractor;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Implementation of converting nucleotide duplications to genome variants.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
class NucleotideDuplicationToGenomeVariantTranslationImpl extends NucleotideChangeToGenomeVariantTranslationImplBase {

	public NucleotideDuplicationToGenomeVariantTranslationImpl(GenomeRegionSequenceExtractor seqExtractor) {
		super(seqExtractor);
	}

	/**
	 * Implementation of translation for {@link NucleotideDuplication} objects
	 *
	 * @param tm
	 *            {@link TranscriptModel} that <code>ntSub</code> is for
	 * @param sequenceType
	 *            {@link SequenceType} that <code>ntSub</code> is for
	 * @param ntDup
	 *            {@link NucleotideDuplication} to convert
	 * @return {@link GenomeVariant} with the translation result, possibly annotated with warning messages
	 * @throws CannotTranslateHGVSVariant
	 *             in case of translation problems
	 */
	public ResultWithWarnings<GenomeVariant> run(TranscriptModel tm, SequenceType sequenceType,
			NucleotideDuplication ntDup) throws CannotTranslateHGVSVariant {
		final NucleotideRange range = ntDup.getRange();
		final NucleotideSeqDescription duplicatedNTDesc = ntDup.getSeq();
		final GenomeInterval gItv = posConverter.translateNucleotideRange(tm, range, sequenceType);

		// obtain duplicated sequence, setting inconsistency warnings into warningMsg, if any
		String warningMsg = null;
		String duplicatedNTs = duplicatedNTDesc.getNucleotides();
		if (duplicatedNTs == null) {
			duplicatedNTs = getGenomeSeq(tm.getStrand(), gItv);
			if (duplicatedNTDesc.length() != NucleotideSeqDescription.INVALID_NT_COUNT
					&& duplicatedNTDesc.length() != duplicatedNTs.length())
				warningMsg = "Invalid nucleotide count in " + ntDup.toHGVSString() + ", expected "
						+ duplicatedNTs.length();
		} else {
			final String refSeq = getGenomeSeq(tm.getStrand(), gItv);
			if (!refSeq.equals(duplicatedNTs))
				warningMsg = "Invalid nucleotides in " + ntDup.toHGVSString() + ", expected " + refSeq;
			duplicatedNTs = refSeq;
		}

		final GenomeVariant result = new GenomeVariant(gItv.withStrand(tm.getStrand()).getGenomeEndPos(), "",
				duplicatedNTs, tm.getStrand()).withStrand(Strand.FWD);
		if (warningMsg != null)
			return ResultWithWarnings.construct(result, warningMsg);
		else
			return ResultWithWarnings.construct(result);
	}

}
