package de.charite.compbio.jannovar.hgvs.parser;

import de.charite.compbio.jannovar.hgvs.legacy.LegacyVariant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LegacyMutationParserIndelTest {

	LegacyChangeParser driver;

	@BeforeEach
	public void setUp() throws Exception {
		driver = new LegacyChangeParser();
	}

	@Test
	public void testIntronic() {
		String legacyVariantStrings[] = new String[]{"XXX:IVS3+3delins", "XXX:IVS3-3delACinsT",
			"XXX:IVS3-3delCATAinsTCT", "XXX:IVS3-3del4ins3"};

		for (String legacyVariantString : legacyVariantStrings) {
			LegacyVariant variant = driver.parseLegacyChangeString(legacyVariantString);

			Assertions.assertEquals(legacyVariantString, variant.toLegacyString());
		}
	}

	@Test
	public void testExonic() {
		String legacyVariantStrings[] = new String[]{"XXX:EX3+3delins", "XXX:EX3-3delACinsT",
			"XXX:EX3-3delCATAinsTCT", "XXX:EX3-3del4ins3"};

		for (String legacyVariantString : legacyVariantStrings) {
			LegacyVariant variant = driver.parseLegacyChangeString(legacyVariantString);

			Assertions.assertEquals(legacyVariantString, variant.toLegacyString());
		}
	}

}
