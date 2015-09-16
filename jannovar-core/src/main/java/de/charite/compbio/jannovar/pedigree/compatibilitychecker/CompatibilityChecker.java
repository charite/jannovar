package de.charite.compbio.jannovar.pedigree.compatibilitychecker;



/**
 * @author Max Schubach <max.schubach@charite.de>
 *
 *@deprecated use {@link InterfaceCompatibilityChecker} instead.
 */
@Deprecated
public interface CompatibilityChecker {

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
