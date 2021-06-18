package de.charite.compbio.jannovar.hgvs.parser;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Test parser for not-sequenced repeats.
 *
 * Examples from https://varnomen.hgvs.org/recommendations/DNA/variant/repeated/:
 *
 * NM_000333.3:c.(4_246)ins(9)
 * NC_000003.12:g.(63912602_63912844)del(15)
 * NM_002024.5:c.(-144_-16)ins(1800_2400)
 *
 * @author Mark Woon
 */
public class HGVSParserDriverNucleotideNotSequencedRepeatTest {

  HGVSParser driver;

  @BeforeEach
  public void setUp() throws Exception {
    driver = new HGVSParser(false);
  }

  @Test
  public void test() {
    String[] hgvsStrings = new String[]{
        "NM_000333.3:c.4_246ins(9)",
        "NC_000003.12:g.63912602_63912844del(15)",
        "NM_002024.5:c.-144_-16ins(1800_2400)",
    };

    for (String hgvsString : hgvsStrings) {
      HGVSVariant variant = driver.parseHGVSString(hgvsString);

      Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant, variant.getClass().getName());
      Assertions.assertEquals(hgvsString, variant.toHGVSString());
    }
  }
}
