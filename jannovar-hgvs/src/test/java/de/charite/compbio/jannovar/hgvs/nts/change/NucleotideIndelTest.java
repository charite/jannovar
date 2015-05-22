package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;

public class NucleotideIndelTest {

	@Test
	public void testWithSequenceDescription() {
		NucleotideIndel del = NucleotideIndel.buildWithOffsetWithSequence(true, 123, 0, 123, 0, "A", "CT");
		Assert.assertEquals("(124delAinsCT)", del.toHGVSString());
	}

	@Test
	public void testWithLengthDescription() {
		NucleotideIndel del = NucleotideIndel.buildWithOffsetWithLength(true, 123, 0, 123, 0, 1, 3);
		Assert.assertEquals("(124del1ins3)", del.toHGVSString());
	}

	@Test
	public void testWithBlankDescription() {
		NucleotideIndel del = NucleotideIndel.buildWithOffsetWithoutSeqDescription(true, 123, 0, 123, 0);
		Assert.assertEquals("(124delins)", del.toHGVSString());
	}

	@Test
	public void testWithNucleotideSeqDescription() {
		NucleotideIndel del = NucleotideIndel.buildWithOffsetWithSeqDescription(true, 123, 0, 125, 0,
				new NucleotideSeqDescription(), new NucleotideSeqDescription("AC"));
		Assert.assertEquals("(124_126delinsAC)", del.toHGVSString());
	}

}
