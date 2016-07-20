package de.charite.compbio.jannovar.mendel.impl;

import java.util.Collection;
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

/**
 * Helper class for checking a {@link Collection} of {@link GenotypeCalls} for compatibility with a {@link Pedigree} and
 * autosomal recessive homozygous mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * In the case of a single individual, we require an homozygous alternative call.
 *
 * In the case of multiple individuals, we require that the affects are compatible, that the unaffected parents of
 * affected individuals are not homozygous ref or homozygous alt and that the unaffected individuals are not homozygous
 * alt. The affected individuals are compatible if no affected individual is homozygous ref or heterozygous and there is
 * at least one affected individual that is homozygous alt.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 */
public class MendelianCheckerARHom extends AbstractMendelianChecker {

	public MendelianCheckerARHom(MendelianInheritanceChecker parent) {
		super(parent);
	}

	@Override
	public ImmutableList<GenotypeCalls> filterCompatibleRecords(Collection<GenotypeCalls> calls)
			throws IncompatiblePedigreeException {
		// Filter to calls on autosomal chromosomes
		Stream<GenotypeCalls> autosomalCalls = calls.stream()
				.filter(call -> call.getChromType() == ChromosomeType.AUTOSOMAL);
		// Filter to calls compatible with AD inheritance
		Stream<GenotypeCalls> compatibleCalls;
		if (this.pedigree.getNMembers() == 1)
			compatibleCalls = autosomalCalls.filter(this::isCompatibleSingleton);
		else
			compatibleCalls = autosomalCalls.filter(this::isCompatibleFamily);
		return ImmutableList.copyOf(compatibleCalls.collect(Collectors.toList()));
	}

	/**
	 * @return whether <code>calls</code> is compatible with AR homozygous inheritance in the case of a single
	 *         individual in the pedigree
	 */
	private boolean isCompatibleSingleton(GenotypeCalls calls) {
		if (calls.getNSamples() == 0)
			return false; // no calls!
		return calls.getGenotypeBySampleNo(0).isHomAlt();
	}

	/**
	 * @return whether <code>calls</code> is compatible with AR homozygous inheritance in the case of multiple
	 *         individuals in the pedigree
	 */
	private boolean isCompatibleFamily(GenotypeCalls calls) {
		return (affectedsAreCompatible(calls) && unaffectedParentsOfAffectedAreNotHomozygous(calls)
				&& unaffectedsAreNotHomozygousAlt(calls));
	}

	private boolean affectedsAreCompatible(GenotypeCalls calls) {
		int numHomozygousAlt = 0;

		for (Pedigree.IndexedPerson entry : pedigree.getNameToMember().values()) {
			if (entry.getPerson().getDisease() == Disease.AFFECTED) {
				final Genotype gt = calls.getGenotypeForSample(entry.getPerson().getName());
				if (gt.isHomRef() || gt.isHet())
					return false;
				else if (gt.isHomAlt())
					numHomozygousAlt += 1;
			}
		}

		return (numHomozygousAlt > 0);
	}

	private boolean unaffectedParentsOfAffectedAreNotHomozygous(GenotypeCalls calls) {
		for (String name : getUnaffectedParentNamesOfAffecteds()) {
			final Genotype gt = calls.getGenotypeForSample(name);
			if (gt.isHomAlt() || gt.isHomRef())
				return false;
		}
		return true;
	}

	/**
	 * @return names of unaffected parents of unaffecteds
	 */
	private ImmutableSet<String> getUnaffectedParentNamesOfAffecteds() {
		ImmutableSet.Builder<String> builder = new ImmutableSet.Builder<String>();

		for (Person person : pedigree.getMembers())
			if (person.getDisease() == Disease.AFFECTED) {
				if (person.getFather() != null && person.getFather().getDisease() == Disease.UNAFFECTED)
					builder.add(person.getFather().getName());
				if (person.getMother() != null && person.getMother().getDisease() == Disease.UNAFFECTED)
					builder.add(person.getMother().getName());
			}

		return builder.build();
	}

	private boolean unaffectedsAreNotHomozygousAlt(GenotypeCalls calls) {
		for (Pedigree.IndexedPerson entry : pedigree.getNameToMember().values())
			if (entry.getPerson().getDisease() == Disease.UNAFFECTED
					&& calls.getGenotypeForSample(entry.getPerson().getName()).isHomAlt())
				return false;
		return true;
	}

}
