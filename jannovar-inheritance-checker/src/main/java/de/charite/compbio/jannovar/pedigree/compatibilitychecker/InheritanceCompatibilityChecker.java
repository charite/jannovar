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
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public class InheritanceCompatibilityChecker {

	/**
	 * Builder for a compatibility checker. A {@link Pedigree} and at least one {@link ModeOfInheritance} is needed!
	 * 
	 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
	 */
	public static final class Builder {

		/** Set builder for all {@link ModeOfInheritance} the user want to check */
		private ImmutableSet.Builder<ModeOfInheritance> modeSetBuilder;
		/** The Pedigree */
		private Pedigree pedigree;

		/**
		 * default constructor.
		 */
		public Builder() {
			this.modeSetBuilder = new ImmutableSet.Builder<ModeOfInheritance>();
		}

		/**
		 * Add a {@link ModeOfInheritance} to the builder.
		 * 
		 * @param inheritanceMode
		 *            add this {@link ModeOfInheritance} to the builder.
		 * @return the new builder with the added mode of inheritance
		 */
		public Builder addMode(ModeOfInheritance inheritanceMode) {
			this.modeSetBuilder.add(inheritanceMode);
			return this;
		}

		/**
		 * Add a collection of {@link ModeOfInheritance} to the builder.
		 * 
		 * @param inheritanceModes
		 *            The collection you want to add
		 * @return the new builder with the added mode of inheritances
		 */
		public Builder addModes(Collection<ModeOfInheritance> inheritanceModes) {
			this.modeSetBuilder.addAll(inheritanceModes);
			return this;
		}

		/**
		 * Set the pedigree for the builder
		 * 
		 * @param pedigree
		 *            The pedigree which you want to use in your checker
		 * @return the new builder with the added pedigree
		 */
		public Builder pedigree(Pedigree pedigree) {
			this.pedigree = pedigree;
			return this;
		}

		/**
		 * Build the {@link InheritanceCompatibilityChecker}.
		 * 
		 * @return An {@link InheritanceCompatibilityChecker} with your given pedigree and modes of inheritances.
		 */
		public InheritanceCompatibilityChecker build() {
			return new InheritanceCompatibilityChecker(this.pedigree, modeSetBuilder.build());
		}
	}

	/** the pedigree */
	private final Pedigree pedigree;
	/** The modes of inheritances to check */
	private final ImmutableSet<ModeOfInheritance> inheritanceModes;

	/**
	 * @param pedigree
	 *            A pedigree
	 * @param inheritanceModes
	 *            the mode of inheritances
	 */
	private InheritanceCompatibilityChecker(Pedigree pedigree, ImmutableSet<ModeOfInheritance> inheritanceModes) {
		this.pedigree = pedigree;
		this.inheritanceModes = inheritanceModes;
	}

	/**
	 * @return The pedigree used in this {@link InheritanceCompatibilityChecker}
	 */
	public Pedigree getPedigree() {
		return pedigree;
	}

	/**
	 * @return mode of inheritances to check in this {@link InheritanceCompatibilityChecker}
	 */
	public ImmutableSet<ModeOfInheritance> getInheritanceModes() {
		return inheritanceModes;
	}

	/**
	 * Method for checking whether a {@link List} of {@link VariantContext} is compatible with a given
	 * {@link ModeOfInheritance} and {@link Pedigree}.
	 *
	 * @param vcList
	 *            {@link List} of {@link VariantContext} to check for compatibility
	 * @return A list with all {@link VariantContext} that matches the mode of inheritances.
	 * @throws InheritanceCompatibilityCheckerException
	 *             if there are problems with <code>list</code> or {@link #pedigree}.
	 */
	public List<VariantContext> getCompatibleWith(List<VariantContext> vcList)
			throws InheritanceCompatibilityCheckerException {

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
