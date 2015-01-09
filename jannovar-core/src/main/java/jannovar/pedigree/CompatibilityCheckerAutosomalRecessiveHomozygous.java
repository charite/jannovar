package jannovar.pedigree;

import jannovar.pedigree.Pedigree.IndexedPerson;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and autosomal recessive
 * compound mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * In the case of a single individual, we require {@link Genotype#HOMOZYGOUS_ALT}.
 *
 * In the case of multiple individuals, we require that the affects are compatible, that the unaffected parents of
 * affected individuals are not {@link Genotype#HOMOZYGOUS_REF} or {@link Genotype#HOMOZYGOUS_ALT} and that the
 * unaffected individuals are not {@link Genotype#HOMOZYGOUS_ALT}. The affected individuals are compatible if no
 * affected individual is {@link Genotype#HOMOZYGOUS_REF} or {@link Genotype#HETEROZYGOUS} and there is at least one
 * affected individual that is {@link Genotype#HOMOZYGOUS_ALT}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
class CompatibilityCheckerAutosomalRecessiveHomozygous {

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
	public CompatibilityCheckerAutosomalRecessiveHomozygous(Pedigree pedigree, GenotypeList list)
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
	 * @return <code>true</code> if {@link #list} is compatible with {@link #pedigree} and the recessive homozygous mode
	 *         of inheritances.
	 */
	public boolean run() {
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
		for (ImmutableList<Genotype> gtList : list.calls)
			if (containsCompatibleHomozygousVariants(gtList))
				return true;
		return false;
	}

	private boolean containsCompatibleHomozygousVariants(ImmutableList<Genotype> gtList) {
		return (affectedsAreCompatible(gtList) && unaffectedParentsOfAffectedAreNotHomozygous(gtList) && unaffectedsAreNotHomozygousAlt(gtList));
	}

	private boolean unaffectedsAreNotHomozygousAlt(ImmutableList<Genotype> gtList) {
		for (Pedigree.IndexedPerson entry : pedigree.nameToMember.values())
			if (entry.person.disease == Disease.UNAFFECTED && gtList.get(entry.idx) == Genotype.HOMOZYGOUS_ALT)
				return false;
		return true;
	}

	private boolean unaffectedParentsOfAffectedAreNotHomozygous(ImmutableList<Genotype> gtList) {
		for (String name : getUnaffectedParentNamesOfAffecteds()) {
			IndexedPerson iPerson = pedigree.nameToMember.get(name);
			// INVARIANT: iPerson cannot be null due to construction of Pedigree class
			if (gtList.get(iPerson.idx) == Genotype.HOMOZYGOUS_ALT
					|| gtList.get(iPerson.idx) == Genotype.HOMOZYGOUS_REF)
				return false;
		}
		return true;
	}

	/**
	 * @return names of unaffected parents of unaffecteds
	 */
	private ImmutableSet<String> getUnaffectedParentNamesOfAffecteds() {
		ImmutableSet.Builder<String> builder = new ImmutableSet.Builder<String>();

		for (Person person : pedigree.members)
			if (person.disease == Disease.AFFECTED) {
				if (person.father != null && person.father.disease == Disease.UNAFFECTED)
					builder.add(person.father.name);
				if (person.mother != null && person.mother.disease == Disease.UNAFFECTED)
					builder.add(person.mother.name);
			}

		return builder.build();
	}

	private boolean affectedsAreCompatible(ImmutableList<Genotype> gtList) {
		int numHomozygousAlt = 0;

		for (Pedigree.IndexedPerson entry : pedigree.nameToMember.values())
			if (entry.person.disease == Disease.AFFECTED) {
				if (gtList.get(entry.idx) != Genotype.HOMOZYGOUS_REF || gtList.get(entry.idx) != Genotype.HETEROZYGOUS)
					return false;
				else if (gtList.get(entry.idx) != Genotype.HOMOZYGOUS_ALT)
					numHomozygousAlt += 1;
			}

		return (numHomozygousAlt > 0);
	}

}
