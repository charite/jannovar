package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.data.ReferenceDictionary;

public class GenomeIntervalTest {

	// TODO(holtgrew): Test conversion from forward to reverse strand.

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	@Test
	public void testConstructorDefaultPositionType() {
		GenomeInterval interval = new GenomeInterval(refDict, Strand.FWD, 1, 23, 45, PositionType.ONE_BASED);
		Assert.assertEquals(interval.getChr(), 1);
		Assert.assertEquals(interval.getBeginPos(), 22);
		Assert.assertEquals(interval.getEndPos(), 45);
		Assert.assertEquals(interval.length(), 23);
	}

	@Test
	public void testConstructorExplicitPositionType() {
		GenomeInterval interval = new GenomeInterval(refDict, Strand.FWD, 1, 23, 45, PositionType.ZERO_BASED);
		Assert.assertEquals(interval.getChr(), 1);
		Assert.assertEquals(interval.getBeginPos(), 23);
		Assert.assertEquals(interval.getEndPos(), 45);
		Assert.assertEquals(interval.length(), 22);
	}

	@Test
	public void testGetGenomeBeginEndPosOneBased() {
		GenomeInterval interval = new GenomeInterval(refDict, Strand.FWD, 1, 23, 45, PositionType.ONE_BASED);
		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 1, 23, PositionType.ONE_BASED),
				interval.getGenomeBeginPos());
		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 1, 45, PositionType.ZERO_BASED),
				interval.getGenomeEndPos());
	}

	@Test
	public void testGetGenomeBeginEndPosZeroBased() {
		GenomeInterval interval = new GenomeInterval(refDict, Strand.FWD, 1, 23, 45, PositionType.ZERO_BASED);
		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 1, 23, PositionType.ZERO_BASED),
				interval.getGenomeBeginPos());
		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 1, 45, PositionType.ZERO_BASED),
				interval.getGenomeEndPos());
	}

	@Test
	public void testConstructForwardToReverseOneBased() {
		GenomeInterval fwdInterval = new GenomeInterval(refDict, Strand.FWD, 1, 1000, 1100, PositionType.ONE_BASED);
		GenomeInterval revInterval = new GenomeInterval(fwdInterval, Strand.REV);

		Assert.assertEquals(revInterval.getStrand(), Strand.REV);
		Assert.assertEquals(revInterval.getChr(), 1);
		Assert.assertEquals(revInterval.getBeginPos(), 249249521);
		Assert.assertEquals(revInterval.getEndPos(), 249249622);
		Assert.assertEquals(revInterval.length(), 101);

		// check contains() after flip
		Assert.assertFalse(revInterval
				.contains(new GenomePosition(refDict, Strand.FWD, 1, 999, PositionType.ONE_BASED)));
		Assert.assertTrue(revInterval
				.contains(new GenomePosition(refDict, Strand.FWD, 1, 1100, PositionType.ONE_BASED)));
		Assert.assertTrue(revInterval
				.contains(new GenomePosition(refDict, Strand.FWD, 1, 1000, PositionType.ONE_BASED)));
		Assert.assertFalse(revInterval.contains(new GenomePosition(refDict, Strand.FWD, 1, 1101,
				PositionType.ONE_BASED)));
	}

	@Test
	public void testConstructForwardToForwardOneBased() {
		GenomeInterval fwdInterval = new GenomeInterval(refDict, Strand.FWD, 1, 1000, 1100, PositionType.ONE_BASED);
		GenomeInterval fwdInterval2 = new GenomeInterval(fwdInterval, Strand.FWD);

		Assert.assertEquals(fwdInterval2.getStrand(), Strand.FWD);
		Assert.assertEquals(fwdInterval2.getChr(), 1);
		Assert.assertEquals(fwdInterval2.getBeginPos(), 999);
		Assert.assertEquals(fwdInterval2.getEndPos(), 1100);
		Assert.assertEquals(fwdInterval2.length(), 101);
	}

	@Test
	public void testConstructReverseToForwardOneBased() {
		GenomeInterval revInterval = new GenomeInterval(refDict, Strand.REV, 1, 1000, 1100, PositionType.ONE_BASED);
		GenomeInterval fwdInterval = new GenomeInterval(revInterval, Strand.FWD);

		Assert.assertEquals(fwdInterval.getStrand(), Strand.FWD);
		Assert.assertEquals(fwdInterval.getChr(), 1);
		Assert.assertEquals(fwdInterval.getBeginPos(), 249249521);
		Assert.assertEquals(fwdInterval.getEndPos(), 249249622);
		Assert.assertEquals(fwdInterval.length(), 101);

		// check contains() after flip
		Assert.assertFalse(revInterval.contains(new GenomePosition(refDict, Strand.REV, 1, 999,
				PositionType.ONE_BASED)));
		Assert.assertTrue(revInterval.contains(new GenomePosition(refDict, Strand.REV, 1, 1100,
				PositionType.ONE_BASED)));
		Assert.assertTrue(revInterval.contains(new GenomePosition(refDict, Strand.REV, 1, 1000,
				PositionType.ONE_BASED)));
		Assert.assertFalse(revInterval.contains(new GenomePosition(refDict, Strand.REV, 1, 1101,
				PositionType.ONE_BASED)));
	}

	@Test
	public void testConstructReverseToReverseOneBased() {
		GenomeInterval revInterval = new GenomeInterval(refDict, Strand.REV, 1, 1000, 1100, PositionType.ONE_BASED);
		GenomeInterval revInterval2 = new GenomeInterval(revInterval, Strand.REV);

		Assert.assertEquals(revInterval2.getStrand(), Strand.REV);
		Assert.assertEquals(revInterval2.getChr(), 1);
		Assert.assertEquals(revInterval2.getBeginPos(), 999);
		Assert.assertEquals(revInterval2.getEndPos(), 1100);
		Assert.assertEquals(revInterval2.length(), 101);
	}

	@Test
	public void testConstructForwardToReverseZeroBased() {
		GenomeInterval fwdInterval = new GenomeInterval(refDict, Strand.FWD, 1, 1000, 1100, PositionType.ZERO_BASED);
		GenomeInterval revInterval = new GenomeInterval(fwdInterval, Strand.REV);

		Assert.assertEquals(revInterval.getStrand(), Strand.REV);
		Assert.assertEquals(revInterval.getChr(), 1);
		Assert.assertEquals(revInterval.getBeginPos(), 249249521);
		Assert.assertEquals(revInterval.getEndPos(), 249249621);
		Assert.assertEquals(revInterval.length(), 100);

		// check contains() after flip
		Assert.assertFalse(revInterval.contains(new GenomePosition(refDict, Strand.FWD, 1, 999,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(revInterval.contains(new GenomePosition(refDict, Strand.FWD, 1, 1000,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(revInterval.contains(new GenomePosition(refDict, Strand.FWD, 1, 1099,
				PositionType.ZERO_BASED)));
		Assert.assertFalse(revInterval.contains(new GenomePosition(refDict, Strand.FWD, 1, 1100,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testConstructReverseToForwardZeroBased() {
		GenomeInterval revInterval = new GenomeInterval(refDict, Strand.REV, 1, 1000, 1100, PositionType.ZERO_BASED);
		GenomeInterval fwdInterval = new GenomeInterval(revInterval, Strand.FWD);

		Assert.assertEquals(fwdInterval.getStrand(), Strand.FWD);
		Assert.assertEquals(fwdInterval.getChr(), 1);
		Assert.assertEquals(fwdInterval.getBeginPos(), 249249521);
		Assert.assertEquals(fwdInterval.getEndPos(), 249249621);
		Assert.assertEquals(fwdInterval.length(), 100);

		// check contains() after flip
		Assert.assertFalse(fwdInterval.contains(new GenomePosition(refDict, Strand.REV, 1, 999,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(fwdInterval.contains(new GenomePosition(refDict, Strand.REV, 1, 1000,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(fwdInterval.contains(new GenomePosition(refDict, Strand.REV, 1, 1099,
				PositionType.ZERO_BASED)));
		Assert.assertFalse(fwdInterval.contains(new GenomePosition(refDict, Strand.REV, 1, 1100,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testIsLeftOf() {
		GenomeInterval interval = new GenomeInterval(refDict, Strand.FWD, 1, 1000, 1200, PositionType.ONE_BASED);
		Assert.assertTrue(interval.isRightOf(new GenomePosition(refDict, Strand.FWD, 1, 999, PositionType.ONE_BASED)));
		Assert.assertFalse(interval
				.isRightOf(new GenomePosition(refDict, Strand.FWD, 1, 1000, PositionType.ONE_BASED)));
	}

	@Test
	public void testIsRightOf() {
		GenomeInterval interval = new GenomeInterval(refDict, Strand.FWD, 1, 1000, 1200, PositionType.ONE_BASED);
		Assert.assertFalse(interval.isLeftOf(new GenomePosition(refDict, Strand.FWD, 1, 1200, PositionType.ONE_BASED)));
		Assert.assertTrue(interval.isLeftOf(new GenomePosition(refDict, Strand.FWD, 1, 1201, PositionType.ONE_BASED)));
	}

	@Test
	public void testContainsSameChrYes() {
		GenomeInterval interval = new GenomeInterval(refDict, Strand.FWD, 1, 1000, 1200, PositionType.ONE_BASED);
		Assert.assertTrue(interval.contains(new GenomePosition(refDict, Strand.FWD, 1, 1000, PositionType.ONE_BASED)));
		Assert.assertTrue(interval.contains(new GenomePosition(refDict, Strand.FWD, 1, 1200, PositionType.ONE_BASED)));
	}

	@Test
	public void testContainsSameChrNo() {
		GenomeInterval interval = new GenomeInterval(refDict, Strand.FWD, 1, 1000, 1200, PositionType.ONE_BASED);
		Assert.assertFalse(interval.contains(new GenomePosition(refDict, Strand.FWD, 1, 999, PositionType.ONE_BASED)));
		Assert.assertFalse(interval.contains(new GenomePosition(refDict, Strand.FWD, 1, 1201, PositionType.ONE_BASED)));
	}

	@Test
	public void testContainsDifferentChr() {
		GenomeInterval interval = new GenomeInterval(refDict, Strand.FWD, 1, 1000, 1200, PositionType.ONE_BASED);
		Assert.assertFalse(interval.contains(new GenomePosition(refDict, Strand.FWD, 2, 1100, PositionType.ONE_BASED)));
	}

	@Test
	public void testIntersection() {
		GenomeInterval intervalL = new GenomeInterval(refDict, Strand.FWD, 1, 1000, 1200, PositionType.ONE_BASED);
		GenomeInterval intervalR = new GenomeInterval(refDict, Strand.FWD, 1, 1100, 1300, PositionType.ONE_BASED);
		GenomeInterval intervalE = new GenomeInterval(refDict, Strand.FWD, 1, 1100, 1200, PositionType.ONE_BASED);
		Assert.assertEquals(intervalE, intervalL.intersection(intervalR));
		Assert.assertEquals(intervalE, intervalR.intersection(intervalL));
	}
}
