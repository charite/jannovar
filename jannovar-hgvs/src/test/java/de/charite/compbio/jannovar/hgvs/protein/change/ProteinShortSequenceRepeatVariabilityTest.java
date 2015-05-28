package de.charite.compbio.jannovar.hgvs.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;

public class ProteinShortSequenceRepeatVariabilityTest {

	@Test
	public void test() {
		ProteinShortSequenceRepeatVariability var = ProteinShortSequenceRepeatVariability.build(true, "A", 123, "C",
				124, 3, 6);
		Assert.assertEquals("(A124_C125(3_6))", var.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
