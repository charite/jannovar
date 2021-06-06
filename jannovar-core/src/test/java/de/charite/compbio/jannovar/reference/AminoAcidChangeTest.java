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
		AminoAcidChange modChange = AminoAcidChangeNormalizer.shiftSynonymousChange(origChange, ref, "LLLCLLLCCC");
		Assert.assertEquals(new AminoAcidChange(4, "CC", ""), modChange);
	}

	@Test
	public void testShiftNoRefChangeEnd() {
		AminoAcidChange origChange = new AminoAcidChange(9, "CC", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.shiftSynonymousChange(origChange, ref, "LLLCCCLLLC");
		Assert.assertEquals(new AminoAcidChange(10, "CC", ""), modChange);
	}

	@Test
	public void testShiftRefChange() {
		AminoAcidChange origChange = new AminoAcidChange(3, "CLT", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.shiftSynonymousChange(origChange, ref2, "CLTCTTCL");
		Assert.assertEquals(new AminoAcidChange(4, "LTC", ""), modChange);
	}

	@Test
	public void testShiftDeletionFrameshiftNoInsAA() {
		AminoAcidChange origChange = new AminoAcidChange(3, "CQY", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.shiftSynonymousChange(origChange, "LLLCQYCLLL", "LLLCQYVA");
				Assert.assertEquals(new AminoAcidChange(6, "CLL", ""), modChange);
	}

	@Test
	public void testShiftDeletionFrameshiftInsAA() {
		AminoAcidChange origChange = new AminoAcidChange(3, "CQY", "C");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.shiftSynonymousChange(origChange, "LLLCQYCLLL", "LLLCQYVA");
		Assert.assertEquals(new AminoAcidChange(6, "CL", ""), modChange);
	}

	@Test
	public void testShiftInsertionNoFrameshift() {
		AminoAcidChange origChange = new AminoAcidChange(3, "C", "CQY");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.shiftSynonymousChange(origChange, "LLLCQYCLLL", "LLLCQYQYCLLL");
		Assert.assertEquals(new AminoAcidChange(6, "", "QY"), modChange);
	}

	@Test
	public void testShiftInsertionFrameshift() {
		AminoAcidChange origChange = new AminoAcidChange(3, "C", "CQY");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.shiftSynonymousChange(origChange, "LLLCQYCLLL", "LLLCQYQYVAAA");
		Assert.assertEquals(new AminoAcidChange(6, "", "QY"), modChange);
	}

	@Test
	public void testShiftSubstitutionNoFrameshift() {
		AminoAcidChange origChange = new AminoAcidChange(3, "CQYC", "CQLC");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.shiftSynonymousChange(origChange, "LLLCQYCLLL", "LLLCQLCLLL");
		Assert.assertEquals(new AminoAcidChange(5, "Y", "L"), modChange);
	}

	@Test
	public void testShiftSubstitutionFrameshift() {
		AminoAcidChange origChange = new AminoAcidChange(3, "CQYC", "CQYQ");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.shiftSynonymousChange(origChange, "LLLCQYCLLL", "LLLCQYQYVAAA");
		Assert.assertEquals(new AminoAcidChange(6, "C", "Q"), modChange);
	}

	@Test
	public void testNoShift() {
		AminoAcidChange origChange = new AminoAcidChange(3, "CCC", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.shiftSynonymousChange(origChange, ref, "LLLLLLCCC");
		Assert.assertEquals(new AminoAcidChange(3, "CCC", ""), modChange);
	}

	@Test
	public void testNoShiftEnd() {
		AminoAcidChange origChange = new AminoAcidChange(9, "CCC", "");
		AminoAcidChange modChange = AminoAcidChangeNormalizer.shiftSynonymousChange(origChange, ref, "LLLCCCLLL");
		Assert.assertEquals(new AminoAcidChange(9, "CCC", ""), modChange);
	}

}
