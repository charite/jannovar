package de.charite.compbio.jannovar.hgvs.change.protein;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.change.AminoAcidCode;

public class TestProteinInsertion {

	@Test
	public void testLengthOneWithInsertedSeq() {
		ProteinInsertion del = ProteinInsertion.build(true, "A", 123, "A", 124, "CGA");
		Assert.assertEquals("(A124_A125insCGA)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthOneWithInsertedSeqLength() {
		ProteinInsertion del = ProteinInsertion.build(true, "A", 123, "A", 124, 1);
		Assert.assertEquals("(A124_A125ins1)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthOneWithBlankInsertedSeqSpec() {
		ProteinInsertion del = ProteinInsertion.build(true, "A", 123, "A", 124);
		Assert.assertEquals("(A124_A125ins)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthTwoWithInsertedSeq() {
		ProteinInsertion del = ProteinInsertion.build(true, "A", 123, "C", 124, "AC");
		Assert.assertEquals("(A124_C125insAC)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthTwoWithInsertedSeqLength() {
		ProteinInsertion del = ProteinInsertion.build(true, "A", 123, "C", 124, 2);
		Assert.assertEquals("(A124_C125ins2)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
