package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.impl.util.StringUtil;

/**
 * Interval on a transcript.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@Immutable
public final class CDSInterval {

	/** the transcript that this position is relative to */
	private final TranscriptModel transcript;
	/** the begin position within the transcript */
	private final int beginPos;
	/** the end position within the transcript */
	private final int endPos;

	/** construct transcript interval with one-based coordinate system */
	public CDSInterval(TranscriptModel transcript, int beginPos, int endPos) {
		this.transcript = transcript;
		this.beginPos = beginPos;
		this.endPos = endPos;
	}

	/** construct transcript interval with selected coordinate system */
	public CDSInterval(TranscriptModel transcript, int beginPos, int endPos, PositionType positionType) {
		this.transcript = transcript;
		if (positionType == PositionType.ONE_BASED)
			this.beginPos = beginPos - 1;
		else
			this.beginPos = beginPos;
		this.endPos = endPos;
	}

	/** @return the transcript that this position is relative to */
	public TranscriptModel getTranscript() {
		return transcript;
	}

	/** @return the begin position within the transcript */
	public int getBeginPos() {
		return beginPos;
	}

	/** @return the end position within the transcript */
	public int getEndPos() {
		return endPos;
	}

	/** returns length of the interval */
	public int length() {
		return this.endPos - this.beginPos;
	}

	/*
	 * Returns string with one-based positions.
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return StringUtil.concatenate(this.transcript.getAccession(), ":c.", beginPos + 1, "-", endPos);
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
		CDSInterval other = (CDSInterval) obj;
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
