package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.io.ReferenceDictionary;

public class GenomeChangeTest {

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	GenomePosition genomePosOneBasedForward;
	GenomePosition genomePosZeroBasedForward;
	GenomePosition genomePosZeroBasedReverse;

	@Before
	public void setUp() {
		this.genomePosOneBasedForward = new GenomePosition(refDict, Strand.FWD, 1, 123, PositionType.ONE_BASED);
		this.genomePosZeroBasedForward = new GenomePosition(refDict, Strand.FWD, 1, 122, PositionType.ZERO_BASED);
		this.genomePosZeroBasedReverse = new GenomePosition(refDict, Strand.REV, 1, 122, PositionType.ZERO_BASED);
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
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "", "C", Strand.REV);
		Assert.assertEquals(this.genomePosOneBasedForward.shifted(-1).withStrand(Strand.REV), change.pos);
		Assert.assertEquals("", change.ref);
		Assert.assertEquals("G", change.alt);
	}

	@Test
	public void testConstructorChangeStrandOneRefBaseOneBased() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "A", "C", Strand.REV);
		Assert.assertEquals(this.genomePosOneBasedForward.shifted(0).withStrand(Strand.REV), change.pos);
		Assert.assertEquals("T", change.ref);
		Assert.assertEquals("G", change.alt);
	}

	@Test
	public void testConstructorChangeStrandThreeRefBasesOneBased() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "AAA", "CCC", Strand.REV);
		Assert.assertEquals(this.genomePosOneBasedForward.shifted(2).withStrand(Strand.REV), change.pos);
		Assert.assertEquals("TTT", change.ref);
		Assert.assertEquals("GGG", change.alt);
	}

	@Test
	public void testConstructorChangeStrandZeroRefBasesZeroBased() {
		GenomeChange change = new GenomeChange(this.genomePosZeroBasedForward, "", "C", Strand.REV);
		Assert.assertEquals(this.genomePosZeroBasedForward.shifted(-1).withStrand(Strand.REV), change.pos);
		Assert.assertEquals("", change.ref);
		Assert.assertEquals("G", change.alt);
	}

	@Test
	public void testConstructorChangeStrandOneRefBaseZeroBased() {
		GenomeChange change = new GenomeChange(this.genomePosZeroBasedForward, "A", "C", Strand.REV);
		Assert.assertEquals(this.genomePosZeroBasedForward.shifted(0).withStrand(Strand.REV), change.pos);
		Assert.assertEquals("T", change.ref);
		Assert.assertEquals("G", change.alt);
	}

	@Test
	public void testConstructorChangeStrandThreeRefBasesZeroBased() {
		GenomeChange change = new GenomeChange(this.genomePosZeroBasedForward, "AAA", "CCC", Strand.REV);
		Assert.assertEquals(this.genomePosZeroBasedForward.shifted(2).withStrand(Strand.REV), change.pos);
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
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "", "C").withStrand(Strand.REV);
		GenomePosition expected = this.genomePosOneBasedForward.shifted(-1);
		GenomePosition actual = change.pos;
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("", change.ref);
		Assert.assertEquals("G", change.alt);
	}

	@Test
	public void testWithStrandOneBase() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "A", "C").withStrand(Strand.REV);
		GenomePosition expected = this.genomePosOneBasedForward.shifted(0);
		GenomePosition actual = change.pos;
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("T", change.ref);
		Assert.assertEquals("G", change.alt);
	}

	@Test
	public void testWithStrandTwoBases() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "AA", "C").withStrand(Strand.REV);
		GenomePosition expected = this.genomePosOneBasedForward.shifted(1);
		GenomePosition actual = change.pos;
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("TT", change.ref);
		Assert.assertEquals("G", change.alt);
	}

	@Test
	public void testWithStrandThreeBases() {
		GenomeChange change = new GenomeChange(this.genomePosOneBasedForward, "AAA", "C").withStrand(Strand.REV);
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
		GenomeInterval expectedInterval = new GenomeInterval(refDict, Strand.FWD, 1, 123, 123, PositionType.ONE_BASED);
		Assert.assertTrue(expectedInterval.equals(genomeInterval));
		Assert.assertEquals(expectedInterval, genomeInterval);
	}

	@Test
	public void testGetGenomeIntervalReverse() {
		GenomeChange change = new GenomeChange(this.genomePosZeroBasedReverse, "A", "C");
		GenomeInterval genomeInterval = change.getGenomeInterval();
		GenomeInterval expectedInterval = new GenomeInterval(refDict, Strand.REV, 1, 122, 123,
				PositionType.ZERO_BASED);
		Assert.assertTrue(expectedInterval.equals(genomeInterval));
		Assert.assertEquals(expectedInterval, genomeInterval);
	}
}
