package de.charite.compbio.jannovar.pedigree.compatibilitychecker;

import java.util.ArrayList;
import java.util.List;

import de.charite.compbio.jannovar.pedigree.ModeOfInheritance;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.ad.VariantContextCompatibilityCheckerAutosomalDominant;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar.VariantContextCompatibilityCheckerAutosomalRecessive;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.xd.VariantContextCompatibilityCheckerXDominant;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.xr.VariantContextCompatibilityCheckerXRecessive;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Decorator for {@link Pedigree} that allows checking whether a {@link htsjdk.variant.variantcontext.Genotype} call of
 * a {@link VariantContext} is compatible with a selected mode of inheritance.
 *
 * @author Max Schubach <max.schubach@charite.de>
 */
public class PedigreeCompatibilityChecker {
	
	/** the pedigree */
	private final Pedigree pedigree;

	/**
	 * Initialize decorator.
	 */
	public PedigreeCompatibilityChecker(Pedigree pedigree) {
		this.pedigree = pedigree;
	}

	/**
	 * @return decorated pedigree
	 */
	public Pedigree getPedigree() {
		return pedigree;
	}

	/**
	 * @param list
	 *            {@link List} of {@link VariantContext} that will be checked and filtered for autosomal dominant
	 *            inheritance
	 * @return A {@link List} of {@link VariantContext} that passes the autosomal dominant mode of inheritance.
	 *         {@link List#isEmpty()} <code>true</code> if no {@link VariantContext} matches the inheritance.
	 * @throws CompatibilityCheckerException
	 *             if there are problems with <code>list</code> or {@link #pedigree}.
	 */
	public List<VariantContext> isCompatibleWithAutosomalDominant(List<VariantContext> list)
			throws CompatibilityCheckerException {
		VariantContextCompatibilityCheckerAutosomalDominant checker = new VariantContextCompatibilityCheckerAutosomalDominant(pedigree, list);
		checker.run();
		return checker.getMatchedVariants();
	}

	/**
	 * @param list
	 *            {@link List} of {@link VariantContext} that will be checked and filtered for autosomal recessive
	 *            inheritance
	 * @return A {@link List} of {@link VariantContext} that passes the autosomal recessive mode of inheritance.
	 *         {@link List#isEmpty()} <code>true</code> if no {@link VariantContext} matches the inheritance.
	 * @throws CompatibilityCheckerException
	 *             if there are problems with <code>list</code> or {@link #pedigree}.
	 */
	public List<VariantContext> isCompatibleWithAutosomalRecessive(List<VariantContext> list)
			throws CompatibilityCheckerException {
		VariantContextCompatibilityCheckerAutosomalRecessive checker =  new VariantContextCompatibilityCheckerAutosomalRecessive(pedigree, list);
		checker.run();
		return checker.getMatchedVariants();
	}

	/**
	 * Check for compatibility with X dominant mode of inheritance.
	 *
	 * The code assumes that all variants that are in <code>list</code> are on the X chromosome.
	 *
	 * @param list
	 *            {@link List} of {@link VariantContext} that will be checked and filtered for X dominant inheritance
	 * @return A {@link List} of {@link VariantContext} that passes the X dominant mode of inheritance.
	 *         {@link List#isEmpty()} <code>true</code> if no {@link VariantContext} matches the inheritance.
	 * @throws CompatibilityCheckerException
	 *             if there are problems with <code>list</code> or {@link #pedigree}.
	 */
	public List<VariantContext> isCompatibleWithXDominant(List<VariantContext> list)
			throws CompatibilityCheckerException {
		VariantContextCompatibilityCheckerXDominant checker =  new VariantContextCompatibilityCheckerXDominant(pedigree, list);
		checker.run();
		return checker.getMatchedVariants();
	}

	/**
	 * Check for compatibility with X recessive mode of inheritance.
	 *
	 * The code assumes that all variants that are in <code>list</code> are on the X chromosome.
	 *
	 * @param list
	 *            {@link List} of {@link VariantContext} that will be checked and filtered for X recessive inheritance
	 * @return A {@link List} of {@link VariantContext} that passes the X recessive mode of inheritance.
	 *         {@link List#isEmpty()} <code>true</code> if no {@link VariantContext} matches the inheritance.
	 * @throws CompatibilityCheckerException
	 *             if there are problems with <code>list</code> or {@link #pedigree}.
	 */
	public List<VariantContext> isCompatibleWithXRecessive(List<VariantContext> list)
			throws CompatibilityCheckerException {
		VariantContextCompatibilityCheckerXRecessive checker = new VariantContextCompatibilityCheckerXRecessive(pedigree, list);
		checker.run();
		return checker.getMatchedVariants();
	}

	/**
	 * Convenience method for checking whether a {@link List} of {@link VariantContext} is compatible with a given
	 * {@link ModeOfInheritance} and {@link Pedigree} .
	 *
	 * @param list
	 *            {@link List} of {@link VariantContext} to check for compatibility
	 * @param mode
	 *            mode of inheritance to use for the checking
	 * @return <code>true</code> if <code>call</code> is compatible with the given <code>mode</code> of inheritance,
	 *         also <code>true</code> if <code>mode</code> is {@link ModeOfInheritance#UNINITIALIZED}
	 * @throws CompatibilityCheckerException
	 *             if there are problems with <code>list</code> or {@link #pedigree}.
	 */
	public List<VariantContext> isCompatibleWith(List<VariantContext> list, ModeOfInheritance mode)
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
			return new ArrayList<VariantContext>(0);
		}
	}

}
