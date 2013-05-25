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
import java.net.URL;

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
 * <p>
 * Note that the file ucsc.ser must be created prior to using this test class
 * and should be put in src/test/resources/.
 */
public class SynonymousAnnotationTest implements Constants {

    
   
    private static HashMap<Byte,Chromosome> chromosomeMap = null;

    @BeforeClass 
    public static void setUp() throws IOException {
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
 * KLHL17, several transcripts affected, some are synonymous and some are 3UTR.
 * The synonymous should get priority.
 *</P>
 */
@Test public void testSynVar1hand() throws AnnotationException  {
	byte chr = 1;
	int pos = 897738;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KLHL17(uc001aca.2:exon5:c.715C>T:p.L239L)",annot);
	}
}

/**
 *<P>
 * annovar: RNF207:uc001amg.3:exon17:c.1718A>G:p.N573S,
 * chr1:6278414A>G
  expected:<...uc011mzv.2:exon12:c.[A1060T:p.T354S,uc010nvg.2:exon11:c.A1090T:p.T364S,uc011mzw.2:exon11:c.A1099T:p.T367S,uc004fmp.2:exon11:c.A1150]T:p.T384S)> but was:<...uc011mzv.2:exon12:c.[1060A>T:p.T354S,uc010nvg.2:exon11:c.1090A>T:p.T364S,uc011mzw.2:exon11:c.1099A>T:p.T367S,uc004fmp.2:exon11:c.1150A>]T:p.T384S)>


 *</P>
 */
@Test public void testSynVar2hand() throws AnnotationException  {
	byte chr = X_CHROMOSOME;
	int pos = 154009588;
	String ref = "T";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NONSYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MPP1(uc011mzv.2:exon12:c.1060A>T:p.T354S,uc010nvg.2:exon11:c.1090A>T:p.T364S,uc011mzw.2:exon11:c.1099A>T:p.T367S,uc004fmp.2:exon11:c.1150A>T:p.T384S)",annot);
	}
}

 	

/**
 *<P>
 * annovar: EPHA2:uc010oca.2:exon3:c.573G>A:p.L191L,EPHA2:uc001aya.2:exon3:c.573G>A:p.L191L,
 * chr1:16475123C>T
 expected:<EPHA2(uc0[10oca.2:exon3:c.573G>A:p.L191L,uc001ay]a.2:exon3:c.573G>A:p...> 
but was:<EPHA2(uc0[01aya.2:exon3:c.573G>A:p.L191L,uc010oc]a.2:exon3:c.573G>A:p...>

 *</P>
 */
@Test public void testSynonymousVar2() throws AnnotationException  {
	byte chr = 1;
	int pos = 16475123;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("EPHA2(uc001aya.2:exon3:c.573G>A:p.L191L,uc010oca.2:exon3:c.573G>A:p.L191L)",annot);
	}
}

/**
 *<P>
 * annovar: UBR4:uc001bbi.3:exon68:c.9981G>C:p.L3327L,UBR4:uc001bbk.1:exon21:c.2922G>C:p.L974L,
 * chr1:19447843C>G
 *</P>
 */
@Test public void testSynonymousVar3() throws AnnotationException  {
	byte chr = 1;
	int pos = 19447843;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("UBR4(uc001bbk.1:exon21:c.2922G>C:p.L974L,uc001bbi.3:exon68:c.9981G>C:p.L3327L)",annot);
	}
}


/**
 *<P>
 * annovar: HMGB4:uc021oky.1:exon1:c.105T>C:p.Y35Y,HMGB4:uc001bxp.3:exon2:c.105T>C:p.Y35Y,
 * chr1:34329897T>C
 expected:<HMGB4(uc0[21oky.1:exon1:c.105T>C:p.Y35Y,uc001bxp.3:exon2]:c.105T>C:p.Y35Y)> 
but was:<HMGB4(uc0[01bxp.3:exon2:c.105T>C:p.Y35Y,uc021oky.1:exon1]:c.105T>C:p.Y35Y)>

 *</P>
 */
@Test public void testSynonymousVar6() throws AnnotationException  {
	byte chr = 1;
	int pos = 34329897;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HMGB4(uc001bxp.3:exon2:c.105T>C:p.Y35Y,uc021oky.1:exon1:c.105T>C:p.Y35Y)",annot);
	}
}


/**
 *<P>
 * annovar: MRPS15:uc001cas.2:exon3:c.207C>T:p.P69P,
 * chr1:36927733G>A
 *</P>
 */
@Test public void testSynonymousVar7() throws AnnotationException  {
	byte chr = 1;
	int pos = 36927733;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MRPS15(uc001cas.2:exon3:c.207C>T:p.P69P)",annot);
	}
}


/**
 *<P>
 * annovar: FRRS1:uc001dsh.1:exon7:c.708C>T:p.S236S,
 * chr1:100203693G>A
 *</P>
 */
@Test public void testSynonymousVar9() throws AnnotationException  {
	byte chr = 1;
	int pos = 100203693;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FRRS1(uc001dsh.1:exon7:c.708C>T:p.S236S)",annot);
	}
}

/**
 *<P>
 * annovar: CELSR2:uc001dxa.4:exon1:c.1551T>C:p.S517S,
 * chr1:109794252T>C
 *</P>
 */
@Test public void testSynonymousVar10() throws AnnotationException  {
	byte chr = 1;
	int pos = 109794252;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CELSR2(uc001dxa.4:exon1:c.1551T>C:p.S517S)",annot);
	}
}


/**
 *<P>
 * annovar: DDX20:uc001ebs.3:exon11:c.1926G>A:p.V642V,DDX20:uc010owf.2:exon10:c.1212G>A:p.V404V,DDX20:uc001ebt.3:exon3:c.750G>A:p.V250V,
 * chr1:112308972G>A
 *</P>
 */
@Test public void testSynonymousVar12() throws AnnotationException  {
	byte chr = 1;
	int pos = 112308972;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DDX20(uc001ebt.3:exon3:c.750G>A:p.V250V,uc010owf.2:exon10:c.1212G>A:p.V404V,uc001ebs.3:exon11:c.1926G>A:p.V642V)",annot);
	}
}


/**
 *<P>
 * annovar: HRNR:uc001ezt.1:exon3:c.814C>A:p.R272R,
 * chr1:152193291G>T
 *</P>
 */
@Test public void testSynonymousVar13() throws AnnotationException  {
	byte chr = 1;
	int pos = 152193291;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HRNR(uc001ezt.1:exon3:c.814C>A:p.R272R)",annot);
	}
}

/**
 *<P>
 * annovar: LRRC52:uc001gde.2:exon2:c.886C>A:p.R296R,
 * chr1:165533005C>A
 *</P>
 */
@Test public void testSynonymousVar15() throws AnnotationException  {
	byte chr = 1;
	int pos = 165533005;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LRRC52(uc001gde.2:exon2:c.886C>A:p.R296R)",annot);
	}
}

/**
 *<P>
 * annovar: GORAB:uc001ggz.4:exon1:c.96A>C:p.G32G,GORAB:uc001gha.2:exon1:c.96A>C:p.G32G,GORAB:uc009wvw.2:exon1:c.96A>C:p.G32G,
 * chr1:170501385A>C
 *</P>
--- Annotation error in sMrna.
@Test public void testSynonymousVar16() throws AnnotationException  {
	byte chr = 1;
	int pos = 170501385;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("GORAB(uc001ggz.4:exon1:c.96A>C:p.G32G,uc001gha.2:exon1:c.96A>C:p.G32G,uc009wvw.2:exon1:c.96A>C:p.G32G)",annot);
	}
}
 */

/**
 *<P>
 * annovar: DNM3:uc001gih.1:exon2:c.291A>G:p.T97T,DNM3:uc001gif.3:exon18:c.2211A>G:p.T737T,DNM3:uc001gie.3:exon19:c.2223A>G:p.T741T,
 * chr1:172356437A>G
 *</P>
 */
@Test public void testSynonymousVar17() throws AnnotationException  {
	byte chr = 1;
	int pos = 172356437;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DNM3(uc001gih.1:exon2:c.291A>G:p.T97T,uc001gif.3:exon18:c.2211A>G:p.T737T,uc001gie.3:exon19:c.2223A>G:p.T741T)",annot);
	}
}


/**
 *<P>
 * annovar: LAMC1:uc001gpy.4:exon25:c.4128T>C:p.R1376R,
 * chr1:183105534T>C
 *</P>
 */
@Test public void testSynonymousVar19() throws AnnotationException  {
	byte chr = 1;
	int pos = 183105534;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LAMC1(uc001gpy.4:exon25:c.4128T>C:p.R1376R)",annot);
	}
}

/**
 *<P>
 * annovar: RNPEP:uc001gxd.3:exon6:c.1143G>A:p.Q381Q,RNPEP:uc001gxe.3:exon5:c.246G>A:p.Q82Q,
 * chr1:201969082G>A
 *</P>
 */
@Test public void testSynonymousVar20() throws AnnotationException  {
	byte chr = 1;
	int pos = 201969082;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RNPEP(uc001gxe.3:exon5:c.246G>A:p.Q82Q,uc001gxd.3:exon6:c.1143G>A:p.Q381Q)",annot);
	}
}

/**
 *<P>
 * annovar: HHIPL2:uc001hnh.1:exon1:c.99G>A:p.L33L,
 * chr1:222721288C>T
 *</P>
 */
@Test public void testSynonymousVar22() throws AnnotationException  {
	byte chr = 1;
	int pos = 222721288;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HHIPL2(uc001hnh.1:exon1:c.99G>A:p.L33L)",annot);
	}
}

/**
 *<P>
 * annovar: OBSCN:uc001hsn.3:exon4:c.1431A>G:p.L477L,OBSCN:uc009xez.1:exon4:c.1431A>G:p.L477L,
 * chr1:228402047A>G
  expected:<OBSCN(uc00[9xez.1:exon4:c.1431A>G:p.L477L,uc001hsn.3]:exon4:c.1431A>G:p.L...> 
but was:<OBSCN(uc00[1hsn.3:exon4:c.1431A>G:p.L477L,uc009xez.1]:exon4:c.1431A>G:p.L...>

 *</P>
 */
@Test public void testSynonymousVar23() throws AnnotationException  {
	byte chr = 1;
	int pos = 228402047;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OBSCN(uc001hsn.3:exon4:c.1431A>G:p.L477L,uc009xez.1:exon4:c.1431A>G:p.L477L)",annot);
	}
}

/**
 *<P>
 * annovar: AGR2:uc003str.3:exon7:c.441T>C:p.N147N,
 * chr7:16834597A>G
 *</P>
 */
@Test public void testSynonymousVar120() throws AnnotationException  {
	byte chr = 7;
	int pos = 16834597;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AGR2(uc003str.3:exon7:c.441T>C:p.N147N)",annot);
	}
}


/**
 *<P>
 * annovar: HDAC9:uc003suh.3:exon23:c.3030C>T:p.F1010F,
 * chr7:18993870C>T
 *</P>
 */
@Test public void testSynonymousVar121() throws AnnotationException  {
	byte chr = 7;
	int pos = 18993870;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HDAC9(uc003suh.3:exon23:c.3030C>T:p.F1010F)",annot);
	}
}


/**
 *<P>
 * annovar: ZNF680:uc003tta.2:exon4:c.1569T>C:p.C523C,
 * chr7:63981563A>G
 *</P>
 */
@Test public void testSynonymousVar124() throws AnnotationException  {
	byte chr = 7;
	int pos = 63981563;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF680(uc003tta.2:exon4:c.1569T>C:p.C523C)",annot);
	}
}


/**
 *<P>
 * annovar: KPNA7:uc010lft.2:exon7:c.936G>A:p.T312T,
 * chr7:98782750C>T
 *</P>
 */
@Test public void testSynonymousVar125() throws AnnotationException  {
	byte chr = 7;
	int pos = 98782750;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KPNA7(uc010lft.2:exon7:c.936G>A:p.T312T)",annot);
	}
}


/**
 *<P>
 * annovar: MUC17:uc003uxp.1:exon3:c.9135T>C:p.S3045S,
 * chr7:100683832T>C
 *</P>
 */
@Test public void testSynonymousVar126() throws AnnotationException  {
	byte chr = 7;
	int pos = 100683832;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MUC17(uc003uxp.1:exon3:c.9135T>C:p.S3045S)",annot);
	}
}

/**
 *<P>
 * annovar: DGKI:uc003vtt.3:exon29:c.2778A>G:p.E926E,DGKI:uc003vtu.3:exon28:c.1785A>G:p.E595E,
 * chr7:137128830T>C
 *</P>
 */
@Test public void testSynonymousVar130() throws AnnotationException  {
	byte chr = 7;
	int pos = 137128830;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DGKI(uc003vtu.3:exon28:c.1785A>G:p.E595E,uc003vtt.3:exon29:c.2778A>G:p.E926E)",annot);
	}
}

/**
 *<P>
 * annovar: CCDC94:uc002lzv.4:exon3:c.171T>C:p.N57N,
 * chr19:4251069T>C
 *</P>
 */
@Test public void testSynonymousVar288() throws AnnotationException  {
	byte chr = 19;
	int pos = 4251069;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CCDC94(uc002lzv.4:exon3:c.171T>C:p.N57N)",annot);
	}
}

/**
 *<P>
 * annovar: MUC16:uc002mkp.3:exon1:c.1284A>G:p.E428E,
 * chr19:9090531T>C
 *</P>
 */
@Test public void testSynonymousVar290() throws AnnotationException  {
	byte chr = 19;
	int pos = 9090531;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MUC16(uc002mkp.3:exon1:c.1284A>G:p.E428E)",annot);
	}
}
/**
 *<P>
 * annovar: MYH9:uc003apg.3:exon26:c.3429T>G:p.A1143A,
 * chr22:36691607A>C
 *</P>
 */
@Test public void testSynonymousVar329() throws AnnotationException  {
	byte chr = 22;
	int pos = 36691607;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SYNONYMOUS,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MYH9(uc003apg.3:exon26:c.3429T>G:p.A1143A)",annot);
	}
}

}