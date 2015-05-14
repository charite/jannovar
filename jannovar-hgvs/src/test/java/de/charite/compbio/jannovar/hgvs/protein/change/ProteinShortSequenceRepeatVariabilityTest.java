package de.charite.compbio.jannovar.hgvs.protein.change;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinRange;

public class ProteinShortSequenceRepeatVariabilityTest {

	private ProteinShortSequenceRepeatVariability srr;
	private ProteinShortSequenceRepeatVariability srr2;

	@Before
	public void setUp() {
		srr = ProteinShortSequenceRepeatVariability.build(false, ProteinRange.build("A", 20, "A", 20), 1, 2);
		srr2 = ProteinShortSequenceRepeatVariability.build(false, ProteinRange.build("A", 20, "C", 23), 3, 6);
	}

	@Test
	public void testToHGVSString() {
		Assert.assertEquals("Ala21(1_2)", srr.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("Ala21_Cys24(3_6)", srr2.toHGVSString(AminoAcidCode.THREE_LETTER));
	}

}
