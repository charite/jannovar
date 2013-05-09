package jpedfilter.pedigree;



import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException; 
import java.util.ArrayList;

import jpedfilter.common.Disease;
import jpedfilter.common.Genotype;
import jpedfilter.exception.PedParseException;
import jpedfilter.genotype.MultipleGenotype;
import jpedfilter.io.PedFileParser;
import jpedfilter.pedigree.Pedigree;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

/**
 *Test the following pedigree
 *ped1 father 0 0 1 1
 *ped1 mother 0 0 2 1
 *ped1 son1 father mother 1 2
 *ped1 son2 father mother 1 1
 *ped1 dau1 father mother 2 2
 *ped1 dau2 father mother 2 1
 */
public class PedigreeARTest {
    static private Pedigree pedigree = null;

     @BeforeClass
	public static void setUp() throws IOException,PedParseException
    {
        PedFileParser parser=null;
        parser = new PedFileParser();
        java.net.URL url = PedigreeADTest.class.getResource("/TestPedigreeAR.ped");
        String path = url.getPath();
        pedigree = parser.parseFile(path);
        
    }
    
     @AfterClass public static void releaseResources() { 
	pedigree = null;
	System.gc();
    }
    
     @Test public void testSizeOfPedigree() 
	{
            int n = pedigree.getNumberOfIndividualsInPedigree();
            Assert.assertEquals(6,n);
        }
	
    private MultipleGenotype constructMultipleGenotype(Genotype... calls) {
        ArrayList<Genotype> lst = new ArrayList<Genotype>();
        for (Genotype g: calls) lst.add(g);
        return new MultipleGenotype(lst,null);
    }
    

    /**
     * Test one compatible HOMOZYGOUS_ALT variant plus two irrelevant second variants.
     */
    @Test public void testARinheritance1() {
        MultipleGenotype mg1 = constructMultipleGenotype(Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,
                                                        Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS);
	MultipleGenotype mg2 = constructMultipleGenotype(Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,
                                                        Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_ALT);
	/* mg3 is compatible with linkage. */
	MultipleGenotype mg3 = constructMultipleGenotype(Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_ALT,
							 Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_ALT,Genotype.HOMOZYGOUS_REF);
        ArrayList<MultipleGenotype> lst = new ArrayList<MultipleGenotype>();
        lst.add(mg1);
	lst.add(mg2);
	lst.add(mg3);
        boolean b = pedigree.isCompatibleWithAutosomalRecessive(lst);
        Assert.assertEquals(true,b);
        
    }

     /**
     * Test one compatible HOMOZYGOUS_ALT variant plus two irrelevant second variants.
     */
    @Test public void testARinheritance3() {
        MultipleGenotype mg1 = constructMultipleGenotype(Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,
                                                        Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS);
	MultipleGenotype mg2 = constructMultipleGenotype(Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,
                                                        Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_ALT);
	/* mg3 is compatible with linkage */
	MultipleGenotype mg3 = constructMultipleGenotype(Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_ALT,
							 Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_ALT,Genotype.HOMOZYGOUS_REF);
        ArrayList<MultipleGenotype> lst = new ArrayList<MultipleGenotype>();
        lst.add(mg1);
	lst.add(mg2);
	lst.add(mg3);
        boolean b = pedigree.isCompatibleWithAutosomalRecessive(lst);
	//pedigree.debugPrint();
        Assert.assertEquals(true,b);  
    }

     /**
     * Test one compatible HOMOZYGOUS_ALT variant plus two irrelevant second variants.
     */
    @Test public void testARinheritance4() {
        MultipleGenotype mg1 = constructMultipleGenotype(Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,
                                                        Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS);
	MultipleGenotype mg2 = constructMultipleGenotype(Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,
                                                        Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_ALT);
	/* mg3 is not compatible with linkage, only one affected is HOMOZYGOUS ALT, the other is HET */
	MultipleGenotype mg3 = constructMultipleGenotype(Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_ALT,
							 Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_REF);
        ArrayList<MultipleGenotype> lst = new ArrayList<MultipleGenotype>();
        lst.add(mg1);
	lst.add(mg2);
	lst.add(mg3);
        boolean b = pedigree.isCompatibleWithAutosomalRecessive(lst);
	//pedigree.debugPrint();
        Assert.assertEquals(false,b);  
    }

    
    /**
    
     *Test the following pedigree
     *ped1 father 0 0 1 1
     *ped1 mother 0 0 2 1
     *ped1 son1 father mother 1 2
     *ped1 son2 father mother 1 1
     *ped1 dau1 father mother 2 2
     *ped1 dau2 father mother 2 1
     Here, mg1 is paternal het, mg2 is maternal het
     */
    @Test public void testARinheritanceCompoundHet1() {
        MultipleGenotype mg1 = constructMultipleGenotype(Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_REF,Genotype.HETEROZYGOUS,
                                                        Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS);
	MultipleGenotype mg2 = constructMultipleGenotype(Genotype.HOMOZYGOUS_REF,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,
                                                        Genotype.HOMOZYGOUS_REF,Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_REF);
	/* mg3 is not compatible with linkage, only one affected is HOMOZYGOUS ALT, the other is HET */

        ArrayList<MultipleGenotype> lst = new ArrayList<MultipleGenotype>();
        lst.add(mg1);
	lst.add(mg2);
	
        boolean b = pedigree.isCompatibleWithAutosomalRecessive(lst);
	//pedigree.debugPrint();
        Assert.assertEquals(true,b);  
    }

    /**
       
     *Test the following pedigree
     *ped1 father 0 0 1 1
     *ped1 mother 0 0 2 1
     *ped1 son1 father mother 1 2
     *ped1 son2 father mother 1 1
     *ped1 dau1 father mother 2 2
     *ped1 dau2 father mother 2 1
     Here, mg1 is paternal het, mg2 is maternal het, but one of theunaffecteds is compound het (dau2
     */
    @Test public void testARinheritanceCompoundHet2() {
        MultipleGenotype mg1 = constructMultipleGenotype(Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_REF,Genotype.HETEROZYGOUS,
                                                        Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS);
	MultipleGenotype mg2 = constructMultipleGenotype(Genotype.HOMOZYGOUS_REF,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,
                                                        Genotype.HOMOZYGOUS_REF,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS);
	/* mg3 is not compatible with linkage, only one affected is HOMOZYGOUS ALT, the other is HET */

        ArrayList<MultipleGenotype> lst = new ArrayList<MultipleGenotype>();
        lst.add(mg1);
	lst.add(mg2);
	
        boolean b = pedigree.isCompatibleWithAutosomalRecessive(lst);
        Assert.assertEquals(false,b);  
    }



    
}
    
    