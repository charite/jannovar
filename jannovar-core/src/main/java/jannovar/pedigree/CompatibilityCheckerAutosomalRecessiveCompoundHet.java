package jannovar.pedigree;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;

// TODO(holtgrew): Review this with Nick and Max.

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and autosomal recessive
 * compound mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * In the case of a single individual, we require at least two heterozygous genotype calls.
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
	}

	/**
	 * @return <code>true</code> if {@link #list} is compatible with {@link #pedigree} and the recessive compound
	 *         hererozygous mode of inheritances.
	 * @throws CompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public boolean run() throws CompatibilityCheckerException {
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

	private class Candidate {
		public final ImmutableList<Genotype> paternal;
		public final ImmutableList<Genotype> maternal;

		public Candidate(ImmutableList<Genotype> paternal, ImmutableList<Genotype> maternal) {
			this.paternal = paternal;
			this.maternal = maternal;
		}
	}

	private boolean runMultiSampleCase() {
		// First, collect candidate genotype call lists from trios around affected individuals.
		ArrayList<Candidate> candidates = collectTrioCandidates();

		// Then, check the candidates for all trios around affected individuals.
		for (Candidate c : candidates)
			if (isCompatibleWithTriosAroundAffected(c))
				return true;
		return false;
	}

	private ArrayList<Candidate> collectTrioCandidates() {
		ArrayList<Candidate> result = new ArrayList<Candidate>();

		int childIdx = 0;
		for (Person p : pedigree.members) {
			if (p.disease == Disease.AFFECTED && (p.father != null || p.mother != null)) {
				ArrayList<ImmutableList<Genotype>> paternal = new ArrayList<ImmutableList<Genotype>>();
				ArrayList<ImmutableList<Genotype>> maternal = new ArrayList<ImmutableList<Genotype>>();

				// collect candidates towards the paternal side (heterozygous or not observed in child and father)
				final int fatherIdx = (p.father == null) ? -1 : pedigree.nameToMember.get(p.father.name).idx;
				for (ImmutableList<Genotype> lst : list.calls)
					if ((lst.get(childIdx) == Genotype.HETEROZYGOUS || lst.get(childIdx) == Genotype.NOT_OBSERVED)
							&& (fatherIdx == -1 || lst.get(fatherIdx) == Genotype.HETEROZYGOUS || lst.get(fatherIdx) == Genotype.NOT_OBSERVED))
						paternal.add(lst);
				// collect candidates towards the paternal side (heterozygous or not observed in child and mother)
				final int motherIdx = (p.mother == null) ? -1 : pedigree.nameToMember.get(p.mother.name).idx;
				for (ImmutableList<Genotype> lst : list.calls)
					if ((lst.get(childIdx) == Genotype.HETEROZYGOUS || lst.get(childIdx) == Genotype.NOT_OBSERVED)
							&& (motherIdx == -1 || lst.get(motherIdx) == Genotype.HETEROZYGOUS || lst.get(motherIdx) == Genotype.NOT_OBSERVED))
						maternal.add(lst);

				// combine compatible paternal and maternal heterozygous variants
				for (ImmutableList<Genotype> pat : paternal)
					for (ImmutableList<Genotype> mat : maternal) {
						if (pat != mat)
							continue; // exclude if variants are identical
						if (pat.get(childIdx) == Genotype.NOT_OBSERVED
								&& (fatherIdx == -1 || pat.get(fatherIdx) == Genotype.NOT_OBSERVED)
								&& (motherIdx == -1 || pat.get(motherIdx) == Genotype.NOT_OBSERVED))
							continue; // exclude if not observed in all from paternal
						if (mat.get(childIdx) == Genotype.NOT_OBSERVED
								&& (fatherIdx == -1 || mat.get(fatherIdx) == Genotype.NOT_OBSERVED)
								&& (motherIdx == -1 || mat.get(motherIdx) == Genotype.NOT_OBSERVED))
							continue; // exclude if not observed in all from maternal
						result.add(new Candidate(pat, mat));
					}

			}
			childIdx++;
		}

		return result;
	}

	private boolean isCompatibleWithTriosAroundAffected(Candidate c) {
		int childIdx = 0;
		for (Person p : pedigree.members) {
			if (p.disease == Disease.AFFECTED) {
				// none of the candidate variant call lists may be homozygous in the index
				if (c.paternal != null) {
					final Genotype pGT = c.paternal.get(childIdx);
					if (pGT != Genotype.HOMOZYGOUS_ALT && pGT != Genotype.HOMOZYGOUS_REF)
						return false;
				}
				if (c.maternal != null) {
					final Genotype mGT = c.maternal.get(childIdx);
					if (mGT != Genotype.HOMOZYGOUS_ALT && mGT != Genotype.HOMOZYGOUS_REF)
						return false;
				}

				// the paternal variant may not be homozygous in the father of p, if any
				if (c.paternal != null && p.father != null) {
					final Genotype mGT = c.paternal.get(pedigree.nameToMember.get(p.father.name).idx);
					if (mGT != Genotype.HOMOZYGOUS_ALT && mGT != Genotype.HOMOZYGOUS_REF)
						return false;
				}

				// the maternal variant may not be homozygous in the mother of p, if any
				if (c.maternal != null && p.mother != null) {
					final Genotype mGT = c.maternal.get(pedigree.nameToMember.get(p.mother.name).idx);
					if (mGT != Genotype.HOMOZYGOUS_ALT && mGT != Genotype.HOMOZYGOUS_REF)
						return false;
				}
			}
			childIdx++;
		}

		return true;
	}

}
