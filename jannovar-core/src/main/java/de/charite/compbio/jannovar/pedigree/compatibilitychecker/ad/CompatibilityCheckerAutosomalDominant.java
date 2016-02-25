package de.charite.compbio.jannovar.pedigree.compatibilitychecker.ad;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.GenotypeList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerBase;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and autosomal dominant
 * mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * For autosomal dominant inheritance, there must be at least one {@link Genotype} that is shared by all affected
 * individuals but no unaffected individuals in the pedigree.
 *
 * We do not allow {@link Genotype#HOMOZYGOUS_ALT} for any affected (and also for the one person in the case of
 * single-person pedigrees) since this is not the interesting case for users of this class.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * 
 * @deprecated use {@link VariantContextCompatibilityCheckerAutosomalDominant} instead.
 */
@Deprecated
public class CompatibilityCheckerAutosomalDominant extends CompatibilityCheckerBase {

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
	public CompatibilityCheckerAutosomalDominant(Pedigree pedigree, GenotypeList list)
			throws CompatibilityCheckerException {
		super(pedigree, list);
	}

	public boolean runSingleSampleCase() {
		// We could also allow Genotye.HOMOZYGOUS_ALT here but that is not the interesting case.
		for (ImmutableList<Genotype> gtList : list.getCalls())
			if (gtList.get(0) == Genotype.HETEROZYGOUS)
				return true;
		return false;
	}

	public boolean runMultiSampleCase() {
		for (ImmutableList<Genotype> gtList : list.getCalls()) {
			boolean currentVariantCompatible = true; // current variant compatible with AD?
			int numAffectedWithHet = 0;

			for (int i = 0; i < pedigree.getMembers().size(); ++i) {
				final Genotype gt = gtList.get(i);
				final Disease d = pedigree.getMembers().get(i).getDisease();

				if (d == Disease.AFFECTED) {
					if (gt == Genotype.HOMOZYGOUS_REF || gt == Genotype.HOMOZYGOUS_ALT) {
						currentVariantCompatible = false; // current variant not compatible with AD
						break;
					} else if (gt == Genotype.HETEROZYGOUS) {
						numAffectedWithHet++;
					}
				} else if (d == Disease.UNAFFECTED) {
					if (gt == Genotype.HETEROZYGOUS || gt == Genotype.HOMOZYGOUS_ALT) {
						currentVariantCompatible = false; // current variant not compatible with AD
						break;
					}
				}
			}

			// If we reach here, we have either examined all members of the pedigree or have decided that the
			// variant is incompatible in one person. If any one variant is compatible with AD inheritance, then the
			// Gene is compatible and we can return true without examining the other variants.
			if (currentVariantCompatible && numAffectedWithHet > 0)
				return true;
		}

		return false;
	}

}
