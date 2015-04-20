package de.charite.compbio.jannovar.pedigree.compatibilitychecker;

import de.charite.compbio.jannovar.pedigree.GenotypeList;
import de.charite.compbio.jannovar.pedigree.Pedigree;

/**
 * Abstract helper class for checking a {@link GenotypeList} for compatibility
 * with a {@link Pedigree}.
 * 
 * This class summarizes the builder compatibility checks.
 *
 * @author Max Schubach <max.schubach@charite.de>
 */
public abstract class ACompatibilityChecker implements ICompatibilityChecker {

	/** the pedigree to use for the checking */
	public final Pedigree pedigree;

	/** the genotype call list to use for the checking */
	public final GenotypeList list;

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
	public ACompatibilityChecker(Pedigree pedigree, GenotypeList list) throws CompatibilityCheckerException {
		if (pedigree.members.size() == 0)
			throw new CompatibilityCheckerException("Invalid pedigree of size 1.");
		if (!list.namesEqual(pedigree))
			throw new CompatibilityCheckerException("Incompatible names in pedigree and genotype list.");
		if (list.calls.get(0).size() == 0)
			throw new CompatibilityCheckerException("Genotype call list must not be empty!");

		this.pedigree = pedigree;
		this.list = list;
	}


	/* (non-Javadoc)
	 * @see de.charite.compbio.jannovar.pedigree.compatibilitychecker.ICompatibilityChecker#run()
	 */
	public boolean run() throws CompatibilityCheckerException {
		if (pedigree.members.size() == 1)
			return runSingleSampleCase();
		else
			return runMultiSampleCase();
	}
}
