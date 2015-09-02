package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * <p>VCCompatibilityCheckerXDominantSingletonFemaleTest class.</p>
 *
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @since 0.15
 */
public class VCCompatibilityCheckerXDominantSingletonFemaleTest extends AbstractCompatibilityCheckerTest {

	/**
	 * <p>setUp.</p>
	 *
	 * @throws de.charite.compbio.jannovar.pedigree.PedParseException if any.
	 */
	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.FEMALE, Disease.AFFECTED));
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
				individuals.build());
		this.pedigree = new Pedigree(pedFileContents, "ped");

		this.names = ImmutableList.of("I.1");
	}

	/**
	 * <p>testSizeOfPedigree.</p>
	 */
	@Test
	public void testSizeOfPedigree() {
		Assert.assertEquals(1, pedigree.getMembers().size());
	}

	/**
	 * <p>testCaseNegativesOneVariant.</p>
	 */
	@Test
	public void testCaseNegativesOneVariant() {
		Assert.assertFalse(!buildCheckerXD(REF).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(UKN).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(ALT).run().isEmpty());
	}

	/**
	 * <p>testCaseNegativesTwoVariants.</p>
	 */
	@Test
	public void testCaseNegativesTwoVariants() {
		Assert.assertFalse(!buildCheckerXD(REF, REF).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(REF, UKN).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(UKN, UKN).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(UKN, ALT).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(REF, ALT).run().isEmpty());
		Assert.assertFalse(!buildCheckerXD(ALT, ALT).run().isEmpty());
	}

	/**
	 * <p>testCasePositiveOneVariant.</p>
	 */
	@Test
	public void testCasePositiveOneVariant() {
		Assert.assertTrue(buildCheckerXD(HET).run().size() == 1);
	}

	/**
	 * <p>testCasePositiveTwoVariants.</p>
	 */
	@Test
	public void testCasePositiveTwoVariants() {
		Assert.assertTrue(buildCheckerXD(HET, REF).run().size() == 1);
		Assert.assertTrue(buildCheckerXD(UKN, HET).run().size() == 1);
		Assert.assertTrue(buildCheckerXD(HET, HET).run().size() == 2);
		Assert.assertTrue(buildCheckerXD(HET, ALT).run().size() == 1);
		Assert.assertTrue(buildCheckerXD(ALT, HET).run().size() == 1);
	}

}
