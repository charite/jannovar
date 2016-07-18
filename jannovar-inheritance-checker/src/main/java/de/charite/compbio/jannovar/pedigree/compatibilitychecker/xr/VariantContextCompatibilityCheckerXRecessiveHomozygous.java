package de.charite.compbio.jannovar.pedigree.compatibilitychecker.xr;

import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.mendel.ModeOfInheritance;
import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.InheritanceVariantContext;
import de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.PedigreeQueryDecorator;
import de.charite.compbio.jannovar.pedigree.Person;
import de.charite.compbio.jannovar.pedigree.Sex;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.AbstractVariantContextCompatibilityChecker;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;

/**
 * Helper class for checking a {@link InheritanceVariantContextList} for compatibility with a {@link Pedigree} and
 * autosomal recessive homozygous mode.
 *
 * <h2>Compatibility Check</h2>
 *
 * In the case of a single individual, we require {@link Genotype#HOMOZYGOUS_ALT}.
 *
 * In the case of multiple individuals, we require that the affects are compatible, that the unaffected parents of
 * affected individuals are not {@link Genotype#HOMOZYGOUS_ALT}, unaffected females are not are not
 * {@link Genotype#HOMOZYGOUS_REF}, and that all unaffected individuals are not {@link Genotype#HOMOZYGOUS_ALT}. The
 * affected individuals are compatible if no affected individual is {@link Genotype#HOMOZYGOUS_REF} or
 * {@link Genotype#HETEROZYGOUS} and there is at least one affected individual that is {@link Genotype#HOMOZYGOUS_ALT}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @version 0.15-SNAPSHOT
 */
class VariantContextCompatibilityCheckerXRecessiveHomozygous extends AbstractVariantContextCompatibilityChecker {

	/**
	 * decorator for getting unaffected individuals and such from the {@link Pedigree}
	 */
	protected final PedigreeQueryDecorator queryDecorator;

	/**
	 * Initialize compatibility checker for X recessive homozygous and perform some sanity checks.
	 *
	 * @see AbstractVariantContextCompatibilityChecker#AbstractVariantContextCompatibilityChecker(Pedigree,
	 *      InheritanceVariantContextList)
	 * @param pedigree
	 *            the {@link de.charite.compbio.jannovar.pedigree.Pedigree} to use for the initialize
	 * @param list
	 *            the {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} to use for the
	 *            initialization
	 * @throws de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public VariantContextCompatibilityCheckerXRecessiveHomozygous(Pedigree pedigree, InheritanceVariantContextList list)
			throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);

		this.queryDecorator = new PedigreeQueryDecorator(pedigree);
	}
	
	public void runSingleSampleCase() {
		// for both male and female subjects, return true if homozygous alt
		for (InheritanceVariantContext vc : list.getVcList())
			if (vc.getSingleSampleGenotype() == Genotype.HOMOZYGOUS_ALT)
				vc.addMatchInheritance(ModeOfInheritance.X_RECESSIVE);
			else if (pedigree.getMembers().get(0).getSex() != Sex.FEMALE
					&& vc.getSingleSampleGenotype() == Genotype.HETEROZYGOUS)
				vc.addMatchInheritance(ModeOfInheritance.X_RECESSIVE);

	}

	public void runMultiSampleCase() {
		for (InheritanceVariantContext vc : list.getVcList()) {
			// Check whether this list of genotype calls is compatible when with
			// the set of affected individuals, the
			// parents, and the unaffected individuals.
			if (checkCompatibilityAffected(vc) && checkCompatibilityParents(vc) && checkCompatibilityUnaffected(vc))
				vc.addMatchInheritance(ModeOfInheritance.X_RECESSIVE);
		}
	}

	private boolean checkCompatibilityAffected(InheritanceVariantContext vc) {
		int numMut = 0;
		for (Person p : pedigree.getMembers()) {
			if (p.getDisease() == Disease.AFFECTED) {
				if (vc.getGenotype(p) == Genotype.HOMOZYGOUS_REF)
					/**
					 * acnnot be disease-causing mutation, an affected male or female does not have it.
					 */
					return false;
				else if (p.getSex() == Sex.FEMALE && vc.getGenotype(p) == Genotype.HETEROZYGOUS)
					/**
					 * cannot be disease-causing mutation if a female have it heterozygous. For a male we think it is a
					 * misscall (alt instead of het)
					 */
					return false;
				else if (vc.getGenotype(p) == Genotype.HOMOZYGOUS_ALT
						|| (p.getSex() != Sex.FEMALE && vc.getGenotype(p) == Genotype.HETEROZYGOUS))
					numMut += 1;
			}
		}

		return (numMut > 0);
	}

	/**
	 * For XR the parents of male and female behaves different. The father of a Female individual must always be
	 * affected. If the sex is unknown to check is made!
	 * 
	 * @param gtList
	 * @return
	 */
	private boolean checkCompatibilityParents(InheritanceVariantContext vc) {
		final ImmutableSet<String> femaleParentNames = queryDecorator.getAffectedFemaleParentNames();
		final ImmutableSet<String> maleParentNames = queryDecorator.getAffectedFemaleParentNames();
		for (Person p : pedigree.getMembers()) {
			final Genotype gt = vc.getGenotype(p);
			if (femaleParentNames.contains(p.getName())) {
				if (p.getSex() == Sex.MALE && p.getDisease() == Disease.UNAFFECTED)
					return false; // must always be affected. If affected it is
									// already checked!
				if (p.getSex() == Sex.FEMALE && (gt == Genotype.HOMOZYGOUS_ALT || gt == Genotype.HOMOZYGOUS_REF))
					return false; // cannot be disease-causing mutation if
									// mother of patient is homozygous or not
									// the carrier
			} else if (maleParentNames.contains(p.getName())) {
				if (p.getSex() == Sex.MALE && p.getDisease() == Disease.UNAFFECTED
						&& (gt == Genotype.HOMOZYGOUS_ALT || gt != Genotype.HETEROZYGOUS))
					return false; // unaffected male can not me heterozygos
									// (wrong call) or hemizygous
				if (p.getSex() == Sex.FEMALE && gt == Genotype.HOMOZYGOUS_ALT)
					return false; // cannot be disease-causing mutation if
									// mother of patient is homozygous
			}
		}

		return true;
	}

	private boolean checkCompatibilityUnaffected(InheritanceVariantContext vc) {
		final ImmutableSet<String> unaffectedNames = queryDecorator.getUnaffectedNames();
		for (Person p : pedigree.getMembers()) {
			if (unaffectedNames.contains(p.getName())) {
				final Genotype gt = vc.getGenotype(p);
				// Strict handling. Males cannot be called heterozygous (will be
				// seen as a homozygous mutation)
				if (p.isMale() && (gt == Genotype.HETEROZYGOUS || gt == Genotype.HOMOZYGOUS_ALT))
					return false;
				else if (gt == Genotype.HOMOZYGOUS_ALT)
					return false; // cannot be disease-causing mutation (female
									// or unknown)
			}
		}

		return true;
	}

}
