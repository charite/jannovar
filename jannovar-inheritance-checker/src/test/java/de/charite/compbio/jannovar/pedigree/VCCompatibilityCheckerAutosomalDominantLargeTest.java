package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;


/**
 * <p>VCCompatibilityCheckerAutosomalDominantLargeTest class.</p>
 *
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @since 0.15
 */
public class VCCompatibilityCheckerAutosomalDominantLargeTest extends AbstractCompatibilityCheckerTest {

	/**
	 * <p>setUp.</p>
	 *
	 * @throws de.charite.compbio.jannovar.pedigree.PedParseException if any.
	 */
	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // grandfather
		individuals.add(new PedPerson("ped", "I.2", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // grandmother
		individuals.add(new PedPerson("ped", "II.1", "I.1", "I.2", Sex.MALE, Disease.UNAFFECTED)); // p1
		individuals.add(new PedPerson("ped", "II.2", "I.1", "I.2", Sex.MALE, Disease.AFFECTED)); // p2
		individuals.add(new PedPerson("ped", "II.3", "I.1", "I.2", Sex.FEMALE, Disease.UNAFFECTED)); // p3
		individuals.add(new PedPerson("ped", "II.4", "I.1", "I.2", Sex.FEMALE, Disease.AFFECTED)); // p4
		individuals.add(new PedPerson("ped", "II.5", "I.1", "I.2", Sex.UNKNOWN, Disease.UNKNOWN)); // p5
		individuals.add(new PedPerson("ped", "III.1", "II.1", "0", Sex.FEMALE, Disease.UNAFFECTED)); // c1
		individuals.add(new PedPerson("ped", "III.2", "II.1", "0", Sex.MALE, Disease.UNAFFECTED)); // c2
		individuals.add(new PedPerson("ped", "III.3", "II.2", "0", Sex.MALE, Disease.AFFECTED)); // c3
		individuals.add(new PedPerson("ped", "III.4", "II.2", "0", Sex.FEMALE, Disease.UNAFFECTED)); // c4
		individuals.add(new PedPerson("ped", "III.5", "0", "II.3", Sex.FEMALE, Disease.UNAFFECTED)); // c5
		individuals.add(new PedPerson("ped", "III.6", "0", "II.4", Sex.MALE, Disease.AFFECTED)); // c6
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
				individuals.build());
		this.pedigree = new Pedigree(pedFileContents, "ped");

		this.names = ImmutableList.of("I.1", "I.2", "II.1", "II.2", "II.3", "II.4", "II.5", "III.1",
				"III.2", "III.3", "III.4", "III.5", "III.6");
	}

	/**
	 * <p>testSizeOfPedigree.</p>
	 */
	@Test
	public void testSizeOfPedigree() {
		Assert.assertEquals(13, pedigree.getMembers().size());
	}

	/**
	 * <p>testCaseNegativesOneVariant.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCaseNegativesOneVariant() throws InheritanceCompatibilityCheckerException {
		Assert.assertFalse(!buildCheckerAD(lst(HET, HET, HET, HET, HET, HET, HET, HET, HET, HET, HET, HET, HET)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(REF, REF, REF, REF, REF, REF, REF, REF, REF, REF, REF, REF, REF)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN)).run().isEmpty());

		// need one het call
		Assert.assertFalse(!buildCheckerAD(lst(REF, REF, REF, UKN, REF, UKN, UKN, REF, REF, UKN, REF, REF, UKN)).run().isEmpty());

		// Note that the following case are NOT considered as AD since we
		// require the mutation to be heterozygous for
		// AD.
		Assert.assertFalse(!buildCheckerAD(lst(REF, REF, REF, ALT, REF, ALT, ALT, REF, REF, ALT, REF, REF, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(REF, REF, REF, ALT, REF, ALT, HET, REF, REF, ALT, REF, REF, ALT)).run().isEmpty());
		Assert.assertFalse(!buildCheckerAD(lst(REF, REF, REF, HET, REF, HET, UKN, REF, REF, HET, REF, REF, ALT)).run().isEmpty());
	}

	/**
	 * <p>testCaseNegativesTwoVariants.</p>
	 */
	@Test
	public void testCaseNegativesTwoVariants() {

	}

	/**
	 * <p>testCasePositiveOneVariant.</p>
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	@Test
	public void testCasePositiveOneVariant() throws InheritanceCompatibilityCheckerException  {
		Assert.assertTrue(buildCheckerAD(lst(REF, REF, REF, HET, REF, HET, HET, REF, REF, HET, REF, REF, HET)).run().size() == 1);
		Assert.assertTrue(buildCheckerAD(lst(REF, REF, REF, HET, REF, HET, REF, REF, REF, HET, REF, REF, HET)).run().size() == 1);
		Assert.assertTrue(buildCheckerAD(lst(REF, REF, REF, HET, REF, HET, UKN, REF, REF, HET, REF, REF, HET)).run().size() == 1);
	}

	/**
	 * <p>testCasePositiveTwoVariants.</p>
	 */
	@Test
	public void testCasePositiveTwoVariants()  {

	}

}
