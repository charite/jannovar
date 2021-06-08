package de.charite.compbio.jannovar.pedigree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PedPersonTest {

	@Test
	public void testIsFounderTrue() {
		PedPerson person = new PedPerson("fam", "name", "0", "0", Sex.MALE, Disease.AFFECTED);
		Assertions.assertTrue(person.isFounder());
	}

	@Test
	public void testIsFounderFalse() {
		PedPerson person = new PedPerson("fam", "name", "father", "0", Sex.MALE, Disease.AFFECTED);
		Assertions.assertFalse(person.isFounder());
	}

}
