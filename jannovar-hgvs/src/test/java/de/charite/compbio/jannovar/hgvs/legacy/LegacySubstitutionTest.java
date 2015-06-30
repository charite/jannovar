package de.charite.compbio.jannovar.hgvs.legacy;

import org.junit.Assert;
import org.junit.Test;

public class LegacySubstitutionTest {

	@Test
	public void test() {
		LegacySubstitution sub = new LegacySubstitution(LegacyLocation.buildIntronicLocation(1, -3), "A", "T");
		Assert.assertEquals("A", sub.getFromSeq());
		Assert.assertEquals("T", sub.getToSeq());
		Assert.assertEquals("IVS1-3A>T", sub.toLegacyString());
	}

}
