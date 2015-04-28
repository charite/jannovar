package de.charite.compbio.jannovar.annotation;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.reference.TranscriptInterval;
import de.charite.compbio.jannovar.reference.TranscriptModel;

// TODO(holtgrem): Test me!

/**
 * Describes the location of an annotation.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public class AnnotationLocation {

	/** Enumeration for rank types, exon, intron, or neither (i.e. spans more than one feature). */
	public enum RankType {
		EXON, INTRON, UNDEFINED
	};

	/** Sentinel value for "invalid rank". */
	public static final int INVALID_RANK = -1;

	/** The transcript that this location lies on */
	private final TranscriptModel transcript;

	/** The rank type (whether {@link #rank} and {@link #totalRank} are exon/intron positions). */
	private final RankType rankType;

	/** Current exon/intron rank, 0-based. */
	private final int rank;

	/** Total number of exons/introns in transcript */
	private final int totalRank;

	/** Location of the change on the transcript, null if outside of transcript */
	private final TranscriptInterval txLocation;

	public AnnotationLocation(TranscriptModel transcript, RankType rankType, int rank, int totalRank,
			TranscriptInterval txLocation) {
		this.transcript = transcript;
		this.rankType = rankType;
		this.rank = rank;
		this.totalRank = totalRank;
		this.txLocation = txLocation;
	}

	/** @return transcript that this location lies on */
	public TranscriptModel getTranscript() {
		return transcript;
	}

	/** @return rank type (whether {@link #rank} and {@link #totalRank} are exon/intron positions). */
	public RankType getRankType() {
		return rankType;
	}

	/** @return current exon/intron rank, 0-based. */
	public int getRank() {
		return rank;
	}

	/** @return total number of exons/introns in transcript */
	public int getTotalRank() {
		return totalRank;
	}

	/** @return location of the change on the transcript, null if outside of transcript */
	public TranscriptInterval getTXLocation() {
		return txLocation;
	}

	/**
	 * @return location to be used in a HGVS String
	 */
	public String toHGVSChunk() {
		StringBuilder builder = new StringBuilder();
		builder.append(transcript.getAccession());
		if (rankType != RankType.UNDEFINED)
			builder.append(":").append(rankType.toString().toLowerCase()).append(rank + 1);
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rank;
		result = prime * result + ((rankType == null) ? 0 : rankType.hashCode());
		result = prime * result + totalRank;
		result = prime * result + ((transcript == null) ? 0 : transcript.hashCode());
		result = prime * result + ((txLocation == null) ? 0 : txLocation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnnotationLocation other = (AnnotationLocation) obj;
		if (rank != other.rank)
			return false;
		if (rankType != other.rankType)
			return false;
		if (totalRank != other.totalRank)
			return false;
		if (transcript == null) {
			if (other.transcript != null)
				return false;
		} else if (!transcript.equals(other.transcript))
			return false;
		if (txLocation == null) {
			if (other.txLocation != null)
				return false;
		} else if (!txLocation.equals(other.txLocation))
			return false;
		return true;
	}

}
