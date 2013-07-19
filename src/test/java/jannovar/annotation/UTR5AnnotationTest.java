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
import jannovar.exception.JannovarException;
import jannovar.io.SerializationManager;
import jannovar.io.UCSCKGParser;
import jannovar.common.Constants;
import jannovar.common.VariantType;
import jannovar.reference.TranscriptModel;
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
	    Assert.assertEquals("SLIT2(uc003gpr.1:c.*51G>A,uc003gps.1:c.*51G>A)",annot);
	}
}


/**
 *<P>
 * annovar: AX747676
 * chr13:76445189A>G
 *</P>
 */
@Test public void testNcRnaExonicVar392() throws AnnotationException  {
	byte chr = 13;
	int pos = 76445189;
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
	    Assert.assertEquals("C13orf45(uc001vjy.2:c.-74A>G)",annot);
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
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ECM1(uc010pcf.2:c.*148C>T,uc010pce.2:c.*148C>T)",annot);
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
	    Assert.assertEquals("KIF26B(uc010pyq.1:c.-39C>T,uc001ibf.1:c.-39C>T)",annot);
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
	    Assert.assertEquals("TRIM58(uc001idp.1:c.-10A>T)",annot);
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
	    Assert.assertEquals("ALMS1P(uc010yrl.2:c.-37T>G)",annot);
	}
}

/**
 *<P>
 * annovar: ASB18
 * chr2:237150166A>C
 * Note minus strand.
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
	    Assert.assertEquals("ASB18(uc010fyp.1:c.-3T>G)",annot);
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
	    Assert.assertEquals("SCML4(uc010kdf.3:c.-49G>A,uc011eam.1:c.-49G>A,uc003psa.3:c.-230G>A)",annot);
	}
}


/**
 *<P>
 * annovar: UBAP2
 * chr9:33933705A>G
 * Minus strand
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
	    Assert.assertEquals("UBAP2(uc003ztp.2:c.-393T>C)",annot);
	}
}

/**
 *<P>
 * annovar: C9orf84
 * chr9:114521630A>G
 * Minus strand.
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
	    Assert.assertEquals("C9orf84(uc010mug.4:c.-62T>C,uc004bfq.3:c.-62T>C)",annot);
	}
}

/**
 *<P>
 * annovar: CATSPER1
 * chr11:65793878A>-
 * Minus strand.
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
	    Assert.assertEquals("CATSPER1(uc001ogt.3:c.-28delT)",annot);
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
	    Assert.assertEquals("FAM90A1(uc001qui.2:c.-20delA,uc001quh.2:c.-20delA)",annot);
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
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CACNB3(uc010slx.2:c.*255_256insT)",annot);
	}
}

/**
 *<P>
 * annovar: TUBA1B
 */
@Test public void testUTR5Var399() throws AnnotationException  {
	byte chr = 12;
	int pos = 49525089;
	String ref = "CT";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR5,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TUBA1B(uc001rtm.3:c.-7_-6delAG,uc001rtl.3:c.-1687_-1686delAG)",annot);
	}
}


/**
 *<P>
 * annovar: ALOX15
 * chr17:4544983->AAG
 * Minus strand
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
	    Assert.assertEquals("ALOX15(uc010vsd.2:c.-37_-36insCTT)",annot);
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
	    Assert.assertEquals("ACE(uc010ddv.2:c.-33G>C)",annot);
	}
}




/**
 *<P>
 * annovar: HPS4
 * chr22:26862153C>A
 * MInus strand
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
	    Assert.assertEquals("HPS4(uc003ach.4:c.-725G>T)",annot);
	}
}

/**
 *<P>
 * annovar: VCX
 * chrX_CHROMOSOME:7811234AGCTGCG>-
 * The nucleotides in the mRNA right before the start codon are
 * agacgttg[agctgcg]gaag
 * Thus, the bases -11 to -5 are affected 
 * 
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
	    Assert.assertEquals("VCX(uc004crz.3:c.-11_-5delAGCTGCG)",annot);
	}
}



}