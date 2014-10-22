package jannovar.pedigree;

import static org.junit.Assert.*;
import jannovar.exception.PedParseException;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the Pedigree class.
 * 
 * There are further tests PedigreeARTest and PedigreeADTest that check more complex cases.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class PedigreeTest {

	// Simple tests on a single-sample pedigree as constructed with Pedigree.constructSingleSamplePedigree().
	@Test
	public void testConstructSingleSamplePedigree() {
		Pedigree pedigree = Pedigree.constructSingleSamplePedigree("INDIVIDUAL");
		
		Assert.assertEquals("INDIVIDUAL", pedigree.getSingleSampleName());
		Assert.assertEquals(1, pedigree.getPedigreeSize());
		Assert.assertEquals(1, pedigree.getNumberOfIndividualsInPedigree());
		Assert.assertEquals(0, pedigree.getNumberOfParentsInPedigree());
		Assert.assertEquals(1, pedigree.getNumberOfAffectedsInPedigree());
		Assert.assertEquals(0, pedigree.getNumberOfUnaffectedsInPedigree());
		Assert.assertEquals("FAMILY:INDIVIDUAL[affected;male]", pedigree.getPedigreeSummary());
		
		Person p = pedigree.getPerson("INDIVIDUAL");
		Assert.assertEquals("FAMILY", p.getFamilyID());
		Assert.assertEquals(null, p.getFatherID());
		Assert.assertEquals(null, p.getMotherID());
		Assert.assertFalse(p.isMale());
		Assert.assertFalse(p.isFemale());
		Assert.assertTrue(p.isAffected());     // AFFECTED
		Assert.assertFalse(p.isUnaffected());  // AFFECTED
		Assert.assertTrue(p.isFounder());
	}
	
	@Test
	public void testPedigreeOnePerson() throws PedParseException {
		ArrayList<Person> pList = new ArrayList<Person>(); 
		pList.add(new Person("FAM", "PERSON", null, null, "2", "1"));
		Pedigree pedigree = new Pedigree(pList, "FAM");
		
		Assert.assertEquals("PERSON", pedigree.getSingleSampleName());
		Assert.assertEquals(1, pedigree.getPedigreeSize());
		Assert.assertEquals(1, pedigree.getNumberOfIndividualsInPedigree());
		Assert.assertEquals(0, pedigree.getNumberOfParentsInPedigree());
		Assert.assertEquals(1, pedigree.getNumberOfAffectedsInPedigree());
		Assert.assertEquals(0, pedigree.getNumberOfUnaffectedsInPedigree());
		Assert.assertEquals("FAM:PERSON[unaffected;female]", pedigree.getPedigreeSummary());
		
		Person p = pedigree.getPerson("PERSON");
		Assert.assertEquals("FAM", p.getFamilyID());
		Assert.assertEquals(null, p.getFatherID());
		Assert.assertEquals(null, p.getMotherID());
		Assert.assertFalse(p.isMale());
		Assert.assertTrue(p.isFemale());
		Assert.assertFalse(p.isAffected());     // UNAFFECTED
		Assert.assertTrue(p.isUnaffected());  // UNAFFECTED
		Assert.assertTrue(p.isFounder());
	}
	
	// TODO(holtgrem): Add many more tests.
	
	// larger family, check counts, is* functions
	// larger faimly, adjustSampleOrderInPedFile
	// checd addIndividual
	// check compatibility functions
	// Genotype call checks with atomic cases
	// check existing tests
}
