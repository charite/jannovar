package jannovar.reference;

import jannovar.Immutable;

/**
 * Interval on a transcript.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class CDSInterval {

	/** the selected coordinate system (0-based, 1-based) */
	public final PositionType positionType;
	/** the transcript that this position is relative to */
	public final TranscriptInfo transcript;
	/** the begin position within the transcript */
	public final int beginPos;
	/** the end position within the transcript */
	public final int endPos;

	/** construct transcript interval with one-based coordinate system */
	public CDSInterval(TranscriptInfo transcript, int beginPos, int endPos) {
		this.positionType = PositionType.ONE_BASED;
		this.transcript = transcript;
		this.beginPos = beginPos;
		this.endPos = endPos;
	}

	/** construct transcript interval with selected coordinate system */
	public CDSInterval(TranscriptInfo transcript, int beginPos, int endPos, PositionType positionType) {
		this.positionType = positionType;
		this.transcript = transcript;
		this.beginPos = beginPos;
		this.endPos = endPos;
	}

	/** construct transcript interval from other with selected coordinate system */
	public CDSInterval(CDSInterval other, PositionType positionType) {
		this.positionType = positionType;
		this.transcript = other.transcript;
		this.endPos = other.endPos;

		int beginPos = other.beginPos;
		if (other.positionType == PositionType.ZERO_BASED && this.positionType == PositionType.ONE_BASED)
			beginPos += 1;
		else if (other.positionType == PositionType.ONE_BASED && this.positionType == PositionType.ZERO_BASED)
			beginPos -= 1;
		this.beginPos = beginPos;
	}

	/** returns length of the interval */
	public int length() {
		return this.endPos - this.beginPos + (positionType == PositionType.ONE_BASED ? 1 : 0);
	}

	/*
	 * Returns string with one-based positions.
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		int beginPos = this.beginPos + (positionType == PositionType.ZERO_BASED ? 1 : 0);
		return String.format("%s:c.%d-%d", this.transcript.accession, beginPos, endPos);
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
		result = prime * result + ((positionType == null) ? 0 : positionType.hashCode());
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
		if (positionType != other.positionType)
			return false;
		if (transcript == null) {
			if (other.transcript != null)
				return false;
		} else if (!transcript.equals(other.transcript))
			return false;
		return true;
	}

}
