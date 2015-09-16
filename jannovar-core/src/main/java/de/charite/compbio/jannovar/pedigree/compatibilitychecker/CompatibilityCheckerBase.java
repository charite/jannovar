package de.charite.compbio.jannovar.pedigree.compatibilitychecker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.GenotypeList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Person;

/**
 * Abstract helper class for checking a {@link GenotypeList} for compatibility
 * with a {@link Pedigree}.
 * 
 * This class summarizes the builder compatibility checks.
 *
 * @author Max Schubach <max.schubach@charite.de>
 * 
 * @deprecated use {@link AbstractCompatibilityChecker} instead.
 */
@Deprecated
public abstract class CompatibilityCheckerBase implements CompatibilityChecker {

	/** the pedigree to use for the checking */
	protected final Pedigree pedigree;

	/** the genotype call list to use for the checking */
	protected final GenotypeList list;
	
	/**
	 * Collects list of compatible mutations from father an mother for compound heterozygous.
	 */
	protected class Candidate {
		/** one VCF record compatible with mutation in father */
		private final ImmutableList<Genotype> paternal;
		/** one VCF record compatible with mutation in mother */
		private final ImmutableList<Genotype> maternal;

		public Candidate(ImmutableList<Genotype> paternal, ImmutableList<Genotype> maternal) {
			this.paternal = paternal;
			this.maternal = maternal;
		}

		/** @return one VCF record compatible with mutation in father */
		public ImmutableList<Genotype> getPaternal() {
			return paternal;
		}
		
		/** @return one VCF record compatible with mutation in mother */
		public ImmutableList<Genotype> getMaternal() {
			return maternal;
		}
}

	/**
	 * Initialize compatibility checker and perform some sanity checks.
	 *
	 * The {@link GenotypeList} object passed to the constructor is expected to
	 * represent all of the variants found in a certain gene (possibly after
	 * filtering for rarity or predicted pathogenicity). The samples represented
	 * by the {@link GenotypeList} must be in the same order as the list of
	 * individuals contained in this pedigree.
	 *
	 * @param pedigree
	 *            the {@link Pedigree} to use for the initialize
	 * @param list
	 *            the {@link GenotypeList} to use for the initialization
	 * @throws CompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public CompatibilityCheckerBase(Pedigree pedigree, GenotypeList list) throws CompatibilityCheckerException {
		if (pedigree.getMembers().size() == 0)
			throw new CompatibilityCheckerException("Invalid pedigree of size 1.");
		if (!list.namesEqual(pedigree))
			throw new CompatibilityCheckerException("Incompatible names in pedigree and genotype list.");
		if (list.getCalls().get(0).size() == 0)
			throw new CompatibilityCheckerException("Genotype call list must not be empty!");

		this.pedigree = pedigree;
		this.list = list;
	}


	/* (non-Javadoc)
	 * @see de.charite.compbio.jannovar.pedigree.compatibilitychecker.ICompatibilityChecker#run()
	 */
	public boolean run() throws CompatibilityCheckerException {
		if (pedigree.getMembers().size() == 1)
			return runSingleSampleCase();
		else
			return runMultiSampleCase();
	}
	
	
	/**
	 * @return siblig map for each person in <code>pedigree</code>, both parents
	 *         must be in <code>pedigree</code> and the same
	 */
	protected static ImmutableMap<Person, ImmutableList<Person>> buildSiblings(Pedigree pedigree) {
		ImmutableMap.Builder<Person, ImmutableList<Person>> mapBuilder = new ImmutableMap.Builder<Person, ImmutableList<Person>>();
		for (Person p1 : pedigree.getMembers()) {
			if (p1.getMother() == null || p1.getFather() == null)
				continue;
			ImmutableList.Builder<Person> listBuilder = new ImmutableList.Builder<Person>();
			for (Person p2 : pedigree.getMembers()) {
				if (p1.equals(p2) || !p1.getMother().equals(p2.getMother()) || !p1.getFather().equals(p2.getFather()))
					continue;
				listBuilder.add(p2);
			}
			mapBuilder.put(p1, listBuilder.build());
		}
		return mapBuilder.build();
	}

}
