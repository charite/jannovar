package de.charite.compbio.jannovar.hgvs.protein;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;

public class ProteinRangeTest {

	private ProteinRange firstRange;
	private ProteinRange firstRange2;
	private ProteinRange secondRange;
	private ProteinRange offsetRange;
	private ProteinRange downstreamOfTerminalRange;

	@Before
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
		Assert.assertEquals(firstRange, firstRange2);
		Assert.assertNotEquals(secondRange, firstRange);
	}

	/**
	 * Test toHGVSString() with a range of length >1.
	 */
	@Test
	public void testToHGVSStringRange() {
		ProteinRange range = new ProteinRange(ProteinPointLocation.build("A", 123),
				ProteinPointLocation.build("G", 125));

		Assert.assertEquals("A124_G126", range.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("Ala124_Gly126", range.toHGVSString(AminoAcidCode.THREE_LETTER));
	}

	/**
	 * Test toHGVSString() with a single position.
	 */
	@Test
	public void testToHGVSStringSinglePos() {
		ProteinRange range = new ProteinRange(ProteinPointLocation.build("A", 123),
				ProteinPointLocation.build("A", 123));

		Assert.assertEquals("A124", range.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("Ala124", range.toHGVSString(AminoAcidCode.THREE_LETTER));
	}

	@Test
	public void testToHGVSStringWithOffset() {
		Assert.assertEquals("Gly126-1_Thr302-1", offsetRange.toHGVSString());
	}

	@Test
	public void testToHGVSStringDownstreamOfTerminal() {
		Assert.assertEquals("Gly*126_Thr*302", downstreamOfTerminalRange.toHGVSString());
	}

}
