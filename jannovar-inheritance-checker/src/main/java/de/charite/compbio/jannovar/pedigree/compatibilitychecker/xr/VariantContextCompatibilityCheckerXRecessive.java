package de.charite.compbio.jannovar.pedigree.compatibilitychecker.xr;

import java.util.ArrayList;
import java.util.List;

import de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.AbstractVariantContextCompatibilityChecker;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper class for checking a {@link InheritanceVariantContextList} for compatibility with a {@link Pedigree} and X
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
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
public class VariantContextCompatibilityCheckerXRecessive extends AbstractVariantContextCompatibilityChecker {

	/**
	 * Initialize compatibility checker and perform some sanity checks.
	 *
	 * The {@link InheritanceVariantContextList} object passed to the constructor is expected to represent all of the
	 * variants found in a certain gene (possibly after filtering for rarity or predicted pathogenicity). The samples
	 * represented by the {@link InheritanceVariantContextList} must be in the same order as the list of individuals
	 * contained in this pedigree.
	 *
	 * @param pedigree
	 *            the {@link Pedigree} to use for the initialize
	 * @param list
	 *            the {@link InheritanceVariantContextList} to use for the initialization
	 * @throws InheritanceCompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public VariantContextCompatibilityCheckerXRecessive(Pedigree pedigree, InheritanceVariantContextList list)
			throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);
	}

	public VariantContextCompatibilityCheckerXRecessive(Pedigree pedigree, List<VariantContext> list) throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);
	}

	/* (non-Javadoc)
	 * @see de.charite.compbio.jannovar.pedigree.compatibilitychecker.AbstractCompatibilityChecker#run()
	 */
	@Override
	public List<VariantContext> run() throws InheritanceCompatibilityCheckerException {
		if (!list.isXChromosomal())
			return new ArrayList<VariantContext>(0);
		new VariantContextCompatibilityCheckerXRecessiveHomozygous(pedigree, list).run();
		new VariantContextCompatibilityCheckerXRecessiveCompoundHet(pedigree, list).run();
		return super.getMatchedVariants();
	}

	/* (non-Javadoc)
	 * @see de.charite.compbio.jannovar.pedigree.compatibilitychecker.InterfaceCompatibilityChecker#runSingleSampleCase()
	 */
	public void runSingleSampleCase() throws InheritanceCompatibilityCheckerException {
	}

	/* (non-Javadoc)
	 * @see de.charite.compbio.jannovar.pedigree.compatibilitychecker.InterfaceCompatibilityChecker#runMultiSampleCase()
	 */
	public void runMultiSampleCase() {
	}

}
