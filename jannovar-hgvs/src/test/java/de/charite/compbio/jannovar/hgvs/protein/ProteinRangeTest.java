package de.charite.compbio.jannovar.hgvs.protein;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinPointLocation;
import de.charite.compbio.jannovar.hgvs.protein.ProteinRange;

public class ProteinRangeTest {

	private ProteinPointLocation firstLoc;
	private ProteinPointLocation firstLoc2;
	private ProteinPointLocation lastLoc;

	@Before
	public void setUp() {
		firstLoc = new ProteinPointLocation(123, "A");
		firstLoc2 = new ProteinPointLocation(123, "A");
		lastLoc = new ProteinPointLocation(125, "G");
	}

	@Test
	public void testEquals() {
		Assert.assertEquals(firstLoc, firstLoc2);
		Assert.assertNotEquals(lastLoc, firstLoc);
	}

	/**
	 * Test toHGVSString() with a range of length >1.
	 */
	@Test
	public void testToHGVSStringRange() {
		ProteinRange range = new ProteinRange(firstLoc, lastLoc);

		Assert.assertEquals("A124_G126", range.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("Ala124_Gly126", range.toHGVSString(AminoAcidCode.THREE_LETTER));
	}

	/**
	 * Test toHGVSString() with a single position.
	 */
	@Test
	public void testToHGVSStringSinglePos() {
		ProteinRange range = new ProteinRange(firstLoc, firstLoc2);

		Assert.assertEquals("A124", range.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("Ala124", range.toHGVSString(AminoAcidCode.THREE_LETTER));

	}

}
