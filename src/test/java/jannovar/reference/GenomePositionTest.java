package jannovar.reference;

import org.junit.Assert;
import org.junit.Test;

public class GenomePositionTest {

	// TODO(holtgrew): Test conversion from forward to reverse strand.

	@Test
	public void testConstructorDefaultPositionType() {
		GenomePosition pos = new GenomePosition('+', 1, 23, PositionType.ONE_BASED);

		Assert.assertEquals(pos.getStrand(), '+');
		Assert.assertEquals(pos.getChr(), 1);
		Assert.assertEquals(pos.getPos(), 23);
		Assert.assertEquals(pos.getPositionType(), PositionType.ONE_BASED);
	}

	@Test
	public void testConstructorExplicitPositionType() {
		GenomePosition pos = new GenomePosition('+', 1, 23, PositionType.ZERO_BASED);

		Assert.assertEquals(pos.getStrand(), '+');
		Assert.assertEquals(pos.getChr(), 1);
		Assert.assertEquals(pos.getPos(), 23);
		Assert.assertEquals(pos.getPositionType(), PositionType.ZERO_BASED);
	}

	@Test
	public void testConstructorOneToZeroPositionType() {
		GenomePosition onePos = new GenomePosition('+', 1, 23, PositionType.ONE_BASED);
		GenomePosition zeroPos = new GenomePosition(onePos, PositionType.ZERO_BASED);

		Assert.assertEquals(zeroPos.getStrand(), '+');
		Assert.assertEquals(zeroPos.getChr(), 1);
		Assert.assertEquals(zeroPos.getPos(), 22);
		Assert.assertEquals(zeroPos.getPositionType(), PositionType.ZERO_BASED);
	}

	@Test
	public void testConstructorZeroToOnePositionType() {
		GenomePosition zeroPos = new GenomePosition('+', 1, 23, PositionType.ZERO_BASED);
		GenomePosition onePos = new GenomePosition(zeroPos, PositionType.ONE_BASED);

		Assert.assertEquals(onePos.getStrand(), '+');
		Assert.assertEquals(onePos.getChr(), 1);
		Assert.assertEquals(onePos.getPos(), 24);
		Assert.assertEquals(onePos.getPositionType(), PositionType.ONE_BASED);
	}

	@Test
	public void testForwardToReverseOneBased() {
		GenomePosition fwdPos = new GenomePosition('+', 1, 1000, PositionType.ONE_BASED);
		GenomePosition revPos = new GenomePosition(fwdPos, '-');

		Assert.assertEquals(revPos.getStrand(), '-');
		Assert.assertEquals(revPos.getChr(), 1);
		Assert.assertEquals(revPos.getPos(), 249249622);
		Assert.assertEquals(revPos.getPositionType(), PositionType.ONE_BASED);
	}

	@Test
	public void testForwardToForwardOneBased() {
		GenomePosition fwdPos = new GenomePosition('+', 1, 1000, PositionType.ONE_BASED);
		GenomePosition fwdPos2 = new GenomePosition(fwdPos, '+');

		Assert.assertEquals(fwdPos2.getStrand(), '+');
		Assert.assertEquals(fwdPos2.getChr(), 1);
		Assert.assertEquals(fwdPos2.getPos(), 1000);
		Assert.assertEquals(fwdPos2.getPositionType(), PositionType.ONE_BASED);
	}

	@Test
	public void testReverseToForwardOneBased() {
		GenomePosition revPos = new GenomePosition('-', 1, 1000, PositionType.ONE_BASED);
		GenomePosition fwdPos = new GenomePosition(revPos, '+');

		Assert.assertEquals(fwdPos.getStrand(), '+');
		Assert.assertEquals(fwdPos.getChr(), 1);
		Assert.assertEquals(fwdPos.getPos(), 249249622);
		Assert.assertEquals(fwdPos.getPositionType(), PositionType.ONE_BASED);
	}

	@Test
	public void testReverseToReverseOneBased() {
		GenomePosition revPos = new GenomePosition('-', 1, 1000, PositionType.ONE_BASED);
		GenomePosition revPos2 = new GenomePosition(revPos, '-');

		Assert.assertEquals(revPos2.getStrand(), '-');
		Assert.assertEquals(revPos2.getChr(), 1);
		Assert.assertEquals(revPos2.getPos(), 1000);
		Assert.assertEquals(revPos2.getPositionType(), PositionType.ONE_BASED);
	}

	@Test
	public void testForwardToReverseZeroBased() {
		GenomePosition fwdPos = new GenomePosition('+', 1, 1000, PositionType.ZERO_BASED);
		GenomePosition revPos = new GenomePosition(fwdPos, '-');

		Assert.assertEquals(revPos.getStrand(), '-');
		Assert.assertEquals(revPos.getChr(), 1);
		Assert.assertEquals(revPos.getPos(), 249249620);
		Assert.assertEquals(revPos.getPositionType(), PositionType.ZERO_BASED);
	}

	@Test
	public void testReverseToForwardZeroBased() {
		GenomePosition fwdPos = new GenomePosition('-', 1, 1000, PositionType.ZERO_BASED);
		GenomePosition fwdPos2 = new GenomePosition(fwdPos, '+');

		Assert.assertEquals(fwdPos2.getStrand(), '+');
		Assert.assertEquals(fwdPos2.getChr(), 1);
		Assert.assertEquals(fwdPos2.getPos(), 249249620);
		Assert.assertEquals(fwdPos2.getPositionType(), PositionType.ZERO_BASED);
	}

	@Test
	public void testForwardToReverseToToReverseZeroBased() {
		GenomePosition fwdPos = new GenomePosition('+', 1, 1000, PositionType.ZERO_BASED);
		GenomePosition fwdPos2 = fwdPos.withStrand('-').withStrand('+');

		Assert.assertEquals(fwdPos2.getStrand(), '+');
		Assert.assertEquals(fwdPos2.getChr(), 1);
		Assert.assertEquals(fwdPos2.getPos(), 1000);
		Assert.assertEquals(fwdPos2.getPositionType(), PositionType.ZERO_BASED);
	}

	@Test
	public void testReverseToForwardToReverseZeroBased() {
		GenomePosition fwdPos = new GenomePosition('-', 1, 1000, PositionType.ZERO_BASED);
		GenomePosition fwdPos2 = fwdPos.withStrand('+').withStrand('-');

		Assert.assertEquals(fwdPos2.getStrand(), '-');
		Assert.assertEquals(fwdPos2.getChr(), 1);
		Assert.assertEquals(fwdPos2.getPos(), 1000);
		Assert.assertEquals(fwdPos2.getPositionType(), PositionType.ZERO_BASED);
	}

	@Test
	public void testForwardToReverseToForwardOneBased() {
		GenomePosition fwdPos = new GenomePosition('+', 1, 1000, PositionType.ONE_BASED);
		GenomePosition fwdPos2 = fwdPos.withStrand('-').withStrand('+');

		Assert.assertEquals(fwdPos2.getStrand(), '+');
		Assert.assertEquals(fwdPos2.getChr(), 1);
		Assert.assertEquals(fwdPos2.getPos(), 1000);
		Assert.assertEquals(fwdPos2.getPositionType(), PositionType.ONE_BASED);
	}

	@Test
	public void testReverseToForwardToReverseOneBased() {
		GenomePosition fwdPos = new GenomePosition('-', 1, 1000, PositionType.ONE_BASED);
		GenomePosition fwdPos2 = fwdPos.withStrand('+').withStrand('-');

		Assert.assertEquals(fwdPos2.getStrand(), '-');
		Assert.assertEquals(fwdPos2.getChr(), 1);
		Assert.assertEquals(fwdPos2.getPos(), 1000);
		Assert.assertEquals(fwdPos2.getPositionType(), PositionType.ONE_BASED);
	}

	@Test
	public void testShiftRight() {
		GenomePosition pos = new GenomePosition('+', 1, 100);
		GenomePosition shifted = pos.shifted(10);

		Assert.assertEquals(shifted.getPos(), 110);
	}

	@Test
	public void testShiftLeft() {
		GenomePosition pos = new GenomePosition('+', 1, 100);
		GenomePosition shifted = pos.shifted(-10);

		Assert.assertEquals(shifted.getPos(), 90);
	}

	@Test
	public void testLt() {
		GenomePosition posL = new GenomePosition('+', 1, 100);
		GenomePosition posR = new GenomePosition('+', 1, 101);

		Assert.assertTrue(posL.isLt(posR));
		Assert.assertFalse(posL.isLt(posL));
		Assert.assertFalse(posR.isLt(posL));
	}

	@Test
	public void testLeq() {
		GenomePosition posL = new GenomePosition('+', 1, 100);
		GenomePosition posR = new GenomePosition('+', 1, 101);

		Assert.assertTrue(posL.isLeq(posR));
		Assert.assertTrue(posL.isLeq(posL));
		Assert.assertFalse(posR.isLeq(posL));
	}

	@Test
	public void testGt() {
		GenomePosition posL = new GenomePosition('+', 1, 100);
		GenomePosition posR = new GenomePosition('+', 1, 101);

		Assert.assertFalse(posL.isGt(posR));
		Assert.assertFalse(posL.isGt(posL));
		Assert.assertTrue(posR.isGt(posL));
	}

	@Test
	public void testGeq() {
		GenomePosition posL = new GenomePosition('+', 1, 100);
		GenomePosition posR = new GenomePosition('+', 1, 101);

		Assert.assertFalse(posL.isGeq(posR));
		Assert.assertTrue(posL.isGeq(posL));
		Assert.assertTrue(posR.isGeq(posL));
	}

	@Test
	public void testEq() {
		GenomePosition posL = new GenomePosition('+', 1, 100);
		GenomePosition posR = new GenomePosition('+', 1, 101);

		Assert.assertFalse(posL.isEq(posR));
		Assert.assertTrue(posL.isEq(posL));
		Assert.assertFalse(posR.isEq(posL));
	}
}
