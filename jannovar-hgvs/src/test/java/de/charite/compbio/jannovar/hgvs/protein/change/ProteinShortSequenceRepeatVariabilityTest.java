package de.charite.compbio.jannovar.hgvs.protein.change;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProteinShortSequenceRepeatVariabilityTest {

	@Test
	public void test() {
		ProteinShortSequenceRepeatVariability var = ProteinShortSequenceRepeatVariability.build(true, "A", 123, "C",
			124, 3, 6);
		Assertions.assertEquals("(A124_C125(3_6))", var.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
