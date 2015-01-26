package de.charite.compbio.jannovar.pedigree;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.io.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.HG19RefDictBuilder;
import de.charite.compbio.jannovar.reference.PositionType;

/** Base class for compatibility checkers with utility methods. */
public class CompatibilityCheckerTestBase {

	static protected final ReferenceDictionary refDict = HG19RefDictBuilder.build();
	static protected final String geneName = "<fakeName>";
	static protected final GenomeInterval genomeRegion = new GenomeInterval(refDict, '+', refDict.contigID.get("1").intValue(), 10, 20, PositionType.ONE_BASED);

	protected final Genotype HET = Genotype.HETEROZYGOUS;
	protected final Genotype REF = Genotype.HOMOZYGOUS_REF;
	protected final Genotype ALT = Genotype.HOMOZYGOUS_ALT;
	protected final Genotype UKN = Genotype.NOT_OBSERVED;

	protected Pedigree pedigree;
	protected ImmutableList<String> names;

	protected CompatibilityCheckerAutosomalDominant buildCheckerAD(Genotype gt) throws CompatibilityCheckerException {
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, ImmutableList.of(ImmutableList.of(gt)));
		return new CompatibilityCheckerAutosomalDominant(pedigree, lst);
	}

	protected CompatibilityCheckerAutosomalDominant buildCheckerAD(Genotype gt1, Genotype gt2) throws CompatibilityCheckerException {
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, ImmutableList.of(ImmutableList.of(gt1), ImmutableList.of(gt2)));
		return new CompatibilityCheckerAutosomalDominant(pedigree, lst);
	}

	protected CompatibilityCheckerAutosomalDominant buildCheckerAD(ImmutableList<Genotype> list) throws CompatibilityCheckerException {
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, ImmutableList.of(list));
		return new CompatibilityCheckerAutosomalDominant(pedigree, lst);
	}

	protected CompatibilityCheckerAutosomalDominant buildCheckerAD(ImmutableList<Genotype> list1, ImmutableList<Genotype> list2) throws CompatibilityCheckerException {
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, ImmutableList.of(list1, list2));
		return new CompatibilityCheckerAutosomalDominant(pedigree, lst);
	}

	protected CompatibilityCheckerAutosomalRecessive buildCheckerAR(Genotype gt) throws CompatibilityCheckerException {
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, ImmutableList.of(ImmutableList.of(gt)));
		return new CompatibilityCheckerAutosomalRecessive(pedigree, lst);
	}

	protected CompatibilityCheckerAutosomalRecessive buildCheckerAR(Genotype gt1, Genotype gt2) throws CompatibilityCheckerException {
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, ImmutableList.of(ImmutableList.of(gt1), ImmutableList.of(gt2)));
		return new CompatibilityCheckerAutosomalRecessive(pedigree, lst);
	}

	protected CompatibilityCheckerAutosomalRecessive buildCheckerAR(ImmutableList<Genotype> list) throws CompatibilityCheckerException {
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, ImmutableList.of(list));
		return new CompatibilityCheckerAutosomalRecessive(pedigree, lst);
	}

	protected CompatibilityCheckerAutosomalRecessive buildCheckerAR(ImmutableList<Genotype> list1, ImmutableList<Genotype> list2) throws CompatibilityCheckerException {
		GenotypeList lst = new GenotypeList(geneName, genomeRegion, names, ImmutableList.of(list1, list2));
		return new CompatibilityCheckerAutosomalRecessive(pedigree, lst);
	}

	protected ImmutableList<Genotype> lst(Genotype... gts) {
		ImmutableList.Builder<Genotype> builder = new ImmutableList.Builder<Genotype>();
		for (int i = 0; i < gts.length; ++i)
			builder.add(gts[i]);
		return builder.build();
	}

}
