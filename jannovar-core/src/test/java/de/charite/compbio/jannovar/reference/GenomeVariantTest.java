package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.data.ReferenceDictionary;

public class GenomeVariantTest {

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
		GenomeVariant change = new GenomeVariant(this.genomePosOneBasedForward, "A", "C");
		Assert.assertEquals(this.genomePosOneBasedForward, change.getGenomePos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandZeroRefBasesOneBased() {
		GenomeVariant change = new GenomeVariant(this.genomePosOneBasedForward, "", "C", Strand.REV);
		Assert.assertEquals(this.genomePosOneBasedForward.shifted(-1).withStrand(Strand.REV), change.getGenomePos());
		Assert.assertEquals("", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandOneRefBaseOneBased() {
		GenomeVariant change = new GenomeVariant(this.genomePosOneBasedForward, "A", "C", Strand.REV);
		Assert.assertEquals(this.genomePosOneBasedForward.shifted(0).withStrand(Strand.REV), change.getGenomePos());
		Assert.assertEquals("T", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandThreeRefBasesOneBased() {
		GenomeVariant change = new GenomeVariant(this.genomePosOneBasedForward, "AAA", "CCC", Strand.REV);
		Assert.assertEquals(this.genomePosOneBasedForward.shifted(2).withStrand(Strand.REV), change.getGenomePos());
		Assert.assertEquals("TTT", change.getRef());
		Assert.assertEquals("GGG", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandZeroRefBasesZeroBased() {
		GenomeVariant change = new GenomeVariant(this.genomePosZeroBasedForward, "", "C", Strand.REV);
		Assert.assertEquals(this.genomePosZeroBasedForward.shifted(-1).withStrand(Strand.REV), change.getGenomePos());
		Assert.assertEquals("", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandOneRefBaseZeroBased() {
		GenomeVariant change = new GenomeVariant(this.genomePosZeroBasedForward, "A", "C", Strand.REV);
		Assert.assertEquals(this.genomePosZeroBasedForward.shifted(0).withStrand(Strand.REV), change.getGenomePos());
		Assert.assertEquals("T", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandThreeRefBasesZeroBased() {
		GenomeVariant change = new GenomeVariant(this.genomePosZeroBasedForward, "AAA", "CCC", Strand.REV);
		Assert.assertEquals(this.genomePosZeroBasedForward.shifted(2).withStrand(Strand.REV), change.getGenomePos());
		Assert.assertEquals("TTT", change.getRef());
		Assert.assertEquals("GGG", change.getAlt());
	}

	@Test
	public void testConstructorStripLeading() {
		GenomeVariant change = new GenomeVariant(this.genomePosOneBasedForward, "AAA", "AAC");
		GenomePosition expectedPos = new GenomePosition(refDict, this.genomePosOneBasedForward.getStrand(),
				this.genomePosOneBasedForward.getChr(), this.genomePosOneBasedForward.getPos() + 2, PositionType.ZERO_BASED);
		Assert.assertEquals(expectedPos, change.getGenomePos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testConstructorStripTrailing() {
		GenomeVariant change = new GenomeVariant(this.genomePosOneBasedForward, "AGG", "CGG");
		Assert.assertEquals(this.genomePosOneBasedForward, change.getGenomePos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testConstructorStripBoth() {
		GenomeVariant change = new GenomeVariant(this.genomePosOneBasedForward, "GGACC", "GGCCC");
		GenomePosition expectedPos = new GenomePosition(refDict, this.genomePosOneBasedForward.getStrand(),
				this.genomePosOneBasedForward.getChr(), this.genomePosOneBasedForward.getPos() + 2, PositionType.ZERO_BASED);
		Assert.assertEquals(expectedPos, change.getGenomePos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testWithStrandZeroBases() {
		GenomeVariant change = new GenomeVariant(this.genomePosOneBasedForward, "", "C").withStrand(Strand.REV);
		GenomePosition expected = this.genomePosOneBasedForward.shifted(-1);
		GenomePosition actual = change.getGenomePos();
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testWithStrandOneBase() {
		GenomeVariant change = new GenomeVariant(this.genomePosOneBasedForward, "A", "C").withStrand(Strand.REV);
		GenomePosition expected = this.genomePosOneBasedForward.shifted(0);
		GenomePosition actual = change.getGenomePos();
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("T", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testWithStrandTwoBases() {
		GenomeVariant change = new GenomeVariant(this.genomePosOneBasedForward, "AA", "C").withStrand(Strand.REV);
		GenomePosition expected = this.genomePosOneBasedForward.shifted(1);
		GenomePosition actual = change.getGenomePos();
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("TT", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testWithStrandThreeBases() {
		GenomeVariant change = new GenomeVariant(this.genomePosOneBasedForward, "AAA", "C").withStrand(Strand.REV);
		GenomePosition expected = this.genomePosOneBasedForward.shifted(2);
		GenomePosition actual = change.getGenomePos();
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("TTT", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testGetGenomeIntervalForward() {
		GenomeVariant change = new GenomeVariant(this.genomePosOneBasedForward, "A", "C");
		GenomeInterval genomeInterval = change.getGenomeInterval();
		GenomeInterval expectedInterval = new GenomeInterval(refDict, Strand.FWD, 1, 123, 123, PositionType.ONE_BASED);
		Assert.assertTrue(expectedInterval.equals(genomeInterval));
		Assert.assertEquals(expectedInterval, genomeInterval);
	}

	@Test
	public void testGetGenomeIntervalReverse() {
		GenomeVariant change = new GenomeVariant(this.genomePosZeroBasedReverse, "A", "C");
		GenomeInterval genomeInterval = change.getGenomeInterval();
		GenomeInterval expectedInterval = new GenomeInterval(refDict, Strand.REV, 1, 122, 123,
				PositionType.ZERO_BASED);
		Assert.assertTrue(expectedInterval.equals(genomeInterval));
		Assert.assertEquals(expectedInterval, genomeInterval);
	}
}
