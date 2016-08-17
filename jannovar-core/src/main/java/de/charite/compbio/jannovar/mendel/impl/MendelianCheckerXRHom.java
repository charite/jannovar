package de.charite.compbio.jannovar.mendel.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.mendel.ChromosomeType;
import de.charite.compbio.jannovar.mendel.Genotype;
import de.charite.compbio.jannovar.mendel.GenotypeCalls;
import de.charite.compbio.jannovar.mendel.IncompatiblePedigreeException;
import de.charite.compbio.jannovar.mendel.MendelianInheritanceChecker;
import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Person;
import de.charite.compbio.jannovar.pedigree.Sex;

/**
 * Helper class for checking a {@link GenotypeCalls} for compatibility with a {@link Pedigree} and AR homozygous mode
 *
 * <h2>Compatibility Check</h2>
 *
 * In the case of a single individual, we require hom. alt.
 *
 * In the case of multiple individuals, we require that the affects are compatible, that the unaffected parents of hom.
 * alt. unaffected females are not are not hom. ref., and that all unaffected individuals are not hom. alt. The affected
 * individuals are compatible if no affected individual is hom. ref. or het. and there is at least one affected
 * individual that is hom. alt.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @version 0.15-SNAPSHOT
 */
public class MendelianCheckerXRHom extends AbstractMendelianChecker {

	public MendelianCheckerXRHom(MendelianInheritanceChecker parent) {
		super(parent);
	}

	@Override
	public ImmutableList<GenotypeCalls> filterCompatibleRecords(Collection<GenotypeCalls> calls)
			throws IncompatiblePedigreeException {
		// Filter to calls on X chromosome
		Stream<GenotypeCalls> xCalls = calls.stream()
				.filter(call -> call.getChromType() == ChromosomeType.X_CHROMOSOMAL);

		// Filter to calls compatible with AD inheritance
		Stream<GenotypeCalls> compatibleCalls;
		if (this.pedigree.getNMembers() == 1)
			compatibleCalls = xCalls.filter(this::isCompatibleSingleton);
		else
			compatibleCalls = xCalls.filter(this::isCompatibleFamily);
		return ImmutableList.copyOf(compatibleCalls.collect(Collectors.toList()));
	}

	/**
	 * @return whether <code>calls</code> is compatible with AR homozygous inheritance in the case of a single
	 *         individual in the pedigree
	 */
	private boolean isCompatibleSingleton(GenotypeCalls calls) {
		if (calls.getNSamples() == 0)
			return false; // no calls!
		if (calls.getGenotypeBySampleNo(0).isHomAlt())
			return true;
		else if (pedigree.getMembers().get(0).getSex() != Sex.FEMALE && calls.getGenotypeBySampleNo(0).isHet())
			return true;
		else
			return false;
	}

	/**
	 * @return whether <code>calls</code> is compatible with AR homozygous inheritance in the case of multiple
	 *         individuals in the pedigree
	 */
	private boolean isCompatibleFamily(GenotypeCalls calls) {
		return (affectedsAreCompatible(calls) && parentsAreCompatible(calls) && unaffectedsAreCompatible(calls));
	}

	private boolean affectedsAreCompatible(GenotypeCalls calls) {
		int numVar = 0;

		for (Person p : pedigree.getMembers()) {
			final String name = p.getName();
			final Genotype gt = calls.getGenotypeForSample(name);
			if (p.getDisease() == Disease.AFFECTED) {
				if (gt.isHomRef()) {
					// Cannot be disease-causing mutation, an affected male or female does not have it
					return false;
				} else if (p.getSex() == Sex.FEMALE && gt.isHet()) {
					// Cannot be disease-causing mutation if a female have it heterozygous. For a male we think it is a
					// misscall (alt instead of het)
					return false;
				} else if (gt.isHomAlt() || (p.getSex() != Sex.FEMALE && gt.isHet())) {
					numVar += 1;
				}
			}
		}

		return (numVar > 0);
	}

	private boolean parentsAreCompatible(GenotypeCalls calls) {
		final ImmutableSet<String> femaleParentNames = queryDecorator.getAffectedFemaleParentNames();
		final ImmutableSet<String> maleParentNames = queryDecorator.getAffectedFemaleParentNames();

		for (Person p : pedigree.getMembers()) {
			final Genotype gt = calls.getGenotypeForSample(p.getName());
			if (femaleParentNames.contains(p.getName())) {
				if (p.getSex() == Sex.MALE && p.getDisease() == Disease.UNAFFECTED) {
					// Must always be affected. If affected it is already checked!
					return false;
				}
				if (p.getSex() == Sex.FEMALE && (gt.isHomAlt() || gt.isHomRef())) {
					// Cannot be disease-causing mutation if mother of patient is homozygous or not the carrier
					return false;
				}
			} else if (maleParentNames.contains(p.getName())) {
				if (p.getSex() == Sex.MALE && p.getDisease() == Disease.UNAFFECTED && (gt.isHomAlt() || gt.isHet())) {
					// Unaffected male can not me heterozygos (wrong call) or hemizygous
					return false;
				}
				if (p.getSex() == Sex.FEMALE && gt.isHomAlt()) {
					// Cannot be disease-causing mutation if mother of patient is homozygous
					return false;
				}
			}
		}

		return true;
	}

	private boolean unaffectedsAreCompatible(GenotypeCalls calls) {
		final ImmutableSet<String> unaffectedNames = queryDecorator.getUnaffectedNames();

		for (Person p : pedigree.getMembers()) {
			if (unaffectedNames.contains(p.getName())) {
				final Genotype gt = calls.getGenotypeForSample(p.getName());
				// Strict handling. Males cannot be called heterozygous (will be seen as a homozygous mutation)
				if (p.isMale() && (gt.isHet() || gt.isHomAlt()))
					return false;
				else if (gt.isHomAlt())
					return false; // cannot be disease-causing mutation (female or unknown)
			}
		}

		return true;
	}

}
