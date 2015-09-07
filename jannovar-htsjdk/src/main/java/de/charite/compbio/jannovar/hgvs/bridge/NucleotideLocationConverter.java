package de.charite.compbio.jannovar.hgvs.bridge;

import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.nts.NucleotidePointLocation;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.reference.CDSPosition;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.ProjectionException;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptPosition;
import de.charite.compbio.jannovar.reference.TranscriptProjectionDecorator;

/**
 * Helper class for converting {@link NucleotidePointLocation}s and {@link NucleotideRange}s to {@link GenomePosition}s
 * and {@link GenomeRange}s.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class NucleotideLocationConverter {

	/**
	 * Convert {@link NucleotidePointLocation} on a {@link TranscriptModel} to a {@link GenomePosition}
	 */
	public GenomePosition translateNucleotidePointLocation(TranscriptModel tm, NucleotidePointLocation pos,
			SequenceType sequenceType) throws CannotTranslateHGVSVariant {
		switch (sequenceType) {
		case CODING_DNA:
			return translateCodingNucleotidePointLocation(tm, pos);
		case NON_CODING_DNA:
			return translateNonCodingNucleotidePointLocation(tm, pos);
		default:
			throw new CannotTranslateHGVSVariant("Unsupported sequence type " + sequenceType);
		}
	}

	/**
	 * Translate CDS ("c.") NucleotidePointLocation to a {@link GenomePosition}.
	 *
	 * @param tm
	 *            {@link TranscriptModel} that the location is relative to
	 * @param pos
	 *            {@link NucleotidePointLocation} to translate
	 * @return resulting {@link GenomePosition} of the translation
	 * @throws CannotTranslateHGVSVariant
	 *             in case of problems with the translation
	 */
	protected GenomePosition translateCodingNucleotidePointLocation(TranscriptModel tm, NucleotidePointLocation pos)
			throws CannotTranslateHGVSVariant {
		final TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(tm);

		// compute transcript position of NucleotidePointLocation pos
		TranscriptPosition txPos;
		if (pos.getBasePos() < 0) {
			// handle 5' UTR case
			txPos = projector.cdsToTranscriptPos(new CDSPosition(tm, 0));
			if (txPos.getPos() < -pos.getBasePos())
				throw new CannotTranslateHGVSVariant("Invalid CDS position " + pos.toHGVSString()
						+ " as it lies upstream of 5' UTR");
			txPos = new TranscriptPosition(tm, txPos.getPos() + pos.getBasePos());
		} else if (pos.isDownstreamOfCDS()) {
			// handle 3' UTR case
			final CDSPosition lastCDSPos = new CDSPosition(tm, tm.cdsTranscriptLength() - 1);
			txPos = projector.cdsToTranscriptPos(lastCDSPos);
			// Note that for HGVS, the terminal codon is not part of the CDS whereas it is in TranscriptModel.
			// This is the case for the SHIFT below.
			final int SHIFT = -3;
			txPos = txPos.shifted(pos.getBasePos() + 1 + SHIFT);
			if (txPos.getPos() >= tm.transcriptLength())
				throw new CannotTranslateHGVSVariant("Invalid CDS position " + pos.toHGVSString()
						+ " as it lies downstream of 3' UTR");
		} else {
			// handle CDS case
			final CDSPosition cdsPos = new CDSPosition(tm, pos.getBasePos());
			txPos = projector.cdsToTranscriptPos(cdsPos);
		}

		// translate this into a GenomePosition on the same strand as tm and return this
		try {
			return projector.transcriptToGenomePos(txPos).withStrand(tm.getStrand()).shifted(pos.getOffset());
		} catch (ProjectionException e) {
			throw new CannotTranslateHGVSVariant("could not translate base transcript position " + txPos + " of "
					+ pos.toHGVSString() + " to a genome position", e);
		}
	}

	/**
	 * Translate non-coding (transcript, "n.") NucleotidePointLocation to a {@link GenomePosition}.
	 *
	 * @param tm
	 *            {@link TranscriptModel} that the location is relative to
	 * @param pos
	 *            {@link NucleotidePointLocation} to translate
	 * @return resulting {@link GenomePosition} of the translation
	 * @throws CannotTranslateHGVSVariant
	 *             in case of problems with the translation
	 */
	protected GenomePosition translateNonCodingNucleotidePointLocation(TranscriptModel tm, NucleotidePointLocation pos)
			throws CannotTranslateHGVSVariant {
		final TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(tm);

		TranscriptPosition txPos = new TranscriptPosition(tm, pos.getBasePos());

		try {
			return projector.transcriptToGenomePos(txPos).withStrand(tm.getStrand()).shifted(pos.getOffset());
		} catch (ProjectionException e) {
			throw new CannotTranslateHGVSVariant("could not translate base transcript position " + txPos + " of "
					+ pos.toHGVSString() + " to a genome position", e);
		}
	}

	/**
	 * Convert {@link NucleotideRange} on a {@link TranscriptModel} to a {@link GenomePosition}
	 */
	public GenomeInterval translateNucleotideRange(TranscriptModel tm, NucleotideRange range, SequenceType sequenceType)
			throws CannotTranslateHGVSVariant {
		final GenomePosition firstPos = translateNucleotidePointLocation(tm, range.getFirstPos(), sequenceType);
		final GenomePosition lastPos = translateNucleotidePointLocation(tm, range.getLastPos(), sequenceType);
		int length = lastPos.differenceTo(firstPos) + 1;

		return new GenomeInterval(firstPos, length);
	}

}
