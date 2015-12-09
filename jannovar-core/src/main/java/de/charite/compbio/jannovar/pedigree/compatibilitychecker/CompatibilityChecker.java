package de.charite.compbio.jannovar.pedigree.compatibilitychecker;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 * @deprecated use {@link InterfaceCompatibilityChecker} instead.
 */
@Deprecated
public interface CompatibilityChecker {

	/**
	 * @return <code>true</code> if the genotype list is compatible with the pedigree and the mode of inheritances
	 * @throws CompatibilityCheckerException
	 */
	public boolean run() throws CompatibilityCheckerException;

	/**
	 * @return <code>true</code> if the genotype list is compatible with the mode of inheritance only of the index
	 *         (first individual in the pedigree)
	 * @throws CompatibilityCheckerException
	 */
	boolean runSingleSampleCase() throws CompatibilityCheckerException;

	/**
	 * @return <code>true</code> if the genotype list is compatible with the complete pedigree and the mode of
	 *         inheritances
	 */
	boolean runMultiSampleCase();
}
