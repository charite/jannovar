package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;

/**
 * Tests for the HGVSParserDriver for parsing nucleotide duplications.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserDriverNucleotideDuplicationTest {

	HGVSParser driver;

	@Before
	public void setUp() throws Exception {
		driver = new HGVSParser(false);
	}

	@Test
	public void testPredictedOnly() {
		String hgvsStrings[] = new String[] { "NM_000138.4:c.(247_248dup)", "NM_000138.4:c.(247+1_247+3dup)",
				"NM_000138.4:c.(247-3_247-1dup)", "NM_000138.4:c.(*247_*247+3dup)", "NM_000138.4:c.(-247_-247-3dup)" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithoutSequence() {
		String hgvsStrings[] = new String[] { "NM_000138.4:c.247_248dup", "NM_000138.4:c.247+1_247+3dup",
				"NM_000138.4:c.247-3_247-1dup", "NM_000138.4:c.*247_*247+3dup", "NM_000138.4:c.-247_-247-3dup" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithSequence() {
		String hgvsStrings[] = new String[] { "NM_000138.4:c.247_248dupAT", "NM_000138.4:c.247+1_247+3dupATA",
				"NM_000138.4:c.247-3_247-1dupATA", "NM_000138.4:c.*247_*247+3dupATAT",
				"NM_000138.4:c.-247_-247-3dupATAT" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithSequenceLength() {
		String hgvsStrings[] = new String[] { "NM_000138.4:c.247_248dup2", "NM_000138.4:c.247+1_247+3dup3",
				"NM_000138.4:c.247-3_247-1dup3", "NM_000138.4:c.*247_*247+3dup4", "NM_000138.4:c.-247_-247-3dup4" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

}
