package de.charite.compbio.jannovar.pedigree.compatibilitychecker.ad;

import java.util.List;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.InheritanceVariantContext;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Person;
import de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.AbstractCompatibilityChecker;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper class for checking a {@link InheritanceVariantContextList} for compatibility with a {@link Pedigree} and autosomal dominant
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
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
public class VariantContextCompatibilityCheckerAutosomalDominant extends AbstractCompatibilityChecker {

	/**
	 * Initialize compatibility checker and perform some sanity checks.
	 *
	 * The {@link InheritanceVariantContextList} object passed to the constructor is expected to represent all of the variants found in a
	 * certain gene (possibly after filtering for rarity or predicted pathogenicity). The samples represented by the
	 * {@link InheritanceVariantContextList} must be in the same order as the list of individuals contained in this pedigree.
	 *
	 * @param pedigree
	 *            the {@link Pedigree} to use for the initialize
	 * @param list
	 *            the {@link InheritanceVariantContextList} to use for the initialization
	 * @throws CompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public VariantContextCompatibilityCheckerAutosomalDominant(Pedigree pedigree, List<VariantContext> list)
			throws CompatibilityCheckerException {
		super(pedigree, list);
	}

	public VariantContextCompatibilityCheckerAutosomalDominant(Pedigree pedigree, InheritanceVariantContextList list) {
		super(pedigree, list);
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
