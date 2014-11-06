package jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GenomeChangeTest {

	GenomePosition genomePosOneBased;
	GenomePosition genomePosZeroBased;

	@Before
	public void setUp() {
		this.genomePosOneBased = new GenomePosition('+', 1, 123, PositionType.ONE_BASED);
		this.genomePosZeroBased = new GenomePosition('+', 1, 122, PositionType.ZERO_BASED);
	}

	@Test
	public void testConstructorNoUpdate() {
		GenomeChange change = new GenomeChange(this.genomePosOneBased, "A", "C");
		Assert.assertEquals(this.genomePosOneBased, change.getPos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandZeroRefBasesOneBased() {
		GenomeChange change = new GenomeChange(this.genomePosOneBased, "", "C", '-');
		Assert.assertEquals(this.genomePosOneBased.shifted(-1).withStrand('-'), change.getPos());
		Assert.assertEquals("", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandOneRefBaseOneBased() {
		GenomeChange change = new GenomeChange(this.genomePosOneBased, "A", "C", '-');
		Assert.assertEquals(this.genomePosOneBased.shifted(0).withStrand('-'), change.getPos());
		Assert.assertEquals("T", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandThreeRefBasesOneBased() {
		GenomeChange change = new GenomeChange(this.genomePosOneBased, "AAA", "CCC", '-');
		Assert.assertEquals(this.genomePosOneBased.shifted(2).withStrand('-'), change.getPos());
		Assert.assertEquals("TTT", change.getRef());
		Assert.assertEquals("GGG", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandZeroRefBasesZeroBased() {
		GenomeChange change = new GenomeChange(this.genomePosZeroBased, "", "C", '-');
		Assert.assertEquals(this.genomePosZeroBased.withStrand('-'), change.getPos());
		Assert.assertEquals("", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandOneRefBaseZeroBased() {
		GenomeChange change = new GenomeChange(this.genomePosZeroBased, "A", "C", '-');
		Assert.assertEquals(this.genomePosZeroBased.shifted(1).withStrand('-'), change.getPos());
		Assert.assertEquals("T", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandThreeRefBasesZeroBased() {
		GenomeChange change = new GenomeChange(this.genomePosZeroBased, "AAA", "CCC", '-');
		Assert.assertEquals(this.genomePosZeroBased.shifted(3).withStrand('-'), change.getPos());
		Assert.assertEquals("TTT", change.getRef());
		Assert.assertEquals("GGG", change.getAlt());
	}

	@Test
	public void testConstructorStripLeading() {
		GenomeChange change = new GenomeChange(this.genomePosOneBased, "AAA", "AAC");
		GenomePosition expectedPos = new GenomePosition(this.genomePosOneBased.getStrand(), this.genomePosOneBased.getChr(),
				this.genomePosOneBased.getPos() + 2);
		Assert.assertEquals(expectedPos, change.getPos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testConstructorStripTrailing() {
		GenomeChange change = new GenomeChange(this.genomePosOneBased, "AGG", "CGG");
		Assert.assertEquals(this.genomePosOneBased, change.getPos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testConstructorStripBoth() {
		GenomeChange change = new GenomeChange(this.genomePosOneBased, "GGACC", "GGCCC");
		GenomePosition expectedPos = new GenomePosition(this.genomePosOneBased.getStrand(), this.genomePosOneBased.getChr(),
				this.genomePosOneBased.getPos() + 2);
		Assert.assertEquals(expectedPos, change.getPos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testWithStrand() {
		GenomeChange change = new GenomeChange(this.genomePosOneBased, "A", "C").withStrand('-');
		Assert.assertEquals(this.genomePosOneBased.withStrand('-'), change.getPos());
		Assert.assertEquals("T", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}
}
