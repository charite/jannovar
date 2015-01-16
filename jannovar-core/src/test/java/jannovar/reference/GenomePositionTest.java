package jannovar.reference;

import jannovar.io.ReferenceDictionary;

import org.junit.Assert;
import org.junit.Test;

public class GenomePositionTest {

	// TODO(holtgrew): Test conversion from forward to reverse strand.

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	@Test
	public void testConstructorDefaultPositionType() {
		GenomePosition pos = new GenomePosition(refDict, '+', 1, 23, PositionType.ONE_BASED);

		Assert.assertEquals(pos.strand, '+');
		Assert.assertEquals(pos.chr, 1);
		Assert.assertEquals(pos.pos, 23);
		Assert.assertEquals(pos.positionType, PositionType.ONE_BASED);
	}

	@Test
	public void testConstructorExplicitPositionType() {
		GenomePosition pos = new GenomePosition(refDict, '+', 1, 23, PositionType.ZERO_BASED);

		Assert.assertEquals(pos.strand, '+');
		Assert.assertEquals(pos.chr, 1);
		Assert.assertEquals(pos.pos, 23);
		Assert.assertEquals(pos.positionType, PositionType.ZERO_BASED);
	}

	@Test
	public void testConstructorOneToZeroPositionType() {
		GenomePosition onePos = new GenomePosition(refDict, '+', 1, 23, PositionType.ONE_BASED);
		GenomePosition zeroPos = new GenomePosition(onePos, PositionType.ZERO_BASED);

		Assert.assertEquals(zeroPos.strand, '+');
		Assert.assertEquals(zeroPos.chr, 1);
		Assert.assertEquals(zeroPos.pos, 22);
		Assert.assertEquals(zeroPos.positionType, PositionType.ZERO_BASED);
	}

	@Test
	public void testConstructorZeroToOnePositionType() {
		GenomePosition zeroPos = new GenomePosition(refDict, '+', 1, 23, PositionType.ZERO_BASED);
		GenomePosition onePos = new GenomePosition(zeroPos, PositionType.ONE_BASED);

		Assert.assertEquals(onePos.strand, '+');
		Assert.assertEquals(onePos.chr, 1);
		Assert.assertEquals(onePos.pos, 24);
		Assert.assertEquals(onePos.positionType, PositionType.ONE_BASED);
	}

	@Test
	public void testForwardToReverseOneBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, '+', 1, 1000, PositionType.ONE_BASED);
		GenomePosition revPos = new GenomePosition(fwdPos, '-');

		Assert.assertEquals(revPos.strand, '-');
		Assert.assertEquals(revPos.chr, 1);
		Assert.assertEquals(revPos.pos, 249249622);
		Assert.assertEquals(revPos.positionType, PositionType.ONE_BASED);
	}

	@Test
	public void testForwardToForwardOneBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, '+', 1, 1000, PositionType.ONE_BASED);
		GenomePosition fwdPos2 = new GenomePosition(fwdPos, '+');

		Assert.assertEquals(fwdPos2.strand, '+');
		Assert.assertEquals(fwdPos2.chr, 1);
		Assert.assertEquals(fwdPos2.pos, 1000);
		Assert.assertEquals(fwdPos2.positionType, PositionType.ONE_BASED);
	}

	@Test
	public void testReverseToForwardOneBased() {
		GenomePosition revPos = new GenomePosition(refDict, '-', 1, 1000, PositionType.ONE_BASED);
		GenomePosition fwdPos = new GenomePosition(revPos, '+');

		Assert.assertEquals(fwdPos.strand, '+');
		Assert.assertEquals(fwdPos.chr, 1);
		Assert.assertEquals(fwdPos.pos, 249249622);
		Assert.assertEquals(fwdPos.positionType, PositionType.ONE_BASED);
	}

	@Test
	public void testReverseToReverseOneBased() {
		GenomePosition revPos = new GenomePosition(refDict, '-', 1, 1000, PositionType.ONE_BASED);
		GenomePosition revPos2 = new GenomePosition(revPos, '-');

		Assert.assertEquals(revPos2.strand, '-');
		Assert.assertEquals(revPos2.chr, 1);
		Assert.assertEquals(revPos2.pos, 1000);
		Assert.assertEquals(revPos2.positionType, PositionType.ONE_BASED);
	}

	@Test
	public void testForwardToReverseZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, '+', 1, 1000, PositionType.ZERO_BASED);
		GenomePosition revPos = new GenomePosition(fwdPos, '-');

		Assert.assertEquals(revPos.strand, '-');
		Assert.assertEquals(revPos.chr, 1);
		Assert.assertEquals(revPos.pos, 249249620);
		Assert.assertEquals(revPos.positionType, PositionType.ZERO_BASED);
	}

	@Test
	public void testReverseToForwardZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, '-', 1, 1000, PositionType.ZERO_BASED);
		GenomePosition fwdPos2 = new GenomePosition(fwdPos, '+');

		Assert.assertEquals(fwdPos2.strand, '+');
		Assert.assertEquals(fwdPos2.chr, 1);
		Assert.assertEquals(fwdPos2.pos, 249249620);
		Assert.assertEquals(fwdPos2.positionType, PositionType.ZERO_BASED);
	}

	@Test
	public void testForwardToReverseToToReverseZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, '+', 1, 1000, PositionType.ZERO_BASED);
		GenomePosition fwdPos2 = fwdPos.withStrand('-').withStrand('+');

		Assert.assertEquals(fwdPos2.strand, '+');
		Assert.assertEquals(fwdPos2.chr, 1);
		Assert.assertEquals(fwdPos2.pos, 1000);
		Assert.assertEquals(fwdPos2.positionType, PositionType.ZERO_BASED);
	}

	@Test
	public void testReverseToForwardToReverseZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, '-', 1, 1000, PositionType.ZERO_BASED);
		GenomePosition fwdPos2 = fwdPos.withStrand('+').withStrand('-');

		Assert.assertEquals(fwdPos2.strand, '-');
		Assert.assertEquals(fwdPos2.chr, 1);
		Assert.assertEquals(fwdPos2.pos, 1000);
		Assert.assertEquals(fwdPos2.positionType, PositionType.ZERO_BASED);
	}

	@Test
	public void testForwardToReverseToForwardOneBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, '+', 1, 1000, PositionType.ONE_BASED);
		GenomePosition fwdPos2 = fwdPos.withStrand('-').withStrand('+');

		Assert.assertEquals(fwdPos2.strand, '+');
		Assert.assertEquals(fwdPos2.chr, 1);
		Assert.assertEquals(fwdPos2.pos, 1000);
		Assert.assertEquals(fwdPos2.positionType, PositionType.ONE_BASED);
	}

	@Test
	public void testReverseToForwardToReverseOneBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, '-', 1, 1000, PositionType.ONE_BASED);
		GenomePosition fwdPos2 = fwdPos.withStrand('+').withStrand('-');

		Assert.assertEquals(fwdPos2.strand, '-');
		Assert.assertEquals(fwdPos2.chr, 1);
		Assert.assertEquals(fwdPos2.pos, 1000);
		Assert.assertEquals(fwdPos2.positionType, PositionType.ONE_BASED);
	}

	@Test
	public void testShiftRight() {
		GenomePosition pos = new GenomePosition(refDict, '+', 1, 100, PositionType.ONE_BASED);
		GenomePosition shifted = pos.shifted(10);

		Assert.assertEquals(shifted.pos, 110);
	}

	@Test
	public void testShiftLeft() {
		GenomePosition pos = new GenomePosition(refDict, '+', 1, 100, PositionType.ONE_BASED);
		GenomePosition shifted = pos.shifted(-10);

		Assert.assertEquals(shifted.pos, 90);
	}

	@Test
	public void testLt() {
		GenomePosition posL = new GenomePosition(refDict, '+', 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, '+', 1, 101, PositionType.ONE_BASED);

		Assert.assertTrue(posL.isLt(posR));
		Assert.assertFalse(posL.isLt(posL));
		Assert.assertFalse(posR.isLt(posL));
	}

	@Test
	public void testLeq() {
		GenomePosition posL = new GenomePosition(refDict, '+', 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, '+', 1, 101, PositionType.ONE_BASED);

		Assert.assertTrue(posL.isLeq(posR));
		Assert.assertTrue(posL.isLeq(posL));
		Assert.assertFalse(posR.isLeq(posL));
	}

	@Test
	public void testGt() {
		GenomePosition posL = new GenomePosition(refDict, '+', 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, '+', 1, 101, PositionType.ONE_BASED);

		Assert.assertFalse(posL.isGt(posR));
		Assert.assertFalse(posL.isGt(posL));
		Assert.assertTrue(posR.isGt(posL));
	}

	@Test
	public void testGeq() {
		GenomePosition posL = new GenomePosition(refDict, '+', 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, '+', 1, 101, PositionType.ONE_BASED);

		Assert.assertFalse(posL.isGeq(posR));
		Assert.assertTrue(posL.isGeq(posL));
		Assert.assertTrue(posR.isGeq(posL));
	}

	@Test
	public void testEq() {
		GenomePosition posL = new GenomePosition(refDict, '+', 1, 100, PositionType.ONE_BASED);
		GenomePosition posR = new GenomePosition(refDict, '+', 1, 101, PositionType.ONE_BASED);

		Assert.assertFalse(posL.isEq(posR));
		Assert.assertTrue(posL.isEq(posL));
		Assert.assertFalse(posR.isEq(posL));
	}
}
