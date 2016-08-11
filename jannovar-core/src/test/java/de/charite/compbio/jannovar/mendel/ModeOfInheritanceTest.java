package de.charite.compbio.jannovar.mendel;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.mendel.ModeOfInheritance;

public class ModeOfInheritanceTest {

	@Test
	public void testSize() {
		Assert.assertEquals(6, ModeOfInheritance.values().length);
	}

}
