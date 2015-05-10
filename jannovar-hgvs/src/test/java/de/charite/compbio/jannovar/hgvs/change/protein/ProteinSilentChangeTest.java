package de.charite.compbio.jannovar.hgvs.change.protein;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.change.AminoAcidCode;

public class ProteinSilentChangeTest {

	@Test
	public void test() {
		Assert.assertEquals("(=)", new ProteinSilentChange(true).toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("=", new ProteinSilentChange(false).toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("(=)", new ProteinSilentChange(true).toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("=", new ProteinSilentChange(false).toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
