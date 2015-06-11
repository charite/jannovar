package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;

public class HGVSParserDriverTest {

	HGVSParserDriver driver;

	@Before
	public void setUp() throws Exception {
		driver = new HGVSParserDriver();
	}

	@Test
	public void testParseNTSubstitution() {
		// TODO(holtgrem): Add back test with "chr11" that previously broken
		String hgvsStrings[] = new String[] { "1:g.123C>T", "1:g.123C>T", "11:g.111959693G>T", "11:g.111959693G>T",
				"NM_000138.4:n.247C>T" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

}
