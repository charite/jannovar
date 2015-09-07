package de.charite.compbio.jannovar.hgvs.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinDuplication;

public class ProteinDuplicationTest {

	@Test
	public void testLengthOneWithDuplicatedSeq() {
		ProteinDuplication del = ProteinDuplication.buildWithSequence(true, "A", 123, "A", 123, "A");
		Assert.assertEquals("(A124dupA)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthOneWithDuplicatedSeqLength() {
		ProteinDuplication del = ProteinDuplication.buildWithLength(true, "A", 123, "A", 123, 1);
		Assert.assertEquals("(A124dup1)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthOneWithBlankDuplicatedSeqSpec() {
		ProteinDuplication del = ProteinDuplication.buildWithoutSeqDescription(true, "A", 123, "A", 123);
		Assert.assertEquals("(A124dup)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthTwoWithDuplicatedSeq() {
		ProteinDuplication del = ProteinDuplication.buildWithSequence(true, "A", 123, "C", 124, "AC");
		Assert.assertEquals("(A124_C125dupAC)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthTwoWithDuplicatedSeqLength() {
		ProteinDuplication del = ProteinDuplication.buildWithLength(true, "A", 123, "C", 124, 2);
		Assert.assertEquals("(A124_C125dup2)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
