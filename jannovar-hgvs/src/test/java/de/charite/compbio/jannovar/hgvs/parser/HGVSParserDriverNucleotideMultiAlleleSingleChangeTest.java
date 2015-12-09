package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.MultiAlleleNucleotideVariant;

// TODO(holtgrew): Test with onlyPredicted flag ("(...)") once this works

/**
 * Tests for the HGVSParserDriver for parsing nucleotide substitutions.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserDriverNucleotideMultiAlleleSingleChangeTest {

	HGVSParser driver;

	@Before
	public void setUp() throws Exception {
		driver = new HGVSParser();
	}

	@Test
	public void testOnlyPredicted() {
		String hgvsStrings[] = new String[] { "1:r.[(123C>T)];[(123C>T)]" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof MultiAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void test() {
		String hgvsStrings[] = new String[] { "1:r.[123C>T];[123C>T]" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof MultiAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

}
