package de.charite.compbio.jannovar.hgvs.change.protein;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.change.AminoAcidCode;

public class ProteinIndelTest {

	@Test
	public void testWithSequenceDescription() {
		ProteinIndel del = ProteinIndel.build(true, "A", 123, "A", 123, "A", "CT");
		Assert.assertEquals("(A124delAinsCT)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testWithLengthDescription() {
		ProteinIndel del = ProteinIndel.build(true, "A", 123, "A", 123, 1, 3);
		Assert.assertEquals("(A124del1ins3)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testWithBlankDescription() {
		ProteinIndel del = ProteinIndel.build(true, "A", 123, "A", 123);
		Assert.assertEquals("(A124delins)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testWithProteinSeqDescription() {
		ProteinIndel del = ProteinIndel.build(true, "A", 123, "C", 124, new ProteinSeqDescription(),
				new ProteinSeqDescription("AC"));
		Assert.assertEquals("(A124_C125delinsAC)", del.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
