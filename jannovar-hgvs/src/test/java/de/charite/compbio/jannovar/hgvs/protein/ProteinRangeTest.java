package de.charite.compbio.jannovar.hgvs.protein;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProteinRangeTest {

	private ProteinRange firstRange;
	private ProteinRange firstRange2;
	private ProteinRange secondRange;
	private ProteinRange offsetRange;
	private ProteinRange downstreamOfTerminalRange;

	@BeforeEach
	public void setUp() {
		firstRange = new ProteinRange(ProteinPointLocation.build("A", 123), ProteinPointLocation.build("T", 300));
		firstRange2 = new ProteinRange(ProteinPointLocation.build("A", 123), ProteinPointLocation.build("T", 300));
		secondRange = new ProteinRange(ProteinPointLocation.build("G", 125), ProteinPointLocation.build("T", 301));
		offsetRange = new ProteinRange(ProteinPointLocation.buildWithOffset("G", 125, -1),
			ProteinPointLocation.buildWithOffset("T", 301, -1));
		downstreamOfTerminalRange = new ProteinRange(ProteinPointLocation.buildDownstreamOfTerminal("G", 125),
			ProteinPointLocation.buildDownstreamOfTerminal("T", 301));
	}

	@Test
	public void testEquals() {
		Assertions.assertEquals(firstRange, firstRange2);
		Assertions.assertNotEquals(secondRange, firstRange);
	}

	/**
	 * Test toHGVSString() with a range of length >1.
	 */
	@Test
	public void testToHGVSStringRange() {
		ProteinRange range = new ProteinRange(ProteinPointLocation.build("A", 123),
			ProteinPointLocation.build("G", 125));

		Assertions.assertEquals("A124_G126", range.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assertions.assertEquals("Ala124_Gly126", range.toHGVSString(AminoAcidCode.THREE_LETTER));
	}

	/**
	 * Test toHGVSString() with a single position.
	 */
	@Test
	public void testToHGVSStringSinglePos() {
		ProteinRange range = new ProteinRange(ProteinPointLocation.build("A", 123),
			ProteinPointLocation.build("A", 123));

		Assertions.assertEquals("A124", range.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assertions.assertEquals("Ala124", range.toHGVSString(AminoAcidCode.THREE_LETTER));
	}

	@Test
	public void testToHGVSStringWithOffset() {
		Assertions.assertEquals("Gly126-1_Thr302-1", offsetRange.toHGVSString());
	}

	@Test
	public void testToHGVSStringDownstreamOfTerminal() {
		Assertions.assertEquals("Gly*126_Thr*302", downstreamOfTerminalRange.toHGVSString());
	}

}
