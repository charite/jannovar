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
import exomizer.exception.AnnotationException;


import org.junit.Test;
import org.junit.BeforeClass;
import junit.framework.Assert;


/**
 * This class is intended to perform unuit testing on variants that
 * are STOPGAIN orn STOPLOSS. 
 */
public class StopAnnotationTest implements Constants {

    
   
    private static HashMap<Byte,Chromosome> chromosomeMap = null;

  

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
	for (KnownGene kgl : kgMap.values()) {
	    byte chrom = kgl.getChromosome();
	    if (! chromosomeMap.containsKey(chrom)) {
		Chromosome chr = new Chromosome(chrom);
		chromosomeMap.put(chrom,chr);
	    }
	    Chromosome c = chromosomeMap.get(chrom);
	    c.addGene(kgl);	
	}
    }


/**
 *<P>
 * annovar: FAM71A:uc001hjk.3:exon1:c.1663A>T:p.K555X,
 * chr1:212799882A>T
 *</P>
 */
@Test public void testStopVar1() throws AnnotationException  {
	byte chr = 1;
	int pos = 212799882;
	String ref = "A";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann =c.getAnnotation(pos,ref,alt); 
	    byte varType = ann.getVarType();
	    Assert.assertEquals(STOPGAIN,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FAM71A(uc001hjk.3:exon1:c.1663A>T:p.K555*)",annot);
	}
}



/**
 *<P>
 * annovar: HLA-L:uc003npv.2:exon5:c.431G>A:p.W144X,
 * chr6:30229463G>A
 *</P>
 */
@Test public void testStopVar4() throws AnnotationException  {
	byte chr = 6;
	int pos = 30229463;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann =c.getAnnotation(pos,ref,alt); 
	    byte varType = ann.getVarType();
	    Assert.assertEquals(STOPGAIN,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HLA-L(uc003npv.2:exon5:c.431G>A:p.W144*)",annot);
	}
}


/**
 *<P>
 * annovar: PTCRA:uc011duz.1:exon3:c.348G>A:p.W116X,PTCRA:uc010jxx.1:exon2:c.198G>A:p.W66X,
 * chr6:42891022G>A
 *</P>
 */
@Test public void testStopVar5() throws AnnotationException  {
	byte chr = 6;
	int pos = 42891022;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann =c.getAnnotation(pos,ref,alt); 
	    byte varType = ann.getVarType();
	    Assert.assertEquals(STOPGAIN,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PTCRA(uc010jxx.1:exon2:c.198G>A:p.W66*,uc011duz.1:exon3:c.348G>A:p.W116*)",annot);
	}
}





/**
 *<P>
 * annovar: OR4X1:uc010rht.2:exon1:c.819T>A:p.Y273X,
 * chr11:48286231T>A
 *</P>
 */
@Test public void testStopVar7() throws AnnotationException  {
	byte chr = 11;
	int pos = 48286231;
	String ref = "T";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann =c.getAnnotation(pos,ref,alt); 
	    byte varType = ann.getVarType();
	    Assert.assertEquals(STOPGAIN,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR4X1(uc010rht.2:exon1:c.819T>A:p.Y273*)",annot);
	}
}

}
