package de.charite.compbio.jannovar.pedigree.compatibilitychecker;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList;
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
public class InheritanceCompatibilityChecker {

	public static final class Builder {

		private ImmutableSet.Builder<ModeOfInheritance> modeSetBuilder;
		private Pedigree pedigree;

		public Builder() {
			this.modeSetBuilder = new ImmutableSet.Builder<ModeOfInheritance>();
		}

		public Builder addMode(ModeOfInheritance inheritanceMode) {
			this.modeSetBuilder.add(inheritanceMode);
			return this;
		}

		public Builder addModes(Collection<ModeOfInheritance> inheritanceModes) {
			this.modeSetBuilder.addAll(inheritanceModes);
			return this;
		}

		public Builder pedigree(Pedigree pedigree) {
			this.pedigree = pedigree;
			return this;
		}

		public InheritanceCompatibilityChecker build() {
			return new InheritanceCompatibilityChecker(this.pedigree, modeSetBuilder.build());
		}
	}

	/** the pedigree */
	private final Pedigree pedigree;
	private final ImmutableSet<ModeOfInheritance> inheritanceModes;

	/**
	 * Initialize decorator.
	 * 
	 * @param immutableSet
	 */
	private InheritanceCompatibilityChecker(Pedigree pedigree, ImmutableSet<ModeOfInheritance> inheritanceModes) {
		this.pedigree = pedigree;
		this.inheritanceModes = inheritanceModes;
	}

	/**
	 * @return decorated pedigree to check
	 */
	public Pedigree getPedigree() {
		return pedigree;
	}

	/**
	 * @return mode of inheritances to check
	 */
	public ImmutableSet<ModeOfInheritance> getInheritanceModes() {
		return inheritanceModes;
	}

	/**
	 * Method for checking whether a {@link List} of {@link VariantContext} is compatible with a given
	 * {@link ModeOfInheritance} and {@link Pedigree} .
	 *
	 * @param vcList
	 *            {@link List} of {@link VariantContext} to check for compatibility
	 * @return
	 * @throws CompatibilityCheckerException
	 *             if there are problems with <code>list</code> or {@link #pedigree}.
	 */
	public List<VariantContext> getCompatibleWith(List<VariantContext> vcList) throws CompatibilityCheckerException {

		InheritanceVariantContextList list = new InheritanceVariantContextList(vcList);
		for (ModeOfInheritance mode : inheritanceModes) {
			switch (mode) {
			case AUTOSOMAL_DOMINANT:
				new VariantContextCompatibilityCheckerAutosomalDominant(pedigree, list).run();
				continue;
			case AUTOSOMAL_RECESSIVE:
				new VariantContextCompatibilityCheckerAutosomalRecessive(pedigree, list).run();
				continue;
			case X_RECESSIVE:
				new VariantContextCompatibilityCheckerXRecessive(pedigree, list).run();
				continue;
			case X_DOMINANT:
				new VariantContextCompatibilityCheckerXDominant(pedigree, list).run();
				continue;
			case UNINITIALIZED:
			default:
				continue;
			}
		}
		return list.getMatchedVariants();
	}

}
