package jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GenomeChangeTest {

	GenomePosition genomePos;

	@Before
	public void setUp() {
		this.genomePos = new GenomePosition('+', 1, 123);
	}

	@Test
	public void testNoUpdate() {
		GenomeChange change = new GenomeChange(this.genomePos, "A", "C");
		Assert.assertEquals(this.genomePos, change.getPos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testStripLeading() {
		GenomeChange change = new GenomeChange(this.genomePos, "AAA", "AAC");
		GenomePosition expectedPos = new GenomePosition(this.genomePos.getStrand(), this.genomePos.getChr(),
				this.genomePos.getPos() + 2);
		Assert.assertEquals(expectedPos, change.getPos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testStripTrailing() {
		GenomeChange change = new GenomeChange(this.genomePos, "AGG", "CGG");
		Assert.assertEquals(this.genomePos, change.getPos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testStripBoth() {
		GenomeChange change = new GenomeChange(this.genomePos, "GGACC", "GGCCC");
		GenomePosition expectedPos = new GenomePosition(this.genomePos.getStrand(), this.genomePos.getChr(),
				this.genomePos.getPos() + 2);
		Assert.assertEquals(expectedPos, change.getPos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

}
