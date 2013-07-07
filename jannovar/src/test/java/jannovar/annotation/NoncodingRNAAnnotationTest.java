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
public class NoncodingRNAAnnotationTest implements Constants {

    
   
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
 * annovar: C12orf54
 * chr12:48883012T>C
 * This variant overlaps an intron of a coding isoform of C12orf54 as well as
 * an exon of a non-coding RNA of this gene (refered to as near-coding by UCSC).
 * Therefore, Jannovar is correct in calling this ncRNA_EXONIC.
 *</P>
 */
@Test public void testIntronicVar11571() throws AnnotationException  {
	byte chr = 12;
	int pos = 48883012;
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
	    Assert.assertEquals("C12orf54(uc009zky.1:exon5:n.359A>G)",annot);
	}
}

/**
 *<P>
 * annovar: BC136808
 * chr1:173429995G>A
 *</P>
 */
@Test public void testNcRnaExonicVar33() throws AnnotationException  {
	byte chr = 1;
	int pos = 173429995;
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
	    Assert.assertEquals("BC136808(uc010pmp.1:exon2:n.507C>T)",annot);
	}
}

/**
 *<P>
 * annovar: abParts
 * chr2:90458648T>-
 *</P>
 */
@Test public void testNcRnaExonicVar68() throws AnnotationException  {
	byte chr = 2;
	int pos = 90458648;
	String ref = "T";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("abParts(uc031rom.1:exon43:n.5842delA)",annot);
	}
}




/**
 *<P>
 * annovar: DQ658414,MIR146A
 * chr5:159912418C>G
 *</P>
 */
@Test public void testNcRnaExonicVar155() throws AnnotationException  {
	byte chr = 5;
	int pos = 159912418;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DQ658414(uc003lyl.4:exon2:n.316G>C),MIR146A(uc021yhe.1:exon1:n.60G>C)",annot);
	}
}

/**
 *<P>
 * annovar: SNORD48
 * chr6:31803065T>-
 *</P>
 */
@Test public void testNcRnaExonicVar165() throws AnnotationException  {
	byte chr = 6;
	int pos = 31803065;
	String ref = "T";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SNORD48(uc003nxo.1:exon1:n.26delA)",annot);
	}
}

/**
 *<P>
 * annovar: BC042913,BC080653
 * chr9:97329738->GA
 *</P>
 */
@Test public void testNcRnaExonicVar281() throws AnnotationException  {
	byte chr = 9;
	int pos = 97329738;
	String ref = "-";
	String alt = "GA";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BC042913(uc004aus.1:exon3:n.493_494insTC),BC080653(uc004aut.1:exon3:n.541_542insTC)",annot);
	}
}



/**
 *<P>
 * annovar: AK024141
 * chr14:73079294->AA
 * minus strand.
 *</P>
 */
@Test public void testNcRnaExonicVar399() throws AnnotationException  {
	byte chr = 14;
	int pos = 73079294;
	String ref = "-";
	String alt = "AA";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AK024141(uc010arh.1:exon1:n.510_511insTT)",annot);
	}
}



/**
 *<P>
 * annovar: LOC440434
 * chr17:36353761C>T
  * minus strand.
 *</P>
 */
@Test public void testNcRnaExonicVar518() throws AnnotationException  {
	byte chr = 17;
	int pos = 36353761;
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
	    Assert.assertEquals("LOC440434(uc010wdn.1:exon7:n.876G>A)",annot);
	}
}

/**
 *<P>
 * annovar: mir-133b
 * chr18:19408950C>T
 *</P>
 */
@Test public void testNcRnaExonicVar530() throws AnnotationException  {
	byte chr = 18;
	int pos = 19408950;
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
	    Assert.assertEquals("mir-133b(uc002ktr.3:exon1:n.862G>A),mir-133b(uc002kts.3:exon1:n.724G>A)",annot);
	}
}
    /**
 *<P>
 * annovar: FAM182B
 * chr20:25829352T>C
 *</P>
 */
@Test public void testNcRnaExonicVar560() throws AnnotationException  {
	byte chr = 20;
	int pos = 25829352;
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
	    Assert.assertEquals("BX648489(uc031rsn.1:exon1:n.2233A>G),FAM182B(uc002wvd.1:exon4:n.431A>G),FAM182B(uc002wve.3:exon2:n.375A>G)",annot);
	}
}


  

/**
 *<P>
 * annovar: INGX
 * chrX_CHROMOSOME:70711958T>C
 *</P>
 */
@Test public void testNcRnaExonicVar649() throws AnnotationException  {
	byte chr = X_CHROMOSOME;
	int pos = 70711958;
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
	    Assert.assertEquals("INGX(uc004dzz.3:exon1:n.647A>G)",annot);
	}
}

/**
 *<P>
 * annovar: TTTY11
 * chrY_CHROMOSOME:8657215C>A
 *</P>
 */
@Test public void testNcRnaExonicVar651() throws AnnotationException  {
	byte chr = Y_CHROMOSOME;
	int pos = 8657215;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TTTY11(uc004frk.2:exon3:n.250G>T)",annot);
	}
}

/**
 *<P>
 * annovar: TTTY13
 * chrY_CHROMOSOME:23749507G>-
 *</P>
 */
@Test public void testNcRnaExonicVar652() throws AnnotationException  {
	byte chr = Y_CHROMOSOME;
	int pos = 23749507;
	String ref = "G";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TTTY13(uc004fus.3:exon3:n.384delC)",annot);
	}
}






}