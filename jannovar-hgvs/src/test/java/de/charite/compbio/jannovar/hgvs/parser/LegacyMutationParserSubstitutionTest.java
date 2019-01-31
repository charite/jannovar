package de.charite.compbio.jannovar.hgvs.parser;

import de.charite.compbio.jannovar.hgvs.legacy.LegacyVariant;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LegacyMutationParserSubstitutionTest {

  LegacyChangeParser driver;

  @Before
  public void setUp() throws Exception {
    driver = new LegacyChangeParser();
  }

  @Test
  public void testIntronic() {
    String legacyVariantStrings[] = new String[] {"XXX:IVS3+3C>T", "XXX:IVS3-3C>T"};

    for (String legacyVariantString : legacyVariantStrings) {
      LegacyVariant variant = driver.parseLegacyChangeString(legacyVariantString);

      Assert.assertEquals(legacyVariantString, variant.toLegacyString());
    }
  }

  @Test
  public void testExonic() {
    String legacyVariantStrings[] = new String[] {"XXX:EX3+3C>T", "XXX:EX3-3C>T"};

    for (String legacyVariantString : legacyVariantStrings) {
      LegacyVariant variant = driver.parseLegacyChangeString(legacyVariantString);

      Assert.assertEquals(legacyVariantString, variant.toLegacyString());
    }
  }
}
