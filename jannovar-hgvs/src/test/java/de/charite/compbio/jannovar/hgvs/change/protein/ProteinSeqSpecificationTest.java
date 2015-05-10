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

		Assert.assertEquals("4", spec.toHGVSString());
		Assert.assertEquals("4", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("4", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testConstructWithAASeq() {
		ProteinSeqSpecification spec = new ProteinSeqSpecification("ACGT");

		Assert.assertEquals("ACGT", spec.getAminoAcids());
		Assert.assertEquals(4, spec.length());

		Assert.assertEquals("AlaCysGlyThr", spec.toHGVSString());
		Assert.assertEquals("AlaCysGlyThr", spec.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("ACGT", spec.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
