package de.charite.compbio.jannovar.hgvs.parser;

import org.apache.logging.log4j.Level;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;

/**
 * Tests for the HGVSParserDriver for parsing nucleotide delitions.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserDriverNucleotideDeletionTest {

	HGVSParser driver;

	@Before
	public void setUp() throws Exception {
		driver = new HGVSParser(false);
	}

	@Test
	public void testPredictedOnly() {
		HGVSParsingTestBase.setLogLevel(Level.DEBUG);

		String hgvsStrings[] = new String[] { "NM_000138.4:c.(247_248del)", "NM_000138.4:c.(247+1_247+3del)",
				"NM_000138.4:c.(247-3_247-1del)", "NM_000138.4:c.(*247_*247+3del)",
				"NM_000138.4:c.(-247_-247-3del)" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithoutSequence() {
		String hgvsStrings[] = new String[] { "NM_000138.4:c.247_248del", "NM_000138.4:c.247+1_247+3del",
				"NM_000138.4:c.247-3_247-1del", "NM_000138.4:c.*247_*247+3del", "NM_000138.4:c.-247_-247-3del" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithSequence() {
		String hgvsStrings[] = new String[] { "NM_000138.4:c.247_248delAT",
				"NM_000138.4:c.247+1_247+3delATA", "NM_000138.4:c.247-3_247-1delATA",
				"NM_000138.4:c.*247_*247+3delATAT", "NM_000138.4:c.-247_-247-3delATAT" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithSequenceLength() {
		String hgvsStrings[] = new String[] { "NM_000138.4:c.247_248del2", "NM_000138.4:c.247+1_247+3del3",
				"NM_000138.4:c.247-3_247-1del3", "NM_000138.4:c.*247_*247+3del4",
				"NM_000138.4:c.-247_-247-3del4" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

}
