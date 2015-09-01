package de.charite.compbio.jannovar.pedigree;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.ad.CompatibilityCheckerAutosomalDominant;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar.CompatibilityCheckerAutosomalRecessive;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.xd.CompatibilityCheckerXDominant;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.xr.CompatibilityCheckerXRecessive;

/**
 * Decorator for {@link Pedigree} that allows checking whether a Genotype call is compatible with a selected mode of
 * inheritance.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 * 
 *  @deprecated use {@link InheritanceCompatibilityChecker} instead.  
 */
@Deprecated
public class PedigreeDiseaseCompatibilityDecorator {

	/** the pedigree */
	private final Pedigree pedigree;

	/**
	 * Initialize decorator.
	 */
	public PedigreeDiseaseCompatibilityDecorator(Pedigree pedigree) {
		this.pedigree = pedigree;
	}

	/** @return decorated pedigree */
	public Pedigree getPedigree() {
		return pedigree;
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
	 * @throws CompatibilityCheckerException
	 *             if there are problems with <code>list</code> or {@link #pedigree}.
	 */
	public boolean isCompatibleWithAutosomalRecessive(GenotypeList list) throws CompatibilityCheckerException {
		return new CompatibilityCheckerAutosomalRecessive(pedigree, list).run();
	}

	/**
	 * Check for compatibility with X dominant mode of inheritance.
	 *
	 * The code assumes that all variants that are in <code>list</code> are on the X chromosome. This is an API decision
	 * made such that no position information is put into {@link GenotypeList} and makes things considerably easier to
	 * use.
	 *
	 * @return <code>true</code> if the <code>list</code> of {@link Genotype} calls is compatible with the X dominant
	 *         mode of inheritance
	 * @throws CompatibilityCheckerException
	 *             if there are problems with <code>list</code> or {@link #pedigree}.
	 */
	public boolean isCompatibleWithXDominant(GenotypeList list) throws CompatibilityCheckerException {
		return new CompatibilityCheckerXDominant(pedigree, list).run();
	}

	/**
	 * Check for compatibility with X recessive mode of inheritance.
	 *
	 * The code assumes that all variants that are in <code>list</code> are on the X chromosome. This is an API decision
	 * made such that no position information is put into {@link GenotypeList} and makes things considerably easier to
	 * use.
	 *
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
	public boolean isCompatibleWith(GenotypeList list, ModeOfInheritance mode)
			throws CompatibilityCheckerException {
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
