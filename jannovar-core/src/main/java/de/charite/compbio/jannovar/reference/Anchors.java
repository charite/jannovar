package de.charite.compbio.jannovar.reference;

import java.util.List;

/**
 * Helper class for working with {@link Anchor}-based alignments.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class Anchors {

	/**
	 * Return length of gaps.
	 */
	public static int gapLength(List<Anchor> anchors) {
		if (anchors.size() < 2) {
			throw new RuntimeException("Must have at least two anchors!");
		}
		return anchors.get(anchors.size() - 1).getGapPos();
	}

	/**
	 * Return length of sequence.
	 */
	public static int seqLength(List<Anchor> anchors) {
		if (anchors.size() < 2) {
			throw new RuntimeException("Must have at least two anchors!");
		}
		return anchors.get(anchors.size() - 1).getSeqPos();
	}

	/**
	 * Project from gaps position to sequence position.
	 *
	 * @param anchors The anchors structure to use for projection.
	 * @param gapPos The gap position to project.
	 */
	public static int projectGapToSeqPos(final List<Anchor> anchors, final int gapPos) {
		// Early exit if anchors are empty => ungapped sequence.
		if (anchors.size() < 2) {
			throw new RuntimeException("Must have at least two anchors!");
		}
		assert gapPos <= gapLength(anchors);

		for (int idx = 0; idx + 1 < anchors.size(); ++idx) {
			final Anchor thisAnchor = anchors.get(idx);
			final Anchor nextAnchor = anchors.get(idx + 1);
			final int seqPos = thisAnchor.getSeqPos();
			int chunkAliLen = nextAnchor.getGapPos() - thisAnchor.getGapPos();
			int offset = gapPos - thisAnchor.getGapPos();
			if (offset < chunkAliLen) {
				return seqPos + (thisAnchor.getSeqPos() == nextAnchor.getSeqPos() ? 0 : offset);
			}
		}
		assert gapPos == gapLength(anchors);
		return seqLength(anchors);
	}

	/**
	 * Project from sequence position to gaps position.
	 *
	 * @param anchors The anchors structure to use for projection.
	 * @param seqPos The sequence position to project.
	 */
	public static int projectSeqToGapPos(final List<Anchor> anchors, final int seqPos) {
		// Early exit if anchors are empty => ungapped sequence.
		if (anchors.size() < 2) {
			throw new RuntimeException("Must have at least two anchors!");
		}
		if (seqPos > seqLength(anchors)) {
			// Project beyond length to the end.
			return gapLength(anchors) + seqLength(anchors) - seqPos;
		}

		int gapPos = 0;
		for (int idx = 0; idx + 1 < anchors.size(); ++idx) {
			final Anchor thisAnchor = anchors.get(idx);
			final Anchor nextAnchor = anchors.get(idx + 1);
			gapPos = thisAnchor.getGapPos();
			int chunkSeqLen = nextAnchor.getSeqPos() - thisAnchor.getSeqPos();
			int offset = seqPos - thisAnchor.getSeqPos();
			if (offset < chunkSeqLen) {
				return gapPos + offset;
			}
		}
		assert seqPos == seqLength(anchors);
		return gapLength(anchors);
	}

	/**
	 * Compute number of leading gaps in anchors.
	 *
	 * @param anchors The anchors structure to use for computation.
	 * @return Number of leading gaps in the {@code anchors}.
	 */
	public static int countLeadingGaps(List<Anchor> anchors) {
		if (anchors.size() < 2) {
			throw new RuntimeException("Must have at least two anchors!");
		} else if (anchors.size() < 2 || (anchors.get(0).getSeqPos() != anchors.get(1)
			.getSeqPos())) {
			return 0;
		} else {
			assert anchors.get(0).getSeqPos() == 0;
			return anchors.get(1).getGapPos();
		}
	}

	/**
	 * Compute number of leading gaps in anchors.
	 *
	 * @param anchors The anchors structure to use for computation.
	 * @return Number of leading gaps in the {@code anchors}.
	 */
	public static int countTrailingGaps(List<Anchor> anchors) {
		final int len = anchors.size();
		if (len < 2) {
			throw new RuntimeException("Must have at least two anchors!");
		} else if ((anchors.get(len - 1).getSeqPos() != anchors.get(len - 2).getSeqPos())) {
			return 0;
		} else {
			return anchors.get(len - 1).getGapPos() - anchors.get(len - 2).getGapPos();
		}
	}
}
