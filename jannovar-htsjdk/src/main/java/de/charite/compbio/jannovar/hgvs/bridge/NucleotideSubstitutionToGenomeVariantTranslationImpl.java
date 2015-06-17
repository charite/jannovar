package de.charite.compbio.jannovar.hgvs.bridge;

import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.nts.NucleotidePointLocation;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideSubstitution;
import de.charite.compbio.jannovar.htsjdk.GenomeRegionSequenceExtractor;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Implementation of converting nucleotide substitutions to genome variants.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
class NucleotideSubstitutionToGenomeVariantTranslationImpl extends NucleotideChangeToGenomeVariantTranslationImplBase {

	public NucleotideSubstitutionToGenomeVariantTranslationImpl(GenomeRegionSequenceExtractor seqExtractor) {
		super(seqExtractor);
	}

	/**
	 * Implementation of translation for {@link NucleotideSubstitution} objects
	 *
	 * @param tm
	 *            {@link TranscriptModel} that <code>ntSub</code> is for
	 * @param sequenceType
	 *            {@link SequenceType} that <code>ntSub</code> is for
	 * @return {@link GenomeVariant} with the translation result, possibly annotated with warning messages
	 * @throws CannotTranslateHGVSVariant
	 *             in case of translation problems
	 */
	public ResultWithWarnings<GenomeVariant> run(TranscriptModel tm, SequenceType sequenceType,
			NucleotideSubstitution ntSub) throws CannotTranslateHGVSVariant {
		final NucleotidePointLocation pos = ntSub.getPosition();
		final String fromNT = ntSub.getFromNT().toUpperCase();
		final String toNT = ntSub.getToNT().toUpperCase();

		if (fromNT.length() != 1 || toNT.length() != 1)
			throw new CannotTranslateHGVSVariant("Both source and target sequence must have length 1 in "
					+ ntSub.toHGVSString());

		final GenomeVariant result = new GenomeVariant(posConverter.translateNucleotidePointLocation(tm, pos,
				sequenceType), fromNT, toNT, tm.getStrand()).withStrand(Strand.FWD);
		final String refSeq = getGenomeSeq(tm.getStrand(), result.getGenomeInterval());
		if (!refSeq.equals(fromNT)) {
			final GenomeVariant result2 = new GenomeVariant(posConverter.translateNucleotidePointLocation(tm, pos,
					sequenceType), refSeq, toNT, tm.getStrand()).withStrand(Strand.FWD);
			return ResultWithWarnings.construct(result2, "Invalid reference nucleotides in " + result.toString()
					+ ", should be " + refSeq + ", auto-correcting.");
		} else {
			return ResultWithWarnings.construct(result);
		}
	}

}
