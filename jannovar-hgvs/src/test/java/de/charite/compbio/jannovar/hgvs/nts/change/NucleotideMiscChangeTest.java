package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.Assert;
import org.junit.Test;

public class NucleotideMiscChangeTest {

	@Test
	public void testSameAsDNA() {
		Assert.assertEquals("(?)", NucleotideMiscChange.build(false, NucleotideMiscChangeType.SAME_AS_DNA)
				.toHGVSString());
		Assert.assertEquals("(?)", NucleotideMiscChange.build(true, NucleotideMiscChangeType.SAME_AS_DNA)
				.toHGVSString());
	}

	@Test
	public void testUnknownEffect() {
		Assert.assertEquals("?", NucleotideMiscChange.build(false, NucleotideMiscChangeType.UNKNOWN_EFFECT)
				.toHGVSString());
		Assert.assertEquals("?", NucleotideMiscChange.build(true, NucleotideMiscChangeType.UNKNOWN_EFFECT)
				.toHGVSString());
	}

	@Test
	public void testSplicingAffected() {
		Assert.assertEquals("spl?", NucleotideMiscChange.build(false, NucleotideMiscChangeType.SPLICING_AFFECTED)
				.toHGVSString());
		Assert.assertEquals("(spl?)", NucleotideMiscChange.build(true, NucleotideMiscChangeType.SPLICING_AFFECTED)
				.toHGVSString());
	}

	@Test
	public void testNoChange() {
		Assert.assertEquals("=", NucleotideMiscChange.build(false, NucleotideMiscChangeType.NO_CHANGE).toHGVSString());
		Assert.assertEquals("(=)", NucleotideMiscChange.build(true, NucleotideMiscChangeType.NO_CHANGE).toHGVSString());
	}

	@Test
	public void testNoRna() {
		Assert.assertEquals("0", NucleotideMiscChange.build(false, NucleotideMiscChangeType.NO_RNA).toHGVSString());
		Assert.assertEquals("(0)", NucleotideMiscChange.build(true, NucleotideMiscChangeType.NO_RNA).toHGVSString());
	}

}
