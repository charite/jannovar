package de.charite.compbio.jannovar.hgvs.parser;

import de.charite.compbio.jannovar.hgvs.legacy.LegacyVariant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LegacyMutationParserDeletionTest {

	LegacyChangeParser driver;

	@BeforeEach
	public void setUp() throws Exception {
		driver = new LegacyChangeParser();
	}

	@Test
	public void testIntronic() {
		String legacyVariantStrings[] = new String[]{"XXX:IVS3+3ins", "XXX:IVS3-3insT", "XXX:IVS3-3insTCT",
			"XXX:IVS3-3ins3"};

		for (String legacyVariantString : legacyVariantStrings) {
			LegacyVariant variant = driver.parseLegacyChangeString(legacyVariantString);

			Assertions.assertEquals(legacyVariantString, variant.toLegacyString());
		}
	}

	@Test
	public void testExonic() {
		String legacyVariantStrings[] = new String[]{"XXX:EX3+3ins", "XXX:EX3-3insT", "XXX:EX3-3insTCT",
			"XXX:EX3-3ins3"};

		for (String legacyVariantString : legacyVariantStrings) {
			LegacyVariant variant = driver.parseLegacyChangeString(legacyVariantString);

			Assertions.assertEquals(legacyVariantString, variant.toLegacyString());
		}
	}

}
