package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.io.ReferenceDictionary;

public class TranscriptInfoBuilderTest {

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	TranscriptModelBuilder builder;

	@Before
	public void setUp() throws Exception {
		builder = new TranscriptModelBuilder();
	}

	@Test
	public void testReverse() {
		builder.setStrand(Strand.REV);
		builder.setAccession("accession");
		builder.setGeneID("ENTREZ10");
		builder.setGeneSymbol("gene-symbol");
		builder.setTXRegion(new GenomeInterval(refDict, Strand.FWD, 1, 100, 200, PositionType.ONE_BASED));
		builder.setCDSRegion(new GenomeInterval(refDict, Strand.FWD, 1, 110, 190, PositionType.ONE_BASED));
		builder.addExonRegion(new GenomeInterval(refDict, Strand.FWD, 1, 120, 170, PositionType.ONE_BASED));
		builder.setSequence("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

		TranscriptModel info = builder.build();

		Assert.assertEquals(Strand.REV, info.getStrand());
		Assert.assertEquals(1, info.getChr());
		Assert.assertEquals("accession", info.accession);
		Assert.assertEquals("ENTREZ10", info.geneID);
		Assert.assertEquals("gene-symbol", info.geneSymbol);
		Assert.assertEquals(builder.getTXRegion(), info.txRegion);
		Assert.assertEquals(builder.getCDSRegion(), info.cdsRegion);
		Assert.assertEquals(builder.getSequence(), info.sequence);
		Assert.assertEquals(1, info.exonRegions.size());
		Assert.assertEquals(builder.getExonRegions().get(0), info.exonRegions.get(0));
	}

}
