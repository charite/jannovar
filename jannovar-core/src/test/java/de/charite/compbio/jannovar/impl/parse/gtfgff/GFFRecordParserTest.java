package de.charite.compbio.jannovar.impl.parse.gtfgff;

import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.impl.parse.gtfgff.FeatureRecord;
import de.charite.compbio.jannovar.impl.parse.gtfgff.FeatureRecordParser;
import de.charite.compbio.jannovar.impl.parse.gtfgff.GFFRecordParser;

import org.junit.Assert;

public class GFFRecordParserTest {

	String line;

	@Before
	public void setUp() throws Exception {
		line = "3\tprotein_coding\texon\t129247483\t129247937\t.\t+\t.\tgene_id=ENSG00000163914;transcript_id=ENST00000296271;exon_number=1;gene_name=RHO;gene_biotype=protein_coding;transcript_name=RHO-001;exon_id=ENSE00001079597;\n";
	}

	@Test
	public void test() {
		FeatureRecordParser parser = new GFFRecordParser();
		FeatureRecord record = parser.parseLine(line);

		Assert.assertEquals(
				"FeatureRecord [seqID=3, source=protein_coding, type=exon, begin=129247482, end=129247937, score=., strand=FORWARD, phase=0, attributes={exon_id=ENSE00001079597, exon_number=1, gene_biotype=protein_coding, gene_id=ENSG00000163914, gene_name=RHO, transcript_id=ENST00000296271, transcript_name=RHO-001}]",
				record.toString());
	}

}
