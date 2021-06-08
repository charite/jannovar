package de.charite.compbio.jannovar.hgvs.protein.change;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProteinDuplicationTest {

	@Test
	public void testLengthOneWithDuplicatedSeq() {
		ProteinDuplication del = ProteinDuplication.buildWithSequence(true, "A", 123, "A", 123, "A");
		Assertions.assertEquals("(A124dupA)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthOneWithDuplicatedSeqLength() {
		ProteinDuplication del = ProteinDuplication.buildWithLength(true, "A", 123, "A", 123, 1);
		Assertions.assertEquals("(A124dup1)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthOneWithBlankDuplicatedSeqSpec() {
		ProteinDuplication del = ProteinDuplication.buildWithoutSeqDescription(true, "A", 123, "A", 123);
		Assertions.assertEquals("(A124dup)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthTwoWithDuplicatedSeq() {
		ProteinDuplication del = ProteinDuplication.buildWithSequence(true, "A", 123, "C", 124, "AC");
		Assertions.assertEquals("(A124_C125dupAC)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthTwoWithDuplicatedSeqLength() {
		ProteinDuplication del = ProteinDuplication.buildWithLength(true, "A", 123, "C", 124, 2);
		Assertions.assertEquals("(A124_C125dup2)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
