package  jannovar.annotation;


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


import jannovar.io.UCSCKGParser;
import jannovar.common.Constants;
import jannovar.io.AnnovarParser;
import jannovar.reference.KnownGene;
import jannovar.reference.Chromosome;
import jannovar.annotation.Annotation;
import jannovar.exome.Variant;
import jannovar.exception.AnnotationException;


import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;


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
	try {
	    java.net.URL url = IntronicAnnotationTest.class.getResource("/ucsc.ser");
	     String path = url.getPath();
	     FileInputStream fileIn = new FileInputStream(path);
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

    @AfterClass public static void releaseResources() { 
	chromosomeMap = null;
	System.gc();
    }
/**
 *<P>
 * annovar: PLEKHN1
 * chr1:909768A>G
 *</P>
 */
@Test public void testIntronicVar4() throws AnnotationException  {
	byte chr = 1;
	int pos = 909768;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PLEKHN1",annot);
	}
}

/**
 *<P>
 * annovar: CDK11A,CDK11B,SLC35E2B
 * chr1:1653004T>C
 *</P>
 */
@Test public void testIntronicVar29() throws AnnotationException  {
	byte chr = 1;
	int pos = 1653004;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CDK11A,CDK11B,SLC35E2B",annot);
	}
}

/**
 *<P>
 * annovar: NADK
 * chr1:1687625T>C
 *</P>
 */
@Test public void testIntronicVar32() throws AnnotationException  {
	byte chr = 1;
	int pos = 1687625;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NADK",annot);
	}
}

/**
 *<P>
 * annovar: MORN1
 * chr1:2286947A>G
 *</P>
 */
@Test public void testIntronicVar42() throws AnnotationException  {
	byte chr = 1;
	int pos = 2286947;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MORN1",annot);
	}
}

/**
 *<P>
 * annovar: CHD5
 * chr1:6204222C>G
 *</P>
 */
@Test public void testIntronicVar70() throws AnnotationException  {
	byte chr = 1;
	int pos = 6204222;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CHD5",annot);
	}
}

/**
 *<P>
 * annovar: TAS1R1
 * chr1:6631319G>A
 *</P>
 */
@Test public void testIntronicVar77() throws AnnotationException  {
	byte chr = 1;
	int pos = 6631319;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TAS1R1",annot);
	}
}

/**
 *<P>
 * annovar: RERE
 * chr1:8422676T>C
 *</P>
 */
@Test public void testIntronicVar97() throws AnnotationException  {
	byte chr = 1;
	int pos = 8422676;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RERE",annot);
	}
}

/**
 *<P>
 * annovar: CTNNBIP1
 * chr1:9910846G>T
 *</P>
 */
@Test public void testIntronicVar111() throws AnnotationException  {
	byte chr = 1;
	int pos = 9910846;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CTNNBIP1",annot);
	}
}

/**
 *<P>
 * annovar: PTCHD2
 * chr1:11575398G>A
 *</P>
 */
@Test public void testIntronicVar130() throws AnnotationException  {
	byte chr = 1;
	int pos = 11575398;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PTCHD2",annot);
	}
}

/**
 *<P>
 * annovar: MTHFR
 * chr1:11852300C>T
 *</P>
 */
@Test public void testIntronicVar138() throws AnnotationException  {
	byte chr = 1;
	int pos = 11852300;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MTHFR",annot);
	}
}

/**
 *<P>
 * annovar: TNFRSF1B
 * chr1:12248965A>G
 *</P>
 */
@Test public void testIntronicVar150() throws AnnotationException  {
	byte chr = 1;
	int pos = 12248965;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TNFRSF1B",annot);
	}
}

/**
 *<P>
 * annovar: VPS13D
 * chr1:12418700T>C
 *</P>
 */
@Test public void testIntronicVar153() throws AnnotationException  {
	byte chr = 1;
	int pos = 12418700;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("VPS13D",annot);
	}
}

/**
 *<P>
 * annovar: PDPN
 * chr1:13937089G>T
 *</P>
 */
@Test public void testIntronicVar162() throws AnnotationException  {
	byte chr = 1;
	int pos = 13937089;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PDPN",annot);
	}
}

/**
 *<P>
 * annovar: CELA2B
 * chr1:15808702C>T
 *</P>
 */
@Test public void testIntronicVar175() throws AnnotationException  {
	byte chr = 1;
	int pos = 15808702;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CELA2B",annot);
	}
}

/**
 *<P>
 * annovar: CELA2B
 * chr1:15813969A>C
 *</P>
 */
@Test public void testIntronicVar178() throws AnnotationException  {
	byte chr = 1;
	int pos = 15813969;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CELA2B",annot);
	}
}

/**
 *<P>
 * annovar: CLCNKB
 * chr1:16376230->T
 *</P>
 */
@Test public void testIntronicVar190() throws AnnotationException  {
	byte chr = 1;
	int pos = 16376230;
	String ref = "-";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CLCNKB",annot);
	}
}

/**
 *<P>
 * annovar: NBPF1
 * chr1:16891485C>T
 *</P>
 */
@Test public void testIntronicVar200() throws AnnotationException  {
	byte chr = 1;
	int pos = 16891485;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NBPF1",annot);
	}
}

/**
 *<P>
 * annovar: PADI6
 * chr1:17715475T>C
 *</P>
 */
@Test public void testIntronicVar232() throws AnnotationException  {
	byte chr = 1;
	int pos = 17715475;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PADI6",annot);
	}
}

/**
 *<P>
 * annovar: ALDH4A1
 * chr1:19202770T>C
 *</P>
 */
@Test public void testIntronicVar239() throws AnnotationException  {
	byte chr = 1;
	int pos = 19202770;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ALDH4A1",annot);
	}
}

/**
 *<P>
 * annovar: UBR4
 * chr1:19491857T>C
 *</P>
 */
@Test public void testIntronicVar246() throws AnnotationException  {
	byte chr = 1;
	int pos = 19491857;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("UBR4",annot);
	}
}

/**
 *<P>
 * annovar: DDOST
 * chr1:20979310->T
 *</P>
 */
@Test public void testIntronicVar262() throws AnnotationException  {
	byte chr = 1;
	int pos = 20979310;
	String ref = "-";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DDOST",annot);
	}
}

/**
 *<P>
 * annovar: NBPF3
 * chr1:21806725->CACC
 *</P>
 */
@Test public void testIntronicVar279() throws AnnotationException  {
	byte chr = 1;
	int pos = 21806725;
	String ref = "-";
	String alt = "CACC";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NBPF3",annot);
	}
}

/**
 *<P>
 * annovar: RAP1GAP
 * chr1:21946415G>A
 *</P>
 */
@Test public void testIntronicVar289() throws AnnotationException  {
	byte chr = 1;
	int pos = 21946415;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RAP1GAP",annot);
	}
}

/**
 *<P>
 * annovar: USP48
 * chr1:22063149G>A
 *</P>
 */
@Test public void testIntronicVar294() throws AnnotationException  {
	byte chr = 1;
	int pos = 22063149;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("USP48",annot);
	}
}

/**
 *<P>
 * annovar: HSPG2
 * chr1:22208030C>T
 *</P>
 */
@Test public void testIntronicVar302() throws AnnotationException  {
	byte chr = 1;
	int pos = 22208030;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HSPG2",annot);
	}
}

/**
 *<P>
 * annovar: CLIC4
 * chr1:25098341C>T
 *</P>
 */
@Test public void testIntronicVar330() throws AnnotationException  {
	byte chr = 1;
	int pos = 25098341;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CLIC4",annot);
	}
}

/**
 *<P>
 * annovar: FAM54B
 * chr1:26158337C>T
 *</P>
 */
@Test public void testIntronicVar338() throws AnnotationException  {
	byte chr = 1;
	int pos = 26158337;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FAM54B",annot);
	}
}

/**
 *<P>
 * annovar: CATSPER4
 * chr1:26526348G>C
 *</P>
 */
@Test public void testIntronicVar343() throws AnnotationException  {
	byte chr = 1;
	int pos = 26526348;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CATSPER4",annot);
	}
}

/**
 *<P>
 * annovar: MAP3K6
 * chr1:27682481G>A
 *</P>
 */
@Test public void testIntronicVar360() throws AnnotationException  {
	byte chr = 1;
	int pos = 27682481;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MAP3K6",annot);
	}
}

/**
 *<P>
 * annovar: SNRNP40
 * chr1:31764718->GAA
 *</P>
 */
@Test public void testIntronicVar377() throws AnnotationException  {
	byte chr = 1;
	int pos = 31764718;
	String ref = "-";
	String alt = "GAA";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SNRNP40",annot);
	}
}

/**
 *<P>
 * annovar: COL16A1
 * chr1:32156722G>A
 *</P>
 */
@Test public void testIntronicVar393() throws AnnotationException  {
	byte chr = 1;
	int pos = 32156722;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("COL16A1",annot);
	}
}

/**
 *<P>
 * annovar: CSMD2
 * chr1:34189961G>-
 *</P>
 */
@Test public void testIntronicVar415() throws AnnotationException  {
	byte chr = 1;
	int pos = 34189961;
	String ref = "G";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CSMD2",annot);
	}
}

/**
 *<P>
 * annovar: CLSPN
 * chr1:36205166A>G
 *</P>
 */
@Test public void testIntronicVar419() throws AnnotationException  {
	byte chr = 1;
	int pos = 36205166;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CLSPN",annot);
	}
}

/**
 *<P>
 * annovar: TRAPPC3
 * chr1:36602943C>T
 *</P>
 */
@Test public void testIntronicVar425() throws AnnotationException  {
	byte chr = 1;
	int pos = 36602943;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TRAPPC3",annot);
	}
}


/**
 *<P>
 * annovar: CAP1
 * chr1:40536074->T
 *</P>
 */
@Test public void testIntronicVar457() throws AnnotationException  {
	byte chr = 1;
	int pos = 40536074;
	String ref = "-";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CAP1",annot);
	}
}

/**
 *<P>
 * annovar: CTPS
 * chr1:41454425G>A
 *</P>
 */
@Test public void testIntronicVar461() throws AnnotationException  {
	byte chr = 1;
	int pos = 41454425;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CTPS",annot);
	}
}

/**
 *<P>
 * annovar: SCMH1
 * chr1:41540853A>T
 *</P>
 */
@Test public void testIntronicVar466() throws AnnotationException  {
	byte chr = 1;
	int pos = 41540853;
	String ref = "A";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SCMH1",annot);
	}
}

/**
 *<P>
 * annovar: SLC2A1
 * chr1:43418026C>T
 *</P>
 */
@Test public void testIntronicVar481() throws AnnotationException  {
	byte chr = 1;
	int pos = 43418026;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC2A1",annot);
	}
}

/**
 *<P>
 * annovar: SZT2
 * chr1:43909265T>C
 *</P>
 */
@Test public void testIntronicVar493() throws AnnotationException  {
	byte chr = 1;
	int pos = 43909265;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SZT2",annot);
	}
}

/**
 *<P>
 * annovar: ST3GAL3
 * chr1:44395786C>T
 *</P>
 */
@Test public void testIntronicVar503() throws AnnotationException  {
	byte chr = 1;
	int pos = 44395786;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ST3GAL3",annot);
	}
}

/**
 *<P>
 * annovar: MAST2
 * chr1:46290315AT>-
 *</P>
 */
@Test public void testIntronicVar526() throws AnnotationException  {
	byte chr = 1;
	int pos = 46290315;
	String ref = "AT";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MAST2",annot);
	}
}

/**
 *<P>
 * annovar: AGBL4
 * chr1:49332969A>G
 *</P>
 */
@Test public void testIntronicVar557() throws AnnotationException  {
	byte chr = 1;
	int pos = 49332969;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AGBL4",annot);
	}
}

/**
 *<P>
 * annovar: AGBL4
 * chr1:50310802T>C
 *</P>
 */
@Test public void testIntronicVar558() throws AnnotationException  {
	byte chr = 1;
	int pos = 50310802;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AGBL4",annot);
	}
}

/**
 *<P>
 * annovar: DIO1
 * chr1:54370306G>A
 *</P>
 */
@Test public void testIntronicVar576() throws AnnotationException  {
	byte chr = 1;
	int pos = 54370306;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DIO1",annot);
	}
}

/**
 *<P>
 * annovar: USP24
 * chr1:55546895T>C
 *</P>
 */
@Test public void testIntronicVar597() throws AnnotationException  {
	byte chr = 1;
	int pos = 55546895;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("USP24",annot);
	}
}

/**
 *<P>
 * annovar: USP24
 * chr1:55557864C>T
 *</P>
 */
@Test public void testIntronicVar598() throws AnnotationException  {
	byte chr = 1;
	int pos = 55557864;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("USP24",annot);
	}
}

/**
 *<P>
 * annovar: INADL
 * chr1:62340855G>C
 *</P>
 */
@Test public void testIntronicVar618() throws AnnotationException  {
	byte chr = 1;
	int pos = 62340855;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("INADL",annot);
	}
}

/**
 *<P>
 * annovar: DOCK7
 * chr1:63027411A>-
 *</P>
 */
@Test public void testIntronicVar624() throws AnnotationException  {
	byte chr = 1;
	int pos = 63027411;
	String ref = "A";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DOCK7",annot);
	}
}

/**
 *<P>
 * annovar: SGIP1
 * chr1:67145322C>A
 *</P>
 */
@Test public void testIntronicVar652() throws AnnotationException  {
	byte chr = 1;
	int pos = 67145322;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SGIP1",annot);
	}
}

/**
 *<P>
 * annovar: C1orf141
 * chr1:67560897A>G
 *</P>
 */
@Test public void testIntronicVar663() throws AnnotationException  {
	byte chr = 1;
	int pos = 67560897;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C1orf141",annot);
	}
}

/**
 *<P>
 * annovar: RPE65
 * chr1:68904513A>G
 *</P>
 */
@Test public void testIntronicVar670() throws AnnotationException  {
	byte chr = 1;
	int pos = 68904513;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RPE65",annot);
	}
}

/**
 *<P>
 * annovar: DNASE2B
 * chr1:84876711->A
 *</P>
 */
@Test public void testIntronicVar720() throws AnnotationException  {
	byte chr = 1;
	int pos = 84876711;
	String ref = "-";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DNASE2B",annot);
	}
}

/**
 *<P>
 * annovar: DNASE2B
 * chr1:84876731T>G
 *</P>
 */
@Test public void testIntronicVar721() throws AnnotationException  {
	byte chr = 1;
	int pos = 84876731;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DNASE2B",annot);
	}
}

/**
 *<P>
 * annovar: CLCA2
 * chr1:86890931T>A
 *</P>
 */
@Test public void testIntronicVar750() throws AnnotationException  {
	byte chr = 1;
	int pos = 86890931;
	String ref = "T";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CLCA2",annot);
	}
}

/**
 *<P>
 * annovar: CCDC18
 * chr1:93704810G>A
 *</P>
 */
@Test public void testIntronicVar793() throws AnnotationException  {
	byte chr = 1;
	int pos = 93704810;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CCDC18",annot);
	}
}

/**
 *<P>
 * annovar: ABCD3
 * chr1:94980671C>G
 *</P>
 */
@Test public void testIntronicVar803() throws AnnotationException  {
	byte chr = 1;
	int pos = 94980671;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ABCD3",annot);
	}
}

/**
 *<P>
 * annovar: COL11A1
 * chr1:103449598G>C
 *</P>
 */
@Test public void testIntronicVar830() throws AnnotationException  {
	byte chr = 1;
	int pos = 103449598;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("COL11A1",annot);
	}
}

/**
 *<P>
 * annovar: KIAA1324
 * chr1:109716533T>G
 *</P>
 */
@Test public void testIntronicVar853() throws AnnotationException  {
	byte chr = 1;
	int pos = 109716533;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KIAA1324",annot);
	}
}

/**
 *<P>
 * annovar: SARS
 * chr1:109779556T>A
 *</P>
 */
@Test public void testIntronicVar860() throws AnnotationException  {
	byte chr = 1;
	int pos = 109779556;
	String ref = "T";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SARS",annot);
	}
}

/**
 *<P>
 * annovar: GSTM1
 * chr1:110231592T>C
 *</P>
 */
@Test public void testIntronicVar880() throws AnnotationException  {
	byte chr = 1;
	int pos = 110231592;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("GSTM1",annot);
	}
}

/**
 *<P>
 * annovar: GSTM1
 * chr1:110233247T>C
 *</P>
 */
@Test public void testIntronicVar882() throws AnnotationException  {
	byte chr = 1;
	int pos = 110233247;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("GSTM1",annot);
	}
}

/**
 *<P>
 * annovar: C1orf88
 * chr1:111891039A>G
 *</P>
 */
@Test public void testIntronicVar903() throws AnnotationException  {
	byte chr = 1;
	int pos = 111891039;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C1orf88",annot);
	}
}

/**
 *<P>
 * annovar: OVGP1
 * chr1:111958885C>A
 *</P>
 */
@Test public void testIntronicVar904() throws AnnotationException  {
	byte chr = 1;
	int pos = 111958885;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OVGP1",annot);
	}
}

/**
 *<P>
 * annovar: ADORA3
 * chr1:112029193A>G
 *</P>
 */
@Test public void testIntronicVar906() throws AnnotationException  {
	byte chr = 1;
	int pos = 112029193;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ADORA3",annot);
	}
}

/**
 *<P>
 * annovar: DDX20
 * chr1:112303260A>G
 *</P>
 */
@Test public void testIntronicVar909() throws AnnotationException  {
	byte chr = 1;
	int pos = 112303260;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DDX20",annot);
	}
}

/**
 *<P>
 * annovar: MAGI3
 * chr1:114092306A>G
 *</P>
 */
@Test public void testIntronicVar917() throws AnnotationException  {
	byte chr = 1;
	int pos = 114092306;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MAGI3",annot);
	}
}

/**
 *<P>
 * annovar: VANGL1
 * chr1:116225180A>C
 *</P>
 */
@Test public void testIntronicVar933() throws AnnotationException  {
	byte chr = 1;
	int pos = 116225180;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("VANGL1",annot);
	}
}

/**
 *<P>
 * annovar: TTF2
 * chr1:117631391T>C
 *</P>
 */
@Test public void testIntronicVar944() throws AnnotationException  {
	byte chr = 1;
	int pos = 117631391;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TTF2",annot);
	}
}

/**
 *<P>
 * annovar: MAN1A2
 * chr1:117957288A>G
 *</P>
 */
@Test public void testIntronicVar946() throws AnnotationException  {
	byte chr = 1;
	int pos = 117957288;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MAN1A2",annot);
	}
}

/**
 *<P>
 * annovar: NBPF14,NBPF9,PDE4DIP
 * chr1:144915412T>C
 *</P>
-- Wierd part of genome with lots of transcripts. All intronic variants.
@Test public void testIntronicVar967() throws AnnotationException  {
	byte chr = 1;
	int pos = 144915412;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NBPF14,NBPF9,PDE4DIP",annot);
	}
}
 */

/**
 *<P>
 * annovar: NBPF14,NBPF9,SEC22B
 * chr1:145109468A>G
 *</P>
-- Wierd part of genome, lots of transcripts some of which are not useful.
@Test public void testIntronicVar973() throws AnnotationException  {
	byte chr = 1;
	int pos = 145109468;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NBPF14,NBPF9,SEC22B",annot);
	}
}
 */
/**
 *<P>
 * annovar: PDZK1
 * chr1:145762361A>G
 *</P>
 */
@Test public void testIntronicVar990() throws AnnotationException  {
	byte chr = 1;
	int pos = 145762361;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PDZK1",annot);
	}
}

/**
 *<P>
 * annovar: CHD1L
 * chr1:146759306T>C
 *</P>
 */
@Test public void testIntronicVar997() throws AnnotationException  {
	byte chr = 1;
	int pos = 146759306;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CHD1L",annot);
	}
}

/**
 *<P>
 * annovar: SEMA6C
 * chr1:151114883->T
 *</P>
 */
@Test public void testIntronicVar1027() throws AnnotationException  {
	byte chr = 1;
	int pos = 151114883;
	String ref = "-";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SEMA6C",annot);
	}
}

/**
 *<P>
 * annovar: POGZ
 * chr1:151396037A>C
 *</P>
 */
@Test public void testIntronicVar1033() throws AnnotationException  {
	byte chr = 1;
	int pos = 151396037;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("POGZ",annot);
	}
}

/**
 *<P>
 * annovar: CGN
 * chr1:151504778C>T
 *</P>
 */
@Test public void testIntronicVar1037() throws AnnotationException  {
	byte chr = 1;
	int pos = 151504778;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CGN",annot);
	}
}

/**
 *<P>
 * annovar: TUFT1
 * chr1:151546931A>G
 *</P>
 */
@Test public void testIntronicVar1038() throws AnnotationException  {
	byte chr = 1;
	int pos = 151546931;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TUFT1",annot);
	}
}

/**
 *<P>
 * annovar: PGLYRP4
 * chr1:153315471->AGA
 *</P>
 */
@Test public void testIntronicVar1052() throws AnnotationException  {
	byte chr = 1;
	int pos = 153315471;
	String ref = "-";
	String alt = "AGA";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PGLYRP4",annot);
	}
}

/**
 *<P>
 * annovar: S100A8
 * chr1:153362788T>C
 *</P>
 */
@Test public void testIntronicVar1056() throws AnnotationException  {
	byte chr = 1;
	int pos = 153362788;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("S100A8",annot);
	}
}

/**
 *<P>
 * annovar: CRTC2
 * chr1:153927270T>C
 *</P>
 */
@Test public void testIntronicVar1071() throws AnnotationException  {
	byte chr = 1;
	int pos = 153927270;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CRTC2",annot);
	}
}

/**
 *<P>
 * annovar: IL6R
 * chr1:154401972T>C
 *</P>
 */
@Test public void testIntronicVar1082() throws AnnotationException  {
	byte chr = 1;
	int pos = 154401972;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("IL6R",annot);
	}
}

/**
 *<P>
 * annovar: LOC100505666
 * -- actually in intron of ADAM15 (many transcripts) and also of LOC100505666, which is
 * -- a noncoding RNA intron
 * chr1:155028522G>A
 *</P>
 */
@Test public void testIntronicVar1095() throws AnnotationException  {
	byte chr = 1;
	int pos = 155028522;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ADAM15",annot);
	}
}

/**
 *<P>
 * annovar: SMG5
 * chr1:156248148T>G
 *</P>
 */
@Test public void testIntronicVar1113() throws AnnotationException  {
	byte chr = 1;
	int pos = 156248148;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SMG5",annot);
	}
}

/**
 *<P>
 * annovar: CCT3
 * chr1:156280671A>T
 *</P>
 */
@Test public void testIntronicVar1117() throws AnnotationException  {
	byte chr = 1;
	int pos = 156280671;
	String ref = "A";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CCT3",annot);
	}
}

/**
 *<P>
 * annovar: APOA1BP
 * chr1:156563375G>A
 *</P>
 */
@Test public void testIntronicVar1138() throws AnnotationException  {
	byte chr = 1;
	int pos = 156563375;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("APOA1BP",annot);
	}
}

/**
 *<P>
 * annovar: CRABP2
 * chr1:156670545C>T
 *</P>
 */
@Test public void testIntronicVar1139() throws AnnotationException  {
	byte chr = 1;
	int pos = 156670545;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CRABP2",annot);
	}
}

/**
 *<P>
 * annovar: NTRK1
 * chr1:156844649G>C
 *</P>
 */
@Test public void testIntronicVar1144() throws AnnotationException  {
	byte chr = 1;
	int pos = 156844649;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NTRK1",annot);
	}
}

/**
 *<P>
 * annovar: NTRK1
 * chr1:156848808T>C
 *</P>
 */
@Test public void testIntronicVar1145() throws AnnotationException  {
	byte chr = 1;
	int pos = 156848808;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NTRK1",annot);
	}
}

/**
 *<P>
 * annovar: FCRL5
 * chr1:157517127C>T
 *</P>
 */
@Test public void testIntronicVar1154() throws AnnotationException  {
	byte chr = 1;
	int pos = 157517127;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FCRL5",annot);
	}
}

/**
 *<P>
 * annovar: FCRL2
 * chr1:157718283A>T
 *</P>
 */
@Test public void testIntronicVar1158() throws AnnotationException  {
	byte chr = 1;
	int pos = 157718283;
	String ref = "A";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FCRL2",annot);
	}
}

/**
 *<P>
 * annovar: KIRREL
 * chr1:158058109C>A
 *</P>
 */
@Test public void testIntronicVar1165() throws AnnotationException  {
	byte chr = 1;
	int pos = 158058109;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KIRREL",annot);
	}
}

/**
 *<P>
 * annovar: IFI16
 * chr1:159002301T>C
 *</P>
 */
@Test public void testIntronicVar1183() throws AnnotationException  {
	byte chr = 1;
	int pos = 159002301;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("IFI16",annot);
	}
}

/**
 *<P>
 * annovar: ATP1A2
 * chr1:160097315C>A
 *</P>
 */
@Test public void testIntronicVar1202() throws AnnotationException  {
	byte chr = 1;
	int pos = 160097315;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ATP1A2",annot);
	}
}


/**
 *<P>
 * annovar: PPFIBP1
 * chr12:27813903C>T
 *</P>
 */
@Test public void testIntronicVar11475() throws AnnotationException  {
	byte chr = 12;
	int pos = 27813903;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PPFIBP1",annot);
	}
}

/**
 *<P>
 * annovar: PPFIBP1
 * chr12:27832582G>A
 *</P>
 */
@Test public void testIntronicVar11478() throws AnnotationException  {
	byte chr = 12;
	int pos = 27832582;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PPFIBP1",annot);
	}
}

/**
 *<P>
 * annovar: TMTC1
 * chr12:29920791->CATA
 *</P>
 */
@Test public void testIntronicVar11489() throws AnnotationException  {
	byte chr = 12;
	int pos = 29920791;
	String ref = "-";
	String alt = "CATA";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TMTC1",annot);
	}
}

/**
 *<P>
 * annovar: BICD1
 * chr12:32459070T>G
 *</P>
 */
@Test public void testIntronicVar11500() throws AnnotationException  {
	byte chr = 12;
	int pos = 32459070;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BICD1",annot);
	}
}

/**
 *<P>
 * annovar: ADAMTS20
 * chr12:43763234G>T
 *</P>
 */
@Test public void testIntronicVar11538() throws AnnotationException  {
	byte chr = 12;
	int pos = 43763234;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ADAMTS20",annot);
	}
}

/**
 *<P>
 * annovar: NELL2
 * chr12:44926334T>A
 *</P>
 */
@Test public void testIntronicVar11543() throws AnnotationException  {
	byte chr = 12;
	int pos = 44926334;
	String ref = "T";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NELL2",annot);
	}
}

/**
 *<P>
 * annovar: SLC38A1
 * chr12:46601056C>T
 *</P>
 */
@Test public void testIntronicVar11550() throws AnnotationException  {
	byte chr = 12;
	int pos = 46601056;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC38A1",annot);
	}
}

/**
 *<P>
 * annovar: C12orf54
 * chr12:48883012T>C
 *</P>
 */
@Test public void testIntronicVar11571() throws AnnotationException  {
	byte chr = 12;
	int pos = 48883012;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C12orf54",annot);
	}
}

/**
 *<P>
 * annovar: PRKAG1
 * chr12:49398862G>A
 *</P>
 */
@Test public void testIntronicVar11581() throws AnnotationException  {
	byte chr = 12;
	int pos = 49398862;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PRKAG1",annot);
	}
}

/**
 *<P>
 * annovar: TMBIM6
 * chr12:50151977G>A
 *</P>
 */
@Test public void testIntronicVar11598() throws AnnotationException  {
	byte chr = 12;
	int pos = 50151977;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TMBIM6",annot);
	}
}

 }
