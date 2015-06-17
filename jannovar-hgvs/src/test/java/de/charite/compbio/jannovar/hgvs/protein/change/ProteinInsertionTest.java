package de.charite.compbio.jannovar.hgvs.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinSeqDescription;

public class ProteinInsertionTest {

	@Test
	public void testLengthOneWithInsertSeq() {
		ProteinInsertion ins = ProteinInsertion.buildWithSequence(true, "A", 123, "C", 124, "TY");
		Assert.assertEquals("(A124_C125insTY)", ins.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthOneWithDeletedSeqLength() {
		ProteinInsertion ins = ProteinInsertion.buildWithLength(true, "A", 123, "C", 124, 2);
		Assert.assertEquals("(A124_C125ins2)", ins.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthOneWithBlankDeletedSeqSpec() {
		ProteinInsertion ins = ProteinInsertion.buildWithSeqDescription(true, "A", 123, "C", 124,
				new ProteinSeqDescription());
		Assert.assertEquals("(A124_C125ins)", ins.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testLengthOneWithoutDeletedSeqSpec() {
		ProteinInsertion ins = ProteinInsertion.buildWithoutSeqDescription(true, "A", 123, "C", 124);
		Assert.assertEquals("(A124_C125ins)", ins.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
