package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

public class CompatibilityCheckerAutosomalRecessiveVerySmallTest extends CompatibilityCheckerTestBase {

	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // father
		individuals.add(new PedPerson("ped", "II.1", "I.1", "0", Sex.MALE, Disease.AFFECTED)); // son
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
				individuals.build());
		this.pedigree = new Pedigree(pedFileContents, "ped");

		this.names = ImmutableList.of("I.1", "II.1");
	}

	@Test
	public void testSizeOfPedigree() {
		Assert.assertEquals(2, pedigree.getMembers().size());
	}

	@Test
	public void testCaseNegativesOneVariant() throws CompatibilityCheckerException {
		Assert.assertFalse(buildCheckerAR(lst(HET, HET)).run());
		Assert.assertFalse(buildCheckerAR(lst(REF, REF)).run());
		Assert.assertFalse(buildCheckerAR(lst(ALT, ALT)).run());
		Assert.assertFalse(buildCheckerAR(lst(UKN, UKN)).run());

		// at least one hom_alt
		Assert.assertFalse(buildCheckerAR(lst(HET, UKN)).run());

	}

	@Test
	public void testCaseNegativesTwoVariants() throws CompatibilityCheckerException {
		// compound heterozygous
		Assert.assertFalse(buildCheckerAR(lst(HET, HET), lst(HET, HET)).run());
		Assert.assertFalse(buildCheckerAR(lst(ALT, HET), lst(HET, HET)).run());
		Assert.assertFalse(buildCheckerAR(lst(ALT, HET), lst(REF, HET)).run());


	}

	@Test
	public void testCasePositiveOneVariant() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerAR(lst(HET, ALT)).run());

		Assert.assertTrue(buildCheckerAR(lst(HET, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(UKN, ALT)).run());
	}

	@Test
	public void testCasePositiveTwoVariants() throws CompatibilityCheckerException {
		// compound heterozygous
		Assert.assertTrue(buildCheckerAR(lst(REF, HET), lst(HET, HET)).run());
		Assert.assertTrue(buildCheckerAR(lst(UKN, HET), lst(HET, HET)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, HET), lst(REF, HET)).run());

		// homozygous
		Assert.assertTrue(buildCheckerAR(lst(REF, HET), lst(HET, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(ALT, HET), lst(HET, ALT)).run());
		Assert.assertTrue(buildCheckerAR(lst(HET, HET), lst(UKN, ALT)).run());
	}

}
