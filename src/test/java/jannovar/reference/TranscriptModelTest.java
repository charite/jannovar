package jannovar.reference;


import jannovar.common.Constants;
import jannovar.exception.KGParseException;
import jannovar.io.UCSCKGParser;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class TranscriptModelTest implements Constants {

    static TranscriptModel uc0210lp = null;
    static TranscriptModel uc010wrv = null;


      @BeforeClass public static void setUp() throws KGParseException {
	  String dummy = "";
	  UCSCKGParser parser = new UCSCKGParser(dummy);
	  /* Note that uc021olp. is TESK2. */
	  uc0210lp = parser.parseTranscriptModelFromLine("uc021olp.1	chr1	-	38674705	38680439	38677458	38678111	4	38674705,38677405,38677769,38680388,	38676494,38677494,38678123,38680439,		uc021olp.1\n");
	  uc010wrv = parser.parseTranscriptModelFromLine("uc010wrv.1	chr17	+	73201596	73231854	73205928	73231774	18	73201596,73205917,73208086,73209170,73211848,73214279,73221197,73221436,73221792,73222145,73227434,73227667,73227922,73228945,73229152,73230731,73231194,73231672,	73201889,73206080,73208157,73209214,73211918,73214401,73221332,73221559,73221924,73222252,73227518,73227733,73228074,73229063,73229253,73230883,73231296,73231854,	Q9BW27	uc010wrv.1");

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
   
    /** This is TESTK2 */
     @Test public void test_coding_uc0210lp() {
	boolean b = uc0210lp.isCodingGene();
	Assert.assertEquals(true,b);	  
    }
    
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
