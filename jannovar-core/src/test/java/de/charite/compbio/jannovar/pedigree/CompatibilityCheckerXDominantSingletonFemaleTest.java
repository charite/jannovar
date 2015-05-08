package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

public class CompatibilityCheckerXDominantSingletonFemaleTest extends CompatibilityCheckerTestBase {

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
		Assert.assertFalse(buildCheckerXD(REF).run());
		Assert.assertFalse(buildCheckerXD(UKN).run());
		Assert.assertFalse(buildCheckerXD(ALT).run());
	}

	@Test
	public void testCaseNegativesTwoVariants() throws CompatibilityCheckerException {
		Assert.assertFalse(buildCheckerXD(REF, REF).run());
		Assert.assertFalse(buildCheckerXD(REF, UKN).run());
		Assert.assertFalse(buildCheckerXD(UKN, UKN).run());
		Assert.assertFalse(buildCheckerXD(UKN, ALT).run());
		Assert.assertFalse(buildCheckerXD(REF, ALT).run());
		Assert.assertFalse(buildCheckerXD(ALT, ALT).run());
	}

	@Test
	public void testCasePositiveOneVariant() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerXD(HET).run());
	}

	@Test
	public void testCasePositiveTwoVariants() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerXD(HET, REF).run());
		Assert.assertTrue(buildCheckerXD(UKN, HET).run());
		Assert.assertTrue(buildCheckerXD(HET, HET).run());
		Assert.assertTrue(buildCheckerXD(HET, ALT).run());
		Assert.assertTrue(buildCheckerXD(ALT, HET).run());
	}

}
