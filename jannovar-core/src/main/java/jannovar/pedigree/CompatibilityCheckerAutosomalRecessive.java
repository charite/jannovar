package jannovar.pedigree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

// TODO(holtgrew): The explanation of the compatibility check appears to the be same as for autosomal recessive compound het.

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and autosomal recessive
 * mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * The class first checks whether there is a homozygous variant that is compatible with autosomal recessive. If there is
 * none, it checks for compound heterozygous variants. This is a little complicated. The function first checks whether
 * there is a variant that is heterozygous in the affected and heteroygous in one, but not both, of the parents. All
 * such variants are stored. If there are such variants, then it checks whether the maternal-het mutations are
 * compatible with the paternal het mutations, and it returns all variants for which there are compatible pairs.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
class CompatibilityCheckerAutosomalRecessive {

	/** the pedigree to use for the checking */
	public final Pedigree pedigree;

	/** the genotype call list to use for the checking */
	public final GenotypeList list;

	/** decorator for getting unaffected individuals and such from the pedigree */
	private final PedigreeQueryDecorator queryDecorator;

	/** index of the father in the pedigree, -1 if none */
	private final int fatherIdx;

	/** index of the one mother in the pedigree, -1 if none */
	private final int motherIdx;

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
	public CompatibilityCheckerAutosomalRecessive(Pedigree pedigree, GenotypeList list)
			throws CompatibilityCheckerException {
		if (pedigree.members.size() == 0)
			throw new CompatibilityCheckerException("Invalid pedigree of size 1.");
		if (list.namesEqual(pedigree))
			throw new CompatibilityCheckerException("Incompatible names in pedigree and genotype list.");
		if (list.calls.get(0).size() == 0)
			throw new CompatibilityCheckerException("Genotype call list must not be empty!");

		this.pedigree = pedigree;
		this.list = list;

		this.queryDecorator = new PedigreeQueryDecorator(pedigree);
		final ImmutableSet<String> parentNames = queryDecorator.getParentNames();
		if (parentNames.size() > 2)
			throw new CompatibilityCheckerException("Only two parents are allowed when checking for autosomal "
					+ "recessive compound heterozygous mode of inheritance.");

		// get father and mother idx in pedigree
		int fatherIdx = -1, motherIdx = -1; // indexes of the parents
		for (String name : parentNames) {
			Pedigree.IndexedPerson indexedPerson = pedigree.nameToMember.get(name);
			if (indexedPerson == null)
				throw new RuntimeException("Unknown member, should never occur here!");
			else if (indexedPerson.person.sex == Sex.MALE)
				fatherIdx = indexedPerson.idx;
			else if (indexedPerson.person.sex == Sex.FEMALE)
				motherIdx = indexedPerson.idx;
		}
		this.fatherIdx = fatherIdx;
		this.motherIdx = motherIdx;
	}

	public boolean run() throws CompatibilityCheckerException {
		if (pedigree.members.size() == 1)
			return runSingleSampleCase();
		else
			return runMultiSampleCase();
	}

	private boolean runSingleSampleCase() {
		int numHet = 0;
		for (ImmutableList<Genotype> gtList : list.calls)
			if (gtList.get(0) == Genotype.HOMOZYGOUS_ALT)
				return true;
			else if (gtList.get(0) == Genotype.HETEROZYGOUS)
				numHet++;
		return (numHet > 1);
	}

	private boolean runMultiSampleCase() throws CompatibilityCheckerException {
		if (new CompatibilityCheckerAutosomalRecessiveHomozygous(pedigree, list).run())
			return true;

		// TODO(holtgrew): Nick's original code is the same as for the autosomal recessive compound heterozygous...
		return false;
	}

}
