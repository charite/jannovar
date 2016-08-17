package de.charite.compbio.jannovar.pedigree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.charite.compbio.jannovar.mendel.ModeOfInheritance;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityChecker;
import htsjdk.variant.variantcontext.VariantContext;

/**
 *
 * This class is an extension of {@link htsjdk.variant.variantcontext.VariantContext} for the
 * {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityChecker}.
 *
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @version 0.15-SNAPSHOT
 */
public class InheritanceVariantContext extends VariantContext {

	/**
	 * Builder to transform a {@link List} of {@link VariantContext} into a {@link List} of
	 * {@link InheritanceVariantContext}.
	 * 
	 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
	 * @version 0.15-SNAPSHOT
	 */
	static final class ListBuilder {

		/**
		 * The final list.
		 */
		private List<InheritanceVariantContext> list = new ArrayList<InheritanceVariantContext>();

		/**
		 * @param vcList
		 *            A {@link List} of {@link VariantContext} that should be transformed.
		 * @return The {@link ListBuilder} to continue with {@link #build()}.
		 */
		public ListBuilder variants(List<VariantContext> vcList) {
			for (VariantContext vc : vcList) {
				list.add(new InheritanceVariantContext(vc));
			}

			return this;
		}

		/**
		 * Build the list.
		 * 
		 * @return the transformed list.
		 */
		public List<InheritanceVariantContext> build() {
			return list;
		}

	}

	/**
	 * Default ID for serialization
	 */
	private static final long serialVersionUID = 6381923752159274326L;

	/**
	 * Set of stored possible modes of inheritances. The set is build by the the {@link InheritanceCompatibilityChecker}
	 * .
	 */
	private Set<ModeOfInheritance> matchedModesOfInheritance;

	/**
	 * Default constructor. Copies the given {@link htsjdk.variant.variantcontext.VariantContext} and sets the
	 * {@link #matchedModesOfInheritance} to <code>false</code>.
	 *
	 * @param other
	 *            Other {@link htsjdk.variant.variantcontext.VariantContext} to copy.
	 */
	protected InheritanceVariantContext(VariantContext other) {
		super(other);
	}

	/**
	 * <p>
	 * Add a mode of inheritance to this variant.
	 * </p>
	 *
	 * @param matchInheritance
	 *            A mode of inheritance that matches to this variant.
	 */
	public void addMatchInheritance(ModeOfInheritance matchInheritance) {
		getMatchedModesOfInheritance().add(matchInheritance);
	}

	/**
	 * Single {@link de.charite.compbio.jannovar.pedigree.Person} {@link de.charite.compbio.jannovar.pedigree.Genotype}
	 * getter. Used for the Single mode of the
	 * {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityChecker}.
	 *
	 * @return the first {@link htsjdk.variant.variantcontext.Genotype} stored in the
	 *         {@link htsjdk.variant.variantcontext.VariantContext}
	 */
	public Genotype getSingleSampleGenotype() {
		return getGenotype(getGenotype(0));
	}

	/**
	 * getter of {@link #matchedModesOfInheritance}.
	 * 
	 * @return A {@link Set} with {@link de.charite.compbio.jannovar.mendel.ModeOfInheritance} that matches with this
	 *         variant.
	 */
	public Set<ModeOfInheritance> getMatchedModesOfInheritance() {
		if (matchedModesOfInheritance == null)
			this.matchedModesOfInheritance = new HashSet<ModeOfInheritance>();
		return matchedModesOfInheritance;
	}

	/**
	 * Getter of {@link #matchedModesOfInheritance}.
	 *
	 * @return <code>true</code> if the {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContext} matches
	 *         the {@link de.charite.compbio.jannovar.mendel.ModeOfInheritance}.
	 */
	public boolean isMatchInheritance() {
		return getMatchedModesOfInheritance().size() > 0;
	}

	/**
	 * Transforms a HTSJDK {@link htsjdk.variant.variantcontext.Genotype} into an Jannovar {@link Genotype}.
	 * 
	 * Note that this code is not optimal. It makes false decissions if we have multiple alternative alleles. For
	 * example A*,G,T: genotype is G/T => {@value Genotype#HETEROZYGOUS} (always correct), genotype is G/G =>
	 * {@value Genotype#HOMOZYGOUS_ALT} (correct or false). But the combination of both (child + mother) we maybe have
	 * to consider G/G as {@value Genotype#HOMOZYGOUS_REF}...
	 * 
	 * @param g
	 *            HTSJDK {@link htsjdk.variant.variantcontext.Genotype} to transform
	 * @return The transformed HTSJDK genotype into a Jannovar {@link Genotype}.
	 */
	private Genotype getGenotype(htsjdk.variant.variantcontext.Genotype g) {
		// TODO(mschubach) What about multi allelic spots...
		if (g.isHet() || g.isHetNonRef())
			return Genotype.HETEROZYGOUS;
		else if (g.isHomRef())
			return Genotype.HOMOZYGOUS_REF;
		else if (g.isHomVar())
			return Genotype.HOMOZYGOUS_ALT;
		else
			return Genotype.NOT_OBSERVED;
	}

	/**
	 * Gets the Jannovar {@link de.charite.compbio.jannovar.pedigree.Genotype} for a
	 * {@link de.charite.compbio.jannovar.pedigree.Person} (name must be the same than in the VCF file)
	 *
	 * @param p
	 *            A {@link de.charite.compbio.jannovar.pedigree.Person} in the
	 *            {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContext}
	 * @return The {@link de.charite.compbio.jannovar.pedigree.Genotype} of the
	 *         {@link de.charite.compbio.jannovar.pedigree.Person}.
	 */
	public Genotype getGenotype(Person p) {
		return getGenotype(getGenotype(p.getName()));
	}

}
