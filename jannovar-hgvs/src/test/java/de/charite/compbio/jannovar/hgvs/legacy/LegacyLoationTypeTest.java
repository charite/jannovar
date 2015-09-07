package de.charite.compbio.jannovar.hgvs.legacy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LegacyLoationTypeTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testLegacyStringToLocationType() {
		Assert.assertEquals(LegacyLocationType.EXONIC, LegacyLocationType.getTypeForLegacyString("E"));
		Assert.assertEquals(LegacyLocationType.EXONIC, LegacyLocationType.getTypeForLegacyString("EX"));
		Assert.assertEquals(LegacyLocationType.INTRONIC, LegacyLocationType.getTypeForLegacyString("IVS"));
	}

	@Test
	public void testToLegacyString() {
		Assert.assertEquals("EX", LegacyLocationType.EXONIC.getLegacyString());
		Assert.assertEquals("IVS", LegacyLocationType.INTRONIC.getLegacyString());
	}

}
