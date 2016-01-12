package de.charite.compbio.jannovar.pedigree.compatibilitychecker.xd;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.GenotypeList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Sex;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerBase;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.ad.CompatibilityCheckerAutosomalDominant;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and X dominant mode of
 * inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * For X-chromosomal dominant inheritance, there must be at least one {@link Genotype} that is shared by all affected
 * individuals but no unaffected individuals in the pedigree.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * 
 * @deprecated use {@link VariantContextCompatibilityCheckerXDominant} instead.
 */
@Deprecated
public class CompatibilityCheckerXDominant extends CompatibilityCheckerBase {

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
	public CompatibilityCheckerXDominant(Pedigree pedigree, GenotypeList list) throws CompatibilityCheckerException {
		super(pedigree, list);
	}

	/**
	 * @return <code>true</code> if {@link #list} is compatible with {@link #pedigree} and the X-chromosomal dominant
	 *         mode of inheritances.
	 * @throws CompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public boolean run() throws CompatibilityCheckerException {
		if (!list.isXChromosomal())
			return false;
		return super.run();
	}

	public boolean runSingleSampleCase() throws CompatibilityCheckerException {
		if (pedigree.getMembers().get(0).getSex() == Sex.FEMALE)
			return new CompatibilityCheckerAutosomalDominant(pedigree, list).run();
		else {
			// We allow homozygous and heterozygous (false call).
			for (ImmutableList<Genotype> gtList : list.getCalls())
				if (gtList.get(0) == Genotype.HETEROZYGOUS || gtList.get(0) == Genotype.HOMOZYGOUS_ALT)
					return true;
			return false;
		}
	}
	
	public boolean runMultiSampleCase() {
		for (ImmutableList<Genotype> gtList : list.getCalls()) {
			boolean currentVariantCompatible = true; // current variant compatible with XD?
			int numAffectedWithMut = 0;

			for (int i = 0; i < pedigree.getMembers().size(); ++i) {
				final Sex sex = pedigree.getMembers().get(i).getSex();
				final Genotype gt = gtList.get(i);
				final Disease d = pedigree.getMembers().get(i).getDisease();

				if (d == Disease.AFFECTED) {
					if (gt == Genotype.HOMOZYGOUS_REF || (sex == Sex.FEMALE && gt == Genotype.HOMOZYGOUS_ALT)) {
						// we do not allow HOM_ALT for females to have the same behaviour as AD for females
						currentVariantCompatible = false;
						break;
					} else if (sex != Sex.FEMALE && (gt == Genotype.HETEROZYGOUS || gt == Genotype.HOMOZYGOUS_ALT)) {
						// we allow heterozygous here as well in case of mis-calls in the one X copy in the male or unknown
						numAffectedWithMut++;
					} else if (sex == Sex.FEMALE && gt == Genotype.HETEROZYGOUS) {
						numAffectedWithMut++;
					}
				} else if (d == Disease.UNAFFECTED) {
					if (gt == Genotype.HETEROZYGOUS || gt == Genotype.HOMOZYGOUS_ALT) {
						currentVariantCompatible = false; // current variant not compatible with AD
						break;
					}
				}
			}

			// If we reach here, we have either examined all members of the pedigree or have decided that the
			// variant is incompatible in one person. If any one variant is compatible with AD inheritance, than the
			// Gene is compatible and we can return true without examining the other variants.
			if (currentVariantCompatible && numAffectedWithMut > 0)
				return true;
		}

		return false;
	}
}
