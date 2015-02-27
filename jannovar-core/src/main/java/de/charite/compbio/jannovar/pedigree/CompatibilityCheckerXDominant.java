package de.charite.compbio.jannovar.pedigree;

import com.google.common.collect.ImmutableList;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and X dominant mode of
 * inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * For X-chromosomal dominant inheritance, there must be at least one {@link Genotype} that is shared by all affected
 * individuals but no unaffected individuals in the pedigree.
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
		new PedigreeQueryDecorator(pedigree);
	}

	/**
	 * @return <code>true</code> if {@link #list} is compatible with {@link #pedigree} and the X-chromosomal dominant
	 *         mode of inheritances.
	 * @throws CompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public boolean run() throws CompatibilityCheckerException {
		if (!list.isXChromosomal)
			return false;
		else if (pedigree.members.size() == 1)
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
			boolean currentVariantCompatible = true; // current variant compatible with XD?
			int numAffectedWithMut = 0;

			for (int i = 0; i < pedigree.members.size(); ++i) {
				final Sex sex = pedigree.members.get(i).sex;
				final Genotype gt = gtList.get(i);
				final Disease d = pedigree.members.get(i).disease;

				if (d == Disease.AFFECTED) {
					if (gt == Genotype.HOMOZYGOUS_REF || (sex == Sex.FEMALE && gt == Genotype.HOMOZYGOUS_ALT)) {
						// we do not allow HOM_ALT for females to have the same behaviour as AD for females
						currentVariantCompatible = false;
						break;
					} else if (sex == Sex.MALE && (gt == Genotype.HETEROZYGOUS || gt == Genotype.HOMOZYGOUS_ALT)) {
						// we allow heterozygous here as well in case of mis-calls in the one X copy in the male
						numAffectedWithMut++;
					} else if (sex == Sex.FEMALE && gt == Genotype.HETEROZYGOUS) {
						numAffectedWithMut++;
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
			if (currentVariantCompatible && numAffectedWithMut > 0)
				return true;
		}

		return false;
	}
}
