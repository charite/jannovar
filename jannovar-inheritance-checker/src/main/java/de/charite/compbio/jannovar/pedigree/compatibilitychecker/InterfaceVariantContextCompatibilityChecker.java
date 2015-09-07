package de.charite.compbio.jannovar.pedigree.compatibilitychecker;

import java.util.List;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * Interface for a VariantContextCompatibilityChecker. The have two modes. A single sample run and a multi sample run.
 * the general method {@link #run()} will decide if it is a singe sample or a multi sample.
 *
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @version 0.15-SNAPSHOT
 */
public interface InterfaceVariantContextCompatibilityChecker {

	/**
	 * <p>
	 * run.
	 * </p>
	 *
	 * @return All variants of the {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} that are
	 *         compatible with {@link de.charite.compbio.jannovar.pedigree.Pedigree} and the mode of inheritances.
	 * @throws de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException
	 *             if the VCF {@link htsjdk.variant.variantcontext.VariantContext} does not match to the
	 *             {@link de.charite.compbio.jannovar.pedigree.Pedigree}
	 */
	public List<VariantContext> run() throws InheritanceCompatibilityCheckerException;

	/**
	 * All variants of the {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} that are compatible with index (first individual of the
	 * VCF file) and the mode of inheritances will be set <code>true</code> for their compatibility
	 *
	 * @throws de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException
	 *             if the VCF {@link htsjdk.variant.variantcontext.VariantContext} does not match to the
	 *             {@link de.charite.compbio.jannovar.pedigree.Pedigree}
	 */
	void runSingleSampleCase() throws InheritanceCompatibilityCheckerException;

	/**
	 * All variants of the {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} that are compatible with {@link de.charite.compbio.jannovar.pedigree.Pedigree} and the mode
	 * of inheritances will be set <code>true</code> for their compatibility.
	 *
	 * @throws de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException
	 *             if the VCF {@link htsjdk.variant.variantcontext.VariantContext} does not match to the
	 *             {@link de.charite.compbio.jannovar.pedigree.Pedigree}
	 */
	void runMultiSampleCase() throws InheritanceCompatibilityCheckerException;
}
