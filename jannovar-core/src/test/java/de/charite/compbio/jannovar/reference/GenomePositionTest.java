package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.data.ReferenceDictionary;

public class GenomePositionTest {

	// TODO(holtgrew): Test conversion from forward to reverse strand.

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	@Test
	public void testConstructorDefaultPositionType() {
		GenomePosition pos = new GenomePosition(refDict, Strand.FWD, 1, 23, PositionType.ONE_BASED);

		Assert.assertEquals(pos.getStrand(), Strand.FWD);
		Assert.assertEquals(pos.getChr(), 1);
		Assert.assertEquals(pos.getPos(), 22);
	}

	@Test
	public void testConstructorExplicitPositionType() {
		GenomePosition pos = new GenomePosition(refDict, Strand.FWD, 1, 23, PositionType.ZERO_BASED);

		Assert.assertEquals(pos.getStrand(), Strand.FWD);
		Assert.assertEquals(pos.getChr(), 1);
		Assert.assertEquals(pos.getPos(), 23);
	}

	@Test
	public void testForwardToReverseZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, Strand.FWD, 1, 1000, PositionType.ZERO_BASED);
		GenomePosition revPos = new GenomePosition(fwdPos, Strand.REV);

		Assert.assertEquals(revPos.getStrand(), Strand.REV);
		Assert.assertEquals(revPos.getChr(), 1);
		Assert.assertEquals(revPos.getPos(), 249249620);
	}

	@Test
	public void testReverseToForwardZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, Strand.REV, 1, 1000, PositionType.ZERO_BASED);
		GenomePosition fwdPos2 = new GenomePosition(fwdPos, Strand.FWD);

		Assert.assertEquals(fwdPos2.getStrand(), Strand.FWD);
		Assert.assertEquals(fwdPos2.getChr(), 1);
		Assert.assertEquals(fwdPos2.getPos(), 249249620);
	}

	@Test
	public void testForwardToReverseToToReverseZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, Strand.FWD, 1, 1000, PositionType.ZERO_BASED);
		GenomePosition fwdPos2 = fwdPos.withStrand(Strand.REV).withStrand(Strand.FWD);

		Assert.assertEquals(fwdPos2.getStrand(), Strand.FWD);
		Assert.assertEquals(fwdPos2.getChr(), 1);
		Assert.assertEquals(fwdPos2.getPos(), 1000);
	}

	@Test
	public void testReverseToForwardToReverseZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, Strand.REV, 1, 1000, PositionType.ZERO_BASED);
		GenomePosition fwdPos2 = fwdPos.withStrand(Strand.FWD).withStrand(Strand.REV);

		Assert.assertEquals(fwdPos2.getStrand(), Strand.REV);
		Assert.assertEquals(fwdPos2.getChr(), 1);
		Assert.assertEquals(fwdPos2.getPos(), 1000);
	}

	@Test
	public void testShiftRight() {
		GenomePosition pos = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition shifted = pos.shifted(10);

		Assert.assertEquals(shifted.getPos(), 109);
	}

	@Test
	public void testShiftLeft() {
		GenomePosition pos = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition shifted = pos.shifted(-10);

		Assert.assertEquals(shifted.getPos(), 89);
	}

	@Test
	public void testLt() {
		GenomePosition posL = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.FWD, 1, 101, PositionType.ONE_BASED);

		Assert.assertTrue(posL.isLt(posR));
		Assert.assertFalse(posL.isLt(posL));
		Assert.assertFalse(posR.isLt(posL));
	}

	@Test
	public void testLeq() {
		GenomePosition posL = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.FWD, 1, 101, PositionType.ONE_BASED);

		Assert.assertTrue(posL.isLeq(posR));
		Assert.assertTrue(posL.isLeq(posL));
		Assert.assertFalse(posR.isLeq(posL));
	}

	@Test
	public void testGt() {
		GenomePosition posL = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.FWD, 1, 101, PositionType.ONE_BASED);

		Assert.assertFalse(posL.isGt(posR));
		Assert.assertFalse(posL.isGt(posL));
		Assert.assertTrue(posR.isGt(posL));
	}

	@Test
	public void testGeq() {
		GenomePosition posL = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.FWD, 1, 101, PositionType.ONE_BASED);

		Assert.assertFalse(posL.isGeq(posR));
		Assert.assertTrue(posL.isGeq(posL));
		Assert.assertTrue(posR.isGeq(posL));
	}

	@Test
	public void testEq() {
		GenomePosition posL = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.FWD, 1, 101, PositionType.ONE_BASED);

		Assert.assertFalse(posL.isEq(posR));
		Assert.assertTrue(posL.isEq(posL));
		Assert.assertFalse(posR.isEq(posL));
	}

	@Test
	public void testDifferenceToPositionForward() {
		GenomePosition posL = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.FWD, 1, 101, PositionType.ONE_BASED);

		Assert.assertEquals(posL.differenceTo(posR), -1);
		Assert.assertEquals(posL.differenceTo(posL), 0);
		Assert.assertEquals(posR.differenceTo(posL), 1);
	}

	@Test
	public void testDifferenceToPositionReverse() {
		GenomePosition posL = new GenomePosition(refDict, Strand.REV, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.REV, 1, 101, PositionType.ONE_BASED);

		Assert.assertEquals(posL.differenceTo(posR), -1);
		Assert.assertEquals(posL.differenceTo(posL), 0);
		Assert.assertEquals(posR.differenceTo(posL), 1);
	}

	@Test
	public void testDifferenceToIntervalForward() {
		GenomePosition posL = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.FWD, 1, 201, PositionType.ONE_BASED);
		GenomeInterval itv = new GenomeInterval(refDict, Strand.FWD, 1, 101, 200, PositionType.ONE_BASED);

		Assert.assertEquals(posL.differenceTo(itv), -1);
		Assert.assertEquals(posL.shifted(1).differenceTo(itv), 0);
		Assert.assertEquals(posR.differenceTo(itv), 1);
		Assert.assertEquals(posR.shifted(-1).differenceTo(itv), 0);
	}

	@Test
	public void testDifferenceToIntervalReverse() {
		GenomePosition posL = new GenomePosition(refDict, Strand.REV, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.REV, 1, 201, PositionType.ONE_BASED);
		GenomeInterval itv = new GenomeInterval(refDict, Strand.REV, 1, 101, 200, PositionType.ONE_BASED);

		Assert.assertEquals(posL.differenceTo(itv), -1);
		Assert.assertEquals(posL.shifted(1).differenceTo(itv), 0);
		Assert.assertEquals(posR.differenceTo(itv), 1);
		Assert.assertEquals(posR.shifted(-1).differenceTo(itv), 0);
	}
}
