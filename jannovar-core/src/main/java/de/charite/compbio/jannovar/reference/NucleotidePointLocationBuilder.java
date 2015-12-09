package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.hgvs.nts.NucleotidePointLocation;

/**
 * Helper class that allows easy building of {@link NucleotidePointLocation}s.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@Immutable
public final class NucleotidePointLocationBuilder {

	/** transcript to use for the coordinate system */
	final TranscriptModel transcript;
	/** helper for performing coordinate projection */
	final TranscriptProjectionDecorator projector;
	/** helper for performing Sequence Ontology feature queries */
	final TranscriptSequenceOntologyDecorator soDecorator;

	/** Construct the position builder with the given transcript */
	public NucleotidePointLocationBuilder(TranscriptModel transcript) {
		this.transcript = transcript;
		this.projector = new TranscriptProjectionDecorator(transcript);
		this.soDecorator = new TranscriptSequenceOntologyDecorator(transcript);
	}

	/**
	 * @param pos
	 *            {@link GenomePosition} with to translate into {@link NucleotidePointLocation}
	 * @return {@link NucleotidePointLocation}, given the transcript in {@link #transcript}.
	 */
	public NucleotidePointLocation getNucleotidePointLocation(GenomePosition pos) {
		// Guard against cases upstream/downstream of transcription region.
		if (transcript.getTXRegion().isRightOf(pos)) // upstream of transcription region
			return getCDNANucleotidePointLocationForUpstreamPos(pos);
		else if (transcript.getTXRegion().isLeftOf(pos)) // downstream of transcription region
			return getCDNANucleotidePointLocationForDownstreamPos(pos);

		// The main difference now is between intronic and exonic regions.
		if (soDecorator.liesInExon(new GenomeInterval(pos, 0)))
			return getCDNANucleotidePointLocationForExonPos(pos);
		else
			return getCDNANucleotidePointLocationForIntronPos(pos);
	}

	/**
	 * Return {@link NucleotidePointLocation} in case of exon positions.
	 *
	 * @param pos
	 *            position to get the HGVS position for
	 * @return corresponding {@link NucleotidePointLocation}
	 */
	private NucleotidePointLocation getCDNANucleotidePointLocationForExonPos(GenomePosition pos) {
		try {
			GenomePosition zeroCDSStartPos = getCDSRegion().getGenomeBeginPos();
			TranscriptPosition tCDSStartPos = projector.genomeToTranscriptPos(zeroCDSStartPos);
			GenomePosition zeroCDSEndPos = getCDSRegion().getGenomeEndPos();
			TranscriptPosition tCDSEndPos = projector.genomeToTranscriptPos(zeroCDSEndPos.shifted(-1));
			TranscriptPosition tPos = projector.genomeToTranscriptPos(pos);

			if (getCDSRegion().contains(pos)) {
				// pos lies within the CDS, the easiest case
				return NucleotidePointLocation.build(tPos.getPos() - tCDSStartPos.getPos());
			} else if (getCDSRegion().isRightOf(pos)) {
				// pos lies upstream of the CDS
				return NucleotidePointLocation.build(-(tCDSStartPos.getPos() - tPos.getPos()));
			} else {
				// pos lies downstream of the CDS
				return NucleotidePointLocation.buildDownstreamOfCDS(tPos.getPos() - tCDSEndPos.getPos() - 1);
			}
		} catch (ProjectionException e) {
			throw new Error("Bug: position must lie in CDS at this point. " + e.getMessage());
		}
	}

	/**
	 * Return {@link NucleotidePointLocation} in case of intron positions.
	 *
	 * @param pos
	 *            position to get the HGVS position for
	 * @return corresponding {@link NucleotidePointLocation}
	 */
	private NucleotidePointLocation getCDNANucleotidePointLocationForIntronPos(GenomePosition pos) {
		// Determine which exon is the closest one, ties are broken to the downstream direction as in HGVS,
		// generate offset position within exon.
		final int exonNumber = projector.locateIntron(pos); // also intronNumber ;)
		if (exonNumber == TranscriptProjectionDecorator.INVALID_INTRON_ID)
			throw new Error("Bug: position must lie in CDS at this point.");
		GenomePosition exonEndPos = transcript.getExonRegions().get(exonNumber).getGenomeEndPos();
		GenomePosition nextExonBeginPos = transcript.getExonRegions().get(exonNumber + 1).getGenomeBeginPos();
		GenomePosition basePos = null;
		int offset = 0;
		if (pos.differenceTo(exonEndPos) < nextExonBeginPos.differenceTo(pos)) {
			basePos = exonEndPos.shifted(-1);
			offset = pos.differenceTo(exonEndPos) + 1;
		} else {
			basePos = nextExonBeginPos;
			offset = -nextExonBeginPos.differenceTo(pos);
		}

		NucleotidePointLocation baseLoc = getCDNANucleotidePointLocationForExonPos(basePos);
		return new NucleotidePointLocation(baseLoc.getBasePos(), offset, baseLoc.isDownstreamOfCDS());
	}

	/**
	 * Return {@link NucleotidePointLocation} in case of upstream positions.
	 *
	 * @param pos
	 *            position to get the HGVS position for
	 * @return corresponding {@link NucleotidePointLocation}
	 */
	private NucleotidePointLocation getCDNANucleotidePointLocationForUpstreamPos(GenomePosition pos) {
		// The upstream position is simply given as "-$count" where $count is the transcript position of the CDS
		// start plus the genomic base distance of pos to the CDS start.
		try {
			TranscriptPosition tPos = projector.genomeToTranscriptPos(getCDSRegion().getGenomeBeginPos());
			int numBases = transcript.getTXRegion().getGenomeBeginPos().differenceTo(pos);
			return NucleotidePointLocation.build(-(tPos.getPos() + numBases));
		} catch (ProjectionException e) {
			throw new Error("CDS end position must be translatable to transcript position.");
		}
	}

	/**
	 * Return {@link NucleotidePointLocation} in case of downstream positions.
	 *
	 * @param pos
	 *            position to get the HGVS position for
	 * @return corresponding {@link NucleotidePointLocation}
	 */
	private NucleotidePointLocation getCDNANucleotidePointLocationForDownstreamPos(GenomePosition pos) {
		// The downstream position is simply given as "*$count" where $count is the genomic base offset after the CDS
		// region.
		int numBases = -getCDSRegion().getGenomeEndPos().differenceTo(pos);
		return NucleotidePointLocation.buildDownstreamOfCDS(numBases);
	}

	/**
	 * @return the CDS region for a coding and the TX region for a non-coding transcript
	 */
	private GenomeInterval getCDSRegion() {
		if (transcript.isCoding())
			return transcript.getCDSRegion();
		else
			return transcript.getTXRegion();
	}

}
