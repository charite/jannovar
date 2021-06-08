package de.charite.compbio.jannovar.hgvs.legacy;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LegacyDeletionTest {

	@Test
	public void test() {
		LegacyDeletion del = new LegacyDeletion(LegacyLocation.buildIntronicLocation(1, -3),
			new NucleotideSeqDescription("A"));
		Assertions.assertEquals("A", del.getDeletedSeq().toHGVSString());
		Assertions.assertEquals("IVS1-3delA", del.toLegacyString());
	}

}
