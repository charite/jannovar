package jannovar.reference;

import jannovar.Immutable;

/**
 * Helper class that allows easy building of HGVS position strings.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class HGVSPositionBuilder {

	/** transcript to use for the coordinate system */
	final TranscriptInfo transcript;
	/** helper for performing coordinate projection */
	final TranscriptProjectionDecorator projector;
	/** helper for performing Sequence Ontology feature queries */
	final TranscriptSequenceOntologyDecorator soDecorator;

	/** Construct the position builder with the given transcript */
	public HGVSPositionBuilder(TranscriptInfo transcript) {
		this.transcript = transcript;
		this.projector = new TranscriptProjectionDecorator(transcript);
		this.soDecorator = new TranscriptSequenceOntologyDecorator(transcript);
	}

	/**
	 * @param pos
	 *            {@link GenomePosition} with to translate into HGSV position
	 * @return the HGVS <code>String</code> with the positions's representation, given the transcript in
	 *         {@link #transcript}.
	 * @throws ProjectionException
	 *             in case there are problems with coordinate translations
	 */
	public String getCDNAPosStr(GenomePosition pos) {
		pos = pos.withPositionType(PositionType.ZERO_BASED); // assuming zero-based positions below

		// Guard against cases upstream/downstream of transcription region.
		if (transcript.txRegion.isRightOf(pos)) // upstream of transcription region
			return getCDNAPosStrForUpstreamPos(pos);
		else if (transcript.txRegion.isLeftOf(pos)) // downstream of transcription region
			return getCDNAPosStrForDownstreamPos(pos);

		// The main difference now is between intronic and exonic regions.
		if (soDecorator.liesInExon(new GenomeInterval(pos, 0)))
			return getCDNAPosStrForExonPos(pos);
		else
			return getCDNAPosStrForIntronPos(pos);
	}

	/**
	 * Return HGVS position string in case of exon positions.
	 *
	 * @param pos
	 *            position to get the HGVS position for
	 * @return HGVS position string
	 */
	private String getCDNAPosStrForExonPos(GenomePosition pos) {
		try {
			GenomePosition zeroCDSStartPos = getCDSRegion().withPositionType(PositionType.ZERO_BASED)
					.getGenomeBeginPos();
			TranscriptPosition tCDSStartPos = projector.genomeToTranscriptPos(zeroCDSStartPos);
			GenomePosition zeroCDSEndPos = getCDSRegion().withPositionType(PositionType.ZERO_BASED).getGenomeEndPos();
			TranscriptPosition tCDSEndPos = projector.genomeToTranscriptPos(zeroCDSEndPos.shifted(-1));
			TranscriptPosition tPos = projector.genomeToTranscriptPos(pos);

			if (getCDSRegion().contains(pos)) {
				// pos lies within the CDS, the easiest case
				return String.format("%d", tPos.pos - tCDSStartPos.pos + 1);
			} else if (getCDSRegion().isRightOf(pos)) {
				// pos lies upstream of the CDS
				return String.format("-%d", tCDSStartPos.pos - tPos.pos);
			} else {
				// pos lies downstream of the CDS
				return String.format("*%d", tPos.pos - tCDSEndPos.pos);
			}
		} catch (ProjectionException e) {
			throw new Error("Bug: position must lie in CDS at this point. " + e.getMessage());
		}
	}

	/**
	 * Return HGVS position string in case of intron positions.
	 *
	 * @param pos
	 *            position to get the HGVS position for
	 * @return HGVS position string
	 */
	private String getCDNAPosStrForIntronPos(GenomePosition pos) {
		// Determine which exon is the closest one, ties are broken to the downstream direction as in HGVS,
		// generate offset position within exon.
		final int exonNumber = projector.locateIntron(pos); // also intronNumber ;)
		if (exonNumber == TranscriptProjectionDecorator.INVALID_INTRON_ID)
			throw new Error("Bug: position must lie in CDS at this point.");
		GenomePosition exonEndPos = transcript.exonRegions.get(exonNumber).withPositionType(PositionType.ZERO_BASED)
				.getGenomeEndPos();
		GenomePosition nextExonBeginPos = transcript.exonRegions.get(exonNumber + 1)
				.withPositionType(PositionType.ZERO_BASED).getGenomeBeginPos();
		GenomePosition basePos = null;
		String offsetStr = null;
		if (pos.differenceTo(exonEndPos) < nextExonBeginPos.differenceTo(pos)) {
			basePos = exonEndPos.shifted(-1);
			offsetStr = String.format("+%d", pos.differenceTo(exonEndPos) + 1);
		} else {
			basePos = nextExonBeginPos;
			offsetStr = String.format("-%d", nextExonBeginPos.differenceTo(pos));
		}

		// Get string for the exonic position exonPos and paste together final position string.
		return String.format("%s%s", getCDNAPosStrForExonPos(basePos), offsetStr);
	}

	/**
	 * Return HGVS position string in case of upstream positions.
	 *
	 * @param pos
	 *            position to get the HGVS position for
	 * @return HGVS position string
	 */
	private String getCDNAPosStrForUpstreamPos(GenomePosition pos) {
		// The upstream position is simply given as "-$count" where $count is the transcript position of the CDS
		// start plus the genomic base distance of pos to the CDS start.
		try {
			TranscriptPosition tPos = projector.genomeToTranscriptPos(getCDSRegion().withPositionType(
					PositionType.ZERO_BASED).getGenomeBeginPos());
			int numBases = transcript.txRegion.withPositionType(PositionType.ZERO_BASED).getGenomeBeginPos()
					.differenceTo(pos);
			return String.format("-%d", tPos.pos + numBases);
		} catch (ProjectionException e) {
			throw new Error("CDS end position must be translatable to transcript position.");
		}
	}

	/**
	 * Return HGVS position string in case of downstream positions.
	 *
	 * @param pos
	 *            position to get the HGVS position for
	 * @return HGVS position string
	 */
	private String getCDNAPosStrForDownstreamPos(GenomePosition pos) {
		// The downstream position is simply given as "*$count" where $count is the genomic base offset after the CDS
		// region.
		int numBases = -getCDSRegion().withPositionType(PositionType.ZERO_BASED).getGenomeEndPos().differenceTo(pos);
		return String.format("*%d", numBases + 1);
	}

	/**
	 * @return the CDS region for a coding and the TX region for a non-coding transcript
	 */
	private GenomeInterval getCDSRegion() {
		if (transcript.isCoding())
			return transcript.cdsRegion;
		else
			return transcript.txRegion;
	}

}
