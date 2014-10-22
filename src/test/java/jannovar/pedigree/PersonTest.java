package jannovar.pedigree;

import static org.junit.Assert.*;
import jannovar.exception.PedParseException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the Person class.
 *  
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class PersonTest {

	/** Test with an entry with invalid sex. */
	@Test(expected=PedParseException.class)
	public void testInValidPersonSex() throws PedParseException {
		new Person("FAM", "PERSON", "0", "0", "3", "0");
	}

	/** Test with an entry with invalid disease */
	@Test(expected=PedParseException.class)
	public void testInvalidPersonDisease() throws PedParseException {
		new Person("FAM", "PERSON", "0", "0", "1", "3");
	}

	/** Tests a few valid cases. */
	@Test
	public void testValidPerson_Mother() throws PedParseException {
		Person mother = new Person("FAM", "MOTHER", null, null, "2", "2");
		Assert.assertEquals("FAM", mother.getFamilyID());
		Assert.assertEquals(null, mother.getFatherID());
		Assert.assertEquals(null, mother.getMotherID());
		Assert.assertFalse(mother.isMale());
		Assert.assertTrue(mother.isFemale());
		Assert.assertTrue(mother.isAffected());    // AFFECTED
		Assert.assertFalse(mother.isUnaffected());  // AFFECTED
		Assert.assertTrue(mother.isFounder());
	}
	
	@Test
	public void testValidPerson_Father() throws PedParseException {
		Person father = new Person("FAM", "FATHER", null, null, "1", "2"); 
		Assert.assertEquals("FAM", father.getFamilyID());
		Assert.assertEquals(null, father.getFatherID());
		Assert.assertEquals(null, father.getMotherID());
		Assert.assertTrue(father.isMale());
		Assert.assertFalse(father.isFemale());
		Assert.assertTrue(father.isAffected());     // AFFECTED
		Assert.assertFalse(father.isUnaffected());  // AFFECTED
		Assert.assertTrue(father.isFounder());
	}
	
	@Test
	public void testValidPerson_Daughter() throws PedParseException {
		Person daughter = new Person("FAM", "DAUGHTER", "FATHER", "MOTHER", "2", "1");
		Assert.assertEquals("FAM", daughter.getFamilyID());
		Assert.assertEquals("FATHER", daughter.getFatherID());
		Assert.assertEquals("MOTHER", daughter.getMotherID());
		Assert.assertFalse(daughter.isMale());
		Assert.assertTrue(daughter.isFemale());
		Assert.assertFalse(daughter.isAffected());   // UNAFFECTED
		Assert.assertTrue(daughter.isUnaffected());  // UNAFFECTED
		Assert.assertFalse(daughter.isFounder());
	}
	
	@Test
	public void testValidPerson_Son() throws PedParseException {
		Person son = new Person("FAM", "SON", "FATHER", "MOTHER", "1", "0");
		Assert.assertEquals("FAM", son.getFamilyID());
		Assert.assertEquals("FATHER", son.getFatherID());
		Assert.assertEquals("MOTHER", son.getMotherID());
		Assert.assertTrue(son.isMale());
		Assert.assertFalse(son.isFemale());
		Assert.assertFalse(son.isAffected());   // UNKNOWN
		Assert.assertFalse(son.isUnaffected());  // UNKNOWN
		Assert.assertFalse(son.isFounder());
	}
}
