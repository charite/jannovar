package de.charite.compbio.jannovar.hgvs.legacy;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LegacyInsertionTest {

	@Test
	public void test() {
		LegacyInsertion ins = new LegacyInsertion(LegacyLocation.buildIntronicLocation(1, -3),
			new NucleotideSeqDescription("A"));
		Assertions.assertEquals("A", ins.getDeletedSeq().toHGVSString());
		Assertions.assertEquals("IVS1-3insA", ins.toLegacyString());
	}

}
