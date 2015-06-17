package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;

/**
 * Tests for the HGVSParserDriver for parsing misc changes.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserDriverNucleotideMiscChangeTest {

	HGVSParserDriver driver;

	@Before
	public void setUp() throws Exception {
		driver = new HGVSParserDriver(false);
	}

	@Test
	public void testParseString() {
		String hgvsStrings[] = new String[] { "XXX:r.(?)", "XXX:r.?", "XXX:r.spl?", "XXX:r.(spl?)", "XXX:r.=",
				"XXX:r.(=)", "XXX:r.0", "XXX:r.(0)" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

}
