package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.legacy.LegacyVariant;

public class LegacyMutationParserInsertionTest {

	LegacyChangeParser driver;

	@Before
	public void setUp() throws Exception {
		driver = new LegacyChangeParser();
	}

	@Test
	public void testIntronic() {
		String legacyVariantStrings[] = new String[] { "XXX:IVS3+3del", "XXX:IVS3-3delT", "XXX:IVS3-3delTCT", "XXX:IVS3-3del3" };

		for (String legacyVariantString : legacyVariantStrings) {
			LegacyVariant variant = driver.parseLegacyChangeString(legacyVariantString);

			Assert.assertEquals(legacyVariantString, variant.toLegacyString());
		}
	}

	@Test
	public void testExonic() {
		String legacyVariantStrings[] = new String[] { "XXX:EX3+3del", "XXX:EX3-3delT", "XXX:EX3-3delTCT", "XXX:EX3-3del3" };

		for (String legacyVariantString : legacyVariantStrings) {
			LegacyVariant variant = driver.parseLegacyChangeString(legacyVariantString);

			Assert.assertEquals(legacyVariantString, variant.toLegacyString());
		}
	}

}
