package de.charite.compbio.jannovar.vardbs.dbsnp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DBSNPGeneInfoTest {

  @Before
  public void setUp() throws Exception {}

  @Test
  public void test() {
    DBSNPGeneInfo info = new DBSNPGeneInfo("SYMBOL", 123);
    Assert.assertEquals(info.getSymbol(), "SYMBOL");
    Assert.assertEquals(info.getId(), 123);
  }
}
