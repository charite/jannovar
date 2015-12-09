package de.charite.compbio.jannovar.reference;

import static de.charite.compbio.jannovar.reference.TranscriptProjectionDecorator.INVALID_EXON_ID;
import static de.charite.compbio.jannovar.reference.TranscriptProjectionDecorator.INVALID_INTRON_ID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.data.ReferenceDictionary;

/**
 * Tests for the coordinate conversion decorator.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class TranscriptProjectionDecoratorTest {

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
		// RefSeq: NM_005341.3

		this.builderReverse = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc001bgu.3\tchr1\t-\t23685940\t23696357\t23688461\t23694498\t4"
						+ "\t23685940,23693534,23694465,23695858,\t23689714,23693661,23694558,"
						+ "23696357,\tQ9C0F3\tuc001bgu.3");
		this.builderReverse.setGeneSymbol("ZNF436");
		this.infoReverse = builderReverse.build();
		// RefSeq: NM_001077195.1
	}

	@Test
	public void testProjectionTranscriptToGenomeForwardSuccess() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		// test with first base of transcript
		Assert.assertEquals("1:g.6640063",
				projector.transcriptToGenomePos(new TranscriptPosition(infoForward, 1, PositionType.ONE_BASED))
						.toString());
		// test with last base of transcript
		Assert.assertEquals("1:g.6649340",
				projector.transcriptToGenomePos(new TranscriptPosition(infoForward, 2338, PositionType.ONE_BASED))
						.toString());
		// test with first base of first exon
		Assert.assertEquals("1:g.6640602",
				projector.transcriptToGenomePos(new TranscriptPosition(infoForward, 136, PositionType.ONE_BASED))
						.toString());
	}

	@Test
	public void testProjectionTranscriptToGenomeReverseSuccess() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		// test with first base of transcript
		Assert.assertEquals("1:g.23696357",
				projector.transcriptToGenomePos(new TranscriptPosition(infoReverse, 1, PositionType.ONE_BASED))
						.toString());
		// test with last base of transcript
		Assert.assertEquals("1:g.23685941",
				projector.transcriptToGenomePos(new TranscriptPosition(infoReverse, 4493, PositionType.ONE_BASED))
						.toString());
		// test with first base of first exon
		Assert.assertEquals("1:g.23694557",
				projector.transcriptToGenomePos(new TranscriptPosition(infoReverse, 501, PositionType.ONE_BASED))
						.toString());
	}

	@Test(expected = ProjectionException.class)
	public void testProjectionTranscriptToGenomeThrowsLeft() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);
		projector.transcriptToGenomePos(new TranscriptPosition(infoForward, 0, PositionType.ONE_BASED));
	}

	@Test(expected = ProjectionException.class)
	public void testProjectionTranscriptToGenomeThrowsRight() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);
		projector.transcriptToGenomePos(new TranscriptPosition(infoForward, 2350, PositionType.ONE_BASED));
	}

	@Test
	public void testLocateIntronFromGenomePosForward() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc001anx.3\tchr1\t+\t6640062\t6649340\t6640669\t6649272\t11"
						+ "\t6640062,6640600,6642117,6645978,6646754,6647264,6647537,"
						+ "6648119,6648337,6648815,6648975,\t6640196,6641359,6642359,"
						+ "6646090,6646847,6647351,6647692,6648256,6648502,6648904,6649340,\tP10074\tuc001anx.3");
		this.builderForward.setGeneSymbol("ZBTB48");
		this.infoForward = this.builderForward.build();

		// last base of first exon
		Assert.assertEquals(INVALID_INTRON_ID,
				projector.locateIntron(new GenomePosition(refDict, Strand.FWD, 1, 6640196, PositionType.ONE_BASED)));
		// first base of first intron
		Assert.assertEquals(0,
				projector.locateIntron(new GenomePosition(refDict, Strand.FWD, 1, 6640197, PositionType.ONE_BASED)));
		// last base of first intron
		Assert.assertEquals(0,
				projector.locateIntron(new GenomePosition(refDict, Strand.FWD, 1, 6640600, PositionType.ONE_BASED)));
		// first base of second exon
		Assert.assertEquals(INVALID_INTRON_ID,
				projector.locateIntron(new GenomePosition(refDict, Strand.FWD, 1, 6640601, PositionType.ONE_BASED)));

		// last base of second-last exon
		Assert.assertEquals(INVALID_INTRON_ID,
				projector.locateIntron(new GenomePosition(refDict, Strand.FWD, 1, 6648904, PositionType.ONE_BASED)));
		// first base of last intron
		Assert.assertEquals(9,
				projector.locateIntron(new GenomePosition(refDict, Strand.FWD, 1, 6648905, PositionType.ONE_BASED)));
		// last base of last intron
		Assert.assertEquals(9,
				projector.locateIntron(new GenomePosition(refDict, Strand.FWD, 1, 6648975, PositionType.ONE_BASED)));
		// first base of last exon
		Assert.assertEquals(INVALID_INTRON_ID,
				projector.locateIntron(new GenomePosition(refDict, Strand.FWD, 1, 6648976, PositionType.ONE_BASED)));
	}

	@Test
	public void testLocateExonFromGenomePosForward() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		// first base of first exon
		Assert.assertEquals(0,
				projector.locateExon(new GenomePosition(refDict, Strand.FWD, 1, 6640063, PositionType.ONE_BASED)));
		// last base of first exon
		Assert.assertEquals(0,
				projector.locateExon(new GenomePosition(refDict, Strand.FWD, 1, 6640196, PositionType.ONE_BASED)));
		// just after last base of first exon
		Assert.assertEquals(INVALID_EXON_ID,
				projector.locateExon(new GenomePosition(refDict, Strand.FWD, 1, 6640197, PositionType.ONE_BASED)));

		// just before first base of last exon
		Assert.assertEquals(INVALID_EXON_ID,
				projector.locateExon(new GenomePosition(refDict, Strand.FWD, 1, 6648974, PositionType.ONE_BASED)));
		// first base of last exon
		Assert.assertEquals(10,
				projector.locateExon(new GenomePosition(refDict, Strand.FWD, 1, 6648976, PositionType.ONE_BASED)));
		// last base of last exon
		Assert.assertEquals(10,
				projector.locateExon(new GenomePosition(refDict, Strand.FWD, 1, 6649340, PositionType.ONE_BASED)));
	}

	@Test
	public void testLocateExonFromGenomePosReverse() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		// first base of first exon
		Assert.assertEquals(3,
				projector.locateExon(new GenomePosition(refDict, Strand.FWD, 1, 23685941, PositionType.ONE_BASED)));
		// last base of first exon
		Assert.assertEquals(3,
				projector.locateExon(new GenomePosition(refDict, Strand.FWD, 1, 23689714, PositionType.ONE_BASED)));
		// just after last base of first exon
		Assert.assertEquals(INVALID_EXON_ID,
				projector.locateExon(new GenomePosition(refDict, Strand.FWD, 1, 23689715, PositionType.ONE_BASED)));

		// just before first base of last exon
		Assert.assertEquals(INVALID_EXON_ID,
				projector.locateExon(new GenomePosition(refDict, Strand.FWD, 1, 23695857, PositionType.ONE_BASED)));
		// first base of last exon
		Assert.assertEquals(0,
				projector.locateExon(new GenomePosition(refDict, Strand.FWD, 1, 23695859, PositionType.ONE_BASED)));
		// last base of last exon
		Assert.assertEquals(0,
				projector.locateExon(new GenomePosition(refDict, Strand.FWD, 1, 23696357, PositionType.ONE_BASED)));
	}

	@Test
	public void testLocateExonFromTranscriptPosForward() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		// first base of first exon
		Assert.assertEquals(0, projector.locateExon(new TranscriptPosition(infoForward, 1, PositionType.ONE_BASED)));
		// last base of first exon
		Assert.assertEquals(0, projector.locateExon(new TranscriptPosition(infoForward, 134, PositionType.ONE_BASED)));
		// just after last base of first exon
		Assert.assertEquals(INVALID_EXON_ID,
				projector.locateExon(new GenomePosition(refDict, Strand.FWD, 1, 6640197, PositionType.ONE_BASED)));

		// first base of last exon
		Assert.assertEquals(10, projector.locateExon(new TranscriptPosition(infoForward, 1984, PositionType.ONE_BASED)));
		// last base of last exon
		Assert.assertEquals(10, projector.locateExon(new TranscriptPosition(infoForward, 2338, PositionType.ONE_BASED)));
	}

	@Test
	public void testLocateExonFromTranscriptPosReverse() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		// first base of first exon
		Assert.assertEquals(3, projector.locateExon(new TranscriptPosition(infoReverse, 723, PositionType.ONE_BASED)));
		// last base of first exon
		Assert.assertEquals(3, projector.locateExon(new TranscriptPosition(infoReverse, 4493, PositionType.ONE_BASED)));

		// first base of last exon
		Assert.assertEquals(0, projector.locateExon(new TranscriptPosition(infoReverse, 1, PositionType.ONE_BASED)));
		// last base of last exon
		Assert.assertEquals(0, projector.locateExon(new TranscriptPosition(infoReverse, 499, PositionType.ONE_BASED)));
	}

	@Test
	public void testExonIDInReferenceOrderForward() {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		for (int i = 0; i < 11; ++i)
			Assert.assertEquals(i, projector.exonIDInReferenceOrder(i));
	}

	@Test
	public void testExonIDInReferenceOrderReverse() {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		for (int i = 0; i < 4; ++i)
			Assert.assertEquals(3 - i, projector.exonIDInReferenceOrder(i));
	}

	@Test
	public void testGenomeToTranscriptPosForward() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		// first base of first exon
		Assert.assertEquals(new TranscriptPosition(infoForward, 1, PositionType.ONE_BASED), projector
				.genomeToTranscriptPos(new GenomePosition(refDict, Strand.FWD, 1, 6640063, PositionType.ONE_BASED)));
		// last base of first exon
		Assert.assertEquals(new TranscriptPosition(infoForward, 134, PositionType.ONE_BASED), projector
				.genomeToTranscriptPos(new GenomePosition(refDict, Strand.FWD, 1, 6640196, PositionType.ONE_BASED)));

		// first base of last exon
		Assert.assertEquals(new TranscriptPosition(infoForward, 1974, PositionType.ONE_BASED), projector
				.genomeToTranscriptPos(new GenomePosition(refDict, Strand.FWD, 1, 6648976, PositionType.ONE_BASED)));
		// last base of last exon
		Assert.assertEquals(new TranscriptPosition(infoForward, 2338, PositionType.ONE_BASED), projector
				.genomeToTranscriptPos(new GenomePosition(refDict, Strand.FWD, 1, 6649340, PositionType.ONE_BASED)));
	}

	@Test
	public void testGenomeToTranscriptPosReverse() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		// first base of first exon
		Assert.assertEquals(new TranscriptPosition(infoReverse, 1, PositionType.ONE_BASED), projector
				.genomeToTranscriptPos(new GenomePosition(refDict, Strand.FWD, 1, 23696357, PositionType.ONE_BASED)));
		// last base of first exon
		Assert.assertEquals(new TranscriptPosition(infoReverse, 499, PositionType.ONE_BASED), projector
				.genomeToTranscriptPos(new GenomePosition(refDict, Strand.FWD, 1, 23695859, PositionType.ONE_BASED)));

		// first base of last exon
		Assert.assertEquals(new TranscriptPosition(infoReverse, 720, PositionType.ONE_BASED), projector
				.genomeToTranscriptPos(new GenomePosition(refDict, Strand.FWD, 1, 23689714, PositionType.ONE_BASED)));
		// last base of last exon
		Assert.assertEquals(new TranscriptPosition(infoReverse, 4493, PositionType.ONE_BASED), projector
				.genomeToTranscriptPos(new GenomePosition(refDict, Strand.FWD, 1, 23685941, PositionType.ONE_BASED)));
	}

	@Test
	public void testGenomeToCDSPosForward() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		// first base in CDS (on second exon)
		Assert.assertEquals(new CDSPosition(infoForward, 1, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, Strand.FWD, 1, 6640670, PositionType.ONE_BASED)));
		// last base of second exon (first exon in CDS)
		Assert.assertEquals(new CDSPosition(infoForward, 690, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, Strand.FWD, 1, 6641359, PositionType.ONE_BASED)));

		// first base of last exon
		Assert.assertEquals(new CDSPosition(infoForward, 1771, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, Strand.FWD, 1, 6648976, PositionType.ONE_BASED)));
		// last base of CDS
		Assert.assertEquals(new CDSPosition(infoForward, 2067, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, Strand.FWD, 1, 6649272, PositionType.ONE_BASED)));
	}

	@Test
	public void testGenomeToCDSPosReverse() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		// 23688461\t23694498

		// first base of CDS (second exon)
		Assert.assertEquals(new CDSPosition(infoReverse, 1, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, Strand.FWD, 1, 23694498, PositionType.ONE_BASED)));
		// last base of second exon (first exon in CDS)
		Assert.assertEquals(new CDSPosition(infoReverse, 33, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, Strand.FWD, 1, 23694466, PositionType.ONE_BASED)));

		// first base of last exon
		Assert.assertEquals(new CDSPosition(infoReverse, 161, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, Strand.FWD, 1, 23689714, PositionType.ONE_BASED)));
		// last base of last exon
		Assert.assertEquals(new CDSPosition(infoReverse, 1413, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, Strand.FWD, 1, 23688462, PositionType.ONE_BASED)));
	}

	@Test
	public void testCDSPosToTranscriptPosForward() {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		Assert.assertEquals(new TranscriptPosition(infoForward, 203),
				projector.cdsToTranscriptPos(new CDSPosition(infoForward, 0)));
		Assert.assertEquals(new TranscriptPosition(infoForward, 233),
				projector.cdsToTranscriptPos(new CDSPosition(infoForward, 30)));

		Assert.assertEquals(new TranscriptPosition(infoForward, 403),
				projector.cdsToTranscriptPos(new CDSPosition(infoForward, 200)));
		Assert.assertEquals(new TranscriptPosition(infoForward, 503),
				projector.cdsToTranscriptPos(new CDSPosition(infoForward, 300)));
	}

	@Test
	public void testCDSPosToTranscriptPosReverse() {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		Assert.assertEquals(new TranscriptPosition(infoReverse, 559),
				projector.cdsToTranscriptPos(new CDSPosition(infoReverse, 0)));
		Assert.assertEquals(new TranscriptPosition(infoReverse, 589),
				projector.cdsToTranscriptPos(new CDSPosition(infoReverse, 30)));

		Assert.assertEquals(new TranscriptPosition(infoReverse, 759),
				projector.cdsToTranscriptPos(new CDSPosition(infoReverse, 200)));
		Assert.assertEquals(new TranscriptPosition(infoReverse, 859),
				projector.cdsToTranscriptPos(new CDSPosition(infoReverse, 300)));
	}

	@Test
	public void testCDSPosToGenomePosForward() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 1, 6640669),
				projector.cdsToGenomePos(new CDSPosition(infoForward, 0)));
		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 1, 6640699),
				projector.cdsToGenomePos(new CDSPosition(infoForward, 30)));

		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 1, 6640869),
				projector.cdsToGenomePos(new CDSPosition(infoForward, 200)));
		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 1, 6640969),
				projector.cdsToGenomePos(new CDSPosition(infoForward, 300)));
	}

	@Test
	public void testCDSPosToGenomePosReverse() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 1, 23694497),
				projector.cdsToGenomePos(new CDSPosition(infoReverse, 0)));
		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 1, 23694467),
				projector.cdsToGenomePos(new CDSPosition(infoReverse, 30)));

		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 1, 23689673),
				projector.cdsToGenomePos(new CDSPosition(infoReverse, 200)));
		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 1, 23689573),
				projector.cdsToGenomePos(new CDSPosition(infoReverse, 300)));
	}

	@Test
	public void testCDSToGenomePosHandcuratedFibrilin() throws ProjectionException {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001zwx.2	chr15	-	48700502	48937985	48703186	48936966	66	48700502,48704765,48707732,48712883,48713754,48714148,48717565,48717935,48719763,48720542,48722867,48725062,48726790,48729157,48729518,48729964,48733917,48736737,48737572,48738902,48740964,48744758,48748833,48752442,48755278,48756095,48757764,48757986,48760134,48760608,48762830,48764747,48766451,48766724,48773851,48776014,48777570,48779271,48779508,48780309,48780564,48782047,48784657,48786400,48787319,48787665,48788296,48789462,48791181,48795983,48797221,48800778,48802240,48805745,48807583,48808379,48812855,48818326,48826276,48829807,48888479,48892335,48902924,48905206,48936802,48937771,	48703576,48704940,48707964,48713003,48713883,48714265,48717688,48718061,48719970,48720668,48722999,48725185,48726910,48729274,48729584,48730114,48734043,48736857,48737701,48739019,48741090,48744881,48748959,48752514,48755437,48756218,48757890,48758055,48760299,48760731,48762953,48764873,48766574,48766847,48773977,48776140,48777693,48779397,48779634,48780438,48780690,48782275,48784783,48786451,48787457,48787785,48788422,48789588,48791235,48796136,48797344,48800901,48802366,48805865,48807724,48808559,48813014,48818452,48826402,48830005,48888575,48892431,48903023,48905289,48937147,48937985,	NP_000129	uc001zwx.2");
		this.builderForward.setGeneSymbol("FBN1");
		this.infoForward = this.builderForward.build();

		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 15, 48936965),
				projector.cdsToGenomePos(new CDSPosition(infoForward, 0)));

		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 15, 48936940),
				projector.cdsToGenomePos(new CDSPosition(infoForward, 25)));

		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 15, 48905214),
				projector.cdsToGenomePos(new CDSPosition(infoForward, 238)));

		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 15, 48888490),
				projector.cdsToGenomePos(new CDSPosition(infoForward, 526)));

		Assert.assertEquals(new GenomePosition(refDict, Strand.FWD, 15, 48800856),
				projector.cdsToGenomePos(new CDSPosition(infoForward, 1758)));
	}

}
