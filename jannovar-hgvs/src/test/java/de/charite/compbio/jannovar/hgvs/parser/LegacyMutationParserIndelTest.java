package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.legacy.LegacyVariant;

public class LegacyMutationParserIndelTest {

	LegacyChangeParser driver;

	@Before
	public void setUp() throws Exception {
		driver = new LegacyChangeParser();
	}

	@Test
	public void testIntronic() {
		String legacyVariantStrings[] = new String[] { "XXX:IVS3+3delins", "XXX:IVS3-3delACinsT",
				"XXX:IVS3-3delCATAinsTCT", "XXX:IVS3-3del4ins3" };

		for (String legacyVariantString : legacyVariantStrings) {
			LegacyVariant variant = driver.parseLegacyChangeString(legacyVariantString);

			Assert.assertEquals(legacyVariantString, variant.toLegacyString());
		}
	}

	@Test
	public void testExonic() {
		String legacyVariantStrings[] = new String[] { "XXX:EX3+3delins", "XXX:EX3-3delACinsT",
				"XXX:EX3-3delCATAinsTCT", "XXX:EX3-3del4ins3" };

		for (String legacyVariantString : legacyVariantStrings) {
			LegacyVariant variant = driver.parseLegacyChangeString(legacyVariantString);

			Assert.assertEquals(legacyVariantString, variant.toLegacyString());
		}
	}

}
