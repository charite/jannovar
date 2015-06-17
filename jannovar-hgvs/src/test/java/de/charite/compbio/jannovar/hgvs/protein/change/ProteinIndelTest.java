package de.charite.compbio.jannovar.hgvs.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinSeqDescription;

public class ProteinIndelTest {

	@Test
	public void testDeletionOfLengthOneWithInsertionOfLengthTwoWithoutSeqDescription() {
		ProteinIndel del = ProteinIndel.buildWithoutSeqDescription(true, "A", 123, "A", 123);
		Assert.assertEquals("(A124delins)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testDeletionOfLengthOneWithInsertionOfLengthTwoWithLength() {
		ProteinIndel del = ProteinIndel.buildWithLength(true, "A", 123, "A", 123, 1, 2);
		Assert.assertEquals("(A124del1ins2)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testDeletionOfLengthOneWithInsertionOfLengthTwoWithSequence() {
		ProteinIndel del = ProteinIndel.buildWithSequence(true, "A", 123, "A", 123, "A", "CT");
		Assert.assertEquals("(A124delAinsCT)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testDeletionOfLengthOneWithInsertionOfLengthTwoWithSeqDescription() {
		ProteinIndel del = ProteinIndel.buildWithSeqDescription(true, "A", 123, "A", 123,
				new ProteinSeqDescription("A"), new ProteinSeqDescription("CT"));
		Assert.assertEquals("(A124delAinsCT)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testDeletionOfLengthTwoWithInsertionOfLengthOneWithoutSeqDescription() {
		ProteinIndel del = ProteinIndel.buildWithoutSeqDescription(true, "A", 123, "C", 124);
		Assert.assertEquals("(A124_C125delins)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testDeletionOfLengthTwoWithInsertionOfLengthOneWithLength() {
		ProteinIndel del = ProteinIndel.buildWithLength(true, "A", 123, "C", 124, 2, 1);
		Assert.assertEquals("(A124_C125del2ins1)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testDeletionOfLengthTwoWithInsertionOfLengthOneWithSequence() {
		ProteinIndel del = ProteinIndel.buildWithSequence(true, "A", 123, "C", 123, "AC", "T");
		Assert.assertEquals("(A124_C124delACinsT)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testDeletionOfLengthTwoWithInsertionOfLengthOneWithSeqDescription() {
		ProteinIndel del = ProteinIndel.buildWithSeqDescription(true, "A", 123, "C", 124, new ProteinSeqDescription(
				"AC"), new ProteinSeqDescription("T"));
		Assert.assertEquals("(A124_C125delACinsT)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
