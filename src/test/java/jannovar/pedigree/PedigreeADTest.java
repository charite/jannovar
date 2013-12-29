package jannovar.pedigree;



import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException; 
import java.util.ArrayList;

import jannovar.common.Disease;
import jannovar.common.Genotype;
import jannovar.exception.PedParseException;
import jannovar.exome.Variant;
import jannovar.genotype.GenotypeCall;
import jannovar.io.PedFileParser;
import jannovar.pedigree.Pedigree;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

/**
 *Test the following pedigree
 *ped1 father 0 0 1 2
 *ped1 mother 0 0 2 1
 *ped1 son1 father mother 1 2
 *ped1 son2 father mother 1 1
 *ped1 dau1 father mother 2 2
 *ped1 dau2 father mother 2 1
 */
public class PedigreeADTest {
    static private Pedigree pedigree = null;

     @BeforeClass
	public static void setUp() throws IOException,PedParseException
    {
        PedFileParser parser=null;
        parser = new PedFileParser();
        java.net.URL url = PedigreeADTest.class.getResource("/TestPedigreeAD.ped");
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
        
        
    @Test public void testFather1() {
        Person son1 = pedigree.getPerson("son1");
        String fatherID = son1.getFatherID();
        Assert.assertEquals("father",fatherID);
    }
        
        
    private ArrayList<Variant> constructGenotypeCall(Genotype... calls) {
	ArrayList<Variant> varList = new ArrayList<Variant>();
	ArrayList<Genotype> lst = new ArrayList<Genotype>();
	for (Genotype g: calls) {
	    lst.add(g);
	}
	GenotypeCall gc = new GenotypeCall(lst,null);
	float dummyPhred=100f;
	Variant v = new Variant ((byte)1, 1, "A", "C", gc,dummyPhred); 
	varList.add(v);
        return varList;
    }
    
    @Test public void testADinheritance1() {
        ArrayList<Variant> lst = constructGenotypeCall(Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,
                                                        Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS);
	boolean b = pedigree.isCompatibleWithAutosomalDominant(lst);
        Assert.assertEquals(false,b);
    }
    
    /** Correct inheritance pattern! */
    @Test public void testADinheritance2() {
	ArrayList<Variant> lst = constructGenotypeCall(Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_REF,Genotype.HETEROZYGOUS,
						       Genotype.HOMOZYGOUS_REF,Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_REF);
	boolean b = pedigree.isCompatibleWithAutosomalDominant(lst);
        Assert.assertEquals(true,b);  
    }
    
    /** An affected is homozygous alt => cannot be right variant! */
    @Test public void testADinheritance3() {
	ArrayList<Variant> lst = constructGenotypeCall(Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_REF,Genotype.HETEROZYGOUS,
						       Genotype.HOMOZYGOUS_REF,Genotype.HOMOZYGOUS_ALT,Genotype.HOMOZYGOUS_REF);
	boolean b = pedigree.isCompatibleWithAutosomalDominant(lst);
        Assert.assertEquals(false,b);  
    }
    
    /** An affected is homozygous ref => cannot be right variant! */
    @Test public void testADinheritance4() {
	ArrayList<Variant> lst  = constructGenotypeCall(Genotype.HOMOZYGOUS_REF,Genotype.HOMOZYGOUS_REF,Genotype.HETEROZYGOUS,
                                                        Genotype.HOMOZYGOUS_REF,Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_REF);
	boolean b = pedigree.isCompatibleWithAutosomalDominant(lst);
        Assert.assertEquals(false,b);  
    }
    
    
    /** An unaffected person is heterozyous -> FALSE */
    @Test public void testADinheritance5() {
	ArrayList<Variant> lst = constructGenotypeCall(Genotype.HETEROZYGOUS,Genotype.HOMOZYGOUS_REF,Genotype.HETEROZYGOUS,
						       Genotype.HOMOZYGOUS_REF,Genotype.HETEROZYGOUS,Genotype.HETEROZYGOUS);
	boolean b = pedigree.isCompatibleWithAutosomalDominant(lst);
        Assert.assertEquals(false,b);  
    }
}