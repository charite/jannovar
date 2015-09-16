package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;

/**
 * <p>VCCompatibilityCheckerAutosomalDominantSmallTest class.</p>
 *
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @since 0.15
 */
public class VCCompatibilityCheckerAutosomalDominantSmallTest extends AbstractCompatibilityCheckerTest {

	/**
	 * <p>setUp.</p>
	 *
	 * @throws de.charite.compbio.jannovar.pedigree.PedParseException if any.
	 */
	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.AFFECTED)); // father
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
		Assert.assertFalse(!buildCheckerAD(lst(HET, HET, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(REF, REF, REF, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(ALT, ALT, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(UKN, UKN, UKN, UKN)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAD(lst(HET, REF, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(HET, HET, HET, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(HET, REF, REF, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(REF, REF, HET, REF)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAD(lst(HET, ALT, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(HET, HET, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(HET, REF, ALT, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(ALT, REF, HET, REF)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAD(lst(ALT, HET, HET, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(ALT, ALT, HET, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(ALT, REF, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(ALT, REF, HET, ALT)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAD(lst(HET, ALT, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(HET, UKN, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(HET, ALT, HET, UKN)).run().isEmpty());

		// Note that the following case are NOT considered as AD since we require the mutation to be heterozygous for
		// AD.
		Assert.assertFalse(!buildCheckerAD(lst(ALT, REF, HET, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(ALT, REF, ALT, REF)).run().isEmpty());
	}

	/**
	 * <p>testCaseNegativesTwoVariants.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCaseNegativesTwoVariants() throws InheritanceCompatibilityCheckerException {
		Assert.assertFalse(!buildCheckerAD(lst(HET, ALT, HET, HET), lst(HET, UKN, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(HET, HET, HET, ALT), lst(HET, UKN, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(HET, REF, ALT, REF), lst(HET, UKN, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(ALT, REF, HET, REF), lst(HET, UKN, HET, ALT)).run().isEmpty());
	}

	/**
	 * <p>testCasePositiveOneVariant.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCasePositiveOneVariant() throws InheritanceCompatibilityCheckerException {
		Assert.assertTrue(buildCheckerAD(lst(HET, REF, HET, REF)).run().size() == 1);

		Assert.assertTrue(buildCheckerAD(lst(HET, UKN, HET, UKN)).run().size() == 1);
		Assert.assertTrue(buildCheckerAD(lst(HET, UKN, UKN, UKN)).run().size() == 1);
		Assert.assertTrue(buildCheckerAD(lst(UKN, UKN, HET, UKN)).run().size() == 1);
	}

	/**
	 * <p>testCasePositiveTwoVariants.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCasePositiveTwoVariants() throws InheritanceCompatibilityCheckerException {
		Assert.assertTrue(buildCheckerAD(lst(HET, REF, HET, REF), lst(HET, REF, HET, REF)).run().size() == 2);

		Assert.assertTrue(buildCheckerAD(lst(HET, UKN, HET, UKN), lst(ALT, ALT, ALT, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAD(lst(HET, UKN, UKN, UKN), lst(HET, UKN, UKN, UKN)).run().size() == 2);
		Assert.assertTrue(buildCheckerAD(lst(UKN, UKN, HET, UKN), lst(ALT, ALT, ALT, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAD(lst(UKN, UKN, HET, UKN), lst(HET, REF, UKN, REF)).run().size() == 2);
	}

}
