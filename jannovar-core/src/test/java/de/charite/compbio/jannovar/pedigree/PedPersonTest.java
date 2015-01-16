package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.PedPerson;
import de.charite.compbio.jannovar.pedigree.Sex;

public class PedPersonTest {

	@Test
	public void testIsFounderTrue() {
		PedPerson person = new PedPerson("fam", "name", "0", "0", Sex.MALE, Disease.AFFECTED);
		Assert.assertTrue(person.isFounder());
	}

	@Test
	public void testIsFounderFalse() {
		PedPerson person = new PedPerson("fam", "name", "father", "0", Sex.MALE, Disease.AFFECTED);
		Assert.assertFalse(person.isFounder());
	}

}
