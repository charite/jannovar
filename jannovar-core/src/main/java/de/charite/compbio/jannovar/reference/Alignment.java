package de.charite.compbio.jannovar.reference;

import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * An anchor-based alignment, optimized for small-ish alignments where linear search in the gap
 * anchor is fast or even faster than binary search.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class Alignment implements Serializable {

	/**
	 * Class version (for serialization).
	 */
	private static final long serialVersionUID = 1L;

	// Reference anchors.
	private final ImmutableList<Anchor> refAnchors;

	// Query anchors.
	private final ImmutableList<Anchor> qryAnchors;

	/**
	 * Construct new {@code Alignment}.
	 *
	 * @param refAnchors The reference anchors.
	 * @param qryAnchors The query anchors.
	 */
	public Alignment(List<Anchor> refAnchors, List<Anchor> qryAnchors) {
		this.refAnchors = ImmutableList.copyOf(refAnchors);
		this.qryAnchors = ImmutableList.copyOf(qryAnchors);
	}

	/**
	 * Create ungapped alignment for the given sequence.
	 *
	 * @param length The transcript length to create the alignment for.
	 * @return Ungapped {@code Alignment}
	 */
	public static Alignment createUngappedAlignment(int length) {
		return new Alignment(ImmutableList.of(new Anchor(0, 0), new Anchor(length, length)),
			ImmutableList.of(new Anchor(0, 0), new Anchor(length, length)));
	}

	/**
	 * @return Return number of leading gaps in reference.
	 */
	public int refLeadingGapLength() {
		return Anchors.countLeadingGaps(refAnchors);
	}

	/**
	 * @return Return number of trailing gaps in reference.
	 */
	public int refTrailingGapLength() {
		return Anchors.countTrailingGaps(refAnchors);
	}

	/**
	 * @return Return number of leading gaps in query.
	 */
	public int qryLeadingGapLength() {
		return Anchors.countLeadingGaps(qryAnchors);
	}

	/**
	 * @return Return number of trailing gaps in query.
	 */
	public int qryTrailingGapLength() {
		return Anchors.countTrailingGaps(qryAnchors);
	}

	/**
	 * Project reference to query position.
	 *
	 * @param refPos Reference position.
	 * @return The aligning position in the query.
	 */
	public int projectRefToQry(int refPos) {
		final int aliPos = Anchors.projectSeqToGapPos(refAnchors, refPos);
		final int qryPos = Anchors.projectGapToSeqPos(qryAnchors, aliPos);
		return qryPos;
	}

	/**
	 * Project query to reference position.
	 *
	 * @param qryPos Query position.
	 * @return The aligning position in the reference.
	 */
	public int projectQryToRef(int qryPos) {
		final int aliPos = Anchors.projectSeqToGapPos(qryAnchors, qryPos);
		final int refPos = Anchors.projectGapToSeqPos(refAnchors, aliPos);
		return refPos;
	}

	@Override public String toString() {
		return "Alignment{" + "refAnchors=" + refAnchors + ", qryAnchors=" + qryAnchors + '}';
	}

	@Override public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Alignment alignment = (Alignment) o;
		return Objects.equals(refAnchors, alignment.refAnchors) && Objects
			.equals(qryAnchors, alignment.qryAnchors);
	}

	@Override public int hashCode() {
		return Objects.hash(refAnchors, qryAnchors);
	}

}
