package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NucleotideInversionTest {

	@Test
	public void testLengthTwoWithInvertedSeq() {
		NucleotideInversion inv = NucleotideInversion.buildWithOffsetWithSequence(true, 123, 0, 124, 0, "CGA");
		Assertions.assertEquals("(124_125invCGA)", inv.toHGVSString());
	}

	@Test
	public void testLengthTwoWithInvertedSeqLength() {
		NucleotideInversion inv = NucleotideInversion.buildWithOffsetWithLength(true, 123, 0, 124, 0, 3);
		Assertions.assertEquals("(124_125inv3)", inv.toHGVSString());
	}

	@Test
	public void testLengthTwoWithBlankInvertedSeqSpec() {
		NucleotideInversion inv = NucleotideInversion.buildWithOffsetWithoutSeqDescription(true, 123, 0, 125, 0);
		Assertions.assertEquals("(124_126inv)", inv.toHGVSString());
	}

}
