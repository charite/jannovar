package jannovar.pedigree;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

// TODO(holtgrew): Besides the check for heterozygous, the code also checks for unaffectedsAreNotHomozygousALT which is not documents.

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and autosomal recessive
 * compound mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
class CompatibilityCheckerAutosomalRecessiveCompoundHet {

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
	 * Also, at most two parents are allowed when checking for compatibility for autosomal recessive compound
	 * hererozygous mode of inheritance.
	 *
	 * @param pedigree
	 *            the {@link Pedigree} to use for the initialize
	 * @param list
	 *            the {@link GenotypeList} to use for the initialization
	 * @throws CompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public CompatibilityCheckerAutosomalRecessiveCompoundHet(Pedigree pedigree, GenotypeList list)
			throws CompatibilityCheckerException {
		if (pedigree.members.size() == 0)
			throw new CompatibilityCheckerException("Invalid pedigree of size 1.");
		if (!list.namesEqual(pedigree))
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

	/**
	 * @return <code>true</code> if {@link #list} is compatible with {@link #pedigree} and the recessive compound
	 *         hererozygous mode of inheritances.
	 * @throws CompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public boolean run() throws CompatibilityCheckerException {
		// Then check the compound case.
		if (pedigree.members.size() == 1)
			return runSingleSampleCase();
		else
			return runMultiSampleCase();
	}

	private boolean runSingleSampleCase() {
		int numHet = 0;
		for (ImmutableList<Genotype> gtList : list.calls)
			if (gtList.get(0) == Genotype.HETEROZYGOUS)
				numHet++;
		return (numHet > 1);
	}

	private boolean runMultiSampleCase() {
		ArrayList<ImmutableList<Genotype>> paternal = new ArrayList<ImmutableList<Genotype>>();
		ArrayList<ImmutableList<Genotype>> maternal = new ArrayList<ImmutableList<Genotype>>();

		// Collect genotype call lists that are heterozygous in in the affected and heterozygous in exactly one parent.
		for (ImmutableList<Genotype> gtList : list.calls) {
			if (affectedsAreHeterozygous(gtList) && onlyOneParentIsHeterozygous(gtList)
					&& unaffectedsAreNotHomozygousALT(gtList)) {
				if (isHeterozygous(gtList, fatherIdx))
					paternal.add(gtList);
				else if (isHeterozygous(gtList, motherIdx))
					maternal.add(gtList);
				else
					throw new RuntimeException("Neither mother nor father are heterozygous with at least one "
							+ "parent being heterozygous.");
			}
		}

		// If we reach here, we have a pontentially empty list of {@link Genotype} calls that are heterozygous in the
		// father or the mother. If there is a combination of maternal and paternal genotypes that could be a valid
		// compount heterozygous mutation, then return true.
		for (ImmutableList<Genotype> patGTList : paternal)
			for (ImmutableList<Genotype> matGTList : maternal)
				if (isValidCompoundHet(patGTList, matGTList))
					return true;

		return false;
	}

	private boolean isValidCompoundHet(ImmutableList<Genotype> patGTList, ImmutableList<Genotype> matGTList) {
		for (Pedigree.IndexedPerson entry : pedigree.nameToMember.values())
			if ((entry.person.disease == Disease.UNAFFECTED) && (patGTList.get(entry.idx) == Genotype.HETEROZYGOUS)
					&& (matGTList.get(entry.idx) == Genotype.HETEROZYGOUS))
				return false;
		return true;
	}

	private boolean isHeterozygous(ImmutableList<Genotype> gtList, int parentIdx) {
		if (parentIdx == -1)
			return false;
		else
			return (gtList.get(parentIdx) == Genotype.HETEROZYGOUS);
	}

	private boolean unaffectedsAreNotHomozygousALT(ImmutableList<Genotype> gtList) {
		for (Pedigree.IndexedPerson entry : pedigree.nameToMember.values())
			if (entry.person.disease == Disease.UNAFFECTED && gtList.get(entry.idx) == Genotype.HOMOZYGOUS_ALT)
				return false;
		return true;
	}

	private boolean onlyOneParentIsHeterozygous(ImmutableList<Genotype> gtList) {
		return (isHeterozygous(gtList, motherIdx) ^ isHeterozygous(gtList, fatherIdx));
	}

	private boolean affectedsAreHeterozygous(ImmutableList<Genotype> gtList) {
		for (Pedigree.IndexedPerson entry : pedigree.nameToMember.values())
			if (entry.person.disease == Disease.AFFECTED && gtList.get(entry.idx) != Genotype.HETEROZYGOUS)
				return false;
		return true;
	}

}
