package de.charite.compbio.jannovar.reference;

import static de.charite.compbio.jannovar.reference.TranscriptProjectionDecorator.INVALID_EXON_ID;
import static de.charite.compbio.jannovar.reference.TranscriptProjectionDecorator.INVALID_INTRON_ID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.io.ReferenceDictionary;

/**
 * Tests for the coordinate conversion decorator.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
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
		Assert.assertEquals("1:6640063",
				projector.transcriptToGenomePos(new TranscriptPosition(infoForward, 1, PositionType.ONE_BASED))
				.toString());
		// test with last base of transcript
		Assert.assertEquals("1:6649340",
				projector.transcriptToGenomePos(new TranscriptPosition(infoForward, 2338, PositionType.ONE_BASED))
				.toString());
		// test with first base of first exon
		Assert.assertEquals("1:6640602",
				projector.transcriptToGenomePos(new TranscriptPosition(infoForward, 136, PositionType.ONE_BASED))
				.toString());
	}

	@Test
	public void testProjectionTranscriptToGenomeReverseSuccess() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		// test with first base of transcript
		Assert.assertEquals("1:23696357",
				projector.transcriptToGenomePos(new TranscriptPosition(infoReverse, 1, PositionType.ONE_BASED))
				.toString());
		// test with last base of transcript
		Assert.assertEquals("1:23685941",
				projector.transcriptToGenomePos(new TranscriptPosition(infoReverse, 4493, PositionType.ONE_BASED))
				.toString());
		// test with first base of first exon
		Assert.assertEquals("1:23694557",
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
				projector.locateIntron(new GenomePosition(refDict, '+', 1, 6640196, PositionType.ONE_BASED)));
		// first base of first intron
		Assert.assertEquals(0,
				projector.locateIntron(new GenomePosition(refDict, '+', 1, 6640197, PositionType.ONE_BASED)));
		// last base of first intron
		Assert.assertEquals(0,
				projector.locateIntron(new GenomePosition(refDict, '+', 1, 6640600, PositionType.ONE_BASED)));
		// first base of second exon
		Assert.assertEquals(INVALID_INTRON_ID,
				projector.locateIntron(new GenomePosition(refDict, '+', 1, 6640601, PositionType.ONE_BASED)));

		// last base of second-last exon
		Assert.assertEquals(INVALID_INTRON_ID,
				projector.locateIntron(new GenomePosition(refDict, '+', 1, 6648904, PositionType.ONE_BASED)));
		// first base of last intron
		Assert.assertEquals(9,
				projector.locateIntron(new GenomePosition(refDict, '+', 1, 6648905, PositionType.ONE_BASED)));
		// last base of last intron
		Assert.assertEquals(9,
				projector.locateIntron(new GenomePosition(refDict, '+', 1, 6648975, PositionType.ONE_BASED)));
		// first base of last exon
		Assert.assertEquals(INVALID_INTRON_ID,
				projector.locateIntron(new GenomePosition(refDict, '+', 1, 6648976, PositionType.ONE_BASED)));
	}

	@Test
	public void testLocateExonFromGenomePosForward() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		// first base of first exon
		Assert.assertEquals(0,
				projector.locateExon(new GenomePosition(refDict, '+', 1, 6640063, PositionType.ONE_BASED)));
		// last base of first exon
		Assert.assertEquals(0,
				projector.locateExon(new GenomePosition(refDict, '+', 1, 6640196, PositionType.ONE_BASED)));
		// just after last base of first exon
		Assert.assertEquals(INVALID_EXON_ID,
				projector.locateExon(new GenomePosition(refDict, '+', 1, 6640197, PositionType.ONE_BASED)));

		// just before first base of last exon
		Assert.assertEquals(INVALID_EXON_ID,
				projector.locateExon(new GenomePosition(refDict, '+', 1, 6648974, PositionType.ONE_BASED)));
		// first base of last exon
		Assert.assertEquals(10,
				projector.locateExon(new GenomePosition(refDict, '+', 1, 6648976, PositionType.ONE_BASED)));
		// last base of last exon
		Assert.assertEquals(10,
				projector.locateExon(new GenomePosition(refDict, '+', 1, 6649340, PositionType.ONE_BASED)));
	}

	@Test
	public void testLocateExonFromGenomePosReverse() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		// first base of first exon
		Assert.assertEquals(3,
				projector.locateExon(new GenomePosition(refDict, '+', 1, 23685941, PositionType.ONE_BASED)));
		// last base of first exon
		Assert.assertEquals(3,
				projector.locateExon(new GenomePosition(refDict, '+', 1, 23689714, PositionType.ONE_BASED)));
		// just after last base of first exon
		Assert.assertEquals(INVALID_EXON_ID,
				projector.locateExon(new GenomePosition(refDict, '+', 1, 23689715, PositionType.ONE_BASED)));

		// just before first base of last exon
		Assert.assertEquals(INVALID_EXON_ID,
				projector.locateExon(new GenomePosition(refDict, '+', 1, 23695857, PositionType.ONE_BASED)));
		// first base of last exon
		Assert.assertEquals(0,
				projector.locateExon(new GenomePosition(refDict, '+', 1, 23695859, PositionType.ONE_BASED)));
		// last base of last exon
		Assert.assertEquals(0,
				projector.locateExon(new GenomePosition(refDict, '+', 1, 23696357, PositionType.ONE_BASED)));
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
				projector.locateExon(new GenomePosition(refDict, '+', 1, 6640197, PositionType.ONE_BASED)));

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
		Assert.assertEquals(new TranscriptPosition(infoForward, 1, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition(refDict, '+', 1, 6640063, PositionType.ONE_BASED)));
		// last base of first exon
		Assert.assertEquals(new TranscriptPosition(infoForward, 134, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition(refDict, '+', 1, 6640196, PositionType.ONE_BASED)));

		// first base of last exon
		Assert.assertEquals(new TranscriptPosition(infoForward, 1974, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition(refDict, '+', 1, 6648976, PositionType.ONE_BASED)));
		// last base of last exon
		Assert.assertEquals(new TranscriptPosition(infoForward, 2338, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition(refDict, '+', 1, 6649340, PositionType.ONE_BASED)));
	}

	@Test
	public void testGenomeToTranscriptPosReverse() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		// first base of first exon
		Assert.assertEquals(new TranscriptPosition(infoReverse, 1, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition(refDict, '+', 1, 23696357, PositionType.ONE_BASED)));
		// last base of first exon
		Assert.assertEquals(new TranscriptPosition(infoReverse, 499, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition(refDict, '+', 1, 23695859, PositionType.ONE_BASED)));

		// first base of last exon
		Assert.assertEquals(new TranscriptPosition(infoReverse, 720, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition(refDict, '+', 1, 23689714, PositionType.ONE_BASED)));
		// last base of last exon
		Assert.assertEquals(new TranscriptPosition(infoReverse, 4493, PositionType.ONE_BASED),
				projector.genomeToTranscriptPos(new GenomePosition(refDict, '+', 1, 23685941, PositionType.ONE_BASED)));
	}

	@Test
	public void testGenomeToCDSPosForward() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoForward);

		// first base in CDS (on second exon)
		Assert.assertEquals(new CDSPosition(infoForward, 1, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, '+', 1, 6640670, PositionType.ONE_BASED)));
		// last base of second exon (first exon in CDS)
		Assert.assertEquals(new CDSPosition(infoForward, 690, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, '+', 1, 6641359, PositionType.ONE_BASED)));

		// first base of last exon
		Assert.assertEquals(new CDSPosition(infoForward, 1771, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, '+', 1, 6648976, PositionType.ONE_BASED)));
		// last base of CDS
		Assert.assertEquals(new CDSPosition(infoForward, 2067, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, '+', 1, 6649272, PositionType.ONE_BASED)));
	}

	@Test
	public void testGenomeToCDSPosReverse() throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(infoReverse);

		// 23688461\t23694498

		// first base of CDS (second exon)
		Assert.assertEquals(new CDSPosition(infoReverse, 1, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, '+', 1, 23694498, PositionType.ONE_BASED)));
		// last base of second exon (first exon in CDS)
		Assert.assertEquals(new CDSPosition(infoReverse, 33, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, '+', 1, 23694466, PositionType.ONE_BASED)));

		// first base of last exon
		Assert.assertEquals(new CDSPosition(infoReverse, 161, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, '+', 1, 23689714, PositionType.ONE_BASED)));
		// last base of last exon
		Assert.assertEquals(new CDSPosition(infoReverse, 1413, PositionType.ONE_BASED),
				projector.genomeToCDSPos(new GenomePosition(refDict, '+', 1, 23688462, PositionType.ONE_BASED)));
	}
}
