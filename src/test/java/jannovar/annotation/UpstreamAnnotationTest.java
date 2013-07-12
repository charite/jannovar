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
import java.io.InputStream;
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
public class UpstreamAnnotationTest implements Constants {

    
   
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



/**
 *<P>
 * annovar: C1orf167
 * chr1:11832100T>A
 *</P>
 */
@Test public void testUpstreamVar2() throws AnnotationException  {
	byte chr = 1;
	int pos = 11832100;
	String ref = "T";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C1orf167(dist=39)",annot);
	}
}
/**
 *<P>
 * annovar: KIAA2013
 * chr1:11986554->G
 *</P>
 */
@Test public void testUpstreamVar3() throws AnnotationException  {
	byte chr = 1;
	int pos = 11986554;
	String ref = "-";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KIAA2013(dist=69)",annot);
	}
}

/**
 *<P>
 * annovar: PADI6
 * chr1:17698725C>T
 *</P>
 */
@Test public void testUpstreamVar4() throws AnnotationException  {
	byte chr = 1;
	int pos = 17698725;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PADI6(dist=16)",annot);
	}
}

/**
 *<P>
 * annovar: C8B
 * chr1:57432128T>C
 *</P>
 */
@Test public void testUpstreamVar5() throws AnnotationException  {
	byte chr = 1;
	int pos = 57432128;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C8B(dist=440)",annot);
	}
}

/**
 *<P>
 * annovar: Mir_548
 * chr1:79152696A>G
 *</P>
 */
@Test public void testUpstreamVar6() throws AnnotationException  {
    byte chr = 1;
    int pos = 79152696;
    String ref = "A";
    String alt = "G";
    Chromosome c = chromosomeMap.get(chr); 
    if (c==null) {
	Assert.fail("Could not identify chromosome \"" + chr + "\"");
    } else {
	AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	VariantType varType = ann.getVariantType();
	Assert.assertEquals(VariantType.UPSTREAM,varType);
	String annot = ann.getVariantAnnotation();
	Assert.assertEquals("Mir_548(dist=49)",annot);
    }
}

/**
 *<P>
 * annovar: SAMD13
 * chr1:84764012A>T
 *</P>
 */
@Test public void testUpstreamVar7() throws AnnotationException  {
	byte chr = 1;
	int pos = 84764012;
	String ref = "A";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SAMD13(dist=37)",annot);
	}
}

/**
 *<P>
 * annovar: CLCA3P
 * chr1:87099909A>G
 *</P>
 */
@Test public void testUpstreamVar8() throws AnnotationException  {
	byte chr = 1;
	int pos = 87099909;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CLCA3P(dist=50)",annot);
	}
}

/**
 *<P>
 * annovar: ADAR
 * chr1:154600533A>G
 *</P>
 */
@Test public void testUpstreamVar9() throws AnnotationException  {
	byte chr = 1;
	int pos = 154600533;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ADAR(dist=77)",annot);
	}
}



/**
 *<P>
 * annovar: OR1C1
 * chr1:247921717C>A
 *</P>
 */
@Test public void testUpstreamVar12() throws AnnotationException  {
	byte chr = 1;
	int pos = 247921717;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR1C1(dist=9)",annot);
	}
}

/**
 *<P>
 * annovar: OR11L1
 * chr1:248005213G>C
 *</P>
 */
@Test public void testUpstreamVar13() throws AnnotationException  {
	byte chr = 1;
	int pos = 248005213;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR11L1(dist=15)",annot);
	}
}

/**
 *<P>
 * annovar: OR2T4
 * chr1:248524817CA>-
 *</P>
 */
@Test public void testUpstreamVar14() throws AnnotationException  {
	byte chr = 1;
	int pos = 248524817;
	String ref = "CA";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR2T4(dist=65)",annot);
	}
}

/**
 *<P>
 * annovar: Mir_548
 * chr2:34628927AAAAT>-
 *</P>
 */
@Test public void testUpstreamVar15() throws AnnotationException  {
	byte chr = 2;
	int pos = 34628927;
	String ref = "AAAAT";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("Mir_548(dist=105)",annot);
	}
}

/**
 *<P>
 * annovar: LOC388946
 * chr2:46706618C>G
 *</P>
 */
@Test public void testUpstreamVar16() throws AnnotationException  {
	byte chr = 2;
	int pos = 46706618;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TMEM247(dist=86)",annot);
	}
}



/**
 *<P>
 * annovar: SF3B1
 * chr2:198299808G>A
 *</P>
 */
@Test public void testUpstreamVar22() throws AnnotationException  {
	byte chr = 2;
	int pos = 198299808;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SF3B1(dist=37)",annot);
	}
}



/**
 *<P>
 * annovar: ROBO2
 * chr3:75986622C>G
 *</P>
 */
@Test public void testUpstreamVar28() throws AnnotationException  {
	byte chr = 3;
	int pos = 75986622;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ROBO2(dist=23)",annot);
	}
}

/**
 *<P>
 * annovar: CEP97
 * chr3:101443461T>C
 *</P>
 */
@Test public void testUpstreamVar29() throws AnnotationException  {
	byte chr = 3;
	int pos = 101443461;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CEP97(dist=33)",annot);
	}
}




/**
 *<P>
 * annovar: FGFBP2
 * chr4:15965194C>T
 *</P>
 */
@Test public void testUpstreamVar33() throws AnnotationException  {
	byte chr = 4;
	int pos = 15965194;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FGFBP2(dist=335)",annot);
	}
}

/**
 *<P>
 * annovar: DQ596041
 * chr5:98861045->CAGG
 * DQ591060 (uc021yby.1) chr5:98,861,082-98,861,113, distance=38 Strand: -
 * DQ596041 (uc011cuv.1) chr5:98,860,479-98,860,510, distance=536
 * Here, there is one annotation for upstream and one for DOWNSTREAM. The option UPSTREAM is
 * chosen arbitrarily for this variant, which is correct, but actually both annotations are made.
 * probably we should change the way such variants are shown.
 *</P>
 */
@Test public void testUpstreamVar36() throws AnnotationException  {
	byte chr = 5;
	int pos = 98861045;
	String ref = "-";
	String alt = "CAGG";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    Assert.assertEquals("DQ596041(dist=535),DQ591060(dist=37)",annot);
	}
}

/**
 *<P>
 * annovar: GRXCR2
 * chr5:145252574C>T
 *</P>
 */
@Test public void testUpstreamVar37() throws AnnotationException  {
	byte chr = 5;
	int pos = 145252574;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("GRXCR2(dist=43)",annot);
	}
}




}