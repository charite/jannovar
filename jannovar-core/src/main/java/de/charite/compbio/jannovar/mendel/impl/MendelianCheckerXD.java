package de.charite.compbio.jannovar.mendel.impl;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.mendel.ChromosomeType;
import de.charite.compbio.jannovar.mendel.Genotype;
import de.charite.compbio.jannovar.mendel.GenotypeCalls;
import de.charite.compbio.jannovar.mendel.MendelianInheritanceChecker;
import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Person;
import de.charite.compbio.jannovar.pedigree.Sex;

/**
 * Implementation of Mendelian compatibility check for autosomal dominant case
 * 
 * <h2>Compatibility Check</h2>
 * 
 * For X-chromosomal dominant inheritance, there must be at least one {@link Genotype} that is shared by all affected
 * individuals but no unaffected individuals in the pedigree.
 * 
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class MendelianCheckerXD extends AbstractMendelianChecker {

	public MendelianCheckerXD(MendelianInheritanceChecker parent) {
		super(parent);
	}

	@Override
	public ImmutableList<GenotypeCalls> filterCompatibleRecords(Collection<GenotypeCalls> calls) {
		// Filter to calls on X chromosomes
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
	 * @return whether <code>calls</code> is compatible with AD inheritance in the case of a single individual in the
	 *         pedigree
	 */
	private boolean isCompatibleSingleton(GenotypeCalls calls) {
		if (calls.getNSamples() == 0)
			return false; // no calls!
		final Genotype gt = calls.getGenotypeBySampleNo(0);
		if (pedigree.getMembers().get(0).getSex() == Sex.FEMALE) {
			// Allow only heterozygous calls
			return calls.getGenotypeBySampleNo(0).isHet();
		} else {
			// We allow homozygous (actually hemizygous) and heterozygous (false call)
			return (gt.isHet() || gt.isHomAlt());
		}
	}

	/**
	 * @return whether <code>calls</code> is compatible with AD inheritance in the case of multiple individuals in the
	 *         pedigree
	 */
	private boolean isCompatibleFamily(GenotypeCalls calls) {
		int numAffectedWithVar = 0;

		for (Person p : pedigree.getMembers()) {
			final Sex sex = p.getSex();
			final Genotype gt = calls.getGenotypeForSample(p.getName());
			final Disease d = p.getDisease();

			if (d == Disease.AFFECTED) {
				if (gt.isHomRef() || (sex == Sex.FEMALE && gt.isHomAlt())) {
					// We do not allow hom. alternative for females to have the same behaviour as AD for females
					return false;
				} else if (sex == Sex.FEMALE && gt.isHet()) {
					numAffectedWithVar++;
				} else if (sex != Sex.FEMALE && (gt.isHet() || gt.isHomAlt())) {
					// We allow heterozygous here as well in the case of mis-calls in the one X copy in the male or
					// unknown
					numAffectedWithVar++;
				}
			} else if (d == Disease.UNAFFECTED) {
				if (gt.isHet() || gt.isHomAlt())
					return false; // unaffected must not have it!
			}
		}

		return (numAffectedWithVar > 0);
	}

}
