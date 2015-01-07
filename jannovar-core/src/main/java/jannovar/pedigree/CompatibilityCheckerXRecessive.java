package jannovar.pedigree;

import jannovar.reference.GenomePosition;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and X recessive mode of
 * inheritance.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
class CompatibilityCheckerXRecessive {

	/** the pedigree to use for the checking */
	public final Pedigree pedigree;

	/** the genotype call list to use for the checking */
	public final GenotypeList list;

	/** decorator for getting unaffected individuals and such from the {@link Pedigree} */
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
	public CompatibilityCheckerXRecessive(Pedigree pedigree, GenotypeList list) throws CompatibilityCheckerException {
		if (pedigree.members.size() == 0)
			throw new CompatibilityCheckerException("Invalid pedigree of size 1.");
		if (list.isNamesEqual(pedigree))
			throw new CompatibilityCheckerException("Incompatible names in pedigree and genotype list.");
		if (list.calls.get(0).size() == 0)
			throw new CompatibilityCheckerException("Genotype call list must not be empty!");

		this.pedigree = pedigree;
		this.list = list;
		this.queryDecorator = new PedigreeQueryDecorator(pedigree);
	}

	public boolean run() {
		// perform basic sanity check, regarding X chromsome
		final GenomePosition txBegin = list.transcript.txRegion.getGenomeBeginPos();
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

	private boolean runSingleSampleCase() {
		for (ImmutableList<Genotype> gtList : list.calls)
			if (gtList.get(0) == Genotype.HOMOZYGOUS_ALT)
				return true;
		return false;
	}

	private boolean runMultiSampleCase() {
		// TODO(holtgrem): There are pedigrees where compound heterozygous is possible.
		for (ImmutableList<Genotype> gtList : list.calls) {
			// Check whether this list of genotype calls is compatible when with the set of affected individuals, the
			// parents, and the unaffected individuals.
			if (checkCompatibilityAffected(gtList) && checkCompatibilityParents(gtList) && checkUnaffected(gtList))
				return true;
		}

		return false;
	}

	private boolean checkCompatibilityAffected(ImmutableList<Genotype> gtList) {
		int i = 0;
		for (Person person : pedigree.members) {
			if (person.disease == Disease.AFFECTED) {
				if (gtList.get(i) != Genotype.HOMOZYGOUS_ALT) {
					// TODO(holtgrew): assumption of male individual implicit only?
					// cannot be disease-causing mutation, an affected male does not have it
					return false;
				}
			}
			++i;
		}
		return true;
	}

	private boolean checkCompatibilityParents(ImmutableList<Genotype> gtList) {
		final ImmutableSet<String> parentNames = queryDecorator.getParentNames();
		int i = 0;
		for (Person person : pedigree.members) {
			if (parentNames.contains(person.name)) {
				final Genotype gt = gtList.get(i);
				if (person.sex == Sex.MALE && person.disease != Disease.AFFECTED && gt == Genotype.HOMOZYGOUS_ALT) {
					// cannot be disease-causing mutation, an unaffected father has it
					return false;
				}
				if (person.sex == Sex.FEMALE && gt != Genotype.HETEROZYGOUS) {
					// cannot be disease-causing mutation, mother of patient is not heterozygous
					return false;
				}
			}
			++i;
		}

		return true;
	}

	private boolean checkUnaffected(ImmutableList<Genotype> gtList) {
		final ImmutableSet<String> unaffectedNames = queryDecorator.getUnaffectedNames();
		int i = 0;
		for (Person person : pedigree.members) {
			if (unaffectedNames.contains(person.name)) {
				final Genotype gt = gtList.get(i);
				if (person.sex == Sex.MALE && gt == Genotype.HOMOZYGOUS_ALT) {
					// TODO(holtgrew): why the assumption about brother relation?
					// cannot be disease-causing mutation, an unaffected brother has it
					return false;
				}
				if (person.sex == Sex.FEMALE && gt != Genotype.HETEROZYGOUS) {
					// TODO(holtgrew): why the assumption about sister relation?
					// cannot be disease-causing mutation, unaffected sister is not heterozygous
					return false;
				}
			}
			++i;
		}

		return true;
	}

}
