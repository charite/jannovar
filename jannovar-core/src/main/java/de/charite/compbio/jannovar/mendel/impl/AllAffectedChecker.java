package de.charite.compbio.jannovar.mendel.impl;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.mendel.Genotype;
import de.charite.compbio.jannovar.mendel.GenotypeCalls;
import de.charite.compbio.jannovar.mendel.IncompatiblePedigreeException;
import de.charite.compbio.jannovar.mendel.MendelianInheritanceChecker;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Person;

/**
 * Check that the variant is carried by all affected individuals
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class AllAffectedChecker extends AbstractMendelianChecker {

	public AllAffectedChecker(MendelianInheritanceChecker parent) {
		super(parent);
	}

	@Override
	public ImmutableList<GenotypeCalls> filterCompatibleRecords(Collection<GenotypeCalls> calls)
			throws IncompatiblePedigreeException {
		return ImmutableList.copyOf(calls.stream().filter(this::isInAllAffected).collect(Collectors.toList()));
	}

	private boolean isInAllAffected(GenotypeCalls calls) {
		final Pedigree pedigree = parent.getPedigree();
		for (Person p : pedigree.getMembers()) {
			final Genotype gt = calls.getGenotypeForSample(p.getName());
			if (gt == null || gt.isHomRef())
				return false; // no call for affected
		}
		return true;
	}

}
