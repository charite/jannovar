package de.charite.compbio.jannovar.hgvs.protein;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinSeqDescription;

public class ProteinSeqSpecificationTest {

	@Test
	public void testConstructWithCount() {
		ProteinSeqDescription spec = new ProteinSeqDescription(4);

		Assert.assertEquals(null, spec.getAminoAcids());
		Assert.assertEquals(4, spec.length());
		Assert.assertFalse(spec.isBlank());

		Assert.assertEquals("4", spec.toHGVSString());
		Assert.assertEquals("4", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("4", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testConstructWithAASeq() {
		ProteinSeqDescription spec = new ProteinSeqDescription("ACGT");

		Assert.assertEquals("ACGT", spec.getAminoAcids());
		Assert.assertEquals(4, spec.length());
		Assert.assertFalse(spec.isBlank());

		Assert.assertEquals("AlaCysGlyThr", spec.toHGVSString());
		Assert.assertEquals("AlaCysGlyThr", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("ACGT", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testConstructBlank() {
		ProteinSeqDescription spec = new ProteinSeqDescription();

		Assert.assertEquals(null, spec.getAminoAcids());
		Assert.assertEquals(-1, spec.length());
		Assert.assertTrue(spec.isBlank());

		Assert.assertEquals("", spec.toHGVSString());
		Assert.assertEquals("", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
