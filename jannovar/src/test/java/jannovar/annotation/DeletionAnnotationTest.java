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
public class DeletionAnnotationTest implements Constants {

    private static HashMap<Byte,Chromosome> chromosomeMap = null;

  
    @SuppressWarnings (value="unchecked")
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
 * annovar: MST1P9:uc010ock.2:exon2:c.117_121del:p.39_41del,
 * chr1:17087544GCTGT>-
 *</P>
 */
@Test public void testFSDeletionVar2() throws AnnotationException  {
	byte chr = 1;
	int pos = 17087544;
	String ref = "GCTGT";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MST1P9(uc010ock.2:exon2:c.117_121del:p.39_41del)",annot);
	}
}

/**
 *<P>
 * annovar: OR14A16:uc001idm.1:exon1:c.486_488del:p.162_163del,
 * chr1:247978544GAG>-
 *</P>
 */
@Test public void testFSDeletionVar4() throws AnnotationException  {
	byte chr = 1;
	int pos = 247978544;
	String ref = "GAG";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR14A16(uc001idm.1:exon1:c.486_488del:p.162_163del)",annot);
	}
}



/**
 *<P>
 * annovar: SRGAP2:uc009xbt.3:exon7:c.816delT:p.I272fs,SRGAP2:uc001hdy.3:exon7:c.1047delT:p.I349fs,SRGAP2:uc010prv.1:exon6:c.819delT:p.I273fs,SRGAP2:uc010pru.2:exon7:c.1044delT:p.I348fs,SRGAP2:uc001hdx.3:exon7:c.1047delT:p.I349fs,SRGAP2:uc010prt.1:exon7:c.816delT:p.I272fs,
 * chr1:206579885T>-
 *</P>
 --- Works in principle, some of the UCSC transcripts have divergent nucleotides.
@Test public void testFSDeletionVar3() throws AnnotationException  {
	byte chr = 1;
	int pos = 206579885;
	String ref = "T";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    byte varType = ann.getVarType();
	    Assert.assertEquals(FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SRGAP2(uc010prt.1:exon7:c.816delT:p.I272fs,uc009xbt.3:exon7:c.816delT:p.I272fs,uc010prv.1:exon6:c.819delT:p.I273fs,uc010pru.2:exon7:c.1044delT:p.I348fs,uc001hdy.3:exon7:c.1047delT:p.I349fs,uc001hdx.3:exon7:c.1047delT:p.I349fs)",annot);
	}
}
*/

/**
 *<P>
 * annovar: ZNF852:uc011azx.2:exon4:c.1472_1473del:p.491_491del,
 * chr3:44540796TC>-
 *</P>
 */
@Test public void testFSDeletionVar6() throws AnnotationException  {
	byte chr = 3;
	int pos = 44540796;
	String ref = "TC";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF852(uc011azx.2:exon4:c.1472_1473del:p.491_491del)",annot);
	}
}


/**
 *<P>
 * annovar: OR5H6:uc003dsi.1:exon1:c.369_377del:p.123_126del,
 * chr3:97983497TGTAACCAC>-
 *</P>
 */
@Test public void testFSDeletionVar8() throws AnnotationException  {
	byte chr = 3;
	int pos = 97983497;
	String ref = "TGTAACCAC";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR5H6(uc003dsi.1:exon1:c.369_377del:p.123_126del)",annot);
	}
}



/**
 *<P>
 * annovar: OR5K2:uc011bgx.2:exon1:c.275_285del:p.92_95del,
 * chr3:98216799TTTCCCTCTAT>-
 *</P>
 */
@Test public void testFSDeletionVar9() throws AnnotationException  {
	byte chr = 3;
	int pos = 98216799;
	String ref = "TTTCCCTCTAT";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR5K2(uc011bgx.2:exon1:c.275_285del:p.92_95del)",annot);
	}
}



/**
 *<P>
 * annovar: PCDHA7:uc003lhq.2:exon1:c.1503_1507del:p.501_503del,PCDHA7:uc011dac.2:exon1:c.1503_1507del:p.501_503del,
 * chr5:140215471GCGCG>-
 *</P>
 */
@Test public void testFSDeletionVar10() throws AnnotationException  {
	byte chr = 5;
	int pos = 140215471;
	String ref = "GCGCG";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PCDHA7(uc003lhq.2:exon1:c.1503_1507del:p.501_503del,uc011dac.2:exon1:c.1503_1507del:p.501_503del)",annot);
	}
}



/**
 *<P>
 * annovar: PCDHB18:uc003ljc.1:exon1:c.1219_1221del:p.407_407del,
 * chr5:140615504GTC>-
 *</P>
 */
@Test public void testFSDeletionVar11() throws AnnotationException  {
	byte chr = 5;
	int pos = 140615504;
	String ref = "GTC";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PCDHB18(uc003ljc.1:exon1:c.1219_1221del:p.407_407del)",annot);
	}
}



/**
 *<P>
 * annovar: OR2B2:uc011dkw.2:exon1:c.985delA:p.T329fs,
 * chr6:27879113T>-
 *</P>
 */
@Test public void testFSDeletionVar12() throws AnnotationException  {
	byte chr = 6;
	int pos = 27879113;
	String ref = "T";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR2B2(uc011dkw.2:exon1:c.985delA:p.T329fs)",annot);
	}
}



/**
 *<P>
 * annovar: KCNK17:uc003ooo.3:exon2:c.318_320del:p.106_107del,KCNK17:uc003oop.3:exon2:c.318_320del:p.106_107del,
 * chr6:39278701AAG>-
 *</P>
 */
@Test public void testFSDeletionVar13() throws AnnotationException  {
	byte chr = 6;
	int pos = 39278701;
	String ref = "AAG";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KCNK17(uc003ooo.3:exon2:c.318_320del:p.106_107del,uc003oop.3:exon2:c.318_320del:p.106_107del)",annot);
	}
}


/**
 *<P>
 * annovar: KIAA2026:uc010mht.3:exon4:c.1539_1541del:p.513_514del,KIAA2026:uc003zjq.4:exon8:c.4014_4016del:p.1338_1339del,
 * chr9:5921980GTT>-
 *</P>
 */
@Test public void testFSDeletionVar14() throws AnnotationException  {
	byte chr = 9;
	int pos = 5921980;
	String ref = "GTT";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KIAA2026(uc010mht.3:exon4:c.1539_1541del:p.513_514del,uc003zjq.4:exon8:c.4014_4016del:p.1338_1339del)",annot);
	}
}

/**
 *<P>
 * annovar: AGAP6:uc001jix.4:exon8:c.791_792del:p.264_264del,
 * chr10:51768676AA>-
 *</P>
 */
@Test public void testFSDeletionVar16() throws AnnotationException  {
	byte chr = 10;
	int pos = 51768676;
	String ref = "AA";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AGAP6(uc001jix.4:exon8:c.791_792del:p.264_264del)",annot);
	}
}

/**
 *<P>
 * annovar: AGAP6:uc001jix.4:exon8:c.890_892del:p.297_298del,
 * chr10:51768775TGA>-
 *</P>
 */
@Test public void testFSDeletionVar17() throws AnnotationException  {
	byte chr = 10;
	int pos = 51768775;
	String ref = "TGA";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AGAP6(uc001jix.4:exon8:c.890_892del:p.297_298del)",annot);
	}
}

/**
 *<P>
 * annovar: OR5M1:uc001nja.1:exon1:c.423_426del:p.141_142del,
 * chr11:56380553GACA>-
 *</P>
 */
@Test public void testFSDeletionVar18() throws AnnotationException  {
	byte chr = 11;
	int pos = 56380553;
	String ref = "GACA";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR5M1(uc001nja.1:exon1:c.423_426del:p.141_142del)",annot);
	}
}


/**
 *<P>
 * annovar: FAM90A1:uc001quh.2:exon5:c.376delC:p.P126fs,FAM90A1:uc001qui.2:exon6:c.376delC:p.P126fs,
 * chr12:8376101G>-
 *</P>
 */
@Test public void testFSDeletionVar19() throws AnnotationException  {
	byte chr = 12;
	int pos = 8376101;
	String ref = "G";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FAM90A1(uc001qui.2:exon6:c.376delC:p.P126fs,uc001quh.2:exon5:c.376delC:p.P126fs)",annot);
	}
}

/**
 *<P>
 * annovar: SETD8:uc001uew.3:exon5:c.542_543del:p.181_181del,
 * chr12:123880924TT>-
 *</P>
 */
@Test public void testFSDeletionVar21() throws AnnotationException  {
	byte chr = 12;
	int pos = 123880924;
	String ref = "TT";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SETD8(uc001uew.3:exon5:c.542_543del:p.181_181del)",annot);
	}
}

/**
 *<P>
 * annovar: FAM194B:uc001vam.1:exon2:c.398_415del:p.133_139del,FAM194B:uc001val.2:exon3:c.398_415del:p.133_139del,
 * chr13:46170726ACTCTTCCTCCTCCAGAT>-
 * expected:<FAM194B(uc001va[m.1:exon2:c.398_415del:p.133_139del,uc001val.2:exon3]:c.398_415del:p.133_...> 
 * but was:<FAM194B(uc001va[l.2:exon3:c.398_415del:p.133_139del,uc001vam.1:exon2]:c.398_415del:p.133_...>
 * (order changed)
 *</P>
 */
@Test public void testFSDeletionVar22() throws AnnotationException  {
	byte chr = 13;
	int pos = 46170726;
	String ref = "ACTCTTCCTCCTCCAGAT";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FAM194B(uc001val.2:exon3:c.398_415del:p.133_139del,uc001vam.1:exon2:c.398_415del:p.133_139del)",annot);
	}
}


/**
 *<P>
 * annovar: CCDC33:uc002axo.3:exon2:c.100_102del:p.34_34del,
 * chr15:74536404AAG>-
 *</P>
 */
@Test public void testFSDeletionVar23() throws AnnotationException  {
	byte chr = 15;
	int pos = 74536404;
	String ref = "AAG";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CCDC33(uc002axo.3:exon2:c.100_102del:p.34_34del)",annot);
	}
}


/**
 *<P>
 * annovar: CCDC33:uc002axo.3:exon2:c.100_102del:p.34_34del,
 * chr15:74536404AAG>-
 *</P>
 */
@Test public void testFSDeletionVar24() throws AnnotationException  {
	byte chr = 15;
	int pos = 74536404;
	String ref = "AAG";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CCDC33(uc002axo.3:exon2:c.100_102del:p.34_34del)",annot);
	}
}

/**
 *<P>
 * annovar: LOC645752:uc010bky.2:exon14:c.832_834del:p.278_278del,
 * chr15:78208899CTC>-
 *</P>
 */
@Test public void testFSDeletionVar25() throws AnnotationException  {
	byte chr = 15;
	int pos = 78208899;
	String ref = "CTC";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC645752(uc010bky.2:exon14:c.832_834del:p.278_278del)",annot);
	}
}


/**
 *<P>
 * annovar: SENP3:uc002ghm.3:exon8:c.1307delA:p.K436fs,
 * chr17:7470288A>-
 *</P>
 */
@Test public void testFSDeletionVar26() throws AnnotationException  {
	byte chr = 17;
	int pos = 7470288;
	String ref = "A";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SENP3(uc002ghm.3:exon8:c.1307delA:p.K436fs)",annot);
	}
}


/**
 *<P>
 * annovar: ATAD5:uc002hfs.1:exon2:c.861_866del:p.287_289del,ATAD5:uc002hft.1:exon1:c.552_557del:p.184_186del,
 * chr17:29161960GTCAAT>-
 *</P>
 */
@Test public void testFSDeletionVar28() throws AnnotationException  {
	byte chr = 17;
	int pos = 29161960;
	String ref = "GTCAAT";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ATAD5(uc002hft.1:exon1:c.552_557del:p.184_186del,uc002hfs.1:exon2:c.861_866del:p.287_289del)",annot);
	}
}

/**
 *<P>
 * annovar: DCAF7:uc002jbc.3:exon6:c.560delG:p.G187fs,
 * chr17:61660895G>-
 *</P>
 */
@Test public void testFSDeletionVar29() throws AnnotationException  {
	byte chr = 17;
	int pos = 61660895;
	String ref = "G";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DCAF7(uc002jbc.3:exon6:c.560delG:p.G187fs)",annot);
	}
}

/**
 *<P>
 * annovar: DEFB126:uc002wcx.3:exon2:c.317_318del:p.106_106del,
 * chr20:126314CC>-
 *</P>
 */
@Test public void testFSDeletionVar32() throws AnnotationException  {
	byte chr = 20;
	int pos = 126314;
	String ref = "CC";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DEFB126(uc002wcx.3:exon2:c.317_318del:p.106_106del)",annot);
	}
}


/**
 *<P>
 * annovar: PLAC4:uc002yyz.3:exon1:c.70_88del:p.24_30del,
 * chr21:42551468GTGTCAGGGTGAGTGAGGG>-
 *</P>
 */
@Test public void testFSDeletionVar33() throws AnnotationException  {
	byte chr = 21;
	int pos = 42551468;
	String ref = "GTGTCAGGGTGAGTGAGGG";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PLAC4(uc002yyz.3:exon1:c.70_88del:p.24_30del)",annot);
	}
}


/**
 *<P>
 * annovar: ZNF135:uc010yhq.2:exon5:c.1992_1997del:p.664_666del,ZNF135:uc002qre.3:exon5:c.1956_1961del:p.652_654del,ZNF135:uc002qrg.3:exon4:c.2028_2033del:p.676_678del,ZNF135:uc002qrd.2:exon5:c.1152_1157del:p.384_386del,ZNF135:uc010yhr.2:exon3:c.1419_1424del:p.473_475del,ZNF135:uc002qrf.3:exon5:c.1830_1835del:p.610_612del,
 * chr19:58579808CCAGAG>-
 *</P>
 */
@Test public void testFSDeletionVar31() throws AnnotationException  {
	byte chr = 19;
	int pos = 58579808;
	String ref = "CCAGAG";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.NON_FS_DELETION,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF135(uc002qrd.2:exon5:c.1152_1157del:p.384_386del,uc010yhr.2:exon3:c.1419_1424del:p.473_475del,uc002qrf.3:exon5:c.1830_1835del:p.610_612del,uc002qre.3:exon5:c.1956_1961del:p.652_654del,uc010yhq.2:exon5:c.1992_1997del:p.664_666del,uc002qrg.3:exon4:c.2028_2033del:p.676_678del)",annot);
	}
}



}