package de.charite.compbio.jannovar.pedigree.compatibilitychecker;

import java.util.List;

import htsjdk.variant.variantcontext.VariantContext;

public interface InterfaceCompatibilityChecker {

	/**
	 * @return <code>true</code> if {@link #list} is compatible with
	 *         {@link #pedigree} and the mode of inheritances.
	 * @throws CompatibilityCheckerException
	 */
	public List<VariantContext> run() throws CompatibilityCheckerException;

	/**
	 * @return <code>true</code> if {@link #list} is compatible with the mode of
	 *         inheritance only of the index (first individual in the
	 *         {@link #pedigree})
	 * @throws CompatibilityCheckerException
	 */
	void runSingleSampleCase() throws CompatibilityCheckerException;

	/**
	 * @return <code>true</code> if {@link #list} is compatible with the
	 *         complete {@link #pedigree} and the mode of inheritances.
	 * @throws CompatibilityCheckerException
	 */
	void runMultiSampleCase();
}
