package de.charite.compbio.jannovar.hgvs.parser;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.MultiAlleleNucleotideVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Test parser for sequenced repeats.
 *
 * Examples from https://varnomen.hgvs.org/recommendations/DNA/variant/repeated/:
 *
 * NC_000003.12:c.89AGC[13]
 * NC_000003.12:g.63912687AGC[13]
 * NM_002024.5:c.-129CGG[79]
 * LRG_763t1:c.53AGC[23]
 * NM_000492.3:c.1210-12T[7]
 * NC_000012.11:g.112036755_112036823CTG[9]TTG[1]CTG[13]
 * NM_000492.3:c.1210-33_1210-6GT[11]T[6]
 * NM_021080.3:c.-136-75952ATTTT[15]
 * NM_002024.5:c.-128_-69GGC[10]GGA[1]GGC[9]GGA[1]GGC[10]
 *
 * NM_023035.2(CACNA1A):c.6955CAG[26]
 *
 * @author Mark Woon
 */
public class HGVSParserDriverNucleotideSequencedRepeatTest {

  HGVSParser driver;

  @BeforeEach
  public void setUp() throws Exception {
    driver = new HGVSParser(false);
  }

  @Test
  public void test() {
    String[] hgvsStrings = new String[]{
        "NC_000003.12:c.89AGC[13]",
        "NM_002024.5:c.-129CGG[79]",
        "LRG_763t1:c.53AGC[23]",
        "NM_000492.3:c.1210-12T[7]",
        "NC_000012.11:g.112036755_112036823CTG[9]TTG[1]CTG[13]",
        "NM_000492.3:c.1210-33_1210-6GT[11]T[6]",
        "NM_021080.3:c.-136-75952ATTTT[15]",
        "NM_002024.5:c.-128_-69GGC[10]GGA[1]GGC[9]GGA[1]GGC[10]",
    };

    for (String hgvsString : hgvsStrings) {
      HGVSVariant variant = driver.parseHGVSString(hgvsString);

      Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant, variant.getClass().getName());
      Assertions.assertEquals(hgvsString, variant.toHGVSString());
    }
  }
}
