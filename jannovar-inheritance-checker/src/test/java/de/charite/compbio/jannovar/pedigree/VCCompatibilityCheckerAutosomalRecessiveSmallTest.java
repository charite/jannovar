package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;

/**
 * <p>VCCompatibilityCheckerAutosomalRecessiveSmallTest class.</p>
 *
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @since 0.15
 */
public class VCCompatibilityCheckerAutosomalRecessiveSmallTest extends AbstractCompatibilityCheckerTest {

	/**
	 * <p>setUp.</p>
	 *
	 * @throws de.charite.compbio.jannovar.pedigree.PedParseException if any.
	 */
	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // father
		individuals.add(new PedPerson("ped", "I.2", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // mother
		individuals.add(new PedPerson("ped", "II.1", "I.1", "I.2", Sex.MALE, Disease.AFFECTED)); // son
		individuals.add(new PedPerson("ped", "II.2", "I.1", "I.2", Sex.FEMALE, Disease.UNAFFECTED)); // daughter
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
				individuals.build());
		this.pedigree = new Pedigree(pedFileContents, "ped");

		this.names = ImmutableList.of("I.1", "I.2", "II.1", "II.2");
	}

	/**
	 * <p>testSizeOfPedigree.</p>
	 */
	@Test
	public void testSizeOfPedigree() {
		Assert.assertEquals(4, pedigree.getMembers().size());
	}

	/**
	 * <p>testCaseNegativesOneVariant.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCaseNegativesOneVariant() throws InheritanceCompatibilityCheckerException {
		Assert.assertFalse(!buildCheckerAR(lst(HET, HET, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(REF, REF, REF, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(ALT, ALT, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(UKN, UKN, UKN, UKN)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, HET, HET, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(REF, REF, HET, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(REF, REF, REF, HET)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAR(lst(HET, ALT, ALT, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, HET, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(ALT, REF, ALT, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(ALT, REF, ALT, REF)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAR(lst(ALT, HET, ALT, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(ALT, ALT, ALT, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(ALT, REF, ALT, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(ALT, REF, ALT, ALT)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAR(lst(HET, ALT, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, UKN, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, ALT, ALT, UKN)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAR(lst(ALT, REF, HET, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(ALT, REF, ALT, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(REF, REF, HET, REF)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAR(lst(REF, HET, ALT, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, HET, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, ALT, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(REF, HET, ALT, UKN)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(REF, REF, ALT, REF)).run().isEmpty());

		// at least one hom_alt
		Assert.assertFalse(!buildCheckerAR(lst(HET, UKN, UKN, UKN)).run().isEmpty());

	}

	/**
	 * <p>testCaseNegativesTwoVariants.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCaseNegativesTwoVariants() throws InheritanceCompatibilityCheckerException {
		// compound heterozygous
		Assert.assertFalse(!buildCheckerAR(lst(HET, ALT, HET, HET), lst(HET, UKN, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, HET, HET, ALT), lst(HET, UKN, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, ALT, REF), lst(HET, UKN, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(ALT, REF, HET, REF), lst(HET, UKN, HET, ALT)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAR(lst(REF, ALT, HET, REF), lst(HET, REF, HET, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(ALT, REF, HET, REF), lst(REF, HET, HET, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, HET, HET, REF), lst(REF, HET, HET, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(REF, HET, HET, REF), lst(HET, HET, HET, REF)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAR(lst(REF, HET, HET, HET), lst(HET, REF, HET, HET)).run().isEmpty());
	}

	/**
	 * <p>testCasePositiveOneVariant.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCasePositiveOneVariant() throws InheritanceCompatibilityCheckerException {
		Assert.assertTrue(buildCheckerAR(lst(HET, HET, ALT, REF)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(HET, HET, ALT, HET)).run().size() == 1);

		Assert.assertTrue(buildCheckerAR(lst(HET, UKN, ALT, UKN)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(UKN, UKN, ALT, UKN)).run().size() == 1);
	}

	/**
	 * <p>testCasePositiveTwoVariants.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCasePositiveTwoVariants() throws InheritanceCompatibilityCheckerException {
		// compound heterozygous
		Assert.assertTrue(buildCheckerAR(lst(REF, HET, HET, REF), lst(HET, REF, HET, REF)).run().size() == 2);
		Assert.assertTrue(buildCheckerAR(lst(REF, HET, HET, HET), lst(HET, REF, HET, REF)).run().size() == 2);
		Assert.assertTrue(buildCheckerAR(lst(REF, HET, HET, REF), lst(HET, REF, HET, HET)).run().size() == 2);

		Assert.assertTrue(buildCheckerAR(lst(UKN, HET, HET, REF), lst(HET, REF, HET, REF)).run().size() == 2);
		Assert.assertTrue(buildCheckerAR(lst(REF, UKN, HET, HET), lst(HET, REF, HET, REF)).run().size() == 2);
		Assert.assertTrue(buildCheckerAR(lst(REF, HET, HET, UKN), lst(HET, REF, HET, HET)).run().size() == 2);

		// homozygous
		Assert.assertTrue(buildCheckerAR(lst(REF, ALT, HET, REF), lst(HET, HET, ALT, REF)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(ALT, REF, HET, REF), lst(HET, UKN, ALT, REF)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(HET, HET, HET, REF), lst(UKN, HET, ALT, HET)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(REF, HET, HET, REF), lst(HET, HET, ALT, UKN)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(HET, HET, ALT, REF), lst(HET, HET, ALT, UKN)).run().size() == 2);
	}

}
