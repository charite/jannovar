package de.charite.compbio.jannovar.hgvs.nts;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NucleotidePointLocationTest {

	@Test
	public void testEquals() {
		NucleotidePointLocation location1 = new NucleotidePointLocation(123, -3, false);
		NucleotidePointLocation location2 = new NucleotidePointLocation(123, -3, false);
		NucleotidePointLocation location3 = new NucleotidePointLocation(123, 3, false);

		Assertions.assertTrue(location1.equals(location2));
		Assertions.assertTrue(location2.equals(location1));
		Assertions.assertFalse(location1.equals(location3));
		Assertions.assertFalse(location3.equals(location1));
	}

	@Test
	public void testToHGVSString() {
		NucleotidePointLocation location1 = new NucleotidePointLocation(123, -3, false);
		NucleotidePointLocation location2 = new NucleotidePointLocation(123, 0, true);

		Assertions.assertEquals("124-3", location1.toHGVSString());
		Assertions.assertEquals("124-3", location1.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assertions.assertEquals("124-3", location1.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("*124", location2.toHGVSString());
	}

}
