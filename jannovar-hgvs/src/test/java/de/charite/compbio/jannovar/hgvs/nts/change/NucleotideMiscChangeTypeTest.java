package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.Assert;
import org.junit.Test;

public class NucleotideMiscChangeTypeTest {

	@Test
	public void testSameAsDNA() {
		Assert.assertEquals("(?)", NucleotideMiscChangeType.SAME_AS_DNA.toHGVSString(false));
		Assert.assertEquals("(?)", NucleotideMiscChangeType.SAME_AS_DNA.toHGVSString(true));
	}

	@Test
	public void testUnknownEffect() {
		Assert.assertEquals("?", NucleotideMiscChangeType.UNKNOWN_EFFECT.toHGVSString(false));
		Assert.assertEquals("?", NucleotideMiscChangeType.UNKNOWN_EFFECT.toHGVSString(true));
	}

	@Test
	public void testSplicingAffected() {
		Assert.assertEquals("spl?", NucleotideMiscChangeType.SPLICING_AFFECTED.toHGVSString(false));
		Assert.assertEquals("(spl?)", NucleotideMiscChangeType.SPLICING_AFFECTED.toHGVSString(true));
	}

	@Test
	public void testNoChange() {
		Assert.assertEquals("=", NucleotideMiscChangeType.NO_CHANGE.toHGVSString(false));
		Assert.assertEquals("(=)", NucleotideMiscChangeType.NO_CHANGE.toHGVSString(true));
	}

	@Test
	public void testNoRna() {
		Assert.assertEquals("0", NucleotideMiscChangeType.NO_RNA.toHGVSString(false));
		Assert.assertEquals("(0)", NucleotideMiscChangeType.NO_RNA.toHGVSString(true));
	}

}
