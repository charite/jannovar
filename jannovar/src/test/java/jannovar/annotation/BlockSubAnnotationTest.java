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
import jannovar.io.AnnovarParser;
import jannovar.reference.KnownGene;
import jannovar.reference.Chromosome;
import jannovar.annotation.Annotation;
import jannovar.exome.Variant;
import jannovar.exception.AnnotationException;


import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Assert;


/**
 * This class is intended to perform unuit testing on variants that
 * are intergenic. 
 */
public class BlockSubAnnotationTest implements Constants {

    
   
    private static HashMap<Byte,Chromosome> chromosomeMap = null;

  

    @SuppressWarnings (value="unchecked")
    @BeforeClass 
    public static void setUp() throws IOException {
	HashMap<String,KnownGene> kgMap=null;
	// The following file must be created prior to running this test
	
	try {
	     java.net.URL url = BlockSubAnnotationTest.class.getResource("/ucsc.ser");
	    String path = url.getPath();
	    FileInputStream fileIn = new FileInputStream(path);
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
	Annotation ann =c.getAnnotation(pos,ref,alt); 
	VariantType varType = ann.getVariantType();
	Assert.assertEquals(VariantType.NON_FS_SUBSTITUTION,varType);
	String annot = ann.getVariantAnnotation();
	Assert.assertEquals("LOC100132247(uc002djq.3:exon7:c.993_1029TCCACCCTCAGCTCTACCCTCAGCG,uc010vbn.1:exon8:c.1050_1086TCCACCCTCAGCTCTACCCTCAGCG,uc002djr.3:exon9:c.1050_1086TCCACCCTCAGCTCTACCCTCAGCG)",annot);
	}
}






}