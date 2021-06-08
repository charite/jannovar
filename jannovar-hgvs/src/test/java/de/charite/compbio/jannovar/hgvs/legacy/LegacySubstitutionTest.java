package de.charite.compbio.jannovar.hgvs.legacy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LegacySubstitutionTest {

	@Test
	public void test() {
		LegacySubstitution sub = new LegacySubstitution(LegacyLocation.buildIntronicLocation(1, -3), "A", "T");
		Assertions.assertEquals("A", sub.getFromSeq());
		Assertions.assertEquals("T", sub.getToSeq());
		Assertions.assertEquals("IVS1-3A>T", sub.toLegacyString());
	}

}
