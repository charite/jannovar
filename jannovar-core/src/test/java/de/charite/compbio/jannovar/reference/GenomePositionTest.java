package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.io.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.HG19RefDictBuilder;
import de.charite.compbio.jannovar.reference.PositionType;

public class GenomePositionTest {

	// TODO(holtgrew): Test conversion from forward to reverse strand.

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	@Test
	public void testConstructorDefaultPositionType() {
		GenomePosition pos = new GenomePosition(refDict, '+', 1, 23, PositionType.ONE_BASED);

		Assert.assertEquals(pos.strand, '+');
		Assert.assertEquals(pos.chr, 1);
		Assert.assertEquals(pos.pos, 22);
	}

	@Test
	public void testConstructorExplicitPositionType() {
		GenomePosition pos = new GenomePosition(refDict, '+', 1, 23, PositionType.ZERO_BASED);

		Assert.assertEquals(pos.strand, '+');
		Assert.assertEquals(pos.chr, 1);
		Assert.assertEquals(pos.pos, 23);
	}

	@Test
	public void testForwardToReverseZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, '+', 1, 1000, PositionType.ZERO_BASED);
		GenomePosition revPos = new GenomePosition(fwdPos, '-');

		Assert.assertEquals(revPos.strand, '-');
		Assert.assertEquals(revPos.chr, 1);
		Assert.assertEquals(revPos.pos, 249249620);
	}

	@Test
	public void testReverseToForwardZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, '-', 1, 1000, PositionType.ZERO_BASED);
		GenomePosition fwdPos2 = new GenomePosition(fwdPos, '+');

		Assert.assertEquals(fwdPos2.strand, '+');
		Assert.assertEquals(fwdPos2.chr, 1);
		Assert.assertEquals(fwdPos2.pos, 249249620);
	}

	@Test
	public void testForwardToReverseToToReverseZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, '+', 1, 1000, PositionType.ZERO_BASED);
		GenomePosition fwdPos2 = fwdPos.withStrand('-').withStrand('+');

		Assert.assertEquals(fwdPos2.strand, '+');
		Assert.assertEquals(fwdPos2.chr, 1);
		Assert.assertEquals(fwdPos2.pos, 1000);
	}

	@Test
	public void testReverseToForwardToReverseZeroBased() {
		GenomePosition fwdPos = new GenomePosition(refDict, '-', 1, 1000, PositionType.ZERO_BASED);
		GenomePosition fwdPos2 = fwdPos.withStrand('+').withStrand('-');

		Assert.assertEquals(fwdPos2.strand, '-');
		Assert.assertEquals(fwdPos2.chr, 1);
		Assert.assertEquals(fwdPos2.pos, 1000);
	}

	@Test
	public void testShiftRight() {
		GenomePosition pos = new GenomePosition(refDict, '+', 1, 100, PositionType.ONE_BASED);
		GenomePosition shifted = pos.shifted(10);

		Assert.assertEquals(shifted.pos, 109);
	}

	@Test
	public void testShiftLeft() {
		GenomePosition pos = new GenomePosition(refDict, '+', 1, 100, PositionType.ONE_BASED);
		GenomePosition shifted = pos.shifted(-10);

		Assert.assertEquals(shifted.pos, 89);
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
