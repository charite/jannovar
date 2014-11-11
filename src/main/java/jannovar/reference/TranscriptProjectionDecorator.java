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
	/** constant for invalid intron index */
	public static final int INVALID_INTRON_ID = -1;

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
	 * Coordinate conversion from genome position to transcript position.
	 *
	 * @param pos
	 *            genome position to convert
	 * @return position on transcript corresponding to <code>pos</code>
	 * @throws ProjectionException
	 *             if the genome position was not valid
	 */
	public TranscriptPosition genomeToTranscriptPos(GenomePosition pos) throws ProjectionException {
		if (!transcript.txRegion.contains(pos)) // guard against incorrect position
			throw new ProjectionException("Position " + pos + " is not in the transcript region " + transcript.txRegion);

		int delta = (transcript.txRegion.getPositionType() == PositionType.ONE_BASED) ? 1 : 0;

		// Look through all exons, find containing one, and compute the position.
		int transcriptPos = 0; // offset in transcript
		for (GenomeInterval region : transcript.exonRegions) {
			if (region.contains(pos))
				// Note that we have to use *the region's position* as the base object for the difference operation so
				// the case of the transcript on the reverse strand but position on forward strand is handled correctly.
				// Consequently, we have to *subtract* this difference from transcriptPos.
				return new TranscriptPosition(transcript.transcriptModel, transcriptPos
						- region.getGenomeBeginPos().differenceTo(pos) + delta);
			transcriptPos += region.length();
		}

		throw new ProjectionException("Position " + pos + " does not lie in an exon.");
	}

	/**
	 * Coordinate conversion from genome position to CDS position.
	 *
	 * This is computed from the transcript position and the offset of the CDS start position.
	 *
	 * @param pos
	 *            genome position to convert
	 * @return position on transcript corresponding to <code>pos</code>
	 * @throws ProjectionException
	 *             if the genome position was not valid
	 */
	public CDSPosition genomeToCDSPos(GenomePosition pos) throws ProjectionException {
		if (!transcript.cdsRegion.contains(pos)) // guard against incorrect position
			throw new ProjectionException("Position " + pos + " is not in the CDS region " + transcript.cdsRegion);

		int delta = (transcript.txRegion.getPositionType() == PositionType.ONE_BASED) ? 1 : 0;

		// first convert from genome to transcript position
		TranscriptPosition txPos = genomeToTranscriptPos(pos);
		// now, compute offset of CDS start in transcript and shift txPos by this to obtain CDS position
		TranscriptPosition cdsStartPos = genomeToTranscriptPos(transcript.cdsRegion.getGenomeBeginPos());
		return new CDSPosition(txPos.getTranscript(), txPos.getPos() - cdsStartPos.getPos() + delta);
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
	 * Returns the index of the (0-based) exon in the reference (forward) order.
	 *
	 * @param exonID
	 *            the 0-based exonID in transcript order (reverse order in the case of transcripts on the reverse
	 *            strand)
	 * @return the exon ID in reference (forward order)
	 */
	public int exonIDInReferenceOrder(int exonID) {
		if (transcript.getStrand() == '+')
			return exonID;
		else
			return transcript.exonRegions.length - exonID - 1;
	}

	/**
	 * Returns (0-based) index of the intron (in the order determined by the transcript's strand).
	 *
	 * @param pos
	 *            the {@link GenomePosition} to use for querying
	 * @return (0-based) index of the selected intron, or {@link #INVALID_INTRON_ID} if <tt>pos</tt> is not in exonic
	 *         region but in transcript interval
	 * @throws ProjectionException
	 *             if the position does not fall within the intron regions of the transcript
	 */
	public int locateIntron(GenomePosition pos) throws ProjectionException {
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
			if (region.withPositionType(PositionType.ZERO_BASED).isRightOf(pos))
				return i - 1;
			if (region.contains(pos))
				return INVALID_INTRON_ID; // not in intron
			++i;
		}

		return INVALID_INTRON_ID;
	}

	/**
	 * Returns (0-based) index of the exon (in the order determined by the transcript's strand).
	 *
	 * @param pos
	 *            the {@link GenomePosition} to use for querying
	 * @return (0-based) index of the selected exon, or {@link INVALID_EXON_ID} if <tt>pos</tt> is not in exonic region
	 *         but in transcript interval
	 * @throws ProjectionException
	 *             if the position does not fall within the transcription region of the transcripts
	 */
	public int locateExon(GenomePosition pos) throws ProjectionException {
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

	/**
	 * Returns (0-based) index of the exon (in the order determined by the transcript's strand).
	 *
	 * @param pos
	 *            the {@link TranscriptPosition} to use for querying
	 * @return (0-based) index of the selected exon
	 * @throws ProjectionException
	 *             if there was a problem with pos (somehow falls out of transcript region, can only happen if negative
	 *             or right of transcript end)
	 */
	public int locateExon(TranscriptPosition pos) throws ProjectionException {
		// ensure that we have a zero-based position
		pos = pos.withPositionType(PositionType.ZERO_BASED);
		// handle the case of transcript position being negative
		if (pos.getPos() < 0)
			throw new ProjectionException("Problem with transcript position " + pos + " (< 0)");

		// find exon containing pos or return null
		int currEndPos = 0; // current end position of exon in transcript
		int i = 0;
		for (GenomeInterval region : transcript.exonRegions) {
			if (pos.getPos() < currEndPos + region.length())
				return i;
			++i;
			currEndPos += region.length();
		}

		// if pos was a valid transcript position then we should not reach here
		throw new ProjectionException("Problem with transcript position " + pos + " (after last exon)");
	}

}
