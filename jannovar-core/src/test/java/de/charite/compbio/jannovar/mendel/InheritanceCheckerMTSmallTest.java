package de.charite.compbio.jannovar.mendel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.charite.compbio.jannovar.pedigree.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InheritanceCheckerMTSmallTest extends MendelianCompatibilityCheckerTestBase {

	MendelianInheritanceChecker checker;
	List<GenotypeCalls> gcList;
	ImmutableMap<ModeOfInheritance, ImmutableList<GenotypeCalls>> result;

	Pedigree inconsistentMTpedigree;

	@Before
	public void setUp() throws Exception {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // father
		individuals.add(new PedPerson("ped", "I.2", "0", "0", Sex.FEMALE, Disease.AFFECTED)); // mother
		individuals.add(new PedPerson("ped", "II.1", "I.1", "I.2", Sex.MALE, Disease.AFFECTED)); // son
		individuals.add(new PedPerson("ped", "II.2", "I.1", "I.2", Sex.FEMALE, Disease.AFFECTED)); // daughter
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
			individuals.build());
		this.pedigree = new Pedigree(pedFileContents, "ped");

		this.names = ImmutableList.of("I.1", "I.2", "II.1", "II.2");

		this.checker = new MendelianInheritanceChecker(this.pedigree);

		this.result = null;
		this.gcList = null;
		/* same as above but father is transmitting mitochrondrial mutation, which is impossible */
		ImmutableList.Builder<PedPerson> individuals2 = new ImmutableList.Builder<PedPerson>();
		individuals2.add(new PedPerson("ped2", "I.1", "0", "0", Sex.MALE, Disease.AFFECTED)); // father
		individuals2.add(new PedPerson("ped2", "I.2", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // mother
		individuals2.add(new PedPerson("ped2", "II.1", "I.1", "I.2", Sex.MALE, Disease.AFFECTED)); // son
		individuals2.add(new PedPerson("ped2", "II.2", "I.1", "I.2", Sex.FEMALE, Disease.AFFECTED)); // daughter
		PedFileContents pedFileContents2 = new PedFileContents(new ImmutableList.Builder<String>().build(),
			individuals2.build());
		this.inconsistentMTpedigree = new Pedigree(pedFileContents2, "ped2");
	}

	protected List<GenotypeCalls> getMitochondrialGenotypeCallsList(ImmutableList<SimpleGenotype> genotypes) {
		HashMap<String, Genotype> entries = new HashMap<String, Genotype>();
		for (int i = 0; i < names.size(); ++i) {
			switch (genotypes.get(i)) {
				case HET:
					entries.put(names.get(i), new Genotype(ImmutableList.of(Genotype.REF_CALL, 1)));
					break;
				case REF:
					entries.put(names.get(i), new Genotype(ImmutableList.of(Genotype.REF_CALL, Genotype.REF_CALL)));
					break;
				case ALT:
					entries.put(names.get(i), new Genotype(ImmutableList.of(1, 1)));
					break;
				case UKN:
					entries.put(names.get(i), new Genotype(ImmutableList.of(Genotype.NO_CALL, Genotype.NO_CALL)));
					break;
			}
		}

		List<GenotypeCalls> gcs = new ArrayList<GenotypeCalls>();
		gcs.add(new GenotypeCalls(ChromosomeType.MITOCHONDRIAL,	entries.entrySet()));
		return gcs;
	}



	@SuppressWarnings("unchecked")
	protected List<GenotypeCalls> getMitochondrialGenotypeCallsList(ImmutableList<SimpleGenotype> genotypes1,
																	ImmutableList<SimpleGenotype> genotypes2) {
		List<GenotypeCalls> gcs = new ArrayList<GenotypeCalls>();
		for (Object obj : new Object[] { genotypes1, genotypes2 }) {
			ImmutableList<SimpleGenotype> genotypes = (ImmutableList<SimpleGenotype>) obj;
			HashMap<String, Genotype> entries = new HashMap<String, Genotype>();
			for (int i = 0; i < names.size(); ++i) {
				switch (genotypes.get(i)) {
					case HET:
						entries.put(names.get(i), new Genotype(ImmutableList.of(Genotype.REF_CALL, 1)));
						break;
					case REF:
						entries.put(names.get(i), new Genotype(ImmutableList.of(Genotype.REF_CALL, Genotype.REF_CALL)));
						break;
					case ALT:
						entries.put(names.get(i), new Genotype(ImmutableList.of(1, 1)));
						break;
					case UKN:
						entries.put(names.get(i), new Genotype(ImmutableList.of(Genotype.NO_CALL, Genotype.NO_CALL)));
						break;
				}
			}

			gcs.add(new GenotypeCalls(ChromosomeType.MITOCHONDRIAL,entries.entrySet()));
		}
		return gcs;
	}


	@Test
	public void testSizeOfPedigree() {
		Assert.assertEquals(4, pedigree.getMembers().size());
	}

	@Test
	public void testSizeOfInconsistentPedigree() {
		Assert.assertEquals(4, inconsistentMTpedigree.getMembers().size());
	}

	@Test
	public void testCaseNegativesOneVariant1() throws IncompatiblePedigreeException {
		gcList = getMitochondrialGenotypeCallsList(lst(REF, REF, REF, REF));
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testCasePositiveOneVariant1() throws IncompatiblePedigreeException {
		gcList = getMitochondrialGenotypeCallsList(lst(REF, HET, ALT, REF));
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}

	@Test
	public void testInconsistentCase() throws IncompatiblePedigreeException {
		gcList = getMitochondrialGenotypeCallsList(lst(REF, HET, ALT, REF));
		MendelianInheritanceChecker checkerForInconsistentCase = new MendelianInheritanceChecker(this.inconsistentMTpedigree);

		result = checkerForInconsistentCase.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}




}
