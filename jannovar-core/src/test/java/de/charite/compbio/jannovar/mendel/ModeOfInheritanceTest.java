package de.charite.compbio.jannovar.mendel;

import org.junit.Assert;
import org.junit.Test;

public class ModeOfInheritanceTest {

  /** Including mitochondrial, there are 6 modes of inheritance */
  @Test
  public void testSize() {
    Assert.assertEquals(6, ModeOfInheritance.values().length);
  }
}
