package de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar;

import java.util.ArrayList;
import java.util.List;

import de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.AbstractVariantContextCompatibilityChecker;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper class for checking a {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} for compatibility with a {@link de.charite.compbio.jannovar.pedigree.Pedigree} and
 * autosomal recessive mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * This class first checks whether we have a case of autosomal recessive homozygous and falls back to a check to
 * autosomal recessive compound heterozygous. The checks themselves are delegated to
 * {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar.VariantContextCompatibilityCheckerAutosomalRecessiveHomozygous} and
 * {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar.VariantContextCompatibilityCheckerAutosomalRecessiveCompoundHet}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @version 0.15-SNAPSHOT
 */
public class VariantContextCompatibilityCheckerAutosomalRecessive extends AbstractVariantContextCompatibilityChecker {

	/**
	 * Initialize compatibility checker for Autosomal recessive and perform some sanity checks.
	 *
	 * @see AbstractVariantContextCompatibilityChecker#AbstractVariantContextCompatibilityChecker(Pedigree, List)
	 * @param pedigree
	 *            the {@link de.charite.compbio.jannovar.pedigree.Pedigree} to use for the initialize
	 * @param list
	 *            the {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} to use for the initialization
	 * @throws de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public VariantContextCompatibilityCheckerAutosomalRecessive(Pedigree pedigree, List<VariantContext> list)
			throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);
	}

	/**
	 * Initialize compatibility checker for autosomal recessive and perform some sanity checks.
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
	public VariantContextCompatibilityCheckerAutosomalRecessive(Pedigree pedigree, InheritanceVariantContextList list)
			throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);
	}
	
	@Override
	public List<VariantContext> run() throws InheritanceCompatibilityCheckerException {
		if (!list.isAutosomal())
			return new ArrayList<VariantContext>(0);
		new VariantContextCompatibilityCheckerAutosomalRecessiveHomozygous(pedigree, list).run();
		new VariantContextCompatibilityCheckerAutosomalRecessiveCompoundHet(pedigree, list).run();
		return super.getMatchedVariants();
	}

	public void runSingleSampleCase() {
	}

	public void runMultiSampleCase() {
	}

}
