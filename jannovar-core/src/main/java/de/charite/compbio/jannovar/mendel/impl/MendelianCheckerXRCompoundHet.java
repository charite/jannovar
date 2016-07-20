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
import de.charite.compbio.jannovar.pedigree.Sex;

// TODO: also return no-call/not-observed variant

/**
 * Helper class for checking a {@link GenotypeCalls} for compatibility with a {@link Pedigree} and X recessive compound
 * het. mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * In the case of a single individual, we require at least two het. genotype calls.
 *
 * In the case of multiple individuals, we require that the affects are compatible, that the parents of affected
 * individuals. Be careful, that the father must be {@link Disease#AFFECTED}. Therefore the father should be
 * {@link Disease#AFFECTED} and hom. alt.. We will allow het. because of miscalls.
 * 
 * Unaffected mothers are not are not hom. ref. for one allele, and that all unaffected individuals are not hom. alt.
 * and should not have it comp. het (only possible if parents are available).
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 */
public class MendelianCheckerXRCompoundHet extends AbstractMendelianChecker {

	/** list of siblings for each person in {@link #pedigree} */
	private final ImmutableMap<Person, ImmutableList<Person>> siblings;

	public MendelianCheckerXRCompoundHet(MendelianInheritanceChecker parent) {
		super(parent);

		this.siblings = queryDecorator.buildSiblings();
	}

	@Override
	public ImmutableList<GenotypeCalls> filterCompatibleRecords(Collection<GenotypeCalls> calls)
			throws IncompatiblePedigreeException {
		List<GenotypeCalls> xCalls = calls.stream().filter(call -> call.getChromType() == ChromosomeType.X_CHROMOSOMAL)
				.collect(Collectors.toList());

		if (pedigree.getNMembers() == 1)
			return filterCompatibleRecordsSingleSample(xCalls);
		else
			return filterCompatibleRecordsMultiSample(xCalls);
	}

	private ImmutableList<GenotypeCalls> filterCompatibleRecordsSingleSample(Collection<GenotypeCalls> calls) {
		if (pedigree.getMembers().get(0).getSex() == Sex.MALE)
			return ImmutableList.of();
		else
			return new MendelianCheckerARCompoundHet(parent).filterCompatibleRecordsSingleSample(calls);
	}

	private ImmutableList<GenotypeCalls> filterCompatibleRecordsMultiSample(Collection<GenotypeCalls> calls) {
		List<GenotypeCalls> autosomalCalls = calls.stream()
				.filter(call -> call.getChromType() == ChromosomeType.AUTOSOMAL).collect(Collectors.toList());

		// First, collect candidate genotype call lists from trios around affected individuals
		ArrayList<Candidate> candidates = collectTrioCandidates(autosomalCalls);

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

	private ArrayList<Candidate> collectTrioCandidates(Collection<GenotypeCalls> calls) {
		ArrayList<Candidate> result = new ArrayList<Candidate>();

		for (Person p : pedigree.getMembers()) {
			if (p.getDisease() == Disease.AFFECTED && (p.getFather() != null || p.getMother() != null)) {
				List<GenotypeCalls> paternal = new ArrayList<GenotypeCalls>();
				List<GenotypeCalls> maternal = new ArrayList<GenotypeCalls>();

				for (GenotypeCalls gc : calls) {
					// Child is heterozygous. male child/ukn can be homozygous
					if ((gc.getGenotypeForSample(p.getName()).isHet()
							|| gc.getGenotypeForSample(p.getName()).isNotObserved()
							|| (p.getSex() != Sex.FEMALE && gc.getGenotypeForSample(p.getName()).isHomAlt()))) {
						// collect candidates towards the paternal side
						// (heterozygous (false call in father) or not observed
						// in child and father. Not hom_alt or het in mother).
						if ((p.getFather() == null || gc.getGenotypeForSample(p.getFather().getName()).isHet()
								|| gc.getGenotypeForSample(p.getFather().getName()).isNotObserved()
								|| gc.getGenotypeForSample(p.getFather().getName()).isHomAlt())
								&& (p.getMother() == null
										|| gc.getGenotypeForSample(p.getMother().getName()).isNotObserved()
										|| gc.getGenotypeForSample(p.getMother().getName()).isHomRef()))
							paternal.add(gc);
						// collect candidates towards the maternal side
						// (heterozygous or not observed in child and mother.
						// For father no restriction, cause father should be affected if present.
						if ((p.getMother() == null || gc.getGenotypeForSample(p.getMother().getName()).isHet()
								|| gc.getGenotypeForSample(p.getMother().getName()).isNotObserved()))
							maternal.add(gc);
					}
				}

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

		return result;
	}

	private boolean isCompatibleWithTriosAroundAffected(Candidate c) {
		for (Person p : pedigree.getMembers()) {
			if (p.getDisease() == Disease.AFFECTED) {
				// we have to check this for paternal,maternal and vice versa.
				// Paternal maternal inheritance can be different for other
				// parents in the pedigree.
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
		// None of the genotypes from the paternal or maternal call lists may be homozygous in a female index. can be
		// homozygous else
		if (paternal != null) {
			final Genotype pGT = paternal.getGenotypeForSample(p.getName());
			if ((pGT.isHomAlt() && p.getSex() == Sex.FEMALE) || pGT.isHomRef())
				return false;
		}
		if (maternal != null) {
			final Genotype mGT = maternal.getGenotypeForSample(p.getName());
			if (p.getSex() == Sex.FEMALE && (mGT.isHomAlt() && mGT.isHomRef()))
				return false;
		}

		// the paternal variant may not be homozygous REF in the father of
		// p, if any
		if (paternal != null && p.getFather() != null) {
			final Genotype pGT = paternal.getGenotypeForSample(p.getFather().getName());
			if (pGT.isHomRef())
				return false;
		}

		// the maternal variant may not be homozygous in the mother of
		// p, if any
		if (maternal != null && p.getMother() != null) {
			final Genotype mGT = maternal.getGenotypeForSample(p.getMother().getName());
			if (mGT.isHomAlt() || mGT.isHomRef())
				return false;
		}

		// none of the unaffected siblings may have the same genotypes
		// as p
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

	private boolean isCompatibleWithUnaffected(Candidate c) {
		for (Person p : pedigree.getMembers()) {
			if (p.getDisease() == Disease.UNAFFECTED) {
				boolean patHet = false;
				boolean matHet = false;
				// None of the genotypes from the paternal or maternal call lists may be homozygous in the index
				if (c.getPaternal() != null) {
					final Genotype pGT = c.getPaternal().getGenotypeForSample(p.getName());
					if (pGT.isHomAlt() || (p.getSex() == Sex.MALE && pGT.isHet()))
						return false;
					if (pGT.isHet())
						patHet = true;
				}
				if (c.getMaternal() != null) {
					final Genotype mGT = c.getMaternal().getGenotypeForSample(p.getName());
					if (mGT.isHomAlt() || (p.getSex() == Sex.MALE && mGT.isHet()))
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
						// way one (paternal and maternal can now be switched
						// around!
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

}
