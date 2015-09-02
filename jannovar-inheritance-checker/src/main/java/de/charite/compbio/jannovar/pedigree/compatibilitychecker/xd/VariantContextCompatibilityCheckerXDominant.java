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
 * Helper class for checking a {@link InheritanceVariantContextList} for compatibility with a {@link Pedigree} and X dominant mode of
 * inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * For X-chromosomal dominant inheritance, there must be at least one {@link Genotype} that is shared by all affected
 * individuals but no unaffected individuals in the pedigree.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
public class VariantContextCompatibilityCheckerXDominant extends AbstractVariantContextCompatibilityChecker {

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
	 * @throws InheritanceCompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public VariantContextCompatibilityCheckerXDominant(Pedigree pedigree, List<VariantContext> list)
			throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);
	}

	public VariantContextCompatibilityCheckerXDominant(Pedigree pedigree, InheritanceVariantContextList list) {
		super(pedigree, list);
	}

	/* (non-Javadoc)
	 * @see de.charite.compbio.jannovar.pedigree.compatibilitychecker.AbstractCompatibilityChecker#run()
	 */
	@Override
	public List<VariantContext> run() throws InheritanceCompatibilityCheckerException {
		if (!list.isXChromosomal())
			return  new ArrayList<VariantContext>(0);
		return super.run();
	}

	/* (non-Javadoc)
	 * @see de.charite.compbio.jannovar.pedigree.compatibilitychecker.InterfaceCompatibilityChecker#runSingleSampleCase()
	 */
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

	/* (non-Javadoc)
	 * @see de.charite.compbio.jannovar.pedigree.compatibilitychecker.InterfaceCompatibilityChecker#runMultiSampleCase()
	 */
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
