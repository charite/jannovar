package de.charite.compbio.jannovar.hgvs.nts;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;

public class NucleotideSeqSpecificationTest {

	@Test
	public void testConstructWithCount() {
		NucleotideSeqDescription spec = new NucleotideSeqDescription(4);

		Assert.assertEquals(null, spec.getNucleotides());
		Assert.assertEquals(4, spec.length());
		Assert.assertFalse(spec.isBlank());

		Assert.assertEquals("4", spec.toHGVSString());
		Assert.assertEquals("4", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("4", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testConstructWithAASeq() {
		NucleotideSeqDescription spec = new NucleotideSeqDescription("ACGT");

		Assert.assertEquals("ACGT", spec.getNucleotides());
		Assert.assertEquals(4, spec.length());
		Assert.assertFalse(spec.isBlank());

		Assert.assertEquals("ACGT", spec.toHGVSString());
		Assert.assertEquals("ACGT", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("ACGT", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testConstructBlank() {
		NucleotideSeqDescription spec = new NucleotideSeqDescription();

		Assert.assertEquals(null, spec.getNucleotides());
		Assert.assertEquals(-1, spec.length());
		Assert.assertTrue(spec.isBlank());

		Assert.assertEquals("", spec.toHGVSString());
		Assert.assertEquals("", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
