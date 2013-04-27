package jannovar.reference;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.HashMap;

import jannovar.reference.KnownGene;
import jannovar.io.UCSCKGParser;
import jannovar.common.Constants;
import jannovar.exception.KGParseException;


import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;


public class KnownGeneTest implements Constants {

    static KnownGene uc0210lp = null;
    static KnownGene uc010wrv = null;


      @BeforeClass public static void setUp() throws KGParseException {
	  /* Note that uc021olp. is a non-coding RNA. */
	  uc0210lp = new KnownGene("uc021olp.1	chr1	-	38674705	38680439	38677458	38678111	4	38674705,38677405,38677769,38680388,	38676494,38677494,38678123,38680439,		uc021olp.1\n");
	  uc010wrv = new KnownGene("uc010wrv.1	chr17	+	73201596	73231854	73205928	73231774	18	73201596,73205917,73208086,73209170,73211848,73214279,73221197,73221436,73221792,73222145,73227434,73227667,73227922,73228945,73229152,73230731,73231194,73231672,	73201889,73206080,73208157,73209214,73211918,73214401,73221332,73221559,73221924,73222252,73227518,73227733,73228074,73229063,73229253,73230883,73231296,73231854,	Q9BW27	uc010wrv.1");

      }

    @AfterClass public static void releaseResources() { 
	uc0210lp = null;
	uc010wrv = null;
	System.gc();
    }


    @Test public void testExonCount_uc0210lp() {
	int N = uc0210lp.getExonCount();
	Assert.assertEquals(4,N);	  
    }

 

    @Test public void testCDSLength_uc0210lp() {
	int N = uc0210lp.getCDSLength();
	Assert.assertEquals(378,N);	  
    }
    /* TODO
     @Test public void test_rcdsstart_uc0210lp() {
	int N = uc0210lp.getRefCDSStart();
	Assert.assertEquals(2303,N);	  
    }
    */
    @Test public void testChromosome_uc0210lp() {
	byte C = uc0210lp.getChromosome();
	Assert.assertEquals(1,C);	  
    }

    

    @Test public void testExonCount_uc010wrv() {
	int N = uc010wrv.getExonCount();
	Assert.assertEquals(18,N);
    }
    
    /**
     * The 5' UTR of this isoform is 304 nucleotides long, thus the
     * rcdsstart (start fo CDS in reference) is 305.
     */
    @Test public void test_rcdsstart_uc010wrv() {
	int r = uc010wrv.getRefCDSStart();
	Assert.assertEquals(305,r);
    }
}
