package de.charite.compbio.jannovar.pedigree.compatibilitychecker;

import java.util.List;

import de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Interface for a VariantContextCompatibilityChecker. The have two modes. A single sample run and a multi sample run.
 * the general method {@link #run()} will decide if it is a singe sample or a multi sample.
 * 
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public interface InterfaceVariantContextCompatibilityChecker {

	/**
	 * @return All variants of the {@link InheritanceVariantContextList} that are compatible with {@link #pedigree} and
	 *         the mode of inheritances.
	 * @throws InheritanceCompatibilityCheckerException
	 *             if the VCF {@link VariantContext} does not match to teh {@link Pedigree}
	 */
	public List<VariantContext> run() throws InheritanceCompatibilityCheckerException;

	/**
	 * @return All variants of the {@link InheritanceVariantContextList} that are compatible with index (first
	 *         individual of the VCF file) and the mode of inheritances.
	 * @throws InheritanceCompatibilityCheckerException
	 *             if the VCF {@link VariantContext} does not match to teh {@link Pedigree}
	 */
	void runSingleSampleCase() throws InheritanceCompatibilityCheckerException;

	/**
	 * @return All variants of the {@link InheritanceVariantContextList} that are compatible with {@link #pedigree} and
	 *         the mode of inheritances.
	 * @throws InheritanceCompatibilityCheckerException
	 *             if the VCF {@link VariantContext} does not match to teh {@link Pedigree}
	 */
	void runMultiSampleCase() throws InheritanceCompatibilityCheckerException;
}
