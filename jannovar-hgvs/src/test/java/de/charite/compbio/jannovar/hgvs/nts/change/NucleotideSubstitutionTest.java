package de.charite.compbio.jannovar.hgvs.nts.change;

import de.charite.compbio.jannovar.hgvs.nts.NucleotidePointLocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NucleotideSubstitutionTest {

	private NucleotideSubstitution subWithOffset;
	private NucleotideSubstitution subWithoutOffset;

	@BeforeEach
	public void setUp() {
		subWithOffset = new NucleotideSubstitution(true, new NucleotidePointLocation(10, 1, false), "A", "T");
		subWithoutOffset = new NucleotideSubstitution(true, new NucleotidePointLocation(10, 0, false), "A", "T");
	}

	@Test
	public void testBuildWithOffset() {
		Assertions.assertEquals(subWithOffset, NucleotideSubstitution.buildWithOffset(true, 10, 1, "A", "T"));
	}

	@Test
	public void testBuildWithoutOffset() {
		Assertions.assertEquals(subWithoutOffset, NucleotideSubstitution.buildWithOffset(true, 10, 0, "A", "T"));
	}

	@Test
	public void testToStringWithOffset() {
		Assertions.assertEquals("(11+1A>T)", subWithOffset.toHGVSString());
	}

	@Test
	public void testToStringWithoutOffset() {
		Assertions.assertEquals("(11A>T)", subWithoutOffset.toHGVSString());
	}

}
