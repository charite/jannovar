package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;

/**
 * Tests for the HGVSParserDriver for parsing nucleotide insertions.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserDriverNucleotideInsertionTest {

	HGVSParser driver;

	@Before
	public void setUp() throws Exception {
		driver = new HGVSParser(false);
	}

	@Test
	public void testPredictedOnly() {
		String hgvsStrings[] = new String[] { "NM_000138.4:c.(247_248ins)", "NM_000138.4:c.(247+1_247+2ins)",
				"NM_000138.4:c.(247-3_247-2ins)", "NM_000138.4:c.(*247_*247+1ins)", "NM_000138.4:c.(-247_-247-1ins)" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithoutSequence() {
		String hgvsStrings[] = new String[] { "NM_000138.4:c.247_248ins", "NM_000138.4:c.247+1_247+2ins",
				"NM_000138.4:c.247-3_247-2ins", "NM_000138.4:c.*247_*247+1ins", "NM_000138.4:c.-247_-247-1ins" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithSequence() {
		String hgvsStrings[] = new String[] { "NM_000138.4:c.247_248insATA", "NM_000138.4:c.247+1_247+2insAT",
				"NM_000138.4:c.247-3_247-2insCAT", "NM_000138.4:c.*247_*247+1insATAC",
				"NM_000138.4:c.-247_-247-1insACGT" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithSequenceLength() {
		String hgvsStrings[] = new String[] { "NM_000138.4:c.247_248ins3", "NM_000138.4:c.247+1_247+2ins2",
				"NM_000138.4:c.247-3_247-2ins3", "NM_000138.4:c.*247_*247+1ins4", "NM_000138.4:c.-247_-247-1ins4" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

}
