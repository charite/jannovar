package jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GenomeChangeTest {

	GenomePosition genomePosOneBasedForward;
	GenomePosition genomePosZeroBasedForward;
	GenomePosition genomePosZeroBasedReverse;

	@Before
	public void setUp() {
		this.genomePosOneBasedForward = new GenomePosition('+', 1, 123, PositionType.ONE_BASED);
		this.genomePosZeroBasedForward = new GenomePosition('+', 1, 122, PositionType.ZERO_BASED);
		this.genomePosZeroBasedReverse = new GenomePosition('-', 1, 122, PositionType.ZERO_BASED);
	}

	@Test
	public void testConstructorNoUpdate() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "A", "C");
		Assert.assertEquals(this.genomePosOneBasedForward, change.getPos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandZeroRefBasesOneBased() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "", "C", '-');
		Assert.assertEquals(this.genomePosOneBasedForward.shifted(-1).withStrand('-'), change.getPos());
		Assert.assertEquals("", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandOneRefBaseOneBased() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "A", "C", '-');
		Assert.assertEquals(this.genomePosOneBasedForward.shifted(0).withStrand('-'), change.getPos());
		Assert.assertEquals("T", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandThreeRefBasesOneBased() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "AAA", "CCC", '-');
		Assert.assertEquals(this.genomePosOneBasedForward.shifted(2).withStrand('-'), change.getPos());
		Assert.assertEquals("TTT", change.getRef());
		Assert.assertEquals("GGG", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandZeroRefBasesZeroBased() {
		GenomeChange change = new GenomeChange(this.genomePosZeroBasedForward, "", "C", '-');
		Assert.assertEquals(this.genomePosZeroBasedForward.withStrand('-'), change.getPos());
		Assert.assertEquals("", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandOneRefBaseZeroBased() {
		GenomeChange change = new GenomeChange(this.genomePosZeroBasedForward, "A", "C", '-');
		Assert.assertEquals(this.genomePosZeroBasedForward.shifted(1).withStrand('-'), change.getPos());
		Assert.assertEquals("T", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandThreeRefBasesZeroBased() {
		GenomeChange change = new GenomeChange(this.genomePosZeroBasedForward, "AAA", "CCC", '-');
		Assert.assertEquals(this.genomePosZeroBasedForward.shifted(3).withStrand('-'), change.getPos());
		Assert.assertEquals("TTT", change.getRef());
		Assert.assertEquals("GGG", change.getAlt());
	}

	@Test
	public void testConstructorStripLeading() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "AAA", "AAC");
		GenomePosition expectedPos = new GenomePosition(this.genomePosOneBasedForward.getStrand(), this.genomePosOneBasedForward.getChr(),
				this.genomePosOneBasedForward.getPos() + 2);
		Assert.assertEquals(expectedPos, change.getPos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testConstructorStripTrailing() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "AGG", "CGG");
		Assert.assertEquals(this.genomePosOneBasedForward, change.getPos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testConstructorStripBoth() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "GGACC", "GGCCC");
		GenomePosition expectedPos = new GenomePosition(this.genomePosOneBasedForward.getStrand(), this.genomePosOneBasedForward.getChr(),
				this.genomePosOneBasedForward.getPos() + 2);
		Assert.assertEquals(expectedPos, change.getPos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testWithStrand() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "A", "C").withStrand('-');
		Assert.assertEquals(this.genomePosOneBasedForward.withStrand('-'), change.getPos());
		Assert.assertEquals("T", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testGetGenomeIntervalForward() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "A", "C");
		GenomeInterval genomeInterval = change.getGenomeInterval();
		GenomeInterval expectedInterval = new GenomeInterval('+', 1, 123, 123, PositionType.ONE_BASED);
		Assert.assertTrue(expectedInterval.equals(genomeInterval));
		Assert.assertEquals(expectedInterval, genomeInterval);
	}

	@Test
	public void testGetGenomeIntervalReverse() {
		GenomeChange change = new GenomeChange(this.genomePosZeroBasedReverse, "A", "C");
		GenomeInterval genomeInterval = change.getGenomeInterval();
		GenomeInterval expectedInterval = new GenomeInterval('-', 1, 122, 123, PositionType.ZERO_BASED);
		Assert.assertTrue(expectedInterval.equals(genomeInterval));
		Assert.assertEquals(expectedInterval, genomeInterval);
	}
}
