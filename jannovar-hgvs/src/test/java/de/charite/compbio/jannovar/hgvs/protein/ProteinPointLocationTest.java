package de.charite.compbio.jannovar.hgvs.protein;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProteinPointLocationTest {

	ProteinPointLocation location1;
	ProteinPointLocation location2;
	ProteinPointLocation location3;
	ProteinPointLocation location4;
	ProteinPointLocation location5;
	ProteinPointLocation location6;

	@BeforeEach
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
		Assertions.assertTrue(location1.equals(location2));
		Assertions.assertTrue(location2.equals(location1));
		Assertions.assertFalse(location1.equals(location3));
		Assertions.assertFalse(location3.equals(location1));
	}

	@Test
	public void testToHGVSString() {
		Assertions.assertEquals("Ala124", location1.toHGVSString());
		Assertions.assertEquals("A124", location1.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assertions.assertEquals("Ala124", location1.toHGVSString(AminoAcidCode.THREE_LETTER));

		Assertions.assertEquals("Ala124", location1.toHGVSString());
		Assertions.assertEquals("Cys124", location3.toHGVSString());
		Assertions.assertEquals("Cys124+1", location4.toHGVSString());
		Assertions.assertEquals("Cys124-1", location5.toHGVSString());
		Assertions.assertEquals("Cys*124", location6.toHGVSString());
	}

}
