package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;

public class NucleotideShortSequenceRepeatVariabilityTest {

	private NucleotideShortSequenceRepeatVariability srr;
	private NucleotideShortSequenceRepeatVariability srr2;

	@Before
	public void setUp() {
		srr = NucleotideShortSequenceRepeatVariability.build(false, NucleotideRange.build(20, 0, 23, 0), 1, 2);
		srr2 = NucleotideShortSequenceRepeatVariability.build(false, NucleotideRange.build(20, 0, 23, 0), 3, 6);
	}

	@Test
	public void testToHGVSString() {
		Assert.assertEquals("21_24(1_2)", srr.toHGVSString());
		Assert.assertEquals("21_24(3_6)", srr2.toHGVSString());
	}

}
