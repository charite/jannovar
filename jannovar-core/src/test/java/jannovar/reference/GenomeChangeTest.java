package jannovar.reference;

import jannovar.io.ReferenceDictionary;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GenomeChangeTest {

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	GenomePosition genomePosOneBasedForward;
	GenomePosition genomePosZeroBasedForward;
	GenomePosition genomePosZeroBasedReverse;

	@Before
	public void setUp() {
		this.genomePosOneBasedForward = new GenomePosition(refDict, '+', 1, 123, PositionType.ONE_BASED);
		this.genomePosZeroBasedForward = new GenomePosition(refDict, '+', 1, 122, PositionType.ZERO_BASED);
		this.genomePosZeroBasedReverse = new GenomePosition(refDict, '-', 1, 122, PositionType.ZERO_BASED);
	}

	@Test
	public void testConstructorNoUpdate() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "A", "C");
		Assert.assertEquals(this.genomePosOneBasedForward, change.pos);
		Assert.assertEquals("A", change.ref);
		Assert.assertEquals("C", change.alt);
	}

	@Test
	public void testConstructorChangeStrandZeroRefBasesOneBased() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "", "C", '-');
		Assert.assertEquals(this.genomePosOneBasedForward.shifted(-1).withStrand('-'), change.pos);
		Assert.assertEquals("", change.ref);
		Assert.assertEquals("G", change.alt);
	}

	@Test
	public void testConstructorChangeStrandOneRefBaseOneBased() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "A", "C", '-');
		Assert.assertEquals(this.genomePosOneBasedForward.shifted(0).withStrand('-'), change.pos);
		Assert.assertEquals("T", change.ref);
		Assert.assertEquals("G", change.alt);
	}

	@Test
	public void testConstructorChangeStrandThreeRefBasesOneBased() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "AAA", "CCC", '-');
		Assert.assertEquals(this.genomePosOneBasedForward.shifted(2).withStrand('-'), change.pos);
		Assert.assertEquals("TTT", change.ref);
		Assert.assertEquals("GGG", change.alt);
	}

	@Test
	public void testConstructorChangeStrandZeroRefBasesZeroBased() {
		GenomeChange change = new GenomeChange(this.genomePosZeroBasedForward, "", "C", '-');
		Assert.assertEquals(this.genomePosZeroBasedForward.shifted(-1).withStrand('-'), change.pos);
		Assert.assertEquals("", change.ref);
		Assert.assertEquals("G", change.alt);
	}

	@Test
	public void testConstructorChangeStrandOneRefBaseZeroBased() {
		GenomeChange change = new GenomeChange(this.genomePosZeroBasedForward, "A", "C", '-');
		Assert.assertEquals(this.genomePosZeroBasedForward.shifted(0).withStrand('-'), change.pos);
		Assert.assertEquals("T", change.ref);
		Assert.assertEquals("G", change.alt);
	}

	@Test
	public void testConstructorChangeStrandThreeRefBasesZeroBased() {
		GenomeChange change = new GenomeChange(this.genomePosZeroBasedForward, "AAA", "CCC", '-');
		Assert.assertEquals(this.genomePosZeroBasedForward.shifted(2).withStrand('-'), change.pos);
		Assert.assertEquals("TTT", change.ref);
		Assert.assertEquals("GGG", change.alt);
	}

	@Test
	public void testConstructorStripLeading() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "AAA", "AAC");
		GenomePosition expectedPos = new GenomePosition(refDict, this.genomePosOneBasedForward.strand,
				this.genomePosOneBasedForward.chr, this.genomePosOneBasedForward.pos + 2, PositionType.ZERO_BASED);
		Assert.assertEquals(expectedPos, change.pos);
		Assert.assertEquals("A", change.ref);
		Assert.assertEquals("C", change.alt);
	}

	@Test
	public void testConstructorStripTrailing() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "AGG", "CGG");
		Assert.assertEquals(this.genomePosOneBasedForward, change.pos);
		Assert.assertEquals("A", change.ref);
		Assert.assertEquals("C", change.alt);
	}

	@Test
	public void testConstructorStripBoth() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "GGACC", "GGCCC");
		GenomePosition expectedPos = new GenomePosition(refDict, this.genomePosOneBasedForward.strand,
				this.genomePosOneBasedForward.chr, this.genomePosOneBasedForward.pos + 2, PositionType.ZERO_BASED);
		Assert.assertEquals(expectedPos, change.pos);
		Assert.assertEquals("A", change.ref);
		Assert.assertEquals("C", change.alt);
	}

	@Test
	public void testWithStrandZeroBases() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "", "C").withStrand('-');
		GenomePosition expected = this.genomePosOneBasedForward.shifted(-1);
		GenomePosition actual = change.pos;
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("", change.ref);
		Assert.assertEquals("G", change.alt);
	}

	@Test
	public void testWithStrandOneBase() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "A", "C").withStrand('-');
		GenomePosition expected = this.genomePosOneBasedForward.shifted(0);
		GenomePosition actual = change.pos;
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("T", change.ref);
		Assert.assertEquals("G", change.alt);
	}

	@Test
	public void testWithStrandTwoBases() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "AA", "C").withStrand('-');
		GenomePosition expected = this.genomePosOneBasedForward.shifted(1);
		GenomePosition actual = change.pos;
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("TT", change.ref);
		Assert.assertEquals("G", change.alt);
	}

	@Test
	public void testWithStrandThreeBases() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "AAA", "C").withStrand('-');
		GenomePosition expected = this.genomePosOneBasedForward.shifted(2);
		GenomePosition actual = change.pos;
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("TTT", change.ref);
		Assert.assertEquals("G", change.alt);
	}

	@Test
	public void testGetGenomeIntervalForward() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "A", "C");
		GenomeInterval genomeInterval = change.getGenomeInterval();
		GenomeInterval expectedInterval = new GenomeInterval(refDict, '+', 1, 123, 123, PositionType.ONE_BASED);
		Assert.assertTrue(expectedInterval.equals(genomeInterval));
		Assert.assertEquals(expectedInterval, genomeInterval);
	}

	@Test
	public void testGetGenomeIntervalReverse() {
		GenomeChange change = new GenomeChange(this.genomePosZeroBasedReverse, "A", "C");
		GenomeInterval genomeInterval = change.getGenomeInterval();
		GenomeInterval expectedInterval = new GenomeInterval(refDict, '-', 1, 122, 123, PositionType.ZERO_BASED);
		Assert.assertTrue(expectedInterval.equals(genomeInterval));
		Assert.assertEquals(expectedInterval, genomeInterval);
	}
}
