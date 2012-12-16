package exomizer.tests;






import exomizer.io.VCFLine;
import exomizer.common.Constants;
import exomizer.exception.VCFParseException;
import exomizer.exome.Variant;

import junit.framework.Assert;

import org.junit.Test;


public class VCFLineTest implements Constants {
    /** This is the tolerance for checking equality of floating point numbers for junit.*/
    private float EPSILON = 0.000001f;


    private String line1="chr1	69569	.	T	C	33.7	.	"+
	"EFFECT=missense;HGVS=OR4F5:NM_001005484:exon1:c.479T>C:p.L160P,;DP=7;VDB=0.0399;"+
	"AF1=0.7867;AC1=10;DP4=1,0,0,5;MQ=31;FQ=6.48;PV4=0.17,1e-05,0.013,1	GT:PL:GQ	"+
	"1/1:0,0,0:3	1/1:20,3,0:5	0/1:0,3,41:5";

    private String line2 ="chr9	125391241	.	G	A	999	.	"+
	"EFFECT=stopgain;HGVS=OR1B1:NM_001004450:exon1:c.574C>T:p.R192X,;DP=64;VDB=0.0324;"+
	"AF1=0.4991;G3=2.124e-08,1,1.458e-37;HWE=0.00397;AC1=6;DP4=34,13,11,5;MQ=57;FQ=999;PV4=0.76,1,1,1	"+
	"GT:PL:GQ	0/1:17,0,193:20	0/1:82,0,114:85	0/1:49,0,204:52";

      private String line3 = "chr20	44352740	.	G	A	163	.	"+
	"EFFECT=intronic;HGVS=SPINT4;DP=89;VDB=0.0115;AF1=0.5831;G3=2.72e-09,0.8334,0.1666;HWE=0.0467;"+
	"AC1=7;DP4=0,22,0,58;MQ=60;FQ=166;PV4=1,2.5e-106,0.19,1	GT:PL:GQ	0/1:34,0,93:38	0/1:39,0,93:43	"+
	"0/1:21,0,164:25";


    @Test
	public void testMissenseLineConstruction()  throws VCFParseException 
	{
	    VCFLine line = new VCFLine(line1);
	    Assert.assertEquals("chr1",line.get_chromosome_as_string());
	    Assert.assertEquals( 69569 ,line.get_position());
	    Assert.assertEquals("T",line.get_reference_sequence());
	    Assert.assertEquals("C",line.get_alternate_sequence());
	}

   
   
     @Test
	public void testMissenseGenotype() throws VCFParseException 
    {
	VCFLine line = new VCFLine(line1);
	byte gtype =  line.get_genotype();
	Assert.assertEquals(Constants.GENOTYPE_HOMOZYGOUS_ALT,gtype);
	String gtype_str = line.get_genotype_as_string();
	Assert.assertEquals("homozygous alt",gtype_str);
	Assert.assertEquals(true,line.is_homozygous_alt());
    }

    
    
    @Test
	public void testMissenseGetGenotypeQuality() throws VCFParseException 
    {
	VCFLine line = new VCFLine(line1);
	float q = line.get_variant_quality();
	Assert.assertEquals(33.7,q,EPSILON);
    }



    @Test
	public void testNonsenseLineConstruction()  throws VCFParseException 
	{
	    VCFLine line = new VCFLine(line2);
	    Assert.assertEquals("chr9",line.get_chromosome_as_string());
	    Assert.assertEquals(125391241 ,line.get_position());
	    Assert.assertEquals("G",line.get_reference_sequence());
	    Assert.assertEquals("A",line.get_alternate_sequence());
	}



 
     @Test
	public void testNonsenseGenotype() throws VCFParseException 
    {
	VCFLine line = new VCFLine(line2);
	byte gtype =  line.get_genotype();
	Assert.assertEquals(Constants.GENOTYPE_HETEROZYGOUS,gtype);
	String gtype_str = line.get_genotype_as_string();
	Assert.assertEquals("heterozygous",gtype_str);
	Assert.assertEquals(true,line.is_heterozygous());
    }

  
  

    @Test public void testNonsenseGetGenotypeQuality() throws VCFParseException 
    {
	VCFLine line = new VCFLine(line2);
	float q = line.get_variant_quality();
	Assert.assertEquals(999,q,EPSILON);

    }



    @Test public void testIntronicLineConstruction() throws VCFParseException 
    {
	VCFLine line = new VCFLine(line3);
	Assert.assertEquals("chr20",line.get_chromosome_as_string());
	Assert.assertEquals(44352740 ,line.get_position());
	Assert.assertEquals("G",line.get_reference_sequence());
	Assert.assertEquals("A",line.get_alternate_sequence());
    }



 
    @Test public void testIntronicGenotype() throws VCFParseException 
    {
	VCFLine line = new VCFLine(line3);
	byte gtype =  line.get_genotype();
	Assert.assertEquals(Constants.GENOTYPE_HETEROZYGOUS,gtype);
	String gtype_str = line.get_genotype_as_string();
	Assert.assertEquals("heterozygous",gtype_str);
	Assert.assertEquals(true,line.is_heterozygous());
    }

   

  
    

    @Test public void testIntronicGetGenotypeQuality() throws VCFParseException 
    {
	VCFLine line = new VCFLine(line3);
	float q = line.get_variant_quality();
	Assert.assertEquals(163,q,EPSILON);
    }

    // The following tests that the expected Exceptions are thrown.
    /** Bad line because there is no position (needs to be an int) */
     private String badline1="chr1	.	.	T	C	33.7	.	"+
	"EFFECT=missense;HGVS=OR4F5:NM_001005484:exon1:c.479T>C:p.L160P,;DP=7;VDB=0.0399;"+
	"AF1=0.7867;AC1=10;DP4=1,0,0,5;MQ=31;FQ=6.48;PV4=0.17,1e-05,0.013,1	GT:PL:GQ	"+
	"1/1:0,0,0:3	1/1:20,3,0:5	0/1:0,3,41:5";
    /** Bad line because alt sequence is missing (".") */
    private String badline2 ="chr9	125391241	.	G	.	999	.	"+
	"DP=64;VDB=0.0324;"+
	"AF1=0.4991;G3=2.124e-08,1,1.458e-37;HWE=0.00397;AC1=6;DP4=34,13,11,5;MQ=57;FQ=999;PV4=0.76,1,1,1	"+
	"GT:PL:GQ	0/1:17,0,193:20	0/1:82,0,114:85	0/1:49,0,204:52";
     /** Bad line because the reference sequence is missing (".") */
      private String badline3 = "chr20	44352740	.	G	.	163	.	"+
	"EFFECT=intronic;HGVS=SPINT4;DP=89;VDB=0.0115;AF1=0.5831;G3=2.72e-09,0.8334,0.1666;HWE=0.0467;"+
	"AC1=7;DP4=0,22,0,58;MQ=60;FQ=166;PV4=1,2.5e-106,0.19,1	GT:PL:GQ	0/1:34,0,93:38	0/1:39,0,93:43	"+
	"0/1:21,0,164:25";



    @Test(expected =  VCFParseException.class)  
	public void shouldChokeOnMalformedVCFLine1() throws VCFParseException {
	VCFLine line = new VCFLine(badline1);
    }

     @Test(expected =  VCFParseException.class)  
	public void shouldChokeOnMalformedVCFLine2() throws VCFParseException {
	VCFLine line = new VCFLine(badline2);
    }

      @Test(expected =  VCFParseException.class)  
	public void shouldChokeOnMalformedVCFLine3() throws VCFParseException {
	VCFLine line = new VCFLine(badline3);
    }


    // The following tests are for indel notation in VCF
    private String indelLine1 = "chr1	1276973	.	GACACA	GACACACACA	999	.	INDEL;DP=20;VDB=0.0141;AF1=1;AC1=12;DP4=0,0,19,0;MQ=34;FQ=-45.4	GT:PL:GQ	1/1:103,12,0:27	1/1:66,9,0:24	1/1:116,15,0:30";

    @Test public void testIndelLineChrom1() throws VCFParseException {
	VCFLine line = new VCFLine(indelLine1);
	Assert.assertEquals("chr1",line.get_chromosome_as_string());
    }

      @Test public void testIndelLineChrom2() throws VCFParseException {
	VCFLine line = new VCFLine(indelLine1);
	Assert.assertEquals(1276973 ,line.get_position());
    }
      
    @Test public void testIndelLineChrom3() throws VCFParseException {
	VCFLine line = new VCFLine(indelLine1);
	Variant var = line.extractVariant();
	String r = var.get_ref();
	Assert.assertEquals("-" ,r);
    }


    /** Deletion line
     * Annovar: chr1	1289368	1289369	TG	-	hom	999	19	43
     */
    private String indelLine2="chr1	1289367	.	CTG	C	999	.	INDEL;DP=19;VDB=0.0450;AF1=1;AC1=12;DP4=0,0,14,1;MQ=43;FQ=-43	GT:PL:GQ	1/1:131,12,0:25	1/1:59,6,0:19	1/1:153,12,0:25";

     @Test public void testIndelLineChrom1A() throws VCFParseException {
	VCFLine line = new VCFLine(indelLine2);
	Assert.assertEquals("chr1",line.get_chromosome_as_string());
    }

      @Test public void testIndelLineChrom2A() throws VCFParseException {
	VCFLine line = new VCFLine(indelLine2);
	Variant var = line.extractVariant();
	Assert.assertEquals(1289368 ,var.get_position());
    }
      
    @Test public void testIndelLineChrom3A() throws VCFParseException {
	VCFLine line = new VCFLine(indelLine2);
	Variant var = line.extractVariant();
	String r = var.get_ref();
	Assert.assertEquals("TG" ,r);
    }
    /** annovar: chr1	1684375	1684375	-	CTC	unknown	3.41	4	25*/
    private String indelLine3="chr1	1684351	.	cctcctcctcctcctcctcctcctc	cCTCctcctcctcctcctcctcctcctc	3.41	.	INDEL;DP=4;AF1=0.6663;AC1=8;DP4=0,1,2,0;MQ=25;FQ=-11.9;PV4=0.33,1,0,1	GT:PL:GQ	0/0:0,3,29:5	0/0:0,0,0:3	0/1:23,3,0:5";
       @Test public void testIndelLineChrom1B() throws VCFParseException {
	VCFLine line = new VCFLine(indelLine3);
	Assert.assertEquals("chr1",line.get_chromosome_as_string());
    }

      @Test public void testIndelLineChrom2B() throws VCFParseException {
	VCFLine line = new VCFLine(indelLine3);
	Variant var = line.extractVariant();
	Assert.assertEquals(1684375,var.get_position());
    }
      
    @Test public void testIndelLineChrom3B() throws VCFParseException {
	VCFLine line = new VCFLine(indelLine3);
	Variant var = line.extractVariant();
	String r = var.get_ref();
	Assert.assertEquals("-" ,r);
    }

    @Test public void testIndelLineChrom4B() throws VCFParseException {
	VCFLine line = new VCFLine(indelLine3);
	Variant var = line.extractVariant();
	String alt = var.get_alt();
	Assert.assertEquals("CTC" ,alt);
    }
	


    /** Line with multiple variants. Expected behavior (for now) is to just extract the first var, i.e., G */
    private String LL="chr1	12854530	.	C	G,A	999	.	EFFECT=missense;HGVS=PRAMEF1:NM_023013:exon3:c.754C>G:p.Q252E,;DP=232;VDB=0.0032;AF1=1;AC1=12;DP4=0,0,73,157;MQ=23;FQ=-140	GT:PL:GQ	1/1:174,111,0,174,111,174:99	1/1:159,105,0,159,105,159:99	1/1:181,111,0,181,111,181:99";

 @Test public void testIndelLineChrom1BL() throws VCFParseException {
	VCFLine line = new VCFLine(LL);
	Assert.assertEquals("chr1",line.get_chromosome_as_string());
    }

      @Test public void testIndelLineChrom2BL() throws VCFParseException {
	VCFLine line = new VCFLine(LL);
	Variant var = line.extractVariant();
	Assert.assertEquals(12854530,var.get_position());
    }

       @Test public void testIndelLineChrom3BL() throws VCFParseException {
	VCFLine line = new VCFLine(LL);
	Variant var = line.extractVariant();
	String r = var.get_ref();
	Assert.assertEquals("C" ,r);
       }

  @Test public void testIndelLineChrom4BL() throws VCFParseException {
	VCFLine line = new VCFLine(LL);
	Variant var = line.extractVariant();
	String r = var.get_alt();
	Assert.assertEquals("G" ,r);
       }

    /* annovar: chr1	150199039	150199068	CTCCTCTTCCTCTTCCTCCTCCTCTTCCTC	CTCCTCTTCCTCCTCCTCTTCCTC	*/
    private String ll="chr1	150199039	.	ctcctcttcctcttcctcctcctcttcctc	ctcctcttcctcctcctcttcctc	121	.	EFFECT=nonframeshift-substitution;HGVS=ANP32E:NM_030920:exon5:c.553_582GAGGAAGAGGAGGAGGAAGAGGAG,ANP32E:NM_001136479:exon5:c.409_438GAGGAAGAGGAGGAGGAAGAGGAG,ANP32E:NM_001136478:exon4:c.430_459GAGGAAGAGGAGGAGGAAGAGGAG,;INDEL;DP=58;VDB=0.0700;AF1=0.5092;G3=1.513e-08,1,4.01e-06;HWE=0.0143;AC1=6;DP4=13,0,12,0;MQ=43;FQ=124;PV4=1,0.082,1.6e-06,0.36	GT:PL:GQ	0/1:95,0,125:98	0/1:19,3,0:3	0/1:6,0,94:9";

    @Test public void testIndelA1() throws VCFParseException {
	VCFLine line = new VCFLine(ll);
	Assert.assertEquals("chr1",line.get_chromosome_as_string());
    }

    @Test public void testIndelA2() throws VCFParseException {
	VCFLine line = new VCFLine(ll);
	Variant var = line.extractVariant();
	Assert.assertEquals(150199039,var.get_position());
    }
    @Test public void testIndelA3() throws VCFParseException {
	VCFLine line = new VCFLine(ll);
	Variant var = line.extractVariant();
	String r = var.get_ref();
	Assert.assertEquals("CTCCTCTTCCTCTTCCTCCTCCTCTTCCTC" ,r);
    }
     @Test public void testIndelA4() throws VCFParseException {
	VCFLine line = new VCFLine(ll);
	Variant var = line.extractVariant();
		String r = var.get_alt();
	Assert.assertEquals("CTCCTCTTCCTCCTCCTCTTCCTC" ,r);
     }
    
}
