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


import jannovar.io.UCSCKGParser;
import jannovar.common.Constants;
import jannovar.io.AnnovarParser;
import jannovar.reference.KnownGene;
import jannovar.reference.Chromosome;
import jannovar.reference.Annotation;
import jannovar.exome.Variant;
import jannovar.exception.AnnotationException;


import org.junit.Test;
import org.junit.BeforeClass;
import junit.framework.Assert;


/**
 * This class is intended to perform unuit testing on variants that
 * are intergenic. 
 */
public class UpstreamAnnotationTest implements Constants {

    
   
    private static HashMap<Byte,Chromosome> chromosomeMap = null;

  

    @SuppressWarnings (value="unchecked")
    @BeforeClass 
    public static void setUp() throws IOException {
	HashMap<String,KnownGene> kgMap=null;
	// The following file must be created prior to running this test
	String serializedFile = "./ucsc.ser"; //getClass().getResourceAsStream("/data.txt")
	try {
	    FileInputStream fileIn =new FileInputStream("/home/peter/SVN/apps/NGSanalysis/jannovar/src/test/resources/ucsc.ser");
		//new FileInputStream(UpstreamAnnotationTest.class.getResource("ucsc.ser").getPath());
	    //InputStream ins = new InputStream (UpstreamAnnotationTest.class.getResourceAsStream("ucsc.ser") );
	     ObjectInputStream in = new ObjectInputStream(fileIn);
	     kgMap = (HashMap<String,KnownGene>) in.readObject();
	     in.close();
            fileIn.close();
	    //ins.close();
	} catch(IOException i) {
            i.printStackTrace();
	    System.err.println("Could not deserialize knownGeneMap");
	    System.exit(1);
           
        } catch(ClassNotFoundException c) {
            System.out.println("Could not find HashMap<String,KnownGene> class.");
            c.printStackTrace();
            System.exit(1);
        }
	//System.out.println("Done deserialization, size of map is " + kgMap.size());
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


/**
 *<P>
 * annovar: C1orf167
 * chr1:11831615C>T
 *</P>
 */
@Test public void testUpstreamVar1() throws AnnotationException  {
	byte chr = 1;
	int pos = 11831615;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C1orf167",annot);
	}
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
	   
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C1orf167",annot);
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
	    
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KIAA2013",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PADI6",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C8B",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("Mir_548",annot);
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
	    
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SAMD13",annot);
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
	   
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CLCA3P",annot);
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
	    
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ADAR",annot);
	}
}

/**
 *<P>
 * annovar: ZNF847P
 * chr1:227885726G>A
 *</P>
 --- annovar mistaken, this is an intron of a oprotein coding gene.
 
@Test public void testUpstreamVar10() throws AnnotationException  {
	byte chr = 1;
	int pos = 227885726;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    
	    int N = anno_list.size();
	    Assert.assertEquals(1,N);
	    Annotation ann = anno_list.get(0);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF847P",annot);
	}
}
*/

/**
 *<P>
 * annovar: C1orf150
 * chr1:247712303T>C
 *</P>
 -- annovar mistaken, this is an intron of a mRNA
@Test public void testUpstreamVar11() throws AnnotationException  {
	byte chr = 1;
	int pos = 247712303;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    
	    int N = anno_list.size();
	    Assert.assertEquals(1,N);
	    Annotation ann = anno_list.get(0);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("C1orf150",annot);
	}
}
*/

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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR1C1",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR11L1",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("OR2T4",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("Mir_548",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("LOC388946",annot);
	}
}

/**
 *<P>
 * annovar: ZNF2
 * chr2:95830742->A
 *</P>
 */
@Test public void testUpstreamVar17() throws AnnotationException  {
	byte chr = 2;
	int pos = 95830742;
	String ref = "-";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF2",annot);
	}
}

/**
 *<P>
 * annovar: ZNF2
 * chr2:95830742->A
 *</P>
 */
@Test public void testUpstreamVar18() throws AnnotationException  {
	byte chr = 2;
	int pos = 95830742;
	String ref = "-";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF2",annot);
	}
}

/**
 *<P>
 * annovar: ZNF2
 * chr2:95830742->A
 *</P>
 */
@Test public void testUpstreamVar19() throws AnnotationException  {
	byte chr = 2;
	int pos = 95830742;
	String ref = "-";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ZNF2",annot);
	}
}

/**
 *<P>
 * annovar: DQ580140
 * chr2:131414176C>T
 *</P>
 */
@Test public void testUpstreamVar20() throws AnnotationException  {
	byte chr = 2;
	int pos = 131414176;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DQ580140",annot);
	}
}


/**
 *<P>
 * annovar: CYBRD1
 * chr2:172378818A>G
 *</P>
 */
@Test public void testUpstreamVar21() throws AnnotationException  {
	byte chr = 2;
	int pos = 172378818;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CYBRD1",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SF3B1",annot);
	}
}

/**
 *<P>
 * annovar: KLF7
 * chr2:208031796C>G
 *</P>
 */
@Test public void testUpstreamVar23() throws AnnotationException  {
	byte chr = 2;
	int pos = 208031796;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KLF7",annot);
	}
}

/**
 *<P>
 * annovar: XRCC5
 * chr2:216973814C>T
 *</P>
 */
@Test public void testUpstreamVar24() throws AnnotationException  {
	byte chr = 2;
	int pos = 216973814;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("XRCC5",annot);
	}
}

/**
 *<P>
 * annovar: WDR69
 * chr2:228735448G>A
 *</P>
 */
@Test public void testUpstreamVar25() throws AnnotationException  {
	byte chr = 2;
	int pos = 228735448;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("WDR69",annot);
	}
}

/**
 *<P>
 * annovar: HDAC4
 * chr2:240323906->G
 *</P>
 */
@Test public void testUpstreamVar26() throws AnnotationException  {
	byte chr = 2;
	int pos = 240323906;
	String ref = "-";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("HDAC4",annot);
	}
}

/**
 *<P>
 * annovar: RYBP
 * chr3:72495777C>G
 *</P>
 */
@Test public void testUpstreamVar27() throws AnnotationException  {
	byte chr = 3;
	int pos = 72495777;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RYBP",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ROBO2",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("CEP97",annot);
	}
}

/**
 *<P>
 * annovar: SENP2
 * chr3:185300395G>A
 *</P>
 */
@Test public void testUpstreamVar30() throws AnnotationException  {
	byte chr = 3;
	int pos = 185300395;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SENP2",annot);
	}
}


/**
 *<P>
 * annovar: PYDC2
 * chr3:191178925G>A
 *</P>
 */
@Test public void testUpstreamVar31() throws AnnotationException  {
	byte chr = 3;
	int pos = 191178925;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PYDC2",annot);
	}
}

/**
 *<P>
 * annovar: ATP13A5
 * chr3:193096529C>T
 *</P>
 */
@Test public void testUpstreamVar32() throws AnnotationException  {
	byte chr = 3;
	int pos = 193096529;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ATP13A5",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FGFBP2",annot);
	}
}

/**
 *<P>
 * annovar: DQ593719
 * chr4:49561616G>C
 *</P>
 */
@Test public void testUpstreamVar34() throws AnnotationException  {
	byte chr = 4;
	int pos = 49561616;
	String ref = "G";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DQ593719",annot);
	}
}

/**
 *<P>
 * annovar: DQ593719
 * chr4:49561626TC>-
 *</P>
 */
@Test public void testUpstreamVar35() throws AnnotationException  {
	byte chr = 4;
	int pos = 49561626;
	String ref = "TC";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DQ593719",annot);
	}
}

/**
 *<P>
 * annovar: DQ596041
 * chr5:98861045->CAGG
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("DQ596041",annot);
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
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("GRXCR2",annot);
	}
}

/**
 *<P>
 * annovar: FAT2
 * chr5:150948537T>C
 *</P>
 */
@Test public void testUpstreamVar38() throws AnnotationException  {
	byte chr = 5;
	int pos = 150948537;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt);;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("FAT2",annot);
	}
}

/**
 *<P>
 * annovar: F13A1
 * chr6:6321128T>C
 *</P>
 */
@Test public void testUpstreamVar39() throws AnnotationException  {
	byte chr = 6;
	int pos = 6321128;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    Annotation ann = c.getAnnotation(pos,ref,alt); ;
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.UPSTREAM,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("F13A1",annot);
	}
}


}