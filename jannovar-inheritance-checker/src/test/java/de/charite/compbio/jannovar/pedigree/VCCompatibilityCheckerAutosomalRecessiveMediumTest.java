package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;

/**
 * <p>VCCompatibilityCheckerAutosomalRecessiveMediumTest class.</p>
 *
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @since 0.15
 */
public class VCCompatibilityCheckerAutosomalRecessiveMediumTest extends AbstractCompatibilityCheckerTest {

	/**
	 * <p>setUp.</p>
	 *
	 * @throws de.charite.compbio.jannovar.pedigree.PedParseException if any.
	 */
	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // grandgrandfather
		individuals.add(new PedPerson("ped", "I.2", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // grandgrandmother
		individuals.add(new PedPerson("ped", "II.1", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // grandmother1
		individuals.add(new PedPerson("ped", "II.2", "I.1", "I.2", Sex.MALE, Disease.UNAFFECTED)); // grandfather1
		individuals.add(new PedPerson("ped", "II.3", "I.1", "I.2", Sex.FEMALE, Disease.UNAFFECTED)); // grandmother2
		individuals.add(new PedPerson("ped", "II.4", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // grandfather2
		individuals.add(new PedPerson("ped", "III.1", "II.1", "II.2", Sex.MALE, Disease.UNAFFECTED)); // father
		individuals.add(new PedPerson("ped", "III.2", "II.3", "II.4", Sex.FEMALE, Disease.UNAFFECTED)); // mother
		individuals.add(new PedPerson("ped", "IV.1", "III.1", "III.2", Sex.FEMALE, Disease.AFFECTED)); // child
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
				individuals.build());
		this.pedigree = new Pedigree(pedFileContents, "ped");

		this.names = ImmutableList.of("I.1", "I.2", "II.1", "II.2", "II.3", "II.4", "III.1", "III.2", "IV.1");
	}

	/**
	 * <p>testSizeOfPedigree.</p>
	 */
	@Test
	public void testSizeOfPedigree() {
		Assert.assertEquals(9, pedigree.getMembers().size());
	}

	/**
	 * <p>testCaseNegativesOneVariant.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCaseNegativesOneVariant() throws InheritanceCompatibilityCheckerException {
		Assert.assertFalse(!buildCheckerAR(lst(HET, HET, HET, HET, HET, HET, HET, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(REF, REF, REF, REF, REF, REF, REF, REF, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, REF, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, REF, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, REF, REF, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, REF, REF, REF)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, ALT, REF, HET, HET, REF, HET, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, ALT, REF, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, ALT, ALT)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, ALT, REF, HET, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, ALT, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, REF, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, REF, ALT)).run().isEmpty());

		Assert.assertFalse(!buildCheckerAR(lst(HET, ALT, REF, HET, HET, REF, HET, UKN, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, ALT, REF, HET, HET, REF, UKN, ALT, ALT)).run().isEmpty());

		// at least one hom_alt
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, HET, UKN)).run().isEmpty());

	}

	/**
	 * <p>testCaseNegativesTwoVariants.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCaseNegativesTwoVariants() throws InheritanceCompatibilityCheckerException {
		// compound heterozygous
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, ALT, REF, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, ALT, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, HET, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, REF, ALT), lst(REF, REF, REF, REF, REF, HET, REF, HET, UKN)).run().isEmpty());
		//If someone unaffected is alt for a comp. het => do not consider
		Assert.assertFalse(!buildCheckerAR(lst(HET, ALT, REF, HET, REF, REF, HET, REF, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run().isEmpty());
		//If a second trio is in the pedigree, the same comp, het should not be available
		Assert.assertFalse(!buildCheckerAR(lst(REF, HET, REF, HET, REF, REF, HET, REF, HET), lst(HET, REF, REF, HET, REF, HET, REF, HET, HET)).run().isEmpty());
	}

	/**
	 * <p>testCasePositiveOneVariant.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCasePositiveOneVariant() throws InheritanceCompatibilityCheckerException {
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, HET, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(HET, HET, REF, HET, HET, REF, HET, HET, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, HET, HET, HET, REF, HET, HET, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, HET, HET, HET, ALT)).run().size() == 1);

		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, UKN, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, UKN, HET, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, UKN, UKN, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, ALT)).run().size() == 1);
		
		//not the pedigree, but also possible
		Assert.assertTrue(buildCheckerAR(lst(REF, REF, REF, REF, REF, REF, HET, HET, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, REF, REF, REF, HET, HET, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(REF, HET, REF, REF, REF, REF, HET, HET, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(REF, REF, HET, REF, REF, REF, HET, HET, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(REF, REF, REF, HET, REF, REF, HET, HET, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(REF, REF, REF, REF, HET, REF, HET, HET, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(REF, REF, REF, REF, REF, HET, HET, HET, ALT)).run().size() == 1);
		
	}

	/**
	 * <p>testCasePositiveTwoVariants.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCasePositiveTwoVariants() throws InheritanceCompatibilityCheckerException {
		// compound heterozygous
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, REF, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run().size() == 2);
		Assert.assertTrue(buildCheckerAR(lst(REF, REF, REF, REF, REF, HET, REF, HET, HET), lst(HET, REF, REF, HET, REF, REF, HET, REF, HET)).run().size() == 2);
		
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, HET, HET, REF, REF, HET, REF, HET), lst(REF, HET, REF, REF, REF, HET, REF, HET, HET)).run().size() == 2);
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, REF, HET, HET, REF, HET), lst(REF, REF, HET, REF, REF, HET, REF, HET, HET)).run().size() == 2);
		Assert.assertTrue(buildCheckerAR(lst(HET, HET, HET, HET, HET, REF, HET, REF, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run().size() == 2);
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, REF, HET), lst(REF, HET, HET, REF, HET, HET, REF, HET, HET)).run().size() == 2);

		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, UKN, REF, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run().size() == 2);
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, UKN, HET), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run().size() == 2);
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, REF, HET), lst(UKN, REF, REF, REF, REF, HET, REF, HET, HET)).run().size() == 2);
		
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, REF, REF, HET, REF, UKN), lst(REF, REF, REF, REF, REF, HET, REF, HET, HET)).run().size() == 2);
		

		// homozygous
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, REF, ALT, HET), lst(HET, REF, REF, HET, HET, REF, HET, HET, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, ALT, REF, HET), lst(HET, REF, REF, HET, HET, REF, HET, UKN, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, HET, HET, HET), lst(HET, REF, REF, HET, HET, REF, UKN, HET, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, REF, HET, HET), lst(HET, REF, REF, HET, HET, REF, UKN, UKN, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerAR(lst(HET, REF, REF, HET, HET, REF, UKN, UKN, ALT), lst(HET, REF, REF, HET, HET, REF, UKN, UKN, ALT)).run().size() == 2);
	}

}
