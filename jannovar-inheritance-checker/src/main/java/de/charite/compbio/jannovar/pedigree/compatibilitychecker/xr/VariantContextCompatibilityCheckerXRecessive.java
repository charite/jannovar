package de.charite.compbio.jannovar.pedigree.compatibilitychecker.xr;

import java.util.ArrayList;
import java.util.List;

import de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.AbstractVariantContextCompatibilityChecker;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper class for checking a {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} for compatibility with a {@link de.charite.compbio.jannovar.pedigree.Pedigree} and X
 * recessive mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * If the pedigree has only one sample then the check is as follows. If the index is female, the checker returns true if
 * the genotype call list is compatible with autosomal recessive compound heterozygous inheritance or if the list
 * contains a homozygous alt call. If the index is male then return true if the list contains a homozygous alt call.
 *
 * If the pedigree has more samples, the checks are more involved.
 *
 * <b>Note</b> that the case of X-chromosomal compound heterozygous mutations is only handled in the single case. For
 * larger pedigrees we assume that female individuals are not affected. Otherwise it will be a dominant mutation,
 * because only affected males can be heredity the variant. De-novo mutations will be handled also from dominant
 * compatibility checker. If the gene is recessive some have to look for the second mutation by its own.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @version 0.15-SNAPSHOT
 */
public class VariantContextCompatibilityCheckerXRecessive extends AbstractVariantContextCompatibilityChecker {

	/**
	 * Initialize compatibility checker for X recessive and perform some sanity checks.
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
	public VariantContextCompatibilityCheckerXRecessive(Pedigree pedigree, InheritanceVariantContextList list)
			throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);
	}
	/**
	 * Initialize compatibility checker for X recessive and perform some sanity checks.
	 *
	 * @see AbstractVariantContextCompatibilityChecker#AbstractVariantContextCompatibilityChecker(Pedigree,
	 *      List)
	 * @param pedigree
	 *            the {@link de.charite.compbio.jannovar.pedigree.Pedigree} to use for the initialize
	 * @param list
	 *            the {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} to use for the initialization
	 * @throws de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public VariantContextCompatibilityCheckerXRecessive(Pedigree pedigree, List<VariantContext> list) throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);
	}

	@Override
	public List<VariantContext> run() throws InheritanceCompatibilityCheckerException {
		if (!list.isXChromosomal())
			return new ArrayList<VariantContext>(0);
		new VariantContextCompatibilityCheckerXRecessiveHomozygous(pedigree, list).run();
		new VariantContextCompatibilityCheckerXRecessiveCompoundHet(pedigree, list).run();
		return super.getMatchedVariants();
	}

	public void runSingleSampleCase() throws InheritanceCompatibilityCheckerException {
	}

	public void runMultiSampleCase() {
	}

}
