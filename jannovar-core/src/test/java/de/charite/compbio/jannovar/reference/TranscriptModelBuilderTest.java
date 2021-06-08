package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TranscriptModelBuilderTest {

	/**
	 * this test uses this static hg19 reference dictionary
	 */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	TranscriptModelBuilder builder;

	@BeforeEach
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

		Assertions.assertEquals(Strand.REV, info.getStrand());
		Assertions.assertEquals(1, info.getChr());
		Assertions.assertEquals("accession", info.getAccession());
		Assertions.assertEquals("ENTREZ10", info.getGeneID());
		Assertions.assertEquals("gene-symbol", info.getGeneSymbol());
		Assertions.assertEquals(builder.getTXRegion(), info.getTXRegion());
		Assertions.assertEquals(builder.getCDSRegion(), info.getCDSRegion());
		Assertions.assertEquals(builder.getSequence(), info.getSequence());
		Assertions.assertEquals(1, info.getExonRegions().size());
		Assertions.assertEquals(builder.getExonRegions().get(0), info.getExonRegions().get(0));
	}

}
