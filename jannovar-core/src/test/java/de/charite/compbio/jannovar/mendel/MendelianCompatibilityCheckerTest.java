package de.charite.compbio.jannovar.mendel;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.PedFileContents;
import de.charite.compbio.jannovar.pedigree.PedParseException;
import de.charite.compbio.jannovar.pedigree.PedPerson;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Sex;

public class MendelianCompatibilityCheckerTest extends MendelianCompatibilityCheckerTestBase {

	MendelianInheritanceChecker checker;
	List<GenotypeCalls> gcList;
	ImmutableMap<ModeOfInheritance, ImmutableList<GenotypeCalls>> result;

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

		this.checker = new MendelianInheritanceChecker(this.pedigree);

		this.result = null;
		this.gcList = null;
	}

	@Test
	public void checkADTest1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, HET, HET, HET), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void checkADTest2() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, REF), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void checkADTest3() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(ALT, ALT, ALT, ALT), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void checkADTest4() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN, UKN, UKN, UKN), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void checkADTest5() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, REF, HET, HET), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void checkADTest6() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, HET, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void checkADTest7() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, REF, REF, REF), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void checkADTest8() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, HET, ALT), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void checkADTest9() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void checkADTest10() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, HET, HET), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void checkADTest11() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, UKN, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void checkADTest12() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN, UKN, HET, UKN), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void checkADARTest1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, HET, HET, UKN), lst(HET, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void checkADARTest2() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, HET, ALT, UKN), lst(REF, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void checkADARTest3() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, HET, ALT, UKN), lst(REF, REF, HET, REF), true);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void checkADARTest4() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, REF, ALT, UKN), lst(REF, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void getAllCompatibleModesTest1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, HET, HET, HET), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void getAllCompatibleModesTest2() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN, UKN, UKN, UKN), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void getAllCompatibleModesTest3() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN, UKN, HET, UKN), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void getAllCompatibleModesTest4() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN, UKN, ALT, UKN), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void getAllCompatibleModesTest5() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN, UKN, ALT, UKN), lst(HET, HET, ALT, REF), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void getAllCompatibleModesTest6() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, HET, HET, UKN), lst(HET, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void getAllCompatibleModesTest7() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, HET, ALT, HET), lst(REF, REF, HET, REF), false);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void getAllCompatibleModesTest8() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN, HET, ALT, HET), lst(REF, REF, ALT, REF), true);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void getAllCompatibleModesTest9() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN, HET, ALT, UKN), true);
		result = checker.checkMendelianInheritance(gcList);
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

}
