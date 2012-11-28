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
@Test public void testIntronicVar241() throws AnnotationException  {
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
@Test public void testIntronicVar371() throws AnnotationException  {
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
@Test public void testIntronicVar425() throws AnnotationException  {
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
@Test public void testIntronicVar429() throws AnnotationException  {
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


/**
 *<P>
 * annovar: SGIP1
 * chr1:67126114A>G
 *</P>
 */
@Test public void testIntronicVar649() throws AnnotationException  {
	byte chr = 1;
	int pos = 67126114;
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
	    Assert.assertEquals("SGIP1",annot);
	}
}

/**
 *<P>
 * annovar: MIER1
 * chr1:67425311T>G
 *</P>
 */
@Test public void testIntronicVar658() throws AnnotationException  {
	byte chr = 1;
	int pos = 67425311;
	String ref = "T";
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
	    Assert.assertEquals("MIER1",annot);
	}
}

/**
 *<P>
 * annovar: BRDT
 * chr1:92446398T>A
 *</P>
 */
@Test public void testIntronicVar778() throws AnnotationException  {
	byte chr = 1;
	int pos = 92446398;
	String ref = "T";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BRDT",annot);
	}
}

/**
 *<P>
 * annovar: TRIM33
 * chr1:114951390T>A
 *</P>
 */
@Test public void testIntronicVar922() throws AnnotationException  {
	byte chr = 1;
	int pos = 114951390;
	String ref = "T";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TRIM33",annot);
	}
}

/**
 *<P>
 * annovar: POGZ
 * chr1:151384733G>A
 *</P>
 */
@Test public void testIntronicVar1032() throws AnnotationException  {
	byte chr = 1;
	int pos = 151384733;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("POGZ",annot);
	}
}

/**
 *<P>
 * annovar: RIT1
 * chr1:155880760->A
 *</P>
 */
@Test public void testIntronicVar1103() throws AnnotationException  {
	byte chr = 1;
	int pos = 155880760;
	String ref = "-";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RIT1",annot);
	}
}

/**
 *<P>
 * annovar: IQGAP3
 * chr1:156536353A>G
 *</P>
 */
@Test public void testIntronicVar1136() throws AnnotationException  {
	byte chr = 1;
	int pos = 156536353;
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
	    Assert.assertEquals("IQGAP3",annot);
	}
}

/**
 *<P>
 * annovar: IFI16
 * chr1:159002557A>G
 *</P>
 */
@Test public void testIntronicVar1184() throws AnnotationException  {
	byte chr = 1;
	int pos = 159002557;
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
	    Assert.assertEquals("IFI16",annot);
	}
}

/**
 *<P>
 * annovar: C1orf204
 * chr1:159810867C>T
 *</P>
 */
@Test public void testIntronicVar1192() throws AnnotationException  {
	byte chr = 1;
	int pos = 159810867;
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
	    Assert.assertEquals("C1orf204",annot);
	}
}

/**
 *<P>
 * annovar: PBX1
 * chr1:164789444A>G
 *</P>
 */
@Test public void testIntronicVar1261() throws AnnotationException  {
	byte chr = 1;
	int pos = 164789444;
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
	    Assert.assertEquals("PBX1",annot);
	}
}

/**
 *<P>
 * annovar: CREG1
 * chr1:167515272T>C
 *</P>
 */
@Test public void testIntronicVar1280() throws AnnotationException  {
	byte chr = 1;
	int pos = 167515272;
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
	    Assert.assertEquals("CREG1",annot);
	}
}

/**
 *<P>
 * annovar: ASTN1
 * chr1:176857347A>G
 *</P>
 */
@Test public void testIntronicVar1334() throws AnnotationException  {
	byte chr = 1;
	int pos = 176857347;
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
	    Assert.assertEquals("ASTN1",annot);
	}
}

/**
 *<P>
 * annovar: HEATR1
 * chr1:236746275G>C
 *</P>
 */
@Test public void testIntronicVar1783() throws AnnotationException  {
	byte chr = 1;
	int pos = 236746275;
	String ref = "G";
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
	    Assert.assertEquals("HEATR1",annot);
	}
}

/**
 *<P>
 * annovar: NOL10
 * chr2:10729897C>T
 *</P>
 */
@Test public void testIntronicVar1913() throws AnnotationException  {
	byte chr = 2;
	int pos = 10729897;
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
	    Assert.assertEquals("NOL10",annot);
	}
}

/**
 *<P>
 * annovar: SPDYA
 * chr2:29052256T>C
 *</P>
 */
@Test public void testIntronicVar2083() throws AnnotationException  {
	byte chr = 2;
	int pos = 29052256;
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
	    Assert.assertEquals("SPDYA",annot);
	}
}

/**
 *<P>
 * annovar: XDH
 * chr2:31571241G>C
 *</P>
 */
@Test public void testIntronicVar2119() throws AnnotationException  {
	byte chr = 2;
	int pos = 31571241;
	String ref = "G";
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
	    Assert.assertEquals("XDH",annot);
	}
}

/**
 *<P>
 * annovar: XDH
 * chr2:31572486->G
 *</P>
 */
@Test public void testIntronicVar2120() throws AnnotationException  {
	byte chr = 2;
	int pos = 31572486;
	String ref = "-";
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
	    Assert.assertEquals("XDH",annot);
	}
}

/**
 *<P>
 * annovar: TTC7A
 * chr2:47251354G>A
 *</P>
 */
@Test public void testIntronicVar2222() throws AnnotationException  {
	byte chr = 2;
	int pos = 47251354;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TTC7A",annot);
	}
}

/**
 *<P>
 * annovar: SFTPB
 * chr2:85893929A>T
 *</P>
 */
@Test public void testIntronicVar2364() throws AnnotationException  {
	byte chr = 2;
	int pos = 85893929;
	String ref = "A";
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
	    Assert.assertEquals("SFTPB",annot);
	}
}


/**
 *<P>
 * annovar: VWA3B
 * chr2:98833448->CC
 *</P>
 */
@Test public void testIntronicVar2443() throws AnnotationException  {
	byte chr = 2;
	int pos = 98833448;
	String ref = "-";
	String alt = "CC";
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
	    Assert.assertEquals("VWA3B",annot);
	}
}

/**
 *<P>
 * annovar: DPP10
 * chr2:116497479A>G
 *</P>
 */
@Test public void testIntronicVar2543() throws AnnotationException  {
	byte chr = 2;
	int pos = 116497479;
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
	    Assert.assertEquals("DPP10",annot);
	}
}

/**
 *<P>
 * annovar: STEAP3
 * chr2:120003642C>T
 *</P>
 */
@Test public void testIntronicVar2557() throws AnnotationException  {
	byte chr = 2;
	int pos = 120003642;
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
	    Assert.assertEquals("STEAP3",annot);
	}
}

/**
 *<P>
 * annovar: MYO7B
 * chr2:128339456G>T
 *</P>
 */
@Test public void testIntronicVar2587() throws AnnotationException  {
	byte chr = 2;
	int pos = 128339456;
	String ref = "G";
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
	    Assert.assertEquals("MYO7B",annot);
	}
}

/**
 *<P>
 * annovar: THSD7B
 * chr2:138000226T>C
 *</P>
 */
@Test public void testIntronicVar2630() throws AnnotationException  {
	byte chr = 2;
	int pos = 138000226;
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
	    Assert.assertEquals("THSD7B",annot);
	}
}

/**
 *<P>
 * annovar: UBR3
 * chr2:170762487C>T
 *</P>
 */
@Test public void testIntronicVar2785() throws AnnotationException  {
	byte chr = 2;
	int pos = 170762487;
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
	    Assert.assertEquals("UBR3",annot);
	}
}

/**
 *<P>
 * annovar: METAP1D
 * chr2:172926211T>G
 *</P>
 */
@Test public void testIntronicVar2807() throws AnnotationException  {
	byte chr = 2;
	int pos = 172926211;
	String ref = "T";
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
	    Assert.assertEquals("METAP1D",annot);
	}
}

/**
 *<P>
 * annovar: TRPM8
 * chr2:234847823A>C
 *</P>
 */
@Test public void testIntronicVar3255() throws AnnotationException  {
	byte chr = 2;
	int pos = 234847823;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TRPM8",annot);
	}
}

/**
 *<P>
 * annovar: ULK4
 * chr3:41957466T>G
 *</P>
 */
@Test public void testIntronicVar3526() throws AnnotationException  {
	byte chr = 3;
	int pos = 41957466;
	String ref = "T";
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
	    Assert.assertEquals("ULK4",annot);
	}
}

/**
 *<P>
 * annovar: SACM1L
 * chr3:45761115G>C
 *</P>
 */
@Test public void testIntronicVar3564() throws AnnotationException  {
	byte chr = 3;
	int pos = 45761115;
	String ref = "G";
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
	    Assert.assertEquals("SACM1L",annot);
	}
}

/**
 *<P>
 * annovar: CDC25A
 * chr3:48209486C>T
 *</P>
 */
@Test public void testIntronicVar3596() throws AnnotationException  {
	byte chr = 3;
	int pos = 48209486;
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
	    Assert.assertEquals("CDC25A",annot);
	}
}

/**
 *<P>
 * annovar: FAM208A
 * chr3:56658481G>C
 *</P>
 */
@Test public void testIntronicVar3712() throws AnnotationException  {
	byte chr = 3;
	int pos = 56658481;
	String ref = "G";
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
	    Assert.assertEquals("FAM208A",annot);
	}
}

/**
 *<P>
 * annovar: ROBO2
 * chr3:77645984A>G
 *</P>
 */
@Test public void testIntronicVar3820() throws AnnotationException  {
	byte chr = 3;
	int pos = 77645984;
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
	    Assert.assertEquals("ROBO2",annot);
	}
}

/**
 *<P>
 * annovar: RABL3
 * chr3:120412972T>G
 *</P>
 */
@Test public void testIntronicVar3948() throws AnnotationException  {
	byte chr = 3;
	int pos = 120412972;
	String ref = "T";
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
	    Assert.assertEquals("RABL3",annot);
	}
}

/**
 *<P>
 * annovar: TMEM44
 * chr3:194344526G>C
 *</P>
 */
@Test public void testIntronicVar4330() throws AnnotationException  {
	byte chr = 3;
	int pos = 194344526;
	String ref = "G";
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
	    Assert.assertEquals("TMEM44",annot);
	}
}

/**
 *<P>
 * annovar: SH3BP2
 * chr4:2822307T>C
 *</P>
 */
@Test public void testIntronicVar4406() throws AnnotationException  {
	byte chr = 4;
	int pos = 2822307;
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
	    Assert.assertEquals("SH3BP2",annot);
	}
}

/**
 *<P>
 * annovar: CRMP1
 * chr4:5837871A>G
 *</P>
 */
@Test public void testIntronicVar4440() throws AnnotationException  {
	byte chr = 4;
	int pos = 5837871;
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
	    Assert.assertEquals("CRMP1",annot);
	}
}

/**
 *<P>
 * annovar: CRMP1
 * chr4:5857850G>A
 *</P>
 */
@Test public void testIntronicVar4444() throws AnnotationException  {
	byte chr = 4;
	int pos = 5857850;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CRMP1",annot);
	}
}

/**
 *<P>
 * annovar: ADAMTS3
 * chr4:73434388T>-
 *</P>
 */
@Test public void testIntronicVar4720() throws AnnotationException  {
	byte chr = 4;
	int pos = 73434388;
	String ref = "T";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ADAMTS3",annot);
	}
}

/**
 *<P>
 * annovar: PPEF2
 * chr4:76809319A>G
 *</P>
 */
@Test public void testIntronicVar4754() throws AnnotationException  {
	byte chr = 4;
	int pos = 76809319;
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
	    Assert.assertEquals("PPEF2",annot);
	}
}

/**
 *<P>
 * annovar: C4orf21
 * chr4:113483717A>G
 *</P>
 */
@Test public void testIntronicVar4923() throws AnnotationException  {
	byte chr = 4;
	int pos = 113483717;
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
	    Assert.assertEquals("C4orf21",annot);
	}
}

/**
 *<P>
 * annovar: SEC24D
 * chr4:119736598C>T
 *</P>
 */
@Test public void testIntronicVar4939() throws AnnotationException  {
	byte chr = 4;
	int pos = 119736598;
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
	    Assert.assertEquals("SEC24D",annot);
	}
}

/**
 *<P>
 * annovar: PET112
 * chr4:152592480->CTTA
 *</P>
 */
@Test public void testIntronicVar5031() throws AnnotationException  {
	byte chr = 4;
	int pos = 152592480;
	String ref = "-";
	String alt = "CTTA";
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
	    Assert.assertEquals("PET112",annot);
	}
}

/**
 *<P>
 * annovar: KIAA0922
 * chr4:154514918T>C
 *</P>
 */
@Test public void testIntronicVar5041() throws AnnotationException  {
	byte chr = 4;
	int pos = 154514918;
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
	    Assert.assertEquals("KIAA0922",annot);
	}
}

/**
 *<P>
 * annovar: PALLD
 * chr4:169846255T>C
 *</P>
 */
@Test public void testIntronicVar5097() throws AnnotationException  {
	byte chr = 4;
	int pos = 169846255;
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
	    Assert.assertEquals("PALLD",annot);
	}
}

/**
 *<P>
 * annovar: GALNT7
 * chr4:174099149T>C
 *</P>
 */
@Test public void testIntronicVar5117() throws AnnotationException  {
	byte chr = 4;
	int pos = 174099149;
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
	    Assert.assertEquals("GALNT7",annot);
	}
}

/**
 *<P>
 * annovar: CYP4V2
 * chr4:187129995A>G
 *</P>
 */
@Test public void testIntronicVar5169() throws AnnotationException  {
	byte chr = 4;
	int pos = 187129995;
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
	    Assert.assertEquals("CYP4V2",annot);
	}
}

/**
 *<P>
 * annovar: FRG1
 * chr4:190878463T>G
 *</P>
 */
@Test public void testIntronicVar5188() throws AnnotationException  {
	byte chr = 4;
	int pos = 190878463;
	String ref = "T";
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
	    Assert.assertEquals("FRG1",annot);
	}
}

/**
 *<P>
 * annovar: ADCY2
 * chr5:7826680T>C
 *</P>
 */
@Test public void testIntronicVar5256() throws AnnotationException  {
	byte chr = 5;
	int pos = 7826680;
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
	    Assert.assertEquals("ADCY2",annot);
	}
}

/**
 *<P>
 * annovar: RANBP3L
 * chr5:36301374T>C
 *</P>
 */
@Test public void testIntronicVar5347() throws AnnotationException  {
	byte chr = 5;
	int pos = 36301374;
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
	    Assert.assertEquals("RANBP3L",annot);
	}
}

/**
 *<P>
 * annovar: MAST4
 * chr5:66226652G>A
 *</P>
 */
@Test public void testIntronicVar5455() throws AnnotationException  {
	byte chr = 5;
	int pos = 66226652;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MAST4",annot);
	}
}

/**
 *<P>
 * annovar: TNPO1
 * chr5:72182857A>G
 *</P>
 */
@Test public void testIntronicVar5481() throws AnnotationException  {
	byte chr = 5;
	int pos = 72182857;
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
	    Assert.assertEquals("TNPO1",annot);
	}
}

/**
 *<P>
 * annovar: POC5
 * chr5:74988336C>T
 *</P>
 */
@Test public void testIntronicVar5497() throws AnnotationException  {
	byte chr = 5;
	int pos = 74988336;
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
	    Assert.assertEquals("POC5",annot);
	}
}

/**
 *<P>
 * annovar: FER
 * chr5:108373106C>A
 *</P>
 */
@Test public void testIntronicVar5617() throws AnnotationException  {
	byte chr = 5;
	int pos = 108373106;
	String ref = "C";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FER",annot);
	}
}

/**
 *<P>
 * annovar: C5orf56,IRF1
 * chr5:131820275C>A
 *</P>
 */
@Test public void testIntronicVar5684() throws AnnotationException  {
	byte chr = 5;
	int pos = 131820275;
	String ref = "C";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("IRF1,C5orf56",annot);
	}
}

/**
 *<P>
 * annovar: WNT8A
 * chr5:137420376T>C
 *</P>
 */
@Test public void testIntronicVar5717() throws AnnotationException  {
	byte chr = 5;
	int pos = 137420376;
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
	    Assert.assertEquals("WNT8A",annot);
	}
}

/**
 *<P>
 * annovar: SPINK5
 * chr5:147505227G>A
 *</P>
 */
@Test public void testIntronicVar5781() throws AnnotationException  {
	byte chr = 5;
	int pos = 147505227;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SPINK5",annot);
	}
}

/**
 *<P>
 * annovar: CSF1R
 * chr5:149435759G>A
 *</P>
 */
@Test public void testIntronicVar5802() throws AnnotationException  {
	byte chr = 5;
	int pos = 149435759;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CSF1R",annot);
	}
}

/**
 *<P>
 * annovar: KCNIP1
 * chr5:170145720T>G
 *</P>
 */
@Test public void testIntronicVar5894() throws AnnotationException  {
	byte chr = 5;
	int pos = 170145720;
	String ref = "T";
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
	    Assert.assertEquals("KCNIP1",annot);
	}
}

/**
 *<P>
 * annovar: HNRNPH1
 * chr5:179043984->AA
 *</P>
 */
@Test public void testIntronicVar5949() throws AnnotationException  {
	byte chr = 5;
	int pos = 179043984;
	String ref = "-";
	String alt = "AA";
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
	    Assert.assertEquals("HNRNPH1",annot);
	}
}

/**
 *<P>
 * annovar: FARS2
 * chr6:5431960A>G
 *</P>
 */
@Test public void testIntronicVar6007() throws AnnotationException  {
	byte chr = 6;
	int pos = 5431960;
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
	    Assert.assertEquals("FARS2",annot);
	}
}

/**
 *<P>
 * annovar: KIF13A
 * chr6:17834346A>G
 *</P>
 */
@Test public void testIntronicVar6061() throws AnnotationException  {
	byte chr = 6;
	int pos = 17834346;
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
	    Assert.assertEquals("KIF13A",annot);
	}
}

/**
 *<P>
 * annovar: LOC554223
 * chr6:29761211G>C
 *</P>
 */
@Test public void testIntronicVar6125() throws AnnotationException  {
	byte chr = 6;
	int pos = 29761211;
	String ref = "G";
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
	    Assert.assertEquals("LOC554223",annot);
	}
}

/**
 *<P>
 * annovar: HLA-G
 * chr6:29794860C>T
 *</P>
 */
@Test public void testIntronicVar6127() throws AnnotationException  {
	byte chr = 6;
	int pos = 29794860;
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
	    Assert.assertEquals("HLA-G",annot);
	}
}

/**
 *<P>
 * annovar: HLA-C
 * chr6:31237323A>G
 *</P>
 */
@Test public void testIntronicVar6192() throws AnnotationException  {
	byte chr = 6;
	int pos = 31237323;
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
	    Assert.assertEquals("HLA-C",annot);
	}
}

/**
 *<P>
 * annovar: C6orf10
 * chr6:32292392->AACT
 *</P>
 */
@Test public void testIntronicVar6236() throws AnnotationException  {
	byte chr = 6;
	int pos = 32292392;
	String ref = "-";
	String alt = "AACT";
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
	    Assert.assertEquals("C6orf10",annot);
	}
}

/**
 *<P>
 * annovar: TMEM63B
 * chr6:44121947C>T
 *</P>
 */
@Test public void testIntronicVar6422() throws AnnotationException  {
	byte chr = 6;
	int pos = 44121947;
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
	    Assert.assertEquals("TMEM63B",annot);
	}
}

/**
 *<P>
 * annovar: FAM184A
 * chr6:119323987A>T
 *</P>
 */
@Test public void testIntronicVar6699() throws AnnotationException  {
	byte chr = 6;
	int pos = 119323987;
	String ref = "A";
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
	    Assert.assertEquals("FAM184A",annot);
	}
}

/**
 *<P>
 * annovar: C6orf170
 * chr6:121434153C>A
 *</P>
 */
@Test public void testIntronicVar6701() throws AnnotationException  {
	byte chr = 6;
	int pos = 121434153;
	String ref = "C";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C6orf170",annot);
	}
}

/**
 *<P>
 * annovar: VNN3
 * chr6:133048213G>A
 *</P>
 */
@Test public void testIntronicVar6746() throws AnnotationException  {
	byte chr = 6;
	int pos = 133048213;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("VNN3",annot);
	}
}

/**
 *<P>
 * annovar: PHACTR2
 * chr6:144086394G>A
 *</P>
 */
@Test public void testIntronicVar6780() throws AnnotationException  {
	byte chr = 6;
	int pos = 144086394;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PHACTR2",annot);
	}
}

/**
 *<P>
 * annovar: ZDHHC14
 * chr6:158074504A>G
 *</P>
 */
@Test public void testIntronicVar6843() throws AnnotationException  {
	byte chr = 6;
	int pos = 158074504;
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
	    Assert.assertEquals("ZDHHC14",annot);
	}
}

/**
 *<P>
 * annovar: SLC22A1
 * chr6:160553238C>T
 *</P>
 */
@Test public void testIntronicVar6896() throws AnnotationException  {
	byte chr = 6;
	int pos = 160553238;
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
	    Assert.assertEquals("SLC22A1",annot);
	}
}

/**
 *<P>
 * annovar: LPAL2
 * chr6:160903622G>A
 -- Annovar is in error here, it should be ncRNA INTRONIC
 *</P>
 */
@Test public void testIntronicVar6899() throws AnnotationException  {
	byte chr = 6;
	int pos = 160903622;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    ArrayList<Annotation> anno_list = c.getAnnotation(pos,ref,alt);
	    for (Annotation a : anno_list) {
		System.out.println("SHIT " + a.getVariantAnnotation());
	    }
	    int N = anno_list.size();
	    Assert.assertEquals(1,N);
	    Annotation ann = anno_list.get(0);
	    byte varType = ann.getVarType();
	    Assert.assertEquals(ncRNA_INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LPAL2",annot);
	}
}

/**
 *<P>
 * annovar: PHF14
 * chr7:11208989C>A
 *</P>
 */
@Test public void testIntronicVar7048() throws AnnotationException  {
	byte chr = 7;
	int pos = 11208989;
	String ref = "C";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PHF14",annot);
	}
}

/**
 *<P>
 * annovar: CASD1
 * chr7:94146975T>G
 *</P>
 */
@Test public void testIntronicVar7443() throws AnnotationException  {
	byte chr = 7;
	int pos = 94146975;
	String ref = "T";
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
	    Assert.assertEquals("CASD1",annot);
	}
}

/**
 *<P>
 * annovar: DYNC1I1
 * chr7:95709602C>T
 *</P>
 */
@Test public void testIntronicVar7452() throws AnnotationException  {
	byte chr = 7;
	int pos = 95709602;
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
	    Assert.assertEquals("DYNC1I1",annot);
	}
}

/**
 *<P>
 * annovar: DEFB136
 * chr8:11831661C>T
 *</P>
 */
@Test public void testIntronicVar7962() throws AnnotationException  {
	byte chr = 8;
	int pos = 11831661;
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
	    Assert.assertEquals("DEFB136",annot);
	}
}

/**
 *<P>
 * annovar: EPB49
 * chr8:21937667T>C
 *</P>
 */
@Test public void testIntronicVar8027() throws AnnotationException  {
	byte chr = 8;
	int pos = 21937667;
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
	    Assert.assertEquals("EPB49",annot);
	}
}

/**
 *<P>
 * annovar: ADAM18
 * chr8:39525750C>A
 *</P>
 */
@Test public void testIntronicVar8163() throws AnnotationException  {
	byte chr = 8;
	int pos = 39525750;
	String ref = "C";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ADAM18",annot);
	}
}

/**
 *<P>
 * annovar: SGK196
 * chr8:42959007C>A
 *</P>
 */
@Test public void testIntronicVar8172() throws AnnotationException  {
	byte chr = 8;
	int pos = 42959007;
	String ref = "C";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SGK196",annot);
	}
}

/**
 *<P>
 * annovar: GLI4
 * chr8:144357213G>C
 *</P>
 */
@Test public void testIntronicVar8524() throws AnnotationException  {
	byte chr = 8;
	int pos = 144357213;
	String ref = "G";
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
	    Assert.assertEquals("GLI4",annot);
	}
}

/**
 *<P>
 * annovar: MPDZ
 * chr9:13186232T>A
 *</P>
 */
@Test public void testIntronicVar8614() throws AnnotationException  {
	byte chr = 9;
	int pos = 13186232;
	String ref = "T";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MPDZ",annot);
	}
}

/**
 *<P>
 * annovar: KIAA1797
 * chr9:20944558G>A
 *</P>
 */
@Test public void testIntronicVar8663() throws AnnotationException  {
	byte chr = 9;
	int pos = 20944558;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KIAA1797",annot);
	}
}

/**
 *<P>
 * annovar: ASTN2
 * chr9:119582872T>G
 *</P>
 */
@Test public void testIntronicVar9022() throws AnnotationException  {
	byte chr = 9;
	int pos = 119582872;
	String ref = "T";
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
	    Assert.assertEquals("ASTN2",annot);
	}
}

/**
 *<P>
 * annovar: C5
 * chr9:123778688A>C
 *</P>
 */
@Test public void testIntronicVar9040() throws AnnotationException  {
	byte chr = 9;
	int pos = 123778688;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C5",annot);
	}
}

/**
 *<P>
 * annovar: GSN
 * chr9:124073176T>C
 *</P>
 */
@Test public void testIntronicVar9051() throws AnnotationException  {
	byte chr = 9;
	int pos = 124073176;
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
	    Assert.assertEquals("GSN",annot);
	}
}

/**
 *<P>
 * annovar: TBC1D13
 * chr9:131553768A>G
 *</P>
 */
@Test public void testIntronicVar9145() throws AnnotationException  {
	byte chr = 9;
	int pos = 131553768;
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
	    Assert.assertEquals("TBC1D13",annot);
	}
}

/**
 *<P>
 * annovar: MAN1B1
 * chr9:139997719A>G
 *</P>
 */
@Test public void testIntronicVar9348() throws AnnotationException  {
	byte chr = 9;
	int pos = 139997719;
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
	    Assert.assertEquals("MAN1B1",annot);
	}
}

/**
 *<P>
 * annovar: PFKP
 * chr10:3145885T>C
 *</P>
 */
@Test public void testIntronicVar9393() throws AnnotationException  {
	byte chr = 10;
	int pos = 3145885;
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
	    Assert.assertEquals("PFKP",annot);
	}
}

/**
 *<P>
 * annovar: NRP1
 * chr10:33495306A>G
 *</P>
 */
@Test public void testIntronicVar9623() throws AnnotationException  {
	byte chr = 10;
	int pos = 33495306;
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
	    Assert.assertEquals("NRP1",annot);
	}
}

/**
 *<P>
 * annovar: PARD3
 * chr10:34420607G>A
 *</P>
 */
@Test public void testIntronicVar9624() throws AnnotationException  {
	byte chr = 10;
	int pos = 34420607;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PARD3",annot);
	}
}

/**
 *<P>
 * annovar: PRKG1
 * chr10:53921754C>A
 *</P>
 */
@Test public void testIntronicVar9697() throws AnnotationException  {
	byte chr = 10;
	int pos = 53921754;
	String ref = "C";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PRKG1",annot);
	}
}

/**
 *<P>
 * annovar: MBL2
 * chr10:54528353C>G
 *</P>
 */
@Test public void testIntronicVar9701() throws AnnotationException  {
	byte chr = 10;
	int pos = 54528353;
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
	    Assert.assertEquals("MBL2",annot);
	}
}

/**
 *<P>
 * annovar: HK1
 * chr10:71060634G>A
 *</P>
 */
@Test public void testIntronicVar9757() throws AnnotationException  {
	byte chr = 10;
	int pos = 71060634;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HK1",annot);
	}
}

/**
 *<P>
 * annovar: AK126491,LOC100132987
 * chr10:80037743A>G
 *</P>
 */
@Test public void testIntronicVar9832() throws AnnotationException  {
	byte chr = 10;
	int pos = 80037743;
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
	    Assert.assertEquals(ncRNA_INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AK126491,LOC100132987",annot);
	}
}

/**
 *<P>
 * annovar: GBF1
 * chr10:104128954T>A
 *</P>
 */
@Test public void testIntronicVar9967() throws AnnotationException  {
	byte chr = 10;
	int pos = 104128954;
	String ref = "T";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("GBF1",annot);
	}
}

/**
 *<P>
 * annovar: BTBD16
 * chr10:124066652G>A
 *</P>
 */
@Test public void testIntronicVar10109() throws AnnotationException  {
	byte chr = 10;
	int pos = 124066652;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BTBD16",annot);
	}
}

/**
 *<P>
 * annovar: TRIM22,TRIM5
 * chr11:5777023C>T
 *</P>
 */
@Test public void testIntronicVar10291() throws AnnotationException  {
	byte chr = 11;
	int pos = 5777023;
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
	    Assert.assertEquals("TRIM22,TRIM5",annot);
	}
}

/**
 *<P>
 * annovar: NLRP14
 * chr11:7070866G>A
 *</P>
 */
@Test public void testIntronicVar10313() throws AnnotationException  {
	byte chr = 11;
	int pos = 7070866;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NLRP14",annot);
	}
}

/**
 *<P>
 * annovar: TRIM66
 * chr11:8670172T>C
 *</P>
 */
@Test public void testIntronicVar10331() throws AnnotationException  {
	byte chr = 11;
	int pos = 8670172;
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
	    Assert.assertEquals("TRIM66",annot);
	}
}

/**
 *<P>
 * annovar: ABCC8
 * chr11:17436936A>G
 *</P>
 */
@Test public void testIntronicVar10394() throws AnnotationException  {
	byte chr = 11;
	int pos = 17436936;
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
	    Assert.assertEquals("ABCC8",annot);
	}
}

/**
 *<P>
 * annovar: TPH1
 * chr11:18044304C>T
 *</P>
 */
@Test public void testIntronicVar10407() throws AnnotationException  {
	byte chr = 11;
	int pos = 18044304;
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
	    Assert.assertEquals("TPH1",annot);
	}
}

/**
 *<P>
 * annovar: SLC6A5
 * chr11:20628479T>C
 *</P>
 */
@Test public void testIntronicVar10440() throws AnnotationException  {
	byte chr = 11;
	int pos = 20628479;
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
	    Assert.assertEquals("SLC6A5",annot);
	}
}

/**
 *<P>
 * annovar: BRMS1
 * chr11:66108660C>T
 *</P>
 */
@Test public void testIntronicVar10725() throws AnnotationException  {
	byte chr = 11;
	int pos = 66108660;
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
	    Assert.assertEquals("BRMS1",annot);
	}
}

/**
 *<P>
 * annovar: NADSYN1
 * chr11:71174452A>G
 *</P>
 */
@Test public void testIntronicVar10775() throws AnnotationException  {
	byte chr = 11;
	int pos = 71174452;
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
	    Assert.assertEquals("NADSYN1",annot);
	}
}

/**
 *<P>
 * annovar: AMICA1
 * chr11:118067426A>G
 *</P>
 */
@Test public void testIntronicVar11049() throws AnnotationException  {
	byte chr = 11;
	int pos = 118067426;
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
	    Assert.assertEquals("AMICA1",annot);
	}
}

/**
 *<P>
 * annovar: ANO2
 * chr12:5961048G>A
 *</P>
 */
@Test public void testIntronicVar11228() throws AnnotationException  {
	byte chr = 12;
	int pos = 5961048;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ANO2",annot);
	}
}

/**
 *<P>
 * annovar: TAPBPL
 * chr12:6567787T>C
 *</P>
 */
@Test public void testIntronicVar11243() throws AnnotationException  {
	byte chr = 12;
	int pos = 6567787;
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
	    Assert.assertEquals("TAPBPL",annot);
	}
}

/**
 *<P>
 * annovar: CD163
 * chr12:7639054T>G
 *</P>
 */
@Test public void testIntronicVar11277() throws AnnotationException  {
	byte chr = 12;
	int pos = 7639054;
	String ref = "T";
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
	    Assert.assertEquals("CD163",annot);
	}
}

/**
 *<P>
 * annovar: SOX5
 * chr12:23757245C>T
 *</P>
 */
@Test public void testIntronicVar11443() throws AnnotationException  {
	byte chr = 12;
	int pos = 23757245;
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
	    Assert.assertEquals("SOX5",annot);
	}
}

/**
 *<P>
 * annovar: SOX5
 * chr12:23998888T>C
 *</P>
 */
@Test public void testIntronicVar11444() throws AnnotationException  {
	byte chr = 12;
	int pos = 23998888;
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
	    Assert.assertEquals("SOX5",annot);
	}
}

/**
 *<P>
 * annovar: KRT76
 * chr12:53167313T>C
 *</P>
 */
@Test public void testIntronicVar11662() throws AnnotationException  {
	byte chr = 12;
	int pos = 53167313;
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
	    Assert.assertEquals("KRT76",annot);
	}
}

/**
 *<P>
 * annovar: MRPL42
 * chr12:93863162C>G
 *</P>
 */
@Test public void testIntronicVar11881() throws AnnotationException  {
	byte chr = 12;
	int pos = 93863162;
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
	    Assert.assertEquals("MRPL42",annot);
	}
}

/**
 *<P>
 * annovar: ISCU
 * chr12:108960906G>T
 *</P>
 */
@Test public void testIntronicVar11979() throws AnnotationException  {
	byte chr = 12;
	int pos = 108960906;
	String ref = "G";
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
	    Assert.assertEquals("ISCU",annot);
	}
}

/**
 *<P>
 * annovar: KDM2B
 * chr12:121871006A>C
 *</P>
 */
@Test public void testIntronicVar12073() throws AnnotationException  {
	byte chr = 12;
	int pos = 121871006;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KDM2B",annot);
	}
}

/**
 *<P>
 * annovar: MYCBP2
 * chr13:77725106C>A
 *</P>
 */
@Test public void testIntronicVar12461() throws AnnotationException  {
	byte chr = 13;
	int pos = 77725106;
	String ref = "C";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MYCBP2",annot);
	}
}

/**
 *<P>
 * annovar: TMTC4
 * chr13:101315175G>A
 *</P>
 */
@Test public void testIntronicVar12530() throws AnnotationException  {
	byte chr = 13;
	int pos = 101315175;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TMTC4",annot);
	}
}

/**
 *<P>
 * annovar: MCF2L
 * chr13:113681243C>A
 *</P>
 */
@Test public void testIntronicVar12601() throws AnnotationException  {
	byte chr = 13;
	int pos = 113681243;
	String ref = "C";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MCF2L",annot);
	}
}

/**
 *<P>
 * annovar: DAAM1
 * chr14:59790813C>T
 *</P>
 */
@Test public void testIntronicVar12871() throws AnnotationException  {
	byte chr = 14;
	int pos = 59790813;
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
	    Assert.assertEquals("DAAM1",annot);
	}
}

/**
 *<P>
 * annovar: RAD51B
 * chr14:68935759G>C
 *</P>
 */
@Test public void testIntronicVar12943() throws AnnotationException  {
	byte chr = 14;
	int pos = 68935759;
	String ref = "G";
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
	    Assert.assertEquals("RAD51B",annot);
	}
}

/**
 *<P>
 * annovar: CCDC88C
 * chr14:91763637C>T
 *</P>
 */
@Test public void testIntronicVar13076() throws AnnotationException  {
	byte chr = 14;
	int pos = 91763637;
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
	    Assert.assertEquals("CCDC88C",annot);
	}
}

/**
 *<P>
 * annovar: HSP90AA1
 * chr14:102551800->ATA
 *</P>
 */
@Test public void testIntronicVar13137() throws AnnotationException  {
	byte chr = 14;
	int pos = 102551800;
	String ref = "-";
	String alt = "ATA";
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
	    Assert.assertEquals("HSP90AA1",annot);
	}
}

/**
 *<P>
 * annovar: abParts
 * chr14:106376354A>G
 --- There is a massive annotation for abParts  ( antibody parts) that the 
 --- exomizer is not picking up. Instead, it is getting an intron of an ncRNA gene.
 --- This is not wrong, but it is unclear why we are not picking up the abParts intron.
 *</P>
 */
@Test public void testIntronicVar13184() throws AnnotationException  {
	byte chr = 14;
	int pos = 106376354;
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
	    Assert.assertEquals("abParts",annot);
	}
}

/**
 *<P>
 * annovar: CALML4
 * chr15:68492028G>A
 *</P>
 */
@Test public void testIntronicVar13594() throws AnnotationException  {
	byte chr = 15;
	int pos = 68492028;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CALML4",annot);
	}
}

/**
 *<P>
 * annovar: KIF7
 * chr15:90172834T>G
 *</P>
 */
@Test public void testIntronicVar13748() throws AnnotationException  {
	byte chr = 15;
	int pos = 90172834;
	String ref = "T";
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
	    Assert.assertEquals("KIF7",annot);
	}
}

/**
 *<P>
 * annovar: PCSK6
 * chr15:101872306G>A
 *</P>
 */
@Test public void testIntronicVar13813() throws AnnotationException  {
	byte chr = 15;
	int pos = 101872306;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PCSK6",annot);
	}
}

/**
 *<P>
 * annovar: ZG16B
 * chr16:2881675G>A
 *</P>
 */
@Test public void testIntronicVar13898() throws AnnotationException  {
	byte chr = 16;
	int pos = 2881675;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZG16B",annot);
	}
}

/**
 *<P>
 * annovar: JMJD5
 * chr16:27230261T>C
 *</P>
 */
@Test public void testIntronicVar14092() throws AnnotationException  {
	byte chr = 16;
	int pos = 27230261;
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
	    Assert.assertEquals("JMJD5",annot);
	}
}

/**
 *<P>
 * annovar: NPIPL1,TUFM
 * chr16:28855727A>G
 *</P>
 -- This is also a mistake in Exomizer because NPIPL1 is really big and there are many interveniing genes in 
 -- one of the NPIPL1 introns...
 */
@Test public void testIntronicVar14118() throws AnnotationException  {
	byte chr = 16;
	int pos = 28855727;
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
	    Assert.assertEquals("NPIPL1,TUFM",annot);
	}
}

/**
 *<P>
 * annovar: ITGAL
 * chr16:30522152G>A
 *</P>
 */
@Test public void testIntronicVar14132() throws AnnotationException  {
	byte chr = 16;
	int pos = 30522152;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ITGAL",annot);
	}
}

/**
 *<P>
 * annovar: ITGAX
 * chr16:31384756C>T
 *</P>
 */
@Test public void testIntronicVar14149() throws AnnotationException  {
	byte chr = 16;
	int pos = 31384756;
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
	    Assert.assertEquals("ITGAX",annot);
	}
}

/**
 *<P>
 * annovar: NUP93
 * chr16:56871480ACTTTATTGATT>-
 *</P>
 */
@Test public void testIntronicVar14223() throws AnnotationException  {
	byte chr = 16;
	int pos = 56871480;
	String ref = "ACTTTATTGATT";
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NUP93",annot);
	}
}

/**
 *<P>
 * annovar: NLRC5
 * chr16:57073683G>C
 *</P>
 */
@Test public void testIntronicVar14235() throws AnnotationException  {
	byte chr = 16;
	int pos = 57073683;
	String ref = "G";
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
	    Assert.assertEquals("NLRC5",annot);
	}
}

/**
 *<P>
 * annovar: CNTNAP4
 * chr16:76523792A>G
 *</P>
 */
@Test public void testIntronicVar14383() throws AnnotationException  {
	byte chr = 16;
	int pos = 76523792;
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
	    Assert.assertEquals("CNTNAP4",annot);
	}
}

/**
 *<P>
 * annovar: PLCG2
 * chr16:81891998G>C
 *</P>
 */
@Test public void testIntronicVar14430() throws AnnotationException  {
	byte chr = 16;
	int pos = 81891998;
	String ref = "G";
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
	    Assert.assertEquals("PLCG2",annot);
	}
}

/**
 *<P>
 * annovar: MINK1
 * chr17:4799897C>T
 *</P>
 */
@Test public void testIntronicVar14669() throws AnnotationException  {
	byte chr = 17;
	int pos = 4799897;
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
	    Assert.assertEquals("MINK1",annot);
	}
}

/**
 *<P>
 * annovar: MYH3
 * chr17:10548992C>T
 *</P>
 */
@Test public void testIntronicVar14788() throws AnnotationException  {
	byte chr = 17;
	int pos = 10548992;
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
	    Assert.assertEquals("MYH3",annot);
	}
}

/**
 *<P>
 * annovar: SLC13A2
 * chr17:26816369->C
 *</P>
 */
@Test public void testIntronicVar14924() throws AnnotationException  {
	byte chr = 17;
	int pos = 26816369;
	String ref = "-";
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
	    Assert.assertEquals("SLC13A2",annot);
	}
}

/**
 *<P>
 * annovar: FOXN1
 * chr17:26851501G>A
 *</P>
 */
@Test public void testIntronicVar14927() throws AnnotationException  {
	byte chr = 17;
	int pos = 26851501;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FOXN1",annot);
	}
}

/**
 *<P>
 * annovar: HAP1,JUP
 * chr17:39883789A>G
 *</P>
 */
@Test public void testIntronicVar15064() throws AnnotationException  {
	byte chr = 17;
	int pos = 39883789;
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
	    Assert.assertEquals("HAP1,JUP",annot);
	}
}

/**
 *<P>
 * annovar: EFCAB3
 * chr17:60464688C>T
 *</P>
 */
@Test public void testIntronicVar15254() throws AnnotationException  {
	byte chr = 17;
	int pos = 60464688;
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
	    Assert.assertEquals("EFCAB3",annot);
	}
}

/**
 *<P>
 * annovar: ABCA6
 * chr17:67075294C>T
 *</P>
 */
@Test public void testIntronicVar15309() throws AnnotationException  {
	byte chr = 17;
	int pos = 67075294;
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
	    Assert.assertEquals("ABCA6",annot);
	}
}

/**
 *<P>
 * annovar: PTPRM
 * chr18:8387065C>T
 *</P>
 */
@Test public void testIntronicVar15624() throws AnnotationException  {
	byte chr = 18;
	int pos = 8387065;
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
	    Assert.assertEquals("PTPRM",annot);
	}
}

/**
 *<P>
 * annovar: GREB1L
 * chr18:18983766C>T
 *</P>
 */
@Test public void testIntronicVar15661() throws AnnotationException  {
	byte chr = 18;
	int pos = 18983766;
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
	    Assert.assertEquals("GREB1L",annot);
	}
}

/**
 *<P>
 * annovar: SLC25A23
 * chr19:6443675G>A
 *</P>
 */
@Test public void testIntronicVar16006() throws AnnotationException  {
	byte chr = 19;
	int pos = 6443675;
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
	    Assert.assertEquals(INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC25A23",annot);
	}
}

/**
 *<P>
 * annovar: INSR
 * chr19:7170517A>G
 *</P>
 */
@Test public void testIntronicVar16038() throws AnnotationException  {
	byte chr = 19;
	int pos = 7170517;
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
	    Assert.assertEquals("INSR",annot);
	}
}

/**
 *<P>
 * annovar: ZNF799
 * chr19:12504051C>T
 *</P>
 */
@Test public void testIntronicVar16144() throws AnnotationException  {
	byte chr = 19;
	int pos = 12504051;
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
	    Assert.assertEquals("ZNF799",annot);
	}
}

/**
 *<P>
 * annovar: AP1M1
 * chr19:16320075C>G
 *</P>
 */
@Test public void testIntronicVar16229() throws AnnotationException  {
	byte chr = 19;
	int pos = 16320075;
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
	    Assert.assertEquals("AP1M1",annot);
	}
}

/**
 *<P>
 * annovar: PGPEP1
 * chr19:18466840C>T
 *</P>
 */
@Test public void testIntronicVar16288() throws AnnotationException  {
	byte chr = 19;
	int pos = 18466840;
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
	    Assert.assertEquals("PGPEP1",annot);
	}
}

/**
 *<P>
 * annovar: LSM14A
 * chr19:34712654C>T
 *</P>
 */
@Test public void testIntronicVar16354() throws AnnotationException  {
	byte chr = 19;
	int pos = 34712654;
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
	    Assert.assertEquals("LSM14A",annot);
	}
}   
}