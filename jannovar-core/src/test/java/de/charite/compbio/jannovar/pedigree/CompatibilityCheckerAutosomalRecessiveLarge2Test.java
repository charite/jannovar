package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

public class CompatibilityCheckerAutosomalRecessiveLarge2Test extends CompatibilityCheckerTestBase {

	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // grandgrandfather
		individuals.add(new PedPerson("ped", "I.2", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // grandgrandmother
		individuals.add(new PedPerson("ped", "II.1", "I.1", "I.2", Sex.FEMALE, Disease.AFFECTED)); // parent1
		individuals.add(new PedPerson("ped", "II.2", "I.1", "I.2", Sex.MALE, Disease.UNAFFECTED)); // parent2
		individuals.add(new PedPerson("ped", "II.3", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // parent3
		individuals.add(new PedPerson("ped", "III.1", "II.2", "II.3", Sex.MALE, Disease.AFFECTED)); // child1
		individuals.add(new PedPerson("ped", "III.2", "II.2", "II.3", Sex.FEMALE, Disease.UNAFFECTED)); // child2
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
		Assert.assertFalse(buildCheckerAR(lst(HET, HET, HET, HET, HET, HET, HET)).run());
		Assert.assertFalse(buildCheckerAR(lst(REF, REF, REF, REF, REF, REF, REF)).run());
		Assert.assertFalse(buildCheckerAR(lst(ALT, ALT, ALT, ALT, ALT, ALT, ALT)).run());
		Assert.assertFalse(buildCheckerAR(lst(UKN, UKN, UKN, UKN, UKN, UKN, UKN)).run());

	}

	@Test
	public void testCaseNegativesTwoVariants() throws CompatibilityCheckerException {
		Assert.assertFalse(buildCheckerAR(lst(HET, REF, HET, HET, REF, HET, HET),
										  lst(REF, HET, HET, REF, HET, HET, HET)).run());
	}

	@Test
	public void testCasePositiveOneVariant() throws CompatibilityCheckerException {
	}

	@Test
	public void testCasePositiveTwoVariants() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, HET, HET, REF, HET, HET),
										 lst(REF, HET, HET, REF, HET, HET, REF)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, HET, HET, REF, HET, UKN),
				 						 lst(REF, HET, HET, REF, HET, HET, UKN)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, HET, REF, HET, HET, REF),
										 lst(REF, HET, HET, HET, REF, HET, REF)).run());
	}

}
