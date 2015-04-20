package de.charite.compbio.jannovar.pedigree.compatibilitychecker;

public interface ICompatibilityChecker {

	/**
	 * @return <code>true</code> if {@link #list} is compatible with
	 *         {@link #pedigree} and the mode of inheritances.
	 * @throws CompatibilityCheckerException
	 */
	public boolean run() throws CompatibilityCheckerException;

	/**
	 * @return <code>true</code> if {@link #list} is compatible with the mode of
	 *         inheritance only of the index (first individual in the
	 *         {@link #pedigree})
	 * @throws CompatibilityCheckerException
	 */
	boolean runSingleSampleCase() throws CompatibilityCheckerException;

	/**
	 * @return <code>true</code> if {@link #list} is compatible with the
	 *         complete {@link #pedigree} and the mode of inheritances.
	 * @throws CompatibilityCheckerException
	 */
	boolean runMultiSampleCase();
}
