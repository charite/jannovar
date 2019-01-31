package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the Person class.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class PersonTest {

	@Test
	public void testIsFounderTrue() {
		Person index = new Person("name", null, null, Sex.MALE, Disease.AFFECTED);
		Assert.assertTrue(index.isFounder());
	}

	@Test
	public void testIsFounderFalse() {
		Person father = new Person("father", null, null, Sex.MALE, Disease.AFFECTED);
		Person index = new Person("name", father, null, Sex.MALE, Disease.AFFECTED);
		Assert.assertFalse(index.isFounder());
	}
}
