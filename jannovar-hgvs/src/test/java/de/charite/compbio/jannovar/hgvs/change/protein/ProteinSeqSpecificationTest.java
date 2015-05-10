package de.charite.compbio.jannovar.hgvs.change.protein;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.change.AminoAcidCode;

public class ProteinSeqSpecificationTest {

	@Test
	public void testConstructWithCount() {
		ProteinSeqSpecification spec = new ProteinSeqSpecification(4);

		Assert.assertEquals(null, spec.getAminoAcids());
		Assert.assertEquals(4, spec.length());
		Assert.assertFalse(spec.isBlank());

		Assert.assertEquals("4", spec.toHGVSString());
		Assert.assertEquals("4", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("4", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testConstructWithAASeq() {
		ProteinSeqSpecification spec = new ProteinSeqSpecification("ACGT");

		Assert.assertEquals("ACGT", spec.getAminoAcids());
		Assert.assertEquals(4, spec.length());
		Assert.assertFalse(spec.isBlank());

		Assert.assertEquals("AlaCysGlyThr", spec.toHGVSString());
		Assert.assertEquals("AlaCysGlyThr", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("ACGT", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testConstructBlank() {
		ProteinSeqSpecification spec = new ProteinSeqSpecification();

		Assert.assertEquals(null, spec.getAminoAcids());
		Assert.assertEquals(-1, spec.length());
		Assert.assertTrue(spec.isBlank());

		Assert.assertEquals("", spec.toHGVSString());
		Assert.assertEquals("", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
