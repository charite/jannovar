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
		Assert.assertEquals("chr1:6640062", projector.transcriptToGenomePos(new TranscriptPosition(transForward, 1))
				.toString());
		// test with last base of transcript
		Assert.assertEquals("chr1:6649340", projector.transcriptToGenomePos(new TranscriptPosition(transForward, 2349))
				.toString());
		// test with first base of first exon
		Assert.assertEquals("chr1:6640600", projector.transcriptToGenomePos(new TranscriptPosition(transForward, 136))
				.toString());
	}

	@Test
	public void testProjectionTranscriptToGenomeReverseSuccess() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		// test with first base of transcript
		Assert.assertEquals("chr1:23696357", projector.transcriptToGenomePos(new TranscriptPosition(transReverse, 1))
				.toString());
		// test with last base of transcript
		Assert.assertEquals("chr1:23685940", projector
				.transcriptToGenomePos(new TranscriptPosition(transReverse, 4497))
				.toString());
		// test with first base of first exon
		Assert.assertEquals("chr1:23694558", projector.transcriptToGenomePos(new TranscriptPosition(transReverse, 501))
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
	public void testLocateExonForward() throws ProjectionException {
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
		Assert.assertEquals(10, projector.locateExon(new GenomePosition('+', 1, 6648975)));
		// last base of last exon
		Assert.assertEquals(10, projector.locateExon(new GenomePosition('+', 1, 6649340)));
	}

	@Test
	public void testLocateExonReverse() throws ProjectionException {
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
		Assert.assertEquals(0, projector.locateExon(new GenomePosition('+', 1, 23695858)));
		// last base of last exon
		Assert.assertEquals(0, projector.locateExon(new GenomePosition('+', 1, 23696357)));
	}
}
