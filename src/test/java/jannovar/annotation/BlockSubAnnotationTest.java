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
import jannovar.annotation.AnnotationList;
import jannovar.exome.Variant;
import jannovar.exception.AnnotationException;


import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Assert;


/**
 * This class is intended to perform unuit testing on variants that
 * are intergenic. 
 */
public class BlockSubAnnotationTest implements Constants {

    
   
    private static HashMap<Byte,Chromosome> chromosomeMap = null;

  
    /**
     * Set up test by importing Chromosome objects from serialized file.
     */
    @SuppressWarnings (value="unchecked")
    @BeforeClass 
	public static void setUp() throws IOException, JannovarException {
	ArrayList<TranscriptModel> kgList=null;
	System.out.println(UCSCserializationTestFileName);
	java.net.URL url = BlockSubAnnotationTest.class.getResource(UCSCserializationTestFileName);
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
@Test public void testBlocSubByHand1() throws AnnotationException  {
    byte chr = 16;
    int pos = 21848622;
    String ref = "CGCTGAGGGTGGAGCTGAGGGTAGAGCTGAGGGTGGA";
    String alt = "CGCTGAGGGTAGAGCTGAGGGTGGA";
    Chromosome c = chromosomeMap.get(chr); 
    if (c==null) {
	Assert.fail("Could not identify chromosome \"" + chr + "\"");
    } else {
	AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	VariantType varType = ann.getVariantType();
	Assert.assertEquals(VariantType.NON_FS_SUBSTITUTION,varType);
	String annot = ann.getVariantAnnotation();
	Assert.assertEquals("LOC100132247(uc002djq.3:exon7:c.993_1029TCCACCCTCAGCTCTACCCTCAGCG,uc010vbn.1:exon8:c.1050_1086TCCACCCTCAGCTCTACCCTCAGCG,uc002djr.3:exon9:c.1050_1086TCCACCCTCAGCTCTACCCTCAGCG)",annot);
	}
}


/**
 *<P>
 
chr15	7453640074536400	74536416	TAAGAAGGAGACCATCA	TAAGGAGACCATCA
line36947	nonframeshift substitution	CCDC33:NM_025055:exon2:c.96_112TAAGGAGACCATCA,	chr15	74536400
 *</P>
 */
@Test public void testBlocSub2() throws AnnotationException  {
    byte chr = 15;
    int pos = 74536400;
    String ref = "TAAGAAGGAGACCATCA";
    String alt = "TAAGGAGACCATCA";
     Chromosome c = chromosomeMap.get(chr); 
    if (c==null) {
	Assert.fail("Could not identify chromosome \"" + chr + "\"");
    } else {
	AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	VariantType varType = ann.getVariantType();
	Assert.assertEquals(VariantType.NON_FS_SUBSTITUTION,varType);
	String annot = ann.getVariantAnnotation();
	Assert.assertEquals("CCDC33(uc002axo.4:exon2:c.96_112TAAGGAGACCATCA)",annot);
    }
}

@Test public void testBlocSub3() throws AnnotationException  {
    byte chr = 11;
    int pos = 5475431;
    String ref = "TCAACA";
    String alt ="TCACAACA";
     Chromosome c = chromosomeMap.get(chr); 
    if (c==null) {
	Assert.fail("Could not identify chromosome \"" + chr + "\"");
    } else {
	AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	VariantType varType = ann.getVariantType();
	Assert.assertEquals(VariantType.FS_SUBSTITUTION,varType);
	String annot = ann.getVariantAnnotation();
	Assert.assertEquals("OR51I2(uc010qzf.2:exon1:c.713_718TCACAACA)",annot);
    }
}





    /*	nonframeshift substitution	HAVCR1:NM_012206:exon3:c.460_480ACGACTGTTCCAATGACGACGACT,HAVCR1:NM_001173393:exon4:c.460_480ACGACTGTTCCAATGACGACGACT,HAVCR1:NM_001099414:exon4:c.460_480ACGACTGTTCCAATGACGACGACT,	chr5	156479565	156479585	
AGTCGT	
AGTGAG	
expected:<[OR51I2(uc010qzf.2:exon1:c.713_718TCACAACA])> but was:<[HAVCR1(uc021ygj.1:exon4:c.475_480CTCACT,uc011ddm.2:exon4:c.475_480CTCACT,uc010jij.1:exon4:c.475_480CTCACT,uc003lwi.2:exon3:c.475_480CTCACT])>



     */

@Test public void testBlocSub4() throws AnnotationException  {
    byte chr = 5;
    int pos = 156479565;
    String ref = "AGTCGT";
    String alt ="AGTGAG";
     Chromosome c = chromosomeMap.get(chr); 
    if (c==null) {
	Assert.fail("Could not identify chromosome \"" + chr + "\"");
    } else {
	AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	VariantType varType = ann.getVariantType();
	Assert.assertEquals(VariantType.NON_FS_SUBSTITUTION,varType);
	String annot = ann.getVariantAnnotation();
	Assert.assertEquals("HAVCR1(uc021ygj.1:exon4:c.475_480CTCACT,uc011ddm.2:exon4:c.475_480CTCACT,uc010jij.1:exon4:c.475_480CTCACT,uc003lwi.2:exon3:c.475_480CTCACT)",annot);
    }
}

}