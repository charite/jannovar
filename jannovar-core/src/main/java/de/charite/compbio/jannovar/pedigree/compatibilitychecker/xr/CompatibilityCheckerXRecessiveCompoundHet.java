package de.charite.compbio.jannovar.pedigree.compatibilitychecker.xr;

import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.GenotypeList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Sex;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.ACompatibilityChecker;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar.CompatibilityCheckerAutosomalRecessiveCompoundHet;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a
 * {@link Pedigree} and autosomal recessive homozygous mode.
 *
 * <h2>Compatibility Check</h2>
 *
 * In the case of a single individual, we require
 * {@link Genotype#HOMOZYGOUS_ALT}.
 *
 * In the case of multiple individuals, we require that the affects are
 * compatible, that the unaffected parents of affected individuals are not
 * {@link Genotype#HOMOZYGOUS_ALT}, unaffected females are not are not
 * {@link Genotype#HOMOZYGOUS_REF}, and that all unaffected individuals are not
 * {@link Genotype#HOMOZYGOUS_ALT}. The affected individuals are compatible if
 * no affected individual is {@link Genotype#HOMOZYGOUS_REF} or
 * {@link Genotype#HETEROZYGOUS} and there is at least one affected individual
 * that is {@link Genotype#HOMOZYGOUS_ALT}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
class CompatibilityCheckerXRecessiveCompoundHet extends ACompatibilityChecker {

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
	public CompatibilityCheckerXRecessiveCompoundHet(Pedigree pedigree, GenotypeList list)
			throws CompatibilityCheckerException {
		super(pedigree, list);
	}

	@Override
	public boolean runSingleSampleCase() throws CompatibilityCheckerException {
		// for female single case samples, allow autosomal recessive compound
				// heterozygous
				if (pedigree.members.get(0).sex == Sex.FEMALE)
					if (new CompatibilityCheckerAutosomalRecessiveCompoundHet(pedigree, list).run())
						return true;

				return false;
	}

	@Override
	public boolean runMultiSampleCase() {
		// FIXME Multisample case is needed.
		return false;
	}

}
