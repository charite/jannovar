package de.charite.compbio.jannovar.impl.intervals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IntervalTest {

	Interval<String> interval;

	@BeforeEach
	public void setUp() throws Exception {
		interval = new Interval<String>(1, 10, "x", 13);
	}

	@Test
	public void testValues() {
		Assertions.assertEquals(1, interval.getBegin());
		Assertions.assertEquals(10, interval.getEnd());
		Assertions.assertEquals(13, interval.getMaxEnd());
		Assertions.assertEquals("x", interval.getValue());
	}

	@Test
	public void testAllLeftOf() {
		Assertions.assertFalse(interval.allLeftOf(10));
		Assertions.assertFalse(interval.allLeftOf(12));
		Assertions.assertTrue(interval.allLeftOf(13));
		Assertions.assertTrue(interval.allLeftOf(14));
	}

	@Test
	public void testCompareTo() {
		Assertions.assertTrue(interval.compareTo(new Interval<String>(0, 10, "y", 10)) > 0);
		Assertions.assertTrue(interval.compareTo(new Interval<String>(1, 9, "y", 10)) > 0);
		Assertions.assertTrue(interval.compareTo(new Interval<String>(1, 10, "y", 10)) == 0);
		Assertions.assertTrue(interval.compareTo(new Interval<String>(1, 11, "y", 10)) < 0);
		Assertions.assertTrue(interval.compareTo(new Interval<String>(2, 10, "y", 10)) < 0);
	}

	@Test
	public void testContains() {
		Assertions.assertFalse(interval.contains(0));
		Assertions.assertTrue(interval.contains(1));
		Assertions.assertTrue(interval.contains(2));
		Assertions.assertTrue(interval.contains(9));
		Assertions.assertFalse(interval.contains(10));
	}

	@Test
	public void testIsLeftOf() {
		Assertions.assertFalse(interval.isLeftOf(8));
		Assertions.assertFalse(interval.isLeftOf(9));
		Assertions.assertTrue(interval.isLeftOf(10));
		Assertions.assertTrue(interval.isLeftOf(11));
	}

	@Test
	public void testIsRightOf() {
		Assertions.assertTrue(interval.isRightOf(-1));
		Assertions.assertTrue(interval.isRightOf(0));
		Assertions.assertFalse(interval.isRightOf(1));
		Assertions.assertFalse(interval.isRightOf(2));
	}

	@Test
	public void overlapsWith() {
		Assertions.assertTrue(interval.overlapsWith(0, 2));
		Assertions.assertTrue(interval.overlapsWith(0, 3));
		Assertions.assertTrue(interval.overlapsWith(0, 20));
		Assertions.assertTrue(interval.overlapsWith(9, 10));

		Assertions.assertFalse(interval.overlapsWith(0, 1));
		Assertions.assertFalse(interval.overlapsWith(10, 11));
	}

}
