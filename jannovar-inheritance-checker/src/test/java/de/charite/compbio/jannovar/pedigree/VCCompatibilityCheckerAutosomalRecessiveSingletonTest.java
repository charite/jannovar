package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

public class VCCompatibilityCheckerAutosomalRecessiveSingletonTest extends AbstractCompatibilityCheckerTest {

	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.AFFECTED));
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
		Assert.assertFalse(!buildCheckerAR(REF).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(UKN).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(HET).run().isEmpty());
	}

	@Test
	public void testCaseNegativesTwoVariants() throws CompatibilityCheckerException {
		Assert.assertFalse(!buildCheckerAR(HET, REF).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(REF, UKN).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(UKN, HET).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(UKN, UKN).run().isEmpty());
	}

	@Test
	public void testCasePositiveOneVariant() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerAR(ALT).run().size() == 1);
	}

	@Test
	public void testCasePositiveTwoVariants() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerAR(HET, HET).run().size() == 2);
		Assert.assertTrue(buildCheckerAR(ALT, REF).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(UKN, ALT).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(ALT, ALT).run().size() == 2);
	}

}
