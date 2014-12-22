package jannovar.reference;

import jannovar.io.ReferenceDictionary;

import org.junit.Assert;
import org.junit.Test;

public class GenomeIntervalTest {

	// TODO(holtgrew): Test conversion from forward to reverse strand.

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	@Test
	public void testConstructorDefaultPositionType() {
		GenomeInterval interval = new GenomeInterval(refDict, '+', 1, 23, 45);
		Assert.assertEquals(interval.chr, 1);
		Assert.assertEquals(interval.beginPos, 23);
		Assert.assertEquals(interval.endPos, 45);
		Assert.assertEquals(interval.positionType, PositionType.ONE_BASED);
		Assert.assertEquals(interval.length(), 23);
	}

	@Test
	public void testConstructorExplicitPositionType() {
		GenomeInterval interval = new GenomeInterval(refDict, '+', 1, 23, 45, PositionType.ZERO_BASED);
		Assert.assertEquals(interval.chr, 1);
		Assert.assertEquals(interval.beginPos, 23);
		Assert.assertEquals(interval.endPos, 45);
		Assert.assertEquals(interval.positionType, PositionType.ZERO_BASED);
		Assert.assertEquals(interval.length(), 22);
	}

	@Test
	public void testGetGenomeBeginEndPosOneBased() {
		GenomeInterval interval = new GenomeInterval(refDict, '+', 1, 23, 45, PositionType.ONE_BASED);
		Assert.assertEquals(new GenomePosition(refDict, '+', 1, 23, PositionType.ONE_BASED),
				interval.getGenomeBeginPos());
		Assert.assertEquals(new GenomePosition(refDict, '+', 1, 45, PositionType.ONE_BASED), interval.getGenomeEndPos());
	}

	@Test
	public void testGetGenomeBeginEndPosZeroBased() {
		GenomeInterval interval = new GenomeInterval(refDict, '+', 1, 23, 45, PositionType.ZERO_BASED);
		Assert.assertEquals(new GenomePosition(refDict, '+', 1, 23, PositionType.ZERO_BASED),
				interval.getGenomeBeginPos());
		Assert.assertEquals(new GenomePosition(refDict, '+', 1, 45, PositionType.ZERO_BASED),
				interval.getGenomeEndPos());
	}

	@Test
	public void testConstructorOneToZeroPositionType() {
		GenomeInterval oneInterval = new GenomeInterval(refDict, '+', 1, 23, 45, PositionType.ONE_BASED);
		GenomeInterval zeroInterval = new GenomeInterval(oneInterval, PositionType.ZERO_BASED);

		Assert.assertEquals(zeroInterval.chr, 1);
		Assert.assertEquals(zeroInterval.beginPos, 22);
		Assert.assertEquals(zeroInterval.endPos, 45);
		Assert.assertEquals(zeroInterval.positionType, PositionType.ZERO_BASED);
		Assert.assertEquals(zeroInterval.length(), 23);
	}

	@Test
	public void testConstructorZeroToOnePositionType() {
		GenomeInterval zeroInterval = new GenomeInterval(refDict, '+', 1, 23, 45, PositionType.ZERO_BASED);
		GenomeInterval oneInterval = new GenomeInterval(zeroInterval, PositionType.ONE_BASED);

		Assert.assertEquals(oneInterval.chr, 1);
		Assert.assertEquals(oneInterval.beginPos, 24);
		Assert.assertEquals(oneInterval.endPos, 45);
		Assert.assertEquals(oneInterval.positionType, PositionType.ONE_BASED);
		Assert.assertEquals(oneInterval.length(), 22);
	}

	@Test
	public void testConstructForwardToReverseOneBased() {
		GenomeInterval fwdInterval = new GenomeInterval(refDict, '+', 1, 1000, 1100, PositionType.ONE_BASED);
		GenomeInterval revInterval = new GenomeInterval(fwdInterval, '-');

		Assert.assertEquals(revInterval.strand, '-');
		Assert.assertEquals(revInterval.chr, 1);
		Assert.assertEquals(revInterval.beginPos, 249249522);
		Assert.assertEquals(revInterval.endPos, 249249622);
		Assert.assertEquals(revInterval.positionType, PositionType.ONE_BASED);
		Assert.assertEquals(revInterval.length(), 101);

		// check contains() after flip
		Assert.assertFalse(revInterval.contains(new GenomePosition(refDict, '+', 1, 999)));
		Assert.assertTrue(revInterval.contains(new GenomePosition(refDict, '+', 1, 1100)));
		Assert.assertTrue(revInterval.contains(new GenomePosition(refDict, '+', 1, 1000)));
		Assert.assertFalse(revInterval.contains(new GenomePosition(refDict, '+', 1, 1101)));
	}

	@Test
	public void testConstructForwardToForwardOneBased() {
		GenomeInterval fwdInterval = new GenomeInterval(refDict, '+', 1, 1000, 1100, PositionType.ONE_BASED);
		GenomeInterval fwdInterval2 = new GenomeInterval(fwdInterval, '+');

		Assert.assertEquals(fwdInterval2.strand, '+');
		Assert.assertEquals(fwdInterval2.chr, 1);
		Assert.assertEquals(fwdInterval2.beginPos, 1000);
		Assert.assertEquals(fwdInterval2.endPos, 1100);
		Assert.assertEquals(fwdInterval2.positionType, PositionType.ONE_BASED);
		Assert.assertEquals(fwdInterval2.length(), 101);
	}

	@Test
	public void testConstructReverseToForwardOneBased() {
		GenomeInterval revInterval = new GenomeInterval(refDict, '-', 1, 1000, 1100, PositionType.ONE_BASED);
		GenomeInterval fwdInterval = new GenomeInterval(revInterval, '+');

		Assert.assertEquals(fwdInterval.strand, '+');
		Assert.assertEquals(fwdInterval.chr, 1);
		Assert.assertEquals(fwdInterval.beginPos, 249249522);
		Assert.assertEquals(fwdInterval.endPos, 249249622);
		Assert.assertEquals(fwdInterval.positionType, PositionType.ONE_BASED);
		Assert.assertEquals(fwdInterval.length(), 101);

		// check contains() after flip
		Assert.assertFalse(revInterval.contains(new GenomePosition(refDict, '-', 1, 999)));
		Assert.assertTrue(revInterval.contains(new GenomePosition(refDict, '-', 1, 1100)));
		Assert.assertTrue(revInterval.contains(new GenomePosition(refDict, '-', 1, 1000)));
		Assert.assertFalse(revInterval.contains(new GenomePosition(refDict, '-', 1, 1101)));
	}

	@Test
	public void testConstructReverseToReverseOneBased() {
		GenomeInterval revInterval = new GenomeInterval(refDict, '-', 1, 1000, 1100, PositionType.ONE_BASED);
		GenomeInterval revInterval2 = new GenomeInterval(revInterval, '-');

		Assert.assertEquals(revInterval2.strand, '-');
		Assert.assertEquals(revInterval2.chr, 1);
		Assert.assertEquals(revInterval2.beginPos, 1000);
		Assert.assertEquals(revInterval2.endPos, 1100);
		Assert.assertEquals(revInterval2.positionType, PositionType.ONE_BASED);
		Assert.assertEquals(revInterval2.length(), 101);
	}

	@Test
	public void testConstructForwardToReverseZeroBased() {
		GenomeInterval fwdInterval = new GenomeInterval(refDict, '+', 1, 1000, 1100, PositionType.ZERO_BASED);
		GenomeInterval revInterval = new GenomeInterval(fwdInterval, '-');

		Assert.assertEquals(revInterval.strand, '-');
		Assert.assertEquals(revInterval.chr, 1);
		Assert.assertEquals(revInterval.beginPos, 249249521);
		Assert.assertEquals(revInterval.endPos, 249249621);
		Assert.assertEquals(revInterval.positionType, PositionType.ZERO_BASED);
		Assert.assertEquals(revInterval.length(), 100);

		// check contains() after flip
		Assert.assertFalse(revInterval.contains(new GenomePosition(refDict, '+', 1, 999, PositionType.ZERO_BASED)));
		Assert.assertTrue(revInterval.contains(new GenomePosition(refDict, '+', 1, 1000, PositionType.ZERO_BASED)));
		Assert.assertTrue(revInterval.contains(new GenomePosition(refDict, '+', 1, 1099, PositionType.ZERO_BASED)));
		Assert.assertFalse(revInterval.contains(new GenomePosition(refDict, '+', 1, 1100, PositionType.ZERO_BASED)));
	}

	@Test
	public void testConstructReverseToForwardZeroBased() {
		GenomeInterval revInterval = new GenomeInterval(refDict, '-', 1, 1000, 1100, PositionType.ZERO_BASED);
		GenomeInterval fwdInterval = new GenomeInterval(revInterval, '+');

		Assert.assertEquals(fwdInterval.strand, '+');
		Assert.assertEquals(fwdInterval.chr, 1);
		Assert.assertEquals(fwdInterval.beginPos, 249249521);
		Assert.assertEquals(fwdInterval.endPos, 249249621);
		Assert.assertEquals(fwdInterval.positionType, PositionType.ZERO_BASED);
		Assert.assertEquals(fwdInterval.length(), 100);

		// check contains() after flip
		Assert.assertFalse(fwdInterval.contains(new GenomePosition(refDict, '-', 1, 999, PositionType.ZERO_BASED)));
		Assert.assertTrue(fwdInterval.contains(new GenomePosition(refDict, '-', 1, 1000, PositionType.ZERO_BASED)));
		Assert.assertTrue(fwdInterval.contains(new GenomePosition(refDict, '-', 1, 1099, PositionType.ZERO_BASED)));
		Assert.assertFalse(fwdInterval.contains(new GenomePosition(refDict, '-', 1, 1100, PositionType.ZERO_BASED)));
	}

	@Test
	public void testIsLeftOf() {
		GenomeInterval interval = new GenomeInterval(refDict, '+', 1, 1000, 1200);
		Assert.assertTrue(interval.isRightOf(new GenomePosition(refDict, '+', 1, 999)));
		Assert.assertFalse(interval.isRightOf(new GenomePosition(refDict, '+', 1, 1000)));
	}

	@Test
	public void testIsRightOf() {
		GenomeInterval interval = new GenomeInterval(refDict, '+', 1, 1000, 1200);
		Assert.assertFalse(interval.isLeftOf(new GenomePosition(refDict, '+', 1, 1200)));
		Assert.assertTrue(interval.isLeftOf(new GenomePosition(refDict, '+', 1, 1201)));
	}

	@Test
	public void testContainsSameChrYes() {
		GenomeInterval interval = new GenomeInterval(refDict, '+', 1, 1000, 1200);
		Assert.assertTrue(interval.contains(new GenomePosition(refDict, '+', 1, 1000)));
		Assert.assertTrue(interval.contains(new GenomePosition(refDict, '+', 1, 1200)));
	}

	@Test
	public void testContainsSameChrNo() {
		GenomeInterval interval = new GenomeInterval(refDict, '+', 1, 1000, 1200);
		Assert.assertFalse(interval.contains(new GenomePosition(refDict, '+', 1, 999)));
		Assert.assertFalse(interval.contains(new GenomePosition(refDict, '+', 1, 1201)));
	}

	@Test
	public void testContainsDifferentChr() {
		GenomeInterval interval = new GenomeInterval(refDict, '+', 1, 1000, 1200);
		Assert.assertFalse(interval.contains(new GenomePosition(refDict, '+', 2, 1100)));
	}

	@Test
	public void testIntersection() {
		GenomeInterval intervalL = new GenomeInterval(refDict, '+', 1, 1000, 1200);
		GenomeInterval intervalR = new GenomeInterval(refDict, '+', 1, 1100, 1300);
		GenomeInterval intervalE = new GenomeInterval(refDict, '+', 1, 1100, 1200);
		Assert.assertEquals(intervalE, intervalL.intersection(intervalR));
		Assert.assertEquals(intervalE, intervalR.intersection(intervalL));
	}
}
