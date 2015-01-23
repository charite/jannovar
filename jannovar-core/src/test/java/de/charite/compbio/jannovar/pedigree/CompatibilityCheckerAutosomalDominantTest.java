package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.io.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.HG19RefDictBuilder;
import de.charite.compbio.jannovar.reference.PositionType;

//TODO(holtgrem): Test complex pedigree.
//TODO(holtgrem): Test one individual.

/**
 * Test the following pedigree
 *
 * <pre>
 * #FAMILY NAME   FATHER MOTHER SEX DISEASE
 * ped1    father 0      0      1   2
 * ped1    mother 0      0      2   1
 * ped1    son1   father mother 1   2
 * ped1    son2   father mother 1   1
 * ped1    dau1   father mother 2   2
 * ped1    dau2   father mother 2   1
 * </pre>
 */
public class CompatibilityCheckerAutosomalDominantTest {

	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();
	static final String geneName = "<fakeName>";
	static final GenomeInterval genomeRegion = new GenomeInterval(refDict, '+', refDict.contigID.get("1").intValue(),
			10, 20, PositionType.ONE_BASED);

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

	// variant call list does not fit with the given pedigree and AD inheritance
	@Test
	public void testAutosomalInheritanceIncompatible() throws CompatibilityCheckerException {
		ImmutableList.Builder<ImmutableList<Genotype>> calls = new ImmutableList.Builder<ImmutableList<Genotype>>();
		calls.add(ImmutableList.of(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS));
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, calls.build());

		CompatibilityCheckerAutosomalDominant checker = new CompatibilityCheckerAutosomalDominant(pedigree, lst);
		Assert.assertFalse(checker.run());
	}

	// correct inheritance pattern!
	@Test
	public void testAutosomalInheritanceCompatible() throws CompatibilityCheckerException {
		ImmutableList.Builder<ImmutableList<Genotype>> calls = new ImmutableList.Builder<ImmutableList<Genotype>>();
		calls.add(ImmutableList.of(Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS,
				Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_REF));
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, calls.build());

		CompatibilityCheckerAutosomalDominant checker = new CompatibilityCheckerAutosomalDominant(pedigree, lst);
		Assert.assertTrue(checker.run());
	}

	// An affected is homozygous alt => cannot be right variant!
	@Test
	public void testAutosomalInheritanceIncompatibleBecauseOfHomozygousAlt() throws CompatibilityCheckerException {
		ImmutableList.Builder<ImmutableList<Genotype>> calls = new ImmutableList.Builder<ImmutableList<Genotype>>();
		calls.add(ImmutableList.of(Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS,
				Genotype.HOMOZYGOUS_REF, Genotype.HOMOZYGOUS_ALT, Genotype.HOMOZYGOUS_REF));
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, calls.build());

		CompatibilityCheckerAutosomalDominant checker = new CompatibilityCheckerAutosomalDominant(pedigree, lst);
		Assert.assertFalse(checker.run());
	}

	// An affected is homozygous ref => cannot be right variant!
	@Test
	public void testAutosomalInheritanceIncompatibleBecauseOfHomozygousRef() throws CompatibilityCheckerException {
		ImmutableList.Builder<ImmutableList<Genotype>> calls = new ImmutableList.Builder<ImmutableList<Genotype>>();
		calls.add(ImmutableList.of(Genotype.HOMOZYGOUS_REF, Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS,
				Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_REF));
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, calls.build());

		CompatibilityCheckerAutosomalDominant checker = new CompatibilityCheckerAutosomalDominant(pedigree, lst);
		Assert.assertFalse(checker.run());
	}

	// An unaffected person is heterozyous => cannot be the right variant!
	@Test
	public void testAutosomalInheritanceIncompatibleBecauseOfHeterozygous() throws CompatibilityCheckerException {
		ImmutableList.Builder<ImmutableList<Genotype>> calls = new ImmutableList.Builder<ImmutableList<Genotype>>();
		calls.add(ImmutableList.of(Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS,
				Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS));
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, calls.build());

		CompatibilityCheckerAutosomalDominant checker = new CompatibilityCheckerAutosomalDominant(pedigree, lst);
		Assert.assertFalse(checker.run());
	}

}
