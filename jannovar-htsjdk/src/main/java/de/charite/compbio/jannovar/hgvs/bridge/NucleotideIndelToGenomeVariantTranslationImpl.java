package de.charite.compbio.jannovar.hgvs.bridge;

import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideIndel;
import de.charite.compbio.jannovar.htsjdk.GenomeRegionSequenceExtractor;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Implementation of converting nucleotide indels to genome variants.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
class NucleotideIndelToGenomeVariantTranslationImpl extends NucleotideChangeToGenomeVariantTranslationImplBase {

	public NucleotideIndelToGenomeVariantTranslationImpl(GenomeRegionSequenceExtractor seqExtractor) {
		super(seqExtractor);
	}

	/**
	 * Implementation of translation for {@link NucleotideIndel} objects
	 *
	 * @param tm
	 *            {@link TranscriptModel} that <code>ntSub</code> is for
	 * @param sequenceType
	 *            {@link SequenceType} that <code>ntSub</code> is for
	 * @param ntIndel
	 *            {@link NucleotideIndel} to convert
	 * @return {@link GenomeVariant} with the translation result, possibly annotated with warning messages
	 * @throws CannotTranslateHGVSVariant
	 *             in case of translation problems
	 */
	public ResultWithWarnings<GenomeVariant> run(TranscriptModel tm, SequenceType sequenceType, NucleotideIndel ntIndel)
			throws CannotTranslateHGVSVariant {
		final NucleotideRange range = ntIndel.getRange();
		final NucleotideSeqDescription insertedNTDesc = ntIndel.getInsSeq();
		final NucleotideSeqDescription deletedNTDesc = ntIndel.getDelSeq();
		final GenomeInterval gItv = posConverter.translateNucleotideRange(tm, range, sequenceType);

		// perform sanity check of inserted nucleotides
		if (insertedNTDesc.getNucleotides() == null)
			throw new CannotTranslateHGVSVariant("Nucleotides must be given but were not in " + ntIndel.toHGVSString());

		// obtain deleted sequence, setting inconsistency warnings into warningMsg, if any
		String warningMsg = null;
		String deletedNTs = deletedNTDesc.getNucleotides();
		if (deletedNTs == null) {
			deletedNTs = getGenomeSeq(tm.getStrand(), gItv);
			if (deletedNTDesc.length() != NucleotideSeqDescription.INVALID_NT_COUNT
					&& deletedNTDesc.length() != deletedNTs.length())
				warningMsg = "Invalid reference nucleotide count in " + ntIndel.toHGVSString() + ", expected "
						+ deletedNTs.length();
		} else {
			final String refSeq = getGenomeSeq(tm.getStrand(), gItv);
			if (!refSeq.equals(deletedNTs))
				warningMsg = "Invalid reference nucleotides in " + ntIndel.toHGVSString() + ", expected " + refSeq;
			deletedNTs = refSeq;
		}

		final GenomeVariant result = new GenomeVariant(gItv.withStrand(tm.getStrand()).getGenomeBeginPos(), deletedNTs,
				insertedNTDesc.getNucleotides(), tm.getStrand()).withStrand(Strand.FWD);
		if (warningMsg != null)
			return ResultWithWarnings.construct(result, warningMsg);
		else
			return ResultWithWarnings.construct(result);
	}

}
