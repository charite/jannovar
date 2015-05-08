package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

public class CompatibilityCheckerAutosomalRecessiveMediumTest extends CompatibilityCheckerTestBase {

	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // grandgrandfather
		individuals.add(new PedPerson("ped", "I.2", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // grandgrandmother
		individuals.add(new PedPerson("ped", "II.1", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // grandmother1
		individuals.add(new PedPerson("ped", "II.2", "I.1", "I.2", Sex.MALE, Disease.UNAFFECTED)); // grandfather1
		individuals.add(new PedPerson("ped", "II.3", "I.1", "I.2", Sex.FEMALE, Disease.UNAFFECTED)); // grandmother2
		individuals.add(new PedPerson("ped", "II.4", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // grandfather2
		individuals.add(new PedPerson("ped", "III.1", "II.1", "II.2", Sex.MALE, Disease.UNAFFECTED)); // father
		individuals.add(new PedPerson("ped", "III.2", "II.3", "II.4", Sex.FEMALE, Disease.UNAFFECTED)); // mother
		individuals.add(new PedPerson("ped", "IV.1", "III.1", "III.2", Sex.FEMALE, Disease.AFFECTED)); // child
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
				individuals.build());
		this.pedigree = new Pedigree(pedFileContents, "ped");

		this.names = ImmutableList.of("I.1", "I.2", "II.1", "II.2", "II.3", "II.4", "III.1", "III.2", "IV.1");
	}

	@Test
	public void testSizeOfPedigree() {
		Assert.assertEquals(9, pedigree.getMembers().size());
	}

	@Test
	public void testCaseNegativesOneVariant() throws CompatibilityCheckerException {
		Assert.assertFalse(buildCheckerAR(lst(HET, HET, HET, HET, HET, HET, HET, HET, HET)).run());
		Assert.assertFalse(buildCheckerAR(lst(REF, REF, REF, REF, REF, REF, REF, REF, REF)).run());
		Assert.assertFalse(buildCheckerAR(lst(ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT)).run());
		Assert.assertFalse(buildCheckerAR(lst(UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN)).run());

		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, REF, HET)).run());
		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, HET, HET)).run());
		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, REF, REF)).run());
		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, REF, REF, HET)).run());
		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, REF, REF, REF)).run());

		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, ALT, ALT)).run());
		Assert.assertFalse(buildCheckerAR(lst(HET, ALT, REF, HET, HET, REF, HET, HET, ALT)).run());
		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, ALT, REF, ALT)).run());
		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, ALT, ALT)).run());

		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, ALT, REF, HET, HET, ALT)).run());
		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, ALT, ALT, ALT)).run());
		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, REF, HET, ALT)).run());
		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, REF, ALT)).run());

		Assert.assertFalse(buildCheckerAR(lst(HET, ALT, REF, HET, HET, REF, HET, UKN, ALT)).run());
		Assert.assertFalse(buildCheckerAR(lst(HET, ALT, REF, HET, HET, REF, UKN, ALT, ALT)).run());

		// at least one hom_alt
		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, HET, UKN)).run());

	}

	@Test
	public void testCaseNegativesTwoVariants() throws CompatibilityCheckerException {
		// compound heterozygous
		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, ALT, REF, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run());
		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, ALT, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run());
		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, HET, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run());
		Assert.assertFalse(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, REF, ALT), lst(REF, REF, REF, REF, REF, HET, REF, HET, UKN)).run());
		//If someone unaffected is alt for a comp. het => do not consider
		Assert.assertFalse(buildCheckerAR(lst(HET, ALT, REF, HET, REF, REF, HET, REF, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run());
		//If a second trio is in the pedigree, the same comp, het should not be available
		Assert.assertFalse(buildCheckerAR(lst(REF, HET, REF, HET, REF, REF, HET, REF, HET), lst(HET, REF, REF, HET, REF, HET, REF, HET, HET)).run());
	}

	@Test
	public void testCasePositiveOneVariant() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, HET, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, HET, REF, HET, HET, REF, HET, HET, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, HET, HET, HET, REF, HET, HET, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, HET, HET, HET, ALT)).run());

		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, UKN, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, UKN, HET, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, UKN, UKN, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, ALT)).run());
		
		//not the pedigree, but also possible
		Assert.assertTrue(buildCheckerAR(lst(REF, REF, REF, REF, REF, REF, HET, HET, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, REF, REF, REF, HET, HET, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(REF, HET, REF, REF, REF, REF, HET, HET, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(REF, REF, HET, REF, REF, REF, HET, HET, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(REF, REF, REF, HET, REF, REF, HET, HET, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(REF, REF, REF, REF, HET, REF, HET, HET, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(REF, REF, REF, REF, REF, HET, HET, HET, ALT)).run());
		
	}

	@Test
	public void testCasePositiveTwoVariants() throws CompatibilityCheckerException {
		// compound heterozygous
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, REF, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run());
		Assert.assertTrue(buildCheckerAR(lst(REF, REF, REF, REF, REF, HET, REF, HET, HET), lst(HET, REF, REF, HET, REF, REF, HET, REF, HET)).run());
		
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, HET, HET, REF, REF, HET, REF, HET), lst(REF, HET, REF, REF, REF, HET, REF, HET, HET)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, REF, HET, HET, REF, HET), lst(REF, REF, HET, REF, REF, HET, REF, HET, HET)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, HET, HET, HET, HET, REF, HET, REF, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, REF, HET), lst(REF, HET, HET, REF, HET, HET, REF, HET, HET)).run());

		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, UKN, REF, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, UKN, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, REF, HET), lst(UKN, REF, REF, REF, REF, HET, REF, HET, HET)).run());
		
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, REF, UKN), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run());
		

		// homozygous
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, REF, ALT, HET), lst(HET, REF, REF, HET, HET, REF, HET, HET, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, ALT, REF, HET), lst(HET, REF, REF, HET, HET, REF, HET, UKN, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, HET, HET), lst(HET, REF, REF, HET, HET, REF, UKN, HET, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, REF, HET, HET), lst(HET, REF, REF, HET, HET, REF, UKN, UKN, ALT)).run());
	}

}
