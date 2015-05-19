package de.charite.compbio.jannovar.hgvs.nts;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;

public class NucleotideRangeTest {

	private NucleotidePointLocation firstLoc;
	private NucleotidePointLocation firstLoc2;
	private NucleotidePointLocation lastLoc;

	@Before
	public void setUp() {
		firstLoc = new NucleotidePointLocation(123, 0, false);
		firstLoc2 = new NucleotidePointLocation(123, 0, false);
		lastLoc = new NucleotidePointLocation(234, -1, false);
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
		NucleotideRange range = new NucleotideRange(firstLoc, lastLoc);

		Assert.assertEquals("124_235-1", range.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("124_235-1", range.toHGVSString(AminoAcidCode.THREE_LETTER));
	}

	/**
	 * Test toHGVSString() with a single position.
	 */
	@Test
	public void testToHGVSStringSinglePos() {
		NucleotideRange range = new NucleotideRange(firstLoc, firstLoc2);

		Assert.assertEquals("124", range.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("124", range.toHGVSString(AminoAcidCode.THREE_LETTER));

	}

}
