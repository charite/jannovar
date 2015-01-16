package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.reference.AminoAcidChange;
import de.charite.compbio.jannovar.reference.AminoAcidChangeNormalizer;

public class AminoAcidChangeTest {

	/** the amino acid strings to use as the references */
	String ref, ref2;

	@Before
	public void setUp() throws Exception {
		this.ref = "AAACCCAAACCC";
		this.ref2 = "CATCATCTTCA";
	}

	@Test
	public void testShiftNoRefChange() {
		AminoAcidChange origChange = new AminoAcidChange(3, "CC", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.normalizeDeletion(ref, origChange);
		Assert.assertEquals(new AminoAcidChange(4, "CC", ""), modChange);
	}

	@Test
	public void testShiftNoRefChangeEnd() {
		AminoAcidChange origChange = new AminoAcidChange(9, "CC", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.normalizeDeletion(ref, origChange);
		Assert.assertEquals(new AminoAcidChange(10, "CC", ""), modChange);
	}

	@Test
	public void testShiftRefChange() {
		AminoAcidChange origChange = new AminoAcidChange(3, "CAT", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.normalizeDeletion(ref2, origChange);
		Assert.assertEquals(new AminoAcidChange(4, "ATC", ""), modChange);
	}

	@Test
	public void testNoShift() {
		AminoAcidChange origChange = new AminoAcidChange(3, "CCC", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.normalizeDeletion(ref, origChange);
		Assert.assertEquals(new AminoAcidChange(3, "CCC", ""), modChange);
	}

	@Test
	public void testNoShiftEnd() {
		AminoAcidChange origChange = new AminoAcidChange(9, "CCC", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.normalizeDeletion(ref, origChange);
		Assert.assertEquals(new AminoAcidChange(9, "CCC", ""), modChange);
	}

}
