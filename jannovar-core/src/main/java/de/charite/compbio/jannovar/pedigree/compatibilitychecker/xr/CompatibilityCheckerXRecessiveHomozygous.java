package de.charite.compbio.jannovar.pedigree.compatibilitychecker.xr;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.GenotypeList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.PedigreeQueryDecorator;
import de.charite.compbio.jannovar.pedigree.Person;
import de.charite.compbio.jannovar.pedigree.Sex;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerBase;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and autosomal recessive
 * homozygous mode.
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
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 * 
 * @deprecated use {@link VariantContextCompatibilityCheckerXRecessiveHomozygous} instead.
 */
@Deprecated
class CompatibilityCheckerXRecessiveHomozygous extends CompatibilityCheckerBase {

	/**
	 * decorator for getting unaffected individuals and such from the {@link Pedigree}
	 */
	protected final PedigreeQueryDecorator queryDecorator;

	/**
	 * Initialize compatibility checker and perform some sanity checks.
	 *
	 * The {@link GenotypeList} object passed to the constructor is expected to represent all of the variants found in a
	 * certain gene (possibly after filtering for rarity or predicted pathogenicity). The samples represented by the
	 * {@link GenotypeList} must be in the same order as the list of individuals contained in this pedigree.
	 *
	 * @param pedigree
	 *            the {@link Pedigree} to use for the initialize
	 * @param list
	 *            the {@link GenotypeList} to use for the initialization
	 * @throws CompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public CompatibilityCheckerXRecessiveHomozygous(Pedigree pedigree, GenotypeList list)
			throws CompatibilityCheckerException {
		super(pedigree, list);

		this.queryDecorator = new PedigreeQueryDecorator(pedigree);
	}

	public boolean runSingleSampleCase() {
		// for both male and female subjects, return true if homozygous alt
		for (ImmutableList<Genotype> gtList : list.getCalls())
			if (gtList.get(0) == Genotype.HOMOZYGOUS_ALT)
				return true;
			else if (pedigree.getMembers().get(0).getSex() != Sex.FEMALE && gtList.get(0) == Genotype.HETEROZYGOUS)
				return true;

		return false;
	}

	public boolean runMultiSampleCase() {
		for (ImmutableList<Genotype> gtList : list.getCalls()) {
			// Check whether this list of genotype calls is compatible when with
			// the set of affected individuals, the
			// parents, and the unaffected individuals.
			if (checkCompatibilityAffected(gtList) && checkCompatibilityParents(gtList)
					&& checkCompatibilityUnaffected(gtList))
				return true;
		}

		return false;
	}

	private boolean checkCompatibilityAffected(ImmutableList<Genotype> gtList) {
		int numMut = 0;
		int i = 0;
		for (Person person : pedigree.getMembers()) {
			if (person.getDisease() == Disease.AFFECTED) {
				if (gtList.get(i) == Genotype.HOMOZYGOUS_REF)
					/**
					 * acnnot be disease-causing mutation, an affected male or female does not have it.
					 */
					return false;
				else if (person.getSex() == Sex.FEMALE && gtList.get(i) == Genotype.HETEROZYGOUS)
					/**
					 * cannot be disease-causing mutation if a female have it heterozygous. For a male we think it is a
					 * misscall (alt instead of het)
					 */
					return false;
				else if (gtList.get(i) == Genotype.HOMOZYGOUS_ALT
						|| (person.getSex() != Sex.FEMALE && gtList.get(i) == Genotype.HETEROZYGOUS))
					numMut += 1;
			}
			++i;
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
	private boolean checkCompatibilityParents(ImmutableList<Genotype> gtList) {
		final ImmutableSet<String> femaleParentNames = queryDecorator.getAffectedFemaleParentNames();
		final ImmutableSet<String> maleParentNames = queryDecorator.getAffectedFemaleParentNames();
		int i = 0;
		for (Person person : pedigree.getMembers()) {
			final Genotype gt = gtList.get(i);
			if (femaleParentNames.contains(person.getName())) {
				if (person.getSex() == Sex.MALE && person.getDisease() == Disease.UNAFFECTED)
					return false; // must always be affected. If affected it is
									// already checked!
				if (person.getSex() == Sex.FEMALE && (gt == Genotype.HOMOZYGOUS_ALT || gt == Genotype.HOMOZYGOUS_REF))
					return false; // cannot be disease-causing mutation if
									// mother of patient is homozygous or not
									// the carrier
			} else if (maleParentNames.contains(person.getName())) {
				if (person.getSex() == Sex.MALE && person.getDisease() == Disease.UNAFFECTED
						&& (gt == Genotype.HOMOZYGOUS_ALT || gt != Genotype.HETEROZYGOUS))
					return false; // unaffected male can not me heterozygos
									// (wrong call) or hemizygous
				if (person.getSex() == Sex.FEMALE && gt == Genotype.HOMOZYGOUS_ALT)
					return false; // cannot be disease-causing mutation if
									// mother of patient is homozygous
			}
			++i;
		}

		return true;
	}

	private boolean checkCompatibilityUnaffected(ImmutableList<Genotype> gtList) {
		final ImmutableSet<String> unaffectedNames = queryDecorator.getUnaffectedNames();
		int i = 0;
		for (Person person : pedigree.getMembers()) {
			if (unaffectedNames.contains(person.getName())) {
				final Genotype gt = gtList.get(i);
				// Strict handling. Males cannot be called heterozygous (will be
				// seen as a homozygous mutation)
				if (person.isMale() && (gt == Genotype.HETEROZYGOUS || gt == Genotype.HOMOZYGOUS_ALT))
					return false;
				else if (gt == Genotype.HOMOZYGOUS_ALT)
					return false; // cannot be disease-causing mutation (female
									// or unknown)
			}
			++i;
		}

		return true;
	}

}
