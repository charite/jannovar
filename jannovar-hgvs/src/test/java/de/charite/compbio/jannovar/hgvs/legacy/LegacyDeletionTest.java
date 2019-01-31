package de.charite.compbio.jannovar.hgvs.legacy;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;
import org.junit.Assert;
import org.junit.Test;

public class LegacyDeletionTest {

  @Test
  public void test() {
    LegacyDeletion del =
        new LegacyDeletion(
            LegacyLocation.buildIntronicLocation(1, -3), new NucleotideSeqDescription("A"));
    Assert.assertEquals("A", del.getDeletedSeq().toHGVSString());
    Assert.assertEquals("IVS1-3delA", del.toLegacyString());
  }
}
