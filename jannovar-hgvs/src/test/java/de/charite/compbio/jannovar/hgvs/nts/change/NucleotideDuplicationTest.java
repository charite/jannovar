package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NucleotideDuplicationTest {

	@Test
	public void testLengthOneWithDuplicatedSeq() {
		NucleotideDuplication del = NucleotideDuplication.buildWithOffsetWithSequence(true, 123, 1, 123, 1, "A");
		Assertions.assertEquals("(124+1dupA)", del.toHGVSString());
	}

	@Test
	public void testLengthOneWithDuplicatedSeqLength() {
		NucleotideDuplication del = NucleotideDuplication.buildWithOffsetWithLength(true, 123, 0, 123, 0, 1);
		Assertions.assertEquals("(124dup1)", del.toHGVSString());
	}

	@Test
	public void testLengthOneWithBlankDuplicatedSeqSpec() {
		NucleotideDuplication del = NucleotideDuplication.buildWithOffsetWithoutSeqDescription(true, 123, 0, 123, 0);
		Assertions.assertEquals("(124dup)", del.toHGVSString());
	}

	@Test
	public void testLengthTwoWithDuplicatedSeq() {
		NucleotideDuplication del = NucleotideDuplication.buildWithOffsetWithSequence(true, 123, 0, 124, 0, "AC");
		Assertions.assertEquals("(124_125dupAC)", del.toHGVSString());
	}

	@Test
	public void testLengthTwoWithDuplicatedSeqLength() {
		NucleotideDuplication del = NucleotideDuplication.buildWithOffsetWithLength(true, 123, 0, 124, 0, 2);
		Assertions.assertEquals("(124_125dup2)", del.toHGVSString());
	}

}
