package de.charite.compbio.jannovar.mendel.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.mendel.ChromosomeType;
import de.charite.compbio.jannovar.mendel.Genotype;
import de.charite.compbio.jannovar.mendel.GenotypeCalls;
import de.charite.compbio.jannovar.mendel.IncompatiblePedigreeException;
import de.charite.compbio.jannovar.mendel.MendelianInheritanceChecker;
import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Person;

// TODO: also return no-call/not-observed variant

/**
 * Helper class for checking a {@link Collection} of {@link GenotypeCalls} for compatibility with a {@link Pedigree} and
 * autosomal recessive compound het mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * In the case of a single individual, we require at least two heterozygous genotype calls.
 * 
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 */
public class MendelianCheckerARCompoundHet extends AbstractMendelianChecker {

	/** list of siblings for each person in {@link #pedigree} */
	private final ImmutableMap<Person, ImmutableList<Person>> siblings;

	public MendelianCheckerARCompoundHet(MendelianInheritanceChecker parent) {
		super(parent);

		this.siblings = queryDecorator.buildSiblings();
	}

	@Override
	public ImmutableList<GenotypeCalls> filterCompatibleRecords(Collection<GenotypeCalls> calls)
			throws IncompatiblePedigreeException {
		List<GenotypeCalls> autosomalCalls = calls.stream()
				.filter(call -> call.getChromType() == ChromosomeType.AUTOSOMAL).collect(Collectors.toList());
		if (pedigree.getNMembers() == 1)
			return filterCompatibleRecordsSingleSample(autosomalCalls);
		else
			return filterCompatibleRecordsMultiSample(autosomalCalls);
	}

	ImmutableList<GenotypeCalls> filterCompatibleRecordsSingleSample(Collection<GenotypeCalls> calls) {
		ImmutableList.Builder<GenotypeCalls> builder = new ImmutableList.Builder<>();
		for (GenotypeCalls gc : calls) {
			if (gc.getGenotypeBySampleNo(0).isHet())
				builder.add(gc);
		}

		ImmutableList<GenotypeCalls> result = builder.build();
		if (result.size() > 1)
			return result;
		else
			return ImmutableList.of();
	}

	private ImmutableList<GenotypeCalls> filterCompatibleRecordsMultiSample(Collection<GenotypeCalls> calls) {
		// First, collect candidate genotype call lists from trios around affected individuals
		ArrayList<Candidate> candidates = collectTrioCandidates(calls);

		// Then, check the candidates for all trios around affected individuals
		Set<GenotypeCalls> result = new HashSet<>();
		for (Candidate c : candidates) {
			if (isCompatibleWithTriosAroundAffected(c)) {
				// If candidate holds, check all unaffected for not being homozygous alt
				if (isCompatibleWithUnaffected(c)) {
					result.add(c.getMaternal());
					result.add(c.getPaternal());
				}
			}
		}
		return ImmutableList.copyOf(result);
	}

	private boolean isCompatibleWithUnaffected(Candidate c) {
		for (Person p : pedigree.getMembers()) {
			if (p.getDisease() == Disease.UNAFFECTED) {
				boolean patHet = false;
				boolean matHet = false;
				// None of the genotypes from the paternal or maternal call lists may be homozygous in the index
				if (c.getPaternal() != null) {
					final Genotype pGT = c.getPaternal().getGenotypeForSample(p.getName());
					if (pGT.isHomAlt())
						return false;
					if (pGT.isHet())
						patHet = true;
				}
				if (c.getMaternal() != null) {
					final Genotype mGT = c.getMaternal().getGenotypeForSample(p.getName());
					if (mGT.isHomAlt())
						return false;
					if (mGT.isHet())
						matHet = true;
				}

				// If mat and pat variant are heterozygous in an unaffected, check if they are on the same allele or not
				if (patHet && matHet) {
					if (c.getPaternal() != null && p.getFather() != null && c.getMaternal() != null
							&& p.getMother() != null) {
						final Genotype ppGT = c.getPaternal().getGenotypeForSample(p.getFather().getName());
						final Genotype mpGT = c.getPaternal().getGenotypeForSample(p.getMother().getName());
						final Genotype pmGT = c.getMaternal().getGenotypeForSample(p.getFather().getName());
						final Genotype mmGT = c.getMaternal().getGenotypeForSample(p.getMother().getName());
						// way one (paternal and maternal can now be switched around!
						if (ppGT.isHet() && mpGT.isHomRef() && pmGT.isHomRef() && mmGT.isHet())
							return false;
						if (ppGT.isHomRef() && mpGT.isHet() && pmGT.isHet() && mmGT.isHomRef())
							return false;
					}
				}
			}
		}

		return true;
	}

	private ArrayList<Candidate> collectTrioCandidates(Collection<GenotypeCalls> calls) {
		ArrayList<Candidate> result = new ArrayList<Candidate>();

		for (Person p : pedigree.getMembers()) {
			if (p.getDisease() == Disease.AFFECTED && (p.getFather() != null || p.getMother() != null)) {
				List<GenotypeCalls> paternal = new ArrayList<GenotypeCalls>();
				List<GenotypeCalls> maternal = new ArrayList<GenotypeCalls>();

				// Collect candidates towards the paternal side (heterozygous or not observed in child and father, not
				// hom_alt or het in mother)
				for (GenotypeCalls gc : calls) {
					final Genotype gtP = gc.getGenotypeForSample(p.getName());
					final Genotype gtF = (p.getFather() == null) ? null
							: gc.getGenotypeForSample(p.getFather().getName());
					final Genotype gtM = (p.getMother() == null) ? null
							: gc.getGenotypeForSample(p.getMother().getName());

					if ((gtP.isHet() || gtP.isNotObserved()) && (gtF == null || gtF.isHet() || gtF.isNotObserved())
							&& (gtM == null || gtM.isNotObserved() || gtM.isHomRef()))
						paternal.add(gc);
				}
				// Collect candidates towards the paternal side (heterozygous or not observed in child and mother. Not
				// hom_alt or het in father)
				for (GenotypeCalls gc : calls) {
					final Genotype gtP = gc.getGenotypeForSample(p.getName());
					final Genotype gtF = (p.getFather() == null) ? null
							: gc.getGenotypeForSample(p.getFather().getName());
					final Genotype gtM = (p.getMother() == null) ? null
							: gc.getGenotypeForSample(p.getMother().getName());

					if ((gtP.isHet() || gtP.isNotObserved()) && (gtM == null || gtM.isHet() || gtM.isNotObserved())
							&& (gtF == null || gtF.isNotObserved() || gtF.isHomRef()))
						maternal.add(gc);

					// Combine compatible paternal and maternal heterozygous variants
					for (GenotypeCalls pat : paternal)
						for (GenotypeCalls mat : maternal) {
							if (pat == mat) // FIXME what means this NOW?
								continue; // exclude if variants are identical

							if (pat.getGenotypeForSample(p.getName()).isNotObserved()
									&& (p.getFather() == null
											|| pat.getGenotypeForSample(p.getFather().getName()).isNotObserved())
									&& (p.getMother() == null
											|| pat.getGenotypeForSample(p.getMother().getName()).isNotObserved()))
								continue; // exclude if not observed in all from paternal
							if (mat.getGenotypeForSample(p.getName()).isNotObserved()
									&& (p.getFather() == null
											|| mat.getGenotypeForSample(p.getFather().getName()).isNotObserved())
									&& (p.getMother() == null
											|| mat.getGenotypeForSample(p.getMother().getName()).isNotObserved()))
								continue; // exclude if not observed in all from maternal
							result.add(new Candidate(pat, mat));
						}
				}
			}
		}

		return result;
	}

	private boolean isCompatibleWithTriosAroundAffected(Candidate c) {
		for (Person p : pedigree.getMembers()) {
			if (p.getDisease() == Disease.AFFECTED) {
				// We have to check this for paternal,maternal and vice versa. Paternal maternal inheritance can be
				// different for other parents in the pedigree.
				if (!isCompatibleWithTriosAndMaternalPaternalInheritanceAroundAffected(p, c.getPaternal(),
						c.getMaternal()))
					if (!isCompatibleWithTriosAndMaternalPaternalInheritanceAroundAffected(p, c.getMaternal(),
							c.getPaternal()))
						return false;
			}
		}

		return true;
	}

	private boolean isCompatibleWithTriosAndMaternalPaternalInheritanceAroundAffected(Person p, GenotypeCalls paternal,
			GenotypeCalls maternal) {
		// None of the genotypes from the paternal or maternal call lists may be homozygous in the index
		if (paternal != null) {
			final Genotype pGT = paternal.getGenotypeForSample(p.getName());
			if (pGT.isHomAlt() || pGT.isHomRef())
				return false;
		}
		if (maternal != null) {
			final Genotype mGT = maternal.getGenotypeForSample(p.getName());
			if (mGT.isHomAlt() || mGT.isHomRef())
				return false;
		}

		// The paternal variant may not be homozygous in the father of p, if any
		if (paternal != null && p.getFather() != null) {
			final Genotype pGT = paternal.getGenotypeForSample(p.getFather().getName());
			if (pGT.isHomAlt() || pGT.isHomRef())
				return false;
		}

		// The maternal variant may not be homozygous in the mother of p, if any
		if (maternal != null && p.getMother() != null) {
			final Genotype mGT = maternal.getGenotypeForSample(p.getMother().getName());
			if (mGT.isHomAlt() || mGT.isHomRef())
				return false;
		}

		// None of the unaffected siblings may have the same genotypes as p
		if (siblings != null && !siblings.isEmpty() && siblings.containsKey(p))
			for (Person sibling : siblings.get(p))
				if (sibling.getDisease() == Disease.UNAFFECTED) {
					final Genotype pGT = paternal.getGenotypeForSample(sibling.getName());
					final Genotype mGT = maternal.getGenotypeForSample(sibling.getName());
					if (pGT.isHet() && mGT.isHet())
						return false;
				}
		return true;
	}

}
