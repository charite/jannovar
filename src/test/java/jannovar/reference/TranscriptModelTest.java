package jannovar.reference;

import jannovar.common.Constants;
import jannovar.exception.KGParseException;
import jannovar.io.UCSCKGParser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the TranscriptModel class.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptModelTest implements Constants {

	// Fixture for genome sequence of shorter gene.
	String LONG_STRING;

	// Fixtures for transcripts.
	TranscriptModel uc0210lp = null;
	TranscriptModel uc010wrv = null;

	/**
	 * Setup this.uc0210lp and thisuc010wrv.
	 */
	@Before
	public void setUp() throws KGParseException {
		String dummy = "";
		UCSCKGParser parser = new UCSCKGParser(dummy);
		
		StringBuilder builder = new StringBuilder();
		char[] chars = {'A', 'C', 'G', 'T'};
		for (int i = 0; i < 5734; ++i)
			builder.append(chars[i % 4]);
		LONG_STRING = builder.toString();

		// Note that uc021olp. is TESK2.
		this.uc0210lp = parser.parseTranscriptModelFromLine(
				"uc021olp.1	chr1	-	38674705	38680439	38677458	38678111	4	" +
				"38674705,38677405,38677769,38680388,	38676494,38677494,38678123,38680439,	" +
				"	uc021olp.1\n");
		this.uc010wrv = parser.parseTranscriptModelFromLine(
				"uc010wrv.1	chr17	+	73201596	73231854	73205928	73231774	18	" +
				"73201596,73205917,73208086,73209170,73211848,73214279,73221197,73221436,73221792," +
				"73222145,73227434,73227667,73227922,73228945,73229152,73230731,73231194,73231672,	" +
				"73201889,73206080,73208157,73209214,73211918,73214401,73221332,73221559,73221924,73222252," +
				"73227518,73227733,73228074,73229063,73229253,73230883,73231296,73231854,	Q9BW27	uc010wrv.1");
	}

	/** Test basic properties parsed from input. */
	@Test
	public void testBasicProperties_uc0210lp() {
		Assert.assertEquals(1, uc0210lp.getChromosome());
		Assert.assertEquals("chr1", uc0210lp.getChromosomeAsString());
		Assert.assertEquals('-', uc0210lp.getStrand());
		Assert.assertEquals(38674706, uc0210lp.getTXStart());
		Assert.assertEquals(38680439, uc0210lp.getTXEnd());
		Assert.assertEquals(38677459, uc0210lp.getCDSStart());
		Assert.assertEquals(38678111, uc0210lp.getCDSEnd());
		Assert.assertEquals(378, uc0210lp.getCDSLength());
		Assert.assertEquals(64, uc0210lp.getRefCDSStart());
		Assert.assertEquals(441, uc0210lp.getRefCDSEnd());
		
		Assert.assertEquals(4, uc0210lp.getExonCount());
		Assert.assertEquals(38674706, uc0210lp.getExonStart(0));
		Assert.assertEquals(38677406, uc0210lp.getExonStart(1));
		Assert.assertEquals(38677770, uc0210lp.getExonStart(2));
		Assert.assertEquals(38680389, uc0210lp.getExonStart(3));
		Assert.assertEquals(38676494, uc0210lp.getExonEnd(0));
		Assert.assertEquals(38677494, uc0210lp.getExonEnd(1));
		Assert.assertEquals(38678123, uc0210lp.getExonEnd(2));
		Assert.assertEquals(38680439, uc0210lp.getExonEnd(3));
		
		Assert.assertEquals(1789, uc0210lp.getLengthOfExon(0));
		Assert.assertEquals(89, uc0210lp.getLengthOfExon(1));
		Assert.assertEquals(354, uc0210lp.getLengthOfExon(2));
		Assert.assertEquals(51, uc0210lp.getLengthOfExon(3));

		Assert.assertEquals(0, uc0210lp.getLengthOfIntron(0));
		Assert.assertEquals(911, uc0210lp.getLengthOfIntron(1));
		Assert.assertEquals(275, uc0210lp.getLengthOfIntron(2));
		Assert.assertEquals(2265, uc0210lp.getLengthOfIntron(3));
		Assert.assertEquals(0, uc0210lp.getLengthOfIntron(4));
		
		Assert.assertEquals(2283, uc0210lp.getMRNALength());
		Assert.assertEquals(378, uc0210lp.getCDSLength());
		
		Assert.assertEquals("uc021olp.1", uc0210lp.getGeneSymbol());
		Assert.assertEquals("uc021olp.1", uc0210lp.getAccessionNumber());
	}
	
	/** Test sequence accessors and query functions */
	@Test
	public void testSequence_uc0210lp() {
		uc0210lp.setSequence(LONG_STRING);
		Assert.assertEquals(LONG_STRING, uc0210lp.getCdnaSequence());
		Assert.assertEquals(5734, uc0210lp.getActualSequenceLength());
		
		Assert.assertEquals(LONG_STRING.substring(63, 441), uc0210lp.getCodingSequence());
		Assert.assertEquals(LONG_STRING.substring(63), uc0210lp.getCodingSequencePlus3UTR());
	}

	/** Test gene ID accessors and query functions */
	@Test
	public void testGeneID_uc0210lp() {
		Assert.assertEquals(TranscriptModel.UNINITIALIZED_INT, uc0210lp.getGeneID());
		uc0210lp.setGeneID(42);
		Assert.assertEquals(42, uc0210lp.getGeneID());
	}
	
	/** Test gene symbol accessors and query functions */
	@Test
	public void testGeneSymbol_uc0210lp() {
		Assert.assertEquals("uc021olp.1", uc0210lp.getGeneSymbol());
		uc0210lp.setGeneSymbol("<symbol>");
		Assert.assertEquals("<symbol>", uc0210lp.getGeneSymbol());
	}
	
	/** Test getCodonAt(). */
	@Test
	public void testGetCodonAt_uc0210lp() {
		uc0210lp.setSequence(LONG_STRING);
		Assert.assertEquals("ACG", uc0210lp.getCodonAt(65, 0));
		Assert.assertEquals("TAC", uc0210lp.getCodonAt(65, 1));
		Assert.assertEquals("GTA", uc0210lp.getCodonAt(65, 2));
	}
	
	/** Test getRVarStart() query. */
	@Test
	public void testRVarStart_uc0210lp() {
		Assert.assertEquals(1067, uc0210lp.getRVarStart(38675805, 33));
		Assert.assertEquals(1067, uc0210lp.getRVarStart(38675807, 35));
	}

	/** Test getRVarStart() query. */
	@Test
	public void testRVarEnd_uc0210lp() {
		Assert.assertEquals(5714, uc0210lp.getRVarEnd(38675805, 33, 20));
		Assert.assertEquals(5714, uc0210lp.getRVarEnd(38675807, 35, 20));
	}

	/** Test isNear* queries. */
	@Test
	public void testIsNearQueries_uc0210lp() {
		Assert.assertFalse(uc0210lp.isThreePrimeToGene(38680438));  // left of 3' end
		Assert.assertFalse(uc0210lp.isThreePrimeToGene(38680439));  // at 3' end
		Assert.assertTrue(uc0210lp.isThreePrimeToGene(38680440));   // right of 3' end

		Assert.assertTrue(uc0210lp.isFivePrimeToGene(38674705));    // left of 3' end
		Assert.assertFalse(uc0210lp.isFivePrimeToGene(38674706));   // at 3' end
		Assert.assertFalse(uc0210lp.isFivePrimeToGene(38674707));   // right of 3' end
	}

	/** Test is{Three,Five}* queries. */
	@Test
	public void testIsThreeFiveQueries_uc0210lp() {
		Assert.assertFalse(uc0210lp.isNearThreePrimeEnd(38680438, 0));
		Assert.assertFalse(uc0210lp.isNearThreePrimeEnd(38680438, 1));
		Assert.assertFalse(uc0210lp.isNearThreePrimeEnd(38680439, 1));
		Assert.assertFalse(uc0210lp.isNearThreePrimeEnd(38680439, 0));

		Assert.assertFalse(uc0210lp.isNearFivePrimeEnd(38674705, 0));
		Assert.assertFalse(uc0210lp.isNearFivePrimeEnd(38674705, 1));
		Assert.assertFalse(uc0210lp.isNearFivePrimeEnd(38674706, 1));
		Assert.assertFalse(uc0210lp.isNearFivePrimeEnd(38674706, 0));
	}
	
	/** Test is* queries. */
	@Test
	public void testIsQueries_uc0210lp() {
		Assert.assertTrue(uc0210lp.isCodingGene());
		Assert.assertFalse(uc0210lp.isNonCodingGene());
	}
	
	/** Test getDistanceTo* methods. */
	@Test
	public void getDistanceTo_uc0210lp() {
		Assert.assertEquals(-2, uc0210lp.getDistanceToThreePrimeTerminus(38680437));
		Assert.assertEquals(-1, uc0210lp.getDistanceToThreePrimeTerminus(38680438));
		Assert.assertEquals(0, uc0210lp.getDistanceToThreePrimeTerminus(38680439));
		Assert.assertEquals(1, uc0210lp.getDistanceToThreePrimeTerminus(38680440));

		Assert.assertEquals(2, uc0210lp.getDistanceToFivePrimeTerminus(38674704));
		Assert.assertEquals(1, uc0210lp.getDistanceToFivePrimeTerminus(38674705));
		Assert.assertEquals(0, uc0210lp.getDistanceToFivePrimeTerminus(38674706));
		Assert.assertEquals(-1, uc0210lp.getDistanceToFivePrimeTerminus(38674707));
		
		Assert.assertEquals(2085, uc0210lp.getDistanceToFivePrimeTerminuscDNA(38674904));
		Assert.assertEquals(2022, uc0210lp.getDistanceToCDSstart(38674904));
		Assert.assertEquals(1645, uc0210lp.getDistanceToCDSend(38674904));
		uc0210lp.setStrand('+');  // switch strands
		Assert.assertEquals(299, uc0210lp.getDistanceToFivePrimeTerminuscDNA(38675004));
		Assert.assertEquals(1645, uc0210lp.getDistanceToCDSstart(38674904));
		Assert.assertEquals(2022, uc0210lp.getDistanceToCDSend(38674904));
	}
	
	/** Test getChromosomalCoordinates(). */
	@Test
	public void getChromosomalCoordinates_uc0210lp() {
		Integer[] arr = {new Integer(38677874), new Integer(38677974)};
		Assert.assertArrayEquals(arr, uc0210lp.getChromosomalCoordinates(200, 300));
	}
	
	/** Test getRefPosition(). */
	@Test
	public void getRefPosition_uc0210lp() {
		Assert.assertEquals(-1, uc0210lp.getRefPosition(38674705));    // intronic
		Assert.assertEquals(2283, uc0210lp.getRefPosition(38674706));  // exonic
		Assert.assertEquals(2282, uc0210lp.getRefPosition(38674707));  // exonic
	}
	
	/** Test parsing of slightly longer transcript with gene symbol. */
	@Test
	public void testSimple_uc010wrv() {
		Assert.assertEquals(18, uc010wrv.getExonCount());
		Assert.assertEquals(305, uc010wrv.getRefCDSStart());

		Assert.assertEquals("uc010wrv.1", uc010wrv.getGeneSymbol());
		Assert.assertEquals("uc010wrv.1", uc010wrv.getAccessionNumber());
	}
}
