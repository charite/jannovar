package de.charite.compbio.jannovar.hgvs.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinSilentChange;

public class ProteinSilentChangeTest {

	@Test
	public void test() {
		Assert.assertEquals("(=)", new ProteinSilentChange(true).toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("=", new ProteinSilentChange(false).toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("(=)", new ProteinSilentChange(true).toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("=", new ProteinSilentChange(false).toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
