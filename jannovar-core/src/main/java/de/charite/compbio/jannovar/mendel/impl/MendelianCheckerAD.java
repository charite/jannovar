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

/**
 * Implementation of Mendelian compatibility check for autosomal dominant case
 * 
 * <h2>Compatibility Check</h2>
 * 
 * For autosomal dominant inheritance there must be at least one {@link Genotype} that is shared by all affected
 * individuals but no unaffected individuals in the pedigree. We do not allow homozygous alternative for any affected
 * individuals (and also for the one person in these of singleton pedigrees since this is not the interesting case for
 * users of this class.
 * 
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class MendelianCheckerAD extends AbstractMendelianChecker {

	public MendelianCheckerAD(MendelianInheritanceChecker parent) {
		super(parent);
	}

	@Override
	public ImmutableList<GenotypeCalls> filterCompatibleRecords(Collection<GenotypeCalls> calls) {
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
	 * @return whether <code>calls</code> is compatible with AD inheritance in the case of a single individual in the
	 *         pedigree
	 */
	private boolean isCompatibleSingleton(GenotypeCalls calls) {
		if (calls.getNSamples() == 0)
			return false; // no calls!
		return calls.getGenotypeBySampleNo(0).isHet();
	}

	/**
	 * @return whether <code>calls</code> is compatible with AD inheritance in the case of multiple individuals in the
	 *         pedigree
	 */
	private boolean isCompatibleFamily(GenotypeCalls calls) {
		int numAffectedWithHet = 0;
		
		for (Person p : pedigree.getMembers()) {
			final Genotype gt = calls.getGenotypeForSample(p.getName());
			final Disease d = p.getDisease();
			
			if (d == Disease.AFFECTED) {
				if (gt.isHomRef() || gt.isHomAlt())
					return false;
				else if (gt.isHet())
					numAffectedWithHet++;
			} else if (d == Disease.UNAFFECTED) {
				if (gt.isHet() || gt.isHomAlt())
					return false;
			}
		}

		return (numAffectedWithHet > 0);
	}

}
