package de.charite.compbio.jannovar.pedigree.compatibilitychecker;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.pedigree.InheritanceVariantContext;
import de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList;
import de.charite.compbio.jannovar.pedigree.ModeOfInheritance;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Person;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Abstract helper class for checking a {@link List} of {@link VariantContext} for compatibility with a {@link Pedigree}
 * .
 * 
 * This class summarizes the builder compatibility checks.
 *
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public abstract class AbstractVariantContextCompatibilityChecker implements InterfaceVariantContextCompatibilityChecker {

	/** the pedigree to use for the checking */
	protected final Pedigree pedigree;

	/** the variant list to use for the checking */
	protected final InheritanceVariantContextList list;

	/**
	 * Collects list of compatible mutations from father an mother for compound heterozygous.
	 */
	protected class Candidate {
		/** one VCF record compatible with mutation in father */
		private final InheritanceVariantContext paternal;
		/** one VCF record compatible with mutation in mother */
		private final InheritanceVariantContext maternal;

		public Candidate(InheritanceVariantContext paternal, InheritanceVariantContext maternal) {
			this.paternal = paternal;
			this.maternal = maternal;
		}

		/**
		 * @return one VCF record compatible with mutation in father
		 */
		public InheritanceVariantContext getPaternal() {
			return paternal;
		}

		/**
		 * @return one VCF record compatible with mutation in mother
		 */
		public InheritanceVariantContext getMaternal() {
			return maternal;
		}
	}

	/**
	 * Initialize compatibility checker and perform some sanity checks.
	 *
	 * The {@link List} of {@link VariantContext} is transformed to a {@link InheritanceVariantContextList}, passed to
	 * the constructor and is expected to represent all of the variants found in a certain gene (possibly after
	 * filtering for rarity or predicted pathogenicity).
	 *
	 * @param pedigree
	 *            the {@link Pedigree} to use for the compatibility
	 * @param list
	 *            the {@link List} of {@link VariantContext} to use for the initialization
	 * @throws InheritanceCompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public AbstractVariantContextCompatibilityChecker(Pedigree pedigree, List<VariantContext> list)
			throws InheritanceCompatibilityCheckerException {
		InheritanceVariantContextList vcList = new InheritanceVariantContextList(list);
		checkCompatibility(pedigree, vcList);

		this.pedigree = pedigree;
		this.list = vcList;
	}

	/**
	 * @param pedigree
	 *            To check for compatibility
	 * @param vcList
	 *            To check for compatibility
	 * @throws InheritanceCompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	private void checkCompatibility(Pedigree pedigree, InheritanceVariantContextList vcList)
			throws InheritanceCompatibilityCheckerException {
		if (pedigree.getMembers().size() == 0)
			throw new InheritanceCompatibilityCheckerException("Invalid pedigree of size 1.");
		if (!vcList.namesEqual(pedigree))
			throw new InheritanceCompatibilityCheckerException("Incompatible names in pedigree and genotype list.");
	}

	/**
	 * Initialize compatibility checker and perform some sanity checks.
	 *
	 * The {@link InheritanceVariantContextList} object passed to the constructor is expected to represent all of the
	 * variants found in a certain gene (possibly after filtering for rarity or predicted pathogenicity).
	 *
	 * @param pedigree
	 *            the {@link Pedigree} to use for the compatibility
	 * @param vcList
	 *            the {@link InheritanceVariantContextList} to use for the initialization
	 * @throws InheritanceCompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public AbstractVariantContextCompatibilityChecker(Pedigree pedigree, InheritanceVariantContextList vcList)
			throws InheritanceCompatibilityCheckerException {
		checkCompatibility(pedigree, vcList);
		this.pedigree = pedigree;
		this.list = vcList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.charite.compbio.jannovar.pedigree.compatibilitychecker.ICompatibilityChecker#run()
	 */
	public List<VariantContext> run() throws InheritanceCompatibilityCheckerException {
		if (pedigree.getMembers().size() == 1)
			runSingleSampleCase();
		else
			runMultiSampleCase();
		return getMatchedVariants();
	}

	/**
	 * @return sibling map for each {@link Person} in {@link Pedigree}, both parents must be in {@link Pedigree} and the
	 *         same
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

	/**
	 * Getter for all {@link VariantContext} that matched the {@link ModeOfInheritance}.
	 * 
	 * @return A List of {@link VariantContext} that are <code>true</code> for
	 *         {@link InheritanceVariantContext#isMatchInheritance()}.
	 */
	public List<VariantContext> getMatchedVariants() {
		return list.getMatchedVariants();
	}

}
