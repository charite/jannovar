package jannovar.pedigree;

import com.google.common.collect.ImmutableList;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and autosomal dominant
 * mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * For autosomal dominant inheritance, there must be at least one {@link Genotype} that is shared by all affected
 * individuals but no unaffected individuals in the pedigree.
 *
 * We do not allow {@link Genotype#HOMOZYGOUS_ALT} for any affected (and also for the one person in the case of
 * single-person pedigrees) since this is not the interesting case for users of this class.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
class CompatibilityCheckerAutosomalDominant {

	/** the pedigree to use for the checking */
	public final Pedigree pedigree;

	/** the genotype call list to use for the checking */
	public final GenotypeList list;

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
	public CompatibilityCheckerAutosomalDominant(Pedigree pedigree, GenotypeList list)
			throws CompatibilityCheckerException {
		if (pedigree.members.size() == 0)
			throw new CompatibilityCheckerException("Invalid pedigree of size 1.");
		if (!list.namesEqual(pedigree))
			throw new CompatibilityCheckerException("Incompatible names in pedigree and genotype list.");
		if (list.calls.get(0).size() == 0)
			throw new CompatibilityCheckerException("Genotype call list must not be empty!");

		this.pedigree = pedigree;
		this.list = list;
	}

	/**
	 * @return <code>true</code> if {@link #list} is compatible with {@link #pedigree} and the autosomal dominant mode
	 *         of inheritances.
	 */
	public boolean run() {
		if (pedigree.members.size() == 1)
			return runSingleSampleCase();
		else
			return runMultiSampleCase();
	}

	private boolean runSingleSampleCase() {
		// We could also allow Genotye.HOMOZYGOUS_ALT here but that is not the interesting case.
		for (ImmutableList<Genotype> gtList : list.calls)
			if (gtList.get(0) == Genotype.HETEROZYGOUS)
				return true;
		return false;
	}

	private boolean runMultiSampleCase() {
		for (ImmutableList<Genotype> gtList : list.calls) {
			boolean currentVariantCompatible = true; // current variant compatible with AD?
			int numAffectedWithHet = 0;

			for (int i = 0; i < pedigree.members.size(); ++i) {
				final Genotype gt = gtList.get(i);
				final Disease d = pedigree.members.get(i).disease;

				if (d == Disease.AFFECTED) {
					if (gt == Genotype.HOMOZYGOUS_REF || gt == Genotype.HOMOZYGOUS_ALT) {
						currentVariantCompatible = false; // current variant not compatible with AD
						break;
					} else if (gt == Genotype.HETEROZYGOUS) {
						numAffectedWithHet++;
					}
				} else if (d == Disease.UNAFFECTED) {
					if (gt == Genotype.HETEROZYGOUS || gt == Genotype.HOMOZYGOUS_ALT) {
						currentVariantCompatible = false; // current variant not compatible with AD
						break;
					}
				}
			}

			// If we reach here, we have either examined all members of the pedigree or have decided that the
			// variant is incompatible in one person. If any one variant is compatible with AD inheritance, than the
			// Gene is compatible and we can return true without examining the other variants.
			if (currentVariantCompatible && numAffectedWithHet > 0)
				return true;
		}

		return false;
	}

}
