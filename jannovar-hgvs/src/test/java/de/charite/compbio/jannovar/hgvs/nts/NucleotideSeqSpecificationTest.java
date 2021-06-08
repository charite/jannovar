package de.charite.compbio.jannovar.hgvs.nts;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NucleotideSeqSpecificationTest {

	@Test
	public void testConstructWithCount() {
		NucleotideSeqDescription spec = new NucleotideSeqDescription(4);

		Assertions.assertEquals(null, spec.getNucleotides());
		Assertions.assertEquals(4, spec.length());
		Assertions.assertFalse(spec.isBlank());

		Assertions.assertEquals("4", spec.toHGVSString());
		Assertions.assertEquals("4", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("4", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testConstructWithAASeq() {
		NucleotideSeqDescription spec = new NucleotideSeqDescription("ACGT");

		Assertions.assertEquals("ACGT", spec.getNucleotides());
		Assertions.assertEquals(4, spec.length());
		Assertions.assertFalse(spec.isBlank());

		Assertions.assertEquals("ACGT", spec.toHGVSString());
		Assertions.assertEquals("ACGT", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("ACGT", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testConstructBlank() {
		NucleotideSeqDescription spec = new NucleotideSeqDescription();

		Assertions.assertEquals(null, spec.getNucleotides());
		Assertions.assertEquals(-1, spec.length());
		Assertions.assertTrue(spec.isBlank());

		Assertions.assertEquals("", spec.toHGVSString());
		Assertions.assertEquals("", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
