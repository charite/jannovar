package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.nts.NucleotidePointLocation;

public class NucleotideSubstitutionTest {

	private NucleotideSubstitution subWithOffset;
	private NucleotideSubstitution subWithoutOffset;

	@Before
	public void setUp() {
		subWithOffset = new NucleotideSubstitution(true, new NucleotidePointLocation(10, 1, false), "A", "T");
		subWithoutOffset = new NucleotideSubstitution(true, new NucleotidePointLocation(10, 0, false), "A", "T");
	}

	@Test
	public void testBuildWithOffset() {
		Assert.assertEquals(subWithOffset, NucleotideSubstitution.buildWithOffset(true, 10, 1, "A", "T"));
	}

	@Test
	public void testBuildWithoutOffset() {
		Assert.assertEquals(subWithoutOffset, NucleotideSubstitution.buildWithOffset(true, 10, 0, "A", "T"));
	}

	@Test
	public void testToStringWithOffset() {
		Assert.assertEquals("(11+1A>T)", subWithOffset.toHGVSString());
	}

	@Test
	public void testToStringWithoutOffset() {
		Assert.assertEquals("(11A>T)", subWithoutOffset.toHGVSString());
	}

}
