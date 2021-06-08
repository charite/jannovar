package de.charite.compbio.jannovar.hgvs.legacy;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LegacyIndelTest {

	@Test
	public void test() {
		LegacyIndel indel = new LegacyIndel(LegacyLocation.buildIntronicLocation(1, -3), new NucleotideSeqDescription(
			"A"), new NucleotideSeqDescription("T"));
		Assertions.assertEquals("A", indel.getDeletedSeq().toHGVSString());
		Assertions.assertEquals("T", indel.getInsertedSeq().toHGVSString());
		Assertions.assertEquals("IVS1-3delAinsT", indel.toLegacyString());
	}

}
