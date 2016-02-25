package de.charite.compbio.jannovar.annotation;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.annotation.AnnotationLocation.RankType;
import de.charite.compbio.jannovar.reference.TranscriptInterval;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Builder for the immutable {@link AnnotationLocation} class.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@Immutable
public class AnnotationLocationBuilder {

	/** {@link AnnotationLocation#transcript} of next build {@link AnnotationLocation}. */
	public TranscriptModel transcript = null;

	/** {@link AnnotationLocation#rankType} of next build {@link AnnotationLocation}. */
	public RankType rankType = RankType.UNDEFINED;

	/** {@link AnnotationLocation#rank} of next build {@link AnnotationLocation}. */
	public int rank = AnnotationLocation.INVALID_RANK;

	// TODO(holtgrem): transcript location probably does not belong here!
	/** {@link AnnotationLocation#txLocation} of next build {@link AnnotationLocation}. */
	public TranscriptInterval txLocation = null;

	/**
	 * @return {@link AnnotationLocation} from the builder's state.
	 */
	public AnnotationLocation build() {
		int totalRank = -1;
		if (rankType == RankType.EXON)
			totalRank = transcript.getExonRegions().size();
		else if (rankType == RankType.INTRON)
			totalRank = transcript.getExonRegions().size() - 1;
		return new AnnotationLocation(transcript, rankType, rank, totalRank, txLocation);
	}

	public TranscriptModel getTranscript() {
		return transcript;
	}

	public void setTranscript(TranscriptModel transcript) {
		this.transcript = transcript;
	}

	public RankType getRankType() {
		return rankType;
	}

	public void setRankType(RankType rankType) {
		this.rankType = rankType;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public TranscriptInterval getTXLocation() {
		return txLocation;
	}

	public void setTXLocation(TranscriptInterval txLocation) {
		this.txLocation = txLocation;
	}

}
