package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NucleotideMiscChangeTypeTest {

	@Test
	public void testSameAsDNA() {
		Assertions.assertEquals("(?)", NucleotideMiscChangeType.SAME_AS_DNA.toHGVSString(false));
		Assertions.assertEquals("(?)", NucleotideMiscChangeType.SAME_AS_DNA.toHGVSString(true));
	}

	@Test
	public void testUnknownEffect() {
		Assertions.assertEquals("?", NucleotideMiscChangeType.UNKNOWN_EFFECT.toHGVSString(false));
		Assertions.assertEquals("?", NucleotideMiscChangeType.UNKNOWN_EFFECT.toHGVSString(true));
	}

	@Test
	public void testSplicingAffected() {
		Assertions.assertEquals("spl?", NucleotideMiscChangeType.SPLICING_AFFECTED.toHGVSString(false));
		Assertions.assertEquals("(spl?)", NucleotideMiscChangeType.SPLICING_AFFECTED.toHGVSString(true));
	}

	@Test
	public void testNoChange() {
		Assertions.assertEquals("=", NucleotideMiscChangeType.NO_CHANGE.toHGVSString(false));
		Assertions.assertEquals("(=)", NucleotideMiscChangeType.NO_CHANGE.toHGVSString(true));
	}

	@Test
	public void testNoRna() {
		Assertions.assertEquals("0", NucleotideMiscChangeType.NO_RNA.toHGVSString(false));
		Assertions.assertEquals("(0)", NucleotideMiscChangeType.NO_RNA.toHGVSString(true));
	}

}
