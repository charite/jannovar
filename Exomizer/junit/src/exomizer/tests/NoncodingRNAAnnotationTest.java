package exomizer.tests;


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


import exomizer.io.UCSCKGParser;
import exomizer.common.Constants;
import exomizer.io.AnnovarParser;
import exomizer.reference.KnownGene;
import exomizer.reference.Chromosome;
import exomizer.reference.Annotation;
import exomizer.exome.Variant;
import exomizer.exception.AnnotationException;


import org.junit.Test;
import org.junit.BeforeClass;
import junit.framework.Assert;


/**
 * This class is intended to perform unuit testing on variants that
 * are intergenic. 
 */
public class NoncodingRNAAnnotationTest implements Constants {

    
   
    private static HashMap<Byte,Chromosome> chromosomeMap = null;

  

    @SuppressWarnings (value="unchecked")
    @BeforeClass 
    public static void setUp() throws IOException {
	HashMap<String,KnownGene> kgMap=null;
	// The following file must be created prior to running this test
	String serializedFile = "../ucsc.ser";
	try {
	     FileInputStream fileIn =
		 new FileInputStream(serializedFile);
	     ObjectInputStream in = new ObjectInputStream(fileIn);
	     kgMap = (HashMap<String,KnownGene>) in.readObject();
            in.close();
            fileIn.close();
	} catch(IOException i) {
            i.printStackTrace();
	    System.err.println("Could not deserialize knownGeneMap");
	    System.exit(1);
           
        } catch(ClassNotFoundException c) {
            System.out.println("Could not find HashMap<String,KnownGene> class.");
            c.printStackTrace();
            System.exit(1);
        }
	chromosomeMap = new HashMap<Byte,Chromosome> ();
	for (KnownGene kgl : kgMap.values()) {
	    byte chrom = kgl.getChromosome();
	    if (! chromosomeMap.containsKey(chrom)) {
		Chromosome chr = new Chromosome(chrom);
		chromosomeMap.put(chrom,chr);
	    }
	    Chromosome c = chromosomeMap.get(chrom);
	    c.addGene(kgl);	
	}
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UTR3,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SLIT2",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BC136808",annot);
	}
}

/**
 *<P>
 * annovar: SCARNA3
 * chr1:175937540C>T
 *</P>
 */
@Test public void testNcRnaExonicVar34() throws AnnotationException  {
	byte chr = 1;
	int pos = 175937540;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SCARNA3",annot);
	}
}

/**
 *<P>
 * annovar: LINC00303
 * chr1:204006538T>C
 *</P>
 */
@Test public void testNcRnaExonicVar37() throws AnnotationException  {
	byte chr = 1;
	int pos = 204006538;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LINC00303",annot);
	}
}

/**
 *<P>
 * annovar: BC051708
 * chr2:10094992A>G
 *</P>
 */
@Test public void testNcRnaExonicVar51() throws AnnotationException  {
	byte chr = 2;
	int pos = 10094992;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BC051708",annot);
	}
}

/**
 *<P>
 * annovar: MIR217
 * chr2:56210140G>A
 *</P>
 */
@Test public void testNcRnaExonicVar53() throws AnnotationException  {
	byte chr = 2;
	int pos = 56210140;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MIR217",annot);
	}
}

/**
 *<P>
 * annovar: abParts
 * chr2:89292182A>G
 *</P>
 */
@Test public void testNcRnaExonicVar61() throws AnnotationException  {
	byte chr = 2;
	int pos = 89292182;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("abParts",annot);
	}
}

/**
 *<P>
 * annovar: abParts
 * chr2:89533810C>T
 *</P>
 */
@Test public void testNcRnaExonicVar67() throws AnnotationException  {
	byte chr = 2;
	int pos = 89533810;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("abParts",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("abParts",annot);
	}
}

/**
 *<P>
 * annovar: LOC654433
 * chr2:114017029A>G
 *</P>
 */
@Test public void testNcRnaExonicVar76() throws AnnotationException  {
	byte chr = 2;
	int pos = 114017029;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC654433",annot);
	}
}

/**
 *<P>
 * annovar: POTEKP
 * chr2:132349413G>A
 *</P>
 */
@Test public void testNcRnaExonicVar79() throws AnnotationException  {
	byte chr = 2;
	int pos = 132349413;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("POTEKP",annot);
	}
}

/**
 *<P>
 * annovar: POTEKP
 * chr2:132384682T>C
 *</P>
 */
@Test public void testNcRnaExonicVar86() throws AnnotationException  {
	byte chr = 2;
	int pos = 132384682;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("POTEKP",annot);
	}
}

/**
 *<P>
 * annovar: C4orf42
 * chr4:1244416A>G
 *</P>
 */
@Test public void testNcRnaExonicVar123() throws AnnotationException  {
	byte chr = 4;
	int pos = 1244416;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C4orf42",annot);
	}
}

/**
 *<P>
 * annovar: FLJ35424
 * chr4:3589623G>A
 *</P>
 */
@Test public void testNcRnaExonicVar125() throws AnnotationException  {
	byte chr = 4;
	int pos = 3589623;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FLJ35424",annot);
	}
}

/**
 *<P>
 * annovar: FAM13A-AS1
 * chr4:89649659A>G
 *</P>
 */
@Test public void testNcRnaExonicVar135() throws AnnotationException  {
	byte chr = 4;
	int pos = 89649659;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FAM13A-AS1",annot);
	}
}

/**
 *<P>
 * annovar: ADH1C
 * chr4:100266371A>G
 *</P>
 */
@Test public void testNcRnaExonicVar137() throws AnnotationException  {
	byte chr = 4;
	int pos = 100266371;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ADH1C",annot);
	}
}

/**
 *<P>
 * annovar: HSP90AA6P
 * chr4:171526012C>T
 *</P>
 */
@Test public void testNcRnaExonicVar142() throws AnnotationException  {
	byte chr = 4;
	int pos = 171526012;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HSP90AA6P",annot);
	}
}

/**
 *<P>
 * annovar: BC013821
 * chr5:479905A>C
 *</P>
 */
@Test public void testNcRnaExonicVar146() throws AnnotationException  {
	byte chr = 5;
	int pos = 479905;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BC013821",annot);
	}
}

/**
 *<P>
 * annovar: CRSP8P
 * chr5:79647685A>C
 *</P>
 */
@Test public void testNcRnaExonicVar149() throws AnnotationException  {
	byte chr = 5;
	int pos = 79647685;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CRSP8P",annot);
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
	int pos = 135272375;
	String ref = "-";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FBXL21",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DQ658414,MIR146A",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SNORD48",annot);
	}
}

/**
 *<P>
 * annovar: SNORA29
 * chr6:160206631T>C
 *</P>
 */
@Test public void testNcRnaExonicVar179() throws AnnotationException  {
	byte chr = 6;
	int pos = 160206631;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SNORA29",annot);
	}
}

/**
 *<P>
 * annovar: SNORA29
 * chr6:160206716T>C
 *</P>
 */
@Test public void testNcRnaExonicVar180() throws AnnotationException  {
	byte chr = 6;
	int pos = 160206716;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SNORA29",annot);
	}
}

/**
 *<P>
 * annovar: MUC3B
 * chr7:100550766A>T
 *</P>
 */
@Test public void testNcRnaExonicVar207() throws AnnotationException  {
	byte chr = 7;
	int pos = 100550766;
	String ref = "A";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MUC3B",annot);
	}
}

/**
 *<P>
 * annovar: MUC3B
 * chr7:100550925G>C
 *</P>
 */
@Test public void testNcRnaExonicVar212() throws AnnotationException  {
	byte chr = 7;
	int pos = 100550925;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MUC3B",annot);
	}
}

/**
 *<P>
 * annovar: MUC3B
 * chr7:100551035G>C
 *</P>
 */
@Test public void testNcRnaExonicVar213() throws AnnotationException  {
	byte chr = 7;
	int pos = 100551035;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MUC3B",annot);
	}
}

/**
 *<P>
 * annovar: SND1-IT1
 * chr7:127637816A>G
 *</P>
 */
@Test public void testNcRnaExonicVar215() throws AnnotationException  {
	byte chr = 7;
	int pos = 127637816;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SND1-IT1",annot);
	}
}

/**
 *<P>
 * annovar: LOC642236
 * chr9:68427825->A
 *</P>
 */
@Test public void testNcRnaExonicVar272() throws AnnotationException  {
	byte chr = 9;
	int pos = 68427825;
	String ref = "-";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC642236",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BC042913,BC080653",annot);
	}
}

/**
 *<P>
 * annovar: C9orf29
 * chr9:114371493C>T
 *</P>
 */
@Test public void testNcRnaExonicVar284() throws AnnotationException  {
	byte chr = 9;
	int pos = 114371493;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C9orf29",annot);
	}
}

/**
 *<P>
 * annovar: AK125237
 * chr10:27577615T>G
 *</P>
 */
@Test public void testNcRnaExonicVar300() throws AnnotationException  {
	byte chr = 10;
	int pos = 27577615;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AK125237",annot);
	}
}

/**
 *<P>
 * annovar: LOC441666
 * chr10:42833133ATT>-
 *</P>
 */
@Test public void testNcRnaExonicVar307() throws AnnotationException  {
	byte chr = 10;
	int pos = 42833133;
	String ref = "ATT";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC441666",annot);
	}
}

/**
 *<P>
 * annovar: LOC441666
 * chr10:42833387->TAGG
 *</P>
 */
@Test public void testNcRnaExonicVar308() throws AnnotationException  {
	byte chr = 10;
	int pos = 42833387;
	String ref = "-";
	String alt = "TAGG";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC441666",annot);
	}
}

/**
 *<P>
 * annovar: BC039000
 * chr10:42920832G>A
 *</P>
 */
@Test public void testNcRnaExonicVar313() throws AnnotationException  {
	byte chr = 10;
	int pos = 42920832;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BC039000",annot);
	}
}

/**
 *<P>
 * annovar: LOC728643
 * chr10:47133833A>G
 *</P>
 */
@Test public void testNcRnaExonicVar317() throws AnnotationException  {
	byte chr = 10;
	int pos = 47133833;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC728643",annot);
	}
}

/**
 *<P>
 * annovar: ANTXRL
 * chr10:47668707T>C
 *</P>
 */
@Test public void testNcRnaExonicVar318() throws AnnotationException  {
	byte chr = 10;
	int pos = 47668707;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ANTXRL",annot);
	}
}

/**
 *<P>
 * annovar: AX748062
 * chr10:90693569T>C
 *</P>
 */
@Test public void testNcRnaExonicVar325() throws AnnotationException  {
	byte chr = 10;
	int pos = 90693569;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AX748062",annot);
	}
}

/**
 *<P>
 * annovar: MIR1307
 * chr10:105154089A>G
 *</P>
 */
@Test public void testNcRnaExonicVar327() throws AnnotationException  {
	byte chr = 10;
	int pos = 105154089;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MIR1307",annot);
	}
}

/**
 *<P>
 * annovar: FLJ46300
 * chr10:133607937G>A
 *</P>
 */
@Test public void testNcRnaExonicVar333() throws AnnotationException  {
	byte chr = 10;
	int pos = 133607937;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FLJ46300",annot);
	}
}

/**
 *<P>
 * annovar: MUC5AC
 * chr11:1213245T>A
 *</P>
 */
@Test public void testNcRnaExonicVar337() throws AnnotationException  {
	byte chr = 11;
	int pos = 1213245;
	String ref = "T";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MUC5AC",annot);
	}
}

/**
 *<P>
 * annovar: AB231741
 * chr11:56570012C>T
 *</P>
 */
@Test public void testNcRnaExonicVar351() throws AnnotationException  {
	byte chr = 11;
	int pos = 56570012;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AB231741",annot);
	}
}

/**
 *<P>
 * annovar: ANKRD20A9P
 * chr13:19409622T>C
 *</P>
 */
@Test public void testNcRnaExonicVar380() throws AnnotationException  {
	byte chr = 13;
	int pos = 19409622;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ANKRD20A9P",annot);
	}
}

/**
 *<P>
 * annovar: ATP5EP2
 * chr13:28519354A>C
 *</P>
 */
@Test public void testNcRnaExonicVar388() throws AnnotationException  {
	byte chr = 13;
	int pos = 28519354;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ATP5EP2",annot);
	}
}

/**
 *<P>
 * annovar: SPG20OS
 * chr13:36939688C>T
 *</P>
 */
@Test public void testNcRnaExonicVar389() throws AnnotationException  {
	byte chr = 13;
	int pos = 36939688;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SPG20OS",annot);
	}
}

/**
 *<P>
 * annovar: TPTE2P3
 * chr13:53153549C>T
 *</P>
 */
@Test public void testNcRnaExonicVar391() throws AnnotationException  {
	byte chr = 13;
	int pos = 53153549;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TPTE2P3",annot);
	}
}

/**
 *<P>
 * annovar: AX747676
 * chr13:76445189A>G
 *</P>
 */
@Test public void testNcRnaExonicVar392() throws AnnotationException  {
	byte chr = 13;
	int pos = 76445189;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AX747676",annot);
	}
}

/**
 *<P>
 * annovar: AK024141
 * chr14:73079294->AA
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AK024141",annot);
	}
}

/**
 *<P>
 * annovar: abParts
 * chr14:106452817A>G
 *</P>
 */
@Test public void testNcRnaExonicVar417() throws AnnotationException  {
	byte chr = 14;
	int pos = 106452817;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("abParts",annot);
	}
}

/**
 *<P>
 * annovar: BC042994,abParts
 * chr14:106586290C>T
 *</P>
 */
@Test public void testNcRnaExonicVar427() throws AnnotationException  {
	byte chr = 14;
	int pos = 106586290;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BC042994,abParts",annot);
	}
}

/**
 *<P>
 * annovar: abParts
 * chr14:106805408C>T
 *</P>
 */
@Test public void testNcRnaExonicVar436() throws AnnotationException  {
	byte chr = 14;
	int pos = 106805408;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("abParts",annot);
	}
}

/**
 *<P>
 * annovar: abParts
 * chr14:107095216G>A
 *</P>
 */
@Test public void testNcRnaExonicVar448() throws AnnotationException  {
	byte chr = 14;
	int pos = 107095216;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("abParts",annot);
	}
}

/**
 *<P>
 * annovar: DQ588973
 * chr15:23260979->AGAG
 *</P>
 */
@Test public void testNcRnaExonicVar465() throws AnnotationException  {
	byte chr = 15;
	int pos = 23260979;
	String ref = "-";
	String alt = "AGAG";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DQ588973",annot);
	}
}

/**
 *<P>
 * annovar: HERC2P9
 * chr15:28899497ACGC>-
 *</P>
 */
@Test public void testNcRnaExonicVar468() throws AnnotationException  {
	byte chr = 15;
	int pos = 28899497;
	String ref = "ACGC";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HERC2P9",annot);
	}
}

/**
 *<P>
 * annovar: DNM1P46
 * chr15:100331800G>A
 *</P>
 */
@Test public void testNcRnaExonicVar476() throws AnnotationException  {
	byte chr = 15;
	int pos = 100331800;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DNM1P46",annot);
	}
}

/**
 *<P>
 * annovar: DNM1P46
 * chr15:100340323G>A
 *</P>
 */
@Test public void testNcRnaExonicVar479() throws AnnotationException  {
	byte chr = 15;
	int pos = 100340323;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DNM1P46",annot);
	}
}

/**
 *<P>
 * annovar: BC073817
 * chr15:101455013G>T
 *</P>
 */
@Test public void testNcRnaExonicVar483() throws AnnotationException  {
	byte chr = 15;
	int pos = 101455013;
	String ref = "G";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BC073817",annot);
	}
}

/**
 *<P>
 * annovar: DQ571896
 * chr15:102295553G>A
 *</P>
 */
@Test public void testNcRnaExonicVar484() throws AnnotationException  {
	byte chr = 15;
	int pos = 102295553;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DQ571896",annot);
	}
}

/**
 *<P>
 * annovar: AK126539
 * chr16:11542894C>T
 *</P>
 */
@Test public void testNcRnaExonicVar491() throws AnnotationException  {
	byte chr = 16;
	int pos = 11542894;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AK126539",annot);
	}
}

/**
 *<P>
 * annovar: UNQ5810
 * chr16:19315621C>G
 *</P>
 */
@Test public void testNcRnaExonicVar495() throws AnnotationException  {
	byte chr = 16;
	int pos = 19315621;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("UNQ5810",annot);
	}
}

/**
 *<P>
 * annovar: HTA
 * chr16:73126858T>G
 *</P>
 */
@Test public void testNcRnaExonicVar502() throws AnnotationException  {
	byte chr = 16;
	int pos = 73126858;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HTA",annot);
	}
}

/**
 *<P>
 * annovar: CCDC144C
 * chr17:20224624CTACTG>-
 *</P>
 */
@Test public void testNcRnaExonicVar515() throws AnnotationException  {
	byte chr = 17;
	int pos = 20224624;
	String ref = "CTACTG";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CCDC144C",annot);
	}
}

/**
 *<P>
 * annovar: LOC440434
 * chr17:36353761C>T
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC440434",annot);
	}
}

/**
 *<P>
 * annovar: FLJ43826
 * chr17:37188445TG>-
 *</P>
 */
@Test public void testNcRnaExonicVar521() throws AnnotationException  {
	byte chr = 17;
	int pos = 37188445;
	String ref = "TG";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FLJ43826",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("mir-133b",annot);
	}
}

/**
 *<P>
 * annovar: BC042382
 * chr18:28711519C>T
 *</P>
 */
@Test public void testNcRnaExonicVar531() throws AnnotationException  {
	byte chr = 18;
	int pos = 28711519;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BC042382",annot);
	}
}

/**
 *<P>
 * annovar: LOC400657
 * chr18:72264473C>T
 *</P>
 */
@Test public void testNcRnaExonicVar533() throws AnnotationException  {
	byte chr = 18;
	int pos = 72264473;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC400657",annot);
	}
}

/**
 *<P>
 * annovar: AL137752
 * chr19:36243813G>A
 *</P>
 */
@Test public void testNcRnaExonicVar540() throws AnnotationException  {
	byte chr = 19;
	int pos = 36243813;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AL137752",annot);
	}
}

/**
 *<P>
 * annovar: BC039524
 * chr19:37064240A>G
 *</P>
 */
@Test public void testNcRnaExonicVar541() throws AnnotationException  {
	byte chr = 19;
	int pos = 37064240;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("BC039524",annot);
	}
}

/**
 *<P>
 * annovar: CYP2G1P
 * chr19:41405962T>G
 *</P>
 */
@Test public void testNcRnaExonicVar544() throws AnnotationException  {
	byte chr = 19;
	int pos = 41405962;
	String ref = "T";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CYP2G1P",annot);
	}
}

/**
 *<P>
 * annovar: ZNF137P
 * chr19:53099975C>G
 *</P>
 */
@Test public void testNcRnaExonicVar549() throws AnnotationException  {
	byte chr = 19;
	int pos = 53099975;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF137P",annot);
	}
}

/**
 *<P>
 * annovar: LOC147804
 * chr19:53945618A>C
 *</P>
 */
@Test public void testNcRnaExonicVar552() throws AnnotationException  {
	byte chr = 19;
	int pos = 53945618;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC147804",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FAM182B",annot);
	}
}

/**
 *<P>
 * annovar: AX746653
 * chr20:47242413->GGG
 *</P>
 */
@Test public void testNcRnaExonicVar567() throws AnnotationException  {
	byte chr = 20;
	int pos = 47242413;
	String ref = "-";
	String alt = "GGG";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AX746653",annot);
	}
}

/**
 *<P>
 * annovar: AK097866
 * chr20:60293919C>A
 *</P>
 */
@Test public void testNcRnaExonicVar569() throws AnnotationException  {
	byte chr = 20;
	int pos = 60293919;
	String ref = "C";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AK097866",annot);
	}
}

/**
 *<P>
 * annovar: ANKRD20A11P
 * chr21:15326405C>T
 *</P>
 */
@Test public void testNcRnaExonicVar577() throws AnnotationException  {
	byte chr = 21;
	int pos = 15326405;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ANKRD20A11P",annot);
	}
}

/**
 *<P>
 * annovar: C21orf88
 * chr21:40977826A>G
 *</P>
 */
@Test public void testNcRnaExonicVar581() throws AnnotationException  {
	byte chr = 21;
	int pos = 40977826;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C21orf88",annot);
	}
}

/**
 *<P>
 * annovar: AK131325
 * chr22:22348689A>G
 *</P>
 */
@Test public void testNcRnaExonicVar595() throws AnnotationException  {
	byte chr = 22;
	int pos = 22348689;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("AK131325",annot);
	}
}

/**
 *<P>
 * annovar: CES5AP1
 * chr22:23712647A>C
 *</P>
 */
@Test public void testNcRnaExonicVar626() throws AnnotationException  {
	byte chr = 22;
	int pos = 23712647;
	String ref = "A";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CES5AP1",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("INGX",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TTTY11",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.ncRNA_EXONIC,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TTTY13",annot);
	}
}






}