package de.charite.compbio.jannovar.hgvs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VariantConfigurationTest {

	@Test
	public void testChimericToHVSSeparator() {
		Assertions.assertEquals("//", VariantConfiguration.CHIMERIC.toHGVSSeparator());
	}

	@Test
	public void testMosaicToHVSSeparator() {
		Assertions.assertEquals("/", VariantConfiguration.MOSAIC.toHGVSSeparator());
	}

	@Test
	public void testInCisToHVSSeparator() {
		Assertions.assertEquals(";", VariantConfiguration.IN_CIS.toHGVSSeparator());
	}

	@Test
	public void testUnknownCisTransToHVSSeparator() {
		Assertions.assertEquals("(;)", VariantConfiguration.UNKNOWN_CIS_TRANS.toHGVSSeparator());
	}

	@Test
	public void testSingleOriginToHVSSeparator() {
		Assertions.assertEquals(",", VariantConfiguration.SINGLE_ORIGIN.toHGVSSeparator());
	}

}
