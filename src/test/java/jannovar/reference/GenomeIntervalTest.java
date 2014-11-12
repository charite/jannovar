package jannovar.reference;

import org.junit.Assert;
import org.junit.Test;

public class GenomeIntervalTest {

	// TODO(holtgrew): Test conversion from forward to reverse strand.

	@Test
	public void testConstructorDefaultPositionType() {
		GenomeInterval interval = new GenomeInterval('+', 1, 23, 45);
		Assert.assertEquals(interval.getChr(), 1);
		Assert.assertEquals(interval.getBeginPos(), 23);
		Assert.assertEquals(interval.getEndPos(), 45);
		Assert.assertEquals(interval.getPositionType(), PositionType.ONE_BASED);
		Assert.assertEquals(interval.length(), 23);
	}

	@Test
	public void testConstructorExplicitPositionType() {
		GenomeInterval interval = new GenomeInterval('+', 1, 23, 45, PositionType.ZERO_BASED);
		Assert.assertEquals(interval.getChr(), 1);
		Assert.assertEquals(interval.getBeginPos(), 23);
		Assert.assertEquals(interval.getEndPos(), 45);
		Assert.assertEquals(interval.getPositionType(), PositionType.ZERO_BASED);
		Assert.assertEquals(interval.length(), 22);
	}

	@Test
	public void testGetGenomeBeginEndPosOneBased() {
		GenomeInterval interval = new GenomeInterval('+', 1, 23, 45, PositionType.ONE_BASED);
		Assert.assertEquals(new GenomePosition('+', 1, 23, PositionType.ONE_BASED), interval.getGenomeBeginPos());
		Assert.assertEquals(new GenomePosition('+', 1, 45, PositionType.ONE_BASED), interval.getGenomeEndPos());
	}

	@Test
	public void testGetGenomeBeginEndPosZeroBased() {
		GenomeInterval interval = new GenomeInterval('+', 1, 23, 45, PositionType.ZERO_BASED);
		Assert.assertEquals(new GenomePosition('+', 1, 23, PositionType.ZERO_BASED), interval.getGenomeBeginPos());
		Assert.assertEquals(new GenomePosition('+', 1, 45, PositionType.ZERO_BASED), interval.getGenomeEndPos());
	}

	@Test
	public void testConstructorOneToZeroPositionType() {
		GenomeInterval oneInterval = new GenomeInterval('+', 1, 23, 45, PositionType.ONE_BASED);
		GenomeInterval zeroInterval = new GenomeInterval(oneInterval, PositionType.ZERO_BASED);

		Assert.assertEquals(zeroInterval.getChr(), 1);
		Assert.assertEquals(zeroInterval.getBeginPos(), 22);
		Assert.assertEquals(zeroInterval.getEndPos(), 45);
		Assert.assertEquals(zeroInterval.getPositionType(), PositionType.ZERO_BASED);
		Assert.assertEquals(zeroInterval.length(), 23);
	}

	@Test
	public void testConstructorZeroToOnePositionType() {
		GenomeInterval zeroInterval = new GenomeInterval('+', 1, 23, 45, PositionType.ZERO_BASED);
		GenomeInterval oneInterval = new GenomeInterval(zeroInterval, PositionType.ONE_BASED);

		Assert.assertEquals(oneInterval.getChr(), 1);
		Assert.assertEquals(oneInterval.getBeginPos(), 24);
		Assert.assertEquals(oneInterval.getEndPos(), 45);
		Assert.assertEquals(oneInterval.getPositionType(), PositionType.ONE_BASED);
		Assert.assertEquals(oneInterval.length(), 22);
	}

	@Test
	public void testConstructForwardToReverseOneBased() {
		GenomeInterval fwdInterval = new GenomeInterval('+', 1, 1000, 1100, PositionType.ONE_BASED);
		GenomeInterval revInterval = new GenomeInterval(fwdInterval, '-');

		Assert.assertEquals(revInterval.getStrand(), '-');
		Assert.assertEquals(revInterval.getChr(), 1);
		Assert.assertEquals(revInterval.getBeginPos(), 249249522);
		Assert.assertEquals(revInterval.getEndPos(), 249249622);
		Assert.assertEquals(revInterval.getPositionType(), PositionType.ONE_BASED);
		Assert.assertEquals(revInterval.length(), 101);

		// check contains() after flip
		Assert.assertFalse(revInterval.contains(new GenomePosition('+', 1, 999)));
		Assert.assertTrue(revInterval.contains(new GenomePosition('+', 1, 1100)));
		Assert.assertTrue(revInterval.contains(new GenomePosition('+', 1, 1000)));
		Assert.assertFalse(revInterval.contains(new GenomePosition('+', 1, 1101)));
	}

	@Test
	public void testConstructForwardToForwardOneBased() {
		GenomeInterval fwdInterval = new GenomeInterval('+', 1, 1000, 1100, PositionType.ONE_BASED);
		GenomeInterval fwdInterval2 = new GenomeInterval(fwdInterval, '+');

		Assert.assertEquals(fwdInterval2.getStrand(), '+');
		Assert.assertEquals(fwdInterval2.getChr(), 1);
		Assert.assertEquals(fwdInterval2.getBeginPos(), 1000);
		Assert.assertEquals(fwdInterval2.getEndPos(), 1100);
		Assert.assertEquals(fwdInterval2.getPositionType(), PositionType.ONE_BASED);
		Assert.assertEquals(fwdInterval2.length(), 101);
	}

	@Test
	public void testConstructReverseToForwardOneBased() {
		GenomeInterval revInterval = new GenomeInterval('-', 1, 1000, 1100, PositionType.ONE_BASED);
		GenomeInterval fwdInterval = new GenomeInterval(revInterval, '+');

		Assert.assertEquals(fwdInterval.getStrand(), '+');
		Assert.assertEquals(fwdInterval.getChr(), 1);
		Assert.assertEquals(fwdInterval.getBeginPos(), 249249522);
		Assert.assertEquals(fwdInterval.getEndPos(), 249249622);
		Assert.assertEquals(fwdInterval.getPositionType(), PositionType.ONE_BASED);
		Assert.assertEquals(fwdInterval.length(), 101);

		// check contains() after flip
		Assert.assertFalse(revInterval.contains(new GenomePosition('-', 1, 999)));
		Assert.assertTrue(revInterval.contains(new GenomePosition('-', 1, 1100)));
		Assert.assertTrue(revInterval.contains(new GenomePosition('-', 1, 1000)));
		Assert.assertFalse(revInterval.contains(new GenomePosition('-', 1, 1101)));
	}

	@Test
	public void testConstructReverseToReverseOneBased() {
		GenomeInterval revInterval = new GenomeInterval('-', 1, 1000, 1100, PositionType.ONE_BASED);
		GenomeInterval revInterval2 = new GenomeInterval(revInterval, '-');

		Assert.assertEquals(revInterval2.getStrand(), '-');
		Assert.assertEquals(revInterval2.getChr(), 1);
		Assert.assertEquals(revInterval2.getBeginPos(), 1000);
		Assert.assertEquals(revInterval2.getEndPos(), 1100);
		Assert.assertEquals(revInterval2.getPositionType(), PositionType.ONE_BASED);
		Assert.assertEquals(revInterval2.length(), 101);
	}

	@Test
	public void testConstructForwardToReverseZeroBased() {
		GenomeInterval fwdInterval = new GenomeInterval('+', 1, 999, 1100, PositionType.ZERO_BASED);
		GenomeInterval revInterval = new GenomeInterval(fwdInterval, '-');

		Assert.assertEquals(revInterval.getStrand(), '-');
		Assert.assertEquals(revInterval.getChr(), 1);
		Assert.assertEquals(revInterval.getBeginPos(), 249249521);
		Assert.assertEquals(revInterval.getEndPos(), 249249622);
		Assert.assertEquals(revInterval.getPositionType(), PositionType.ZERO_BASED);
		Assert.assertEquals(revInterval.length(), 101);

		// check contains() after flip
		Assert.assertFalse(revInterval.contains(new GenomePosition('+', 1, 999)));
		Assert.assertTrue(revInterval.contains(new GenomePosition('+', 1, 1100)));
		Assert.assertTrue(revInterval.contains(new GenomePosition('+', 1, 1000)));
		Assert.assertFalse(revInterval.contains(new GenomePosition('+', 1, 1101)));
	}

	@Test
	public void testConstructReverseToForwardZeroBased() {
		GenomeInterval revInterval = new GenomeInterval('-', 1, 999, 1100, PositionType.ZERO_BASED);
		GenomeInterval fwdInterval = new GenomeInterval(revInterval, '+');

		Assert.assertEquals(fwdInterval.getStrand(), '+');
		Assert.assertEquals(fwdInterval.getChr(), 1);
		Assert.assertEquals(fwdInterval.getBeginPos(), 249249521);
		Assert.assertEquals(fwdInterval.getEndPos(), 249249622);
		Assert.assertEquals(fwdInterval.getPositionType(), PositionType.ZERO_BASED);
		Assert.assertEquals(fwdInterval.length(), 101);

		// check contains() after flip
		Assert.assertFalse(revInterval.contains(new GenomePosition('-', 1, 999)));
		Assert.assertTrue(revInterval.contains(new GenomePosition('-', 1, 1100)));
		Assert.assertTrue(revInterval.contains(new GenomePosition('-', 1, 1000)));
		Assert.assertFalse(revInterval.contains(new GenomePosition('-', 1, 1101)));
	}

	@Test
	public void testIsLeftOf() {
		GenomeInterval interval = new GenomeInterval('+', 1, 1000, 1200);
		Assert.assertTrue(interval.isRightOf(new GenomePosition('+', 1, 999)));
		Assert.assertFalse(interval.isRightOf(new GenomePosition('+', 1, 1000)));
	}

	@Test
	public void testIsRightOf() {
		GenomeInterval interval = new GenomeInterval('+', 1, 1000, 1200);
		Assert.assertFalse(interval.isLeftOf(new GenomePosition('+', 1, 1200)));
		Assert.assertTrue(interval.isLeftOf(new GenomePosition('+', 1, 1201)));
	}

	@Test
	public void testContainsSameChrYes() {
		GenomeInterval interval = new GenomeInterval('+', 1, 1000, 1200);
		Assert.assertTrue(interval.contains(new GenomePosition('+', 1, 1000)));
		Assert.assertTrue(interval.contains(new GenomePosition('+', 1, 1200)));
	}

	@Test
	public void testContainsSameChrNo() {
		GenomeInterval interval = new GenomeInterval('+', 1, 1000, 1200);
		Assert.assertFalse(interval.contains(new GenomePosition('+', 1, 999)));
		Assert.assertFalse(interval.contains(new GenomePosition('+', 1, 1201)));
	}

	@Test
	public void testContainsDifferentChr() {
		GenomeInterval interval = new GenomeInterval('+', 1, 1000, 1200);
		Assert.assertFalse(interval.contains(new GenomePosition('+', 2, 1100)));
	}

	@Test
	public void testIntersection() {
		GenomeInterval intervalL = new GenomeInterval('+', 1, 1000, 1200);
		GenomeInterval intervalR = new GenomeInterval('+', 1, 1100, 1300);
		GenomeInterval intervalE = new GenomeInterval('+', 1, 1100, 1200);
		Assert.assertEquals(intervalE, intervalL.intersection(intervalR));
		Assert.assertEquals(intervalE, intervalR.intersection(intervalL));
	}
}
