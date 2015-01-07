package jannovar.pedigree;

import jannovar.reference.GenomePosition;

import com.google.common.collect.ImmutableList;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and X dominant mode of
 * inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * For X-chromosomal dominant inheritance, there must be at least one {@link Genotype} that is shared by all affected
 * individuals but no unaffected individuals in the pedigree and is on the X chromosome.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
class CompatibilityCheckerXDominant {

	/** the pedigree to use for the checking */
	public final Pedigree pedigree;

	/** the genotype call list to use for the checking */
	public final GenotypeList list;

	/** decorator for getting unaffected individuals and such from the pedigree */
	private final PedigreeQueryDecorator queryDecorator;

	/**
	 * Initialize compatibility checker and perform some sanity checks.
	 *
	 * The {@link GenotypeList} object passed to the constructor is expected to represent all of the variants found in a
	 * certain gene (possibly after filtering for rarity or predicted pathogenicity). The samples represented by the
	 * {@link GenotypeList} must be in the same order as the list of individuals contained in this pedigree.
	 *
	 * @param pedigree
	 *            the {@link Pedigree} to use for the initialize
	 * @param list
	 *            the {@link GenotypeList} to use for the initialization
	 * @throws CompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public CompatibilityCheckerXDominant(Pedigree pedigree, GenotypeList list) throws CompatibilityCheckerException {
		if (pedigree.members.size() == 0)
			throw new CompatibilityCheckerException("Invalid pedigree of size 1.");
		if (!list.namesEqual(pedigree))
			throw new CompatibilityCheckerException("Incompatible names in pedigree and genotype list.");
		if (list.calls.get(0).size() == 0)
			throw new CompatibilityCheckerException("Genotype call list must not be empty!");

		this.pedigree = pedigree;
		this.list = list;
		this.queryDecorator = new PedigreeQueryDecorator(pedigree);
	}

	/**
	 * @return <code>true</code> if {@link #list} is compatible with {@link #pedigree} and the X-chromosomal dominant
	 *         mode of inheritances.
	 * @throws CompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public boolean run() throws CompatibilityCheckerException {
		// perform basic sanity check, regarding X chromsome
		final GenomePosition txBegin = list.genomeRegion.getGenomeBeginPos();
		Integer chrXID = txBegin.refDict.contigID.get("X");
		if (chrXID == null)
			return false; // this organism type does not have X chromosome
		if (chrXID.intValue() != txBegin.chr)
			return false; // transcript of genotype list is not on X chromosome

		if (pedigree.members.size() == 1)
			return runSingleSampleCase();
		else
			return runMultiSampleCase();
	}

	private boolean runSingleSampleCase() throws CompatibilityCheckerException {
		if (pedigree.members.get(0).sex == Sex.FEMALE)
			return new CompatibilityCheckerAutosomalDominant(pedigree, list).run();
		else
			return new CompatibilityCheckerXRecessive(pedigree, list).run();
	}

	private boolean runMultiSampleCase() {
		for (ImmutableList<Genotype> gtList : list.calls) {
			int i = 0;
			for (Person person : pedigree.members) {
				if (person.disease == Disease.AFFECTED && gtList.get(i) == Genotype.HOMOZYGOUS_REF)
					return false;
				else if (person.disease == Disease.UNAFFECTED
						&& (gtList.get(i) == Genotype.HOMOZYGOUS_ALT || gtList.get(i) == Genotype.HETEROZYGOUS))
					return false;
				i++;
			}
		}

		return true;
	}
}
