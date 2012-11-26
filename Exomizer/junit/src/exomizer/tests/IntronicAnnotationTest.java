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
 * are intergenic. 
 */
public class IntronicAnnotationTest implements Constants {

    
   
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
 * annovar: IFFO2
 * chr1:19243589A>G
 *</P>
 */
@Test public void testUpstreamVar241() throws AnnotationException  {
	byte chr = 1;
	int pos = 19243589;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("IFFO2",annot);
	}
}

/**
 *<P>
 * annovar: MECR
 * chr1:29542435C>G
 *</P>
 */
@Test public void testUpstreamVar371() throws AnnotationException  {
	byte chr = 1;
	int pos = 29542435;
	String ref = "C";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MECR",annot);
	}
}

/**
 *<P>
 * annovar: TRAPPC3
 * chr1:36602943C>T
 *</P>
 */
@Test public void testUpstreamVar425() throws AnnotationException  {
	byte chr = 1;
	int pos = 36602943;
	String ref = "C";
	String alt = "T";
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
	    Assert.assertEquals("TRAPPC3",annot);
	}
}

/**
 *<P>
 * annovar: OSCP1
 * chr1:36883946T>C
 *</P>
 */
@Test public void testUpstreamVar429() throws AnnotationException  {
	byte chr = 1;
	int pos = 36883946;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OSCP1",annot);
	}
}


   
}