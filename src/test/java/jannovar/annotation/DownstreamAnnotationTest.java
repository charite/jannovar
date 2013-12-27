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
public class DownstreamAnnotationTest implements Constants {

    
   
    private static HashMap<Byte,Chromosome> chromosomeMap = null;

  

    @SuppressWarnings (value="unchecked")
    @BeforeClass 
	public static void setUp() throws IOException, JannovarException {
	ArrayList<TranscriptModel> kgList=null;
	java.net.URL url = SynonymousAnnotationTest.class.getResource(UCSCserializationTestFileName);
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
 * annovar: ISG15
 * chr1:949925C>T
 *</P>
 */
@Test public void testDownstreamVar1() throws AnnotationException  {
	byte chr = 1;
	int pos = 949925;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ISG15(dist=6)",annot);
	}
}

/**
 *<P>
 * annovar: MASP2,TARDBP
 * chr1:11086098->T

<TARDBP[(dist=549),MASP2(dist=482)]>
MASP2:  chr1:11,086,580-11,107,296
11086580 - 11086098 = 482
TARDBP: chr1:11,072,679-11,085,549
11085549 - 11086098 = -549
 *</P>
 */
@Test public void testDownstreamVar2() throws AnnotationException  {
	byte chr = 1;
	int pos = 11086098;
	String ref = "-";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TARDBP(dist=549),MASP2(dist=482)",annot);
	}
}

/**
 *<P>
 * annovar: VAV3
 * chr1:108113510G>T
 *</P>
 */
@Test public void testDownstreamVar4() throws AnnotationException  {
	byte chr = 1;
	int pos = 108113510;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("VAV3(dist=272)",annot);
	}
}


/**
 *<P>
 * annovar: S100A8
 * chr1:153362507A>G
 *</P>
 */
@Test public void testDownstreamVar6() throws AnnotationException  {
	byte chr = 1;
	int pos = 153362507;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("S100A8(dist=1)",annot);
	}
}

/**
 *<P>
 * annovar: OR10K2
 * chr1:158389693C>T
 *</P>
 */
@Test public void testDownstreamVar7() throws AnnotationException  {
	byte chr = 1;
	int pos = 158389693;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR10K2(dist=25)",annot);
	}
}


/**
 *<P>
 * annovar: WNT9A
 * chr1:228109137A>G
 *</P>
 */
@Test public void testDownstreamVar9() throws AnnotationException  {
	byte chr = 1;
	int pos = 228109137;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("WNT9A(dist=28)",annot);
	}
}


/**
 *<P>
 * annovar: OR2T35
 * chr1:248801550C>T
 *</P>
 */
@Test public void testDownstreamVar13() throws AnnotationException  {
	byte chr = 1;
	int pos = 248801550;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR2T35(dist=38)",annot);
	}
}



/**
 *<P>
 * annovar: DKFZp667P0924
 * chr2:96521455A>G
 *</P>
 */
@Test public void testDownstreamVar24() throws AnnotationException  {
	byte chr = 2;
	int pos = 96521455;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ANKRD36C(dist=301)",annot);
	}
}

/**
 *<P>
 * annovar: DKFZp667P0924
 * chr2:96521548G>A
 *</P>
 */
@Test public void testDownstreamVar25() throws AnnotationException  {
	byte chr = 2;
	int pos = 96521548;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ANKRD36C(dist=208)",annot);
	}
}





/**
 *<P>
 * annovar: NPM2
 * chr8:21895146A>G
 *</P>
 */
@Test public void testDownstreamVar56() throws AnnotationException  {
	byte chr = 8;
	int pos = 21895146;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NPM2(dist=738)",annot);
	}
}



/**
 *<P>
 * annovar: DQ656008
 * chr11:5141980A>G
 *</P>
 */
@Test public void testDownstreamVar73() throws AnnotationException  {
	byte chr = 11;
	int pos = 5141980;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DQ656008(dist=405)",annot);
	}
}


/**
 *<P>
 * annovar: OR5AN1
 * chr11:59132882A>G
 *</P>
 */
@Test public void testDownstreamVar84() throws AnnotationException  {
	byte chr = 11;
	int pos = 59132882;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR5AN1(dist=15)",annot);
	}
}



}
