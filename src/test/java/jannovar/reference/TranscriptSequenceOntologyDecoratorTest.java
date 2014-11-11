package jannovar.reference;

import jannovar.exception.ProjectionException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TranscriptSequenceOntologyDecoratorTest {

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
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640060, 6640060,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640061, 6640061,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640062, 6640062,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640063, 6640063,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640064, 6640064,
				PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640065, 6640065,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testSpliceSiteDetectionChangeOneRefNucleotideForward() throws ProjectionException {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640059, 6640060,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640060, 6640061,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640061, 6640062,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640062, 6640063,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640063, 6640064,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640064, 6640065,
				PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640065, 6640066,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testSpliceSiteDetectionChangeTwoRefNucleotidesForward() throws ProjectionException {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640058, 6640060,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640059, 6640061,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640060, 6640062,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640061, 6640063,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640062, 6640064,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640063, 6640065,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640064, 6640066,
				PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 6640065, 6640067,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testSpliceSiteDetectionChangeZeroRefNucleotidesReverse() throws ProjectionException {
		TranscriptSequenceOntologyDecorator detector = new TranscriptSequenceOntologyDecorator(infoReverse);

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

		Assert.assertFalse(detector.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685938, 23685938,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(detector.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685939, 23685939,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(detector.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685940, 23685940,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(detector.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685941, 23685941,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(detector.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685942, 23685942,
				PositionType.ZERO_BASED)));
		Assert.assertFalse(detector.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685943, 23685943,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testSpliceSiteDetectionChangeOneRefNucleotideReverse() throws ProjectionException {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoReverse);

		Assert.assertFalse(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685937, 23685938,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685938, 23685939,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685939, 23685940,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685940, 23685941,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685941, 23685942,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685942, 23685943,
				PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685943, 23685944,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testSpliceSiteDetectionChangeTwoRefNucleotidesReverse() throws ProjectionException {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoReverse);

		Assert.assertFalse(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685936, 23685937,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685937, 23685939,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685938, 23685940,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685939, 23685942,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685940, 23685943,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685941, 23685944,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685942, 23685945,
				PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithSpliceSite(new GenomeInterval('+', 1, 23685943, 23685946,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testGetStartStopCodonIntervalForward() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		GenomeInterval expectedStart = new GenomeInterval('+', 1, 6640669, 6640672, PositionType.ZERO_BASED);
		Assert.assertEquals(expectedStart, decorator.getStartCodonInterval());
		GenomeInterval expectedStop = new GenomeInterval('+', 1, 6649269, 6649272, PositionType.ZERO_BASED);
		Assert.assertEquals(expectedStop, decorator.getStopCodonInterval());
	}

	@Test
	public void testGetStartStopCodonIntervalReverse() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoReverse);

		GenomeInterval expectedStop = new GenomeInterval('+', 1, 23688461, 23688464, PositionType.ZERO_BASED);
		Assert.assertEquals(expectedStop, decorator.getStopCodonInterval());
		GenomeInterval expectedStart = new GenomeInterval('+', 1, 23694495, 23694498, PositionType.ZERO_BASED);
		Assert.assertEquals(expectedStart, decorator.getStartCodonInterval());
	}

	@Test
	public void testGetFivePrimeUTRIntervalForward() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		GenomeInterval expectedFivePrime = new GenomeInterval('+', 1, 6640062, 6640669, PositionType.ZERO_BASED);
		Assert.assertEquals(expectedFivePrime, decorator.getFivePrimeUTRInterval());
		GenomeInterval expectedThreePrime = new GenomeInterval('+', 1, 6649272, 6649340, PositionType.ZERO_BASED);
		Assert.assertEquals(expectedThreePrime, decorator.getThreePrimeUTRInterval());
	}

	@Test
	public void testGetFivePrimeUTRIntervalReverse() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoReverse);

		GenomeInterval expectedFivePrime = new GenomeInterval('+', 1, 23694498, 23696357, PositionType.ZERO_BASED);
		Assert.assertEquals(expectedFivePrime, decorator.getFivePrimeUTRInterval());
		GenomeInterval expectedThreePrime = new GenomeInterval('+', 1, 23685940, 23688461, PositionType.ZERO_BASED);
		Assert.assertEquals(expectedThreePrime, decorator.getThreePrimeUTRInterval());
	}

	@Test
	public void testTranslationalStartSiteOverlapForward() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithTranslationalStartSite(new GenomeInterval('+', 1, 6640666, 6640669,
				PositionType.ZERO_BASED)));
		for (int i = 1; i <= 5; ++i)
			Assert.assertTrue(decorator.overlapsWithTranslationalStartSite(new GenomeInterval('+', 1, 6640666 + i,
					6640669 + i, PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithTranslationalStartSite(new GenomeInterval('+', 1, 6640672, 6640675,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testTranslationalStopSiteOverlapForward() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithTranslationalStopSite(new GenomeInterval('+', 1, 6649266, 6649269,
				PositionType.ZERO_BASED)));
		for (int i = 1; i <= 5; ++i)
			Assert.assertTrue(decorator.overlapsWithTranslationalStopSite(new GenomeInterval('+', 1, 6649266 + i,
					6649269 + i, PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithTranslationalStopSite(new GenomeInterval('+', 1, 6649272, 6649275,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testFivePrimeUTROverlapForward() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		this.transForward = TranscriptModelFactory
				.parseKnownGenesLine("uc001anx.3\tchr1\t+\t6640062\t6649340\t6640669\t6649272\t11"
						+ "\t6640062,6640600,6642117,6645978,6646754,6647264,6647537,"
						+ "6648119,6648337,6648815,6648975,\t6640196,6641359,6642359,"
						+ "6646090,6646847,6647351,6647692,6648256,6648502,6648904,6649340,\tP10074\tuc001anx.3");
		this.transForward.setGeneSymbol("ZBTB48");

		Assert.assertFalse(decorator.overlapsWithFivePrimeUTR(new GenomeInterval('+', 1, 6640059, 6640062,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithFivePrimeUTR(new GenomeInterval('+', 1, 6640060, 6640063,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithFivePrimeUTR(new GenomeInterval('+', 1, 6640668, 6640671,
				PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithFivePrimeUTR(new GenomeInterval('+', 1, 6640669, 6640672,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testThreePrimeUTROverlapForward() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithThreePrimeUTR(new GenomeInterval('+', 1, 6649269, 6649272,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithThreePrimeUTR(new GenomeInterval('+', 1, 6649270, 6649273,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithThreePrimeUTR(new GenomeInterval('+', 1, 6649339, 6649342,
				PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithThreePrimeUTR(new GenomeInterval('+', 1, 6649340, 6649343,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testLiesInExonForwardSuccess() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		this.transForward = TranscriptModelFactory
				.parseKnownGenesLine("uc001anx.3\tchr1\t+\t6640062\t6649340\t6640669\t6649272\t11"
						+ "\t6640062,6640600,6642117,6645978,6646754,6647264,6647537,"
						+ "6648119,6648337,6648815,6648975,\t6640196,6641359,6642359,"
						+ "6646090,6646847,6647351,6647692,6648256,6648502,6648904,6649340,\tP10074\tuc001anx.3");
		this.transForward.setGeneSymbol("ZBTB48");

		Assert.assertTrue(decorator.liesInExon(new GenomeInterval('+', 1, 6640669, 6640672, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.liesInExon(new GenomeInterval('+', 1, 6646754, 6646757, PositionType.ZERO_BASED)));
	}

	@Test
	public void testLiesInExonForwardFailure() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.liesInExon(new GenomeInterval('+', 1, 6640195, 6640198, PositionType.ZERO_BASED)));
	}

	@Test
	public void testLiesInCDSExonForwardSuccess() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertTrue(decorator
				.liesInCDSExon(new GenomeInterval('+', 1, 6640669, 6640672, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator
				.liesInCDSExon(new GenomeInterval('+', 1, 6646754, 6646757, PositionType.ZERO_BASED)));
	}

	@Test
	public void testLiesInCDSExonForwardFailure() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator
				.liesInCDSExon(new GenomeInterval('+', 1, 6640668, 6640671, PositionType.ZERO_BASED)));
	}
}
