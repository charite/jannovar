package de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar;

import de.charite.compbio.jannovar.pedigree.GenotypeList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerBase;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and autosomal recessive
 * mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * This class first checks whether we have a case of autosomal recessive homozygous and falls back to a check to
 * autosomal recessive compound heterozygous. The checks themselves are delegated to
 * {@link CompatibilityCheckerAutosomalRecessiveHomozygous} and
 * {@link CompatibilityCheckerAutosomalRecessiveCompoundHet}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * 
 * @deprecated use {@link VariantContextCompatibilityCheckerAutosomalDominant} instead.
 */
@Deprecated
public class CompatibilityCheckerAutosomalRecessive extends CompatibilityCheckerBase {

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
		super(pedigree, list);
	}

	@Override
	public boolean run() throws CompatibilityCheckerException {
		if (new CompatibilityCheckerAutosomalRecessiveHomozygous(pedigree, list).run())
			return true;
		else
			return new CompatibilityCheckerAutosomalRecessiveCompoundHet(pedigree, list).run();
	}

	public boolean runSingleSampleCase() throws CompatibilityCheckerException {
		return false;
	}

	public boolean runMultiSampleCase() {
		return false;
	}

}
