package jannovar.reference;

import jannovar.common.Constants;
import jannovar.exception.KGParseException;
import jannovar.parse.UCSCParser;

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
	TranscriptInfoBuilder uc0210lp;
	TranscriptInfoBuilder uc010wrv;

	/**
	 * Setup this.uc0210lp and thisuc010wrv.
	 */
	@Before
	public void setUp() throws KGParseException {
		String dummy = "";
		UCSCParser parser = new UCSCParser(dummy);

		StringBuilder builder = new StringBuilder();
		char[] chars = { 'A', 'C', 'G', 'T' };
		for (int i = 0; i < 5734; ++i)
			builder.append(chars[i % 4]);
		LONG_STRING = builder.toString();

		// Note that uc021olp. is TESK2.
		this.uc0210lp = parser.parseTranscriptModelFromLine(
				"uc021olp.1	chr1	-	38674705	38680439	38677458	38678111	4	"
						+ "38674705,38677405,38677769,38680388,	38676494,38677494,38678123,38680439,	"
 + "	uc021olp.1\n");
		this.uc010wrv = parser.parseTranscriptModelFromLine(
				"uc010wrv.1	chr17	+	73201596	73231854	73205928	73231774	18	"
						+ "73201596,73205917,73208086,73209170,73211848,73214279,73221197,73221436,73221792,"
						+ "73222145,73227434,73227667,73227922,73228945,73229152,73230731,73231194,73231672,	"
						+ "73201889,73206080,73208157,73209214,73211918,73214401,73221332,73221559,73221924,73222252,"
						+ "73227518,73227733,73228074,73229063,73229253,73230883,73231296,73231854,	Q9BW27	uc010wrv.1");
	}

	/** Test basic properties parsed from input. */
	@Test
	public void testBasicProperties_uc0210lp() {
		TranscriptInfo uc0210lp = this.uc0210lp.make();

		Assert.assertEquals(1, uc0210lp.getChr());
		Assert.assertEquals('-', uc0210lp.getStrand());
		Assert.assertEquals(new GenomeInterval('+', 1, 38674706, 38680439, PositionType.ONE_BASED), uc0210lp.txRegion);
		Assert.assertEquals(new GenomeInterval('+', 1, 38677459, 38678111, PositionType.ONE_BASED), uc0210lp.cdsRegion);

		Assert.assertEquals(4, uc0210lp.exonRegions.size());
		Assert.assertEquals(new GenomeInterval('+', 1, 38674706, 38676494, PositionType.ONE_BASED),
				uc0210lp.exonRegions.get(0));
		Assert.assertEquals(new GenomeInterval('+', 1, 38677406, 38677494, PositionType.ONE_BASED),
				uc0210lp.exonRegions.get(1));
		Assert.assertEquals(new GenomeInterval('+', 1, 38677770, 38678123, PositionType.ONE_BASED),
				uc0210lp.exonRegions.get(2));
		Assert.assertEquals(new GenomeInterval('+', 1, 38680389, 38680439, PositionType.ONE_BASED),
				uc0210lp.exonRegions.get(3));

		Assert.assertEquals("uc021olp.1", uc0210lp.accession);
		Assert.assertEquals("uc021olp.1", uc0210lp.geneSymbol);
	}

	/** Test sequence accessors and query functions */
	@Test
	public void testSequence_uc0210lp() {
		this.uc0210lp.setSequence(LONG_STRING);
		TranscriptInfo uc0210lp = this.uc0210lp.make();

		Assert.assertEquals(LONG_STRING, uc0210lp.sequence);
		Assert.assertEquals(5734, uc0210lp.sequence.length());
	}

	/** Test gene ID accessors and query functions */
	@Test
	public void testGeneID_uc0210lp() {
		Assert.assertEquals(Constants.UNINITIALIZED_INT, uc0210lp.getGeneID());
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

	/** Test parsing of slightly longer transcript with gene symbol. */
	@Test
	public void testSimple_uc010wrv() {
		TranscriptInfo uc010wrv = this.uc010wrv.make();

		Assert.assertEquals(18, uc010wrv.exonRegions.size());

		Assert.assertEquals("uc010wrv.1", uc010wrv.geneSymbol);
		Assert.assertEquals("uc010wrv.1", uc010wrv.accession);
	}
}
