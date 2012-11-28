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
public class SpliceAnnotationTest implements Constants {

    
   
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
 * annovar: PADI6(uc001bak.1:exon9:c.1026+2G>-)
 * chr1:17718674G>-
 *</P>
--- chokes on single base intron
@Test public void testSpliceVar4() throws AnnotationException  {
	byte chr = 1;
	int pos = 17718674;
	String ref = "G";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    ArrayList<Annotation> anno_list = c.getAnnotation(pos,ref,alt);
	    int N = anno_list.size();
	    Assert.assertEquals(1,N);
	    Annotation ann = anno_list.get(0);
	    byte varType = ann.getVarType();
	    Assert.assertEquals(SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PADI6(uc001bak.1:exon9:c.1026+2G>-)",annot);
	}
} */

/**
 *<P>
 * annovar: KDM4A(uc001cjx.3:exon4:c.315-2A>-,uc010oki.2:exon4:c.315-2A>-)
 * chr1:44125967A>-
 *</P>
 */
@Test public void testSpliceVar7() throws AnnotationException  {
	byte chr = 1;
	int pos = 44125967;
	String ref = "A";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    ArrayList<Annotation> anno_list = c.getAnnotation(pos,ref,alt);
	    int N = anno_list.size();
	    Assert.assertEquals(1,N);
	    Annotation ann = anno_list.get(0);
	    byte varType = ann.getVarType();
	    Assert.assertEquals(SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KDM4A(uc001cjx.3:exon4:c.315-2A>-,uc010oki.2:exon4:c.315-2A>-)",annot);
	}
}

/**
 *<P>
 * annovar: TCTEX1D1(uc001dcv.3:exon4:c.336+1G>A)
 * chr1:67242087G>A
 *</P>
 */
@Test public void testSpliceVar9() throws AnnotationException  {
	byte chr = 1;
	int pos = 67242087;
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
	    Assert.assertEquals(SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TCTEX1D1(uc001dcv.3:exon4:c.336+1G>A)",annot);
	}
}

}