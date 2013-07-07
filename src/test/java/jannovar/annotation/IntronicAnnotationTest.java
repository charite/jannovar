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
public class IntronicAnnotationTest implements Constants {

    
   
    private static HashMap<Byte,Chromosome> chromosomeMap = null;

    @BeforeClass 
	public static void setUp() throws IOException, JannovarException {
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
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PLEKHN1(uc001acd.3)",annot);
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
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC35E2B(uc001ahh.4),CDK11B(uc001agz.1),CDK11A(uc009vkr.3)",annot);
	   
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
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MORN1(uc001ajb.1)",annot);
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
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CHD5(uc001amb.2)",annot);
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
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TNFRSF1B(uc001att.3)",annot);
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
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NBPF14,NBPF9,PDE4DIP",annot);
	}
}*/
 


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
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC100505666(uc021pan.2),ADAM15(uc001fgv.2)",annot);
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
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PPFIBP1(uc001ric.2)",annot);
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
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TMTC1(uc001rjb.3)",annot);
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
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BICD1(uc001rkv.3)",annot);
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
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NELL2(uc010sla.1)",annot);
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
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLC38A1(uc001rpe.3)",annot);
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
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PRKAG1(uc001rsy.3)",annot);
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
	    AnnotationList ann  = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.INTRONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TMBIM6(uc001ruz.2)",annot);
	}
}

 }
