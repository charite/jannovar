package de.charite.compbio.jannovar.hgvs.nts;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;

public class NucleotidePointLocationTest {

	@Test
	public void testEquals() {
		NucleotidePointLocation location1 = new NucleotidePointLocation(123, -3, false);
		NucleotidePointLocation location2 = new NucleotidePointLocation(123, -3, false);
		NucleotidePointLocation location3 = new NucleotidePointLocation(123, 3, false);

		Assert.assertTrue(location1.equals(location2));
		Assert.assertTrue(location2.equals(location1));
		Assert.assertFalse(location1.equals(location3));
		Assert.assertFalse(location3.equals(location1));
	}

	@Test
	public void testToHGVSString() {
		NucleotidePointLocation location1 = new NucleotidePointLocation(123, -3, false);
		NucleotidePointLocation location2 = new NucleotidePointLocation(123, 0, true);

		Assert.assertEquals("124-3", location1.toHGVSString());
		Assert.assertEquals("124-3", location1.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("124-3", location1.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("*124", location2.toHGVSString());
	}

}
