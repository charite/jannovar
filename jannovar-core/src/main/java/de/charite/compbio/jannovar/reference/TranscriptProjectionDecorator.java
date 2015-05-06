package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.Immutable;

/**
 * Wraps a {@link TranscriptInfo} object and allow the coordinate conversion.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class TranscriptProjectionDecorator {

	/** constant for invalid exon index */
	public static final int INVALID_EXON_ID = -1;
	/** constant for invalid intron index */
	public static final int INVALID_INTRON_ID = -1;

	/** the transcript information to perform the projection upon. */
	private final TranscriptModel transcript;

	/**
	 * Initialize the object with the given {@link TranscriptInfo}.
	 *
	 * @param transcript
	 *            the {@link TranscriptInfo} to decorate
	 */
	public TranscriptProjectionDecorator(TranscriptModel transcript) {
		this.transcript = transcript;
	}

	/**
	 * @return the transcript
	 */
	public TranscriptModel getTranscript() {
		return transcript;
	}

	/**
	 * @return the CDS transcript string
	 */
	public String getCDSTranscript() {
		try {
			TranscriptPosition tBeginPos = genomeToTranscriptPos(transcript.getCDSRegion().getGenomeBeginPos());
			TranscriptPosition tEndPos = genomeToTranscriptPos(transcript.getCDSRegion().getGenomeEndPos());
			return transcript.getSequence().substring(tBeginPos.getPos(), tEndPos.getPos());
		} catch (ProjectionException e) {
			throw new Error("Bug: CDS begin/end must be translatable into transcript positions");
		}
	}

	/**
	 * @return the CDS transcript string extended to the right for the full transcript
	 */
	public String getTranscriptStartingAtCDS() {
		try {
			TranscriptPosition tBeginPos = genomeToTranscriptPos(transcript.getCDSRegion().getGenomeBeginPos());
			return transcript.getSequence().substring(tBeginPos.getPos(), transcript.getSequence().length());
		} catch (ProjectionException e) {
			throw new Error("Bug: CDS begin must be translatable into transcript positions");
		}
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
		if (!transcript.getTXRegion().contains(pos)) // guard against incorrect position
			throw new ProjectionException("Position " + pos + " is not in the transcript region "
					+ transcript.getTXRegion());
		pos = pos.withStrand(transcript.getStrand());

		// Look through all exons, find containing one, and compute the position.
		int tOffset = 0; // offset in transcript
		for (GenomeInterval region : transcript.getExonRegions()) {
			if (region.contains(pos)) {
				// Note that we have to use *the region's position* as the base object for the difference operation so
				// the case of the transcript on the reverse strand but position on forward strand is handled correctly.
				// Consequently, we have to *subtract* this difference from transcriptPos.
				int posInExon = pos.differenceTo(region.getGenomeBeginPos());
				int transcriptPos = tOffset + posInExon;
				return new TranscriptPosition(transcript, transcriptPos, PositionType.ZERO_BASED);
			}
			tOffset += region.length();
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
		if (!transcript.getCDSRegion().contains(pos)) // guard against incorrect position
			throw new ProjectionException("Position " + pos + " is not in the CDS region " + transcript.getCDSRegion());
		pos = pos.withStrand(transcript.getStrand());

		// first convert from genome to transcript position
		TranscriptPosition txPos = genomeToTranscriptPos(pos);
		// now, compute offset of CDS start in transcript and shift txPos by this to obtain CDS position
		TranscriptPosition cdsStartPos = genomeToTranscriptPos(transcript.getCDSRegion().getGenomeBeginPos());
		return new CDSPosition(txPos.getTranscript(), txPos.getPos() - cdsStartPos.getPos(), PositionType.ZERO_BASED);
	}

	/**
	 * Coordinate conversion from CDS to transcript position.
	 *
	 * @param pos
	 *            the position in the CDS transcript
	 * @return the corresponding genome position for pos, will be on the same strand as the transcript
	 */
	public TranscriptPosition cdsToTranscriptPos(CDSPosition pos) {
		final GenomePosition cdsBeginPos = transcript.getCDSRegion().getGenomeBeginPos();

		int currPos = 0; // current transcript position
		for (GenomeInterval region : transcript.getExonRegions()) {
			if (region.getGenomeEndPos().isLeq(cdsBeginPos)) {
				currPos += region.length();
			} else {
				currPos += cdsBeginPos.differenceTo(region.getGenomeBeginPos());
				break;
			}
		}

		return new TranscriptPosition(transcript, currPos + pos.getPos());
	}

	/**
	 * Coordinate conversion from CDS to genome position.
	 *
	 * @param pos
	 *            the position in the CDS transcript
	 * @return the corresponding genome position for pos, will be on the same strand as the transcript
	 *
	 * @throws ProjectionException
	 *             on problems with the coordinate transformation (outside of the transcript)
	 */
	public GenomePosition cdsToGenomePos(CDSPosition pos) throws ProjectionException {
		return transcriptToGenomePos(cdsToTranscriptPos(pos));
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
		final int targetPos = pos.getPos(); // 0-based target pos
		if (targetPos < 0)
			throw new ProjectionException("Invalid transcript position " + targetPos);

		int currPos = 0; // relative begin position of current exon
		for (GenomeInterval region : transcript.getExonRegions()) {
			if (targetPos < currPos + region.length())
				return region.getGenomeBeginPos().shifted(targetPos - currPos);
			currPos += region.length();
		}

		// handling case of transcript end position
		// TODO(holtgrewe): add test for this
		GenomeInterval lastRegion = transcript.getExonRegions().get(transcript.getExonRegions().size() - 1);
		if (targetPos == currPos)
			return lastRegion.getGenomeEndPos();

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
		if (transcript.getStrand().isForward())
			return exonID;
		else
			return transcript.getExonRegions().size() - exonID - 1;
	}

	/**
	 * Returns (0-based) index of the intron (in the order determined by the transcript's strand).
	 *
	 * @param pos
	 *            the {@link GenomePosition} to use for querying
	 * @return (0-based) index of the selected intron, or {@link #INVALID_INTRON_ID} if <tt>pos</tt> is not in exonic
	 *         region but in transcript interval
	 */
	public int locateIntron(GenomePosition pos) {
		if (pos.getChr() != transcript.getChr()) // guard against different chromosomes
			return INVALID_INTRON_ID;
		if (pos.getStrand() != transcript.getStrand()) // ensure pos is on the same strand
			pos = pos.withStrand(transcript.getStrand());

		// handle the case that the position is outside the transcript region
		if (transcript.getTXRegion().isLeftOf(pos) || transcript.getTXRegion().isRightOf(pos))
			return INVALID_INTRON_ID;

		// find exon containing pos or return null
		int i = 0;
		for (GenomeInterval region : transcript.getExonRegions()) {
			if (region.isRightOf(pos))
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
	 * @return (0-based) index of the selected exon, or {@link #INVALID_EXON_ID} if <tt>pos</tt> is not in exonic region
	 *         but in transcript interval
	 */
	public int locateExon(GenomePosition pos) {
		if (pos.getChr() != transcript.getChr()) // guard against different chromosomes
			return INVALID_EXON_ID;
		if (pos.getStrand() != transcript.getStrand()) // ensure pos is on the same strand
			pos = pos.withStrand(transcript.getStrand());

		// handle the case that the position is outside the transcript region
		if (transcript.getTXRegion().isLeftOf(pos) || transcript.getTXRegion().isRightOf(pos))
			return INVALID_EXON_ID;

		// find exon containing pos or return null
		GenomeInterval posBase = new GenomeInterval(pos, 1); // region of referenced base
		int i = 0;
		for (GenomeInterval region : transcript.getExonRegions()) {
			if (region.contains(posBase))
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
		// handle the case of transcript position being negative
		if (pos.getPos() < 0)
			throw new ProjectionException("Problem with transcript position " + pos + " (< 0)");

		// find exon containing pos or return null
		int currEndPos = 0; // current end position of exon in transcript
		int i = 0;
		for (GenomeInterval region : transcript.getExonRegions()) {
			if (pos.getPos() < currEndPos + region.length())
				return i;
			++i;
			currEndPos += region.length();
		}

		// if pos was a valid transcript position then we should not reach here
		throw new ProjectionException("Problem with transcript position " + pos + " (after last exon)");
	}

	/**
	 * Translate {@link GenomePosition} to {@link CDSPosition} for {@link #transcript}.
	 *
	 * Positions upstream of CDS region are projected to the CDS begin position, downstream of CDS are projected to the
	 * CDS end, positions in CDS introns are projected to first position of the next CDS exon.
	 *
	 * @param pos
	 *            the position to translate
	 * @return the corresponding position in the transcript sequence
	 */
	public CDSPosition projectGenomeToCDSPosition(GenomePosition pos) {
		// TODO(holtgrem): Test me!
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		TranscriptSequenceOntologyDecorator soDecorator = new TranscriptSequenceOntologyDecorator(transcript);

		try {
			// Get transcript begin position.
			if (transcript.getCDSRegion().isRightOf(pos)) {
				// Deletion begins left of CDS, project to begin of CDS.
				return new CDSPosition(transcript, 0);
			} else if (transcript.getCDSRegion().isLeftOf(pos)) {
				// Deletion begins right of CDS, project to end of CDS.
				return new CDSPosition(transcript, transcript.cdsTranscriptLength());
			} else if (soDecorator.liesInExon(pos)) {
				return projector.genomeToCDSPos(pos);
			} else { // lies in intron, project to begin position of next exon
				int intronNum = projector.locateIntron(pos);
				return projector.genomeToCDSPos(transcript.getExonRegions().get(intronNum + 1).getGenomeBeginPos());
			}
		} catch (ProjectionException e) {
			throw new Error("Bug: must be able to convert CDS exon position! " + e.getMessage());
		}
	}

	/**
	 * Translate {@link GenomeInterval} to {@link CDSInterval} for {@link #transcript}.
	 *
	 * Positions upstream of CDS region are projected to the CDS begin position, downstream of CDS are projected to the
	 * CDS end.
	 *
	 * @param pos
	 *            the position to translate
	 * @return the corresponding position in the transcript sequence
	 */
	public CDSInterval projectGenomeToCDSInterval(GenomeInterval interval) {
		final CDSPosition cdsBeginPos = projectGenomeToCDSPosition(interval.getGenomeBeginPos());
		final CDSPosition cdsEndPos = projectGenomeToCDSPosition(interval.getGenomeEndPos().shifted(-1)).shifted(1);
		return new CDSInterval(transcript, cdsBeginPos.getPos(), cdsEndPos.getPos(), PositionType.ZERO_BASED);
	}

	/**
	 * Translate {@link GenomePosition} to {@link TranscriptPosition} for {@link #transcript}.
	 *
	 * Positions upstream of TX region are projected to the TX begin position, downstream of TX are projected to the TX
	 * end, positions in introns are projected to first position of the next CDS exon.
	 *
	 * @param pos
	 *            the position to translate
	 * @return the corresponding position in the transcript sequence
	 */
	public TranscriptPosition projectGenomeToTXPosition(GenomePosition pos) {
		// TODO(holtgrem): Test me!
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		TranscriptSequenceOntologyDecorator soDecorator = new TranscriptSequenceOntologyDecorator(transcript);

		try {
			// Get transcript begin position.
			if (transcript.getTXRegion().isRightOf(pos)) {
				// Deletion begins left of CDS, project to begin of CDS.
				return new TranscriptPosition(transcript, 0, PositionType.ZERO_BASED);
			} else if (transcript.getTXRegion().isLeftOf(pos)) {
				// Deletion begins right of CDS, project to end of CDS.
				return new TranscriptPosition(transcript, transcript.transcriptLength(), PositionType.ZERO_BASED);
			} else if (soDecorator.liesInExon(pos)) {
				return projector.genomeToTranscriptPos(pos);
			} else { // lies in intron, project to begin position of next exon
				int intronNum = projector.locateIntron(pos);
				return projector.genomeToTranscriptPos(transcript.getExonRegions().get(intronNum + 1)
						.getGenomeBeginPos());
			}
		} catch (ProjectionException e) {
			throw new Error("Bug: must be able to convert TX exon position! " + e.getMessage());
		}
	}

	/**
	 * Translate {@link GenomeInterval} to {@link TranscriptInterval} for {@link #transcript}.
	 *
	 * Positions upstream of TX region are projected to the TX begin position, downstream of TX are projected to the TX
	 * end.
	 *
	 * @param pos
	 *            the position to translate
	 * @return the corresponding position in the transcript sequence
	 */
	public TranscriptInterval projectGenomeToTXInterval(GenomeInterval interval) {
		final TranscriptPosition txBeginPos = projectGenomeToTXPosition(interval.getGenomeBeginPos());
		final TranscriptPosition txEndPos = projectGenomeToTXPosition(interval.getGenomeEndPos().shifted(-1))
				.shifted(1);
		return new TranscriptInterval(transcript, txBeginPos.getPos(), txEndPos.getPos(), PositionType.ZERO_BASED);
	}

}
