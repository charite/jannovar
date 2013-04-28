package jannovar.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import jannovar.reference.TranscriptModel;
import jannovar.io.UCSCKGParser;
import jannovar.common.Constants;



import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

/**
 * Tests the UCSC Parser by creating a temporary file and making sure we get the correct
 * information from the known genes represented in this file.
 */
public class UCSCKGParserTest implements Constants {

    private static UCSCKGParser parser = null;
   
    private static HashMap<String,TranscriptModel> knownGeneMap=null;

    /** The following are the indices in the array list of the genes to be tested. */
    public static final int UC009VIS=2;
    public static final int UC001ZWX=1;

    @BeforeClass public static void setUp() throws IOException {
	File tmp = File.createTempFile("ucsckg-test","ucsckg-test");
	PrintStream ps = new PrintStream(new FileOutputStream(tmp));
	ps.append("uc021olp.1	chr1	-	38674705	38680439	38677458	38678111	4	38674705,38677405,38677769,38680388,	38676494,38677494,38678123,38680439,		uc021olp.1\n");
	ps.append("uc001zwx.2	chr15	-	48700502	48937985	48703186	48936966	66	48700502,48704765,48707732,48712883,48713754,48714148,48717565,48717935,48719763,48720542,48722867,48725062,48726790,48729157,48729518,48729964,48733917,48736737,48737572,48738902,48740964,48744758,48748833,48752442,48755278,48756095,48757764,48757986,48760134,48760608,48762830,48764747,48766451,48766724,48773851,48776014,48777570,48779271,48779508,48780309,48780564,48782047,48784657,48786400,48787319,48787665,48788296,48789462,48791181,48795983,48797221,48800778,48802240,48805745,48807583,48808379,48812855,48818326,48826276,48829807,48888479,48892335,48902924,48905206,48936802,48937771,	48703576,48704940,48707964,48713003,48713883,48714265,48717688,48718061,48719970,48720668,48722999,48725185,48726910,48729274,48729584,48730114,48734043,48736857,48737701,48739019,48741090,48744881,48748959,48752514,48755437,48756218,48757890,48758055,48760299,48760731,48762953,48764873,48766574,48766847,48773977,48776140,48777693,48779397,48779634,48780438,48780690,48782275,48784783,48786451,48787457,48787785,48788422,48789588,48791235,48796136,48797344,48800901,48802366,48805865,48807724,48808559,48813014,48818452,48826402,48830005,48888575,48892431,48903023,48905289,48937147,48937985,	D2JYH6	uc001zwx.2\n");

	ps.append("uc009vis.3	chr1	-	14361	16765	14361	14361	4	14361,14969,15795,16606,	14829,15038,15942,16765,		uc009vis.3\n");
	ps.append("uc009viv.2	chr1	-	14406	29370	14406	14406	7	14406,16857,17232,17605,17914,24737,29320,	16765,17055,17368,17742,18061,24891,29370,		uc009viv.2\n");
	ps.append("uc003xub.3	chr8	-	61177380	61193954	61178438	61193706	3	61177380,61192247,61193606,	61178608,61192439,61193954,	NP_004047	uc003xub.3\n");
	ps.close();
	//String mypath="/home/peter/data/ucsc/knownGene.txt";
	knownGeneMap = new HashMap<String,TranscriptModel>();


	parser = new UCSCKGParser(tmp.getAbsolutePath());
	parser.parseFile();
	Iterator<TranscriptModel> iter =  parser.getKnownGeneMap().values().iterator();
	while (iter.hasNext()) {
	    TranscriptModel k = iter.next();
	    String name = k.getName();
	    knownGeneMap.put(name,k);
	}
	
    }

    @AfterClass public static void releaseResources() { 
	knownGeneMap = null;
	parser = null;
	System.gc();
    }

    @Test public void testSizeOfKnownGeneMap() {
	int N = knownGeneMap.size();
	Assert.assertEquals(5,N);	  
    }

    /**
     *uc009vis.3 is the gene WASH7P and has four exons */
    @Test public void test_uc009vis_ExonCount() {
	TranscriptModel kgl = knownGeneMap.get("uc009vis.3");
	if (kgl==null) {
	    Assert.fail("Could not find uc009vis.3");
	}
	int N = kgl.getExonCount();
	Assert.assertEquals(4,N);

    }

    /** uc009vis.3 is the pseudogene WASH7P; the CDS Start is the same as teh CDS end, which is a signal that this
	gene is not a protein coding gene. */
    @Test public void test_uc009visCDSSize() {
	TranscriptModel kgl = knownGeneMap.get("uc009vis.3");
	int ORFsize = kgl.getCDSLength();
	Assert.assertEquals(0,ORFsize);
    }

    /** uc009vis.3 is the pseudogene WASH7P; according to the knownGene file, its RNA size must be 843.
	There is a discrepancy on the website. */
    @Test public void test_uc009vis_mRNASize() {
	TranscriptModel kgl = knownGeneMap.get("uc009vis.3");
	int N = kgl.getMRNALength();
	Assert.assertEquals(843,N);
    }

    @Test public void test_uc001zwx_ExonCount() {
	TranscriptModel kgl = knownGeneMap.get("uc001zwx.2");
	int N = kgl.getExonCount();
	Assert.assertEquals(66,N);
    }

    @Test public void test_uc001zwxCDSSize() {
	TranscriptModel kgl = knownGeneMap.get("uc001zwx.2");
	int ORFsize = kgl.getCDSLength();
	Assert.assertEquals(8616,ORFsize);

    }

     @Test public void test_uc001zwxMRNASize() {
	TranscriptModel kgl = knownGeneMap.get("uc001zwx.2");
	int size = kgl.getMRNALength();
	Assert.assertEquals(11695,size);
    }

    /** uc003xub.3 is CA8 */
    @Test public void test_CA8_MRNASize() {
	TranscriptModel kgl = knownGeneMap.get("uc003xub.3");
	int size = kgl.getMRNALength();
	Assert.assertEquals(1768,size);
    }

    /** uc003xub.3 is CA8 */
    @Test public void test_CA8_CDSSize() {
	TranscriptModel kgl = knownGeneMap.get("uc003xub.3");
	int size = kgl.getCDSLength();
	Assert.assertEquals(462,size);
    }

}

