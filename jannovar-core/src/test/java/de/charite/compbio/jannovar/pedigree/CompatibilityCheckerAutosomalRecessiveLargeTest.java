package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

public class CompatibilityCheckerAutosomalRecessiveLargeTest extends CompatibilityCheckerTestBase {

	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // grandgrandfather
		individuals.add(new PedPerson("ped", "I.2", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // grandgrandmother
		individuals.add(new PedPerson("ped", "II.1", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // parent1
		individuals.add(new PedPerson("ped", "II.2", "I.1", "I.2", Sex.FEMALE, Disease.UNAFFECTED)); // parent2
		individuals.add(new PedPerson("ped", "II.3", "I.1", "I.2", Sex.FEMALE, Disease.AFFECTED)); // parent3
		individuals.add(new PedPerson("ped", "II.4", "I.1", "I.2", Sex.FEMALE, Disease.UNKNOWN)); // parent4
		individuals.add(new PedPerson("ped", "III.1", "II.1", "II.2", Sex.MALE, Disease.UNAFFECTED)); // child1
		individuals.add(new PedPerson("ped", "III.2", "0", "II.3", Sex.MALE, Disease.UNAFFECTED)); // child2
		individuals.add(new PedPerson("ped", "IV.1", "III.1", "0", Sex.FEMALE, Disease.AFFECTED)); // baby1
		individuals.add(new PedPerson("ped", "IV.2", "III.2", "0", Sex.FEMALE, Disease.UNAFFECTED)); // baby2
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
				individuals.build());
		this.pedigree = new Pedigree(pedFileContents, "ped");

		this.names = ImmutableList.of("I.1", "I.2", "II.1", "II.2", "II.3", "II.4", "III.1", "III.2", "IV.1", "IV.2");
	}

	@Test
	public void testSizeOfPedigree() {
		Assert.assertEquals(10, pedigree.getMembers().size());
	}

	@Test
	public void testCaseNegativesOneVariant() throws CompatibilityCheckerException {
		Assert.assertFalse(buildCheckerAR(lst(HET, HET, HET, HET, HET, HET, HET, HET, HET, HET)).run());
		Assert.assertFalse(buildCheckerAR(lst(REF, REF, REF, REF, REF, REF, REF, REF, REF, REF)).run());
		Assert.assertFalse(buildCheckerAR(lst(ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT)).run());
		Assert.assertFalse(buildCheckerAR(lst(UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN)).run());

		Assert.assertTrue(buildCheckerAR(lst(HET, HET, REF, HET, ALT, UKN, HET, HET, ALT, REF)).run());

		// at least one hom_alt
		Assert.assertFalse(buildCheckerAR(lst(HET, HET, REF, HET, UKN, UKN, HET, HET, UKN, REF)).run());

		// Only one UKN has ALT is not sufficient!
		Assert.assertFalse(buildCheckerAR(lst(HET, HET, REF, HET, UKN, ALT, HET, HET, UKN, REF)).run());

	}

	@Test
	public void testCaseNegativesTwoVariants() throws CompatibilityCheckerException {
	}

	@Test
	public void testCasePositiveOneVariant() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerAR(lst(HET, HET, REF, HET, ALT, UKN, HET, HET, ALT, REF)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, HET, REF, HET, ALT, REF, HET, HET, ALT, REF)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, HET, REF, HET, ALT, ALT, HET, HET, ALT, REF)).run());

		Assert.assertTrue(buildCheckerAR(lst(HET, HET, REF, HET, ALT, REF, HET, HET, UKN, REF)).run());

		// correct inheritance from II.1/2 to III.1=> II.1 is now HET.
		// but false inheritance from I.1/2 to II.2 (cause II.3 is alt, II.2 must be het).
		Assert.assertTrue(buildCheckerAR(lst(HET, HET, HET, REF, ALT, UKN, HET, HET, ALT, REF)).run());
		// correct inheritance from II.1/2 to III.1. II.1 must have the same allele affected than II.2
		// correct inheritance from I.1/2 to II.2.
		Assert.assertTrue(buildCheckerAR(lst(HET, HET, HET, HET, ALT, UKN, HET, HET, ALT, REF)).run());
		// False inheritance II.1 or II.2 must be HET/UKN (or ALT and affected).
		// But it is (very unlikely) a de-novo mutation in III.1.
		Assert.assertTrue(buildCheckerAR(lst(HET, HET, REF, REF, ALT, UKN, HET, HET, ALT, REF)).run());
	}

	@Test
	public void testCasePositiveTwoVariants() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, REF, HET, UKN, REF, REF, HET, REF),
										 lst(REF, HET, HET, REF, HET, UKN, HET, REF, HET, REF)).run());
		Assert.assertTrue(buildCheckerAR(lst(REF, HET, REF, HET, HET, UKN, HET, HET, HET, REF),
										 lst(HET, REF, REF, REF, HET, UKN, REF, REF, HET, REF)).run());
	}

}
