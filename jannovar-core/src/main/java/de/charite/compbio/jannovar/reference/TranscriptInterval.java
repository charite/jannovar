package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.impl.util.StringUtil;

/**
 * Interval on a transcript.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class TranscriptInterval {

	/** the transcript that this position is relative to */
	private final TranscriptModel transcript;
	/** the begin position within the transcript */
	private final int beginPos;
	/** the end position within the transcript */
	private final int endPos;

	/** construct transcript interval with one-based coordinate system */
	public TranscriptInterval(TranscriptModel transcript, int beginPos, int endPos) {
		this(transcript, beginPos, endPos, PositionType.ZERO_BASED);
	}

	/** construct transcript interval with selected coordinate system */
	public TranscriptInterval(TranscriptModel transcript, int beginPos, int endPos, PositionType positionType) {
		this.transcript = transcript;
		this.beginPos = beginPos + ((positionType == PositionType.ONE_BASED) ? -1 : 0);
		this.endPos = endPos;
	}

	/** @return transcript that this position is relative to */
	public TranscriptModel getTranscript() {
		return transcript;
	}

	/** @return zero-based begin position */
	public int getBeginPos() {
		return beginPos;
	}

	/** @return zero-based end position */
	public int getEndPos() {
		return endPos;
	}

	/** @return length of the interval */
	public int length() {
		return this.endPos - this.beginPos;
	}

	/** @return begin position of the interval */
	public TranscriptPosition getTranscriptBeginPos() {
		// TODO(holtgrem): test me!
		return new TranscriptPosition(transcript, beginPos, PositionType.ZERO_BASED);
	}

	/** @return end position of the interval */
	public TranscriptPosition getTranscriptEndPos() {
		// TODO(holtgrem): test me!
		return new TranscriptPosition(transcript, endPos, PositionType.ZERO_BASED);
	}

	/*
	 * Returns string with one-based positions.
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return StringUtil.concatenate(transcript.getAccession(), ":n.", beginPos + 1, "-", endPos);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + beginPos;
		result = prime * result + endPos;
		result = prime * result + ((transcript == null) ? 0 : transcript.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TranscriptInterval other = (TranscriptInterval) obj;
		if (beginPos != other.beginPos)
			return false;
		if (endPos != other.endPos)
			return false;
		if (transcript == null) {
			if (other.transcript != null)
				return false;
		} else if (!transcript.equals(other.transcript))
			return false;
		return true;
	}

}
