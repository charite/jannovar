package de.charite.compbio.jannovar.mendel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.charite.compbio.jannovar.pedigree.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MendelianCompatibilityCheckerARLarge2Test extends MendelianCompatibilityCheckerTestBase {

	MendelianInheritanceChecker checker;
	List<GenotypeCalls> gcList;
	ImmutableMap<ModeOfInheritance, ImmutableList<GenotypeCalls>> result;

	@BeforeEach
	public void setUp() throws Exception {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // grandgrandfather
		individuals.add(new PedPerson("ped", "I.2", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // grandgrandmother
		individuals.add(new PedPerson("ped", "II.1", "I.1", "I.2", Sex.FEMALE, Disease.AFFECTED)); // parent1
		individuals.add(new PedPerson("ped", "II.2", "I.1", "I.2", Sex.MALE, Disease.UNAFFECTED)); // parent2
		individuals.add(new PedPerson("ped", "II.3", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // parent3
		individuals.add(new PedPerson("ped", "III.1", "II.2", "II.3", Sex.MALE, Disease.AFFECTED)); // child1
		individuals.add(new PedPerson("ped", "III.2", "II.2", "II.3", Sex.FEMALE, Disease.UNAFFECTED)); // child2
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
		Assertions.assertEquals(7, pedigree.getMembers().size());
	}

	@Test
	public void testCaseNegativesOneVariant1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, HET, HET, HET, HET, HET, HET), ChromosomeType.AUTOSOMAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesOneVariant2() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF, REF, REF, REF, REF, REF, REF), ChromosomeType.AUTOSOMAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesOneVariant3() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(ALT, ALT, ALT, ALT, ALT, ALT, ALT), ChromosomeType.AUTOSOMAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesOneVariant4() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(UKN, UKN, UKN, UKN, UKN, UKN, UKN), ChromosomeType.AUTOSOMAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCaseNegativesTwoVariants1() throws IncompatiblePedigreeException {
		// Only one UKN has ALT is not sufficient!
		gcList = getGenotypeCallsList(lst(HET, REF, HET, HET, REF, HET, HET), lst(REF, HET, HET, REF, HET, HET, HET),
			ChromosomeType.AUTOSOMAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositivesTwoVariants1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, REF, HET, HET, REF, HET, HET), lst(REF, HET, HET, REF, HET, HET, REF),
			ChromosomeType.AUTOSOMAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositivesTwoVariants2() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, REF, HET, HET, REF, HET, UKN), lst(REF, HET, HET, REF, HET, HET, UKN),
			ChromosomeType.AUTOSOMAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositivesTwoVariants3() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(HET, REF, HET, REF, HET, HET, REF), lst(REF, HET, HET, HET, REF, HET, REF),
			ChromosomeType.AUTOSOMAL);
		result = checker.checkMendelianInheritance(gcList);

		Assertions.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assertions.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assertions.assertEquals(2, result.get(ModeOfInheritance.ANY).size());
	}
}
