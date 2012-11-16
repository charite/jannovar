package exomizer.tests;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.HashMap;

/* serialization */
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;


import exomizer.io.UCSCKGParser;
import exomizer.common.Constants;
import exomizer.io.AnnovarParser;
import exomizer.reference.KnownGene;
import exomizer.reference.Chromosome;
import exomizer.reference.Annotation;
import exomizer.exome.Variant;



import org.junit.Test;
import org.junit.BeforeClass;
import junit.framework.Assert;



public class AnnotatorTest implements Constants {

    
   
    private static HashMap<Byte,Chromosome> chromosomeMap = null;

    /** The following are the indices in the array list of the genes to be tested. */
    public static final int UC009VIS=2;
    public static final int UC001ZWX=1;

    @SuppressWarnings (value="unchecked")
    @BeforeClass 
    public static void setUp() throws IOException {
	HashMap<String,KnownGene> kgMap=null;
	// The following file must be created prior to running this test
	String serializedFile = "../ucsc.ser";
	try {
	     FileInputStream fileIn =
		 new FileInputStream(serializedFile);
	     ObjectInputStream in = new ObjectInputStream(fileIn);
	     kgMap = (HashMap<String,KnownGene>) in.readObject();
            in.close();
            fileIn.close();
	} catch(IOException i) {
            i.printStackTrace();
	    System.err.println("Could not deserialize knownGeneMap");
	    System.exit(1);
           
        } catch(ClassNotFoundException c) {
            System.out.println("Could not find HashMap<String,KnownGene> class.");
            c.printStackTrace();
            System.exit(1);
        }
	//System.out.println("Done deserialization, size of map is " + kgMap.size());
	chromosomeMap = new HashMap<Byte,Chromosome> ();
	System.out.println("Adding KGs to Chromosomes");
	System.out.println("Number of KGs is " + kgMap.size());
	for (KnownGene kgl : kgMap.values()) {
	    byte chrom = kgl.getChromosome();
	    //System.out.println("Chromosome is " + chrom);
	    if (! chromosomeMap.containsKey(chrom)) {
		Chromosome chr = new Chromosome(chrom);
		//System.out.println("Adding chromosome for " + chrom);
		chromosomeMap.put(chrom,chr);
	    }
	    Chromosome c = chromosomeMap.get(chrom);
	    c.addGene(kgl);	
	}
    }


    /**
     * The variant is
     *<P>
     * 1	753405	753405	C	T
     *<P>
     * annovar's annotation is intergenic	LOC100288069(dist=39337),LINC00115(dist=8181)	
     */
    @Test public void testIntergenicVar1() {
	byte chr = 1;
	int pos = 753405;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    ArrayList<Annotation> anno_list = c.getAnnotation(pos,ref,alt);
	    /* There should be just one annotation */
	    int N = anno_list.size();
	    Assert.assertEquals(1,N);
	    Annotation ann = anno_list.get(0);
	    byte varType = ann.getVarType();
	    Assert.assertEquals(INTERGENIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HGVS=LOC100288069(dist=39337),LINC00115(dist=8181)",annot);
	    
	}
    }

    /**
     * The variant is
     * <P>
     * 1	11828319	11828319	G	A
     * <P>
     * annovar's annotation is intergenic	AGTRAP(dist=17491),C1orf167(dist=3820)	
     */
     @Test public void testIntergenicVar2() {
	byte chr = 1;
	int pos = 11828319;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    ArrayList<Annotation> anno_list = c.getAnnotation(pos,ref,alt);
	    int N = anno_list.size();
	    Assert.assertEquals(1,N);
	    Annotation ann = anno_list.get(0);
	    byte varType = ann.getVarType();
	    Assert.assertEquals(INTERGENIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HGVS=C1orf167(dist=1746),C1orf167(dist=3820)",annot);
	    
	}
    }
    
    /**
     * The variant is
     * <P>
     * chr12	2851259	.	A	G
     * <P>
     * annovar's annotation is intergenic;HGVS=CACNA1C(dist=44144),LOC283440(dist=19107)	
     */
     @Test public void testIntergenicVar3() {
	 byte chr = 12;
	int pos = 2851259;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    ArrayList<Annotation> anno_list = c.getAnnotation(pos,ref,alt);
	    int N = anno_list.size();
	    Assert.assertEquals(1,N);
	    Annotation ann = anno_list.get(0);
	    byte varType = ann.getVarType();
	    Assert.assertEquals(INTERGENIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HGVS=CACNA1C(dist=44144),LOC283440(dist=19107)",annot);
	    
	}

     }

    /**
     * The variant is
     * <P>
     * 13	19043558	19043558	A	C
     * <P>
     * annovar's annotation is intergenic	NONE(dist=NONE),DQ586768(dist=276134)	
     */
     @Test public void testIntergenicVar4() {
	 byte chr = 13;
	int pos = 19043558;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    ArrayList<Annotation> anno_list = c.getAnnotation(pos,ref,alt);
	    int N = anno_list.size();
	    Assert.assertEquals(1,N);
	    Annotation ann = anno_list.get(0);
	    byte varType = ann.getVarType();
	    Assert.assertEquals(INTERGENIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HGVS=NONE(dist=NONE),DQ586768(dist=276134)",annot);
	    
	}

     }

     /**
     * The variant is
     * <P>
     * chr15	23577570 C	T		
     * <P>
     * annovar's annotation is intergenic;HGVS=GOLGA8IP(dist=2184),HERC2P2(dist=17338)	
     */
     @Test public void testIntergenicVar5() {
	 byte chr = 15;
	int pos = 23577570;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    ArrayList<Annotation> anno_list = c.getAnnotation(pos,ref,alt);
	    int N = anno_list.size();
	    //Assert.assertEquals(1,N);
	    Annotation ann = anno_list.get(0);
	    byte varType = ann.getVarType();
	    Assert.assertEquals(DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HGVS=DQ595648",annot);
	    
	}

     }

     /**
     * The variant is
     * <P>
     * 	15	25307562	25307562	A	G		
     * <P>
     * annovar's annotation is ncRNA_exonic	SNORD116-5	
     */
     @Test public void testNcRNAVar1() {
	  byte chr = 15;
	int pos = 25307562;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    ArrayList<Annotation> anno_list = c.getAnnotation(pos,ref,alt);
	    int N = anno_list.size();
	    Assert.assertEquals(1,N);
	    Annotation ann = anno_list.get(0);
	    byte varType = ann.getVarType();
	    Assert.assertEquals(ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HGVS=SNORD116-5",annot);
	    
	}
     }





    
    /**
     * The variant is
     * <P>
     * 	18	32398340	32398340	T	C
     * <P>
     * annovar's annotation is EFFECT=UTR5;HGVS=DTNA	
     */
     @Test public void testUTR5Var1() {
	  byte chr = 18;
	int pos = 32398340;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    ArrayList<Annotation> anno_list = c.getAnnotation(pos,ref,alt);
	    int N = anno_list.size();
	    Assert.assertEquals(1,N);
	    Annotation ann = anno_list.get(0);
	    byte varType = ann.getVarType();
	    Assert.assertEquals(UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HGVS=DTNA",annot);
	    
	}
     }

    /**
       1	43300921	43300921	C	G	EFFECT=intronic;HGVS=ERMAP;D
    */
     @Test public void testIntronicVar1() {
	 byte chr = 1;
	 int pos = 43300921;
	 String ref = "C";
	 String alt ="G";

	 Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    ArrayList<Annotation> anno_list = c.getAnnotation(pos,ref,alt);
	    int N = anno_list.size();
	    Assert.assertEquals(1,N);
	    Annotation ann = anno_list.get(0);
	    byte varType = ann.getVarType();
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HGVS=ERMAP",annot);
	}
	    

     }

}