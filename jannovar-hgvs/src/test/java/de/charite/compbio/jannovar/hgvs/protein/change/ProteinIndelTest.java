package de.charite.compbio.jannovar.hgvs.protein.change;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinSeqDescription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProteinIndelTest {

	@Test
	public void testDeletionOfLengthOneWithInsertionOfLengthTwoWithoutSeqDescription() {
		ProteinIndel del = ProteinIndel.buildWithoutSeqDescription(true, "A", 123, "A", 123);
		Assertions.assertEquals("(A124delins)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testDeletionOfLengthOneWithInsertionOfLengthTwoWithLength() {
		ProteinIndel del = ProteinIndel.buildWithLength(true, "A", 123, "A", 123, 1, 2);
		Assertions.assertEquals("(A124del1ins2)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testDeletionOfLengthOneWithInsertionOfLengthTwoWithSequence() {
		ProteinIndel del = ProteinIndel.buildWithSequence(true, "A", 123, "A", 123, "A", "CT");
		Assertions.assertEquals("(A124delAinsCT)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testDeletionOfLengthOneWithInsertionOfLengthTwoWithSeqDescription() {
		ProteinIndel del = ProteinIndel.buildWithSeqDescription(true, "A", 123, "A", 123,
			new ProteinSeqDescription("A"), new ProteinSeqDescription("CT"));
		Assertions.assertEquals("(A124delAinsCT)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testDeletionOfLengthTwoWithInsertionOfLengthOneWithoutSeqDescription() {
		ProteinIndel del = ProteinIndel.buildWithoutSeqDescription(true, "A", 123, "C", 124);
		Assertions.assertEquals("(A124_C125delins)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testDeletionOfLengthTwoWithInsertionOfLengthOneWithLength() {
		ProteinIndel del = ProteinIndel.buildWithLength(true, "A", 123, "C", 124, 2, 1);
		Assertions.assertEquals("(A124_C125del2ins1)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testDeletionOfLengthTwoWithInsertionOfLengthOneWithSequence() {
		ProteinIndel del = ProteinIndel.buildWithSequence(true, "A", 123, "C", 123, "AC", "T");
		Assertions.assertEquals("(A124_C124delACinsT)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testDeletionOfLengthTwoWithInsertionOfLengthOneWithSeqDescription() {
		ProteinIndel del = ProteinIndel.buildWithSeqDescription(true, "A", 123, "C", 124, new ProteinSeqDescription(
			"AC"), new ProteinSeqDescription("T"));
		Assertions.assertEquals("(A124_C125delACinsT)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
