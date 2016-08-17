package de.charite.compbio.jannovar.impl.parse.gtfgff;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.impl.parse.gtfgff.FeatureRecord;
import de.charite.compbio.jannovar.impl.parse.gtfgff.GFFParser;

public class FeatureParserTest {

	InputStream stream;
	String lines;

	@Before
	public void setUp() throws Exception {
		lines = "3\tprotein_coding\texon\t129247483\t129247937\t.\t+\t.\tgene_id \"ENSG00000163914\"; transcript_id \"ENST00000296271\"; exon_number \"1\"; gene_name \"RHO\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; exon_id \"ENSE00001079597\";\n"
				+ "3\tprotein_coding\tCDS\t129247577\t129247937\t.\t+\t0\tgene_id \"ENSG00000163914\"; transcript_id \"ENST00000296271\"; exon_number \"1\"; gene_name \"RHO\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; protein_id \"ENSP00000296271\";\n"
				+ "3\tprotein_coding\tstart_codon\t129247577\t129247579\t.\t+\t0\tgene_id \"ENSG00000163914\"; transcript_id \"ENST00000296271\"; exon_number \"1\"; gene_name \"RHO\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\";\n"
				+ "3\tprotein_coding\texon\t129249719\t129249887\t.\t+\t.\tgene_id \"ENSG00000163914\"; transcript_id \"ENST00000296271\"; exon_number \"2\"; gene_name \"RHO\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; exon_id \"ENSE00001152211\";\n"
				+ "3\tprotein_coding\tCDS\t129249719\t129249887\t.\t+\t2\tgene_id \"ENSG00000163914\"; transcript_id \"ENST00000296271\"; exon_number \"2\"; gene_name \"RHO\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; protein_id \"ENSP00000296271\";\n"
				+ "3\tprotein_coding\texon\t129251094\t129251259\t.\t+\t.\tgene_id \"ENSG00000163914\"; transcript_id \"ENST00000296271\"; exon_number \"3\"; gene_name \"RHO\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; exon_id \"ENSE00001152205\";\n"
				+ "3\tprotein_coding\tCDS\t129251094\t129251259\t.\t+\t1\tgene_id \"ENSG00000163914\"; transcript_id \"ENST00000296271\"; exon_number \"3\"; gene_name \"RHO\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; protein_id \"ENSP00000296271\";\n"
				+ "3\tprotein_coding\texon\t129251376\t129251615\t.\t+\t.\tgene_id \"ENSG00000163914\"; transcript_id \"ENST00000296271\"; exon_number \"4\"; gene_name \"RHO\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; exon_id \"ENSE00001152199\";\n"
				+ "3\tprotein_coding\tCDS\t129251376\t129251615\t.\t+\t0\tgene_id \"ENSG00000163914\"; transcript_id \"ENST00000296271\"; exon_number \"4\"; gene_name \"RHO\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; protein_id \"ENSP00000296271\";\n"
				+ "3\tprotein_coding\texon\t129252451\t129254012\t.\t+\t.\tgene_id \"ENSG00000163914\"; transcript_id \"ENST00000296271\"; exon_number \"5\"; gene_name \"RHO\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; exon_id \"ENSE00001079599\";\n"
				+ "3\tprotein_coding\tCDS\t129252451\t129252558\t.\t+\t0\tgene_id \"ENSG00000163914\"; transcript_id \"ENST00000296271\"; exon_number \"5\"; gene_name \"RHO\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; protein_id \"ENSP00000296271\";\n"
				+ "3\tprotein_coding\tstop_codon\t129252559\t129252561\t.\t+\t0\tgene_id \"ENSG00000163914\"; transcript_id \"ENST00000296271\"; exon_number \"5\"; gene_name \"RHO\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\";\n";
		stream = new ByteArrayInputStream(lines.getBytes());
	}

	@Test
	public void test() throws IOException {
		GFFParser parser = new GFFParser(stream);

		ArrayList<FeatureRecord> records = new ArrayList<>();
		FeatureRecord record;
		while ((record = parser.next()) != null)
			records.add(record);

		Assert.assertEquals(12, records.size());
		Assert.assertEquals(
				"FeatureRecord [seqID=3, source=protein_coding, type=exon, begin=129247482, end=129247937, score=., strand=FORWARD, phase=0, attributes={exon_id=ENSE00001079597, exon_number=1, gene_biotype=protein_coding, gene_id=ENSG00000163914, gene_name=RHO, transcript_id=ENST00000296271, transcript_name=RHO-001}]",
				records.get(0).toString());
		Assert.assertEquals(
				"FeatureRecord [seqID=3, source=protein_coding, type=stop_codon, begin=129252558, end=129252561, score=., strand=FORWARD, phase=0, attributes={exon_number=5, gene_biotype=protein_coding, gene_id=ENSG00000163914, gene_name=RHO, transcript_id=ENST00000296271, transcript_name=RHO-001}]",
				records.get(11).toString());
	}

}
