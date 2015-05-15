package de.charite.compbio.jannovar.hgvs.protein;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinPointLocation;

public class ProteinPointLocationTest {

	@Test
	public void testEquals() {
		ProteinPointLocation location1 = new ProteinPointLocation("A", 123);
		ProteinPointLocation location2 = new ProteinPointLocation("A", 123);
		ProteinPointLocation location3 = new ProteinPointLocation("C", 123);

		Assert.assertTrue(location1.equals(location2));
		Assert.assertTrue(location2.equals(location1));
		Assert.assertFalse(location1.equals(location3));
		Assert.assertFalse(location3.equals(location1));
	}

	@Test
	public void testToHGVSString() {
		ProteinPointLocation location = new ProteinPointLocation("A", 123);

		Assert.assertEquals("Ala124", location.toHGVSString());
		Assert.assertEquals("A124", location.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("Ala124", location.toHGVSString(AminoAcidCode.THREE_LETTER));
	}

}
