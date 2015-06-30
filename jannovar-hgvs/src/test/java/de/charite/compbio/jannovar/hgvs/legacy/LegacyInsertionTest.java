package de.charite.compbio.jannovar.hgvs.legacy;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;

public class LegacyInsertionTest {

	@Test
	public void test() {
		LegacyInsertion ins = new LegacyInsertion(LegacyLocation.buildIntronicLocation(1, -3),
				new NucleotideSeqDescription("A"));
		Assert.assertEquals("A", ins.getDeletedSeq().toHGVSString());
		Assert.assertEquals("IVS1-3insA", ins.toLegacyString());
	}

}
