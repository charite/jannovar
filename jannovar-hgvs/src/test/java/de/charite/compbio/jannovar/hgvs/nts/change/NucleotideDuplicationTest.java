package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.Assert;
import org.junit.Test;

public class NucleotideDuplicationTest {

	@Test
	public void testLengthOneWithDuplicatedSeq() {
		NucleotideDuplication del = NucleotideDuplication.buildWithSequence(true, 123, 1, 123, 1, "A");
		Assert.assertEquals("(124+1dupA)", del.toHGVSString());
	}

	@Test
	public void testLengthOneWithDuplicatedSeqLength() {
		NucleotideDuplication del = NucleotideDuplication.buildWithLength(true, 123, 0, 123, 0, 1);
		Assert.assertEquals("(124dup1)", del.toHGVSString());
	}

	@Test
	public void testLengthOneWithBlankDuplicatedSeqSpec() {
		NucleotideDuplication del = NucleotideDuplication.buildWithoutSeqDescription(true, 123, 0, 123, 0);
		Assert.assertEquals("(124dup)", del.toHGVSString());
	}

	@Test
	public void testLengthTwoWithDuplicatedSeq() {
		NucleotideDuplication del = NucleotideDuplication.buildWithSequence(true, 123, 0, 124, 0, "AC");
		Assert.assertEquals("(124_125dupAC)", del.toHGVSString());
	}

	@Test
	public void testLengthTwoWithDuplicatedSeqLength() {
		NucleotideDuplication del = NucleotideDuplication.buildWithLength(true, 123, 0, 124, 0, 2);
		Assert.assertEquals("(124_125dup2)", del.toHGVSString());
	}

}
