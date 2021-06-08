package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GenomePositionTest {

	// TODO(holtgrew): Test conversion from forward to reverse strand.

	/**
	 * this test uses this static hg19 reference dictionary
	 */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	@Test
	public void testConstructorDefaultPositionType() {
		GenomePosition pos = new GenomePosition(refDict, Strand.FWD, 1, 23, PositionType.ONE_BASED);

		Assertions.assertEquals(pos.getStrand(), Strand.FWD);
		Assertions.assertEquals(pos.getChr(), 1);
		Assertions.assertEquals(pos.getPos(), 22);
	}

	@Test
	public void testConstructorExplicitPositionType() {
		GenomePosition pos = new GenomePosition(refDict, Strand.FWD, 1, 23, PositionType.ZERO_BASED);

		Assertions.assertEquals(pos.getStrand(), Strand.FWD);
		Assertions.assertEquals(pos.getChr(), 1);
		Assertions.assertEquals(pos.getPos(), 23);
	}

	@Test
	public void testForwardToReverseZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, Strand.FWD, 1, 1000, PositionType.ZERO_BASED);
		GenomePosition revPos = new GenomePosition(fwdPos, Strand.REV);

		Assertions.assertEquals(revPos.getStrand(), Strand.REV);
		Assertions.assertEquals(revPos.getChr(), 1);
		Assertions.assertEquals(revPos.getPos(), 249249620);
	}

	@Test
	public void testReverseToForwardZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, Strand.REV, 1, 1000, PositionType.ZERO_BASED);
		GenomePosition fwdPos2 = new GenomePosition(fwdPos, Strand.FWD);

		Assertions.assertEquals(fwdPos2.getStrand(), Strand.FWD);
		Assertions.assertEquals(fwdPos2.getChr(), 1);
		Assertions.assertEquals(fwdPos2.getPos(), 249249620);
	}

	@Test
	public void testForwardToReverseToToReverseZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, Strand.FWD, 1, 1000, PositionType.ZERO_BASED);
		GenomePosition fwdPos2 = fwdPos.withStrand(Strand.REV).withStrand(Strand.FWD);

		Assertions.assertEquals(fwdPos2.getStrand(), Strand.FWD);
		Assertions.assertEquals(fwdPos2.getChr(), 1);
		Assertions.assertEquals(fwdPos2.getPos(), 1000);
	}

	@Test
	public void testReverseToForwardToReverseZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, Strand.REV, 1, 1000, PositionType.ZERO_BASED);
		GenomePosition fwdPos2 = fwdPos.withStrand(Strand.FWD).withStrand(Strand.REV);

		Assertions.assertEquals(fwdPos2.getStrand(), Strand.REV);
		Assertions.assertEquals(fwdPos2.getChr(), 1);
		Assertions.assertEquals(fwdPos2.getPos(), 1000);
	}

	@Test
	public void testShiftRight() {
		GenomePosition pos = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition shifted = pos.shifted(10);

		Assertions.assertEquals(shifted.getPos(), 109);
	}

	@Test
	public void testShiftLeft() {
		GenomePosition pos = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition shifted = pos.shifted(-10);

		Assertions.assertEquals(shifted.getPos(), 89);
	}

	@Test
	public void testLt() {
		GenomePosition posL = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.FWD, 1, 101, PositionType.ONE_BASED);

		Assertions.assertTrue(posL.isLt(posR));
		Assertions.assertFalse(posL.isLt(posL));
		Assertions.assertFalse(posR.isLt(posL));
	}

	@Test
	public void testLeq() {
		GenomePosition posL = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.FWD, 1, 101, PositionType.ONE_BASED);

		Assertions.assertTrue(posL.isLeq(posR));
		Assertions.assertTrue(posL.isLeq(posL));
		Assertions.assertFalse(posR.isLeq(posL));
	}

	@Test
	public void testGt() {
		GenomePosition posL = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.FWD, 1, 101, PositionType.ONE_BASED);

		Assertions.assertFalse(posL.isGt(posR));
		Assertions.assertFalse(posL.isGt(posL));
		Assertions.assertTrue(posR.isGt(posL));
	}

	@Test
	public void testGeq() {
		GenomePosition posL = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.FWD, 1, 101, PositionType.ONE_BASED);

		Assertions.assertFalse(posL.isGeq(posR));
		Assertions.assertTrue(posL.isGeq(posL));
		Assertions.assertTrue(posR.isGeq(posL));
	}

	@Test
	public void testEq() {
		GenomePosition posL = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.FWD, 1, 101, PositionType.ONE_BASED);

		Assertions.assertFalse(posL.isEq(posR));
		Assertions.assertTrue(posL.isEq(posL));
		Assertions.assertFalse(posR.isEq(posL));
	}

	@Test
	public void testDifferenceToPositionForward() {
		GenomePosition posL = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.FWD, 1, 101, PositionType.ONE_BASED);

		Assertions.assertEquals(posL.differenceTo(posR), -1);
		Assertions.assertEquals(posL.differenceTo(posL), 0);
		Assertions.assertEquals(posR.differenceTo(posL), 1);
	}

	@Test
	public void testDifferenceToPositionReverse() {
		GenomePosition posL = new GenomePosition(refDict, Strand.REV, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.REV, 1, 101, PositionType.ONE_BASED);

		Assertions.assertEquals(posL.differenceTo(posR), -1);
		Assertions.assertEquals(posL.differenceTo(posL), 0);
		Assertions.assertEquals(posR.differenceTo(posL), 1);
	}

	@Test
	public void testDifferenceToIntervalForward() {
		GenomePosition posL = new GenomePosition(refDict, Strand.FWD, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.FWD, 1, 201, PositionType.ONE_BASED);
		GenomeInterval itv = new GenomeInterval(refDict, Strand.FWD, 1, 101, 200, PositionType.ONE_BASED);

		Assertions.assertEquals(posL.differenceTo(itv), -1);
		Assertions.assertEquals(posL.shifted(1).differenceTo(itv), 0);
		Assertions.assertEquals(posR.differenceTo(itv), 1);
		Assertions.assertEquals(posR.shifted(-1).differenceTo(itv), 0);
	}

	@Test
	public void testDifferenceToIntervalReverse() {
		GenomePosition posL = new GenomePosition(refDict, Strand.REV, 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, Strand.REV, 1, 201, PositionType.ONE_BASED);
		GenomeInterval itv = new GenomeInterval(refDict, Strand.REV, 1, 101, 200, PositionType.ONE_BASED);

		Assertions.assertEquals(posL.differenceTo(itv), -1);
		Assertions.assertEquals(posL.shifted(1).differenceTo(itv), 0);
		Assertions.assertEquals(posR.differenceTo(itv), 1);
		Assertions.assertEquals(posR.shifted(-1).differenceTo(itv), 0);
	}
}
