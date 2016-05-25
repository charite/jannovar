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

public class GFFParserTest {

	InputStream stream;
	String lines;

	@Before
	public void setUp() throws Exception {
		lines = "##gff-version 3\n"
				+ "NC_000003.11\tBestRefSeq\tgene\t129247482\t129254187\t.\t+\t.\tID=gene7867;Name=RHO;Dbxref=GeneID:6010,HGNC:10012,HPRD:01584,MIM:180380;description=rhodopsin;gbkey=Gene;gene=RHO;gene_synonym=CSNBAD1,OPN2,RP4;part=1%2F1\n"
				+ "NC_000003.11\tBestRefSeq\tmRNA\t129247482\t129254187\t.\t+\t.\tID=rna17010;Name=NM_000539.3;Parent=gene7867;Dbxref=GeneID:6010,Genbank:NM_000539.3,HGNC:10012,HPRD:01584,MIM:180380;gbkey=mRNA;gene=RHO;product=rhodopsin;transcript_id=NM_000539.3\n"
				+ "NC_000003.11\tBestRefSeq\texon\t129247482\t129247937\t.\t+\t.\tID=id202743;Parent=rna17010;Dbxref=GeneID:6010,Genbank:NM_000539.3,HGNC:10012,HPRD:01584,MIM:180380;gbkey=mRNA;gene=RHO;product=rhodopsin;transcript_id=NM_000539.3\n"
				+ "NC_000003.11\tBestRefSeq\texon\t129249719\t129249887\t.\t+\t.\tID=id202744;Parent=rna17010;Dbxref=GeneID:6010,Genbank:NM_000539.3,HGNC:10012,HPRD:01584,MIM:180380;gbkey=mRNA;gene=RHO;product=rhodopsin;transcript_id=NM_000539.3\n"
				+ "NC_000003.11\tBestRefSeq\texon\t129251094\t129251259\t.\t+\t.\tID=id202745;Parent=rna17010;Dbxref=GeneID:6010,Genbank:NM_000539.3,HGNC:10012,HPRD:01584,MIM:180380;gbkey=mRNA;gene=RHO;product=rhodopsin;transcript_id=NM_000539.3\n"
				+ "NC_000003.11\tBestRefSeq\texon\t129251376\t129251615\t.\t+\t.\tID=id202746;Parent=rna17010;Dbxref=GeneID:6010,Genbank:NM_000539.3,HGNC:10012,HPRD:01584,MIM:180380;gbkey=mRNA;gene=RHO;product=rhodopsin;transcript_id=NM_000539.3\n"
				+ "NC_000003.11\tBestRefSeq\texon\t129252451\t129254187\t.\t+\t.\tID=id202747;Parent=rna17010;Dbxref=GeneID:6010,Genbank:NM_000539.3,HGNC:10012,HPRD:01584,MIM:180380;gbkey=mRNA;gene=RHO;product=rhodopsin;transcript_id=NM_000539.3\n"
				+ "NC_000003.11\tBestRefSeq\tCDS\t129247577\t129247937\t.\t+\t0\tID=cds13732;Name=NP_000530.1;Parent=rna17010;Dbxref=CCDS:CCDS3063.1,GeneID:6010,Genbank:NP_000530.1,HGNC:10012,HPRD:01584,MIM:180380;gbkey=CDS;gene=RHO;product=rhodopsin;protein_id=NP_000530.1\n"
				+ "NC_000003.11\tBestRefSeq\tCDS\t129249719\t129249887\t.\t+\t2\tID=cds13732;Name=NP_000530.1;Parent=rna17010;Dbxref=CCDS:CCDS3063.1,GeneID:6010,Genbank:NP_000530.1,HGNC:10012,HPRD:01584,MIM:180380;gbkey=CDS;gene=RHO;product=rhodopsin;protein_id=NP_000530.1\n"
				+ "NC_000003.11\tBestRefSeq\tCDS\t129251094\t129251259\t.\t+\t1\tID=cds13732;Name=NP_000530.1;Parent=rna17010;Dbxref=CCDS:CCDS3063.1,GeneID:6010,Genbank:NP_000530.1,HGNC:10012,HPRD:01584,MIM:180380;gbkey=CDS;gene=RHO;product=rhodopsin;protein_id=NP_000530.1\n"
				+ "NC_000003.11\tBestRefSeq\tCDS\t129251376\t129251615\t.\t+\t0\tID=cds13732;Name=NP_000530.1;Parent=rna17010;Dbxref=CCDS:CCDS3063.1,GeneID:6010,Genbank:NP_000530.1,HGNC:10012,HPRD:01584,MIM:180380;gbkey=CDS;gene=RHO;product=rhodopsin;protein_id=NP_000530.1\n"
				+ "NC_000003.11\tBestRefSeq\tCDS\t129252451\t129252561\t.\t+\t0\tID=cds13732;Name=NP_000530.1;Parent=rna17010;Dbxref=CCDS:CCDS3063.1,GeneID:6010,Genbank:NP_000530.1,HGNC:10012,HPRD:01584,MIM:180380;gbkey=CDS;gene=RHO;product=rhodopsin;protein_id=NP_000530.1\n";
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
				"FeatureRecord [seqID=NC_000003.11, source=BestRefSeq, type=gene, begin=129247481, end=129254187, score=., strand=FORWARD, phase=0, attributes={Dbxref=GeneID:6010,HGNC:10012,HPRD:01584,MIM:180380, ID=gene7867, Name=RHO, description=rhodopsin, gbkey=Gene, gene=RHO, gene_synonym=CSNBAD1,OPN2,RP4, part=1%2F1}]",
				records.get(0).toString());
		Assert.assertEquals(
				"FeatureRecord [seqID=NC_000003.11, source=BestRefSeq, type=CDS, begin=129252450, end=129252561, score=., strand=FORWARD, phase=0, attributes={Dbxref=CCDS:CCDS3063.1,GeneID:6010,Genbank:NP_000530.1,HGNC:10012,HPRD:01584,MIM:180380, ID=cds13732, Name=NP_000530.1, Parent=rna17010, gbkey=CDS, gene=RHO, product=rhodopsin, protein_id=NP_000530.1}]",
				records.get(11).toString());
	}

}
