package jannovar.reference;

import jannovar.exception.ProjectionException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TranscriptSpliceSiteDecoratorTest {

	/** transcript on forward strand */
	TranscriptModel transForward;
	/** transcript on reverse strand */
	TranscriptModel transReverse;

	/** transcript info for forward strand */
	TranscriptInfo infoForward;
	/** transcript info for reverse strand */
	TranscriptInfo infoReverse;

	@Before
	public void setUp() {

		this.transForward = TranscriptModelFactory
				.parseKnownGenesLine("uc001anx.3\tchr1\t+\t6640062\t6649340\t6640669\t6649272\t11"
						+ "\t6640062,6640600,6642117,6645978,6646754,6647264,6647537,"
						+ "6648119,6648337,6648815,6648975,\t6640196,6641359,6642359,"
						+ "6646090,6646847,6647351,6647692,6648256,6648502,6648904,6649340,\tP10074\tuc001anx.3");
		this.transForward.setGeneSymbol("ZBTB48");

		this.transReverse = TranscriptModelFactory
				.parseKnownGenesLine("uc001bgu.3\tchr1\t-\t23685940\t23696357\t23688461\t23694498\t4"
						+ "\t23685940,23693534,23694465,23695858,\t23689714,23693661,23694558,"
						+ "23696357,\tQ9C0F3\tuc001bgu.3");
		this.transReverse.setGeneSymbol("ZNF436");

		this.infoForward = new TranscriptInfo(this.transForward);
		this.infoReverse = new TranscriptInfo(this.transReverse);
	}

	@Test
	public void testSpliceSiteDetectionChangeZeroRefNucleotidesForward() throws ProjectionException {
		TranscriptSpliceSiteDecorator detector = new TranscriptSpliceSiteDecorator(infoForward);

		Assert.assertFalse(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640060,
				PositionType.ZERO_BASED), "", "A")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640061,
				PositionType.ZERO_BASED), "", "A")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640062,
				PositionType.ZERO_BASED), "", "A")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640063,
				PositionType.ZERO_BASED), "", "A")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640064,
				PositionType.ZERO_BASED), "", "A")));
		Assert.assertFalse(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640065,
				PositionType.ZERO_BASED), "", "A")));
	}

	@Test
	public void testSpliceSiteDetectionChangeOneRefNucleotideForward() throws ProjectionException {
		TranscriptSpliceSiteDecorator detector = new TranscriptSpliceSiteDecorator(infoForward);

		Assert.assertFalse(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640059,
				PositionType.ZERO_BASED), "A", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640060,
				PositionType.ZERO_BASED), "A", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640061,
				PositionType.ZERO_BASED), "A", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640062,
				PositionType.ZERO_BASED), "A", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640063,
				PositionType.ZERO_BASED), "A", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640064,
				PositionType.ZERO_BASED), "A", "C")));
		Assert.assertFalse(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640065,
				PositionType.ZERO_BASED), "A", "C")));
	}

	@Test
	public void testSpliceSiteDetectionChangeTwoRefNucleotidesForward() throws ProjectionException {
		TranscriptSpliceSiteDecorator detector = new TranscriptSpliceSiteDecorator(infoForward);

		Assert.assertFalse(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640058,
				PositionType.ZERO_BASED), "AA", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640059,
				PositionType.ZERO_BASED), "AA", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640060,
				PositionType.ZERO_BASED), "AA", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640061,
				PositionType.ZERO_BASED), "AA", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640062,
				PositionType.ZERO_BASED), "AA", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640063,
				PositionType.ZERO_BASED), "AA", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640064,
				PositionType.ZERO_BASED), "AA", "C")));
		Assert.assertFalse(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 6640065,
				PositionType.ZERO_BASED), "AA", "C")));
	}

	@Test
	public void testSpliceSiteDetectionChangeZeroRefNucleotidesReverse() throws ProjectionException {
		TranscriptSpliceSiteDecorator detector = new TranscriptSpliceSiteDecorator(infoReverse);

		Assert.assertFalse(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685938,
				PositionType.ZERO_BASED), "", "A")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685939,
				PositionType.ZERO_BASED), "", "A")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685940,
				PositionType.ZERO_BASED), "", "A")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685941,
				PositionType.ZERO_BASED), "", "A")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685942,
				PositionType.ZERO_BASED), "", "A")));
		Assert.assertFalse(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685943,
				PositionType.ZERO_BASED), "", "A")));
	}

	@Test
	public void testSpliceSiteDetectionChangeOneRefNucleotideReverse() throws ProjectionException {
		TranscriptSpliceSiteDecorator detector = new TranscriptSpliceSiteDecorator(infoReverse);

		Assert.assertFalse(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685937,
				PositionType.ZERO_BASED), "A", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685938,
				PositionType.ZERO_BASED), "A", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685939,
				PositionType.ZERO_BASED), "A", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685940,
				PositionType.ZERO_BASED), "A", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685941,
				PositionType.ZERO_BASED), "A", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685942,
				PositionType.ZERO_BASED), "A", "C")));
		Assert.assertFalse(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685943,
				PositionType.ZERO_BASED), "A", "C")));
	}

	@Test
	public void testSpliceSiteDetectionChangeTwoRefNucleotidesReverse() throws ProjectionException {
		TranscriptSpliceSiteDecorator detector = new TranscriptSpliceSiteDecorator(infoReverse);

		Assert.assertFalse(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685936,
				PositionType.ZERO_BASED), "AA", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685937,
				PositionType.ZERO_BASED), "AA", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685938,
				PositionType.ZERO_BASED), "AA", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685939,
				PositionType.ZERO_BASED), "AA", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685940,
				PositionType.ZERO_BASED), "AA", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685941,
				PositionType.ZERO_BASED), "AA", "C")));
		Assert.assertTrue(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685942,
				PositionType.ZERO_BASED), "AA", "C")));
		Assert.assertFalse(detector.doesChangeAffectSpliceSite(new GenomeChange(new GenomePosition('+', 1, 23685943,
				PositionType.ZERO_BASED), "AA", "C")));
	}

}
