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

public class MendelianCompatibilityCheckerADLargeTest extends MendelianCompatibilityCheckerTestBase {

	MendelianInheritanceChecker checker;
	List<GenotypeCalls> gcList;
	ImmutableMap<ModeOfInheritance, ImmutableList<GenotypeCalls>> result;

	@Before
	public void setUp() throws Exception {
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

		this.names = ImmutableList.of("I.1", "I.2", "II.1", "II.2", "II.3", "II.4", "II.5", "III.1", "III.2", "III.3",
				"III.4", "III.5", "III.6");

		this.checker = new MendelianInheritanceChecker(this.pedigree);

		this.result = null;
		this.gcList = null;
	}

	@Test
	public void testSizeOfPedigree() {
		Assert.assertEquals(13, pedigree.getMembers().size());
	}

	@Test
	public void testCaseNegativesOneVariant1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, HET, HET, HET, HET, HET, HET, HET, HET, HET, HET, HET, HET), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesOneVariant2() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, REF, REF, REF, REF, REF, REF, REF, REF, REF, REF), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesOneVariant3() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT, ALT), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesOneVariant4() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN, UKN), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesOneVariant5() throws IncompatiblePedigreeException {
		// need one het call
		gcList = getGenotypeCallsList(lst(REF, REF, REF, UKN, REF, UKN, UKN, REF, REF, UKN, REF, REF, UKN), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	// Note that the following cases are NOT considered as AD since we require the mutation to be heterozygous for AD.

	@Test
	public void testCaseNegativesOneVariant6() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, ALT, REF, ALT, ALT, REF, REF, ALT, REF, REF, ALT), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesOneVariant7() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, ALT, REF, ALT, HET, REF, REF, ALT, REF, REF, ALT), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesOneVariant8() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, REF, HET, UKN, REF, REF, HET, REF, REF, ALT), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositivesOneVariant1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, REF, HET, HET, REF, REF, HET, REF, REF, HET), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositivesOneVariant2() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, REF, HET, REF, REF, REF, HET, REF, REF, HET), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositivesOneVariant3() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, HET, REF, HET, UKN, REF, REF, HET, REF, REF, HET), false);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

}
