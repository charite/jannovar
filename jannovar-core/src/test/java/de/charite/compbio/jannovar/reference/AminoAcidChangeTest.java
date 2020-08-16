package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AminoAcidChangeTest {

	/**
	 * the amino acid strings to use as the references
	 */
	String ref, ref2;

	@Before
	public void setUp() throws Exception {
		this.ref = "LLLCCCLLLCCC";
		this.ref2 = "CLTCLTCTTCL";
	}

	@Test
	public void testShiftNoRefChange() {
		AminoAcidChange origChange = new AminoAcidChange(3, "CC", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.normalizeDeletion(ref, "LLLCLLLCCC", origChange);
		Assert.assertEquals(new AminoAcidChange(4, "CC", ""), modChange);
	}

	@Test
	public void testShiftNoRefChangeEnd() {
		AminoAcidChange origChange = new AminoAcidChange(9, "CC", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.normalizeDeletion(ref, "LLLCCCLLLC", origChange);
		Assert.assertEquals(new AminoAcidChange(10, "CC", ""), modChange);
	}

	@Test
	public void testShiftRefChange() {
		AminoAcidChange origChange = new AminoAcidChange(3, "CLT", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.normalizeDeletion(ref2, "CLTCTTCL", origChange);
		Assert.assertEquals(new AminoAcidChange(4, "LTC", ""), modChange);
	}

	@Test
	public void testShiftFrameshiftNoInsAA() {
		AminoAcidChange origChange = new AminoAcidChange(3, "CQY", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.normalizeDeletion("LLLCQYCLLL", "LLLCQYVA", origChange);
				Assert.assertEquals(new AminoAcidChange(6, "CLL", ""), modChange);
	}

	@Test
	public void testShiftFrameshiftInsAA() {
		AminoAcidChange origChange = new AminoAcidChange(3, "CQY", "C");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.normalizeDeletion("LLLCQYCLLL", "LLLCQYVA", origChange);
		Assert.assertEquals(new AminoAcidChange(6, "CLL", "V"), modChange);
	}

	@Test
	public void testNoShift() {
		AminoAcidChange origChange = new AminoAcidChange(3, "CCC", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.normalizeDeletion(ref, "LLLLLLCCC", origChange);
		Assert.assertEquals(new AminoAcidChange(3, "CCC", ""), modChange);
	}

	@Test
	public void testNoShiftEnd() {
		AminoAcidChange origChange = new AminoAcidChange(9, "CCC", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.normalizeDeletion(ref, "LLLCCCLLL", origChange);
		Assert.assertEquals(new AminoAcidChange(9, "CCC", ""), modChange);
	}

}
