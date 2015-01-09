package jannovar.pedigree;

import jannovar.io.ReferenceDictionary;
import jannovar.reference.GenomeInterval;
import jannovar.reference.HG19RefDictBuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

//TODO(holtgrew): Review this with Nick and Max.

public class CompatibilityCheckerAutosomalRecessiveHomozygousTest {

	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();
	static final String geneName = "<fakeName>";
	static final GenomeInterval genomeRegion = new GenomeInterval(refDict, '+', refDict.contigID.get("1").intValue(),
			10, 20);

	Pedigree pedigree;
	ImmutableList<String> names;

	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped1", "father", "0", "0", Sex.MALE, Disease.AFFECTED));
		individuals.add(new PedPerson("ped1", "mother", "0", "0", Sex.FEMALE, Disease.UNAFFECTED));
		individuals.add(new PedPerson("ped1", "son1", "0", "0", Sex.MALE, Disease.AFFECTED));
		individuals.add(new PedPerson("ped1", "son2", "0", "0", Sex.MALE, Disease.UNAFFECTED));
		individuals.add(new PedPerson("ped1", "dau1", "0", "0", Sex.FEMALE, Disease.AFFECTED));
		individuals.add(new PedPerson("ped1", "dau2", "0", "0", Sex.FEMALE, Disease.UNAFFECTED));
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
				individuals.build());
		this.pedigree = new Pedigree(pedFileContents, "ped1");

		this.names = ImmutableList.of("father", "mother", "son1", "son2", "dau1", "dau2");
	}

	@Test
	public void testSizeOfPedigree() {
		Assert.assertEquals(6, pedigree.members.size());
	}

	// Test one compatible HOMOZYGOUS_ALT variant plus two irrelevant second variants.
	@Test
	public void testARinheritance4HomozygousOnly() throws CompatibilityCheckerException {
		ImmutableList.Builder<ImmutableList<Genotype>> calls = new ImmutableList.Builder<ImmutableList<Genotype>>();
		calls.add(ImmutableList.of(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS));
		calls.add(ImmutableList.of(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT));
		// third list is not compatible with linkage, only one affected is HOMOZYGOUS_ALT, the other is HET
		calls.add(ImmutableList.of(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_REF));
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, calls.build());

		CompatibilityCheckerAutosomalRecessiveHomozygous checker = new CompatibilityCheckerAutosomalRecessiveHomozygous(
				pedigree, lst);
		Assert.assertFalse(checker.run());
	}

	// Test one compatible HOMOZYGOUS_ALT variant plus two irrelevant second variants.
	@Ignore("Fix me!")
	@Test
	public void testARinheritance3HomozygousOnly() throws CompatibilityCheckerException {
		ImmutableList.Builder<ImmutableList<Genotype>> calls = new ImmutableList.Builder<ImmutableList<Genotype>>();
		calls.add(ImmutableList.of(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS));
		calls.add(ImmutableList.of(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT));
		// third list is compatible with linkage
		calls.add(ImmutableList.of(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT,
				Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT, Genotype.HOMOZYGOUS_REF));
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, calls.build());

		CompatibilityCheckerAutosomalRecessiveHomozygous checker = new CompatibilityCheckerAutosomalRecessiveHomozygous(
				pedigree, lst);
		Assert.assertTrue(checker.run());
	}

	// Test one compatible HOMOZYGOUS_ALT variant plus two irrelevant second variants.
	@Ignore("Fix me!")
	@Test
	public void testARinheritance1homozygousOnly() throws CompatibilityCheckerException {
		ImmutableList.Builder<ImmutableList<Genotype>> calls = new ImmutableList.Builder<ImmutableList<Genotype>>();
		calls.add(ImmutableList.of(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS));
		calls.add(ImmutableList.of(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT));
		// third list is compatible with linkage
		calls.add(ImmutableList.of(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT,
				Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT, Genotype.HOMOZYGOUS_REF));
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, calls.build());

		CompatibilityCheckerAutosomalRecessiveHomozygous checker = new CompatibilityCheckerAutosomalRecessiveHomozygous(
				pedigree, lst);
		Assert.assertTrue(checker.run());
	}

}
