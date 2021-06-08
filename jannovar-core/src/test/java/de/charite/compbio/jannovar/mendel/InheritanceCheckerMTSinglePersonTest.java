package de.charite.compbio.jannovar.mendel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.charite.compbio.jannovar.pedigree.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InheritanceCheckerMTSinglePersonTest extends MendelianCompatibilityCheckerTestBase {

	private MendelianInheritanceChecker checker;
	private List<GenotypeCalls> gcList;
	private ImmutableMap<ModeOfInheritance, ImmutableList<GenotypeCalls>> result;

	@BeforeEach
	public void setUp() throws Exception {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.FEMALE, Disease.AFFECTED));
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
			individuals.build());
		this.pedigree = new Pedigree(pedFileContents, "ped");

		this.names = ImmutableList.of("I.1");

		this.checker = new MendelianInheritanceChecker(this.pedigree);

		this.result = null;
		this.gcList = null;
	}

	@Test
	public void testSizeOfPedigree() {
		Assertions.assertEquals(1, pedigree.getMembers().size());
	}

	@Test
	public void testCaseNegativesOneMitochondrialVariant1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF), ChromosomeType.MITOCHONDRIAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assertions.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}


	@Test
	public void testCaseNegativesOneMitochondrialVariant2() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN), ChromosomeType.MITOCHONDRIAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assertions.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositivesOneVariant1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(ALT), ChromosomeType.MITOCHONDRIAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assertions.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositivesOneVariant2() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET), ChromosomeType.MITOCHONDRIAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assertions.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesTwoVariants1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF), lst(REF), ChromosomeType.MITOCHONDRIAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}


	@Test
	public void testCaseNegativesTwoVariants2() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN), lst(REF), ChromosomeType.MITOCHONDRIAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesTwoVariants3() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN), lst(UKN), ChromosomeType.MITOCHONDRIAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositivesTwoVariants1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF), lst(ALT), ChromosomeType.MITOCHONDRIAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositivesTwoVariants2() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN), lst(ALT), ChromosomeType.MITOCHONDRIAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositivesTwoVariants3() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN), lst(HET), ChromosomeType.MITOCHONDRIAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositivesTwoVariants4() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET), lst(ALT), ChromosomeType.MITOCHONDRIAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositivesTwoVariants5() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(ALT), lst(ALT), ChromosomeType.MITOCHONDRIAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositivesTwoVariants6() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(ALT), lst(HET), ChromosomeType.MITOCHONDRIAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}


}
