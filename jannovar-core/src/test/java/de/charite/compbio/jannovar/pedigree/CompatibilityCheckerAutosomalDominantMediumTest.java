package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

public class CompatibilityCheckerAutosomalDominantMediumTest extends CompatibilityCheckerTestBase {

	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // grandfather
		individuals.add(new PedPerson("ped", "I.2", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // grandmother
		individuals.add(new PedPerson("ped", "II.1", "I.1", "I.2", Sex.MALE, Disease.UNAFFECTED)); // uncle
		individuals.add(new PedPerson("ped", "II.2", "I.1", "I.2", Sex.MALE, Disease.AFFECTED)); // father
		individuals.add(new PedPerson("ped", "II.3", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // mother
		individuals.add(new PedPerson("ped", "III.1", "II.2", "II.3", Sex.FEMALE, Disease.AFFECTED)); // daughter
		individuals.add(new PedPerson("ped", "III.2", "II.2", "II.3", Sex.MALE, Disease.UNAFFECTED)); // son
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
				individuals.build());
		this.pedigree = new Pedigree(pedFileContents, "ped");

		this.names = ImmutableList.of("I.1", "I.2", "II.1", "II.2", "II.3", "III.1", "III.2");
	}

	@Test
	public void testSizeOfPedigree() {
		Assert.assertEquals(7, pedigree.getMembers().size());
	}

	@Test
	public void testCaseNegativesOneVariant() throws CompatibilityCheckerException {
		Assert.assertFalse(buildCheckerAD(lst(HET, HET, HET, HET, HET, HET, HET)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, REF, REF, REF, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(ALT, ALT, ALT, ALT, ALT, ALT, ALT)).run());
		Assert.assertFalse(buildCheckerAD(lst(UKN, UKN, UKN, UKN, UKN, UKN, UKN)).run());

		Assert.assertFalse(buildCheckerAD(lst(HET, REF, REF, HET, REF, HET, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, HET, REF, HET, REF, HET, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, HET, HET, REF, HET, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, HET, HET, HET, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, HET, REF, HET, HET)).run());

		Assert.assertFalse(buildCheckerAD(lst(ALT, REF, REF, HET, REF, HET, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, ALT, REF, HET, REF, HET, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, ALT, HET, REF, HET, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, HET, ALT, HET, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, HET, REF, HET, ALT)).run());

		Assert.assertFalse(buildCheckerAD(lst(HET, REF, REF, ALT, REF, ALT, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, HET, REF, ALT, REF, ALT, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, HET, ALT, REF, ALT, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, ALT, HET, ALT, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, ALT, REF, HET, HET)).run());

		Assert.assertFalse(buildCheckerAD(lst(HET, REF, REF, UKN, REF, HET, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, HET, REF, UKN, REF, HET, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, HET, UKN, REF, HET, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, UKN, HET, HET, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, UKN, REF, HET, HET)).run());

		// need one het call
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, UKN, REF, UKN, REF)).run());

		// Note that the following case are NOT considered as AD since we
		// require the mutation to be heterozygous for
		// AD.
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, ALT, REF, ALT, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, UKN, REF, ALT, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, ALT, REF, UKN, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, ALT, REF, ALT, UKN)).run());
	}

	@Test
	public void testCaseNegativesTwoVariants() throws CompatibilityCheckerException {
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, HET, ALT, HET, HET), lst(REF, REF, REF, HET, UKN, HET, ALT)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, HET, HET, HET, ALT), lst(REF, REF, REF, HET, UKN, HET, ALT)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, HET, REF, ALT, REF), lst(REF, REF, REF, HET, UKN, HET, ALT)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, ALT, REF, HET, REF), lst(REF, REF, REF, HET, UKN, HET, ALT)).run());
		
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, HET, REF, HET, HET), lst(REF, HET, REF, HET, REF, HET, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, HET, REF, HET, REF, HET, REF), lst(HET, REF, REF, HET, REF, HET, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, HET, HET, HET, REF), lst(ALT, REF, REF, HET, REF, HET, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF, REF, HET, REF, HET, ALT), lst(REF, ALT, REF, HET, REF, HET, REF)).run());
		
	}

	@Test
	public void testCasePositiveOneVariant() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerAD(lst(REF, REF, REF, HET, REF, HET, REF)).run());
		
		Assert.assertTrue(buildCheckerAD(lst(REF, REF, REF, HET, UKN, HET, UKN)).run());
		Assert.assertTrue(buildCheckerAD(lst(REF, REF, REF, HET, UKN, UKN, UKN)).run());
		Assert.assertTrue(buildCheckerAD(lst(REF, REF, REF, UKN, UKN, HET, UKN)).run());
		
		Assert.assertTrue(buildCheckerAD(lst(UKN, REF, REF, HET, REF, HET, REF)).run());
		Assert.assertTrue(buildCheckerAD(lst(REF, UKN, REF, HET, UKN, HET, UKN)).run());
		Assert.assertTrue(buildCheckerAD(lst(UKN, REF, REF, HET, UKN, UKN, UKN)).run());
		Assert.assertTrue(buildCheckerAD(lst(REF, REF, UKN, UKN, UKN, HET, UKN)).run());
	}

	@Test
	public void testCasePositiveTwoVariants() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerAD(lst(REF, REF, REF, HET, REF, HET, REF), lst(REF, REF, REF, HET, REF, HET, REF)).run());

		Assert.assertTrue(buildCheckerAD(lst(REF, REF, REF, HET, UKN, HET, UKN), lst(REF, REF, REF, ALT, ALT, ALT, ALT)).run());
		Assert.assertTrue(buildCheckerAD(lst(REF, REF, REF, HET, UKN, UKN, UKN), lst(REF, REF, REF, HET, UKN, UKN, UKN)).run());
		Assert.assertTrue(buildCheckerAD(lst(REF, REF, REF, UKN, UKN, HET, UKN), lst(REF, REF, REF, ALT, ALT, ALT, ALT)).run());
	}

}
