package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

public class VCCompatibilityCheckerXDominantSmallFemaleTest extends AbstractCompatibilityCheckerTest {

	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // father
		individuals.add(new PedPerson("ped", "I.2", "0", "0", Sex.FEMALE, Disease.AFFECTED)); // mother
		individuals.add(new PedPerson("ped", "II.1", "I.1", "I.2", Sex.MALE, Disease.UNAFFECTED)); // son
		individuals.add(new PedPerson("ped", "II.2", "I.1", "I.2", Sex.FEMALE, Disease.AFFECTED)); // daughter
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
				individuals.build());
		this.pedigree = new Pedigree(pedFileContents, "ped");

		this.names = ImmutableList.of("I.1", "I.2", "II.1", "II.2");
	}

	@Test
	public void testSizeOfPedigree() {
		Assert.assertEquals(4, pedigree.getMembers().size());
	}

	@Test
	public void testCaseNegativesOneVariant() throws CompatibilityCheckerException {
		Assert.assertFalse(!buildCheckerXD(lst(HET, HET, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(REF, REF, REF, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(ALT, ALT, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(UKN, UKN, UKN, UKN)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXD(lst(HET, REF, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(HET, HET, REF, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(HET, REF, REF, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(REF, REF, REF, HET)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXD(lst(HET, ALT, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(HET, HET, ALT, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(HET, REF, REF, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(ALT, REF, REF, HET)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXD(lst(ALT, HET, REF, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(ALT, ALT, REF, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(ALT, REF, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(ALT, REF, ALT, HET)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXD(lst(HET, ALT, ALT, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(HET, UKN, ALT, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(HET, ALT, UKN, HET)).run().isEmpty());

		// Note that the following case are NOT considered as AD since we require the mutation to be heterozygous for
		// AD.
		Assert.assertFalse(!buildCheckerXD(lst(ALT, REF, HET, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(ALT, REF, ALT, REF)).run().isEmpty());
	}

	@Test
	public void testCaseNegativesTwoVariants() throws CompatibilityCheckerException {
		Assert.assertFalse(!buildCheckerXD(lst(HET, ALT, HET, HET), lst(HET, UKN, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(HET, HET, HET, ALT), lst(HET, UKN, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(HET, REF, ALT, REF), lst(HET, UKN, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(lst(ALT, REF, HET, REF), lst(HET, UKN, HET, ALT)).run().isEmpty());
	}

	@Test
	public void testCasePositiveOneVariant() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerXD(lst(REF, HET, REF, HET)).run().size() == 1);

		Assert.assertTrue(buildCheckerXD(lst(UKN, HET, UKN, HET)).run().size() == 1);
		Assert.assertTrue(buildCheckerXD(lst(UKN, UKN, UKN, HET)).run().size() == 1);
		Assert.assertTrue(buildCheckerXD(lst(UKN, HET, UKN, UKN)).run().size() == 1);
	}

	@Test
	public void testCasePositiveTwoVariants() throws CompatibilityCheckerException {
		Assert.assertTrue(buildCheckerXD(lst(REF, HET, REF, HET), lst(REF, HET, REF, HET)).run().size() == 2);

		Assert.assertTrue(buildCheckerXD(lst(UKN, HET, UKN, HET), lst(ALT, ALT, ALT, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerXD(lst(UKN, UKN, UKN, HET), lst(UKN, UKN, UKN, HET)).run().size() == 2);
		Assert.assertTrue(buildCheckerXD(lst(UKN, HET, UKN, UKN), lst(ALT, ALT, ALT, ALT)).run().size() == 1);
	}

}
