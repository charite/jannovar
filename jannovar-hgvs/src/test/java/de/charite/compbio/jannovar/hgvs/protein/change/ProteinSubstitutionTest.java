package de.charite.compbio.jannovar.hgvs.protein.change;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinPointLocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProteinSubstitutionTest {

	private ProteinSubstitution sub1;
	private ProteinSubstitution sub2;
	private ProteinSubstitution sub3;

	@BeforeEach
	public void setUp() {
		sub1 = new ProteinSubstitution(true, new ProteinPointLocation("A", 123, 0, false), "G");
		sub2 = new ProteinSubstitution(true, new ProteinPointLocation("A", 123, 0, false), "G");
		sub3 = new ProteinSubstitution(true, new ProteinPointLocation("A", 123, 0, false), "T");
	}

	@Test
	public void testEquals() {
		Assertions.assertTrue(sub1.equals(sub2));
		Assertions.assertTrue(sub2.equals(sub1));
		Assertions.assertFalse(sub1.equals(sub3));
		Assertions.assertFalse(sub3.equals(sub1));
	}

	@Test
	public void testToHGVSString() {
		Assertions.assertEquals("(A124G)", sub1.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assertions.assertEquals("(Ala124Gly)", sub1.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("(A124G)", sub1.toHGVSString());
	}

	@Test
	public void testFactoryMethod() {
		Assertions.assertEquals(sub1, ProteinSubstitution.build(true, "A", 123, "G"));
	}
}
