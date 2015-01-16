package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.pedigree.ModeOfInheritance;

public class ModeOfInheritanceTest {

	@Test
	public void testSize() {
		Assert.assertEquals(5, ModeOfInheritance.values().length);
	}

}
