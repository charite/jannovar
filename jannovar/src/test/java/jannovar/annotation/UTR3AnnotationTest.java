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
	    Assert.assertEquals("CD24(uc004ftz.1:c.*30G>A)",annot);
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
	    Assert.assertEquals("THAP3(uc001aoc.3:c.*28->TA,uc001aod.3:c.*28->TA)",annot);
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
	    Assert.assertEquals("MEAF6(uc001cbd.2:c.*250T>C,uc001cbe.2:c.*250T>C,uc001cbg.2:c.*250T>C)",annot);
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
	    Assert.assertEquals("ANGPTL3(uc001das.2:c.*52TAATGTGGT>-)",annot);
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
	    Assert.assertEquals("GBP7(uc001dna.2:c.*77T>C)",annot);
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
	    Assert.assertEquals("FCGR2B(uc009wum.2:c.*21A>G)",annot);
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
	    Assert.assertEquals("LRRC52(uc001gde.2:c.*14C>T)",annot);
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
	    Assert.assertEquals("XCL1(uc001gfo.2:c.*77A>G)",annot);
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
	    Assert.assertEquals("RGS21(uc001gsh.3:c.*20->CTAA)",annot);
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
	    Assert.assertEquals("RGS21(uc001gsh.3:c.*21->TAAT)",annot);
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
	    Assert.assertEquals("LMOD1(uc010ppu.2:c.*1643A>G,uc021phl.1:c.*1643A>G,uc021phm.1:c.*1737A>G)",annot);
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
	    Assert.assertEquals("GPBAR1(uc010zjx.1:c.*66C>T,uc010zjy.1:c.*66C>T,uc010zjw.1:c.*66C>T)",annot);
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
	    Assert.assertEquals("GLT8D1(uc003dfl.3:c.*57C>T,uc003dfm.3:c.*57C>T,uc003dfn.3:c.*57C>T,uc003dfk.3:c.*57C>T,uc003dfi.4:c.*57C>T)",annot);
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
	    Assert.assertEquals("TPRG1(uc003frw.2:c.*39T>C,uc003frv.2:c.*39T>C)",annot);
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
	    Assert.assertEquals("AK308309(uc0101my.1:c.*99CAAGAA>-)",annot);
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
	    Assert.assertEquals("FRG1(uc003izs.3:c.*5->GACA)",annot);
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
	    Assert.assertEquals("SLC6A3(uc003jck.3:c.*1089->GG)",annot);
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
	    Assert.assertEquals("ATP10B(uc003lyn.3:c.*52T>C)",annot);
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
	    Assert.assertEquals("MICB(uc031snm.1:c.*85GA>-,uc011dnm.2:c.*85GA>-,uc003nto.4:c.*85GA>-,uc003ntn.4:c.*85GA>-)",annot);
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
	    Assert.assertEquals("PRPH2(uc003osk.3:c.*13G>A)",annot);
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
	    Assert.assertEquals("ULBP3(uc011eej.1:c.*13T>G,uc003qns.3:c.*13T>G)",annot);
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
	    Assert.assertEquals("CDCA7L(uc003sve.4:c.*74TCTT>-,uc003svf.4c.*74TCTT>-,uc010kuk.3c.*74TCTT>-,uc010kul.3c.*74TCTT>-)",annot);
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
	    Assert.assertEquals("WBSCR22(uc003tyw.1:c.*337T>C)",annot);
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
	    Assert.assertEquals("HGF(uc003uho.1:c.*102A>G,uc003uhn.1:c.*102A>G)",annot);
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
	    Assert.assertEquals("AKR1D1(uc010lmy.1,uc003vtz.3:c.*5A>C,uc011kqf.2:c.*5A>C,uc011kqe.1:c.*30A>C)",annot);
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
	    Assert.assertEquals("TMEM70(uc022awa.1,uc003yac.3c.*497C>T,uc003yab.3:c.*24C>T)",annot);
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
	    Assert.assertEquals("SPATA31D3(uc010mpt.2:c.*573->CTAC)",annot);
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
	    Assert.assertEquals("ROR2(uc004ari.1:c.*1G>C)",annot);
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
	    Assert.assertEquals("DDX31(uc004cbq.1:c.*77C>G,uc010mzu.1:c.*77C>G)",annot);
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
	    Assert.assertEquals("WDFY4(uc001jha.4:c.*179C>T)",annot);
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
	    Assert.assertEquals("AGAP11(uc001kee.2:c.*16->TGC,uc031pwm.1:c.*16->TGC)",annot);
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
	    Assert.assertEquals("WT1(uc009yjs.2,...)",annot);
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
	    Assert.assertEquals("LOC440040(uc010rhy.2:c.*317G>-,uc009ymb.3:c.*317G>-)",annot);
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
	    Assert.assertEquals("SLC22A20(uc021qlg.1:c.*30->TAAG)",annot);
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
	    Assert.assertEquals("SLC22A20(uc021qlg.1:c.*33->GCAA)",annot);
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
	    Assert.assertEquals("KDELC2(uc001pkj.2:c.*39->A,uc001pki.2:c.*39->A)",annot);
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
	    Assert.assertEquals("DPPA3(uc001qtf.3:c.*25->CCCG)",annot);
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
	    Assert.assertEquals("STRAP(uc001rdd.4:c.*15->T,uc001rdc.4:c.*15->T,uc010shw.2:c.*15->T)",annot);
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
	    Assert.assertEquals("WIBG(uc001sie.1:c.*108TAAG>-,uc001sif.1:c.*108TAAG>-)",annot);
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
	    Assert.assertEquals("PRDM4(uc001tmq.3,uc001tmp.3:c.*22A>G)",annot);
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
	    Assert.assertEquals("POTEM(uc001vwb.3,uc001vwc.3:c.*795T>C)",annot);
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
	    Assert.assertEquals("ABHD12B(uc010any.3,uc001wys.3:c.*37C>T,uc001wyr.3:c.*37C>T,uc001wyq.3:c.*37C>T)",annot);
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
	    Assert.assertEquals("ESR2(uc001xgw.3,uc001xgy.2:c.*411C>T,uc001xgu.2:c.*56C>T,uc001xgx.2:c.*56C>T,uc001xgv.2:c.*56C>T)",annot);
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
	    Assert.assertEquals("AHNAK2(uc021seo.1:c.*16T>C,uc021sen.1:c.*16T>C,uc001ypx.2:c.*16T>C,uc010axc.1:c.*16T>C)",annot);
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
	    Assert.assertEquals("METTL22(uc021tcq.1,uc002cyz.3:c.*15A>C)",annot);
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
	    Assert.assertEquals("CMTM3(uc002epy.3,uc002epu.3:c.*51C>T,uc002epv.3:c.*51C>T,uc002epx.3:c.*51C>T)",annot);
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
	    Assert.assertEquals("RNASEK-C17ORF49(uc021tow.2,uc002gec.3:c.-474C>T,uc010vti.2:c.-474C>T,uc002ged.3:c.-474C>T,uc002gea.4:c.*98C>T)",annot);
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
	    Assert.assertEquals("DUSP3(uc021tjya.1,uc002ied.4:c.*13T>C,uc002iee.4:c.*185T>C)",annot);
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
	    Assert.assertEquals("SNX11(uc002inf.1:c.*6G>C,uc002ing.1:c.*6G>C,uc010wlj.1:c.*6G>C,uc010wlh.1:c.*6G>C,uc010wli.1:c.*6G>C,uc010wlg.1:c.*6G>C,uc002inh.1:c.*6G>C)",annot);
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
	    Assert.assertEquals("ATP5G1(uc002ioh.3:c.*15G>-,uc002iog.3:c.*15G>-)",annot);
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
	    Assert.assertEquals("EXOC7(uc002jqr.3:c.*2232G>C,uc002jqs.3:c.*2232G>C,uc002jqq.3:c.*2232G>C,uc010wsw.2:c.*2232G>C,uc010wsx.2:c.*2232G>C,uc010wsv.2:c.*2232G>C,uc010dgv.2:c.*2232G>C,uc002jqp.2:c.*2261G>C)",annot);
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
	    Assert.assertEquals("ENOSF1(uc010dke.3,uc010dkf.3:c.*1289C>T,uc010dkf.3:c.*1289C>T,uc002kku.4:c.*204C>Tuc010dkf.3:c.*1289C>T);TYMS(uc010dka.1:c.*19C>T,uc010dkb.1:c.*19C>T,uc010dkc.1:c.*19C>T)",annot);
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
	    Assert.assertEquals("SLC14A2(uc002lbe.3:c.*48C>A,uc010dnj.3:c.*48C>A)",annot);
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
	    Assert.assertEquals("SKA1(uc002let.3:c.*22A>C,uc002leu.3:c.*22A>C,uc010xdl.2:c.*22A>C)",annot);
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
	    Assert.assertEquals("FZR1(uc010dtk.2:c.*8G>A,uc002lxv.2:c.*8G>A,uc002lxt.2:c.*8G>A)",annot);
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
	    Assert.assertEquals("C19orf40(uc002nud.4:c.*32T>C)",annot);
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
	    Assert.assertEquals("LGI4(uc002nxy.1:c.*11C>T,uc002nxx.2:c.*11C>T,uc002nxz.1:c.*986C>T)",annot);
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
	    Assert.assertEquals("C19orf55(uc021usz.1:c.*47C>A)",annot);
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
	    Assert.assertEquals("MIA(uc002opb.4:c.*40C>G,uc021uuu.1:c.*40C>G)",annot);
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
	    Assert.assertEquals("PLA2G4C(uc002phx.3:c.*54A>T,uc010elr.3:c.*139A>T,uc010xzd.2:c.*54A>T,uc002phw.3:c.*54A>T)",annot);
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
	    Assert.assertEquals("ZNF160(uc002qas.4:c.*1C>G)",annot);
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
	    Assert.assertEquals("KIR2DS4(uc010yfk.1,uc002qhm.1:c.*28A>G)",annot);
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
	    Assert.assertEquals("SEL1L2(uc002wog.5:c.*29T>C,uc010zrl.3:c.*29T>C,uc002wor.4)",annot);
	}
}

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
	    Assert.assertEquals("JAM2(uc002ylq.2,uc002ylp.2:c.*61T>C,uc011acf.2:c.*61T>C)",annot);
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
	    Assert.assertEquals("TRAPPC10(uc002zea.3:c.*4C>T,uc010gpo.3:c.*4C>T,uc011afa.2:c.*4C>T)",annot);
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
	    Assert.assertEquals("C21orf33(uc002zec.4:c.*642C>T,uc002zed.4:c.*642C>T)",annot);
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
	    Assert.assertEquals("LSS(uc002zij.3:c.*194G>A)",annot);
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
	    Assert.assertEquals("GUCD1(uc003aal.2:c.*2004A>G,uc003aah.2:c.*2004A>G)",annot);
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
	    Assert.assertEquals("PVALB(uc010gwz.3:c.*63G>A,uc003apx.3:c.*63G>A)",annot);
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
	    Assert.assertEquals("MCAT(uc003bdm.1:c.*432C>T,uc003bdl.1:c.*20C>T,uc031rxv.1)",annot);
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
	    Assert.assertEquals("NUP50(uc003bfs.3:c.*38->TT,uc003bfr.3:c.*38->TT,uc011aqn.2:c.*38->TT,uc003bft.3:c.*38->TT,uc011aqo.1:c.*1123->TT)",annot);
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
	    Assert.assertEquals("NCAPH2(uc003blx.4:c.*50T>C,uc003blr.4:c.*50T>C,uc003blv.3:c.*166T>C)",annot);
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
	    Assert.assertEquals("FAM47B(uc004ddi.2:c.*23A>C)",annot);
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
	    Assert.assertEquals("CHIC1(uc004ebk.4:c.*90G>A,uc010nlo.3,uc004ebl.4:c.*90G>A,uc011mql.2:c.*157G>A,)",annot);
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
	    Assert.assertEquals("SAGE1(uc010nry.1:c.*481T>G)",annot);
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
	    Assert.assertEquals("SAGE1(uc010nry.1:c.*492G>C)",annot);
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
	    Assert.assertEquals("CD24(uc004ftz.1:c.*30G>A)",annot);
	}
}


}