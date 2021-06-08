package de.charite.compbio.jannovar.hgvs.protein;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProteinSeqSpecificationTest {

	@Test
	public void testConstructWithCount() {
		ProteinSeqDescription spec = new ProteinSeqDescription(4);

		Assertions.assertEquals(null, spec.getAminoAcids());
		Assertions.assertEquals(4, spec.length());
		Assertions.assertFalse(spec.isBlank());

		Assertions.assertEquals("4", spec.toHGVSString());
		Assertions.assertEquals("4", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("4", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testConstructWithAASeq() {
		ProteinSeqDescription spec = new ProteinSeqDescription("ACGT");

		Assertions.assertEquals("ACGT", spec.getAminoAcids());
		Assertions.assertEquals(4, spec.length());
		Assertions.assertFalse(spec.isBlank());

		Assertions.assertEquals("AlaCysGlyThr", spec.toHGVSString());
		Assertions.assertEquals("AlaCysGlyThr", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("ACGT", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testConstructBlank() {
		ProteinSeqDescription spec = new ProteinSeqDescription();

		Assertions.assertEquals(null, spec.getAminoAcids());
		Assertions.assertEquals(-1, spec.length());
		Assertions.assertTrue(spec.isBlank());

		Assertions.assertEquals("", spec.toHGVSString());
		Assertions.assertEquals("", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
