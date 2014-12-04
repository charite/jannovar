package jannovar.reference;

import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

/**
 * Tests for the TranscriptInfo class.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptInfoTest {

	/** transcript on forward strand */
	TranscriptModel transcriptForward;
	/** transcript on reverse strand */
	TranscriptModel transcriptReverse;

	@Before
	public void setUp() {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc009vmz.1\tchr1\t+\t11539294\t11541938\t11539294\t11539294\t2\t"
						+ "11539294,11541314,\t11539429,11541938,\tuc009vmz.1");

		this.transcriptReverse = TranscriptModelFactory
				.parseKnownGenesLine("uc009vjr.2\tchr1\t-\t893648\t894679\t894010\t894620\t2\t"
						+ "893648,894594,\t894461,894679,\tuc009vjr.2");
	}

	@Test
	public void testForwardTranscript() {
		TranscriptInfo info = new TranscriptInfo(this.transcriptForward);

		Assert.assertEquals(info.accession, this.transcriptForward.getAccessionNumber());
		Assert.assertEquals(info.geneSymbol, this.transcriptForward.getGeneSymbol());

		Assert.assertEquals(info.txRegion.strand, '+');
		Assert.assertEquals(info.txRegion.chr, this.transcriptForward.getChromosome());
		Assert.assertEquals(info.txRegion.beginPos, this.transcriptForward.getTXStart());
		Assert.assertEquals(info.txRegion.endPos, this.transcriptForward.getTXEnd());

		Assert.assertEquals(info.cdsRegion.strand, '+');
		Assert.assertEquals(info.cdsRegion.chr, this.transcriptForward.getChromosome());
		Assert.assertEquals(info.cdsRegion.beginPos, this.transcriptForward.getCDSStart());
		Assert.assertEquals(info.cdsRegion.endPos, this.transcriptForward.getCDSEnd());

		Assert.assertEquals(info.exonRegions.size(), 2);
		Assert.assertEquals(info.exonRegions.get(0).strand, '+');
		Assert.assertEquals(info.exonRegions.get(0).chr, this.transcriptForward.getChromosome());
		Assert.assertEquals(info.exonRegions.get(0).beginPos, this.transcriptForward.getExonStart(0));
		Assert.assertEquals(info.exonRegions.get(0).endPos, this.transcriptForward.getExonEnd(0));
		Assert.assertEquals(info.exonRegions.get(1).strand, '+');
		Assert.assertEquals(info.exonRegions.get(1).chr, this.transcriptForward.getChromosome());
		Assert.assertEquals(info.exonRegions.get(1).beginPos, this.transcriptForward.getExonStart(1));
		Assert.assertEquals(info.exonRegions.get(1).endPos, this.transcriptForward.getExonEnd(1));

		Assert.assertEquals(info.sequence, this.transcriptForward.getSequence());

		Assert.assertEquals(0, info.cdsTranscriptLength());
		Assert.assertEquals(759, info.transcriptLength());
	}

	@Test
	public void testReverseTranscript() {
		TranscriptInfo info = new TranscriptInfo(this.transcriptReverse);

		Assert.assertEquals(info.accession, this.transcriptReverse.getAccessionNumber());
		Assert.assertEquals(info.geneSymbol, this.transcriptReverse.getGeneSymbol());

		Assert.assertEquals(info.txRegion.strand, '-');
		Assert.assertEquals(info.txRegion.chr, this.transcriptReverse.getChromosome());
		Assert.assertEquals(info.txRegion.beginPos, 248355943);
		Assert.assertEquals(info.txRegion.endPos, 248356973);

		Assert.assertEquals(info.cdsRegion.strand, this.transcriptReverse.getStrand());
		Assert.assertEquals(info.cdsRegion.chr, this.transcriptReverse.getChromosome());
		Assert.assertEquals(info.cdsRegion.beginPos, 248356002);
		Assert.assertEquals(info.cdsRegion.endPos, 248356611);

		Assert.assertEquals(info.exonRegions.size(), 2);
		Assert.assertEquals(info.exonRegions.get(0).positionType, PositionType.ONE_BASED);
		Assert.assertEquals(info.exonRegions.get(0).strand, this.transcriptReverse.getStrand());
		Assert.assertEquals(info.exonRegions.get(0).chr, this.transcriptReverse.getChromosome());
		Assert.assertEquals(info.exonRegions.get(0).beginPos, 248355943);
		Assert.assertEquals(info.exonRegions.get(0).endPos, 248356027);
		Assert.assertEquals(info.exonRegions.get(1).positionType, PositionType.ONE_BASED);
		Assert.assertEquals(info.exonRegions.get(1).strand, this.transcriptReverse.getStrand());
		Assert.assertEquals(info.exonRegions.get(1).chr, this.transcriptReverse.getChromosome());
		Assert.assertEquals(info.exonRegions.get(1).beginPos, 248356161);
		Assert.assertEquals(info.exonRegions.get(1).endPos, 248356973);

		Assert.assertEquals(info.sequence, this.transcriptReverse.getSequence());
		Assert.assertEquals(477, info.cdsTranscriptLength());
		Assert.assertEquals(898, info.transcriptLength());
	}
}
