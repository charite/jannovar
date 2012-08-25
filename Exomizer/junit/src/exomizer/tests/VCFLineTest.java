package exomizer.tests;






import exomizer.io.VCFLine;
import exomizer.common.Constants;
import exomizer.exception.VCFParseException;

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
	public void testMissenseVariantType() throws VCFParseException 
    {
	VCFLine line = new VCFLine(line1);
	byte vtype = line.get_variant_type_as_byte();
	Assert.assertEquals(Constants.MISSENSE,vtype);
    }
   
     @Test
	public void testMissenseGenotype() throws VCFParseException 
    {
	VCFLine.set_has_format(true); 
	VCFLine.set_sample_begin_index(9);
	VCFLine line = new VCFLine(line1);
	byte gtype =  line.get_genotype();
	Assert.assertEquals(Constants.GENOTYPE_HOMOZYGOUS_ALT,gtype);
	String gtype_str = line.get_genotype_as_string();
	Assert.assertEquals("homozygous alt",gtype_str);
	Assert.assertEquals(true,line.is_homozygous_alt());

    }

     @Test
	 public void testMissenseGetGenename() throws VCFParseException 
    {
	VCFLine line = new VCFLine(line1);
	String name = line.get_genename();
	Assert.assertEquals("OR4F5",name);
    }
    
    @Test
	 public void testMissenseGetNucleotideMutation() throws VCFParseException 
    {
	VCFLine.set_has_format(true); 
	VCFLine.set_sample_begin_index(9);
	VCFLine line = new VCFLine(line1);
	String mut = line.getNucleotideMutation();
	Assert.assertEquals("c.479T>C",mut);
    }

     @Test
	 public void testMissenseGetAAMutation() throws VCFParseException 
    {
	VCFLine.set_has_format(true); 
	VCFLine.set_sample_begin_index(9);
	VCFLine line = new VCFLine(line1);
	String mut = line.get_aa_mutation();
	Assert.assertEquals("p.L160P",mut);
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
	public void testNonsenseVariantType() throws VCFParseException 
    {
	VCFLine line = new VCFLine(line2);
	byte vtype = line.get_variant_type_as_byte();
	Assert.assertEquals(Constants.NONSENSE,vtype);
    }
   
     @Test
	public void testNonsenseGenotype() throws VCFParseException 
    {
	VCFLine.set_has_format(true); 
	VCFLine.set_sample_begin_index(9);
	VCFLine line = new VCFLine(line2);
	byte gtype =  line.get_genotype();
	Assert.assertEquals(Constants.GENOTYPE_HETEROZYGOUS,gtype);
	String gtype_str = line.get_genotype_as_string();
	Assert.assertEquals("heterozygous",gtype_str);
	Assert.assertEquals(true,line.is_heterozygous());

    }

     @Test
	 public void testNonsenseGetGenename() throws VCFParseException 
    {
	VCFLine line = new VCFLine(line2);
	String name = line.get_genename();
	Assert.assertEquals("OR1B1",name);
    }
    

  @Test
	 public void testNonsenseGetNucleotideMutation() throws VCFParseException 
    {
	VCFLine.set_has_format(true); 
	VCFLine.set_sample_begin_index(9);
	VCFLine line = new VCFLine(line2);
	String mut = line.getNucleotideMutation();
	Assert.assertEquals("c.574C>T",mut);
    }

     @Test
	 public void testNonsenseGetAAMutation()  throws VCFParseException 
    {
	VCFLine.set_has_format(true); 
	VCFLine.set_sample_begin_index(9);
	VCFLine line = new VCFLine(line2);
	String mut = line.get_aa_mutation();
	Assert.assertEquals("p.R192X",mut);
    }

    @Test
	public void testNonsenseGetGenotypeQuality() throws VCFParseException 
    {
	VCFLine line = new VCFLine(line2);
	float q = line.get_variant_quality();
	Assert.assertEquals(999,q,EPSILON);

    }



    @Test
	public void testIntronicLineConstruction() throws VCFParseException 
	{
	    VCFLine line = new VCFLine(line3);
	    

	    Assert.assertEquals("chr20",line.get_chromosome_as_string());
	    Assert.assertEquals(44352740 ,line.get_position());
	    Assert.assertEquals("G",line.get_reference_sequence());
	    Assert.assertEquals("A",line.get_alternate_sequence());
	}



 @Test
	public void testIntronicVariantType() throws VCFParseException 
    {
	VCFLine line = new VCFLine(line3);
	byte vtype = line.get_variant_type_as_byte();
	Assert.assertEquals(Constants.INTRONIC,vtype);
    }
   
     @Test
	public void testIntronicGenotype() throws VCFParseException 
    {
	VCFLine.set_has_format(true); 
	VCFLine.set_sample_begin_index(9);
	VCFLine line = new VCFLine(line3);
	byte gtype =  line.get_genotype();
	Assert.assertEquals(Constants.GENOTYPE_HETEROZYGOUS,gtype);
	String gtype_str = line.get_genotype_as_string();
	Assert.assertEquals("heterozygous",gtype_str);
	Assert.assertEquals(true,line.is_heterozygous());

    }

     @Test
	 public void testIntronicGetGenename() throws VCFParseException 
    {
	VCFLine line = new VCFLine(line3);
	String name = line.get_genename();
	Assert.assertEquals("SPINT4",name);
    }
    

  @Test
	 public void testIntronicGetNucleotideMutation() throws VCFParseException 
    {
	VCFLine.set_has_format(true); 
	VCFLine.set_sample_begin_index(9);
	VCFLine line = new VCFLine(line3);
	String mut = line.getNucleotideMutation();
	Assert.assertEquals("-",mut);
    }

     @Test
	 public void testIntronicGetAAMutation() throws VCFParseException 
    {
	VCFLine.set_has_format(true); 
	VCFLine.set_sample_begin_index(9);
	VCFLine line = new VCFLine(line3);
	String mut = line.get_aa_mutation();
	Assert.assertEquals("-",mut);
    }

    @Test
	public void testIntronicGetGenotypeQuality() throws VCFParseException 
    {
	VCFLine line = new VCFLine(line3);
	float q = line.get_variant_quality();
	Assert.assertEquals(163,q,EPSILON);

    }

    // The following tests that the expected Exceptions are thrown.

     private String badline1="chr1	.	.	T	C	33.7	.	"+
	"EFFECT=missense;HGVS=OR4F5:NM_001005484:exon1:c.479T>C:p.L160P,;DP=7;VDB=0.0399;"+
	"AF1=0.7867;AC1=10;DP4=1,0,0,5;MQ=31;FQ=6.48;PV4=0.17,1e-05,0.013,1	GT:PL:GQ	"+
	"1/1:0,0,0:3	1/1:20,3,0:5	0/1:0,3,41:5";

    private String badline2 ="chr9	125391241	.	G	A	999	.	"+
	"EFFECT=stopit;HGVS=OR1B1:NM_001004450:exon1:c.574C>T:p.R192X,;DP=64;VDB=0.0324;"+
	"AF1=0.4991;G3=2.124e-08,1,1.458e-37;HWE=0.00397;AC1=6;DP4=34,13,11,5;MQ=57;FQ=999;PV4=0.76,1,1,1	"+
	"GT:PL:GQ	0/1:17,0,193:20	0/1:82,0,114:85	0/1:49,0,204:52";

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

}
