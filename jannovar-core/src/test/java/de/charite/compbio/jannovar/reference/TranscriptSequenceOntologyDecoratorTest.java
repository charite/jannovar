package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.data.ReferenceDictionary;

public class TranscriptSequenceOntologyDecoratorTest {

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	/** transcript builder for the forward strand */
	TranscriptModelBuilder builderForward;
	/** transcript builder for the reverse strand */
	TranscriptModelBuilder builderReverse;
	/** transcript info for the forward strand */
	TranscriptModel infoForward;
	/** transcript info for the reverse strand */
	TranscriptModel infoReverse;

	@Before
	public void setUp() {

		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc001anx.3\tchr1\t+\t6640062\t6649340\t6640669\t6649272\t11"
						+ "\t6640062,6640600,6642117,6645978,6646754,6647264,6647537,"
						+ "6648119,6648337,6648815,6648975,\t6640196,6641359,6642359,"
						+ "6646090,6646847,6647351,6647692,6648256,6648502,6648904,6649340,\tP10074\tuc001anx.3");
		this.builderForward.setGeneSymbol("ZBTB48");
		this.infoForward = builderForward.build();

		this.builderReverse = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc001bgu.3\tchr1\t-\t23685940\t23696357\t23688461\t23694498\t4"
						+ "\t23685940,23693534,23694465,23695858,\t23689714,23693661,23694558,"
						+ "23696357,\tQ9C0F3\tuc001bgu.3");
		this.builderReverse.setGeneSymbol("ZNF436");
		this.infoReverse = builderReverse.build();
	}

	@Test
	public void testGetStartStopCodonIntervalForward() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		GenomeInterval expectedStart = new GenomeInterval(refDict, Strand.FWD, 1, 6640669, 6640672,
				PositionType.ZERO_BASED);
		Assert.assertEquals(expectedStart, decorator.getStartCodonInterval());
		GenomeInterval expectedStop = new GenomeInterval(refDict, Strand.FWD, 1, 6649269, 6649272,
				PositionType.ZERO_BASED);
		Assert.assertEquals(expectedStop, decorator.getStopCodonInterval());
	}

	@Test
	public void testGetStartStopCodonIntervalReverse() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoReverse);

		GenomeInterval expectedStop = new GenomeInterval(refDict, Strand.FWD, 1, 23688461, 23688464,
				PositionType.ZERO_BASED);
		Assert.assertEquals(expectedStop, decorator.getStopCodonInterval());
		GenomeInterval expectedStart = new GenomeInterval(refDict, Strand.FWD, 1, 23694495, 23694498,
				PositionType.ZERO_BASED);
		Assert.assertEquals(expectedStart, decorator.getStartCodonInterval());
	}

	@Test
	public void testGetFivePrimeUTRIntervalForward() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		GenomeInterval expectedFivePrime = new GenomeInterval(refDict, Strand.FWD, 1, 6640062, 6640669,
				PositionType.ZERO_BASED);
		Assert.assertEquals(expectedFivePrime, decorator.getFivePrimeUTRInterval());
		GenomeInterval expectedThreePrime = new GenomeInterval(refDict, Strand.FWD, 1, 6649272, 6649340,
				PositionType.ZERO_BASED);
		Assert.assertEquals(expectedThreePrime, decorator.getThreePrimeUTRInterval());
	}

	@Test
	public void testGetFivePrimeUTRIntervalReverse() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoReverse);

		GenomeInterval expectedFivePrime = new GenomeInterval(refDict, Strand.FWD, 1, 23694498, 23696357,
				PositionType.ZERO_BASED);
		Assert.assertEquals(expectedFivePrime, decorator.getFivePrimeUTRInterval());
		GenomeInterval expectedThreePrime = new GenomeInterval(refDict, Strand.FWD, 1, 23685940, 23688461,
				PositionType.ZERO_BASED);
		Assert.assertEquals(expectedThreePrime, decorator.getThreePrimeUTRInterval());
	}

	@Test
	public void testTranslationalStartSiteOverlapForward() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithTranslationalStartSite(new GenomeInterval(refDict, Strand.FWD, 1,
				6640666, 6640669, PositionType.ZERO_BASED)));
		for (int i = 1; i <= 5; ++i)
			Assert.assertTrue(decorator.overlapsWithTranslationalStartSite(new GenomeInterval(refDict, Strand.FWD, 1,
					6640666 + i, 6640669 + i, PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithTranslationalStartSite(new GenomeInterval(refDict, Strand.FWD, 1,
				6640672, 6640675, PositionType.ZERO_BASED)));
	}

	@Test
	public void testTranslationalStopSiteOverlapForward() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithTranslationalStopSite(new GenomeInterval(refDict, Strand.FWD, 1,
				6649266, 6649269, PositionType.ZERO_BASED)));
		for (int i = 1; i <= 5; ++i)
			Assert.assertTrue(decorator.overlapsWithTranslationalStopSite(new GenomeInterval(refDict, Strand.FWD, 1,
					6649266 + i, 6649269 + i, PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithTranslationalStopSite(new GenomeInterval(refDict, Strand.FWD, 1,
				6649272, 6649275, PositionType.ZERO_BASED)));
	}

	@Test
	public void testFivePrimeUTROverlapForward() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithFivePrimeUTR(new GenomeInterval(refDict, Strand.FWD, 1, 6640059,
				6640062, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithFivePrimeUTR(new GenomeInterval(refDict, Strand.FWD, 1, 6640060,
				6640063, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithFivePrimeUTR(new GenomeInterval(refDict, Strand.FWD, 1, 6640668,
				6640671, PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithFivePrimeUTR(new GenomeInterval(refDict, Strand.FWD, 1, 6640669,
				6640672, PositionType.ZERO_BASED)));
	}

	@Test
	public void testThreePrimeUTROverlapForward() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithThreePrimeUTR(new GenomeInterval(refDict, Strand.FWD, 1, 6649269,
				6649272, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithThreePrimeUTR(new GenomeInterval(refDict, Strand.FWD, 1, 6649270,
				6649273, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithThreePrimeUTR(new GenomeInterval(refDict, Strand.FWD, 1, 6649339,
				6649342, PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithThreePrimeUTR(new GenomeInterval(refDict, Strand.FWD, 1, 6649340,
				6649343, PositionType.ZERO_BASED)));
	}

	@Test
	public void testLiesInExonForwardSuccess() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertTrue(decorator.liesInExon(new GenomeInterval(refDict, Strand.FWD, 1, 6640669, 6640672,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.liesInExon(new GenomeInterval(refDict, Strand.FWD, 1, 6646754, 6646757,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testLiesInExonForwardFailure() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.liesInExon(new GenomeInterval(refDict, Strand.FWD, 1, 6640195, 6640198,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testLiesInCDSExonForwardSuccess() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertTrue(decorator.liesInCDSExon(new GenomeInterval(refDict, Strand.FWD, 1, 6640669, 6640672,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.liesInCDSExon(new GenomeInterval(refDict, Strand.FWD, 1, 6646754, 6646757,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testLiesInCDSExonForwardFailure() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.liesInCDSExon(new GenomeInterval(refDict, Strand.FWD, 1, 6640668, 6640671,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testLiesInIntron() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.liesInIntron(new GenomeInterval(refDict, Strand.FWD, 1, 6640195, 6640600,
				PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.liesInIntron(new GenomeInterval(refDict, Strand.FWD, 1, 6640196, 6640600,
				PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.liesInIntron(new GenomeInterval(refDict, Strand.FWD, 1, 6640196, 6640601,
				PositionType.ZERO_BASED)));
	}

	@Test
	public void testOverlapsWithSpliceRegion() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		// donor region
		Assert.assertFalse(decorator.overlapsWithSpliceRegion(new GenomeInterval(refDict, Strand.FWD, 1, 6642350,
				6642356, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceRegion(new GenomeInterval(refDict, Strand.FWD, 1, 6642350,
				6642357, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceRegion(new GenomeInterval(refDict, Strand.FWD, 1, 6642366,
				6642370, PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithSpliceRegion(new GenomeInterval(refDict, Strand.FWD, 1, 6642367,
				6642370, PositionType.ZERO_BASED)));

		// acceptor region
		Assert.assertFalse(decorator.overlapsWithSpliceRegion(new GenomeInterval(refDict, Strand.FWD, 1, 6642100,
				6642109, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceRegion(new GenomeInterval(refDict, Strand.FWD, 1, 6642100,
				6642110, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceRegion(new GenomeInterval(refDict, Strand.FWD, 1, 6642119,
				6642125, PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithSpliceRegion(new GenomeInterval(refDict, Strand.FWD, 1, 6642120,
				6642125, PositionType.ZERO_BASED)));
	}

	@Test
	public void testOverlapsWithSpliceDonorSite() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithSpliceAcceptorSite(new GenomeInterval(refDict, Strand.FWD, 1,
				6645978, 6645980, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceAcceptorSite(new GenomeInterval(refDict, Strand.FWD, 1, 6645977,
				6645980, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceAcceptorSite(new GenomeInterval(refDict, Strand.FWD, 1, 6645975,
				6645977, PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithSpliceAcceptorSite(new GenomeInterval(refDict, Strand.FWD, 1,
				6645972, 6645976, PositionType.ZERO_BASED)));
	}

	@Test
	public void testOverlapsWithSpliceAcceptorSite() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithSpliceAcceptorSite(new GenomeInterval(refDict, Strand.FWD, 1,
				6642117, 6642119, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceAcceptorSite(new GenomeInterval(refDict, Strand.FWD, 1, 6642116,
				6642119, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithSpliceAcceptorSite(new GenomeInterval(refDict, Strand.FWD, 1, 6642113,
				6642117, PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithSpliceAcceptorSite(new GenomeInterval(refDict, Strand.FWD, 1,
				6642112, 6642115, PositionType.ZERO_BASED)));
	}

	@Test
	public void testOverlapsWithUpstreamRegion() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithUpstreamRegion(new GenomeInterval(refDict, Strand.FWD, 1, 6640062,
				6640064, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithUpstreamRegion(new GenomeInterval(refDict, Strand.FWD, 1, 6640061,
				6640064, PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithUpstreamRegion(new GenomeInterval(refDict, Strand.FWD, 1, 6638062,
				6639062, PositionType.ZERO_BASED)));

	}

	@Test
	public void testOverlapsWithDownstreamRegion() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithDownstreamRegion(new GenomeInterval(refDict, Strand.FWD, 1, 6649339,
				6649340, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithDownstreamRegion(new GenomeInterval(refDict, Strand.FWD, 1, 6649340,
				6649341, PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithDownstreamRegion(new GenomeInterval(refDict, Strand.FWD, 1, 6650340,
				6651340, PositionType.ZERO_BASED)));
	}

	@Test
	public void testOverlapsWithFivePrimeUTR() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithFivePrimeUTR(new GenomeInterval(refDict, Strand.FWD, 1, 6640669,
				6640670, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithFivePrimeUTR(new GenomeInterval(refDict, Strand.FWD, 1, 6640668,
				6640670, PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithFivePrimeUTR(new GenomeInterval(refDict, Strand.FWD, 1, 6640061,
				6640062, PositionType.ZERO_BASED)));

	}

	@Test
	public void testOverlapsWithThreePrimeUTR() {
		TranscriptSequenceOntologyDecorator decorator = new TranscriptSequenceOntologyDecorator(infoForward);

		Assert.assertFalse(decorator.overlapsWithThreePrimeUTR(new GenomeInterval(refDict, Strand.FWD, 1, 6649270,
				6649272, PositionType.ZERO_BASED)));
		Assert.assertTrue(decorator.overlapsWithThreePrimeUTR(new GenomeInterval(refDict, Strand.FWD, 1, 6649270,
				6649273, PositionType.ZERO_BASED)));
		Assert.assertFalse(decorator.overlapsWithThreePrimeUTR(new GenomeInterval(refDict, Strand.FWD, 1, 6649340,
				6649341, PositionType.ZERO_BASED)));

	}
}
