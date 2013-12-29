package jannovar.io;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;

import jannovar.exception.VCFParseException;
import jannovar.io.VCFLine;
import jannovar.io.VCFReader;


import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Assert;


public class VCFReaderTest  {

    private static VCFReader reader = null;


    @BeforeClass
	public static void setUp() throws IOException,VCFParseException
    {
	File tmp = File.createTempFile("vcfreader-test","vcfreader-test");
	PrintStream ps = new PrintStream(new FileOutputStream(tmp));
	ps.append("##fileformat=VCFv4.1\n");
	ps.append("##samtoolsVersion=0.1.18 (r982:295)\n");
	ps.append("##INFO=<ID=DP,Number=1,Type=Integer,Description=\"Raw read depth\">\n");
	ps.append("##INFO=<ID=DP4,Number=4,Type=Integer,Description=\"");
	ps.append("# high-quality ref-forward bases, ref-reverse, alt-forward and alt-reverse bases\">\n");
	ps.append("##INFO=<ID=MQ,Number=1,Type=Integer,Description=\"Root-mean-square mapping quality of covering reads\">\n");
	ps.append("##INFO=<ID=FQ,Number=1,Type=Float,Description=\"Phred probability of all samples being the same\">\n");
	ps.append("##INFO=<ID=AF1,Number=1,Type=Float,Description=\"");
	ps.append("Max-likelihood estimate of the first ALT allele frequency (assuming HWE)\">\n");
	ps.append("##INFO=<ID=AC1,Number=1,Type=Float,Description=\"");
	ps.append("Max-likelihood estimate of the first ALT allele count (no HWE assumption)\">\n");
	ps.append("##INFO=<ID=G3,Number=3,Type=Float,Description=\"ML estimate of genotype frequencies\">");
	ps.append("##INFO=<ID=HWE,Number=1,Type=Float,Description=\"Chi^2 based HWE test P-value based on G3\">\n");
	ps.append("##INFO=<ID=CLR,Number=1,Type=Integer,Description=\"");
	ps.append("Log ratio of genotype likelihoods with and without the constraint\">\n");
	ps.append("##INFO=<ID=UGT,Number=1,Type=String,Description=\"The most probable unconstrained genotype configuration in the trio\">");
	ps.append("##INFO=<ID=CGT,Number=1,Type=String,Description=\"The most probable constrained genotype configuration in the trio\">");
	ps.append("##INFO=<ID=PV4,Number=4,Type=Float,Description=\"P-values for strand bias, baseQ bias, mapQ bias and tail distance bias\">");
	ps.append("##INFO=<ID=INDEL,Number=0,Type=Flag,Description=\"Indicates that the variant is an INDEL.\">");
	ps.append("##INFO=<ID=PC2,Number=2,Type=Integer,Description=\"");
	ps.append("Phred probability of the nonRef allele frequency in group1 samples being larger (,smaller) than in group2.\">\n");
	ps.append("##INFO=<ID=PCHI2,Number=1,Type=Float,Description=\"");
	ps.append("Posterior weighted chi^2 P-value for testing the association between group1 and group2 samples.\">\n");
	ps.append("##INFO=<ID=QCHI2,Number=1,Type=Integer,Description=\"Phred scaled PCHI2.\">\n");
	ps.append("##INFO=<ID=PR,Number=1,Type=Integer,Description=\"# permutations yielding a smaller PCHI2.\">");
	ps.append("##INFO=<ID=VDB,Number=1,Type=Float,Description=\"Variant Distance Bias\">\n");
	ps.append("##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">");
	ps.append("##FORMAT=<ID=GQ,Number=1,Type=Integer,Description=\"Genotype Quality\">\n");
	ps.append("##FORMAT=<ID=GL,Number=3,Type=Float,Description=\"Likelihoods for RR,RA,AA genotypes (R=ref,A=alt)\">\n");
	ps.append("##FORMAT=<ID=DP,Number=1,Type=Integer,Description=\"# high-quality bases\">\n");
	ps.append("##FORMAT=<ID=SP,Number=1,Type=Integer,Description=\"Phred-scaled strand bias P-value\">\n");
	ps.append("##FORMAT=<ID=PL,Number=G,Type=Integer,Description=\"List of Phred-scaled genotype likelihoods\">\n");
	ps.append("#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	Mentalist02	"+
		  "Mentalist02_dup	Mentalist02_dup2	Mentalist02_dup3	Mentalist02_dup4	Mentalist02_dup5\n");

	String line1="chr1	69569	.	T	C	33.7	.	"+
	    "DP=7;VDB=0.0399;"+
	    "AF1=0.7867;AC1=10;DP4=1,0,0,5;MQ=31;FQ=6.48;PV4=0.17,1e-05,0.013,1	GT:PL:GQ	"+
	    "1/1:0,0,0:3	1/1:20,3,0:5	0/1:0,3,41:5";

	String line2 ="chr9	125391241	.	G	A	999	.	"+
	    "DP=64;VDB=0.0324;"+
	    "AF1=0.4991;G3=2.124e-08,1,1.458e-37;HWE=0.00397;AC1=6;DP4=34,13,11,5;MQ=57;FQ=999;PV4=0.76,1,1,1	"+
	    "GT:PL:GQ	0/1:17,0,193:20	0/1:82,0,114:85	0/1:49,0,204:52";
	
	String line3 = "chr20	44352740	.	G	A	163	.	"+
	    "DP=89;VDB=0.0115;AF1=0.5831;G3=2.72e-09,0.8334,0.1666;HWE=0.0467;"+
	    "AC1=7;DP4=0,22,0,58;MQ=60;FQ=166;PV4=1,2.5e-106,0.19,1	GT:PL:GQ	0/1:34,0,93:38	0/1:39,0,93:43	"+
	    "0/1:21,0,164:25";
	
      ps.append(line1 + "\n");
      ps.append(line2 + "\n");
      ps.append(line3 + "\n");

      ps.close();


      reader = new VCFReader(tmp.getAbsolutePath());
      reader.parseFile();
    }

     @AfterClass public static void releaseResources() { 
	reader = null;
	System.gc();
    }


   
     @Test
	public void testSizeOfVariantList() 
	{
	    int nvar = reader.get_total_number_of_variants();
	    Assert.assertEquals(3,nvar);	  
	}

    /**
     * Mentalist02 Mentalist02_dup	Mentalist02_dup2
     Mentalist02_dup3 Mentalist02_dup4 Mentalist02_dup5
    */
    @Test public void testSampleNameList() {
	ArrayList<String> sampleList = reader.getSampleNames();
	int N = sampleList.size();
	Assert.assertEquals(6,N);
	String sampl2 = sampleList.get(1);
	Assert.assertEquals("Mentalist02_dup",sampl2);
    }

    @Test(expected =  VCFParseException.class)  
	public void testBadChromosomeLine1() throws VCFParseException {
	String chrlin = "#CHROM	POSS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	Mentalist02	"+
		  "Mentalist02_dup	Mentalist02_dup2	Mentalist02_dup3	Mentalist02_dup4	Mentalist02_dup5\n";
	reader.parse_chrom_line(chrlin);
    }

    @Test(expected =  VCFParseException.class)  
	public void testBadChromosomeLine2() throws VCFParseException {
	String chrlin = "#CHROM	POS	ID	RE	ALT	QUAL	FILTER	INFO	FORMAT	Mentalist02	"+
		  "Mentalist02_dup	Mentalist02_dup2	Mentalist02_dup3	Mentalist02_dup4	Mentalist02_dup5\n";
	reader.parse_chrom_line(chrlin);
    }

    /** This CHROM line does not contain a sample name and is thus invalid. */
    @Test(expected =  VCFParseException.class)  
	public void testBadChromosomeLine3() throws VCFParseException {
	String chrlin = "#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT\n";
	reader.parse_chrom_line(chrlin);
    }
     

}