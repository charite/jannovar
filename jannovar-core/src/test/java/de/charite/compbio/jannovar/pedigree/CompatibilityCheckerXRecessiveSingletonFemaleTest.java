package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

public class CompatibilityCheckerXRecessiveSingletonFemaleTest extends CompatibilityCheckerTestBase {

	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.FEMALE, Disease.AFFECTED));
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
				individuals.build());
		this.pedigree = new Pedigree(pedFileContents, "ped");

		this.names = ImmutableList.of("I.1");
	}

	@Test
	public void testSizeOfPedigree() {
		Assert.assertEquals(1, pedigree.getMembers().size());
	}

	@Test
	public void testCaseNegativesOneVariant() throws CompatibilityCheckerException {
		Assert.assertFalse(buildCheckerXR(REF).run());
		Assert.assertFalse(buildCheckerXR(UKN).run());
		Assert.assertFalse(buildCheckerXR(HET).run());
	}

	@Test
	public void testCaseNegativesTwoVariants() throws CompatibilityCheckerException {
		Assert.assertFalse(buildCheckerXR(REF, REF).run());
		Assert.assertFalse(buildCheckerXR(REF, UKN).run());
		Assert.assertFalse(buildCheckerXR(UKN, UKN).run());
		Assert.assertFalse(buildCheckerXR(HET, UKN).run());
	}

	@Test
	public void testCasePositiveOneVariant() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerXR(ALT).run());
	}

	@Test
	public void testCasePositiveTwoVariants() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerXR(HET, HET).run());
		Assert.assertTrue(buildCheckerXR(ALT, REF).run());
		Assert.assertTrue(buildCheckerXR(UKN, ALT).run());
		Assert.assertTrue(buildCheckerXR(ALT, ALT).run());
	}

}
