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
import jannovar.io.AnnovarParser;
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
 * This class is intended to perform unuit testing on splice variants.
 * <p>
 * Note that the file ucsc.ser must be created prior to using this test class
 * and should be put in src/test/resources/.
 */
public class SpliceAnnotationTest implements Constants {

    
   
    private static HashMap<Byte,Chromosome> chromosomeMap = null;

  

    @SuppressWarnings (value="unchecked")
    @BeforeClass 
    public static void setUp() throws IOException {
	HashMap<String,TranscriptModel> kgMap=null;
	try {
	     java.net.URL url = SynonymousAnnotationTest.class.getResource("/ucsc.ser");
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
 * annovar: PADI6(uc001bak.1:exon9:c.1026+2G>-)
 * chr1:17718674G>-
 *</P>
--- chokes on single base intron
@Test public void testSpliceVar4() throws AnnotationException  {
	byte chr = 1;
	int pos = 17718674;
	String ref = "G";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    ArrayList<Annotation> anno_list = c.getAnnotationList(pos,ref,alt);
	    int N = anno_list.size();
	    Assert.assertEquals(1,N);
	    AnnotationList ann = anno_list.get(0);
	    VariantType varType = ann.getVarType();
	    Assert.assertEquals(VariantType.SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("PADI6(uc001bak.1:exon9:c.1026+2G>-)",annot);
	}
} */

/**
 *<P>
 * annovar: KDM4A(uc001cjx.3:exon4:c.315-2A>-,uc010oki.2:exon4:c.315-2A>-)
 * chr1:44125967A>-
 *</P>
 */
@Test public void testSpliceVar7() throws AnnotationException  {
	byte chr = 1;
	int pos = 44125967;
	String ref = "A";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =  c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KDM4A(uc001cjx.3:exon4:c.315-2A>-,uc010oki.2:exon4:c.315-2A>-)",annot);
	}
}

/**
 *<P>
 * annovar: TCTEX1D1(uc001dcv.3:exon4:c.336+1G>A)
 * chr1:67242087G>A
 *</P>
 */
@Test public void testSpliceVar9() throws AnnotationException  {
	byte chr = 1;
	int pos = 67242087;
	String ref = "G";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("TCTEX1D1(uc001dcv.3:exon4:c.336+1G>A)",annot);
	}
}


/**
 *<P>

 *</P>
 */
@Test public void testSpliceVar2b() throws AnnotationException  {
	byte chr = 1;
	int pos = 5935162;
	String ref = "A";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NPHP4(uc001alq.2:exon22:c.2818-2T>A)",annot);
	}
}


@Test public void testSpliceVar3b() throws AnnotationException  {
	byte chr = 1;
	int pos = 35917393;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KIAA0319L(uc001byw.3:exon5:c.225-1G>A)",annot);
	}
}



@Test public void testSpliceVar4b() throws AnnotationException  {
	byte chr = 1;
	int pos = 44125967;
	String ref = "A";
	String alt = "-";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KDM4A(uc001cjx.3:exon4:c.315-2A>-,uc010oki.2:exon4:c.315-2A>-)",annot);
	}
}


@Test public void testSpliceVar1h() throws AnnotationException  {
	byte chr = 1;
	int pos = 155348070;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("ASH1L(uc001fkt.3:exon11:c.6332+2T>C,uc009wqq.3:exon11:c.6347+2T>C)",annot);
	}
}


@Test public void testSpliceVar2h() throws AnnotationException  {
	byte chr = 1;
	int pos = 156704287;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RRNAD1(uc001fpu.3:exon6:c.1121+2T>C)",annot);
	}
}


@Test public void testSpliceVar3h() throws AnnotationException  {
	byte chr = 1;
	int pos = 158064182;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("KIRREL(uc001fro.4:exon10:c.1239+2T>C,uc009wsq.3:exon11:c.1305+2T>C,uc010pib.2:exon12:c.1497+2T>C,uc001frn.4:exon14:c.1797+2T>C)",annot);
	}
}

@Test public void testSpliceVar4h() throws AnnotationException  {
	byte chr = 1;
	int pos = 212964870;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NSL1(uc010pti.2:exon2:c.234+2T>C,uc001hjm.3:exon2:c.234+2T>C,uc001hjn.3:exon2:c.234+2T>C)",annot);
	}
}

@Test public void testSpliceVar5h() throws AnnotationException  {
	byte chr = 1;
	int pos = 247419509;
	String ref = "T";
	String alt = "C";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("VN1R5(uc010pyu.2:exon1:c.135+1T>C)",annot);
	}
}


@Test public void testSpliceVar6h() throws AnnotationException  {
	byte chr = 2;
	int pos = 42871265;
	String ref = "A";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("MTA3(uc002rsp.1:exon6:c.214-2A>G,uc002rso.1:exon7:c.214-2A>G,uc002rsq.3:exon6:c.382-2A>G)",annot);
	}
}

@Test public void testSpliceVar7h() throws AnnotationException  {
	byte chr = 2;
	int pos = 85571472;
	String ref = "C";
	String alt = "G";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("RETSAT(uc010ysm.2:exon8:c.1074-1G>C,uc002spd.3:exon9:c.1257-1G>C)",annot);
	}
}


@Test public void testSpliceVar8h() throws AnnotationException  {
	byte chr = 2;
	int pos = 85662248;
	String ref = "T";
	String alt = "A";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("SH2D6(uc002spq.3:exon1:c.168+2T>A)",annot);
	}
}

@Test public void testSpliceVar9h() throws AnnotationException  {
	byte chr = 2;
	int pos = 110926131;
	String ref = "C";
	String alt = "T";
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos,ref,alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(VariantType.SPLICING,varType);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals("NPHP1(uc002tfo.4:exon5:c.337-1G>A,uc010fjv.1:exon7:c.523-1G>A,uc002tfl.4:exon7:c.523-1G>A,uc002tfn.4:exon7:c.523-1G>A,uc002tfm.4:exon7:c.523-1G>A,uc010ywx.2:exon7:c.523-1G>A)",annot);
	}
}




	

}
