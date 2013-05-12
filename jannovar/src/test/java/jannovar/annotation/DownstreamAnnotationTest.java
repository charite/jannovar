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
    public static void setUp() throws IOException {
	HashMap<String,TranscriptModel> kgMap=null;
	// The following file must be created prior to running this test
	try {
	     java.net.URL url = DownstreamAnnotationTest.class.getResource("/ucsc.ser");
	    String path = url.getPath();
	    FileInputStream fileIn = new FileInputStream(path);
	     ObjectInputStream in = new ObjectInputStream(fileIn);
	     kgMap = (HashMap<String,TranscriptModel>) in.readObject();
            in.close();
            fileIn.close();
	} catch(IOException i) {
            i.printStackTrace();
	    System.err.println("Could not deserialize knownGeneMap");
	    System.exit(1);
           
        } catch(ClassNotFoundException c) {
            System.out.println("Could not find HashMap<String,TranscriptModel> class.");
            c.printStackTrace();
            System.exit(1);
        }
	//System.out.println("Done deserialization, size of map is " + kgMap.size());
	chromosomeMap = new HashMap<Byte,Chromosome> ();
	for (TranscriptModel kgl : kgMap.values()) {
	    byte chrom = kgl.getChromosome();
	    if (! chromosomeMap.containsKey(chrom)) {
		Chromosome chr = new Chromosome(chrom);
		chromosomeMap.put(chrom,chr);
	    }
	    Chromosome c = chromosomeMap.get(chrom);
	    c.addGene(kgl);	
	}
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
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ISG15",annot);
	}
}

/**
 *<P>
 * annovar: MASP2,TARDBP
 * chr1:11086098->T
	* Note annovar has "MASP2,TARDBP" but exomizer vice versa
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
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TARDBP,MASP2",annot);
	}
}

/**
 *<P>
 * annovar: EPHA10
 * chr1:38181503C>T
 *</P>
-- Appears to be error in annovar
-- this is actually intronic in some EPHA10 isoforms

@Test public void testDownstreamVar3() throws AnnotationException  {
	byte chr = 1;
	int pos = 38181503;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	  
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("EPHA10",annot);
	}
}
 */

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
	    Assert.assertEquals("VAV3",annot);
	}
}

/**
 *<P>
 * annovar: VAV3
 * chr1:108113526C>G
 *</P>
 */
@Test public void testDownstreamVar5() throws AnnotationException  {
	byte chr = 1;
	int pos = 108113526;
	String ref = "C";
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
	    Assert.assertEquals("VAV3",annot);
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
	    Assert.assertEquals("S100A8",annot);
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
	    Assert.assertEquals("OR10K2",annot);
	}
}

/**
 *<P>
 * annovar: OR10K1
 * chr1:158436299T>C
 *</P>
 */
@Test public void testDownstreamVar8() throws AnnotationException  {
	byte chr = 1;
	int pos = 158436299;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR10K1",annot);
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
	    Assert.assertEquals("WNT9A",annot);
	}
}

/**
 *<P>
 * annovar: OR14A16
 * chr1:247978087G>A
 *</P>
 */
@Test public void testDownstreamVar10() throws AnnotationException  {
	byte chr = 1;
	int pos = 247978087;
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
	    Assert.assertEquals("OR14A16",annot);
	}
}

/**
 *<P>
 * annovar: OR2T1
 * chr1:248570440G>A
 *</P>
 */
@Test public void testDownstreamVar11() throws AnnotationException  {
	byte chr = 1;
	int pos = 248570440;
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
	    Assert.assertEquals("OR2T1",annot);
	}
}

/**
 *<P>
 * annovar: OR2T11
 * chr1:248789454G>C
 *</P>
 */
@Test public void testDownstreamVar12() throws AnnotationException  {
	byte chr = 1;
	int pos = 248789454;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR2T11",annot);
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
	    Assert.assertEquals("OR2T35",annot);
	}
}

/**
 *<P>
 * annovar: OR2T35
 * chr1:248801556C>T
 *</P>
 */
@Test public void testDownstreamVar14() throws AnnotationException  {
	byte chr = 1;
	int pos = 248801556;
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
	    Assert.assertEquals("OR2T35",annot);
	}
}

/**
 *<P>
 * annovar: OR2T35
 * chr1:248801566T>G
 *</P>
 */
@Test public void testDownstreamVar15() throws AnnotationException  {
	byte chr = 1;
	int pos = 248801566;
	String ref = "T";
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
	    Assert.assertEquals("OR2T35",annot);
	}
}

/**
 *<P>
 * annovar: DKFZp667P0924
 * chr2:96521107G>A
 *</P>
 */
@Test public void testDownstreamVar16() throws AnnotationException  {
	byte chr = 2;
	int pos = 96521107;
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
	    Assert.assertEquals("DKFZp667P0924",annot);
	}
}

/**
 *<P>
 * annovar: DKFZp667P0924
 * chr2:96521138T>C
 *</P>
 */
@Test public void testDownstreamVar17() throws AnnotationException  {
	byte chr = 2;
	int pos = 96521138;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DKFZp667P0924",annot);
	}
}

/**
 *<P>
 * annovar: DKFZp667P0924
 * chr2:96521154C>G
 *</P>
 */
@Test public void testDownstreamVar18() throws AnnotationException  {
	byte chr = 2;
	int pos = 96521154;
	String ref = "C";
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
	    Assert.assertEquals("DKFZp667P0924",annot);
	}
}

/**
 *<P>
 * annovar: DKFZp667P0924
 * chr2:96521248TTTT>-
 *</P>
 */
@Test public void testDownstreamVar19() throws AnnotationException  {
	byte chr = 2;
	int pos = 96521248;
	String ref = "TTTT";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DKFZp667P0924",annot);
	}
}

/**
 *<P>
 * annovar: DKFZp667P0924
 * chr2:96521248TTTT>-
 *</P>
 */
@Test public void testDownstreamVar20() throws AnnotationException  {
	byte chr = 2;
	int pos = 96521248;
	String ref = "TTTT";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DKFZp667P0924",annot);
	}
}

/**
 *<P>
 * annovar: DKFZp667P0924
 * chr2:96521406C>T
 *</P>
 */
@Test public void testDownstreamVar21() throws AnnotationException  {
	byte chr = 2;
	int pos = 96521406;
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
	    Assert.assertEquals("DKFZp667P0924",annot);
	}
}

/**
 *<P>
 * annovar: DKFZp667P0924
 * chr2:96521418G>A
 *</P>
 */
@Test public void testDownstreamVar22() throws AnnotationException  {
	byte chr = 2;
	int pos = 96521418;
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
	    Assert.assertEquals("DKFZp667P0924",annot);
	}
}

/**
 *<P>
 * annovar: DKFZp667P0924
 * chr2:96521438A>G
 *</P>
 */
@Test public void testDownstreamVar23() throws AnnotationException  {
	byte chr = 2;
	int pos = 96521438;
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
	    Assert.assertEquals("DKFZp667P0924",annot);
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
	    Assert.assertEquals("DKFZp667P0924",annot);
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
	    Assert.assertEquals("DKFZp667P0924",annot);
	}
}

/**
 *<P>
 * annovar: DKFZp667P0924
 * chr2:96521556T>C
 *</P>
 */
@Test public void testDownstreamVar26() throws AnnotationException  {
	byte chr = 2;
	int pos = 96521556;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DKFZp667P0924",annot);
	}
}

/**
 *<P>
 * annovar: DKFZp667P0924
 * chr2:96521563G>A
 *</P>
 */
@Test public void testDownstreamVar27() throws AnnotationException  {
	byte chr = 2;
	int pos = 96521563;
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
	    Assert.assertEquals("DKFZp667P0924",annot);
	}
}

/**
 *<P>
 * annovar: DKFZp667P0924
 * chr2:96521596C>G
 *</P>
 */
@Test public void testDownstreamVar28() throws AnnotationException  {
	byte chr = 2;
	int pos = 96521596;
	String ref = "C";
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
	    Assert.assertEquals("DKFZp667P0924",annot);
	}
}

/**
 *<P>
 * annovar: MZT2B
 * chr2:130948303T>C
 *</P>
 */
@Test public void testDownstreamVar29() throws AnnotationException  {
	byte chr = 2;
	int pos = 130948303;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MZT2B",annot);
	}
}

/**
 *<P>
 * annovar: Mir_548
 * chr2:225104088A>C
 *</P>
 */
@Test public void testDownstreamVar30() throws AnnotationException  {
	byte chr = 2;
	int pos = 225104088;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("Mir_548",annot);
	}
}

/**
 *<P>
 * annovar: PRR21
 * chr2:240981179T>C
 *</P>
 */
@Test public void testDownstreamVar31() throws AnnotationException  {
	byte chr = 2;
	int pos = 240981179;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PRR21",annot);
	}
}

/**
 *<P>
 * annovar: MTERFD2,SNED1
 * chr2:242033770T>A
 *</P>
 */
@Test public void testDownstreamVar32() throws AnnotationException  {
	byte chr = 2;
	int pos = 242033770;
	String ref = "T";
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
	    Assert.assertEquals("SNED1,MTERFD2",annot);
	}
}

/**
 *<P>
 * annovar: IQCF5
 * chr3:51907736A>G
 *</P>

@Test public void testDownstreamVar33() throws AnnotationException  {
	byte chr = 3;
	int pos = 51907736;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	   
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("IQCF5",annot);
	}
}
 */
/**
 *<P>
 * annovar: MIR1324
 * chr3:75680035G>C
 *</P>
 */
@Test public void testDownstreamVar34() throws AnnotationException  {
	byte chr = 3;
	int pos = 75680035;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MIR1324",annot);
	}
}

/**
 *<P>
 * annovar: OR5AC2
 * chr3:97806999T>C
 *</P>
 */
@Test public void testDownstreamVar35() throws AnnotationException  {
	byte chr = 3;
	int pos = 97806999;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR5AC2",annot);
	}
}

/**
 *<P>
 * annovar: GPR15
 * chr3:98252027G>A
 *</P>
 */
@Test public void testDownstreamVar36() throws AnnotationException  {
	byte chr = 3;
	int pos = 98252027;
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
	    Assert.assertEquals("GPR15",annot);
	}
}

/**
 *<P>
 * annovar: MIR548I1
 * chr3:125509108CGG>-
 *</P>
 */
@Test public void testDownstreamVar37() throws AnnotationException  {
	byte chr = 3;
	int pos = 125509108;
	String ref = "CGG";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MIR548I1",annot);
	}
}

/**
 *<P>
 * annovar: MIR551B
 * chr3:168269756A>G
 *</P>
	--- Annovar appears to be in error here, it is in an ncRNA intron

@Test public void testDownstreamVar38() throws AnnotationException  {
	byte chr = 3;
	int pos = 168269756;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MIR551B",annot);
	}
}
 */
/**
 *<P>
 * annovar: PYDC2
 * chr3:191179303C>T
 *</P>
 */
@Test public void testDownstreamVar39() throws AnnotationException  {
	byte chr = 3;
	int pos = 191179303;
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
	    Assert.assertEquals("PYDC2",annot);
	}
}

/**
 *<P>
 * annovar: HSP90AB3P
 * chr4:88815223C>T
 *</P>
 */
@Test public void testDownstreamVar40() throws AnnotationException  {
	byte chr = 4;
	int pos = 88815223;
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
	    Assert.assertEquals("HSP90AB3P",annot);
	}
}


/**
 *<P>
 * annovar: AK308309
 * chr4:119436020->TC
 *</P>
 */
@Test public void testDownstreamVar41() throws AnnotationException  {
	byte chr = 4;
	int pos = 119436020;
	String ref = "-";
	String alt = "TC";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AK308309",annot);
	}
}

/**
 *<P>
 * annovar: LRRC14B
 * chr5:195507C>T
 *</P>
 */
@Test public void testDownstreamVar42() throws AnnotationException  {
	byte chr = 5;
	int pos = 195507;
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
	    Assert.assertEquals("LRRC14B",annot);
	}
}

/**
 *<P>
 * annovar: GUSBP1
 * chr5:21497312G>T
-- mistakenly called as downstream by annovar
 *</P>
 
@Test public void testDownstreamVar43() throws AnnotationException  {
	byte chr = 5;
	int pos = 21497312;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    ArrayList<Annotation> anno_list = c.getAnnotationList(pos,ref,alt)
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("GUSBP1",annot);
	}
}
*/

/**
 *<P>
 * annovar: RPS14
 * chr5:149823785G>A
 *</P>
 */
@Test public void testDownstreamVar44() throws AnnotationException  {
	byte chr = 5;
	int pos = 149823785;
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
	    Assert.assertEquals("RPS14",annot);
	}
}

/**
 *<P>
 * annovar: FABP6
 * chr5:159665733C>T
 *</P>
 */
@Test public void testDownstreamVar45() throws AnnotationException  {
	byte chr = 5;
	int pos = 159665733;
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
	    Assert.assertEquals("FABP6",annot);
	}
}

/**
 *<P>
 * annovar: BTNL2,HCG23
 * chr6:32362453A>G
 *</P>
	-- annovar and exomizer have the order vice versa
 */
@Test public void testDownstreamVar46() throws AnnotationException  {
	byte chr = 6;
	int pos = 32362453;
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
	    Assert.assertEquals("HCG23,BTNL2",annot);
	}
}

/**
 *<P>
 * annovar: MAPK13
 * chr6:36108118G>C
 *</P>
 */
@Test public void testDownstreamVar47() throws AnnotationException  {
	byte chr = 6;
	int pos = 36108118;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MAPK13",annot);
	}
}

/**
 *<P>
 * annovar: HTR1B
 * chr6:78171941C>T
 *</P>
 */
@Test public void testDownstreamVar48() throws AnnotationException  {
	byte chr = 6;
	int pos = 78171941;
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
	    Assert.assertEquals("HTR1B",annot);
	}
}

/**
 *<P>
 * annovar: TAAR9
 * chr6:132860522C>G
 *</P>
 */
@Test public void testDownstreamVar49() throws AnnotationException  {
	byte chr = 6;
	int pos = 132860522;
	String ref = "C";
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
	    Assert.assertEquals("TAAR9",annot);
	}
}

/**
 *<P>
 * annovar: MIR148A
 * chr7:25989520T>G
 *</P>
 */
@Test public void testDownstreamVar50() throws AnnotationException  {
	byte chr = 7;
	int pos = 25989520;
	String ref = "T";
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
	    Assert.assertEquals("MIR148A",annot);
	}
}

/**
 *<P>
 * annovar: STEAP2
 * chr7:89867369C>G
 *</P>
 */
@Test public void testDownstreamVar51() throws AnnotationException  {
	byte chr = 7;
	int pos = 89867369;
	String ref = "C";
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
	    Assert.assertEquals("STEAP2",annot);
	}
}

/**
 *<P>
 * annovar: OR2AE1
 * chr7:99473624T>G
 *</P>
 */
@Test public void testDownstreamVar52() throws AnnotationException  {
	byte chr = 7;
	int pos = 99473624;
	String ref = "T";
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
	    Assert.assertEquals("OR2AE1",annot);
	}
}



/**
 *<P>
 * annovar: TAS2R39
 * chr7:142881540G>A
 *</P>
 */
@Test public void testDownstreamVar53() throws AnnotationException  {
	byte chr = 7;
	int pos = 142881540;
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
	    Assert.assertEquals("TAS2R39",annot);
	}
}

/**
 *<P>
 * annovar: OR2A2
 * chr7:143807674T>C
 *</P>
 */
@Test public void testDownstreamVar54() throws AnnotationException  {
	byte chr = 7;
	int pos = 143807674;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR2A2",annot);
	}
}

/**
 *<P>
 * annovar: NPM2
 * chr8:21895070G>A
 *</P>
 */
@Test public void testDownstreamVar55() throws AnnotationException  {
	byte chr = 8;
	int pos = 21895070;
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
	    Assert.assertEquals("NPM2",annot);
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
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NPM2",annot);
	}
}

/**
 *<P>
 * annovar: PSKH2
 * chr8:87060672A>G
 *</P>
 */
@Test public void testDownstreamVar57() throws AnnotationException  {
	byte chr = 8;
	int pos = 87060672;
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
	    Assert.assertEquals("PSKH2",annot);
	}
}

/**
 *<P>
 * annovar: Mir_320
 * chr9:4378368A>G
 *</P>
 */
@Test public void testDownstreamVar58() throws AnnotationException  {
	byte chr = 9;
	int pos = 4378368;
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
	    Assert.assertEquals("Mir_320",annot);
	}
}

/**
 *<P>
 * annovar: OR13D1
 * chr9:107457759C>T
 *</P>
 */
@Test public void testDownstreamVar59() throws AnnotationException  {
	byte chr = 9;
	int pos = 107457759;
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
	    Assert.assertEquals("OR13D1",annot);
	}
}

/**
 *<P>
 * annovar: COL27A1
 * chr9:117073006C>T
 *</P>
 */
@Test public void testDownstreamVar60() throws AnnotationException  {
	byte chr = 9;
	int pos = 117073006;
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
	    Assert.assertEquals("COL27A1",annot);
	}
}

/**
 *<P>
 * annovar: LOC100288842
 * chr9:123561856C>A
 *</P>
 */
@Test public void testDownstreamVar61() throws AnnotationException  {
	byte chr = 9;
	int pos = 123561856;
	String ref = "C";
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
	    Assert.assertEquals("LOC100288842",annot);
	}
}



/**
 *<P>
 * annovar: OR1J4
 * chr9:125282393C>T
 *</P>
 */
@Test public void testDownstreamVar62() throws AnnotationException  {
	byte chr = 9;
	int pos = 125282393;
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
	    Assert.assertEquals("OR1J4",annot);
	}
}

/**
 *<P>
 * annovar: FCN1
 * chr9:137801314A>G
 *</P>
 */
@Test public void testDownstreamVar63() throws AnnotationException  {
	byte chr = 9;
	int pos = 137801314;
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
	    Assert.assertEquals("FCN1",annot);
	}
}

/**
 *<P>
 * annovar: FCN1
 * chr9:137801416T>C
 *</P>
 */
@Test public void testDownstreamVar64() throws AnnotationException  {
	byte chr = 9;
	int pos = 137801416;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FCN1",annot);
	}
}

/**
 *<P>
 * annovar: AKR1CL1
 * chr10:5196273A>G
 *</P>
 */
@Test public void testDownstreamVar65() throws AnnotationException  {
	byte chr = 10;
	int pos = 5196273;
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
	    Assert.assertEquals("AKR1CL1",annot);
	}
}

/**
 *<P>
 * annovar: AK128534
 * chr10:5558146->C
 *</P>
 */
@Test public void testDownstreamVar66() throws AnnotationException  {
	byte chr = 10;
	int pos = 5558146;
	String ref = "-";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AK128534",annot);
	}
}

/**
 *<P>
 * annovar: C10orf112
 * chr10:19896830C>A
 *</P>
 */
@Test public void testDownstreamVar67() throws AnnotationException  {
	byte chr = 10;
	int pos = 19896830;
	String ref = "C";
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
	    Assert.assertEquals("C10orf112",annot);
	}
}

/**
 *<P>
 * annovar: LOC619207
 * chr10:135279283T>C
 *</P>
 --mistake in annovar, this is ncRNA intron!

@Test public void testDownstreamVar68() throws AnnotationException  {
	byte chr = 10;
	int pos = 135279283;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	   
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC619207",annot);
	}
}
*/

/**
 *<P>
 * annovar: MUC5B
 * chr11:1284227A>G
 *</P>
 */
@Test public void testDownstreamVar69() throws AnnotationException  {
	byte chr = 11;
	int pos = 1284227;
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
	    Assert.assertEquals("MUC5B",annot);
	}
}


/**
 *<P>
 * annovar: SLC22A18
 * chr11:2946522G>A
 *</P>
 */
@Test public void testDownstreamVar70() throws AnnotationException  {
	byte chr = 11;
	int pos = 2946522;
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
	    Assert.assertEquals("SLC22A18",annot);
	}
}

/**
 *<P>
 * annovar: DQ656008
 * chr11:5141876G>A
 *</P>
 */
@Test public void testDownstreamVar71() throws AnnotationException  {
	byte chr = 11;
	int pos = 5141876;
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
	    Assert.assertEquals("DQ656008",annot);
	}
}

/**
 *<P>
 * annovar: DQ656008
 * chr11:5141902T>C
 *</P>
 */
@Test public void testDownstreamVar72() throws AnnotationException  {
	byte chr = 11;
	int pos = 5141902;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DQ656008",annot);
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
	    Assert.assertEquals("DQ656008",annot);
	}
}

/**
 *<P>
 * annovar: DQ656008
 * chr11:5142270T>G
 *</P>
 */
@Test public void testDownstreamVar74() throws AnnotationException  {
	byte chr = 11;
	int pos = 5142270;
	String ref = "T";
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
	    Assert.assertEquals("DQ656008",annot);
	}
}

/**
 *<P>
 * annovar: OR52A1
 * chr11:5172607A>C
 *</P>
 */
@Test public void testDownstreamVar75() throws AnnotationException  {
	byte chr = 11;
	int pos = 5172607;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR52A1",annot);
	}
}

/**
 *<P>
 * annovar: OR56A5
 * chr11:5988340C>T
 *</P>
 */
@Test public void testDownstreamVar76() throws AnnotationException  {
	byte chr = 11;
	int pos = 5988340;
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
	    Assert.assertEquals("OR56A5",annot);
	}
}

/**
 *<P>
 * annovar: OR56A5
 * chr11:5988446T>C
 *</P>
 */
@Test public void testDownstreamVar77() throws AnnotationException  {
	byte chr = 11;
	int pos = 5988446;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR56A5",annot);
	}
}

/**
 *<P>
 * annovar: RPS13,SNORD14
 * chr11:17095596T>C
 *</P>
 */
@Test public void testDownstreamVar78() throws AnnotationException  {
	byte chr = 11;
	int pos = 17095596;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RPS13,SNORD14",annot);
	}
}

/**
 *<P>
 * annovar: MRGPRX1
 * chr11:18954699T>G
 *</P>
 */
@Test public void testDownstreamVar79() throws AnnotationException  {
	byte chr = 11;
	int pos = 18954699;
	String ref = "T";
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
	    Assert.assertEquals("MRGPRX1",annot);
	}
}

/**
 *<P>
 * annovar: COMMD9
 * chr11:36293048G>A
 *</P>
 */
@Test public void testDownstreamVar80() throws AnnotationException  {
	byte chr = 11;
	int pos = 36293048;
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
	    Assert.assertEquals("COMMD9",annot);
	}
}

/**
 *<P>
 * annovar: OR4X2
 * chr11:48267589A>G
 *</P>
 */
@Test public void testDownstreamVar81() throws AnnotationException  {
	byte chr = 11;
	int pos = 48267589;
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
	    Assert.assertEquals("OR4X2",annot);
	}
}

/**
 *<P>
 * annovar: LOC440041
 * chr11:55061100C>G
 *</P>
 */
@Test public void testDownstreamVar82() throws AnnotationException  {
	byte chr = 11;
	int pos = 55061100;
	String ref = "C";
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
	    Assert.assertEquals("LOC440041",annot);
	}
}

/**
 *<P>
 * annovar: OR10Q1
 * chr11:57995323T>G
 *</P>
 */
@Test public void testDownstreamVar83() throws AnnotationException  {
	byte chr = 11;
	int pos = 57995323;
	String ref = "T";
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
	    Assert.assertEquals("OR10Q1",annot);
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
	    Assert.assertEquals("OR5AN1",annot);
	}
}

/**
 *<P>
 * annovar: OR5A2
 * chr11:59189405T>C
 *</P>
 */
@Test public void testDownstreamVar85() throws AnnotationException  {
	byte chr = 11;
	int pos = 59189405;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR5A2",annot);
	}
}

/**
 *<P>
 * annovar: BC104003;TM7SF2,ZNHIT2
 * chr11:64883864A>G
 *</P>
 TODO: In annovar this is annotated as up AND downstream (correctly).
@Test public void testDownstreamVar86() throws AnnotationException  {
	byte chr = 11;
	int pos = 64883864;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	   
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BC104003;TM7SF2,ZNHIT2",annot);
	}
}
*/
/**
 *<P>
 * annovar: FAM86C2P
 * chr11:67558613->TTTA
 *</P>
 */
@Test public void testDownstreamVar87() throws AnnotationException  {
	byte chr = 11;
	int pos = 67558613;
	String ref = "-";
	String alt = "TTTA";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FAM86C2P",annot);
	}
}

/**
 *<P>
 * annovar: KIAA1731,SNORA8
 * chr11:93463666C>A
 *</P>

@Test public void testDownstreamVar88() throws AnnotationException  {
	byte chr = 11;
	int pos = 93463666;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	  
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KIAA1731,SNORA8",annot);
	}
}
 */

/**
 *<P>
 * annovar: OR10S1
 * chr11:123847392G>C
 *</P>
 */
@Test public void testDownstreamVar89() throws AnnotationException  {
	byte chr = 11;
	int pos = 123847392;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR10S1",annot);
	}
}

/**
 *<P>
 * annovar: AMHR2
 * chr12:53825325T>C
 *</P>
 */
@Test public void testDownstreamVar90() throws AnnotationException  {
	byte chr = 12;
	int pos = 53825325;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	   
	    /* There should be just one annotation */
	   
	   
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.DOWNSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AMHR2",annot);
	}
}

}
