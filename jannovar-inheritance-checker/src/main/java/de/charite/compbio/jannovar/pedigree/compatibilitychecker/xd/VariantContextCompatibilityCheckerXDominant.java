package de.charite.compbio.jannovar.pedigree.compatibilitychecker.xd;

import java.util.ArrayList;
import java.util.List;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.InheritanceVariantContext;
import de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Person;
import de.charite.compbio.jannovar.pedigree.Sex;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.AbstractVariantContextCompatibilityChecker;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.ad.VariantContextCompatibilityCheckerAutosomalDominant;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper class for checking a {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} for compatibility with a {@link de.charite.compbio.jannovar.pedigree.Pedigree} and X
 * dominant mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * For X-chromosomal dominant inheritance, there must be at least one {@link de.charite.compbio.jannovar.pedigree.Genotype} that is shared by all affected
 * individuals but no unaffected individuals in the pedigree.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @version 0.15-SNAPSHOT
 */
public class VariantContextCompatibilityCheckerXDominant extends AbstractVariantContextCompatibilityChecker {

	/**
	 * Initialize compatibility checker for X dominant and perform some sanity checks.
	 *
	 * @see AbstractVariantContextCompatibilityChecker#AbstractVariantContextCompatibilityChecker(Pedigree, List)
	 * @param pedigree
	 *            the {@link de.charite.compbio.jannovar.pedigree.Pedigree} to use for the initialize
	 * @param list
	 *            the {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} to use for the initialization
	 * @throws de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public VariantContextCompatibilityCheckerXDominant(Pedigree pedigree, List<VariantContext> list)
			throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);
	}

	/**
	 * Initialize compatibility checker for X dominant t and perform some sanity checks.
	 *
	 * @see AbstractVariantContextCompatibilityChecker#AbstractVariantContextCompatibilityChecker(Pedigree,
	 *      InheritanceVariantContextList)
	 * @param pedigree
	 *            the {@link de.charite.compbio.jannovar.pedigree.Pedigree} to use for the initialize
	 * @param list
	 *            the {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} to use for the initialization
	 * @throws de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public VariantContextCompatibilityCheckerXDominant(Pedigree pedigree, InheritanceVariantContextList list)
			throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);
	}

	@Override
	public List<VariantContext> run() throws InheritanceCompatibilityCheckerException {
		if (!list.isXChromosomal())
			return new ArrayList<VariantContext>(0);
		return super.run();
	}

	public void runSingleSampleCase() throws InheritanceCompatibilityCheckerException {
		if (pedigree.getMembers().get(0).getSex() == Sex.FEMALE)
			new VariantContextCompatibilityCheckerAutosomalDominant(pedigree, list).runSingleSampleCase();
		else {
			// We allow homozygous and heterozygous (false call).
			for (InheritanceVariantContext vc : list.getVcList())
				if (vc.getSingleSampleGenotype() == Genotype.HETEROZYGOUS
						|| vc.getSingleSampleGenotype() == Genotype.HOMOZYGOUS_ALT)
					vc.setMatchInheritance(true);
		}
	}

	public void runMultiSampleCase() {
		for (InheritanceVariantContext vc : list.getVcList()) {
			boolean currentVariantCompatible = true; // current variant compatible with XD?
			int numAffectedWithMut = 0;

			for (Person p : pedigree.getMembers()) {
				final Sex sex = p.getSex();
				final Genotype gt = vc.getGenotype(p);
				final Disease d = p.getDisease();

				if (d == Disease.AFFECTED) {
					if (gt == Genotype.HOMOZYGOUS_REF || (sex == Sex.FEMALE && gt == Genotype.HOMOZYGOUS_ALT)) {
						// we do not allow HOM_ALT for females to have the same behaviour as AD for females
						currentVariantCompatible = false;
						break;
					} else if (sex != Sex.FEMALE && (gt == Genotype.HETEROZYGOUS || gt == Genotype.HOMOZYGOUS_ALT)) {
						// we allow heterozygous here as well in case of mis-calls in the one X copy in the male or
						// unknown
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
				vc.setMatchInheritance(true);
		}
	}
}
