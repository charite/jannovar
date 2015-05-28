package de.charite.compbio.jannovar.hgvs;
import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.VariantConfiguration;

public class VariantConfigurationTest {

	@Test
	public void testChimericToHVSSeparator() {
		Assert.assertEquals("//", VariantConfiguration.CHIMERIC.toHGVSSeparator());
	}

	@Test
	public void testMosaicToHVSSeparator() {
		Assert.assertEquals("/", VariantConfiguration.MOSAIC.toHGVSSeparator());
	}

	@Test
	public void testInCisToHVSSeparator() {
		Assert.assertEquals(";", VariantConfiguration.IN_CIS.toHGVSSeparator());
	}

	@Test
	public void testUnknownCisTransToHVSSeparator() {
		Assert.assertEquals("(;)", VariantConfiguration.UNKNOWN_CIS_TRANS.toHGVSSeparator());
	}

	@Test
	public void testSingleOriginToHVSSeparator() {
		Assert.assertEquals(",", VariantConfiguration.SINGLE_ORIGIN.toHGVSSeparator());
	}

}
