package de.charite.compbio.jannovar.hgvs.bridge;

import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideInsertion;
import de.charite.compbio.jannovar.htsjdk.GenomeRegionSequenceExtractor;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Implementation of converting nucleotide insertions to genome variants.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
class NucleotideInsertionToGenomeVariantTranslationImpl extends NucleotideChangeToGenomeVariantTranslationImplBase {

	public NucleotideInsertionToGenomeVariantTranslationImpl(GenomeRegionSequenceExtractor seqExtractor) {
		super(seqExtractor);
	}

	/**
	 * Implementation of translation for {@link NucleotideInsertion} objects
	 *
	 * @param tm
	 *            {@link TranscriptModel} that <code>ntSub</code> is for
	 * @param sequenceType
	 *            {@link SequenceType} that <code>ntSub</code> is for
	 * @param ntIns
	 *            {@link NucleotideInsertion} to convert
	 * @return {@link GenomeVariant} with the translation result, possibly annotated with warning messages
	 * @throws CannotTranslateHGVSVariant
	 *             in case of translation problems
	 */
	public ResultWithWarnings<GenomeVariant> run(TranscriptModel tm, SequenceType sequenceType,
			NucleotideInsertion ntIns) throws CannotTranslateHGVSVariant {
		final NucleotideRange range = ntIns.getRange();
		final NucleotideSeqDescription insertedNTDesc = ntIns.getSeq();
		final GenomeInterval gItv = posConverter.translateNucleotideRange(tm, range, sequenceType);

		// perform sanity check of inserted nucleotides
		if (insertedNTDesc.getNucleotides() == null)
			throw new CannotTranslateHGVSVariant("Nucleotides must be given but were not in " + ntIns.toHGVSString());

		final GenomeVariant result = new GenomeVariant(gItv.withStrand(tm.getStrand()).getGenomeBeginPos().shifted(1),
				"", insertedNTDesc.getNucleotides(), tm.getStrand()).withStrand(Strand.FWD);
		return ResultWithWarnings.construct(result);
	}

}
