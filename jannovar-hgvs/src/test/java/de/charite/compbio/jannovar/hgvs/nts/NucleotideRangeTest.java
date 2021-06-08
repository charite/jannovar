package de.charite.compbio.jannovar.hgvs.nts;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NucleotideRangeTest {

	private NucleotidePointLocation firstLoc;
	private NucleotidePointLocation firstLoc2;
	private NucleotidePointLocation lastLoc;

	@BeforeEach
	public void setUp() {
		firstLoc = new NucleotidePointLocation(123, 0, false);
		firstLoc2 = new NucleotidePointLocation(123, 0, false);
		lastLoc = new NucleotidePointLocation(234, -1, false);
	}

	@Test
	public void testEquals() {
		Assertions.assertEquals(firstLoc, firstLoc2);
		Assertions.assertNotEquals(lastLoc, firstLoc);
	}

	/**
	 * Test toHGVSString() with a range of length >1.
	 */
	@Test
	public void testToHGVSStringRange() {
		NucleotideRange range = new NucleotideRange(firstLoc, lastLoc);

		Assertions.assertEquals("124_235-1", range.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assertions.assertEquals("124_235-1", range.toHGVSString(AminoAcidCode.THREE_LETTER));
	}

	/**
	 * Test toHGVSString() with a single position.
	 */
	@Test
	public void testToHGVSStringSinglePos() {
		NucleotideRange range = new NucleotideRange(firstLoc, firstLoc2);

		Assertions.assertEquals("124", range.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assertions.assertEquals("124", range.toHGVSString(AminoAcidCode.THREE_LETTER));

	}

}
