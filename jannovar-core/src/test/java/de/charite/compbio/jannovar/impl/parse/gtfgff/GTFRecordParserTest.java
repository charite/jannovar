package de.charite.compbio.jannovar.impl.parse.gtfgff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GTFRecordParserTest {

	String line;

	@BeforeEach
	public void setUp() throws Exception {
		line = "3\tprotein_coding\texon\t129247483\t129247937\t.\t+\t.\tgene_id \"ENSG00000163914\"; transcript_id \"ENST00000296271\"; exon_number \"1\"; gene_name \"RHO\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; exon_id \"ENSE00001079597\";\n";
	}

	@Test
	public void test() {
		FeatureRecordParser parser = new GTFRecordParser();
		FeatureRecord record = parser.parseLine(line);

		Assertions.assertEquals(
			"FeatureRecord [seqID=3, source=protein_coding, type=exon, begin=129247482, end=129247937, score=., strand=FORWARD, phase=0, attributes={exon_id=ENSE00001079597, exon_number=1, gene_biotype=protein_coding, gene_id=ENSG00000163914, gene_name=RHO, transcript_id=ENST00000296271, transcript_name=RHO-001}]",
			record.toString());
	}

}
