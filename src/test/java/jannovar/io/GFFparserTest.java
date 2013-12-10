package jannovar.io;

import static org.junit.Assert.*;
import jannovar.common.FeatureType;
import jannovar.exception.FeatureFormatException;
import jannovar.gff.Feature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class GFFparserTest {

	private static GFFparser reader = null;

	@Before
	public void setUp() throws Exception {
		File tmp = File.createTempFile("gff3reader-test", "gff3reader-test");
		PrintStream ps = new PrintStream(new FileOutputStream(tmp));

		ps.append("##gff-version 3\n");
		ps.append("##sequence-region ctg123 1 1497228\n");
		ps.append("ctg123\t.\tgene\t1000\t9000\t.\t+\t.\tID=gene00001;Name=EDEN\n");
		ps.append("ctg123\t.\tTF_binding_site\t1000\t1012\t.\t+\t.\tID=tfbs00001;Parent=gene00001\n");
		ps.append("ctg123\t.\tmRNA\t1050\t9000\t.\t+\t.\tID=mRNA00001;Parent=gene00001;Name=EDEN.1\n");
		ps.append("ctg123\t.\tmRNA\t1050\t9000\t.\t+\t.\tID=mRNA00002;Parent=gene00001;Name=EDEN.2\n");
		ps.append("ctg123\t.\tmRNA\t1300\t9000\t.\t+\t.\tID=mRNA00003;Parent=gene00001;Name=EDEN.3\n");
		ps.append("ctg123\t.\texon\t1300\t1500\t.\t+\t.\tID=exon00001;Parent=mRNA00003\n");
		ps.append("ctg123\t.\texon\t1050\t1500\t.\t+\t.\tID=exon00002;Parent=mRNA00001,mRNA00002\n");
		ps.append("ctg123\t.\texon\t3000\t3902\t.\t+\t.\tID=exon00003;Parent=mRNA00001,mRNA00003\n");
		ps.append("ctg123\t.\texon\t5000\t5500\t.\t+\t.\tID=exon00004;Parent=mRNA00001,mRNA00002,mRNA00003\n");
		ps.append("ctg123\t.\texon\t7000\t9000\t.\t+\t.\tID=exon00005;Parent=mRNA00001,mRNA00002,mRNA00003\n");
		ps.append("ctg123\t.\tCDS\t1201\t1500\t.\t+\t0\tID=cds00001;Parent=mRNA00001;Name=edenprotein.1\n");
		ps.append("ctg123\t.\tCDS\t3000\t3902\t.\t+\t0\tID=cds00001;Parent=mRNA00001;Name=edenprotein.1\n");
		ps.append("ctg123\t.\tCDS\t5000\t5500\t.\t+\t0\tID=cds00001;Parent=mRNA00001;Name=edenprotein.1\n");
		ps.append("ctg123\t.\tCDS\t7000\t7600\t.\t+\t0\tID=cds00001;Parent=mRNA00001;Name=edenprotein.1\n");
		ps.append("ctg123\t.\tCDS\t1201\t1500\t.\t+\t0\tID=cds00002;Parent=mRNA00002;Name=edenprotein.2\n");
		ps.append("ctg123\t.\tCDS\t5000\t5500\t.\t+\t0\tID=cds00002;Parent=mRNA00002;Name=edenprotein.2\n");
		ps.append("ctg123\t.\tCDS\t7000\t7600\t.\t+\t0\tID=cds00002;Parent=mRNA00002;Name=edenprotein.2\n");
		ps.append("ctg123\t.\tCDS\t3301\t3902\t.\t+\t0\tID=cds00003;Parent=mRNA00003;Name=edenprotein.3\n");
		ps.append("ctg123\t.\tCDS\t5000\t5500\t.\t+\t1\tID=cds00003;Parent=mRNA00003;Name=edenprotein.3\n");
		ps.append("ctg123\t.\tCDS\t7000\t7600\t.\t+\t1\tID=cds00003;Parent=mRNA00003;Name=edenprotein.3\n");
		ps.append("ctg123\t.\tCDS\t3391\t3902\t.\t+\t0\tID=cds00004;Parent=mRNA00003;Name=edenprotein.4\n");
		ps.append("ctg123\t.\tCDS\t5000\t5500\t.\t+\t1\tID=cds00004;Parent=mRNA00003;Name=edenprotein.4\n");
		ps.append("ctg123\t.\tCDS\t7000\t7600\t.\t+\t1\tID=cds00004;Parent=mRNA00003;Name=edenprotein.4\n");
		ps.close();

//		reader = new GFFparser(tmp.getAbsolutePath());
		reader = new GFFparser();
//		reader.parse("data/interim_GRCh37.p13_top_level_2013-07-05.gff3.gz");
	}

	@AfterClass
	public static void releaseResources() {
		reader = null;
		System.gc();
	}

//	@Test
//	public void testGFFversion() {
//		assertEquals(3, reader.getGFFversion());
//	}
	
	@Test
	public void testProcessFeatureRNAGFF3(){
		String line = "ctg123\t.\texon\t5000\t5500\t.\t+\t.\tID=exon00004;Parent=mRNA00001,mRNA00002,mRNA00003";
		try {
			reader.setGFFversion(3);
			Feature feature	= reader.processFeature(line);
			assertEquals(FeatureType.EXON, feature.getType());
			assertEquals(5000, feature.getStart());
			assertEquals(5500, feature.getEnd());
//			assertEquals('.', feature.getPhase());
			assertEquals(true, feature.getStrand());
//			assertEquals('.', feature.getScore());
			assertEquals("ctg123", feature.getSequence_id());
			assertEquals(2, feature.getAttributes().size());
			assertEquals("exon00004", feature.getAttribute("ID"));
			assertEquals("mRNA00001,mRNA00002,mRNA00003", feature.getAttribute("Parent"));
			
		} catch (FeatureFormatException e) {
			fail("misformed feature line: "+line+"\n"+e);
			e.printStackTrace();
		}
	}
	
	@Test
	public void testProcessFeatureGeneGFF3(){
		String line = "ctg123\t.\tgene\t1000\t9000\t.\t+\t.\tID=gene00001;Name=EDEN";
		try {
			reader.setGFFversion(3);
			Feature feature	= reader.processFeature(line);
			assertEquals(FeatureType.GENE, feature.getType());
			assertEquals(1000, feature.getStart());
			assertEquals(9000, feature.getEnd());
//			assertEquals('.', feature.getPhase());
			assertEquals(true, feature.getStrand());
//			assertEquals('.', feature.getScore());
			assertEquals("ctg123", feature.getSequence_id());
			assertEquals(2, feature.getAttributes().size());
			assertEquals("gene00001", feature.getAttribute("ID"));
			assertEquals("EDEN", feature.getAttribute("Name"));
			
		} catch (FeatureFormatException e) {
			fail("misformed feature line: "+line+"\n"+e);
			e.printStackTrace();
		}
	}

	@Test
	public void testProcessFeature001GFF2(){
		String line = "18	protein_coding	exon	246324	246433	.	-	.	gene_id \"ENSG00000079134\"; transcript_id \"ENST00000579891\"; exon_number \"1\"; gene_name \"THOC1\"; gene_biotype \"protein_coding\"; transcript_name \"THOC1-020\"; exon_id \"ENSE00002716487\";";
		try {
			reader.setGFFversion(2);
			reader.setValueSeperator(" ");
			Feature feature	= reader.processFeature(line);
			assertEquals(FeatureType.EXON, feature.getType());
			assertEquals(246324, feature.getStart());
			assertEquals(246433, feature.getEnd());
//			assertEquals('.', feature.getPhase());
			assertEquals(false, feature.getStrand());
//			assertEquals('.', feature.getScore());
			assertEquals("18", feature.getSequence_id());
			assertEquals(7, feature.getAttributes().size());
			assertEquals("ENSG00000079134", feature.getAttribute("gene_id"));
			assertEquals("ENST00000579891", feature.getAttribute("transcript_id"));
			assertEquals("1", feature.getAttribute("exon_number"));
			assertEquals("THOC1", feature.getAttribute("gene_name"));
			assertEquals("protein_coding", feature.getAttribute("gene_biotype"));
			assertEquals("THOC1-020", feature.getAttribute("transcript_name"));
			assertEquals("ENSE00002716487", feature.getAttribute("exon_id"));
			
		} catch (FeatureFormatException e) {
			fail("misformed feature line: "+line+"\n"+e);
			e.printStackTrace();
		}
	}

}
