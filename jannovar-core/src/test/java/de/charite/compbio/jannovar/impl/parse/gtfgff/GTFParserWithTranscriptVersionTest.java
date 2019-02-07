package de.charite.compbio.jannovar.impl.parse.gtfgff;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GTFParserWithTranscriptVersionTest {

	InputStream stream;
	String lines;

	@Before
	public void setUp() throws Exception {
		lines = "3\tensembl_havana\tgene\t129528640\t129535169\t.\t+\t.\tgene_id \"ENSG00000163914\"; gene_version \"4\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\";\n" +
			"3\tensembl_havana\ttranscript\t129528640\t129535169\t.\t+\t.\tgene_id \"ENSG00000163914\"; gene_version \"4\"; transcript_id \"ENST00000296271\"; transcript_version \"3\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; transcript_source \"ensembl_havana\"; transcript_biotype \"protein_coding\"; tag \"CCDS\"; ccds_id \"CCDS3063\";\n" +
			"3\tensembl_havana\texon\t129528640\t129529094\t.\t+\t.\tgene_id \"ENSG00000163914\"; gene_version \"4\"; transcript_id \"ENST00000296271\"; transcript_version \"3\"; exon_number \"1\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; transcript_source \"ensembl_havana\"; transcript_biotype \"protein_coding\"; tag \"CCDS\"; ccds_id \"CCDS3063\"; exon_id \"ENSE00001079597\"; exon_version \"4\";\n" +
			"3\tensembl_havana\tCDS\t129528734\t129529094\t.\t+\t0\tgene_id \"ENSG00000163914\"; gene_version \"4\"; transcript_id \"ENST00000296271\"; transcript_version \"3\"; exon_number \"1\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; transcript_source \"ensembl_havana\"; transcript_biotype \"protein_coding\"; tag \"CCDS\"; ccds_id \"CCDS3063\"; protein_id \"ENSP00000296271\"; protein_version \"3\";\n" +
			"3\tensembl_havana\tstart_codon\t129528734\t129528736\t.\t+\t0\tgene_id \"ENSG00000163914\"; gene_version \"4\"; transcript_id \"ENST00000296271\"; transcript_version \"3\"; exon_number \"1\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; transcript_source \"ensembl_havana\"; transcript_biotype \"protein_coding\"; tag \"CCDS\"; ccds_id \"CCDS3063\";\n" +
			"3\tensembl_havana\texon\t129530876\t129531044\t.\t+\t.\tgene_id \"ENSG00000163914\"; gene_version \"4\"; transcript_id \"ENST00000296271\"; transcript_version \"3\"; exon_number \"2\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; transcript_source \"ensembl_havana\"; transcript_biotype \"protein_coding\"; tag \"CCDS\"; ccds_id \"CCDS3063\"; exon_id \"ENSE00001152211\"; exon_version \"1\";\n" +
			"3\tensembl_havana\tCDS\t129530876\t129531044\t.\t+\t2\tgene_id \"ENSG00000163914\"; gene_version \"4\"; transcript_id \"ENST00000296271\"; transcript_version \"3\"; exon_number \"2\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; transcript_source \"ensembl_havana\"; transcript_biotype \"protein_coding\"; tag \"CCDS\"; ccds_id \"CCDS3063\"; protein_id \"ENSP00000296271\"; protein_version \"3\";\n" +
			"3\tensembl_havana\texon\t129532251\t129532416\t.\t+\t.\tgene_id \"ENSG00000163914\"; gene_version \"4\"; transcript_id \"ENST00000296271\"; transcript_version \"3\"; exon_number \"3\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; transcript_source \"ensembl_havana\"; transcript_biotype \"protein_coding\"; tag \"CCDS\"; ccds_id \"CCDS3063\"; exon_id \"ENSE00001152205\"; exon_version \"1\";\n" +
			"3\tensembl_havana\tCDS\t129532251\t129532416\t.\t+\t1\tgene_id \"ENSG00000163914\"; gene_version \"4\"; transcript_id \"ENST00000296271\"; transcript_version \"3\"; exon_number \"3\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; transcript_source \"ensembl_havana\"; transcript_biotype \"protein_coding\"; tag \"CCDS\"; ccds_id \"CCDS3063\"; protein_id \"ENSP00000296271\"; protein_version \"3\";\n" +
			"3\tensembl_havana\texon\t129532533\t129532772\t.\t+\t.\tgene_id \"ENSG00000163914\"; gene_version \"4\"; transcript_id \"ENST00000296271\"; transcript_version \"3\"; exon_number \"4\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; transcript_source \"ensembl_havana\"; transcript_biotype \"protein_coding\"; tag \"CCDS\"; ccds_id \"CCDS3063\"; exon_id \"ENSE00001152199\"; exon_version \"1\";\n" +
			"3\tensembl_havana\tCDS\t129532533\t129532772\t.\t+\t0\tgene_id \"ENSG00000163914\"; gene_version \"4\"; transcript_id \"ENST00000296271\"; transcript_version \"3\"; exon_number \"4\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; transcript_source \"ensembl_havana\"; transcript_biotype \"protein_coding\"; tag \"CCDS\"; ccds_id \"CCDS3063\"; protein_id \"ENSP00000296271\"; protein_version \"3\";\n" +
			"3\tensembl_havana\texon\t129533608\t129535169\t.\t+\t.\tgene_id \"ENSG00000163914\"; gene_version \"4\"; transcript_id \"ENST00000296271\"; transcript_version \"3\"; exon_number \"5\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; transcript_source \"ensembl_havana\"; transcript_biotype \"protein_coding\"; tag \"CCDS\"; ccds_id \"CCDS3063\"; exon_id \"ENSE00001079599\"; exon_version \"5\";\n" +
			"3\tensembl_havana\tCDS\t129533608\t129533715\t.\t+\t0\tgene_id \"ENSG00000163914\"; gene_version \"4\"; transcript_id \"ENST00000296271\"; transcript_version \"3\"; exon_number \"5\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; transcript_source \"ensembl_havana\"; transcript_biotype \"protein_coding\"; tag \"CCDS\"; ccds_id \"CCDS3063\"; protein_id \"ENSP00000296271\"; protein_version \"3\";\n" +
			"3\tensembl_havana\tstop_codon\t129533716\t129533718\t.\t+\t0\tgene_id \"ENSG00000163914\"; gene_version \"4\"; transcript_id \"ENST00000296271\"; transcript_version \"3\"; exon_number \"5\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; transcript_source \"ensembl_havana\"; transcript_biotype \"protein_coding\"; tag \"CCDS\"; ccds_id \"CCDS3063\";\n" +
			"3\tensembl_havana\tUTR\t129528640\t129528733\t.\t+\t.\tgene_id \"ENSG00000163914\"; gene_version \"4\"; transcript_id \"ENST00000296271\"; transcript_version \"3\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; transcript_source \"ensembl_havana\"; transcript_biotype \"protein_coding\"; tag \"CCDS\"; ccds_id \"CCDS3063\";\n" +
			"3\tensembl_havana\tUTR\t129533719\t129535169\t.\t+\t.\tgene_id \"ENSG00000163914\"; gene_version \"4\"; transcript_id \"ENST00000296271\"; transcript_version \"3\"; gene_name \"RHO\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\"; transcript_name \"RHO-001\"; transcript_source \"ensembl_havana\"; transcript_biotype \"protein_coding\"; tag \"CCDS\"; ccds_id \"CCDS3063\";\n";
		stream = new ByteArrayInputStream(lines.getBytes());
	}

	@Test
	public void test() throws IOException {
		GFFParser parser = new GFFParser(stream);

		ArrayList<FeatureRecord> records = new ArrayList<>();
		FeatureRecord record;
		while ((record = parser.next()) != null)
			records.add(record);

		Assert.assertEquals(16, records.size());
		Assert.assertEquals(
			"FeatureRecord [seqID=3, source=ensembl_havana, type=gene, begin=129528639, end=129535169, score=., strand=FORWARD, phase=0, attributes={gene_biotype=protein_coding, gene_id=ENSG00000163914, gene_name=RHO, gene_source=ensembl_havana, gene_version=4}]",
			records.get(0).toString());
		Assert.assertEquals(
			"FeatureRecord [seqID=3, source=ensembl_havana, type=UTR, begin=129533718, end=129535169, score=., strand=FORWARD, phase=0, attributes={ccds_id=CCDS3063, gene_biotype=protein_coding, gene_id=ENSG00000163914, gene_name=RHO, gene_source=ensembl_havana, gene_version=4, tag=CCDS, transcript_biotype=protein_coding, transcript_id=ENST00000296271, transcript_name=RHO-001, transcript_source=ensembl_havana, transcript_version=3}]",
			records.get(15).toString());
	}

}
