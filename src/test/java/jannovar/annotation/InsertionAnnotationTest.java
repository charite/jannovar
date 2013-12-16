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
import jannovar.annotation.Annotation;
import jannovar.annotation.AnnotationList;
import jannovar.common.Constants;
import jannovar.common.VariantType;
import jannovar.exome.Variant;
import jannovar.exception.AnnotationException;
import jannovar.io.UCSCKGParser;
import jannovar.reference.Chromosome;
import jannovar.reference.TranscriptModel;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;


/**
 * This class is intended to perform unuit testing on variants that
 * are intergenic. 
 */
public class InsertionAnnotationTest implements Constants {

    
    private static HashMap<Byte,Chromosome> chromosomeMap = null;

  

    
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
 * annovar: FAM178B:uc002sxl.4:exon13:c.1579_1580insCGAT:p.L527fs,FAM178B:uc002sxk.4:exon7:c.628_629insCGAT:p.L210fs,
 * chr2:97568428->ATCG
 *</P>
 */
@Test public void testInsertionVar11() throws AnnotationException  {
	byte chr = 2;
	int pos = 97568428;
	String ref = "-";
	String alt = "ATCG";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FAM178B(uc002sxk.4:exon7:c.627_628insCGAT:p.L210fs,uc002sxl.4:exon13:c.1578_1579insCGAT:p.L527fs)",annot);
	}
}


/**
 *<P>
 * annovar: RANBP2:uc002tem.4:exon16:c.2265_2266insCC:p.D755fs,
 * chr2:109371423->CC
 *</P>
 */
@Test public void testInsertionVar12() throws AnnotationException  {
	byte chr = 2;
	int pos = 109371423;
	String ref = "-";
	String alt = "CC";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
//	    Assert.assertEquals("RANBP2(uc002tem.4:exon16:c.2265_2266insCC:p.D756fs)",annot);
	    Assert.assertEquals("RANBP2(uc002tem.4:exon16:c.2265_2266insCC:p.D756fs)",annot);
	}
}


/**
 *<P>
 * annovar: FBXL21
 * chr5:135272375->A
 *</P>
 */
@Test public void testNcRnaExonicVar152() throws AnnotationException  {
	byte chr = 5;
	int pos = 135272376;
	String ref = "-";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_DUPLICATION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FBXL21(uc031sld.1:exon5:c.92dupA:p.N31fs)",annot);
	}
}


/**
 *<P>
 * annovar: RANBP2:uc002tem.4:exon20:c.6318_6319insAGCG:p.M2106fs,
 * chr2:109383313->AGCG
 *</P>
 */
@Test public void testInsertionVar13() throws AnnotationException  {
	byte chr = 2;
	int pos = 109383313;
	String ref = "-";
	String alt = "AGCG";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RANBP2(uc002tem.4:exon20:c.6318_6319insAGCG:p.M2107fs)",annot);
	}
}



/**
 *<P>
 * annovar: RANBP2:uc002tem.4:exon20:c.6882_6883insCAT:p.D2294delinsDH,
 * chr2:109383877->CAT
 *</P>
 */
@Test public void testInsertionVar14() throws AnnotationException  {
	byte chr = 2;
	int pos = 109383877;
	String ref = "-";
	String alt = "CAT";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RANBP2(uc002tem.4:exon20:c.6882_6883insCAT:p.D2295delinsDH)",annot);
	}
}


/**
 *<P>
 * annovar: TTN:uc002umz.1:exon112:c.21594_21595insACTT:p.K7198fs,
 * chr2:179519685->AAGT
 *</P>
 */
@Test public void testInsertionVar17() throws AnnotationException  {
	byte chr = 2;
	int pos = 179519685;
	String ref = "-";
	String alt = "AAGT";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TTN(uc002umz.1:exon112:c.21593_21594insACTT:p.K7198fs,uc031rqc.1:exon190:c.38075_38076insACTT:p.K12692fs)",annot);
	}
}

/**
 *<P>
 * annovar: CPS1:uc010fur.3:exon2:c.15_16insTTC:p.I5delinsIF,
 * chr2:211421454->TTC
 *</P>
 */
@Test public void testInsertionVar18() throws AnnotationException  {
	byte chr = 2;
	int pos = 211421454;
	String ref = "-";
	String alt = "TTC";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CPS1(uc010fur.3:exon2:c.15_16insTTC:p.I6delinsIF)",annot);
	}
}


/**
 *<P>
 * annovar: MAGEF1:uc003fpa.3:exon1:c.456_457insGGA:p.L152delinsLE,
 * chr3:184429154->TCC
 *</P>
 */
@Test public void testInsertionVar25() throws AnnotationException  {
	byte chr = 3;
	int pos = 184429154;
	String ref = "-";
	String alt = "TCC";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.455_456insGGA:p.L152delinsLE)",annot);
	}
}


/**
 *<P>
 * annovar: MUC4:uc021xjp.1:exon2:c.8108_8109insTG:p.T2703fs,
 * chr3:195510343->CA
 *</P>
 */
@Test public void testInsertionVar26() throws AnnotationException  {
	byte chr = 3;
	int pos = 195510343;
	String ref = "-";
	String alt = "CA";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MUC4(uc021xjp.1:exon2:c.8107_8108insTG:p.T2703fs)",annot);
	}
}


/**
 *<P>
 * annovar: MUC4:uc021xjp.1:exon2:c.6858_6859insCAG:p.T2286delinsTS,
 * chr3:195511593->CTG
 *</P>
 */
@Test public void testInsertionVar27() throws AnnotationException  {
	byte chr = 3;
	int pos = 195511593;
	String ref = "-";
	String alt = "CTG";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MUC4(uc021xjp.1:exon2:c.6857_6858insCAG:p.T2286delinsTS)",annot);
	}
}


/**
 *<P>
 * annovar: FRG1:uc003izs.3:exon6:c.439_440insA:p.M147fs,
 * chr4:190878559->A
 *</P>
 */
@Test public void testInsertionVar29() throws AnnotationException  {
	byte chr = 4;
	int pos = 190878559;
	String ref = "-";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_DUPLICATION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FRG1(uc003izs.3:exon6:c.439dupA:p.M147fs)",annot);
	}
}

/**
 *<P>
 * annovar: FRG1:uc003izs.3:exon7:c.608_609insGACT:p.K203fs,
 * chr4:190881973->GACT
 *</P>
 */
@Test public void testInsertionVar30() throws AnnotationException  {
	byte chr = 4;
	int pos = 190881973;
	String ref = "-";
	String alt = "GACT";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FRG1(uc003izs.3:exon7:c.608_609insGACT:p.K203fs)",annot);
	}
}




/**
 *<P>
 * annovar: PRDM9:uc003jgo.3:exon11:c.1147_1148insTGA:p.P383delinsLT,
 * chr5:23526344->TGA
 *</P>
 */
@Test public void testInsertionVar31() throws AnnotationException  {
	byte chr = 5;
	int pos = 23526344;
	String ref = "-";
	String alt = "TGA";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PRDM9(uc003jgo.3:exon11:c.1147_1148insTGA:p.P383delinsLT)",annot);
	}
}



/**
 *<P>
 * annovar: SCAMP1:uc003kfl.3:exon8:c.730_731insT:p.C244fs,
 * chr5:77745856->T
 *</P>
-- According to mutalyzer, p.(Asn244Ilefs*52), thus should be p.N244fs (this is what
jannovar says, annovar finds a "C")
 */
@Test public void testInsertionVar32() throws AnnotationException  {
	byte chr = 5;
	int pos = 77745856;
	String ref = "-";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SCAMP1(uc003kfl.3:exon8:c.730_731insT:p.N244fs)",annot);
	}
}

/**
 *<P>
 * annovar: PCDHB10:uc003lix.3:exon1:c.1806_1807insATGC:p.L602fs,
 * chr5:140573931->ATGC
 *</P>
 */
@Test public void testInsertionVar34() throws AnnotationException  {
	byte chr = 5;
	int pos = 140573931;
	String ref = "-";
	String alt = "ATGC";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PCDHB10(uc003lix.3:exon1:c.1806_1807insATGC:p.L603fs)",annot);
	}
}

/**
 *<P>
 * annovar: AK098012:uc003nrp.1:exon2:c.254_255insCAAA:p.P85fs,
 * chr6:30782220->TTTG
 *</P>
 */
@Test public void testInsertionVar37() throws AnnotationException  {
	byte chr = 6;
	int pos = 30782220;
	String ref = "-";
	String alt = "TTTG";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AK098012(uc003nrp.1:exon2:c.253_254insCAAA:p.P85fs)",annot);
	}
}

/**
 *<P>
 * annovar: PRICKLE4:uc011duf.1:exon8:c.863_864insTCT:p.L288delinsLL,
 * chr6:41754575->TCT
 *</P>
 */
@Test public void testInsertionVar40() throws AnnotationException  {
	byte chr = 6;
	int pos = 41754575;
	String ref = "-";
	String alt = "TCT";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PRICKLE4(uc011duf.1:exon8:c.863_864insTCT:p.L288delinsLL)",annot);
	}
}

/**
 *<P>
 * annovar: AEBP1:uc003tkb.3:exon1:c.118_119insAAAA:p.G40fs,
 * chr7:44144382->AAAA
 *</P>
 */
@Test public void testInsertionVar43() throws AnnotationException  {
	byte chr = 7;
	int pos = 44144382;
	String ref = "-";
	String alt = "AAAA";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AEBP1(uc003tkb.4:exon1:c.118_119insAAAA:p.G40fs)",annot);
	}
}
/**
 *<P>
 * annovar: MUC12:uc003uxo.3:exon2:c.3442_3443insGTA:p.T1148delinsST,
 * chr7:100637286->GTA
 *</P>
 */
@Test public void testInsertionVar44() throws AnnotationException  {
	byte chr = 7;
	int pos = 100637286;
	String ref = "-";
	String alt = "GTA";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_INSERTION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MUC12(uc003uxo.3:exon2:c.3442_3443insGTA:p.T1148delinsST)",annot);
	}
}
/**
 *<P>
 * annovar: OLFM1:uc010naq.2:exon2:c.328_329insAA:p.G110fs,
 * chr9:137968919->AA
 *</P>
 */
@Test public void testInsertionVar48() throws AnnotationException  {
    byte chr = 9;
    int pos = 137968919;
    String ref = "-";
    String alt = "AA";
    Chromosome c = chromosomeMap.get(chr); 
    if (c==null) {
	Assert.fail("Could not identify chromosome \"" + chr + "\"");
    } else {
	AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	VariantType varType = ann.getVariantType();
	Assert.assertEquals(VariantType.FS_INSERTION,varType);
	String annot = ann.getVariantAnnotation();
	Assert.assertEquals("OLFM1(uc010naq.2:exon2:c.328_329insAA:p.G110fs)",annot);
    }
}


/**
 *<P>

g19:chr17:g.37830925_37830926insG. This is reported as
NM_033419.3:c.440_441insC in Annovar. Correct nomenclature in HGVS
should be: NM_033419.3:c.441dup, NM_033419.3:p.(Asn148Glnfs*15).

However, I think there is also a position bug. Correct cDNA code
should be:NM_033419.3:c.439dup,
p.(Leu147Profs*16)
 *</P>
 */
@Test public void testInsertionVar49() throws AnnotationException  {
    byte chr = 17;
    int pos = 37830925;
    String ref = "-";
    String alt = "G";
    Chromosome c = chromosomeMap.get(chr); 
    if (c==null) {
	Assert.fail("Could not identify chromosome \"" + chr + "\"");
    } else {
	AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	VariantType varType = ann.getVariantType();
	Assert.assertEquals(VariantType.FS_DUPLICATION,varType);
	String annot = ann.getVariantAnnotation();
	Assert.assertEquals("PGAP3(uc002hsk.3:exon3:c.286dupC:p.L96fs,uc002hsj.3:exon4:c.439dupC:p.L147fs)",annot);
    }
}


/**
 *<P>

This duplication variation should lead to the loss of the translation initiation site 
 *</P>
 */
@Test public void testInsertionVar50() throws AnnotationException  {
    byte chr = 4;
    int pos = 190862165;
    String ref = "-";
    String alt = "A";
    Chromosome c = chromosomeMap.get(chr); 
    if (c==null) {
	Assert.fail("Could not identify chromosome \"" + chr + "\"");
    } else {
	AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	VariantType varType = ann.getVariantType();
	Assert.assertEquals(VariantType.START_LOSS,varType);
	String annot = ann.getVariantAnnotation();
	Assert.assertEquals("FRG1(uc003izs.3:exon1:c.1dupA:p.M1?)",annot);
    }
}
/**
 *<P>

This duplication variation should lead to the loss of the translation initiation site 
 *</P>
 */
@Test public void testInsertionVar51() throws AnnotationException  {
    byte chr = 4;
    int pos = 190862166;
    String ref = "-";
    String alt = "A";
    Chromosome c = chromosomeMap.get(chr); 
    if (c==null) {
	Assert.fail("Could not identify chromosome \"" + chr + "\"");
    } else {
	AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	VariantType varType = ann.getVariantType();
	Assert.assertEquals(VariantType.START_LOSS,varType);
	String annot = ann.getVariantAnnotation();
	Assert.assertEquals("FRG1(uc003izs.3:exon1:c.2insA:p.M1?)",annot);
    }
}
    
}