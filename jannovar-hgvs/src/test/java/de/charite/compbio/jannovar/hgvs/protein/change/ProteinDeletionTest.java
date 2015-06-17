package de.charite.compbio.jannovar.hgvs.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinDeletion;

public class ProteinDeletionTest {

	@Test
	public void testLengthOneWithDeletedSeq() {
		ProteinDeletion del = ProteinDeletion.buildWithSequence(true, "A", 123, "A", 123, "A");
		Assert.assertEquals("(A124delA)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthOneWithDeletedSeqLength() {
		ProteinDeletion del = ProteinDeletion.buildWithLength(true, "A", 123, "A", 123, 1);
		Assert.assertEquals("(A124del1)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthOneWithBlankDeletedSeqSpec() {
		ProteinDeletion del = ProteinDeletion.buildWithoutSeqDescription(true, "A", 123, "A", 123);
		Assert.assertEquals("(A124del)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthTwoWithDeletedSeq() {
		ProteinDeletion del = ProteinDeletion.buildWithSequence(true, "A", 123, "C", 124, "AC");
		Assert.assertEquals("(A124_C125delAC)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthTwoWithDeletedSeqLength() {
		ProteinDeletion del = ProteinDeletion.buildWithLength(true, "A", 123, "C", 124, 2);
		Assert.assertEquals("(A124_C125del2)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
