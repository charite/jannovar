package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NucleotideMiscChangeTest {

	@Test
	public void testSameAsDNA() {
		Assertions.assertEquals("(?)", NucleotideMiscChange.build(false, NucleotideMiscChangeType.SAME_AS_DNA)
			.toHGVSString());
		Assertions.assertEquals("(?)", NucleotideMiscChange.build(true, NucleotideMiscChangeType.SAME_AS_DNA)
			.toHGVSString());
	}

	@Test
	public void testUnknownEffect() {
		Assertions.assertEquals("?", NucleotideMiscChange.build(false, NucleotideMiscChangeType.UNKNOWN_EFFECT)
			.toHGVSString());
		Assertions.assertEquals("?", NucleotideMiscChange.build(true, NucleotideMiscChangeType.UNKNOWN_EFFECT)
			.toHGVSString());
	}

	@Test
	public void testSplicingAffected() {
		Assertions.assertEquals("spl?", NucleotideMiscChange.build(false, NucleotideMiscChangeType.SPLICING_AFFECTED)
			.toHGVSString());
		Assertions.assertEquals("(spl?)", NucleotideMiscChange.build(true, NucleotideMiscChangeType.SPLICING_AFFECTED)
			.toHGVSString());
	}

	@Test
	public void testNoChange() {
		Assertions.assertEquals("=", NucleotideMiscChange.build(false, NucleotideMiscChangeType.NO_CHANGE).toHGVSString());
		Assertions.assertEquals("(=)", NucleotideMiscChange.build(true, NucleotideMiscChangeType.NO_CHANGE).toHGVSString());
	}

	@Test
	public void testNoRna() {
		Assertions.assertEquals("0", NucleotideMiscChange.build(false, NucleotideMiscChangeType.NO_RNA).toHGVSString());
		Assertions.assertEquals("(0)", NucleotideMiscChange.build(true, NucleotideMiscChangeType.NO_RNA).toHGVSString());
	}

}
