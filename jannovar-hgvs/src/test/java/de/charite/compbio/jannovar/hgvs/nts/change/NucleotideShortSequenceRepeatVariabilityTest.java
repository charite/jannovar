package de.charite.compbio.jannovar.hgvs.nts.change;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NucleotideShortSequenceRepeatVariabilityTest {

	private NucleotideShortSequenceRepeatVariability srr;
	private NucleotideShortSequenceRepeatVariability srr2;

	@BeforeEach
	public void setUp() {
		srr = NucleotideShortSequenceRepeatVariability.build(false, NucleotideRange.build(20, 0, 23, 0), 1, 2);
		srr2 = NucleotideShortSequenceRepeatVariability.build(false, NucleotideRange.build(20, 0, 23, 0), 3, 6);
	}

	@Test
	public void testToHGVSString() {
		Assertions.assertEquals("21_24(1_2)", srr.toHGVSString());
		Assertions.assertEquals("21_24(3_6)", srr2.toHGVSString());
	}

}
