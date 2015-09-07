package de.charite.compbio.jannovar.pedigree.compatibilitychecker.ad;

import java.util.ArrayList;
import java.util.List;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.InheritanceVariantContext;
import de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Person;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.AbstractVariantContextCompatibilityChecker;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper class for checking a {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} for compatibility with a {@link de.charite.compbio.jannovar.pedigree.Pedigree} and
 * autosomal dominant mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * For autosomal dominant inheritance, there must be at least one {@link de.charite.compbio.jannovar.pedigree.Genotype} that is shared by all affected
 * individuals but no unaffected individuals in the pedigree.
 *
 * We do not allow {@link de.charite.compbio.jannovar.pedigree.Genotype#HOMOZYGOUS_ALT} for any affected (and also for the one person in the case of
 * single-person pedigrees) since this is not the interesting case for users of this class.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @version 0.15-SNAPSHOT
 */
public class VariantContextCompatibilityCheckerAutosomalDominant extends AbstractVariantContextCompatibilityChecker {

	/**
	 * Initialize compatibility checker for autosomal dominant and perform some sanity checks.
	 *
	 * @see AbstractVariantContextCompatibilityChecker#AbstractVariantContextCompatibilityChecker(Pedigree, List)
	 * @param pedigree
	 *            the {@link de.charite.compbio.jannovar.pedigree.Pedigree} to use for the initialize
	 * @param list
	 *            the {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} to use for the initialization
	 * @throws de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public VariantContextCompatibilityCheckerAutosomalDominant(Pedigree pedigree, List<VariantContext> list)
			throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);
	}

	/**
	 *
	 * Initialize compatibility checker for AutosomaöDominant and perform some sanity checks.
	 *
	 * @see AbstractVariantContextCompatibilityChecker#AbstractVariantContextCompatibilityChecker(Pedigree,
	 *      InheritanceVariantContextList)
	 * @param pedigree a {@link de.charite.compbio.jannovar.pedigree.Pedigree} object.
	 * @param list a {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} object.
	 * @throws de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public VariantContextCompatibilityCheckerAutosomalDominant(Pedigree pedigree, InheritanceVariantContextList list)
			throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);
	}
	
	@Override
	public List<VariantContext> run() throws InheritanceCompatibilityCheckerException {
		if (!list.isAutosomal())
			return new ArrayList<VariantContext>(0);
		return super.run();
	}

	public void runSingleSampleCase() {
		// We could also allow Genotye.HOMOZYGOUS_ALT here but that is not the interesting case.
		for (InheritanceVariantContext vc : list.getVcList())
			if (vc.getSingleSampleGenotype() == Genotype.HETEROZYGOUS)
				vc.setMatchInheritance(true);
	}

	public void runMultiSampleCase() {
		for (InheritanceVariantContext vc : list.getVcList()) {
			boolean currentVariantCompatible = true; // current variant compatible with AD?
			int numAffectedWithHet = 0;

			for (Person p : pedigree.getMembers()) {
				final Genotype gt = vc.getGenotype(p);
				final Disease d = p.getDisease();

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
			// variant is incompatible in one person. If any one genotype is compatible with AD inheritance, then the
			// Gene is compatible and we can return true without examining the other variants.
			if (currentVariantCompatible && numAffectedWithHet > 0)
				vc.setMatchInheritance(true);
		}

	}

}
