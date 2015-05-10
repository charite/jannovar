package de.charite.compbio.jannovar.hgvs.change.protein;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.change.AminoAcidCode;

public class ProteinPointLocationTest {

	@Test
	public void testEquals() {
		ProteinPointLocation location1 = new ProteinPointLocation(123, "A");
		ProteinPointLocation location2 = new ProteinPointLocation(123, "A");
		ProteinPointLocation location3 = new ProteinPointLocation(123, "C");

		Assert.assertTrue(location1.equals(location2));
		Assert.assertTrue(location2.equals(location1));
		Assert.assertFalse(location1.equals(location3));
		Assert.assertFalse(location3.equals(location1));
	}

	@Test
	public void testToHGVSString() {
		ProteinPointLocation location = new ProteinPointLocation(123, "A");

		Assert.assertEquals("Ala124", location.toHGVSString());
		Assert.assertEquals("A124", location.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("Ala124", location.toHGVSString(AminoAcidCode.THREE_LETTER));
	}

}
