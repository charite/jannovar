package de.charite.compbio.jannovar.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.impl.parse.gff.Feature;
import de.charite.compbio.jannovar.impl.parse.gff.FeatureType;
import de.charite.compbio.jannovar.impl.parse.gff.GFFParser;
import de.charite.compbio.jannovar.impl.parse.gff.GFFVersion;

public class GFFparserTest {

	private File tmpFile = null;

	@Before
	public void setUp() throws Exception {
		// TODO(holtgrem): Somehow, this is used nowhere.
		/*
		 * this.tmpFile = File.createTempFile("gff3reader-test", "gff3reader-test"); PrintStream ps = new
		 * PrintStream(new FileOutputStream(tmpFile));
		 *
		 * ps.append("##gff-version 3\n"); ps.append("##sequence-region ctg123 1 1497228\n");
		 * ps.append("ctg123\t.\tgene\t1000\t9000\t.\t+\t.\tID=gene00001;Name=EDEN\n");
		 * ps.append("ctg123\t.\tTF_binding_site\t1000\t1012\t.\t+\t.\tID=tfbs00001;Parent=gene00001\n");
		 * ps.append("ctg123\t.\tmRNA\t1050\t9000\t.\t+\t.\tID=mRNA00001;Parent=gene00001;Name=EDEN.1\n");
		 * ps.append("ctg123\t.\tmRNA\t1050\t9000\t.\t+\t.\tID=mRNA00002;Parent=gene00001;Name=EDEN.2\n");
		 * ps.append("ctg123\t.\tmRNA\t1300\t9000\t.\t+\t.\tID=mRNA00003;Parent=gene00001;Name=EDEN.3\n");
		 * ps.append("ctg123\t.\texon\t1300\t1500\t.\t+\t.\tID=exon00001;Parent=mRNA00003\n");
		 * ps.append("ctg123\t.\texon\t1050\t1500\t.\t+\t.\tID=exon00002;Parent=mRNA00001,mRNA00002\n");
		 * ps.append("ctg123\t.\texon\t3000\t3902\t.\t+\t.\tID=exon00003;Parent=mRNA00001,mRNA00003\n");
		 * ps.append("ctg123\t.\texon\t5000\t5500\t.\t+\t.\tID=exon00004;Parent=mRNA00001,mRNA00002,mRNA00003\n");
		 * ps.append("ctg123\t.\texon\t7000\t9000\t.\t+\t.\tID=exon00005;Parent=mRNA00001,mRNA00002,mRNA00003\n");
		 * ps.append("ctg123\t.\tCDS\t1201\t1500\t.\t+\t0\tID=cds00001;Parent=mRNA00001;Name=edenprotein.1\n");
		 * ps.append("ctg123\t.\tCDS\t3000\t3902\t.\t+\t0\tID=cds00001;Parent=mRNA00001;Name=edenprotein.1\n");
		 * ps.append("ctg123\t.\tCDS\t5000\t5500\t.\t+\t0\tID=cds00001;Parent=mRNA00001;Name=edenprotein.1\n");
		 * ps.append("ctg123\t.\tCDS\t7000\t7600\t.\t+\t0\tID=cds00001;Parent=mRNA00001;Name=edenprotein.1\n");
		 * ps.append("ctg123\t.\tCDS\t1201\t1500\t.\t+\t0\tID=cds00002;Parent=mRNA00002;Name=edenprotein.2\n");
		 * ps.append("ctg123\t.\tCDS\t5000\t5500\t.\t+\t0\tID=cds00002;Parent=mRNA00002;Name=edenprotein.2\n");
		 * ps.append("ctg123\t.\tCDS\t7000\t7600\t.\t+\t0\tID=cds00002;Parent=mRNA00002;Name=edenprotein.2\n");
		 * ps.append("ctg123\t.\tCDS\t3301\t3902\t.\t+\t0\tID=cds00003;Parent=mRNA00003;Name=edenprotein.3\n");
		 * ps.append("ctg123\t.\tCDS\t5000\t5500\t.\t+\t1\tID=cds00003;Parent=mRNA00003;Name=edenprotein.3\n");
		 * ps.append("ctg123\t.\tCDS\t7000\t7600\t.\t+\t1\tID=cds00003;Parent=mRNA00003;Name=edenprotein.3\n");
		 * ps.append("ctg123\t.\tCDS\t3391\t3902\t.\t+\t0\tID=cds00004;Parent=mRNA00003;Name=edenprotein.4\n");
		 * ps.append("ctg123\t.\tCDS\t5000\t5500\t.\t+\t1\tID=cds00004;Parent=mRNA00003;Name=edenprotein.4\n");
		 * ps.append("ctg123\t.\tCDS\t7000\t7600\t.\t+\t1\tID=cds00004;Parent=mRNA00003;Name=edenprotein.4\n");
		 * ps.close();
		 */
	}

	@After
	public void tearDown() {
		if (tmpFile != null)
			tmpFile.delete();
		tmpFile = null;
	}

	private void writeGFF(String fileContents) throws IOException {
		this.tmpFile = File.createTempFile("gff3reader-test", "gff3reader-test");
		PrintStream ps = new PrintStream(new FileOutputStream(tmpFile));
		ps.append("ctg123\t.\texon\t5000\t5500\t.\t+\t.\tID=exon00004;Parent=mRNA00001,mRNA00002,mRNA00003\n");
		ps.close();
	}

	@Test
	public void testProcessFeatureRNAGFF3() throws IOException {
		String line = "ctg123\t.\texon\t5000\t5500\t.\t+\t.\tID=exon00004;Parent=mRNA00001,mRNA00002,mRNA00003";
		writeGFF(line);

		GFFParser reader = new GFFParser(tmpFile.getAbsolutePath(), new GFFVersion(3), false);
		Feature feature = reader.parseFeature(line);
		Assert.assertEquals(FeatureType.EXON, feature.getType());
		Assert.assertEquals(5000, feature.getStart());
		Assert.assertEquals(5500, feature.getEnd());
		// Assert.assertEquals('.', feature.getPhase());
		Assert.assertEquals(true, feature.getStrand());
		// Assert.assertEquals('.', feature.getScore());
		Assert.assertEquals("ctg123", feature.getSequenceID());
		Assert.assertEquals(2, feature.getAttributes().size());
		Assert.assertEquals("exon00004", feature.getAttributes().get("ID"));
		Assert.assertEquals("mRNA00001,mRNA00002,mRNA00003", feature.getAttributes().get("Parent"));
	}

	@Test
	public void testProcessFeatureGeneGFF3() throws IOException {
		String line = "ctg123\t.\tgene\t1000\t9000\t.\t+\t.\tID=gene00001;Name=EDEN";
		writeGFF(line);

		GFFParser reader = new GFFParser(tmpFile.getAbsolutePath(), new GFFVersion(3), false);
		Feature feature = reader.parseFeature(line);
		Assert.assertEquals(FeatureType.GENE, feature.getType());
		Assert.assertEquals(1000, feature.getStart());
		Assert.assertEquals(9000, feature.getEnd());
		// Assert.assertEquals('.', feature.getPhase());
		Assert.assertEquals(true, feature.getStrand());
		// Assert.assertEquals('.', feature.getScore());
		Assert.assertEquals("ctg123", feature.getSequenceID());
		Assert.assertEquals(2, feature.getAttributes().size());
		Assert.assertEquals("gene00001", feature.getAttributes().get("ID"));
		Assert.assertEquals("EDEN", feature.getAttributes().get("Name"));
	}

	@Test
	public void testProcessFeature001GFF2() throws IOException {
		String line = "18	protein_coding	exon	246324	246433	.	-	.	gene_id \"ENSG00000079134\"; transcript_id \"ENST00000579891\"; exon_number \"1\"; gene_name \"THOC1\"; gene_biotype \"protein_coding\"; transcript_name \"THOC1-020\"; exon_id \"ENSE00002716487\";";
		writeGFF(line);

		GFFParser reader = new GFFParser(tmpFile.getAbsolutePath(), new GFFVersion(2), false);
		Feature feature = reader.parseFeature(line);
		Assert.assertEquals(FeatureType.EXON, feature.getType());
		Assert.assertEquals(246324, feature.getStart());
		Assert.assertEquals(246433, feature.getEnd());
		// Assert.assertEquals('.', feature.getPhase());
		Assert.assertEquals(false, feature.getStrand());
		// Assert.assertEquals('.', feature.getScore());
		Assert.assertEquals("18", feature.getSequenceID());
		Assert.assertEquals(7, feature.getAttributes().size());
		Assert.assertEquals("ENSG00000079134", feature.getAttributes().get("gene_id"));
		Assert.assertEquals("ENST00000579891", feature.getAttributes().get("transcript_id"));
		Assert.assertEquals("1", feature.getAttributes().get("exon_number"));
		Assert.assertEquals("THOC1", feature.getAttributes().get("gene_name"));
		Assert.assertEquals("protein_coding", feature.getAttributes().get("gene_biotype"));
		Assert.assertEquals("THOC1-020", feature.getAttributes().get("transcript_name"));
		Assert.assertEquals("ENSE00002716487", feature.getAttributes().get("exon_id"));
	}

}
