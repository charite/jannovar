package de.charite.compbio.jannovar.impl.intervals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.impl.intervals.Interval;

public class IntervalTest {

	Interval<String> interval;

	@Before
	public void setUp() throws Exception {
		interval = new Interval<String>(1, 10, "x", 13);
	}

	@Test
	public void testValues() {
		Assert.assertEquals(1, interval.getBegin());
		Assert.assertEquals(10, interval.getEnd());
		Assert.assertEquals(13, interval.getMaxEnd());
		Assert.assertEquals("x", interval.getValue());
	}

	@Test
	public void testAllLeftOf() {
		Assert.assertFalse(interval.allLeftOf(10));
		Assert.assertFalse(interval.allLeftOf(12));
		Assert.assertTrue(interval.allLeftOf(13));
		Assert.assertTrue(interval.allLeftOf(14));
	}

	@Test
	public void testCompareTo() {
		Assert.assertTrue(interval.compareTo(new Interval<String>(0, 10, "y", 10)) > 0);
		Assert.assertTrue(interval.compareTo(new Interval<String>(1, 9, "y", 10)) > 0);
		Assert.assertTrue(interval.compareTo(new Interval<String>(1, 10, "y", 10)) == 0);
		Assert.assertTrue(interval.compareTo(new Interval<String>(1, 11, "y", 10)) < 0);
		Assert.assertTrue(interval.compareTo(new Interval<String>(2, 10, "y", 10)) < 0);
	}

	@Test
	public void testContains() {
		Assert.assertFalse(interval.contains(0));
		Assert.assertTrue(interval.contains(1));
		Assert.assertTrue(interval.contains(2));
		Assert.assertTrue(interval.contains(9));
		Assert.assertFalse(interval.contains(10));
	}

	@Test
	public void testIsLeftOf() {
		Assert.assertFalse(interval.isLeftOf(8));
		Assert.assertFalse(interval.isLeftOf(9));
		Assert.assertTrue(interval.isLeftOf(10));
		Assert.assertTrue(interval.isLeftOf(11));
	}

	@Test
	public void testIsRightOf() {
		Assert.assertTrue(interval.isRightOf(-1));
		Assert.assertTrue(interval.isRightOf(0));
		Assert.assertFalse(interval.isRightOf(1));
		Assert.assertFalse(interval.isRightOf(2));
	}

	@Test
	public void overlapsWith() {
		Assert.assertTrue(interval.overlapsWith(0, 2));
		Assert.assertTrue(interval.overlapsWith(0, 3));
		Assert.assertTrue(interval.overlapsWith(0, 20));
		Assert.assertTrue(interval.overlapsWith(9, 10));

		Assert.assertFalse(interval.overlapsWith(0, 1));
		Assert.assertFalse(interval.overlapsWith(10, 11));
	}

}
