package de.charite.compbio.jannovar.mendel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.charite.compbio.jannovar.pedigree.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class InheritanceCheckerMTSinglePersonTest  extends MendelianCompatibilityCheckerTestBase {

	MendelianInheritanceChecker checker;
	List<GenotypeCalls> gcList;
	ImmutableMap<ModeOfInheritance, ImmutableList<GenotypeCalls>> result;

	@Before
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
		Assert.assertEquals(1, pedigree.getMembers().size());
	}

    	protected List<GenotypeCalls> getMitochondrialGenotypeCallsList(ImmutableList<SimpleGenotype> genotypes,
			boolean isMitochondrial) {
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
		gcs.add(new GenotypeCalls(isMitochondrial ? ChromosomeType.MITOCHONDRIAL : ChromosomeType.AUTOSOMAL,
				entries.entrySet()));
		return gcs;
	}

    	@Test
	public void testCaseNegativesOneVariant1() throws IncompatiblePedigreeException {
		gcList = getGenotypeCallsList(lst(REF), true);
		result = checker.checkMendelianInheritance(gcList);

		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
		Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
		Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
	}
}
