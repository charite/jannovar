package de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar;

import java.util.List;

import de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.AbstractVariantContextCompatibilityChecker;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper class for checking a {@link InheritanceVariantContextList} for compatibility with a {@link Pedigree} and autosomal recessive
 * mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * This class first checks whether we have a case of autosomal recessive homozygous and falls back to a check to
 * autosomal recessive compound heterozygous. The checks themselves are delegated to
 * {@link VariantContextCompatibilityCheckerAutosomalRecessiveHomozygous} and
 * {@link VariantContextCompatibilityCheckerAutosomalRecessiveCompoundHet}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
public class VariantContextCompatibilityCheckerAutosomalRecessive extends AbstractVariantContextCompatibilityChecker {

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
	public VariantContextCompatibilityCheckerAutosomalRecessive(Pedigree pedigree, List<VariantContext> list)
			throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);
	}

	public VariantContextCompatibilityCheckerAutosomalRecessive(Pedigree pedigree, InheritanceVariantContextList list) {
		super(pedigree, list);
	}

	/* (non-Javadoc)
	 * @see de.charite.compbio.jannovar.pedigree.compatibilitychecker.AbstractCompatibilityChecker#run()
	 */
	@Override
	public List<VariantContext> run() throws InheritanceCompatibilityCheckerException {
		new VariantContextCompatibilityCheckerAutosomalRecessiveHomozygous(pedigree, list).run();
		new VariantContextCompatibilityCheckerAutosomalRecessiveCompoundHet(pedigree, list).run();
		return super.getMatchedVariants();
	}

	public void runSingleSampleCase() {
	}

	public void runMultiSampleCase() {
	}

}
