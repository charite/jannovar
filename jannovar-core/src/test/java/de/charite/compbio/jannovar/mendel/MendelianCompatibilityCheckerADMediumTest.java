package de.charite.compbio.jannovar.mendel;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.PedFileContents;
import de.charite.compbio.jannovar.pedigree.PedPerson;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Sex;

public class MendelianCompatibilityCheckerADMediumTest extends MendelianCompatibilityCheckerTestBase {

	MendelianInheritanceChecker checker;
	List<GenotypeCalls> gcList;
	ImmutableMap<ModeOfInheritance, ImmutableList<GenotypeCalls>> result;

	@Before
	public void setUp() throws Exception {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // grandfather
		individuals.add(new PedPerson("ped", "I.2", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // grandmother
		individuals.add(new PedPerson("ped", "II.1", "I.1", "I.2", Sex.MALE, Disease.UNAFFECTED)); // uncle
		individuals.add(new PedPerson("ped", "II.2", "I.1", "I.2", Sex.MALE, Disease.AFFECTED)); // father
		individuals.add(new PedPerson("ped", "II.3", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // mother
		individuals.add(new PedPerson("ped", "III.1", "II.2", "II.3", Sex.FEMALE, Disease.AFFECTED)); // daughter
		individuals.add(new PedPerson("ped", "III.2", "II.2", "II.3", Sex.MALE, Disease.UNAFFECTED)); // son
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
				individuals.build());
		this.pedigree = new Pedigree(pedFileContents, "ped");

		this.names = ImmutableList.of("I.1", "I.2", "II.1", "II.2", "II.3", "III.1", "III.2");

		this.checker = new MendelianInheritanceChecker(this.pedigree);

		this.result = null;
		this.gcList = null;
	}

	@Test
	public void testSizeOfPedigree() {
		Assert.assertEquals(7, pedigree.getMembers().size());
	}

	public void testCaseNegativesOneVariant1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, HET, HET, HET, HET, HET, HET), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant2() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, REF, REF, REF, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant3() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(ALT, ALT, ALT, ALT, ALT, ALT, ALT), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant4() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN, UKN, UKN, UKN, UKN, UKN, UKN), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant5() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, REF, REF, HET, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant6() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, HET, REF, HET, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant7() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, HET, HET, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant8() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, HET, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant9() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, REF, HET, HET), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant10() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(ALT, REF, REF, HET, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant11() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, ALT, REF, HET, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant12() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, ALT, HET, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant13() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, ALT, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant14() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, REF, HET, ALT), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant15() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, REF, REF, ALT, REF, ALT, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant16() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, HET, REF, ALT, REF, ALT, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant17() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, HET, ALT, REF, ALT, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant18() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, ALT, HET, ALT, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant19() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, ALT, REF, HET, HET), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant20() throws IncompatiblePedigreeException {
		// This is not AD as we require the mutation to be heterozygous
		gcList = getGenotypeCallsList(lst(HET, REF, REF, UKN, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant21() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, HET, REF, UKN, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant22() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, HET, UKN, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant23() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, UKN, HET, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant24() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, UKN, REF, HET, HET), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant25() throws IncompatiblePedigreeException {
		// We require at least one heterozygous variant
		gcList = getGenotypeCallsList(lst(REF, REF, REF, UKN, REF, UKN, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant26() throws IncompatiblePedigreeException {
		// We require at heterozygous variants
		gcList = getGenotypeCallsList(lst(REF, REF, REF, ALT, REF, ALT, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant27() throws IncompatiblePedigreeException {
		// We require at heterozygous variants
		gcList = getGenotypeCallsList(lst(REF, REF, REF, UKN, REF, ALT, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant28() throws IncompatiblePedigreeException {
		// We require at heterozygous variants
		gcList = getGenotypeCallsList(lst(REF, REF, REF, ALT, REF, UKN, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	public void testCaseNegativesOneVariant29() throws IncompatiblePedigreeException {
		// We require at heterozygous variants
		gcList = getGenotypeCallsList(lst(REF, REF, REF, ALT, REF, ALT, UKN), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesTwoVariants1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, ALT, HET, HET), lst(REF, REF, REF, HET, UKN, HET, ALT),
				false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesTwoVariants2() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, HET, HET, ALT), lst(REF, REF, REF, HET, UKN, HET, ALT),
				false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesTwoVariants3() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, REF, ALT, REF), lst(REF, REF, REF, HET, UKN, HET, ALT),
				false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesTwoVariants4() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, ALT, REF, HET, REF), lst(REF, REF, REF, HET, UKN, HET, ALT),
				false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesTwoVariants5() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, REF, HET, HET), lst(REF, HET, REF, HET, REF, HET, REF),
				false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesTwoVariants6() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, HET, REF, HET, REF, HET, REF), lst(HET, REF, REF, HET, REF, HET, REF),
				false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesTwoVariants7() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, HET, HET, REF), lst(ALT, REF, REF, HET, REF, HET, REF),
				false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesTwoVariants8() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, REF, HET, ALT), lst(REF, ALT, REF, HET, REF, HET, REF),
				false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositiveOneVariant1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositiveOneVariant2() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, UKN, HET, UKN), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositiveOneVariant3() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, UKN, UKN, UKN), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositiveOneVariant4() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, UKN, UKN, HET, UKN), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositiveOneVariant5() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN, REF, REF, HET, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositiveOneVariant6() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, UKN, REF, HET, UKN, HET, UKN), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositiveOneVariant7() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN, REF, REF, HET, UKN, UKN, UKN), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositiveOneVariant8() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, UKN, UKN, UKN, HET, UKN), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositiveTwoVariants1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, REF, HET, REF), lst(REF, REF, REF, HET, REF, HET, REF),
				false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(2, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositiveTwoVariants2() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, UKN, HET, UKN), lst(REF, REF, REF, ALT, ALT, ALT, ALT),
				false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositiveTwoVariants3() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, UKN, UKN, UKN), lst(REF, REF, REF, HET, UKN, UKN, UKN),
				false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(2, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositiveTwoVariants4() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, UKN, UKN, HET, UKN), lst(REF, REF, REF, ALT, ALT, ALT, ALT),
				false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

}
