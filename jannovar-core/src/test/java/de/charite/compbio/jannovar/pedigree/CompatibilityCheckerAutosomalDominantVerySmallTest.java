package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

public class CompatibilityCheckerAutosomalDominantVerySmallTest extends CompatibilityCheckerTestBase {

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
	public void testCaseNegativesOneVariant() throws CompatibilityCheckerException {
		Assert.assertFalse(buildCheckerAD(lst(HET, HET)).run());
		Assert.assertFalse(buildCheckerAD(lst(REF, REF)).run());
		Assert.assertFalse(buildCheckerAD(lst(ALT, ALT)).run());
		Assert.assertFalse(buildCheckerAD(lst(UKN, UKN)).run());
		
		Assert.assertFalse(buildCheckerAD(lst(REF, ALT)).run());
		Assert.assertFalse(buildCheckerAD(lst(HET, ALT)).run());
		Assert.assertFalse(buildCheckerAD(lst(UKN, ALT)).run());
		
		
		Assert.assertFalse(buildCheckerAD(lst(ALT, HET)).run());
		
		// at least one het
		Assert.assertFalse(buildCheckerAD(lst(REF, UKN)).run());

	}

	@Test
	public void testCaseNegativesTwoVariants() throws CompatibilityCheckerException {
		Assert.assertFalse(buildCheckerAD(lst(HET, HET), lst(HET, HET)).run());
		Assert.assertFalse(buildCheckerAD(lst(ALT, HET), lst(HET, HET)).run());
		Assert.assertFalse(buildCheckerAD(lst(ALT, HET), lst(REF, UKN)).run());


	}

	@Test
	public void testCasePositiveOneVariant() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerAD(lst(REF, HET)).run());
		Assert.assertTrue(buildCheckerAD(lst(UKN, HET)).run());
	}

	@Test
	public void testCasePositiveTwoVariants() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerAD(lst(ALT, HET), lst(REF, HET)).run());
		Assert.assertTrue(buildCheckerAD(lst(UKN, HET), lst(UKN, HET)).run());
		Assert.assertTrue(buildCheckerAD(lst(HET, HET), lst(REF, HET)).run());
	}

}
