package exomizer.io;




import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException; 
import java.util.ArrayList;

import exomizer.io.VCFLine;
import exomizer.io.VCFReader;
import exomizer.common.Constants;
import exomizer.exception.VCFParseException;
import exomizer.exome.Variant;
import exomizer.exome.GenotypeI;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import junit.framework.Assert;


import org.junit.Test;

/**
 * Note that this class inputs the file exomizer/data/TestExome.vcf, which is a small
 * excerpt of the Manuel Corpas VCF file for the f1000 research article.
 * <p>
 * Refactored on 13 April, 2013 to take changes in the API of VCFLine into account.
 * <P>
 * The test cases were made by examining some of the lines from that file.
 */
public class VCFLineTest implements Constants {
    /** This is the tolerance for checking equality of floating point numbers for junit.*/
    private float EPSILON = 0.000001f;

    private static VCFReader reader = null;
    private static String vcfPath="src/test/resources/TestExome.vcf";
    private static ArrayList<VCFLine> VCFLineList=null;

    @BeforeClass public static void setUp() throws IOException,VCFParseException
    {
	reader = new VCFReader(vcfPath);
	VCFLineList = new ArrayList<VCFLine>();
	FileInputStream fstream = new FileInputStream(vcfPath);
	DataInputStream in = new DataInputStream(fstream);
	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	String line;
	
	while ((line = br.readLine()) != null)   {
	    if (line.isEmpty()) continue;
	    if (line.startsWith("#")) continue; 
	    if (line == null) {
		System.err.println("Error: First line of VCF file was not read (null pointer)");
		System.err.println("File: " + vcfPath);
		System.exit(1);
	    }
	    VCFLine ln  = new VCFLine(line);
	    VCFLineList.add(ln);
	}
    }

    /**
     * Line 0 has
     * <pre>
     * 1       866511  rs60722469      C       CCCCT 
     * </pre>
     */
    @Test public void testGetReferenceSequence() throws VCFParseException 
    {
	VCFLine line = VCFLineList.get(0);
	String ref = line.get_reference_sequence();
	Assert.assertEquals("C", ref);
	String var = line.get_variant_sequence();
	Assert.assertEquals("CCCCT",var);
    }
    
    @Test public void testGetPosition() throws VCFParseException 
    {
	VCFLine line = VCFLineList.get(1);
	int pos = line.get_position();
	Assert.assertEquals(879317,pos);
	String chr = line.get_chromosome_as_string();
	Assert.assertEquals("1",chr);
    }
    

    /**
     * Line 2 is ...0/1:28,20:48:99:515,0,794
     * Thus, we expect "Het"
     */
    @Test public void testIsGenotype() 
    {
	VCFLine line = VCFLineList.get(2);
	GenotypeI GI = line.getGenotype();
	String gt = GI.get_genotype_as_string();
	Assert.assertEquals("Het",gt);
	Assert.assertEquals(true,GI.is_heterozygous() );
	Assert.assertEquals(false,GI.is_homozygous_alt() );
	Assert.assertEquals(false,GI.is_homozygous_ref() );
	Assert.assertEquals(false,GI.is_unknown_genotype());
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


   

    
}
