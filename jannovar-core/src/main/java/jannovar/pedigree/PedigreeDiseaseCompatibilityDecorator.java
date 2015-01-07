package jannovar.pedigree;


/**
 * Decorator for {@link Pedigree} that allows checking whether a Genotype call is compatible with a selected mode of
 * inheritance.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
public class PedigreeDiseaseCompatibilityDecorator {

	/** the pedigree */
	public final Pedigree pedigree;

	/**
	 * Initialize decorator.
	 */
	public PedigreeDiseaseCompatibilityDecorator(Pedigree pedigree) {
		this.pedigree = pedigree;
	}

	/**
	 * @return <code>true</code> if the <code>list</code> of {@link Genotype} calls is compatible with the autosomal
	 *         dominant mode of inheritance
	 * @throws CompatibilityCheckerException
	 *             if there are problems with <code>list</code> or {@link #pedigree}.
	 */
	public boolean isCompatibleWithAutosomalDominant(GenotypeList list) throws CompatibilityCheckerException {
		return new CompatibilityCheckerAutosomalDominant(pedigree, list).run();
	}

	/**
	 * @return <code>true</code> if the <code>list</code> of {@link Genotype} calls is compatible with the autosomal
	 *         recessive mode of inheritance
	 */
	public boolean isCompatibleWithAutosomalRecessive(GenotypeList list) throws CompatibilityCheckerException {
		return new CompatibilityCheckerAutosomalRecessive(pedigree, list).run();
	}

	/**
	 * @return <code>true</code> if the <code>list</code> of {@link Genotype} calls is compatible with the X dominant
	 *         mode of inheritance
	 */
	public boolean isCompatibleWithXDominant(GenotypeList list) throws CompatibilityCheckerException {
		return new CompatibilityCheckerXDominant(pedigree, list).run();
	}

	/**
	 * @return <code>true</code> if the <code>list</code> of {@link Genotype} calls is compatible with the X recessive
	 *         mode of inheritance
	 * @throws CompatibilityCheckerException
	 *             if there are problems with <code>list</code> or {@link #pedigree}.
	 */
	public boolean isCompatibleWithXRecessive(GenotypeList list) throws CompatibilityCheckerException {
		return new CompatibilityCheckerXRecessive(pedigree, list).run();
	}

	/**
	 * Convenience method for checking whether a {@link GenotypeList} is compatible with a given
	 * {@link ModeOfInheritance} and pedigree.
	 *
	 * @param list
	 *            list of genotype calls to check for compatibility
	 * @param mode
	 *            mode of inheritance to use for the checking
	 * @return <code>true</code> if <code>call</code> is compatible with the given <code>mode</code> of inheritance,
	 *         also <code>true</code> if <code>mode</code> is {@link ModeOfInheritance#UNINITIALIZED}
	 * @throws CompatibilityCheckerException
	 *             if there are problems with <code>list</code> or {@link #pedigree}.
	 */
	public boolean isCompatibleWith(GenotypeList list, ModeOfInheritance mode) throws CompatibilityCheckerException {
		switch (mode) {
		case AUTOSOMAL_DOMINANT:
			return isCompatibleWithAutosomalDominant(list);
		case AUTOSOMAL_RECESSIVE:
			return isCompatibleWithAutosomalRecessive(list);
		case X_RECESSIVE:
			return isCompatibleWithXRecessive(list);
		case X_DOMINANT:
			return isCompatibleWithXDominant(list);
		case UNINITIALIZED:
		default:
			return true;
		}
	}

}
