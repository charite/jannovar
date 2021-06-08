package de.charite.compbio.jannovar.hgvs.legacy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LegacyLocationTest {

	@Test
	public void testExonicLocationPositiveOffset() {
		LegacyLocation loc = LegacyLocation.buildExonicLocation(2, 4);
		Assertions.assertEquals(2, loc.getFeatureNo());
		Assertions.assertEquals(4, loc.getBaseOffset());
		Assertions.assertEquals(LegacyLocationType.EXONIC, loc.getLocationType());
		Assertions.assertEquals("EX2+4", loc.toLegacyString());
	}

	@Test
	public void testExonicLocationNegativeOffset() {
		LegacyLocation loc = LegacyLocation.buildExonicLocation(2, -4);
		Assertions.assertEquals(2, loc.getFeatureNo());
		Assertions.assertEquals(-4, loc.getBaseOffset());
		Assertions.assertEquals(LegacyLocationType.EXONIC, loc.getLocationType());
		Assertions.assertEquals("EX2-4", loc.toLegacyString());
	}

	@Test
	public void testIntronicLocationPositiveOffset() {
		LegacyLocation loc = LegacyLocation.buildIntronicLocation(2, 4);
		Assertions.assertEquals(2, loc.getFeatureNo());
		Assertions.assertEquals(4, loc.getBaseOffset());
		Assertions.assertEquals(LegacyLocationType.INTRONIC, loc.getLocationType());
		Assertions.assertEquals("IVS2+4", loc.toLegacyString());
	}

	@Test
	public void testIntronicLocationNegativeOffset() {
		LegacyLocation loc = LegacyLocation.buildIntronicLocation(2, -4);
		Assertions.assertEquals(2, loc.getFeatureNo());
		Assertions.assertEquals(-4, loc.getBaseOffset());
		Assertions.assertEquals(LegacyLocationType.INTRONIC, loc.getLocationType());
		Assertions.assertEquals("IVS2-4", loc.toLegacyString());
	}

}
