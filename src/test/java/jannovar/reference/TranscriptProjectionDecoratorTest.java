package jannovar.reference;

import static jannovar.reference.TranscriptProjectionDecorator.INVALID_EXON_ID;
import jannovar.exception.ProjectionException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the coordinate conversion decorator.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptProjectionDecoratorTest {

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
	public void testProjectionTranscriptToGenomeForwardSuccess() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		// test with first base of transcript
		Assert.assertEquals("chr1:6640063", projector.transcriptToGenomePos(new TranscriptPosition(transForward, 1))
				.toString());
		// test with last base of transcript
		Assert.assertEquals("chr1:6649340", projector.transcriptToGenomePos(new TranscriptPosition(transForward, 2338))
				.toString());
		// test with first base of first exon
		Assert.assertEquals("chr1:6640602", projector.transcriptToGenomePos(new TranscriptPosition(transForward, 136))
				.toString());
	}

	@Test
	public void testProjectionTranscriptToGenomeReverseSuccess() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		// test with first base of transcript
		Assert.assertEquals("chr1:23696357", projector.transcriptToGenomePos(new TranscriptPosition(transReverse, 1))
				.toString());
		// test with last base of transcript
		Assert.assertEquals("chr1:23685941", projector
				.transcriptToGenomePos(new TranscriptPosition(transReverse, 4493)).toString());
		// test with first base of first exon
		Assert.assertEquals("chr1:23694557", projector.transcriptToGenomePos(new TranscriptPosition(transReverse, 501))
				.toString());
	}

	@Test(expected = ProjectionException.class)
	public void testProjectionTranscriptToGenomeThrowsLeft() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);
		projector.transcriptToGenomePos(new TranscriptPosition(transForward, 0));
	}

	@Test(expected = ProjectionException.class)
	public void testProjectionTranscriptToGenomeThrowsRight() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);
		projector.transcriptToGenomePos(new TranscriptPosition(transForward, 2350));
	}

	@Test
	public void testLocateExonFromGenomePosForward() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		// first base of first exon
		Assert.assertEquals(0, projector.locateExon(new GenomePosition('+', 1, 6640063)));
		// last base of first exon
		Assert.assertEquals(0, projector.locateExon(new GenomePosition('+', 1, 6640196)));
		// just after last base of first exon
		Assert.assertEquals(INVALID_EXON_ID, projector.locateExon(new GenomePosition('+', 1, 6640197)));

		// just before first base of last exon
		Assert.assertEquals(INVALID_EXON_ID, projector.locateExon(new GenomePosition('+', 1, 6648974)));
		// first base of last exon
		Assert.assertEquals(10, projector.locateExon(new GenomePosition('+', 1, 6648976)));
		// last base of last exon
		Assert.assertEquals(10, projector.locateExon(new GenomePosition('+', 1, 6649340)));
	}

	@Test
	public void testLocateExonFromGenomePosReverse() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		// first base of first exon
		Assert.assertEquals(3, projector.locateExon(new GenomePosition('+', 1, 23685941)));
		// last base of first exon
		Assert.assertEquals(3, projector.locateExon(new GenomePosition('+', 1, 23689714)));
		// just after last base of first exon
		Assert.assertEquals(INVALID_EXON_ID, projector.locateExon(new GenomePosition('+', 1, 23689715)));

		// just before first base of last exon
		Assert.assertEquals(INVALID_EXON_ID, projector.locateExon(new GenomePosition('+', 1, 23695857)));
		// first base of last exon
		Assert.assertEquals(0, projector.locateExon(new GenomePosition('+', 1, 23695859)));
		// last base of last exon
		Assert.assertEquals(0, projector.locateExon(new GenomePosition('+', 1, 23696357)));
	}

	@Test
	public void testLocateExonFromTranscriptPosForward() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		// first base of first exon
		Assert.assertEquals(0, projector.locateExon(new TranscriptPosition(infoForward.transcriptModel, 1)));
		// last base of first exon
		Assert.assertEquals(0, projector.locateExon(new TranscriptPosition(infoForward.transcriptModel, 134)));
		// just after last base of first exon
		Assert.assertEquals(INVALID_EXON_ID, projector.locateExon(new GenomePosition('+', 1, 6640197)));

		// first base of last exon
		Assert.assertEquals(10, projector.locateExon(new TranscriptPosition(infoForward.transcriptModel, 1984)));
		// last base of last exon
		Assert.assertEquals(10, projector.locateExon(new TranscriptPosition(infoForward.transcriptModel, 2338)));
	}

	@Test
	public void testLocateExonFromTranscriptPosReverse() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		// first base of first exon
		Assert.assertEquals(3, projector.locateExon(new TranscriptPosition(infoReverse.transcriptModel, 723)));
		// last base of first exon
		Assert.assertEquals(3, projector.locateExon(new TranscriptPosition(infoReverse.transcriptModel, 4493)));

		// first base of last exon
		Assert.assertEquals(0, projector.locateExon(new TranscriptPosition(infoReverse.transcriptModel, 1)));
		// last base of last exon
		Assert.assertEquals(0, projector.locateExon(new TranscriptPosition(infoReverse.transcriptModel, 499)));
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
		Assert.assertEquals(new TranscriptPosition(transForward, 1, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition('+', 1, 6640063, PositionType.ONE_BASED)));
		// last base of first exon
		Assert.assertEquals(new TranscriptPosition(transForward, 134, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition('+', 1, 6640196, PositionType.ONE_BASED)));

		// first base of last exon
		Assert.assertEquals(new TranscriptPosition(transForward, 1974, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition('+', 1, 6648976, PositionType.ONE_BASED)));
		// last base of last exon
		Assert.assertEquals(new TranscriptPosition(transForward, 2338, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition('+', 1, 6649340, PositionType.ONE_BASED)));
	}

	@Test
	public void testGenomeToTranscriptPosReverse() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		// first base of first exon
		Assert.assertEquals(new TranscriptPosition(transReverse, 1, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition('+', 1, 23696357, PositionType.ONE_BASED)));
		// last base of first exon
		Assert.assertEquals(new TranscriptPosition(transReverse, 499, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition('+', 1, 23695859, PositionType.ONE_BASED)));

		// first base of last exon
		Assert.assertEquals(new TranscriptPosition(transReverse, 720, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition('+', 1, 23689714, PositionType.ONE_BASED)));
		// last base of last exon
		Assert.assertEquals(new TranscriptPosition(transReverse, 4493, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition('+', 1, 23685941, PositionType.ONE_BASED)));
	}

	@Test
	public void testGenomeToCDSPosForward() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		// first base in CDS (on second exon)
		Assert.assertEquals(new CDSPosition(transForward, 1, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition('+', 1, 6640670, PositionType.ONE_BASED)));
		// last base of second exon (first exon in CDS)
		Assert.assertEquals(new CDSPosition(transForward, 690, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition('+', 1, 6641359, PositionType.ONE_BASED)));

		// first base of last exon
		Assert.assertEquals(new CDSPosition(transForward, 1771, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition('+', 1, 6648976, PositionType.ONE_BASED)));
		// last base of CDS
		Assert.assertEquals(new CDSPosition(transForward, 2067, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition('+', 1, 6649272, PositionType.ONE_BASED)));
	}

	@Test
	public void testGenomeToCDSPosReverse() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		// 23688461\t23694498

		// first base of CDS (second exon)
		Assert.assertEquals(new CDSPosition(transReverse, 1, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition('+', 1, 23694498, PositionType.ONE_BASED)));
		// last base of second exon (first exon in CDS)
		Assert.assertEquals(new CDSPosition(transReverse, 33, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition('+', 1, 23694466, PositionType.ONE_BASED)));

		// first base of last exon
		Assert.assertEquals(new CDSPosition(transReverse, 161, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition('+', 1, 23689714, PositionType.ONE_BASED)));
		// last base of last exon
		Assert.assertEquals(new CDSPosition(transReverse, 1413, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition('+', 1, 23688462, PositionType.ONE_BASED)));
	}
}
