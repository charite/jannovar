package de.charite.compbio.jannovar.hgvs.nts.change;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NucleotideIndelTest {

	@Test
	public void testWithSequenceDescription() {
		NucleotideIndel del = NucleotideIndel.buildWithOffsetWithSequence(true, 123, 0, 123, 0, "A", "CT");
		Assertions.assertEquals("(124delAinsCT)", del.toHGVSString());
	}

	@Test
	public void testWithLengthDescription() {
		NucleotideIndel del = NucleotideIndel.buildWithOffsetWithLength(true, 123, 0, 123, 0, 1, 3);
		Assertions.assertEquals("(124del1ins3)", del.toHGVSString());
	}

	@Test
	public void testWithBlankDescription() {
		NucleotideIndel del = NucleotideIndel.buildWithOffsetWithoutSeqDescription(true, 123, 0, 123, 0);
		Assertions.assertEquals("(124delins)", del.toHGVSString());
	}

	@Test
	public void testWithNucleotideSeqDescription() {
		NucleotideIndel del = NucleotideIndel.buildWithOffsetWithSeqDescription(true, 123, 0, 125, 0,
			new NucleotideSeqDescription(), new NucleotideSeqDescription("AC"));
		Assertions.assertEquals("(124_126delinsAC)", del.toHGVSString());
	}

}
