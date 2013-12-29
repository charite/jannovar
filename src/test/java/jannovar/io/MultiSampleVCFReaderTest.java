package jannovar.io;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;

import jannovar.exception.VCFParseException;
import jannovar.exome.Variant;
import jannovar.io.VCFReader;
import jannovar.common.Genotype;
import jannovar.genotype.GenotypeCall;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Assert;

/**
 * @author peter robinson
 * @version 0.2 December 29, 2013
 */
public class MultiSampleVCFReaderTest  {

    private static VCFReader reader = null;
    
    private static  ArrayList<Variant> vcfList = null;


    @BeforeClass
	public static void setUp() throws IOException,VCFParseException
    {
	java.net.URL url = MultiSampleVCFReaderTest.class.getResource("/MultiTestExome.vcf");
        String path = url.getPath();
	reader = new VCFReader(path);
        reader.parseFile();
        vcfList = reader.getVariantList();
        /*for (VCFLine v : vcfList){
            v.dump_VCF_line_for_debug();
        }*/
    }

     @AfterClass public static void releaseResources() { 
	reader = null;
	System.gc();
    }
    
    /**
     *chr1	14907	.	A	G	30.91	.	AC=2;AF=0.500;AN=4;BaseQRankSum=0.736;DP=3;Dels=0.00;FS=4.771;HaplotypeScore=0.0000;MLEAC=3;MLEAF=0.750;MQ=56.55;MQ0=0;MQRankSum=-0.736;QD=15.46;ReadPosRankSum=0.736
     *GT:AD:DP:GQ:PL	./.	1/1:0,2:2:6:58,6,0	./.	0/0:1,0:1:3:0,3,33	./.	./.
     **/
    @Test public void testVariant1() 
	{
	   Variant line = this.vcfList.get(0); 
	    Assert.assertEquals("chr1",line.get_chromosome_as_string());
            Assert.assertEquals(14907,line.get_position());
            Assert.assertEquals("A",line.get_ref());
            Assert.assertEquals("G",line.get_alt());
	}
        
        
    @Test public void testVariant1genotype() {
        Variant line = this.vcfList.get(0);
        //line.dump_VCF_line_for_debug();
        GenotypeCall gt = line.getGenotype();
        //System.out.println(line.get_genotype_as_string());
        Assert.assertEquals(Genotype.NOT_OBSERVED, gt.getGenotypeInIndividualN(0));
        Assert.assertEquals(Genotype.HOMOZYGOUS_ALT, gt.getGenotypeInIndividualN(1));
        Assert.assertEquals(Genotype.NOT_OBSERVED, gt.getGenotypeInIndividualN(2));
        Assert.assertEquals(Genotype.HOMOZYGOUS_REF, gt.getGenotypeInIndividualN(3));
        Assert.assertEquals(Genotype.NOT_OBSERVED, gt.getGenotypeInIndividualN(4));
        Assert.assertEquals(Genotype.NOT_OBSERVED, gt.getGenotypeInIndividualN(5)); 
    }


    @Test(expected =  IllegalArgumentException.class)   public void testVariant1genotypeBadIndex() {
        Variant line = this.vcfList.get(0);
        GenotypeCall gt = line.getGenotype();
        Genotype g = gt.getGenotypeInIndividualN(17);
        Assert.assertEquals(Genotype.NOT_OBSERVED, g);
    }
    
    @Test(expected =  IllegalArgumentException.class)   public void testVariant1genotypeBadIndex2() {
        Variant line = this.vcfList.get(0);
	GenotypeCall gt = line.getGenotype();
        Genotype g = gt.getGenotypeInIndividualN(-1);
        Assert.assertEquals(Genotype.NOT_OBSERVED, g);
    }
    
    /**
     * Test on the second line
     * <p>
     *chr1	14930	.	A	G	660.63	.
     *AC=10;AF=0.833;AN=12;BaseQRankSum=0.840;DP=29;Dels=0.00;FS=7.482;HaplotypeScore=1.3567;MLEAC=10;MLEAF=0.833;MQ=41.06;MQ0=0;MQRankSum=0.900;QD=22.78;ReadPosRankSum=1.500
     *GT:AD:DP:GQ:PL
     *0/1:1,4:5:30:115,0,30	1/1:0,4:4:9:109,9,0	1/1:1,6:7:18:189,18,0	0/1:1,1:2:36:36,0,36	1/1:0,4:4:12:153,12,0	1/1:2,4:6:9:95,9,0
     */
    
    @Test public void testVariant2() 
	{
	   Variant v = this.vcfList.get(1); 
	   
	   Assert.assertEquals((byte)1,v.get_chromosome());
	   Assert.assertEquals(14930,v.get_position());
	   Assert.assertEquals("A",v.get_ref());
	   Assert.assertEquals("G",v.get_alt());
	}
        
             
    @Test public void testVariant2genotype() {
        Variant v = this.vcfList.get(1);
        //line.dump_VCF_line_for_debug();
        GenotypeCall gt = v.getGenotype();
        //System.out.println(line.get_genotype_as_string());
        Assert.assertEquals(Genotype.HETEROZYGOUS, gt.getGenotypeInIndividualN(0));
        Assert.assertEquals(Genotype.HOMOZYGOUS_ALT, gt.getGenotypeInIndividualN(1));
        Assert.assertEquals(Genotype.HOMOZYGOUS_ALT, gt.getGenotypeInIndividualN(2));
        Assert.assertEquals(Genotype.HETEROZYGOUS, gt.getGenotypeInIndividualN(3));
        Assert.assertEquals(Genotype.HOMOZYGOUS_ALT, gt.getGenotypeInIndividualN(4));
        Assert.assertEquals(Genotype.HOMOZYGOUS_ALT, gt.getGenotypeInIndividualN(5)); 
    }



}