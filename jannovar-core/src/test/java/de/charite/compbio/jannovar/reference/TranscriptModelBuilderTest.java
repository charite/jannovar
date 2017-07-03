package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.data.ReferenceDictionary;

public class TranscriptModelBuilderTest {

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
		Assert.assertEquals("accession", info.getAccession());
		Assert.assertEquals("ENTREZ10", info.getGeneID());
		Assert.assertEquals("gene-symbol", info.getGeneSymbol());
		Assert.assertEquals(builder.getTXRegion(), info.getTXRegion());
		Assert.assertEquals(builder.getCDSRegion(), info.getCDSRegion());
		Assert.assertEquals(builder.getSequence(), info.getSequence());
		Assert.assertEquals(1, info.getExonRegions().size());
		Assert.assertEquals(builder.getExonRegions().get(0), info.getExonRegions().get(0));
	}

}
