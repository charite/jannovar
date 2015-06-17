package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.impl.util.StringUtil;

/**
 * Position on a transcript.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class TranscriptPosition {

	/** the transcript that this position is relative to */
	private final TranscriptModel transcript;
	/** the position within the transcript */
	private final int pos;

	/** construct transcript position with one-based coordinate system */
	public TranscriptPosition(TranscriptModel transcript, int pos) {
		this(transcript, pos, PositionType.ZERO_BASED);
	}

	/** construct transcript position with selected coordinate system */
	public TranscriptPosition(TranscriptModel transcript, int pos, PositionType positionType) {
		this.transcript = transcript;
		this.pos = pos + ((positionType == PositionType.ONE_BASED) ? -1 : 0);
	}

	/** @return the transcript that this position is relative to */
	public TranscriptModel getTranscript() {
		return transcript;
	}

	/** @return the position within the transcript */
	public int getPos() {
		return pos;
	}

	/**
	 * Return shifted TranscriptPosition.
	 *
	 * @param delta
	 *            the value to add to the position
	 * @return the position shifted by <tt>delta</tt>
	 */
	public TranscriptPosition shifted(int delta) {
		return new TranscriptPosition(transcript, pos + delta, PositionType.ZERO_BASED);
	}

	/*
	 * Returns string with one-based position.
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return StringUtil.concatenate(transcript.getAccession(), ":n.", pos + 1);
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
		result = prime * result + pos;
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
		TranscriptPosition other = (TranscriptPosition) obj;
		if (pos != other.pos)
			return false;
		if (transcript == null) {
			if (other.transcript != null)
				return false;
		} else if (!transcript.equals(other.transcript))
			return false;
		return true;
	}

}
