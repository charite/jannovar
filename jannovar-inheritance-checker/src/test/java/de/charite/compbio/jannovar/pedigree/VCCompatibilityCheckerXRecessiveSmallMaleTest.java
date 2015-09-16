package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;

/**
 * <p>VCCompatibilityCheckerXRecessiveSmallMaleTest class.</p>
 *
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @since 0.15
 */
public class VCCompatibilityCheckerXRecessiveSmallMaleTest extends AbstractCompatibilityCheckerTest {

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
		Assert.assertFalse(!buildCheckerXR(lst(HET, HET, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(REF, REF, REF, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(ALT, ALT, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(UKN, UKN, UKN, UKN)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(HET, REF, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(HET, HET, REF, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(HET, REF, REF, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(REF, REF, REF, HET)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(HET, ALT, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(HET, HET, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(ALT, REF, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(ALT, REF, REF, ALT)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(ALT, HET, REF, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(ALT, ALT, REF, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(ALT, REF, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(ALT, REF, ALT, ALT)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(HET, ALT, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(HET, UKN, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(HET, ALT, UKN, ALT)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(ALT, REF, REF, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(ALT, REF, REF, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(REF, REF, REF, HET)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(HET, HET, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(HET, REF, HET, ALT)).run().isEmpty());
		
		// at least one hom_alt
		Assert.assertFalse(!buildCheckerXR(lst(HET, UKN, UKN, UKN)).run().isEmpty());

	}

	/**
	 * <p>testCaseNegativesTwoVariants.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCaseNegativesTwoVariants() throws InheritanceCompatibilityCheckerException {
		Assert.assertFalse(!buildCheckerXR(lst(HET, ALT, HET, HET), lst(HET, UKN, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(HET, HET, HET, ALT), lst(HET, UKN, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(HET, REF, ALT, REF), lst(HET, UKN, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(ALT, REF, HET, REF), lst(HET, UKN, HET, ALT)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(REF, ALT, HET, REF), lst(HET, REF, HET, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(ALT, REF, HET, REF), lst(REF, HET, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(HET, HET, HET, REF), lst(REF, HET, HET, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerXR(lst(REF, HET, HET, ALT), lst(ALT, HET, HET, REF)).run().isEmpty());

		Assert.assertFalse(!buildCheckerXR(lst(ALT, HET, HET, HET), lst(HET, REF, HET, HET)).run().isEmpty());
	}

	/**
	 * <p>testCasePositiveOneVariant.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCasePositiveOneVariant() throws InheritanceCompatibilityCheckerException {
		
		// male has it HOM ALT
		Assert.assertTrue(buildCheckerXR(lst(REF, HET, ALT, REF)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(REF, HET, ALT, HET)).run().size() == 1);

		Assert.assertTrue(buildCheckerXR(lst(REF, UKN, ALT, UKN)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(UKN, UKN, ALT, UKN)).run().size() == 1);
		
		//Male has it HET (misscall)
		Assert.assertTrue(buildCheckerXR(lst(REF, HET, HET, REF)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(REF, HET, HET, HET)).run().size() == 1);

		Assert.assertTrue(buildCheckerXR(lst(REF, UKN, HET, UKN)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(UKN, UKN, HET, UKN)).run().size() == 1);
		
		// this is a de-novo mutation. can be dominant or recessive
		Assert.assertTrue(buildCheckerXR(lst(REF, REF, ALT, REF)).run().size() == 1);
	}

	/**
	 * <p>testCasePositiveTwoVariants.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCasePositiveTwoVariants() throws InheritanceCompatibilityCheckerException {
		// heterozygous misscalls
		Assert.assertTrue(buildCheckerXR(lst(REF, HET, HET, REF), lst(HET, REF, HET, REF)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(REF, HET, HET, HET), lst(HET, REF, HET, REF)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(REF, HET, HET, REF), lst(HET, REF, HET, HET)).run().size() == 1);

		Assert.assertTrue(buildCheckerXR(lst(UKN, HET, HET, REF), lst(HET, REF, HET, REF)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(REF, UKN, HET, HET), lst(HET, REF, HET, REF)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(REF, HET, HET, UKN), lst(HET, REF, HET, HET)).run().size() == 1);

		// homozygous
		Assert.assertTrue(buildCheckerXR(lst(REF, HET, ALT, REF), lst(REF, HET, ALT, REF)).run().size() == 2);
		Assert.assertTrue(buildCheckerXR(lst(REF, REF, ALT, REF), lst(REF, UKN, ALT, REF)).run().size() == 2);
		Assert.assertTrue(buildCheckerXR(lst(REF, UKN, ALT, UKN), lst(REF, HET, ALT, REF)).run().size() == 2);
		Assert.assertTrue(buildCheckerXR(lst(REF, HET, ALT, HET), lst(REF, UKN, ALT, REF)).run().size() == 2);
		Assert.assertTrue(buildCheckerXR(lst(UKN, HET, ALT, REF), lst(REF, HET, ALT, REF)).run().size() == 2);
		Assert.assertTrue(buildCheckerXR(lst(HET, REF, ALT, REF), lst(REF, UKN, ALT, REF)).run().size() == 1);
		Assert.assertTrue(buildCheckerXR(lst(UKN, UKN, ALT, UKN), lst(REF, HET, ALT, REF)).run().size() == 2);
		Assert.assertTrue(buildCheckerXR(lst(UKN, HET, ALT, HET), lst(REF, UKN, ALT, REF)).run().size() == 2);
	}

}
