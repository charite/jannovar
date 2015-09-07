package de.charite.compbio.jannovar.hgvs.legacy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LegacyLocationTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testExonicLocationPositiveOffset() {
		LegacyLocation loc = LegacyLocation.buildExonicLocation(2, 4);
		Assert.assertEquals(2, loc.getFeatureNo());
		Assert.assertEquals(4, loc.getBaseOffset());
		Assert.assertEquals(LegacyLocationType.EXONIC, loc.getLocationType());
		Assert.assertEquals("EX2+4", loc.toLegacyString());
	}

	@Test
	public void testExonicLocationNegativeOffset() {
		LegacyLocation loc = LegacyLocation.buildExonicLocation(2, -4);
		Assert.assertEquals(2, loc.getFeatureNo());
		Assert.assertEquals(-4, loc.getBaseOffset());
		Assert.assertEquals(LegacyLocationType.EXONIC, loc.getLocationType());
		Assert.assertEquals("EX2-4", loc.toLegacyString());
	}

	@Test
	public void testIntronicLocationPositiveOffset() {
		LegacyLocation loc = LegacyLocation.buildIntronicLocation(2, 4);
		Assert.assertEquals(2, loc.getFeatureNo());
		Assert.assertEquals(4, loc.getBaseOffset());
		Assert.assertEquals(LegacyLocationType.INTRONIC, loc.getLocationType());
		Assert.assertEquals("IVS2+4", loc.toLegacyString());
	}

	@Test
	public void testIntronicLocationNegativeOffset() {
		LegacyLocation loc = LegacyLocation.buildIntronicLocation(2, -4);
		Assert.assertEquals(2, loc.getFeatureNo());
		Assert.assertEquals(-4, loc.getBaseOffset());
		Assert.assertEquals(LegacyLocationType.INTRONIC, loc.getLocationType());
		Assert.assertEquals("IVS2-4", loc.toLegacyString());
	}

}
