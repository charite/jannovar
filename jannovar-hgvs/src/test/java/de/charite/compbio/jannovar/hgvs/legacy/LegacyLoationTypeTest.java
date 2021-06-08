package de.charite.compbio.jannovar.hgvs.legacy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LegacyLoationTypeTest {


	@Test
	public void testLegacyStringToLocationType() {
		Assertions.assertEquals(LegacyLocationType.EXONIC, LegacyLocationType.getTypeForLegacyString("E"));
		Assertions.assertEquals(LegacyLocationType.EXONIC, LegacyLocationType.getTypeForLegacyString("EX"));
		Assertions.assertEquals(LegacyLocationType.INTRONIC, LegacyLocationType.getTypeForLegacyString("IVS"));
	}

	@Test
	public void testToLegacyString() {
		Assertions.assertEquals("EX", LegacyLocationType.EXONIC.getLegacyString());
		Assertions.assertEquals("IVS", LegacyLocationType.INTRONIC.getLegacyString());
	}

}
