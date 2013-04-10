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



public class ExonicAnnotationTest implements Constants {

    
   
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
     * 1	949608	949608	G	A
     *<P>
     * ISG15:uc001acj.4:exon1:c.248G>A:p.S83N (Type:MISSENSE)
     */
    @Test public void testVar1() throws AnnotationException {
	byte chr = 1;
	int pos = 949608;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.MISSENSE,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ISG15:uc001acj.4:exon1:c.248G>A:p.S83N (Type:MISSENSE)",annot);
	    
	}
    }


}