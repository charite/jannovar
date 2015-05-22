package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.Assert;
import org.junit.Test;

public class NucleotideInversionTest {

	@Test
	public void testLengthTwoWithInvertedSeq() {
		NucleotideInversion inv = NucleotideInversion.buildWithOffsetWithSequence(true, 123, 0, 124, 0, "CGA");
		Assert.assertEquals("(124_125invCGA)", inv.toHGVSString());
	}

	@Test
	public void testLengthTwoWithInvertedSeqLength() {
		NucleotideInversion inv = NucleotideInversion.buildWithOffsetWithLength(true, 123, 0, 124, 0, 3);
		Assert.assertEquals("(124_125inv3)", inv.toHGVSString());
	}

	@Test
	public void testLengthTwoWithBlankInvertedSeqSpec() {
		NucleotideInversion inv = NucleotideInversion.buildWithOffsetWithoutSeqDescription(true, 123, 0, 125, 0);
		Assert.assertEquals("(124_126inv)", inv.toHGVSString());
	}

}
