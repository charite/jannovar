package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;

/**
 * <p>VCCompatibilityCheckerXRecessiveSmallFemaleTest class.</p>
 *
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @since 0.15
 */
public class VCCompatibilityCheckerXRecessiveSmallFemaleTest extends AbstractCompatibilityCheckerTest {

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
		individuals.add(new PedPerson("ped", "II.1", "I.1", "I.2", Sex.MALE, Disease.UNAFFECTED)); // son
		individuals.add(new PedPerson("ped", "II.2", "I.1", "I.2", Sex.FEMALE, Disease.AFFECTED)); // daughter
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
		Assert.assertFalse(!buildCheckerXR(lst(HET, HET, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(REF, REF, REF, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(ALT, ALT, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(UKN, UKN, UKN, UKN)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(HET, REF, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(HET, HET, REF, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(HET, REF, REF, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(REF, REF, REF, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(REF, REF, HET, REF)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(HET, ALT, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(HET, HET, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(ALT, REF, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(ALT, ALT, REF, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(ALT, REF, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(ALT, REF, ALT, ALT)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(ALT, REF, REF, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(REF, REF, REF, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(HET, REF, REF, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(UKN, REF, REF, ALT)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(HET, ALT, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(HET, UKN, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(HET, ALT, UKN, ALT)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(ALT, REF, REF, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(REF, REF, REF, HET)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(REF, HET, HET, ALT)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(HET, REF, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(REF, HET, REF, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(REF, UKN, REF, ALT)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(REF, HET, UKN, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(REF, UKN, UKN, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(UKN, HET, REF, UKN)).run().isEmpty());

	}
	
	/**
	 * <p>testCaseNegativesTwoVariants.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCaseNegativesTwoVariants() throws InheritanceCompatibilityCheckerException {
		// compound heterozygous
		Assert.assertFalse(!buildCheckerXR(lst(ALT, HET, REF, HET), lst(REF, HET, REF, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(ALT, ALT, REF, HET), lst(REF, HET, REF, HET)).run().isEmpty());
		// cannot be disease causing if an other unaffected sibling has it
		Assert.assertFalse(!buildCheckerXR(lst(ALT, REF, HET, HET), lst(REF, HET, HET, HET)).run().isEmpty());
		// cannot be disease causing if unaffected has it homozygous alt
		Assert.assertFalse(!buildCheckerXR(lst(ALT, REF, REF, HET), lst(REF, HET, ALT, HET)).run().isEmpty());
		
	}

	/**
	 * <p>testCasePositiveOneVariant.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCasePositiveOneVariant() throws InheritanceCompatibilityCheckerException {

		Assert.assertTrue(buildCheckerXR(lst(ALT, HET, REF, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(HET, HET, REF, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(UKN, HET, REF, ALT)).run().size() == 1);

		Assert.assertTrue(buildCheckerXR(lst(ALT, UKN, REF, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(HET, UKN, REF, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(UKN, UKN, REF, ALT)).run().size() == 1);

		Assert.assertTrue(buildCheckerXR(lst(ALT, HET, UKN, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(HET, HET, UKN, ALT)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(UKN, HET, UKN, ALT)).run().size() == 1);

		Assert.assertTrue(buildCheckerXR(lst(ALT, HET, REF, UKN)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(HET, HET, REF, UKN)).run().size() == 1);

		Assert.assertTrue(buildCheckerXR(lst(HET, UKN, UKN, UKN)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(UKN, UKN, UKN, ALT)).run().size() == 1);
	}
	/**
	 * <p>testCasePositiveTwoVariants.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCasePositiveTwoVariants() throws InheritanceCompatibilityCheckerException {

		// compound heterozygous
		Assert.assertTrue(buildCheckerXR(lst(ALT, REF, REF, HET), lst(REF, HET, REF, HET)).run().size() == 2);
		Assert.assertTrue(buildCheckerXR(lst(REF, HET, REF, HET), lst(ALT, REF, REF, HET)).run().size() == 2);
		
		Assert.assertTrue(buildCheckerXR(lst(HET, REF, REF, HET), lst(REF, HET, REF, HET)).run().size() == 2);
		Assert.assertTrue(buildCheckerXR(lst(UKN, REF, REF, HET), lst(REF, HET, REF, HET)).run().size() == 2);
		
		Assert.assertTrue(buildCheckerXR(lst(ALT, REF, REF, HET), lst(REF, UKN, REF, HET)).run().size() == 2);
		Assert.assertTrue(buildCheckerXR(lst(ALT, REF, REF, HET), lst(UKN, HET, REF, HET)).run().size() == 2);
		
		Assert.assertTrue(buildCheckerXR(lst(ALT, UKN, REF, HET), lst(REF, HET, UKN, HET)).run().size() == 2);
		
		//comp. hetetygous special case: male is affected and can have both variants!
		Assert.assertTrue(buildCheckerXR(lst(ALT, REF, REF, HET), lst(ALT, HET, REF, HET)).run().size() == 2);
		Assert.assertTrue(buildCheckerXR(lst(ALT, REF, REF, HET), lst(HET, HET, REF, HET)).run().size() == 2);
		Assert.assertTrue(buildCheckerXR(lst(HET, REF, REF, HET), lst(HET, HET, REF, HET)).run().size() == 2);

	}

}
