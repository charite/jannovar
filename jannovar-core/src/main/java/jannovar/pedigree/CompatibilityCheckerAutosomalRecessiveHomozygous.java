package jannovar.pedigree;

import jannovar.pedigree.Pedigree.IndexedPerson;

import java.util.HashSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and autosomal recessive
 * compound mode of inheritance.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
class CompatibilityCheckerAutosomalRecessiveHomozygous {

	/** the pedigree to use for the checking */
	public final Pedigree pedigree;

	/** the genotype call list to use for the checking */
	public final GenotypeList list;

	/** decorator for getting unaffected individuals and such from the pedigree */
	private final PedigreeQueryDecorator queryDecorator;

	/** set of parent names */
	private final ImmutableSet<String> parentNames;

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
		if (list.isNamesEqual(pedigree))
			throw new CompatibilityCheckerException("Incompatible names in pedigree and genotype list.");
		if (list.calls.get(0).size() == 0)
			throw new CompatibilityCheckerException("Genotype call list must not be empty!");

		this.pedigree = pedigree;
		this.list = list;

		this.queryDecorator = new PedigreeQueryDecorator(pedigree);
		HashSet<String> parentNames = queryDecorator.getParentNames();
		if (parentNames.size() > 2)
			throw new CompatibilityCheckerException("Only two parents are allowed when checking for autosomal "
					+ "recessive homozygous mode of inheritance.");
		this.parentNames = ImmutableSet.copyOf(parentNames);
	}

	/**
	 * @return <code>true</code> if {@link #list} is compatible with {@link #pedigree} and the recessive homozygous mode
	 *         of inheritances.
	 * @throws CompatibilityCheckerException
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
			if (containsCompatibleHomozygousVariant(gtList))
				return true;
		return false;
	}

	private boolean containsCompatibleHomozygousVariant(ImmutableList<Genotype> gtList) {
		return (affectedsAreHomozygousALT(gtList) && parentsAreHeterozygous(gtList) && unaffectedsAreNotHomozygousALT(gtList));
	}

	private boolean unaffectedsAreNotHomozygousALT(ImmutableList<Genotype> gtList) {
		for (Pedigree.IndexedPerson entry : pedigree.nameToMember.values())
			if (entry.person.disease == Disease.UNAFFECTED && gtList.get(entry.idx) == Genotype.HOMOZYGOUS_ALT)
				return false;
		return true;
	}

	private boolean affectedsAreHomozygousALT(ImmutableList<Genotype> gtList) {
		for (Pedigree.IndexedPerson entry : pedigree.nameToMember.values())
			if (entry.person.disease == Disease.AFFECTED && gtList.get(entry.idx) != Genotype.HOMOZYGOUS_ALT)
				return false;
		return true;
	}

	private boolean parentsAreHeterozygous(ImmutableList<Genotype> gtList) {
		for (String name : parentNames) {
			IndexedPerson iPerson = pedigree.nameToMember.get(name);
			if (iPerson == null)
				throw new RuntimeException("Could not find person with name " + name + " and this is a bug here.");
			if (gtList.get(iPerson.idx) != Genotype.HETEROZYGOUS)
				return false;
		}
		return true;
	}
}
