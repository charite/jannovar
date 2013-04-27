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
import jannovar.common.VariantType;
import jannovar.io.AnnovarParser;
import jannovar.reference.KnownGene;
import jannovar.reference.Chromosome;
import jannovar.annotation.Annotation;
import jannovar.annotation.AnnotationList;
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
public class UTR5AnnotationTest implements Constants {

    
   
    private static HashMap<Byte,Chromosome> chromosomeMap = null;

  

    @SuppressWarnings (value="unchecked")
    @BeforeClass 
    public static void setUp() throws IOException {
	HashMap<String,KnownGene> kgMap=null;

	try {
	    java.net.URL url = UTR5AnnotationTest.class.getResource("/ucsc.ser");
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
  
    @Test public void testUTR3VarByHand1() throws AnnotationException  {
	byte chr = 4;
	int pos = 20620683;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLIT2",annot);
	}
}
/**
 *<P>
 * annovar: KAZN
 * chr1:15427982G>A
 *</P>
 */
@Test public void testUTR5Var8() throws AnnotationException  {
	byte chr = 1;
	int pos = 15427982;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KAZN",annot);
	}
}

/**
 *<P>
 * annovar: ESPNP
 * chr1:17046613C>T
 *</P>
 */
@Test public void testUTR5Var10() throws AnnotationException  {
	byte chr = 1;
	int pos = 17046613;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ESPNP",annot);
	}
}

/**
 *<P>
 * annovar: PPIH
 * chr1:43124859C>T
 *</P>
 */
@Test public void testUTR5Var20() throws AnnotationException  {
	byte chr = 1;
	int pos = 43124859;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PPIH",annot);
	}
}

/**
 *<P>
 * annovar: HECTD3
 * chr1:45474157G>A
 *</P>
 */
@Test public void testUTR5Var22() throws AnnotationException  {
	byte chr = 1;
	int pos = 45474157;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HECTD3",annot);
	}
}

/**
 *<P>
 * annovar: PPIAL4G
 * chr1:143767875T>C
 *</P>
 */
@Test public void testUTR5Var36() throws AnnotationException  {
	byte chr = 1;
	int pos = 143767875;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PPIAL4G",annot);
	}
}

/**
 *<P>
 * annovar: ECM1;ECM1
 * chr1:150483840C>T
 *</P>
 */
@Test public void testUTR5Var39() throws AnnotationException  {
	byte chr = 1;
	int pos = 150483840;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR53,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ECM1",annot);
	}
}

/**
 *<P>
 * annovar: PGLYRP3
 * chr1:153283179A>G
 *</P>
 */
@Test public void testUTR5Var44() throws AnnotationException  {
	byte chr = 1;
	int pos = 153283179;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PGLYRP3",annot);
	}
}

/**
 *<P>
 * annovar: RIT1
 * chr1:155880573C>G
 *</P>
 */
@Test public void testUTR5Var47() throws AnnotationException  {
	byte chr = 1;
	int pos = 155880573;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RIT1",annot);
	}
}

/**
 *<P>
 * annovar: C1orf182
 * chr1:156309471T>C
 *</P>
 */
@Test public void testUTR5Var50() throws AnnotationException  {
	byte chr = 1;
	int pos = 156309471;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C1orf182",annot);
	}
}

/**
 *<P>
 * annovar: ATP1A4
 * chr1:160140998G>A
 *</P>
 */
@Test public void testUTR5Var59() throws AnnotationException  {
	byte chr = 1;
	int pos = 160140998;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ATP1A4",annot);
	}
}

/**
 *<P>
 * annovar: FAM5B
 * chr1:177198998T>C
 *</P>
 */
@Test public void testUTR5Var69() throws AnnotationException  {
	byte chr = 1;
	int pos = 177198998;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FAM5B",annot);
	}
}

/**
 *<P>
 * annovar: KIAA1614
 * chr1:180897521C>T
 *</P>
 */
@Test public void testUTR5Var71() throws AnnotationException  {
	byte chr = 1;
	int pos = 180897521;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KIAA1614",annot);
	}
}

/**
 *<P>
 * annovar: KIF26B
 * chr1:245318688C>T
 *</P>
 */
@Test public void testUTR5Var83() throws AnnotationException  {
	byte chr = 1;
	int pos = 245318688;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KIF26B",annot);
	}
}

/**
 *<P>
 * annovar: OR2W3
 * chr1:248058879A>T
 *</P>
 */
@Test public void testUTR5Var84() throws AnnotationException  {
	byte chr = 1;
	int pos = 248058879;
	String ref = "A";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR2W3",annot);
	}
}

/**
 *<P>
 * annovar: RSAD2
 * chr2:7017903A>G
 *</P>
 */
@Test public void testUTR5Var86() throws AnnotationException  {
	byte chr = 2;
	int pos = 7017903;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RSAD2",annot);
	}
}

/**
 *<P>
 * annovar: ABCG8
 * chr2:44066158G>A
 *</P>
 */
@Test public void testUTR5Var92() throws AnnotationException  {
	byte chr = 2;
	int pos = 44066158;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ABCG8",annot);
	}
}

/**
 *<P>
 * annovar: ALMS1P
 * chr2:73899613T>G
 *</P>
 */
@Test public void testUTR5Var94() throws AnnotationException  {
	byte chr = 2;
	int pos = 73899613;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ALMS1P",annot);
	}
}

/**
 *<P>
 * annovar: ITPRIPL1
 * chr2:96991492A>G
 *</P>
 */
@Test public void testUTR5Var98() throws AnnotationException  {
	byte chr = 2;
	int pos = 96991492;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ITPRIPL1",annot);
	}
}

/**
 *<P>
 * annovar: TANC1
 * chr2:160075889->C
 *</P>
 */
@Test public void testUTR5Var109() throws AnnotationException  {
	byte chr = 2;
	int pos = 160075889;
	String ref = "-";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TANC1",annot);
	}
}

/**
 *<P>
 * annovar: TANC1
 * chr2:160075889->C
 *</P>
 */
@Test public void testUTR5Var110() throws AnnotationException  {
	byte chr = 2;
	int pos = 160075889;
	String ref = "-";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TANC1",annot);
	}
}

/**
 *<P>
 * annovar: ASB18
 * chr2:237150166A>C
 *</P>
 */
@Test public void testUTR5Var119() throws AnnotationException  {
	byte chr = 2;
	int pos = 237150166;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ASB18",annot);
	}
}

/**
 *<P>
 * annovar: IL17RC
 * chr3:9965745C>T
 *</P>
 */
@Test public void testUTR5Var122() throws AnnotationException  {
	byte chr = 3;
	int pos = 9965745;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("IL17RC",annot);
	}
}

/**
 *<P>
 * annovar: HRH1
 * chr3:11300707T>C
 *</P>
 */
@Test public void testUTR5Var123() throws AnnotationException  {
	byte chr = 3;
	int pos = 11300707;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HRH1",annot);
	}
}

/**
 *<P>
 * annovar: FBXW12
 * chr3:48414197A>T
 *</P>
 */
@Test public void testUTR5Var131() throws AnnotationException  {
	byte chr = 3;
	int pos = 48414197;
	String ref = "A";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FBXW12",annot);
	}
}

/**
 *<P>
 * annovar: CACNA1D
 * chr3:53529173T>A
 *</P>
 */
@Test public void testUTR5Var137() throws AnnotationException  {
	byte chr = 3;
	int pos = 53529173;
	String ref = "T";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CACNA1D",annot);
	}
}

/**
 *<P>
 * annovar: LRTM1
 * chr3:54961949C>T
 *</P>
 */
@Test public void testUTR5Var138() throws AnnotationException  {
	byte chr = 3;
	int pos = 54961949;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LRTM1",annot);
	}
}

/**
 *<P>
 * annovar: C3orf38
 * chr3:88199192->C
 *</P>
 */
@Test public void testUTR5Var144() throws AnnotationException  {
	byte chr = 3;
	int pos = 88199192;
	String ref = "-";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C3orf38",annot);
	}
}

/**
 *<P>
 * annovar: SLC41A3
 * chr3:125775428G>A
 *</P>
 */
@Test public void testUTR5Var152() throws AnnotationException  {
	byte chr = 3;
	int pos = 125775428;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC41A3",annot);
	}
}

/**
 *<P>
 * annovar: AADAC
 * chr3:151531894C>T
 *</P>
 */
@Test public void testUTR5Var158() throws AnnotationException  {
	byte chr = 3;
	int pos = 151531894;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AADAC",annot);
	}
}

/**
 *<P>
 * annovar: KCNAB1
 * chr3:156009614A>G
 *</P>
 */
@Test public void testUTR5Var159() throws AnnotationException  {
	byte chr = 3;
	int pos = 156009614;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KCNAB1",annot);
	}
}

/**
 *<P>
 * annovar: TMEM207
 * chr3:190167647T>A
 *</P>
 */
@Test public void testUTR5Var164() throws AnnotationException  {
	byte chr = 3;
	int pos = 190167647;
	String ref = "T";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TMEM207",annot);
	}
}

/**
 *<P>
 * annovar: KCNIP4
 * chr4:21950259A>G
 *</P>
 */
@Test public void testUTR5Var169() throws AnnotationException  {
	byte chr = 4;
	int pos = 21950259;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KCNIP4",annot);
	}
}

/**
 *<P>
 * annovar: LRBA
 * chr4:151231606G>T
 *</P>
 */
@Test public void testUTR5Var177() throws AnnotationException  {
	byte chr = 4;
	int pos = 151231606;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LRBA",annot);
	}
}

/**
 *<P>
 * annovar: AHRR
 * chr5:432905C>A
 *</P>
 */
@Test public void testUTR5Var182() throws AnnotationException  {
	byte chr = 5;
	int pos = 432905;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AHRR",annot);
	}
}

/**
 *<P>
 * annovar: MED10
 * chr5:6378614G>C
 *</P>
 */
@Test public void testUTR5Var183() throws AnnotationException  {
	byte chr = 5;
	int pos = 6378614;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MED10",annot);
	}
}

/**
 *<P>
 * annovar: C5orf34
 * chr5:43509463->C
 *</P>
 */
@Test public void testUTR5Var188() throws AnnotationException  {
	byte chr = 5;
	int pos = 43509463;
	String ref = "-";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C5orf34",annot);
	}
}

/**
 *<P>
 * annovar: LOC644936
 * chr5:79596175T>C
 *</P>
 */
@Test public void testUTR5Var192() throws AnnotationException  {
	byte chr = 5;
	int pos = 79596175;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC644936",annot);
	}
}

/**
 *<P>
 * annovar: PJA2
 * chr5:108719151G>A
 *</P>
 */
@Test public void testUTR5Var194() throws AnnotationException  {
	byte chr = 5;
	int pos = 108719151;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PJA2",annot);
	}
}

/**
 *<P>
 * annovar: RAD50
 * chr5:131892979G>A
 *</P>
 */
@Test public void testUTR5Var197() throws AnnotationException  {
	byte chr = 5;
	int pos = 131892979;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RAD50",annot);
	}
}

/**
 *<P>
 * annovar: DDX39B
 * chr6:31508863->G
 *</P>
 */
@Test public void testUTR5Var223() throws AnnotationException  {
	byte chr = 6;
	int pos = 31508863;
	String ref = "-";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DDX39B",annot);
	}
}

/**
 *<P>
 * annovar: KHDC1L
 * chr6:73935135G>C
 *</P>
--jannovar gets KHDC1, exonic ncRNA, appears OK!
@Test public void testUTR5Var244() throws AnnotationException  {
	byte chr = 6;
	int pos = 73935135;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KHDC1L",annot);
	}
} */

/**
 *<P>
 * annovar: MYO6
 * chr6:76604573T>G
 *</P>
 */
@Test public void testUTR5Var245() throws AnnotationException  {
	byte chr = 6;
	int pos = 76604573;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MYO6",annot);
	}
}

/**
 *<P>
 * annovar: SCML4
 * chr6:108093580C>T
 *</P>
 */
@Test public void testUTR5Var248() throws AnnotationException  {
	byte chr = 6;
	int pos = 108093580;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SCML4",annot);
	}
}

/**
 *<P>
 * annovar: ULBP2
 * chr6:150263186A>C
 *</P>
 */
@Test public void testUTR5Var255() throws AnnotationException  {
	byte chr = 6;
	int pos = 150263186;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ULBP2",annot);
	}
}

/**
 *<P>
 * annovar: hCG_2023280
 * chr7:97937258T>C
 *</P>
 */
@Test public void testUTR5Var277() throws AnnotationException  {
	byte chr = 7;
	int pos = 97937258;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("hCG_2023280",annot);
	}
}

/**
 *<P>
 * annovar: ZNHIT1
 * chr7:100861213T>C
 *</P>
 */
@Test public void testUTR5Var278() throws AnnotationException  {
	byte chr = 7;
	int pos = 100861213;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNHIT1",annot);
	}
}

/**
 *<P>
 * annovar: ANGPT2
 * chr8:6420490C>G
 *</P>
 */
@Test public void testUTR5Var292() throws AnnotationException  {
	byte chr = 8;
	int pos = 6420490;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ANGPT2",annot);
	}
}

/**
 *<P>
 * annovar: ANGPT2
 * chr8:6420534A>G
 *</P>
 */
@Test public void testUTR5Var293() throws AnnotationException  {
	byte chr = 8;
	int pos = 6420534;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ANGPT2",annot);
	}
}

/**
 *<P>
 * annovar: DLC1
 * chr8:13357587G>A
 *</P>
 */
@Test public void testUTR5Var296() throws AnnotationException  {
	byte chr = 8;
	int pos = 13357587;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DLC1",annot);
	}
}

/**
 *<P>
 * annovar: ZNF34
 * chr8:146003567A>G
 *</P>
 */
@Test public void testUTR5Var307() throws AnnotationException  {
	byte chr = 8;
	int pos = 146003567;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF34",annot);
	}
}

/**
 *<P>
 * annovar: KANK1
 * chr9:676954T>C
 *</P>
 */
@Test public void testUTR5Var308() throws AnnotationException  {
	byte chr = 9;
	int pos = 676954;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KANK1",annot);
	}
}

/**
 *<P>
 * annovar: UBAP2
 * chr9:33933705A>G
 *</P>
 */
@Test public void testUTR5Var310() throws AnnotationException  {
	byte chr = 9;
	int pos = 33933705;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("UBAP2",annot);
	}
}

/**
 *<P>
 * annovar: CENPP
 * chr9:95362715G>C
 *</P>
 */
@Test public void testUTR5Var316() throws AnnotationException  {
	byte chr = 9;
	int pos = 95362715;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CENPP",annot);
	}
}

/**
 *<P>
 * annovar: C9orf84
 * chr9:114521630A>G
 *</P>
 */
@Test public void testUTR5Var318() throws AnnotationException  {
	byte chr = 9;
	int pos = 114521630;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C9orf84",annot);
	}
}

/**
 *<P>
 * annovar: PIK3AP1
 * chr10:98392872A>C
 *</P>
 */
@Test public void testUTR5Var344() throws AnnotationException  {
	byte chr = 10;
	int pos = 98392872;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PIK3AP1",annot);
	}
}

/**
 *<P>
 * annovar: C10orf90
 * chr10:128209959G>T
 *</P>
 */
@Test public void testUTR5Var347() throws AnnotationException  {
	byte chr = 10;
	int pos = 128209959;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C10orf90",annot);
	}
}

/**
 *<P>
 * annovar: CATSPER1
 * chr11:65793878A>-
 *</P>
 */
@Test public void testUTR5Var370() throws AnnotationException  {
	byte chr = 11;
	int pos = 65793878;
	String ref = "A";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CATSPER1",annot);
	}
}

/**
 *<P>
 * annovar: SLCO2B1
 * chr11:74862356T>C
 *</P>
 */
@Test public void testUTR5Var374() throws AnnotationException  {
	byte chr = 11;
	int pos = 74862356;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLCO2B1",annot);
	}
}

/**
 *<P>
 * annovar: BCO2
 * chr11:112064431T>C
 *</P>
 */
@Test public void testUTR5Var378() throws AnnotationException  {
	byte chr = 11;
	int pos = 112064431;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BCO2",annot);
	}
}

/**
 *<P>
 * annovar: FOXRED1
 * chr11:126139100T>C
 *</P>
 */
@Test public void testUTR5Var386() throws AnnotationException  {
	byte chr = 11;
	int pos = 126139100;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FOXRED1",annot);
	}
}

/**
 *<P>
 * annovar: B4GALNT3
 * chr12:662276C>T
 *</P>
 */
@Test public void testUTR5Var387() throws AnnotationException  {
	byte chr = 12;
	int pos = 662276;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("B4GALNT3",annot);
	}
}

/**
 *<P>
 * annovar: CLEC4A
 * chr12:8276432C>T
 *</P>
 */
@Test public void testUTR5Var390() throws AnnotationException  {
	byte chr = 12;
	int pos = 8276432;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CLEC4A",annot);
	}
}

/**
 *<P>
 * annovar: FAM90A1
 * chr12:8377448T>-
 *</P>
 */
@Test public void testUTR5Var392() throws AnnotationException  {
	byte chr = 12;
	int pos = 8377448;
	String ref = "T";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FAM90A1",annot);
	}
}

/**
 *<P>
 * annovar: MED21
 * chr12:27175494T>C
 *</P>
 */
@Test public void testUTR5Var394() throws AnnotationException  {
	byte chr = 12;
	int pos = 27175494;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MED21",annot);
	}
}

/**
 *<P>
 * annovar: CACNB3;CACNB3
 * chr12:49218812->T
 *</P>
 */
@Test public void testUTR5Var397() throws AnnotationException  {
	byte chr = 12;
	int pos = 49218812;
	String ref = "-";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR53,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CACNB3",annot);
	}
}

/**
 *<P>
 * annovar: LMBR1L
 * chr12:49497643T>A
 *</P>
 */
@Test public void testUTR5Var398() throws AnnotationException  {
	byte chr = 12;
	int pos = 49497643;
	String ref = "T";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LMBR1L",annot);
	}
}

/**
 *<P>
 * annovar: TUBA1B
 * chr12:49523526AT>-
 *</P>
 */
@Test public void testUTR5Var399() throws AnnotationException  {
	byte chr = 12;
	int pos = 49523526;
	String ref = "AT";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TUBA1B",annot);
	}
}

/**
 *<P>
 * annovar: POU6F1
 * chr12:51593616T>G
 *</P>
 */
@Test public void testUTR5Var403() throws AnnotationException  {
	byte chr = 12;
	int pos = 51593616;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("POU6F1",annot);
	}
}

/**
 *<P>
 * annovar: RPS26
 * chr12:56435929C>G
 *</P>
 */
@Test public void testUTR5Var408() throws AnnotationException  {
	byte chr = 12;
	int pos = 56435929;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RPS26",annot);
	}
}

/**
 *<P>
 * annovar: SLC39A5
 * chr12:56625045T>C
 *</P>
 */
@Test public void testUTR5Var409() throws AnnotationException  {
	byte chr = 12;
	int pos = 56625045;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC39A5",annot);
	}
}

/**
 *<P>
 * annovar: ARHGAP9;ARHGAP9
 * chr12:57870648A>G
 *</P>
 */
@Test public void testUTR5Var412() throws AnnotationException  {
	byte chr = 12;
	int pos = 57870648;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR53,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ARHGAP9",annot);
	}
}

/**
 *<P>
 * annovar: ZNF84
 * chr12:133618022G>A
 *</P>
 */
@Test public void testUTR5Var425() throws AnnotationException  {
	byte chr = 12;
	int pos = 133618022;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF84",annot);
	}
}

/**
 *<P>
 * annovar: CAB39L
 * chr13:50008301A>G
 *</P>
 */
@Test public void testUTR5Var432() throws AnnotationException  {
	byte chr = 13;
	int pos = 50008301;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CAB39L",annot);
	}
}

/**
 *<P>
 * annovar: TOX4
 * chr14:21955930G>A
 *</P>
 */
@Test public void testUTR5Var441() throws AnnotationException  {
	byte chr = 14;
	int pos = 21955930;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TOX4",annot);
	}
}

/**
 *<P>
 * annovar: TRAV12-1
 * chr14:22309386A>C
 *</P>
 */
@Test public void testUTR5Var443() throws AnnotationException  {
	byte chr = 14;
	int pos = 22309386;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TRAV12-1",annot);
	}
}

/**
 *<P>
 * annovar: KCNK10
 * chr14:88792816T>G
 *</P>
 */
@Test public void testUTR5Var461() throws AnnotationException  {
	byte chr = 14;
	int pos = 88792816;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KCNK10",annot);
	}
}

/**
 *<P>
 * annovar: MEG3
 * chr14:101301012T>C
 *</P>
 */
@Test public void testUTR5Var466() throws AnnotationException  {
	byte chr = 14;
	int pos = 101301012;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MEG3",annot);
	}
}

/**
 *<P>
 * annovar: SERINC4
 * chr15:44091351A>T
 *</P>
 */
@Test public void testUTR5Var474() throws AnnotationException  {
	byte chr = 15;
	int pos = 44091351;
	String ref = "A";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SERINC4",annot);
	}
}

/**
 *<P>
 * annovar: FAH
 * chr15:80450389G>A
 *</P>
 */
@Test public void testUTR5Var479() throws AnnotationException  {
	byte chr = 15;
	int pos = 80450389;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FAH",annot);
	}
}

/**
 *<P>
 * annovar: PDE8A
 * chr15:85607563G>A
 *</P>
 */
@Test public void testUTR5Var482() throws AnnotationException  {
	byte chr = 15;
	int pos = 85607563;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PDE8A",annot);
	}
}

/**
 *<P>
 * annovar: MCTP2
 * chr15:94899308A>G
 *</P>
 */
@Test public void testUTR5Var486() throws AnnotationException  {
	byte chr = 15;
	int pos = 94899308;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MCTP2",annot);
	}
}

/**
 *<P>
 * annovar: C16orf59
 * chr16:2510603C>T
 *</P>
 */
@Test public void testUTR5Var490() throws AnnotationException  {
	byte chr = 16;
	int pos = 2510603;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C16orf59",annot);
	}
}

/**
 *<P>
 * annovar: ERCC4
 * chr16:14026007G>A
 *</P>
 */
@Test public void testUTR5Var491() throws AnnotationException  {
	byte chr = 16;
	int pos = 14026007;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ERCC4",annot);
	}
}

/**
 *<P>
 * annovar: GPR114
 * chr16:57595999A>G
 *</P>
 */
@Test public void testUTR5Var497() throws AnnotationException  {
	byte chr = 16;
	int pos = 57595999;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("GPR114",annot);
	}
}

/**
 *<P>
 * annovar: ALOX15
 * chr17:4544983->AAG
 *</P>
 */
@Test public void testUTR5Var509() throws AnnotationException  {
	byte chr = 17;
	int pos = 4544983;
	String ref = "-";
	String alt = "AAG";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ALOX15",annot);
	}
}

/**
 *<P>
 * annovar: ALOX15
 * chr17:4544983->AAG
 *</P>
 */
@Test public void testUTR5Var512() throws AnnotationException  {
	byte chr = 17;
	int pos = 4544983;
	String ref = "-";
	String alt = "AAG";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ALOX15",annot);
	}
}

/**
 *<P>
 * annovar: PLSCR3
 * chr17:7297452A>G
 *</P>
 */
@Test public void testUTR5Var515() throws AnnotationException  {
	byte chr = 17;
	int pos = 7297452;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PLSCR3",annot);
	}
}

/**
 *<P>
 * annovar: ACE
 * chr17:61565990G>C
 *</P>
 */
@Test public void testUTR5Var536() throws AnnotationException  {
	byte chr = 17;
	int pos = 61565990;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ACE",annot);
	}
}

/**
 *<P>
 * annovar: MYO15B
 * chr17:73586358G>A
 *</P>
 */
@Test public void testUTR5Var540() throws AnnotationException  {
	byte chr = 17;
	int pos = 73586358;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MYO15B",annot);
	}
}

/**
 *<P>
 * annovar: LOC100653515
 * chr17:76897974C>T
 *</P>
 */
@Test public void testUTR5Var543() throws AnnotationException  {
	byte chr = 17;
	int pos = 76897974;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC100653515",annot);
	}
}

/**
 *<P>
 * annovar: ANKRD62
 * chr18:12094006C>T
 *</P>
 */
@Test public void testUTR5Var550() throws AnnotationException  {
	byte chr = 18;
	int pos = 12094006;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ANKRD62",annot);
	}
}

/**
 *<P>
 * annovar: MAPK4
 * chr18:48190271T>G
 *</P>
 */
@Test public void testUTR5Var556() throws AnnotationException  {
	byte chr = 18;
	int pos = 48190271;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MAPK4",annot);
	}
}

/**
 *<P>
 * annovar: ZNF682
 * chr19:20150175G>C
 *</P>
 */
@Test public void testUTR5Var568() throws AnnotationException  {
	byte chr = 19;
	int pos = 20150175;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF682",annot);
	}
}

/**
 *<P>
 * annovar: ZNF430
 * chr19:21203552C>T
 *</P>
 */
@Test public void testUTR5Var570() throws AnnotationException  {
	byte chr = 19;
	int pos = 21203552;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF430",annot);
	}
}

/**
 *<P>
 * annovar: PNMAL1
 * chr19:46974307C>T
 *</P>
 */
@Test public void testUTR5Var580() throws AnnotationException  {
	byte chr = 19;
	int pos = 46974307;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PNMAL1",annot);
	}
}

/**
 *<P>
 * annovar: TMEM150B
 * chr19:55832427T>G
 *</P>
 */
@Test public void testUTR5Var591() throws AnnotationException  {
	byte chr = 19;
	int pos = 55832427;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TMEM150B",annot);
	}
}

/**
 *<P>
 * annovar: CCDC106
 * chr19:56160399T>C
 *</P>
 */
@Test public void testUTR5Var593() throws AnnotationException  {
	byte chr = 19;
	int pos = 56160399;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CCDC106",annot);
	}
}

/**
 *<P>
 * annovar: HPS4
 * chr22:26862153C>A
 *</P>
 */
@Test public void testUTR5Var618() throws AnnotationException  {
	byte chr = 22;
	int pos = 26862153;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HPS4",annot);
	}
}

/**
 *<P>
 * annovar: APOL1
 * chr22:36649966C>T
 *</P>
 */
@Test public void testUTR5Var621() throws AnnotationException  {
	byte chr = 22;
	int pos = 36649966;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("APOL1",annot);
	}
}

/**
 *<P>
 * annovar: RPL3
 * chr22:39712936G>A
 *</P>
 */
@Test public void testUTR5Var624() throws AnnotationException  {
	byte chr = 22;
	int pos = 39712936;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RPL3",annot);
	}
}

/**
 *<P>
 * annovar: VCX
 * chrX_CHROMOSOME:7811234AGCTGCG>-
 *</P>
 */
@Test public void testUTR5Var627() throws AnnotationException  {
	byte chr = X_CHROMOSOME;
	int pos = 7811234;
	String ref = "AGCTGCG";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("VCX",annot);
	}
}

/**
 *<P>
 * annovar: PCYT1B
 * chrX_CHROMOSOME:24665273G>A
 *</P>
 */
@Test public void testUTR5Var632() throws AnnotationException  {
	byte chr = X_CHROMOSOME;
	int pos = 24665273;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PCYT1B",annot);
	}
}

/**
 *<P>
 * annovar: EFHC2
 * chrX_CHROMOSOME:44202891->C
 *</P>
 */
@Test public void testUTR5Var635() throws AnnotationException  {
	byte chr = X_CHROMOSOME;
	int pos = 44202891;
	String ref = "-";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("EFHC2",annot);
	}
}

/**
 *<P>
 * annovar: GRIA3
 * chrX_CHROMOSOME:122318387->C
 *</P>
 */
@Test public void testUTR5Var643() throws AnnotationException  {
	byte chr = X_CHROMOSOME;
	int pos = 122318387;
	String ref = "-";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("GRIA3",annot);
	}
}


}