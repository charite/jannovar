package de.charite.compbio.jannovar.pedigree.compatibilitychecker.xr;

import de.charite.compbio.jannovar.pedigree.GenotypeList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerBase;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a
 * {@link Pedigree} and X recessive mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * If the pedigree has only one sample then the check is as follows. If the
 * index is female, the checker returns true if the genotype call list is
 * compatible with autosomal recessive compound heterozygous inheritance or if
 * the list contains a homozygous alt call. If the index is male then return
 * true if the list contains a homozygous alt call.
 *
 * If the pedigree has more samples, the checks are more involved.
 *
 * <b>Note</b> that the case of X-chromosomal compound heterozygous mutations is
 * only handled in the single case. For larger pedigrees we assume that female
 * individuals are not affected. Otherwise it will be a dominant mutation,
 * because only affected males can be heredity the variant. De-novo mutations
 * will be handled also from dominant compatibility checker. If the gene is
 * recessive some have to look for the second mutation by its own.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * 
 * @deprecated use {@link VariantContextCompatibilityCheckerXRecessive} instead.
 */
@Deprecated
public class CompatibilityCheckerXRecessive extends CompatibilityCheckerBase {
	
	

	/**
	 * Initialize compatibility checker and perform some sanity checks.
	 *
	 * The {@link GenotypeList} object passed to the constructor is expected to
	 * represent all of the variants found in a certain gene (possibly after
	 * filtering for rarity or predicted pathogenicity). The samples represented
	 * by the {@link GenotypeList} must be in the same order as the list of
	 * individuals contained in this pedigree.
	 *
	 * @param pedigree
	 *            the {@link Pedigree} to use for the initialize
	 * @param list
	 *            the {@link GenotypeList} to use for the initialization
	 * @throws CompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public CompatibilityCheckerXRecessive(Pedigree pedigree, GenotypeList list) throws CompatibilityCheckerException {
		super(pedigree, list);
	}
	
	@Override
	public boolean run() throws CompatibilityCheckerException {
		if (!list.isXChromosomal())
			return false;
		else if (new CompatibilityCheckerXRecessiveHomozygous(pedigree, list).run())
			return true;
		else
			return new CompatibilityCheckerXRecessiveCompoundHet(pedigree, list).run();
	}

	public boolean runSingleSampleCase() throws CompatibilityCheckerException {
		return false;
	}

	public boolean runMultiSampleCase() {
		return false;
	}
	
}
