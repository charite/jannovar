package de.charite.compbio.jannovar.reference;

import java.io.Serializable;
import java.util.Objects;

/**
 * An anchor in an {@code Alignment}.
 * <pre>
 *        0    5   10
 *        :    .    :
 *   ali: --NNN--NNN--
 *
 *   gaps: [(0, 0), (2, 0), (5, 3), (7, 3), (10, 6), (12, 6)]
 * </pre>
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class Anchor implements Serializable {
	private final int gapPos;
	private final int seqPos;

	// Version for serialization.
	private static final long serialVersionUID = 0L;

	/**
	 * Construct new anchor.
	 *
	 * @param gapPos Reference position of anchor.
	 * @param seqPos Sequence position fo anchor.
	 */
	public Anchor(int gapPos, int seqPos) {
		this.gapPos = gapPos;
		this.seqPos = seqPos;
	}

	/**
	 * @return Alignment position of anchor.
	 */
	public int getGapPos() {
		return gapPos;
	}

	/**
	 * @return Sequence position of anchor.
	 */
	public int getSeqPos() {
		return seqPos;
	}

	@Override public String toString() {
		return "Anchor{" + "gapPos=" + gapPos + ", seqPos=" + seqPos + '}';
	}

	@Override public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Anchor anchor = (Anchor) o;
		return gapPos == anchor.gapPos && seqPos == anchor.seqPos;
	}

	@Override public int hashCode() {
		return Objects.hash(gapPos, seqPos);
	}
}
