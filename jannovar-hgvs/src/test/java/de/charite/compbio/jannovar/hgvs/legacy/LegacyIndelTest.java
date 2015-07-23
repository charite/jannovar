package de.charite.compbio.jannovar.hgvs.legacy;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;

public class LegacyIndelTest {

	@Test
	public void test() {
		LegacyIndel indel = new LegacyIndel(LegacyLocation.buildIntronicLocation(1, -3), new NucleotideSeqDescription(
				"A"), new NucleotideSeqDescription("T"));
		Assert.assertEquals("A", indel.getDeletedSeq().toHGVSString());
		Assert.assertEquals("T", indel.getInsertedSeq().toHGVSString());
		Assert.assertEquals("IVS1-3delAinsT", indel.toLegacyString());
	}

}
