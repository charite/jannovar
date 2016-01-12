package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;

/**
 * Tests for the HGVSParserDriver for parsing nucleotide short sequence variabilities.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserDriverNucleotideShortSequenceVariabilityTest {

	HGVSParser driver;

	@Before
	public void setUp() throws Exception {
		driver = new HGVSParser(false);
	}

	@Test
	public void testOnlyPredicted() {
		String hgvsStrings[] = new String[] { "NM_000138.4:c.(247(1_6))", "NM_000138.4:c.(247+1(1_6))",
				"NM_000138.4:c.(247-1(1_6))", "NM_000138.4:c.(*247(1_6))", "NM_000138.4:c.(-247(1_6))" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithPointLocations() {
		String hgvsStrings[] = new String[] { "NM_000138.4:c.247(1_6)", "NM_000138.4:c.247+1(1_6)",
				"NM_000138.4:c.247-1(1_6)", "NM_000138.4:c.*247(1_6)", "NM_000138.4:c.-247(1_6)" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithRanges() {
		String hgvsStrings[] = new String[] { "NM_000138.4:c.247_248(1_6)", "NM_000138.4:c.247+1_247+3(1_6)",
				"NM_000138.4:c.247-3_247-1(1_6)", "NM_000138.4:c.*247_*247+3(1_6)", "NM_000138.4:c.-247_-247-3(1_6)" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

}
