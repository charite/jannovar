package de.charite.compbio.jannovar.pedigree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for the Person class.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class PersonTest {

	@Test
	public void testIsFounderTrue() {
		Person index = new Person("name", null, null, Sex.MALE, Disease.AFFECTED);
		Assertions.assertTrue(index.isFounder());
	}

	@Test
	public void testIsFounderFalse() {
		Person father = new Person("father", null, null, Sex.MALE, Disease.AFFECTED);
		Person index = new Person("name", father, null, Sex.MALE, Disease.AFFECTED);
		Assertions.assertFalse(index.isFounder());
	}
}
