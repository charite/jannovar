package jannovar.annotation;


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
import jannovar.exception.JannovarException;
import jannovar.io.SerializationManager;
import jannovar.io.UCSCKGParser;
import jannovar.common.Constants;
import jannovar.common.VariantType;
import jannovar.reference.TranscriptModel;
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
public class UTR3AnnotationTest implements Constants {

    
   
    private static HashMap<Byte,Chromosome> chromosomeMap = null;


    @BeforeClass 
	public static void setUp() throws IOException, JannovarException  {
	ArrayList<TranscriptModel> kgList=null;
	java.net.URL url = SynonymousAnnotationTest.class.getResource("/ucsc.ser");
	String path = url.getPath();
	SerializationManager manager = new SerializationManager();
	kgList = manager.deserializeKnownGeneList(path);
	chromosomeMap = Chromosome.constructChromosomeMapWithIntervalTree(kgList);
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
 * annovar: CD24
 * chrY_CHROMOSOME:21154323G>A
 *</P>
 */
@Test public void testUTR3Var1346b() throws AnnotationException  {
	byte chr = Y_CHROMOSOME;
	int pos = 21154323;
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
	    Assert.assertEquals("CD24",annot);
	}
}

/**
 *<P>
 * annovar: TP73
 * chr1:3646137C>T
 *</P>
 */
@Test public void testUTR3Var4() throws AnnotationException  {
	byte chr = 1;
	int pos = 3646137;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TP73",annot);
	}
}

/**
 *<P>
 * annovar: TP73
 * chr1:3646192G>A
 *</P>
 */
@Test public void testUTR3Var5() throws AnnotationException  {
	byte chr = 1;
	int pos = 3646192;
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
	    Assert.assertEquals("TP73",annot);
	}
}

/**
 *<P>
 * annovar: THAP3
 * chr1:6693165->TA
 *</P>
 */
@Test public void testUTR3Var7() throws AnnotationException  {
	byte chr = 1;
	int pos = 6693165;
	String ref = "-";
	String alt = "TA";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("THAP3",annot);
	}
}

/**
 *<P>
 * annovar: CLCN6
 * chr1:11876662G>A
 *</P>
 */
@Test public void testUTR3Var9() throws AnnotationException  {
	byte chr = 1;
	int pos = 11876662;
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
	    Assert.assertEquals("CLCN6",annot);
	}
}

/**
 *<P>
 * annovar: CLCNKB
 * chr1:16383742C>G
 *</P>
 */
@Test public void testUTR3Var13() throws AnnotationException  {
	byte chr = 1;
	int pos = 16383742;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CLCNKB",annot);
	}
}

/**
 *<P>
 * annovar: COL8A2
 * chr1:36563158C>A
 *</P>
 */
@Test public void testUTR3Var26() throws AnnotationException  {
	byte chr = 1;
	int pos = 36563158;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("COL8A2",annot);
	}
}

/**
 *<P>
 * annovar: MEAF6
 * chr1:37959450T>C
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these
 *</P>
 */
@Test public void testUTR3Var27() throws AnnotationException  {
	byte chr = 1;
	int pos = 37959450;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MEAF6",annot);
	}
}

/**
 *<P>
 * annovar: ANGPTL3
 * chr1:63070540TAATGTGGT>-
 *</P>
 */
@Test public void testUTR3Var44() throws AnnotationException  {
	byte chr = 1;
	int pos = 63070540;
	String ref = "TAATGTGGT";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ANGPTL3",annot);
	}
}

/**
 *<P>
 * annovar: GBP7
 * chr1:89597755T>C
 *</P>
 */
@Test public void testUTR3Var54() throws AnnotationException  {
	byte chr = 1;
	int pos = 89597755;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("GBP7",annot);
	}
}

/**
 *<P>
 * annovar: PSMB4
 * chr1:151373200T>C
 *</P>
 */
@Test public void testUTR3Var72() throws AnnotationException  {
	byte chr = 1;
	int pos = 151373200;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PSMB4",annot);
	}
}

/**
 *<P>
 * annovar: IL6R
 * chr1:154437896T>C
 *</P>
 */
@Test public void testUTR3Var86() throws AnnotationException  {
	byte chr = 1;
	int pos = 154437896;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("IL6R",annot);
	}
}

/**
 *<P>
 * annovar: ISG20L2
 * chr1:156693135C>T
 *</P>
 */
@Test public void testUTR3Var90() throws AnnotationException  {
	byte chr = 1;
	int pos = 156693135;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ISG20L2",annot);
	}
}

/**
 *<P>
 * annovar: FCGR2B
 * chr1:161643333A>G
 *</P>
 */
@Test public void testUTR3Var95() throws AnnotationException  {
	byte chr = 1;
	int pos = 161643333;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FCGR2B",annot);
	}
}

/**
 *<P>
 * annovar: LRRC52
 * chr1:165533075C>T
 *</P>
 */
@Test public void testUTR3Var96() throws AnnotationException  {
	byte chr = 1;
	int pos = 165533075;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LRRC52",annot);
	}
}

/**
 *<P>
 * annovar: XCL1
 * chr1:168550535A>G
 *</P>
 */
@Test public void testUTR3Var99() throws AnnotationException  {
	byte chr = 1;
	int pos = 168550535;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("XCL1",annot);
	}
}

/**
 *<P>
 * annovar: RGS21
 * chr1:192335274->CTAA
 *</P>
 */
@Test public void testUTR3Var112() throws AnnotationException  {
	byte chr = 1;
	int pos = 192335274;
	String ref = "-";
	String alt = "CTAA";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RGS21",annot);
	}
}

/**
 *<P>
 * annovar: RGS21
 * chr1:192335275->TAAT
 *</P>
 */
@Test public void testUTR3Var116() throws AnnotationException  {
	byte chr = 1;
	int pos = 192335275;
	String ref = "-";
	String alt = "TAAT";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RGS21",annot);
	}
}

/**
 *<P>
 * annovar: LMOD1
 * chr1:201865763A>G
 *</P>
 */
@Test public void testUTR3Var119() throws AnnotationException  {
	byte chr = 1;
	int pos = 201865763;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LMOD1",annot);
	}
}

/**
 *<P>
 * annovar: HS1BP3
 * chr2:20818454T>A
 *</P>
 */
@Test public void testUTR3Var137() throws AnnotationException  {
	byte chr = 2;
	int pos = 20818454;
	String ref = "T";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HS1BP3",annot);
	}
}

/**
 *<P>
 * annovar: HS1BP3
 * chr2:20818458G>A
 *</P>
 */
@Test public void testUTR3Var138() throws AnnotationException  {
	byte chr = 2;
	int pos = 20818458;
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
	    Assert.assertEquals("HS1BP3",annot);
	}
}

/**
 *<P>
 * annovar: CIB4
 * chr2:26804218G>A
 *</P>
 */
@Test public void testUTR3Var140() throws AnnotationException  {
	byte chr = 2;
	int pos = 26804218;
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
	    Assert.assertEquals("CIB4",annot);
	}
}

/**
 *<P>
 * annovar: PREB
 * chr2:27354173C>A
 *</P>
 */
@Test public void testUTR3Var142() throws AnnotationException  {
	byte chr = 2;
	int pos = 27354173;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PREB",annot);
	}
}

/**
 *<P>
 * annovar: EIF2B4
 * chr2:27587266G>A
 *</P>
 */
@Test public void testUTR3Var143() throws AnnotationException  {
	byte chr = 2;
	int pos = 27587266;
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
	    Assert.assertEquals("EIF2B4",annot);
	}
}

/**
 *<P>
 * annovar: EIF2B4
 * chr2:27589810T>C
 *</P>
 */
@Test public void testUTR3Var144() throws AnnotationException  {
	byte chr = 2;
	int pos = 27589810;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("EIF2B4",annot);
	}
}

/**
 *<P>
 * annovar: PLB1
 * chr2:28816948C>T
 *</P>
 */
@Test public void testUTR3Var146() throws AnnotationException  {
	byte chr = 2;
	int pos = 28816948;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PLB1",annot);
	}
}

/**
 *<P>
 * annovar: CDC42EP3
 * chr2:37872871C>T
 *</P>
 */
@Test public void testUTR3Var148() throws AnnotationException  {
	byte chr = 2;
	int pos = 37872871;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CDC42EP3",annot);
	}
}

/**
 *<P>
 * annovar: PLEK
 * chr2:68622952G>C
 *</P>
 */
@Test public void testUTR3Var153() throws AnnotationException  {
	byte chr = 2;
	int pos = 68622952;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PLEK",annot);
	}
}

/**
 *<P>
 * annovar: PLEK
 * chr2:68622990T>C
 *</P>
 */
@Test public void testUTR3Var154() throws AnnotationException  {
	byte chr = 2;
	int pos = 68622990;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PLEK",annot);
	}
}

/**
 *<P>
 * annovar: ACTG2
 * chr2:74129895G>A
 *</P>
 */
@Test public void testUTR3Var161() throws AnnotationException  {
	byte chr = 2;
	int pos = 74129895;
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
	    Assert.assertEquals("ACTG2",annot);
	}
}

/**
 *<P>
 * annovar: REG3G
 * chr2:79255426G>C
 *</P>
 */
@Test public void testUTR3Var163() throws AnnotationException  {
	byte chr = 2;
	int pos = 79255426;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("REG3G",annot);
	}
}

/**
 *<P>
 * annovar: REG1A
 * chr2:79350347A>G
 *</P>
 */
@Test public void testUTR3Var164() throws AnnotationException  {
	byte chr = 2;
	int pos = 79350347;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("REG1A",annot);
	}
}

/**
 *<P>
 * annovar: GNLY
 * chr2:85925756C>G
 *</P>
 */
@Test public void testUTR3Var166() throws AnnotationException  {
	byte chr = 2;
	int pos = 85925756;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("GNLY",annot);
	}
}

/**
 *<P>
 * annovar: EDAR
 * chr2:109513321C>A
 *</P>
 */
@Test public void testUTR3Var172() throws AnnotationException  {
	byte chr = 2;
	int pos = 109513321;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("EDAR",annot);
	}
}

/**
 *<P>
 * annovar: KLHL23,PHOSPHO2-KLHL23
 * chr2:170606300->A
 *</P>
 */
@Test public void testUTR3Var182() throws AnnotationException  {
	byte chr = 2;
	int pos = 170606300;
	String ref = "-";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KLHL23,PHOSPHO2-KLHL23",annot);
	}
}

/**
 *<P>
 * annovar: OSBPL6
 * chr2:179260382A>G
 *</P>
 */
@Test public void testUTR3Var186() throws AnnotationException  {
	byte chr = 2;
	int pos = 179260382;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OSBPL6",annot);
	}
}

/**
 *<P>
 * annovar: AX746670
 * chr2:179444626A>C
 *</P>
 */
@Test public void testUTR3Var187() throws AnnotationException  {
	byte chr = 2;
	int pos = 179444626;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AX746670",annot);
	}
}

/**
 *<P>
 * annovar: ACADL
 * chr2:211082599T>C
 *</P>
 */
@Test public void testUTR3Var199() throws AnnotationException  {
	byte chr = 2;
	int pos = 211082599;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ACADL",annot);
	}
}

/**
 *<P>
 * annovar: SPAG16
 * chr2:214182100A>T
 *</P>
 */
@Test public void testUTR3Var200() throws AnnotationException  {
	byte chr = 2;
	int pos = 214182100;
	String ref = "A";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SPAG16",annot);
	}
}

/**
 *<P>
 * annovar: GPBAR1
 * chr2:219128506C>T
 *</P>
 */
@Test public void testUTR3Var205() throws AnnotationException  {
	byte chr = 2;
	int pos = 219128506;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("GPBAR1",annot);
	}
}

/**
 *<P>
 * annovar: NMUR1
 * chr2:232389744A>G
 *</P>
 */
@Test public void testUTR3Var213() throws AnnotationException  {
	byte chr = 2;
	int pos = 232389744;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NMUR1",annot);
	}
}

/**
 *<P>
 * annovar: NGEF
 * chr2:233759362G>C
 *</P>
 */
@Test public void testUTR3Var215() throws AnnotationException  {
	byte chr = 2;
	int pos = 233759362;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NGEF",annot);
	}
}

/**
 *<P>
 * annovar: NDUFA10
 * chr2:240954110A>G
 *</P>
 */
@Test public void testUTR3Var223() throws AnnotationException  {
	byte chr = 2;
	int pos = 240954110;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NDUFA10",annot);
	}
}

/**
 *<P>
 * annovar: GLT8D1
 * chr3:52728804C>T
 *</P>
 */
@Test public void testUTR3Var248() throws AnnotationException  {
	byte chr = 3;
	int pos = 52728804;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("GLT8D1",annot);
	}
}

/**
 *<P>
 * annovar: FRG2C
 * chr3:75715199T>G
 *</P>
 */
@Test public void testUTR3Var254() throws AnnotationException  {
	byte chr = 3;
	int pos = 75715199;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FRG2C",annot);
	}
}

/**
 *<P>
 * annovar: BTLA
 * chr3:112184927A>G
 *</P>
 */
@Test public void testUTR3Var262() throws AnnotationException  {
	byte chr = 3;
	int pos = 112184927;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BTLA",annot);
	}
}

/**
 *<P>
 * annovar: RUVBL1
 * chr3:127800071A>G
 *</P>
 */
@Test public void testUTR3Var273() throws AnnotationException  {
	byte chr = 3;
	int pos = 127800071;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RUVBL1",annot);
	}
}

/**
 *<P>
 * annovar: AX746590
 * chr3:183523657A>G
 *</P>
 */
@Test public void testUTR3Var292() throws AnnotationException  {
	byte chr = 3;
	int pos = 183523657;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AX746590",annot);
	}
}

/**
 *<P>
 * annovar: TPRG1
 * chr3:189038648T>C
 *</P>
 */
@Test public void testUTR3Var298() throws AnnotationException  {
	byte chr = 3;
	int pos = 189038648;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TPRG1",annot);
	}
}

/**
 *<P>
 * annovar: ACOX3
 * chr4:8368604C>T
 *</P>
 */
@Test public void testUTR3Var307() throws AnnotationException  {
	byte chr = 4;
	int pos = 8368604;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ACOX3",annot);
	}
}

/**
 *<P>
 * annovar: KDR
 * chr4:55946081A>G
 *</P>
 */
@Test public void testUTR3Var318() throws AnnotationException  {
	byte chr = 4;
	int pos = 55946081;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KDR",annot);
	}
}

/**
 *<P>
 * annovar: SMR3A
 * chr4:71232742C>G
 *</P>
 */
@Test public void testUTR3Var323() throws AnnotationException  {
	byte chr = 4;
	int pos = 71232742;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SMR3A",annot);
	}
}

/**
 *<P>
 * annovar: MUC7
 * chr4:71347694T>C
 *</P>
 */
@Test public void testUTR3Var324() throws AnnotationException  {
	byte chr = 4;
	int pos = 71347694;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MUC7",annot);
	}
}

/**
 *<P>
 * annovar: RRH
 * chr4:110765361T>G
 *</P>
 */
@Test public void testUTR3Var335() throws AnnotationException  {
	byte chr = 4;
	int pos = 110765361;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RRH",annot);
	}
}

/**
 *<P>
 * annovar: AK308309
 * chr4:119435320CAAGAA>-
 *</P>
 */
@Test public void testUTR3Var338() throws AnnotationException  {
	byte chr = 4;
	int pos = 119435320;
	String ref = "CAAGAA";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AK308309",annot);
	}
}

/**
 *<P>
 * annovar: FRG1
 * chr4:190884289->GACA
 *</P>
 */
@Test public void testUTR3Var352() throws AnnotationException  {
	byte chr = 4;
	int pos = 190884289;
	String ref = "-";
	String alt = "GACA";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FRG1",annot);
	}
}

/**
 *<P>
 * annovar: SLC6A3
 * chr5:1393761->GG
 *</P>
 */
@Test public void testUTR3Var360() throws AnnotationException  {
	byte chr = 5;
	int pos = 1393761;
	String ref = "-";
	String alt = "GG";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC6A3",annot);
	}
}

/**
 *<P>
 * annovar: NPR3
 * chr5:32789852T>C
 *</P>
 */
@Test public void testUTR3Var366() throws AnnotationException  {
	byte chr = 5;
	int pos = 32789852;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NPR3",annot);
	}
}

/**
 *<P>
 * annovar: DNAJC21
 * chr5:34956294C>T
 *</P>
 */
@Test public void testUTR3Var369() throws AnnotationException  {
	byte chr = 5;
	int pos = 34956294;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DNAJC21",annot);
	}
}

/**
 *<P>
 * annovar: IL31RA
 * chr5:55213023C>T
 *</P>
 */
@Test public void testUTR3Var376() throws AnnotationException  {
	byte chr = 5;
	int pos = 55213023;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("IL31RA",annot);
	}
}

/**
 *<P>
 * annovar: ATP10B
 * chr5:160039687T>C
 *</P>
 */
@Test public void testUTR3Var390() throws AnnotationException  {
	byte chr = 5;
	int pos = 160039687;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ATP10B",annot);
	}
}

/**
 *<P>
 * annovar: PXDC1
 * chr6:3723764G>T
 *</P>
 */
@Test public void testUTR3Var402() throws AnnotationException  {
	byte chr = 6;
	int pos = 3723764;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PXDC1",annot);
	}
}

/**
 *<P>
 * annovar: KIF13A
 * chr6:17764279A>C
 *</P>
 */
@Test public void testUTR3Var407() throws AnnotationException  {
	byte chr = 6;
	int pos = 17764279;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KIF13A",annot);
	}
}

/**
 *<P>
 * annovar: DCDC2
 * chr6:24174955G>C
 *</P>
 */
@Test public void testUTR3Var408() throws AnnotationException  {
	byte chr = 6;
	int pos = 24174955;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DCDC2",annot);
	}
}

/**
 *<P>
 * annovar: GPLD1
 * chr6:24448061A>C
 *</P>
 */
@Test public void testUTR3Var409() throws AnnotationException  {
	byte chr = 6;
	int pos = 24448061;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("GPLD1",annot);
	}
}

/**
 *<P>
 * annovar: BTN2A2
 * chr6:26392535G>A
 *</P>
 */
@Test public void testUTR3Var413() throws AnnotationException  {
	byte chr = 6;
	int pos = 26392535;
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
	    Assert.assertEquals("BTN2A2",annot);
	}
}

/**
 *<P>
 * annovar: HLA-A
 * chr6:29913074A>G
 *</P>
 */
@Test public void testUTR3Var418() throws AnnotationException  {
	byte chr = 6;
	int pos = 29913074;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HLA-A",annot);
	}
}

/**
 *<P>
 * annovar: HLA-C
 * chr6:31236853G>A
 *</P>
 */
@Test public void testUTR3Var427() throws AnnotationException  {
	byte chr = 6;
	int pos = 31236853;
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
	    Assert.assertEquals("HLA-C",annot);
	}
}

/**
 *<P>
 * annovar: MICB
 * chr6:31477771GA>-
 *</P>
 */
@Test public void testUTR3Var430() throws AnnotationException  {
	byte chr = 6;
	int pos = 31477771;
	String ref = "GA";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MICB",annot);
	}
}

/**
 *<P>
 * annovar: HLA-DMB
 * chr6:32904661G>A
 *</P>
 */
@Test public void testUTR3Var436() throws AnnotationException  {
	byte chr = 6;
	int pos = 32904661;
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
	    Assert.assertEquals("HLA-DMB",annot);
	}
}

/**
 *<P>
 * annovar: LEMD2
 * chr6:33740382G>A
 *</P>
 */
@Test public void testUTR3Var443() throws AnnotationException  {
	byte chr = 6;
	int pos = 33740382;
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
	    Assert.assertEquals("LEMD2",annot);
	}
}

/**
 *<P>
 * annovar: PRPH2
 * chr6:42666020G>A
 *</P>
 */
@Test public void testUTR3Var448() throws AnnotationException  {
	byte chr = 6;
	int pos = 42666020;
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
	    Assert.assertEquals("PRPH2",annot);
	}
}

/**
 *<P>
 * annovar: PKHD1
 * chr6:51586771G>A
 *</P>
 */
@Test public void testUTR3Var453() throws AnnotationException  {
	byte chr = 6;
	int pos = 51586771;
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
	    Assert.assertEquals("PKHD1",annot);
	}
}

/**
 *<P>
 * annovar: ARMC2
 * chr6:109294752C>T
 *</P>
 */
@Test public void testUTR3Var467() throws AnnotationException  {
	byte chr = 6;
	int pos = 109294752;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ARMC2",annot);
	}
}

/**
 *<P>
 * annovar: ULBP3
 * chr6:150385730T>G
 *</P>
 */
@Test public void testUTR3Var472() throws AnnotationException  {
	byte chr = 6;
	int pos = 150385730;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ULBP3",annot);
	}
}

/**
 *<P>
 * annovar: SYNJ2
 * chr6:158517451A>G
 *</P>
 */
@Test public void testUTR3Var474() throws AnnotationException  {
	byte chr = 6;
	int pos = 158517451;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SYNJ2",annot);
	}
}

/**
 *<P>
 * annovar: SLC22A2
 * chr6:160670494A>G
 *</P>
 */
@Test public void testUTR3Var478() throws AnnotationException  {
	byte chr = 6;
	int pos = 160670494;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC22A2",annot);
	}
}

/**
 *<P>
 * annovar: SLC22A2
 * chr6:160671500T>C
 *</P>
 */
@Test public void testUTR3Var479() throws AnnotationException  {
	byte chr = 6;
	int pos = 160671500;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC22A2",annot);
	}
}

/**
 *<P>
 * annovar: CDCA7L
 * chr7:21941866TCTT>-
 *</P>
 */
@Test public void testUTR3Var498() throws AnnotationException  {
	byte chr = 7;
	int pos = 21941866;
	String ref = "TCTT";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CDCA7L",annot);
	}
}

/**
 *<P>
 * annovar: WBSCR22
 * chr7:73118196T>C
 *</P>
 */
@Test public void testUTR3Var511() throws AnnotationException  {
	byte chr = 7;
	int pos = 73118196;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("WBSCR22",annot);
	}
}

/**
 *<P>
 * annovar: HGF
 * chr7:81372156A>G
 *</P>
 */
@Test public void testUTR3Var513() throws AnnotationException  {
	byte chr = 7;
	int pos = 81372156;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HGF",annot);
	}
}

/**
 *<P>
 * annovar: EMID2
 * chr7:101200877G>A
 *</P>
 */
@Test public void testUTR3Var522() throws AnnotationException  {
	byte chr = 7;
	int pos = 101200877;
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
	    Assert.assertEquals("EMID2",annot);
	}
}

/**
 *<P>
 * annovar: TSPAN12
 * chr7:120428607G>A
 *</P>
 */
@Test public void testUTR3Var527() throws AnnotationException  {
	byte chr = 7;
	int pos = 120428607;
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
	    Assert.assertEquals("TSPAN12",annot);
	}
}

/**
 *<P>
 * annovar: SLC13A4
 * chr7:135366196G>A
 *</P>
 */
@Test public void testUTR3Var537() throws AnnotationException  {
	byte chr = 7;
	int pos = 135366196;
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
	    Assert.assertEquals("SLC13A4",annot);
	}
}

/**
 *<P>
 * annovar: AKR1D1
 * chr7:137801413A>C
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these
 *</P>
 */
@Test public void testUTR3Var538() throws AnnotationException  {
	byte chr = 7;
	int pos = 137801413;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AKR1D1",annot);
	}
}

/**
 *<P>
 * annovar: LZTS1
 * chr8:20107143A>G
 *</P>
 */
@Test public void testUTR3Var556() throws AnnotationException  {
	byte chr = 8;
	int pos = 20107143;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LZTS1",annot);
	}
}

/**
 *<P>
 * annovar: PBK
 * chr8:27667793C>T
 *</P>
 */
@Test public void testUTR3Var565() throws AnnotationException  {
	byte chr = 8;
	int pos = 27667793;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PBK",annot);
	}
}

/**
 *<P>
 * annovar: C8orf86
 * chr8:38369831A>C
 *</P>
 */
@Test public void testUTR3Var569() throws AnnotationException  {
	byte chr = 8;
	int pos = 38369831;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C8orf86",annot);
	}
}

/**
 *<P>
 * annovar: HTRA4
 * chr8:38845638A>C
 *</P>
 */
@Test public void testUTR3Var570() throws AnnotationException  {
	byte chr = 8;
	int pos = 38845638;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HTRA4",annot);
	}
}

/**
 *<P>
 * annovar: C8orf45
 * chr8:67813633A>G
 *</P>
 */
@Test public void testUTR3Var572() throws AnnotationException  {
	byte chr = 8;
	int pos = 67813633;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C8orf45",annot);
	}
}

/**
 *<P>
 * annovar: TMEM70
 * chr8:74893880C>T
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these
 *</P>
 */
@Test public void testUTR3Var575() throws AnnotationException  {
	byte chr = 8;
	int pos = 74893880;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TMEM70",annot);
	}
}

/**
 *<P>
 * annovar: KANK1
 * chr9:732302T>C
 *</P>
 */
@Test public void testUTR3Var587() throws AnnotationException  {
	byte chr = 9;
	int pos = 732302;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KANK1",annot);
	}
}

/**
 *<P>
 * annovar: C9orf11
 * chr9:27284708T>A
 *</P>
 */
@Test public void testUTR3Var597() throws AnnotationException  {
	byte chr = 9;
	int pos = 27284708;
	String ref = "T";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C9orf11",annot);
	}
}

/**
 *<P>
 * annovar: FAM75D3
 * chr9:84563495->CTAC
 *</P>
 */
@Test public void testUTR3Var608() throws AnnotationException  {
	byte chr = 9;
	int pos = 84563495;
	String ref = "-";
	String alt = "CTAC";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FAM75D3",annot);
	}
}

/**
 *<P>
 * annovar: LOC286238
 * chr9:91262296G>A
 *</P>
 */
@Test public void testUTR3Var610() throws AnnotationException  {
	byte chr = 9;
	int pos = 91262296;
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
	    Assert.assertEquals("LOC286238",annot);
	}
}

/**
 *<P>
 * annovar: ROR2
 * chr9:94456643G>C
 *</P>
 */
@Test public void testUTR3Var611() throws AnnotationException  {
	byte chr = 9;
	int pos = 94456643;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ROR2",annot);
	}
}

/**
 *<P>
 * annovar: ROR2
 * chr9:94485928C>T
 *</P>
 */
@Test public void testUTR3Var612() throws AnnotationException  {
	byte chr = 9;
	int pos = 94485928;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ROR2",annot);
	}
}

/**
 *<P>
 * annovar: DDX31
 * chr9:135470176C>G
 *</P>
 */
@Test public void testUTR3Var643() throws AnnotationException  {
	byte chr = 9;
	int pos = 135470176;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DDX31",annot);
	}
}

/**
 *<P>
 * annovar: ATP5C1
 * chr10:7849688T>C
 *</P>
 */
@Test public void testUTR3Var656() throws AnnotationException  {
	byte chr = 10;
	int pos = 7849688;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ATP5C1",annot);
	}
}

/**
 *<P>
 * annovar: TRDMT1
 * chr10:17195428G>C
 *</P>
 */
@Test public void testUTR3Var662() throws AnnotationException  {
	byte chr = 10;
	int pos = 17195428;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TRDMT1",annot);
	}
}

/**
 *<P>
 * annovar: CACNB2
 * chr10:18828663G>T
 *</P>
 */
@Test public void testUTR3Var663() throws AnnotationException  {
	byte chr = 10;
	int pos = 18828663;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CACNB2",annot);
	}
}

/**
 *<P>
 * annovar: ARHGAP22
 * chr10:49654403C>T
 *</P>
 */
@Test public void testUTR3Var678() throws AnnotationException  {
	byte chr = 10;
	int pos = 49654403;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ARHGAP22",annot);
	}
}

/**
 *<P>
 * annovar: WDFY4
 * chr10:50190799C>T
 *</P>
 */
@Test public void testUTR3Var681() throws AnnotationException  {
	byte chr = 10;
	int pos = 50190799;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("WDFY4",annot);
	}
}

/**
 *<P>
 * annovar: PBLD
 * chr10:70048643G>A
 *</P>
 */
@Test public void testUTR3Var688() throws AnnotationException  {
	byte chr = 10;
	int pos = 70048643;
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
	    Assert.assertEquals("PBLD",annot);
	}
}

/**
 *<P>
 * annovar: LRRC20
 * chr10:72061079C>T
 *</P>
 */
@Test public void testUTR3Var690() throws AnnotationException  {
	byte chr = 10;
	int pos = 72061079;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LRRC20",annot);
	}
}

/**
 *<P>
 * annovar: CDHR1
 * chr10:85974381A>G
 *</P>
 */
@Test public void testUTR3Var696() throws AnnotationException  {
	byte chr = 10;
	int pos = 85974381;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CDHR1",annot);
	}
}

/**
 *<P>
 * annovar: AGAP11
 * chr10:88769678->TGC
 *</P>
 */
@Test public void testUTR3Var697() throws AnnotationException  {
	byte chr = 10;
	int pos = 88769678;
	String ref = "-";
	String alt = "TGC";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AGAP11",annot);
	}
}

/**
 *<P>
 * annovar: ADAM8
 * chr10:135076523C>A
 *</P>
 */
@Test public void testUTR3Var716() throws AnnotationException  {
	byte chr = 10;
	int pos = 135076523;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ADAM8",annot);
	}
}

/**
 *<P>
 * annovar: POLR2L
 * chr11:840363C>T
 *</P>
 */
@Test public void testUTR3Var724() throws AnnotationException  {
	byte chr = 11;
	int pos = 840363;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("POLR2L",annot);
	}
}

/**
 *<P>
 * annovar: TRIM22
 * chr11:5730914C>T
 *</P>
 */
@Test public void testUTR3Var735() throws AnnotationException  {
	byte chr = 11;
	int pos = 5730914;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TRIM22",annot);
	}
}

/**
 *<P>
 * annovar: DCDC1
 * chr11:30965855A>T
 *</P>
 */
@Test public void testUTR3Var746() throws AnnotationException  {
	byte chr = 11;
	int pos = 30965855;
	String ref = "A";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DCDC1",annot);
	}
}

/**
 *<P>
 * annovar: WT1
 * chr11:32410516T>C
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these
 *</P>
 */
@Test public void testUTR3Var748() throws AnnotationException  {
	byte chr = 11;
	int pos = 32410516;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("WT1",annot);
	}
}

/**
 *<P>
 * annovar: AK097878
 * chr11:45234877T>C
 *</P>
 */
@Test public void testUTR3Var751() throws AnnotationException  {
	byte chr = 11;
	int pos = 45234877;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AK097878",annot);
	}
}

/**
 *<P>
 * annovar: LOC440040
 * chr11:49831603G>-
 *</P>
 */
@Test public void testUTR3Var755() throws AnnotationException  {
	byte chr = 11;
	int pos = 49831603;
	String ref = "G";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC440040",annot);
	}
}

/**
 *<P>
 * annovar: FADS3
 * chr11:61641211A>G
 *</P>
 */
@Test public void testUTR3Var761() throws AnnotationException  {
	byte chr = 11;
	int pos = 61641211;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FADS3",annot);
	}
}

/**
 *<P>
 * annovar: SLC22A20
 * chr11:64993197->TAAG
 *</P>
 */
@Test public void testUTR3Var766() throws AnnotationException  {
	byte chr = 11;
	int pos = 64993197;
	String ref = "-";
	String alt = "TAAG";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC22A20",annot);
	}
}

/**
 *<P>
 * annovar: SLC22A20
 * chr11:64993200->GCAA
 *</P>
 */
@Test public void testUTR3Var767() throws AnnotationException  {
	byte chr = 11;
	int pos = 64993200;
	String ref = "-";
	String alt = "GCAA";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC22A20",annot);
	}
}

/**
 *<P>
 * annovar: FAM86C2P
 * chr11:67559770C>A
 *</P>
 */
@Test public void testUTR3Var773() throws AnnotationException  {
	byte chr = 11;
	int pos = 67559770;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FAM86C2P",annot);
	}
}

/**
 *<P>
 * annovar: SLC35F2
 * chr11:107675566C>T
 *</P>
 */
@Test public void testUTR3Var786() throws AnnotationException  {
	byte chr = 11;
	int pos = 107675566;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC35F2",annot);
	}
}

/**
 *<P>
 * annovar: KDELC2
 * chr11:108345515->A
 *</P>
 */
@Test public void testUTR3Var787() throws AnnotationException  {
	byte chr = 11;
	int pos = 108345515;
	String ref = "-";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KDELC2",annot);
	}
}

/**
 *<P>
 * annovar: APOC3
 * chr11:116703671G>T
 *</P>
 */
@Test public void testUTR3Var795() throws AnnotationException  {
	byte chr = 11;
	int pos = 116703671;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("APOC3",annot);
	}
}

/**
 *<P>
 * annovar: GALNT8
 * chr12:4881775T>C
 *</P>
 */
@Test public void testUTR3Var809() throws AnnotationException  {
	byte chr = 12;
	int pos = 4881775;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("GALNT8",annot);
	}
}

/**
 *<P>
 * annovar: DPPA3
 * chr12:7869698->CCCG
 *</P>
 */
@Test public void testUTR3Var815() throws AnnotationException  {
	byte chr = 12;
	int pos = 7869698;
	String ref = "-";
	String alt = "CCCG";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DPPA3",annot);
	}
}

/**
 *<P>
 * annovar: KLRC2
 * chr12:10583696C>T
 *</P>
 */
@Test public void testUTR3Var821() throws AnnotationException  {
	byte chr = 12;
	int pos = 10583696;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KLRC2",annot);
	}
}

/**
 *<P>
 * annovar: STRAP
 * chr12:16055927->T
 *</P>
 */
@Test public void testUTR3Var824() throws AnnotationException  {
	byte chr = 12;
	int pos = 16055927;
	String ref = "-";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("STRAP",annot);
	}
}

/**
 *<P>
 * annovar: CELA1
 * chr12:51722311C>T
 *</P>
 */
@Test public void testUTR3Var839() throws AnnotationException  {
	byte chr = 12;
	int pos = 51722311;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CELA1",annot);
	}
}

/**
 *<P>
 * annovar: WIBG
 * chr12:56295548TAAG>-
 *</P>
 */
@Test public void testUTR3Var843() throws AnnotationException  {
	byte chr = 12;
	int pos = 56295548;
	String ref = "TAAG";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("WIBG",annot);
	}
}

/**
 *<P>
 * annovar: SLC5A8
 * chr12:101550977CA>-
 *</P>
 */
@Test public void testUTR3Var850() throws AnnotationException  {
	byte chr = 12;
	int pos = 101550977;
	String ref = "CA";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC5A8",annot);
	}
}

/**
 *<P>
 * annovar: PRDM4
 * chr12:108127965A>G
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these
 *</P>
 */
@Test public void testUTR3Var852() throws AnnotationException  {
	byte chr = 12;
	int pos = 108127965;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PRDM4",annot);
	}
}

/**
 *<P>
 * annovar: OAS1
 * chr12:113369759G>C
 *</P>
 */
@Test public void testUTR3Var855() throws AnnotationException  {
	byte chr = 12;
	int pos = 113369759;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OAS1",annot);
	}
}

/**
 *<P>
 * annovar: SLC46A3
 * chr13:29275102T>C
 *</P>
 */
@Test public void testUTR3Var864() throws AnnotationException  {
	byte chr = 13;
	int pos = 29275102;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC46A3",annot);
	}
}

/**
 *<P>
 * annovar: CLDN10
 * chr13:96230279T>G
 *</P>
 */
@Test public void testUTR3Var884() throws AnnotationException  {
	byte chr = 13;
	int pos = 96230279;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CLDN10",annot);
	}
}

/**
 *<P>
 * annovar: POTEM
 * chr14:19988246T>C
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these
 *</P>
 */
@Test public void testUTR3Var893() throws AnnotationException  {
	byte chr = 14;
	int pos = 19988246;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("POTEM",annot);
	}
}

/**
 *<P>
 * annovar: RNASE12
 * chr14:21058425G>T
 *</P>
 */
@Test public void testUTR3Var895() throws AnnotationException  {
	byte chr = 14;
	int pos = 21058425;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RNASE12",annot);
	}
}

/**
 *<P>
 * annovar: ECRP
 * chr14:21388302G>A
 *</P>
 */
@Test public void testUTR3Var898() throws AnnotationException  {
	byte chr = 14;
	int pos = 21388302;
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
	    Assert.assertEquals("ECRP",annot);
	}
}

/**
 *<P>
 * annovar: ABHD12B
 * chr14:51371121C>T
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these
 *</P>
 */
@Test public void testUTR3Var916() throws AnnotationException  {
	byte chr = 14;
	int pos = 51371121;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ABHD12B",annot);
	}
}

/**
 *<P>
 * annovar: GCH1
 * chr14:55310492G>A
 *</P>
 */
@Test public void testUTR3Var923() throws AnnotationException  {
	byte chr = 14;
	int pos = 55310492;
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
	    Assert.assertEquals("GCH1",annot);
	}
}

/**
 *<P>
 * annovar: ESR2
 * chr14:64694195C>T
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these
 *</P>
 */
@Test public void testUTR3Var930() throws AnnotationException  {
	byte chr = 14;
	int pos = 64694195;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ESR2",annot);
	}
}

/**
 *<P>
 * annovar: MAX
 * chr14:65550816T>A
 *</P>
 */
@Test public void testUTR3Var932() throws AnnotationException  {
	byte chr = 14;
	int pos = 65550816;
	String ref = "T";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MAX",annot);
	}
}

/**
 *<P>
 * annovar: AHNAK2
 * chr14:105404384T>C
 *</P>
 */
@Test public void testUTR3Var950() throws AnnotationException  {
	byte chr = 14;
	int pos = 105404384;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AHNAK2",annot);
	}
}

/**
 *<P>
 * annovar: LOC653061
 * chr15:23610305C>T
 *</P>
 */
@Test public void testUTR3Var952() throws AnnotationException  {
	byte chr = 15;
	int pos = 23610305;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC653061",annot);
	}
}

/**
 *<P>
 * annovar: HERC2
 * chr15:28356859C>T
 *</P>
 */
@Test public void testUTR3Var953() throws AnnotationException  {
	byte chr = 15;
	int pos = 28356859;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HERC2",annot);
	}
}

/**
 *<P>
 * annovar: FMN1
 * chr15:33066478C>T
 *</P>
 */
@Test public void testUTR3Var955() throws AnnotationException  {
	byte chr = 15;
	int pos = 33066478;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FMN1",annot);
	}
}

/**
 *<P>
 * annovar: GATM
 * chr15:45654305G>A
 *</P>
 */
@Test public void testUTR3Var963() throws AnnotationException  {
	byte chr = 15;
	int pos = 45654305;
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
	    Assert.assertEquals("GATM",annot);
	}
}

/**
 *<P>
 * annovar: SLC30A4
 * chr15:45777344T>C
 *</P>
 */
@Test public void testUTR3Var964() throws AnnotationException  {
	byte chr = 15;
	int pos = 45777344;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC30A4",annot);
	}
}

/**
 *<P>
 * annovar: BCL2L10
 * chr15:52401933A>G
 *</P>
 */
@Test public void testUTR3Var966() throws AnnotationException  {
	byte chr = 15;
	int pos = 52401933;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BCL2L10",annot);
	}
}

/**
 *<P>
 * annovar: IGDCC3
 * chr15:65621138G>A
 *</P>
 */
@Test public void testUTR3Var971() throws AnnotationException  {
	byte chr = 15;
	int pos = 65621138;
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
	    Assert.assertEquals("IGDCC3",annot);
	}
}

/**
 *<P>
 * annovar: SCAPER
 * chr15:76673627T>C
 *</P>
 */
@Test public void testUTR3Var982() throws AnnotationException  {
	byte chr = 15;
	int pos = 76673627;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SCAPER",annot);
	}
}

/**
 *<P>
 * annovar: MEF2A
 * chr15:100256618A>C
 *</P>
 -- jannovar annotates as ncRNA exonic, correctly
@Test public void testUTR3Var989() throws AnnotationException  {
	byte chr = 15;
	int pos = 100256618;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MEF2A",annot);
	}
}
*/

/**
 *<P>
 * annovar: POLR3K
 * chr16:97354A>G
 *</P>
 */
@Test public void testUTR3Var992() throws AnnotationException  {
	byte chr = 16;
	int pos = 97354;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("POLR3K",annot);
	}
}

/**
 *<P>
 * annovar: RAB40C
 * chr16:677629T>C
 *</P>
 */
@Test public void testUTR3Var993() throws AnnotationException  {
	byte chr = 16;
	int pos = 677629;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RAB40C",annot);
	}
}

/**
 *<P>
 * annovar: ZG16B
 * chr16:2882197G>T
 *</P>
 */
@Test public void testUTR3Var998() throws AnnotationException  {
	byte chr = 16;
	int pos = 2882197;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZG16B",annot);
	}
}

/**
 *<P>
 * annovar: METTL22
 * chr16:8740015A>C
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these
 *</P>
 */
@Test public void testUTR3Var1006() throws AnnotationException  {
	byte chr = 16;
	int pos = 8740015;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("METTL22",annot);
	}
}

/**
 *<P>
 * annovar: EMP2
 * chr16:10625887C>T
 *</P>
 */
@Test public void testUTR3Var1008() throws AnnotationException  {
	byte chr = 16;
	int pos = 10625887;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("EMP2",annot);
	}
}

/**
 *<P>
 * annovar: USP31
 * chr16:23079353T>C
 *</P>
 */
@Test public void testUTR3Var1014() throws AnnotationException  {
	byte chr = 16;
	int pos = 23079353;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("USP31",annot);
	}
}

/**
 *<P>
 * annovar: FBXL19-AS1
 * chr16:30934075G>A
 *</P>
 */
@Test public void testUTR3Var1017() throws AnnotationException  {
	byte chr = 16;
	int pos = 30934075;
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
	    Assert.assertEquals("FBXL19-AS1",annot);
	}
}

/**
 *<P>
 * annovar: CMTM3
 * chr16:66646591C>T
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these
 *</P>
 */
@Test public void testUTR3Var1025() throws AnnotationException  {
	byte chr = 16;
	int pos = 66646591;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CMTM3",annot);
	}
}

/**
 *<P>
 * annovar: ACD
 * chr16:67691477A>T
 *</P>
 */
@Test public void testUTR3Var1026() throws AnnotationException  {
	byte chr = 16;
	int pos = 67691477;
	String ref = "A";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ACD",annot);
	}
}

/**
 *<P>
 * annovar: C16orf46
 * chr16:81094760A>G
 *</P>
 */
@Test public void testUTR3Var1037() throws AnnotationException  {
	byte chr = 16;
	int pos = 81094760;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C16orf46",annot);
	}
}

/**
 *<P>
 * annovar: AK126852
 * chr16:88123598C>G
 *</P>
 */
@Test public void testUTR3Var1044() throws AnnotationException  {
	byte chr = 16;
	int pos = 88123598;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AK126852",annot);
	}
}

/**
 *<P>
 * annovar: SERPINF2
 * chr17:1657899G>T
 *</P>
 */
@Test public void testUTR3Var1049() throws AnnotationException  {
	byte chr = 17;
	int pos = 1657899;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SERPINF2",annot);
	}
}

/**
 *<P>
 * annovar: C17orf49;RNASEK,RNASEK-C17ORF49
 * chr17:6917703C>T
-- An unusual one
 *</P>
 */
@Test public void testUTR3Var1055() throws AnnotationException  {
	byte chr = 17;
	int pos = 6917703;
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
	    Assert.assertEquals("C17orf49,RNASEK,RNASEK-C17ORF49",annot);
	}
}

/**
 *<P>
 * annovar: C17orf108
 * chr17:26206414C>A
 *</P>
 */
@Test public void testUTR3Var1073() throws AnnotationException  {
	byte chr = 17;
	int pos = 26206414;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C17orf108",annot);
	}
}

/**
 *<P>
 * annovar: FLOT2
 * chr17:27207571C>T
 *</P>
 */
@Test public void testUTR3Var1074() throws AnnotationException  {
	byte chr = 17;
	int pos = 27207571;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FLOT2",annot);
	}
}

/**
 *<P>
 * annovar: DUSP3
 * chr17:41846964T>C
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these
 *</P>
 */
@Test public void testUTR3Var1089() throws AnnotationException  {
	byte chr = 17;
	int pos = 41846964;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DUSP3",annot);
	}
}

/**
 *<P>
 * annovar: MPP2
 * chr17:41955158A>G
 *</P>
 */
@Test public void testUTR3Var1090() throws AnnotationException  {
	byte chr = 17;
	int pos = 41955158;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MPP2",annot);
	}
}

/**
 *<P>
 * annovar: SNX11
 * chr17:46198876G>C
 *</P>
 */
@Test public void testUTR3Var1099() throws AnnotationException  {
	byte chr = 17;
	int pos = 46198876;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SNX11",annot);
	}
}

/**
 *<P>
 * annovar: ATP5G1
 * chr17:46973146G>-
 *</P>
 */
@Test public void testUTR3Var1100() throws AnnotationException  {
	byte chr = 17;
	int pos = 46973146;
	String ref = "G";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ATP5G1",annot);
	}
}

/**
 *<P>
 * annovar: MRPL27
 * chr17:48445699C>T
 *</P>
 */
@Test public void testUTR3Var1103() throws AnnotationException  {
	byte chr = 17;
	int pos = 48445699;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MRPL27",annot);
	}
}

/**
 *<P>
 * annovar: TBX2
 * chr17:59485393C>T
 *</P>
 */
@Test public void testUTR3Var1107() throws AnnotationException  {
	byte chr = 17;
	int pos = 59485393;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TBX2",annot);
	}
}

/**
 *<P>
 * annovar: ITGB4
 * chr17:73753661C>T
 *</P>
 */
@Test public void testUTR3Var1115() throws AnnotationException  {
	byte chr = 17;
	int pos = 73753661;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ITGB4",annot);
	}
}

/**
 *<P>
 * annovar: EXOC7
 * chr17:74077497G>C
 *</P>
 */
@Test public void testUTR3Var1118() throws AnnotationException  {
	byte chr = 17;
	int pos = 74077497;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("EXOC7",annot);
	}
}

/**
 *<P>
 * annovar: ENOSF1,TYMS
 * chr18:673016C>T
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these)
 * Note that there is a 3UTR annotation for TYMS, but this is not prioritized because the variant also hits
 * a ncRNA exonic sequence of ENOSF1
 *</P>
 */
@Test public void testUTR3Var1124() throws AnnotationException  {
	byte chr = 18;
	int pos = 673016;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ENOSF1",annot);
	}
}

/**
 *<P>
 * annovar: SLC14A2
 * chr18:43262532C>A
 *</P>
 */
@Test public void testUTR3Var1131() throws AnnotationException  {
	byte chr = 18;
	int pos = 43262532;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC14A2",annot);
	}
}

/**
 *<P>
 * annovar: SKA1
 * chr18:47918639A>C
 *</P>
 */
@Test public void testUTR3Var1133() throws AnnotationException  {
	byte chr = 18;
	int pos = 47918639;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SKA1",annot);
	}
}

/**
 *<P>
 * annovar: FZR1
 * chr19:3534842G>A
 *</P>
 */
@Test public void testUTR3Var1146() throws AnnotationException  {
	byte chr = 19;
	int pos = 3534842;
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
	    Assert.assertEquals("FZR1",annot);
	}
}

/**
 *<P>
 * annovar: ZNF626
 * chr19:20806893T>C
 *</P>
 */
@Test public void testUTR3Var1171() throws AnnotationException  {
	byte chr = 19;
	int pos = 20806893;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF626",annot);
	}
}

/**
 *<P>
 * annovar: C19orf40
 * chr19:33467620T>C
 *</P>
 */
@Test public void testUTR3Var1174() throws AnnotationException  {
	byte chr = 19;
	int pos = 33467620;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C19orf40",annot);
	}
}

/**
 *<P>
 * annovar: LGI4
 * chr19:35616086C>T
 *</P>
 */
@Test public void testUTR3Var1178() throws AnnotationException  {
	byte chr = 19;
	int pos = 35616086;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LGI4",annot);
	}
}

/**
 *<P>
 * annovar: C19orf55
 * chr19:36259494C>A
 *</P>
 */
@Test public void testUTR3Var1183() throws AnnotationException  {
	byte chr = 19;
	int pos = 36259494;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C19orf55",annot);
	}
}

/**
 *<P>
 * annovar: FBXO27
 * chr19:39515939T>C
 *</P>
 */
@Test public void testUTR3Var1188() throws AnnotationException  {
	byte chr = 19;
	int pos = 39515939;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FBXO27",annot);
	}
}

/**
 *<P>
 * annovar: FCGBP
 * chr19:40354067T>C
 *</P>
 */
@Test public void testUTR3Var1189() throws AnnotationException  {
	byte chr = 19;
	int pos = 40354067;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FCGBP",annot);
	}
}

/**
 *<P>
 * annovar: MIA
 * chr19:41283365C>G
 *</P>
 */
@Test public void testUTR3Var1192() throws AnnotationException  {
	byte chr = 19;
	int pos = 41283365;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MIA",annot);
	}
}

/**
 *<P>
 * annovar: PLA2G4C
 * chr19:48551546A>T
 *</P>
 */
@Test public void testUTR3Var1199() throws AnnotationException  {
	byte chr = 19;
	int pos = 48551546;
	String ref = "A";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PLA2G4C",annot);
	}
}

/**
 *<P>
 * annovar: ZNF610
 * chr19:52870052C>G
 *</P>
 */
@Test public void testUTR3Var1204() throws AnnotationException  {
	byte chr = 19;
	int pos = 52870052;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF610",annot);
	}
}

/**
 *<P>
 * annovar: ZNF528
 * chr19:52920009->GT
 *</P>
 */
@Test public void testUTR3Var1205() throws AnnotationException  {
	byte chr = 19;
	int pos = 52920009;
	String ref = "-";
	String alt = "GT";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF528",annot);
	}
}

/**
 *<P>
 * annovar: ZNF528
 * chr19:52920009->GT
 *</P>
 */
@Test public void testUTR3Var1206() throws AnnotationException  {
	byte chr = 19;
	int pos = 52920009;
	String ref = "-";
	String alt = "GT";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF528",annot);
	}
}

/**
 *<P>
 * annovar: ZNF160
 * chr19:53576589C>G
 *</P>
 */
@Test public void testUTR3Var1209() throws AnnotationException  {
	byte chr = 19;
	int pos = 53576589;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF160",annot);
	}
}

/**
 *<P>
 * annovar: LILRB5
 * chr19:54756302A>G
 *</P>
 */
@Test public void testUTR3Var1215() throws AnnotationException  {
	byte chr = 19;
	int pos = 54756302;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LILRB5",annot);
	}
}

/**
 *<P>
 * annovar: KIR2DS4
 * chr19:55359426A>G
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these
 *</P>
 */
@Test public void testUTR3Var1217() throws AnnotationException  {
	byte chr = 19;
	int pos = 55359426;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KIR2DS4",annot);
	}
}

/**
 *<P>
 * annovar: SEL1L2
 * chr20:13830102C>T
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these
 *</P>
 */
@Test public void testUTR3Var1229() throws AnnotationException  {
	byte chr = 20;
	int pos = 13830102;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SEL1L2",annot);
	}
}

/**
 *<P>
 * annovar: C20orf26
 * chr20:20152462A>C
 *</P>
 */
@Test public void testUTR3Var1231() throws AnnotationException  {
	byte chr = 20;
	int pos = 20152462;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C20orf26",annot);
	}
}

/**
 *<P>
 * annovar: ZBP1
 * chr20:56179586G>A
 *</P>
 */
@Test public void testUTR3Var1243() throws AnnotationException  {
	byte chr = 20;
	int pos = 56179586;
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
	    Assert.assertEquals("ZBP1",annot);
	}
}

/**
 *<P>
 * annovar: BAGE2
 * chr21:11029616G>T
 *</P>
 -- jannovar gets  "TPTE" (Type:ncRNA_exonic)
--- this seems correct to me.
@Test public void testUTR3Var1248() throws AnnotationException  {
	byte chr = 21;
	int pos = 11029616;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BAGE2",annot);
	}
}*/

/**
 *<P>
 * annovar: JAM2
 * chr21:27087044T>C
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these
 *</P>
 */
@Test public void testUTR3Var1257() throws AnnotationException  {
	byte chr = 21;
	int pos = 27087044;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("JAM2",annot);
	}
}

/**
 *<P>
 * annovar: ADAMTS5
 * chr21:28296324A>G
 *</P>
 */
@Test public void testUTR3Var1258() throws AnnotationException  {
	byte chr = 21;
	int pos = 28296324;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ADAMTS5",annot);
	}
}

/**
 *<P>
 * annovar: TRAPPC10
 * chr21:45523416C>T
 *</P>
 */
@Test public void testUTR3Var1269() throws AnnotationException  {
	byte chr = 21;
	int pos = 45523416;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TRAPPC10(uc011afa.2:c.*4C>T,uc002zea.3:c.*4C>T,uc010gpo.3:c.*4C>T)",annot);
	}
}

/**
 *<P>
 * annovar: C21orf33
 * chr21:45565473C>T
 *</P>
 */
@Test public void testUTR3Var1270() throws AnnotationException  {
	byte chr = 21;
	int pos = 45565473;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C21orf33",annot);
	}
}

/**
 *<P>
 * annovar: LSS
 * chr21:47608580G>A
 *</P>
 */
@Test public void testUTR3Var1276() throws AnnotationException  {
	byte chr = 21;
	int pos = 47608580;
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
	    Assert.assertEquals("LSS",annot);
	}
}

/**
 *<P>
 * annovar: C22orf13
 * chr22:24936970A>G
 *</P>
 */
@Test public void testUTR3Var1284() throws AnnotationException  {
	byte chr = 22;
	int pos = 24936970;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C22orf13",annot);
	}
}

/**
 *<P>
 * annovar: PVALB
 * chr22:37196871G>A
 *</P>
 */
@Test public void testUTR3Var1290() throws AnnotationException  {
	byte chr = 22;
	int pos = 37196871;
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
	    Assert.assertEquals("PVALB",annot);
	}
}

/**
 *<P>
 * annovar: TRIOBP
 * chr22:38168508T>C
 *</P>
 */
@Test public void testUTR3Var1291() throws AnnotationException  {
	byte chr = 22;
	int pos = 38168508;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TRIOBP",annot);
	}
}

/**
 *<P>
 * annovar: NFAM1
 * chr22:42780331G>C
 *</P>
 */
@Test public void testUTR3Var1300() throws AnnotationException  {
	byte chr = 22;
	int pos = 42780331;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NFAM1",annot);
	}
}

/**
 *<P>
 * annovar: MCAT
 * chr22:43529029C>T
 *</P>
 */
@Test public void testUTR3Var1304() throws AnnotationException  {
	byte chr = 22;
	int pos = 43529029;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MCAT",annot);
	}
}

/**
 *<P>
 * annovar: NUP50
 * chr22:45580574->TT
 *</P>
 */
@Test public void testUTR3Var1306() throws AnnotationException  {
	byte chr = 22;
	int pos = 45580574;
	String ref = "-";
	String alt = "TT";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NUP50",annot);
	}
}

/**
 *<P>
 * annovar: NCAPH2
 * chr22:50961854T>C
 *</P>
 */
@Test public void testUTR3Var1312() throws AnnotationException  {
	byte chr = 22;
	int pos = 50961854;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NCAPH2",annot);
	}
}

/**
 *<P>
 * annovar: FAM47B
 * chrX_CHROMOSOME:34962909A>C
 *</P>
 */
@Test public void testUTR3Var1317() throws AnnotationException  {
	byte chr = X_CHROMOSOME;
	int pos = 34962909;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FAM47B",annot);
	}
}

/**
 *<P>
 * annovar: CHIC1
 * chrX_CHROMOSOME:72900930G>A
 * ncRNA-Transcript exonic change together with UTR3 change of another isoform. Jannovar correctly prioritizes these
 *</P>
 */
@Test public void testUTR3Var1324() throws AnnotationException  {
	byte chr = X_CHROMOSOME;
	int pos = 72900930;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CHIC1",annot);
	}
}

/**
 *<P>
 * annovar: SAGE1
 * chrX_CHROMOSOME:134994622T>G
 *</P>
 */
@Test public void testUTR3Var1332() throws AnnotationException  {
	byte chr = X_CHROMOSOME;
	int pos = 134994622;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SAGE1",annot);
	}
}

/**
 *<P>
 * annovar: SAGE1
 * chrX_CHROMOSOME:134994633G>C
 *</P>
 */
@Test public void testUTR3Var1337() throws AnnotationException  {
	byte chr = X_CHROMOSOME;
	int pos = 134994633;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SAGE1",annot);
	}
}

/**
 *<P>
 * annovar: CD24
 * chrY_CHROMOSOME:21154323G>A
 *</P>
 */
@Test public void testUTR3Var1346() throws AnnotationException  {
	byte chr = Y_CHROMOSOME;
	int pos = 21154323;
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
	    Assert.assertEquals("CD24",annot);
	}
}


}