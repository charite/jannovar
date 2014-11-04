package jannovar.reference;

import jannovar.exception.ProjectionException;

/**
 * Wraps a {@link TranscriptInfo} object and allow the coordinate conversion.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptProjectionDecorator {
	/** constant for invalid exon index */
	public static final int INVALID_EXON_ID = -1;

	/** the transcript information to perform the projection upon. */
	private final TranscriptInfo transcript;

	/**
	 * Initialize the object with the given {@link TranscriptInfo}.
	 *
	 * @param transcript
	 */
	public TranscriptProjectionDecorator(TranscriptInfo transcript) {
		this.transcript = transcript;
	}

	/**
	 * @return the transcript
	 */
	public TranscriptInfo getTranscript() {
		return transcript;
	}

	/**
	 * Coordinate conversion from transcript to genome position.
	 *
	 * @param pos
	 *            the position on the transcript
	 * @return the corresponding genome position for pos, will be on the same strand as the transcript
	 *
	 * @throws ProjectionException
	 *             on problems with the coordinate transformation (outside of the transcript)
	 */
	// TODO(holtgrem): Is this code correct? Does the transcript start at the CDS or at the first exon? The algorithm is
	// equivalent to TranscriptModel.getChromosomalCoordinates.
	public GenomePosition transcriptToGenomePos(TranscriptPosition pos) throws ProjectionException {
		final int targetPos = pos.withPositionType(PositionType.ZERO_BASED).getPos(); // 0-based target pos
		if (targetPos < 0)
			throw new ProjectionException("Invalid transcript position " + targetPos);

		int currPos = 0; // relative begin position of current exon
		for (GenomeInterval region : transcript.exonRegions) {
			if (targetPos < currPos + region.length())
				return region.getGenomeBeginPos().shifted(targetPos - currPos);
			currPos += region.length();
		}

		throw new ProjectionException("Invalid transcript position " + targetPos);
	}

	/**
	 * Coordinate conversion from genome position to transcript position.
	 */

	/**
	 * Returns index of the exon (in the order determined by the transcript's strand).
	 *
	 * @param pos
	 *            the {@link GenomePosition} to use for querying
	 * @return index of the selected exon, or {@link INVALID_EXON_ID} if <tt>pos</tt> is not in exonic region but in
	 *         transcript interval
	 * @throws ProjectionException
	 *             if the position does not fall within the transcription region of the transcripts
	 */
	// TODO(holtgrem): throwing logic correct here?
	int locateExon(GenomePosition pos) throws ProjectionException {
		if (pos.getChr() != transcript.getChr()) // guard against different chromosomes
			throw new ProjectionException("Different chromosome in position " + pos + " than on transcript region "
					+ transcript.txRegion);
		if (pos.getStrand() != transcript.getStrand()) // ensure pos is on the same strand
			pos = pos.withStrand(transcript.getStrand());

		// handle the case that the position is outside the transcript region
		if (transcript.txRegion.isLeftOf(pos) || transcript.txRegion.isRightOf(pos))
			throw new ProjectionException("Position " + pos + " outside of tx region " + transcript.txRegion);

		// find exon containing pos or return null
		int i = 0;
		for (GenomeInterval region : transcript.exonRegions) {
			if (region.contains(pos))
				return i;
			++i;
		}

		return INVALID_EXON_ID;
	}
}
