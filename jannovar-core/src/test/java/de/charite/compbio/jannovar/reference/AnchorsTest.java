package de.charite.compbio.jannovar.reference;

import com.google.common.collect.ImmutableList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AnchorsTest {

	List<Anchor> ungapped;
	List<Anchor> leadingGap;
	List<Anchor> oneGap;
	List<Anchor> twoGaps;
	List<Anchor> trailingGap;

	@BeforeEach
	public void setUp() {
		// 10M
		ungapped = ImmutableList.of(new Anchor(0, 0), new Anchor(10, 10));
		// 2D8M
		leadingGap = ImmutableList.of(new Anchor(0, 0), new Anchor(2, 0), new Anchor(10, 8));
		// 4M2D4M
		oneGap = ImmutableList
			.of(new Anchor(0, 0), new Anchor(4, 4), new Anchor(6, 4), new Anchor(10, 8));
		// 2M1D4M1D2M
		twoGaps = ImmutableList
			.of(new Anchor(0, 0), new Anchor(2, 2), new Anchor(3, 2), new Anchor(7, 6),
				new Anchor(8, 6), new Anchor(10, 8));
		trailingGap = ImmutableList.of(new Anchor(0, 0), new Anchor(8, 8), new Anchor(10, 8));
	}

	@Test
	public void testGapLength() {
		Assertions.assertEquals(10, Anchors.gapLength(ungapped));
		Assertions.assertEquals(10, Anchors.gapLength(leadingGap));
		Assertions.assertEquals(10, Anchors.gapLength(oneGap));
		Assertions.assertEquals(10, Anchors.gapLength(twoGaps));
		Assertions.assertEquals(10, Anchors.gapLength(trailingGap));
	}

	@Test
	public void testSeqLength() {
		Assertions.assertEquals(10, Anchors.seqLength(ungapped));
		Assertions.assertEquals(8, Anchors.seqLength(leadingGap));
		Assertions.assertEquals(8, Anchors.seqLength(oneGap));
		Assertions.assertEquals(8, Anchors.seqLength(twoGaps));
		Assertions.assertEquals(8, Anchors.seqLength(trailingGap));
	}

	@Test
	public void testProjectGapToSeqPos() {
		Assertions.assertEquals(0, Anchors.projectGapToSeqPos(ungapped, 0));
		Assertions.assertEquals(1, Anchors.projectGapToSeqPos(ungapped, 1));
		Assertions.assertEquals(2, Anchors.projectGapToSeqPos(ungapped, 2));
		Assertions.assertEquals(3, Anchors.projectGapToSeqPos(ungapped, 3));
		Assertions.assertEquals(4, Anchors.projectGapToSeqPos(ungapped, 4));
		Assertions.assertEquals(5, Anchors.projectGapToSeqPos(ungapped, 5));
		Assertions.assertEquals(6, Anchors.projectGapToSeqPos(ungapped, 6));
		Assertions.assertEquals(7, Anchors.projectGapToSeqPos(ungapped, 7));
		Assertions.assertEquals(8, Anchors.projectGapToSeqPos(ungapped, 8));
		Assertions.assertEquals(9, Anchors.projectGapToSeqPos(ungapped, 9));
		Assertions.assertEquals(10, Anchors.projectGapToSeqPos(ungapped, 10));

		Assertions.assertEquals(0, Anchors.projectGapToSeqPos(leadingGap, 0));
		Assertions.assertEquals(0, Anchors.projectGapToSeqPos(leadingGap, 1));
		Assertions.assertEquals(0, Anchors.projectGapToSeqPos(leadingGap, 2));
		Assertions.assertEquals(1, Anchors.projectGapToSeqPos(leadingGap, 3));
		Assertions.assertEquals(2, Anchors.projectGapToSeqPos(leadingGap, 4));
		Assertions.assertEquals(3, Anchors.projectGapToSeqPos(leadingGap, 5));
		Assertions.assertEquals(4, Anchors.projectGapToSeqPos(leadingGap, 6));
		Assertions.assertEquals(5, Anchors.projectGapToSeqPos(leadingGap, 7));
		Assertions.assertEquals(6, Anchors.projectGapToSeqPos(leadingGap, 8));
		Assertions.assertEquals(7, Anchors.projectGapToSeqPos(leadingGap, 9));
		Assertions.assertEquals(8, Anchors.projectGapToSeqPos(leadingGap, 10));

		Assertions.assertEquals(0, Anchors.projectGapToSeqPos(oneGap, 0));
		Assertions.assertEquals(1, Anchors.projectGapToSeqPos(oneGap, 1));
		Assertions.assertEquals(2, Anchors.projectGapToSeqPos(oneGap, 2));
		Assertions.assertEquals(3, Anchors.projectGapToSeqPos(oneGap, 3));
		Assertions.assertEquals(4, Anchors.projectGapToSeqPos(oneGap, 4));
		Assertions.assertEquals(4, Anchors.projectGapToSeqPos(oneGap, 5));
		Assertions.assertEquals(4, Anchors.projectGapToSeqPos(oneGap, 6));
		Assertions.assertEquals(5, Anchors.projectGapToSeqPos(oneGap, 7));
		Assertions.assertEquals(6, Anchors.projectGapToSeqPos(oneGap, 8));
		Assertions.assertEquals(7, Anchors.projectGapToSeqPos(oneGap, 9));
		Assertions.assertEquals(8, Anchors.projectGapToSeqPos(oneGap, 10));

		Assertions.assertEquals(0, Anchors.projectGapToSeqPos(twoGaps, 0));
		Assertions.assertEquals(1, Anchors.projectGapToSeqPos(twoGaps, 1));
		Assertions.assertEquals(2, Anchors.projectGapToSeqPos(twoGaps, 2));
		Assertions.assertEquals(2, Anchors.projectGapToSeqPos(twoGaps, 3));
		Assertions.assertEquals(3, Anchors.projectGapToSeqPos(twoGaps, 4));
		Assertions.assertEquals(4, Anchors.projectGapToSeqPos(twoGaps, 5));
		Assertions.assertEquals(5, Anchors.projectGapToSeqPos(twoGaps, 6));
		Assertions.assertEquals(6, Anchors.projectGapToSeqPos(twoGaps, 7));
		Assertions.assertEquals(6, Anchors.projectGapToSeqPos(twoGaps, 8));
		Assertions.assertEquals(7, Anchors.projectGapToSeqPos(twoGaps, 9));
		Assertions.assertEquals(8, Anchors.projectGapToSeqPos(twoGaps, 10));

		Assertions.assertEquals(0, Anchors.projectGapToSeqPos(trailingGap, 0));
		Assertions.assertEquals(1, Anchors.projectGapToSeqPos(trailingGap, 1));
		Assertions.assertEquals(2, Anchors.projectGapToSeqPos(trailingGap, 2));
		Assertions.assertEquals(3, Anchors.projectGapToSeqPos(trailingGap, 3));
		Assertions.assertEquals(4, Anchors.projectGapToSeqPos(trailingGap, 4));
		Assertions.assertEquals(5, Anchors.projectGapToSeqPos(trailingGap, 5));
		Assertions.assertEquals(6, Anchors.projectGapToSeqPos(trailingGap, 6));
		Assertions.assertEquals(7, Anchors.projectGapToSeqPos(trailingGap, 7));
		Assertions.assertEquals(8, Anchors.projectGapToSeqPos(trailingGap, 8));
		Assertions.assertEquals(8, Anchors.projectGapToSeqPos(trailingGap, 9));
		Assertions.assertEquals(8, Anchors.projectGapToSeqPos(trailingGap, 10));
	}

	@Test
	public void testProjectSeqToGapPos() {
		Assertions.assertEquals(0, Anchors.projectSeqToGapPos(ungapped, 0));
		Assertions.assertEquals(1, Anchors.projectSeqToGapPos(ungapped, 1));
		Assertions.assertEquals(2, Anchors.projectSeqToGapPos(ungapped, 2));
		Assertions.assertEquals(3, Anchors.projectSeqToGapPos(ungapped, 3));
		Assertions.assertEquals(4, Anchors.projectSeqToGapPos(ungapped, 4));
		Assertions.assertEquals(5, Anchors.projectSeqToGapPos(ungapped, 5));
		Assertions.assertEquals(6, Anchors.projectSeqToGapPos(ungapped, 6));
		Assertions.assertEquals(7, Anchors.projectSeqToGapPos(ungapped, 7));
		Assertions.assertEquals(8, Anchors.projectSeqToGapPos(ungapped, 8));
		Assertions.assertEquals(9, Anchors.projectSeqToGapPos(ungapped, 9));
		Assertions.assertEquals(10, Anchors.projectSeqToGapPos(ungapped, 10));

		Assertions.assertEquals(2, Anchors.projectSeqToGapPos(leadingGap, 0));
		Assertions.assertEquals(3, Anchors.projectSeqToGapPos(leadingGap, 1));
		Assertions.assertEquals(4, Anchors.projectSeqToGapPos(leadingGap, 2));
		Assertions.assertEquals(5, Anchors.projectSeqToGapPos(leadingGap, 3));
		Assertions.assertEquals(6, Anchors.projectSeqToGapPos(leadingGap, 4));
		Assertions.assertEquals(7, Anchors.projectSeqToGapPos(leadingGap, 5));
		Assertions.assertEquals(8, Anchors.projectSeqToGapPos(leadingGap, 6));
		Assertions.assertEquals(9, Anchors.projectSeqToGapPos(leadingGap, 7));
		Assertions.assertEquals(10, Anchors.projectSeqToGapPos(leadingGap, 8));

		Assertions.assertEquals(0, Anchors.projectSeqToGapPos(oneGap, 0));
		Assertions.assertEquals(1, Anchors.projectSeqToGapPos(oneGap, 1));
		Assertions.assertEquals(2, Anchors.projectSeqToGapPos(oneGap, 2));
		Assertions.assertEquals(3, Anchors.projectSeqToGapPos(oneGap, 3));
		Assertions.assertEquals(6, Anchors.projectSeqToGapPos(oneGap, 4));
		Assertions.assertEquals(7, Anchors.projectSeqToGapPos(oneGap, 5));
		Assertions.assertEquals(8, Anchors.projectSeqToGapPos(oneGap, 6));
		Assertions.assertEquals(9, Anchors.projectSeqToGapPos(oneGap, 7));
		Assertions.assertEquals(10, Anchors.projectSeqToGapPos(oneGap, 8));

		Assertions.assertEquals(0, Anchors.projectSeqToGapPos(twoGaps, 0));
		Assertions.assertEquals(1, Anchors.projectSeqToGapPos(twoGaps, 1));
		Assertions.assertEquals(3, Anchors.projectSeqToGapPos(twoGaps, 2));
		Assertions.assertEquals(4, Anchors.projectSeqToGapPos(twoGaps, 3));
		Assertions.assertEquals(5, Anchors.projectSeqToGapPos(twoGaps, 4));
		Assertions.assertEquals(6, Anchors.projectSeqToGapPos(twoGaps, 5));
		Assertions.assertEquals(8, Anchors.projectSeqToGapPos(twoGaps, 6));
		Assertions.assertEquals(9, Anchors.projectSeqToGapPos(twoGaps, 7));
		Assertions.assertEquals(10, Anchors.projectSeqToGapPos(twoGaps, 8));

		Assertions.assertEquals(0, Anchors.projectSeqToGapPos(trailingGap, 0));
		Assertions.assertEquals(1, Anchors.projectSeqToGapPos(trailingGap, 1));
		Assertions.assertEquals(2, Anchors.projectSeqToGapPos(trailingGap, 2));
		Assertions.assertEquals(3, Anchors.projectSeqToGapPos(trailingGap, 3));
		Assertions.assertEquals(4, Anchors.projectSeqToGapPos(trailingGap, 4));
		Assertions.assertEquals(5, Anchors.projectSeqToGapPos(trailingGap, 5));
		Assertions.assertEquals(6, Anchors.projectSeqToGapPos(trailingGap, 6));
		Assertions.assertEquals(7, Anchors.projectSeqToGapPos(trailingGap, 7));
		Assertions.assertEquals(10, Anchors.projectSeqToGapPos(trailingGap, 8));
	}

	@Test public void testCountLeadingGaps() {
		Assertions.assertEquals(0, Anchors.countLeadingGaps(ungapped));
		Assertions.assertEquals(2, Anchors.countLeadingGaps(leadingGap));
		Assertions.assertEquals(0, Anchors.countLeadingGaps(oneGap));
		Assertions.assertEquals(0, Anchors.countLeadingGaps(twoGaps));
		Assertions.assertEquals(0, Anchors.countLeadingGaps(trailingGap));
	}

	@Test public void testCountTrailingGaps() {
		Assertions.assertEquals(0, Anchors.countTrailingGaps(ungapped));
		Assertions.assertEquals(0, Anchors.countTrailingGaps(leadingGap));
		Assertions.assertEquals(0, Anchors.countTrailingGaps(oneGap));
		Assertions.assertEquals(0, Anchors.countTrailingGaps(twoGaps));
		Assertions.assertEquals(2, Anchors.countTrailingGaps(trailingGap));
	}
}
