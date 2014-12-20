package jannovar.reference;

import jannovar.io.ReferenceDictionary;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TranscriptInfoBuilderTest {

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	TranscriptInfoBuilder builder;

	@Before
	public void setUp() throws Exception {
		builder = new TranscriptInfoBuilder();
	}

	@Test
	public void testReverse() {
		builder.setStrand('-');
		builder.setAccession("accession");
		builder.setGeneID(10);
		builder.setGeneSymbol("gene-symbol");
		builder.setTxRegion(new GenomeInterval(refDict, '+', 1, 100, 200, PositionType.ONE_BASED));
		builder.setCdsRegion(new GenomeInterval(refDict, '+', 1, 110, 190, PositionType.ONE_BASED));
		builder.addExonRegion(new GenomeInterval(refDict, '+', 1, 120, 170, PositionType.ONE_BASED));
		builder.setSequence("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

		TranscriptInfo info = builder.make();

		Assert.assertEquals('-', info.getStrand());
		Assert.assertEquals(1, info.getChr());
		Assert.assertEquals("accession", info.accession);
		Assert.assertEquals(10, info.geneID);
		Assert.assertEquals("gene-symbol", info.geneSymbol);
		Assert.assertEquals(builder.getTxRegion(), info.txRegion);
		Assert.assertEquals(builder.getCdsRegion(), info.cdsRegion);
		Assert.assertEquals(builder.getSequence(), info.sequence);
		Assert.assertEquals(1, info.exonRegions.size());
		Assert.assertEquals(builder.getExonRegions().get(0), info.exonRegions.get(0));
	}

}
