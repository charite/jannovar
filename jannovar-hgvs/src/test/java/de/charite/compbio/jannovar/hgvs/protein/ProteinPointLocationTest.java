package de.charite.compbio.jannovar.hgvs.protein;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;

public class ProteinPointLocationTest {

	ProteinPointLocation location1;
	ProteinPointLocation location2;
	ProteinPointLocation location3;
	ProteinPointLocation location4;
	ProteinPointLocation location5;
	ProteinPointLocation location6;

	@Before
	public void setUp() {
		location1 = new ProteinPointLocation("A", 123, 0, false);
		location2 = new ProteinPointLocation("A", 123, 0, false);
		location3 = new ProteinPointLocation("C", 123, 0, false);
		location4 = new ProteinPointLocation("C", 123, 1, false);
		location5 = new ProteinPointLocation("C", 123, -1, false);
		location6 = new ProteinPointLocation("C", 123, 1, true);
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(location1.equals(location2));
		Assert.assertTrue(location2.equals(location1));
		Assert.assertFalse(location1.equals(location3));
		Assert.assertFalse(location3.equals(location1));
	}

	@Test
	public void testToHGVSString() {
		Assert.assertEquals("Ala124", location1.toHGVSString());
		Assert.assertEquals("A124", location1.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("Ala124", location1.toHGVSString(AminoAcidCode.THREE_LETTER));
		
		Assert.assertEquals("Ala124", location1.toHGVSString());
		Assert.assertEquals("Cys124", location3.toHGVSString());
		Assert.assertEquals("Cys124+1", location4.toHGVSString());
		Assert.assertEquals("Cys124-1", location5.toHGVSString());
		Assert.assertEquals("Cys*124", location6.toHGVSString());
	}

}
