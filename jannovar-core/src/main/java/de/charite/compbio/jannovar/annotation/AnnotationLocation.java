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
	public static int INVALID_RANK = -1;

	/** The that this location lies on */
	public final TranscriptModel transcript;

	/** The rank type (whether {@link #rank} and {@link #totalRank} are exon/intron positions). */
	public final RankType rankType;

	/** Current exon/intron rank, 0-based. */
	public final int rank;

	/** Total number of exons/introns in transcript */
	public final int totalRank;

	/** Location of the change on the transcript, null if outside of transcript */
	public final TranscriptInterval txLocation;

	public AnnotationLocation(TranscriptModel transcript, RankType rankType, int rank, int totalRank,
			TranscriptInterval txLocation) {
		this.transcript = transcript;
		this.rankType = rankType;
		this.rank = rank;
		this.totalRank = totalRank;
		this.txLocation = txLocation;
	}

	// TODO(holtgrem): rename!
	/**
	 * @return location as a HGVS String
	 */
	public String toHGVSString() {
		StringBuilder builder = new StringBuilder();
		builder.append(transcript.accession);
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
