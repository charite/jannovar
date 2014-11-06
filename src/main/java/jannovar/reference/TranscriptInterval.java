package jannovar.reference;

/**
 * Interval on a transcript.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptInterval {
	/** the selected coordinate system (0-based, 1-based) */
	private final PositionType positionType;
	/** the transcript that this position is relative to */
	private final TranscriptModel transcript;
	/** the begin position within the transcript */
	private int beginPos;
	/** the end position within the transcript */
	private int endPos;

	/** construct transcript interval with one-based coordinate system */
	public TranscriptInterval(TranscriptModel transcript, int beginPos, int endPos) {
		this.positionType = PositionType.ONE_BASED;
		this.transcript = transcript;
		this.beginPos = beginPos;
		this.endPos = endPos;
	}

	/** construct transcript interval with selected coordinate system */
	public TranscriptInterval(TranscriptModel transcript, int beginPos, int endPos, PositionType positionType) {
		this.positionType = positionType;
		this.transcript = transcript;
		this.beginPos = beginPos;
		this.endPos = endPos;
	}

	/** construct transcript interval from other with selected coordinate system */
	public TranscriptInterval(TranscriptInterval other, PositionType positionType) {
		this.positionType = positionType;
		this.transcript = other.transcript;
		this.beginPos = other.beginPos;
		this.endPos = other.endPos;
		if (other.positionType == PositionType.ZERO_BASED && this.positionType == PositionType.ONE_BASED)
			this.beginPos += 1;
		else if (other.positionType == PositionType.ONE_BASED && this.positionType == PositionType.ZERO_BASED)
			this.beginPos -= 1;
	}

	/** returns length of the interval */
	public int length() {
		return this.endPos - this.beginPos + (positionType == PositionType.ONE_BASED ? 1 : 0);
	}

	/**
	 * @return the beginPos
	 */
	public int getBeginPos() {
		return beginPos;
	}

	/**
	 * @param beginPos
	 *            the beginPos to set
	 */
	public void setBeginPos(int beginPos) {
		this.beginPos = beginPos;
	}

	/**
	 * @return the endPos
	 */
	public int getEndPos() {
		return endPos;
	}

	/**
	 * @param endPos
	 *            the endPos to set
	 */
	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	/**
	 * @return the positionType
	 */
	public PositionType getPositionType() {
		return positionType;
	}

	/**
	 * @return the transcript
	 */
	public TranscriptModel getTranscript() {
		return transcript;
	}

	/*
	 * Returns string with one-based positions.
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		int beginPos = this.beginPos + (positionType == PositionType.ZERO_BASED ? 1 : 0);
		return String.format("%s:n.%d-%d", this.transcript.getAccessionNumber(), beginPos, endPos);
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
		TranscriptInterval other = (TranscriptInterval) obj;
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
