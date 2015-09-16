package de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.GenotypeList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Person;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerBase;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a
 * {@link Pedigree} and autosomal recessive compound het mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * In the case of a single individual, we require at least two  {@link Genotype#HETEROZYGOUS}
 * genotype calls.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 * 
 * @deprecated use {@link VariantContextCompatibilityCheckerAutosomalDominant} instead.
 */
@Deprecated
public class CompatibilityCheckerAutosomalRecessiveCompoundHet extends CompatibilityCheckerBase {

	
	/** list of siblings for each person in {@link #pedigree} */
	private final ImmutableMap<Person, ImmutableList<Person>> siblings;

	/**
	 * Initialize compatibility checker and perform some sanity checks.
	 *
	 * The {@link GenotypeList} object passed to the constructor is expected to
	 * represent all of the variants found in a certain gene (possibly after
	 * filtering for rarity or predicted pathogenicity). The samples represented
	 * by the {@link GenotypeList} must be in the same order as the list of
	 * individuals contained in this pedigree.
	 *
	 * @param pedigree
	 *            the {@link Pedigree} to use for the initialize
	 * @param list
	 *            the {@link GenotypeList} to use for the initialization
	 * @throws CompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public CompatibilityCheckerAutosomalRecessiveCompoundHet(Pedigree pedigree, GenotypeList list)
			throws CompatibilityCheckerException {
		super(pedigree, list);
		this.siblings = buildSiblings(pedigree);
	}

	public boolean runSingleSampleCase() {
		int numHet = 0;
		for (ImmutableList<Genotype> gtList : list.getCalls())
			if (gtList.get(0) == Genotype.HETEROZYGOUS)
				numHet++;
		return (numHet > 1);
	}

	public boolean runMultiSampleCase() {
		// First, collect candidate genotype call lists from trios around
		// affected individuals.
		ArrayList<Candidate> candidates = collectTrioCandidates();

		// Then, check the candidates for all trios around affected individuals.
		for (Candidate c : candidates)
			if (isCompatibleWithTriosAroundAffected(c))
				// If candidate holds, check all unaffected for not being
				// homozygous alt.
				if (isCompatibleWithUnaffected(c))
					return true;
		return false;
	}

	private boolean isCompatibleWithUnaffected(Candidate c) {
		int pIdx = 0;
		for (Person p : pedigree.getMembers()) {
			if (p.getDisease() == Disease.UNAFFECTED) {
				boolean patHet = false;
				boolean matHet = false;
				// none of the genotypes from the paternal or maternal call
				// lists may be homozygous in the index
				if (c.getPaternal() != null) {
					final Genotype pGT = c.getPaternal().get(pIdx);
					if (pGT == Genotype.HOMOZYGOUS_ALT)
						return false;
					if (pGT == Genotype.HETEROZYGOUS)
						patHet = true;
				}
				if (c.getMaternal() != null) {
					final Genotype mGT = c.getMaternal().get(pIdx);
					if (mGT == Genotype.HOMOZYGOUS_ALT)
						return false;
					if (mGT == Genotype.HETEROZYGOUS)
						matHet = true;
				}

				// if mat and pat variant are heterozygous in an unaffected,
				// check if they are on the same allele or not
				if (patHet && matHet) {
					if (c.getPaternal() != null && p.getFather() != null && c.getMaternal() != null && p.getMother() != null) {
						final Genotype ppGT = c.getPaternal().get(pedigree.getNameToMember().get(p.getFather().getName()).getIdx());
						final Genotype mpGT = c.getPaternal().get(pedigree.getNameToMember().get(p.getMother().getName()).getIdx());
						final Genotype pmGT = c.getMaternal().get(pedigree.getNameToMember().get(p.getFather().getName()).getIdx());
						final Genotype mmGT = c.getMaternal().get(pedigree.getNameToMember().get(p.getMother().getName()).getIdx());
						// way one (paternal and maternal can now be switched around!
						if (ppGT == Genotype.HETEROZYGOUS && mpGT == Genotype.HOMOZYGOUS_REF && pmGT == Genotype.HOMOZYGOUS_REF && mmGT == Genotype.HETEROZYGOUS)
							return false;
						if (ppGT == Genotype.HOMOZYGOUS_REF && mpGT == Genotype.HETEROZYGOUS && pmGT == Genotype.HETEROZYGOUS && mmGT == Genotype.HOMOZYGOUS_REF)
							return false;
					}

				}
			}
			pIdx++;
		}

		return true;
	}

	private ArrayList<Candidate> collectTrioCandidates() {
		ArrayList<Candidate> result = new ArrayList<Candidate>();

		int pIdx = 0;
		for (Person p : pedigree.getMembers()) {
			if (p.getDisease() == Disease.AFFECTED && (p.getFather() != null || p.getMother() != null)) {
				ArrayList<ImmutableList<Genotype>> paternal = new ArrayList<ImmutableList<Genotype>>();
				ArrayList<ImmutableList<Genotype>> maternal = new ArrayList<ImmutableList<Genotype>>();

				// collect candidates towards the paternal side (heterozygous or
				// not observed in child and father. Not
				// hom_alt or het in mother)
				final int motherIdx = (p.getMother() == null) ? -1 : pedigree.getNameToMember().get(p.getMother().getName()).getIdx();
				final int fatherIdx = (p.getFather() == null) ? -1 : pedigree.getNameToMember().get(p.getFather().getName()).getIdx();
				for (ImmutableList<Genotype> lst : list.getCalls())
					if ((lst.get(pIdx) == Genotype.HETEROZYGOUS || lst.get(pIdx) == Genotype.NOT_OBSERVED)
							&& (fatherIdx == -1 || lst.get(fatherIdx) == Genotype.HETEROZYGOUS || lst.get(fatherIdx) == Genotype.NOT_OBSERVED)
							&& (motherIdx == -1 || lst.get(motherIdx) == Genotype.NOT_OBSERVED || lst.get(motherIdx) == Genotype.HOMOZYGOUS_REF))
						paternal.add(lst);
				// collect candidates towards the paternal side (heterozygous or
				// not observed in child and mother. Not
				// hom_alt or het in father)
				for (ImmutableList<Genotype> lst : list.getCalls())
					if ((lst.get(pIdx) == Genotype.HETEROZYGOUS || lst.get(pIdx) == Genotype.NOT_OBSERVED)
							&& (motherIdx == -1 || lst.get(motherIdx) == Genotype.HETEROZYGOUS || lst.get(motherIdx) == Genotype.NOT_OBSERVED)
							&& (fatherIdx == -1 || lst.get(fatherIdx) == Genotype.NOT_OBSERVED || lst.get(fatherIdx) == Genotype.HOMOZYGOUS_REF))
						maternal.add(lst);

				// combine compatible paternal and maternal heterozygous
				// variants
				for (ImmutableList<Genotype> pat : paternal)
					for (ImmutableList<Genotype> mat : maternal) {
						if (pat == mat)
							continue; // exclude if variants are identical
						if (pat.get(pIdx) == Genotype.NOT_OBSERVED
								&& (fatherIdx == -1 || pat.get(fatherIdx) == Genotype.NOT_OBSERVED)
								&& (motherIdx == -1 || pat.get(motherIdx) == Genotype.NOT_OBSERVED))
							continue; // exclude if not observed in all from
										// paternal
						if (mat.get(pIdx) == Genotype.NOT_OBSERVED
								&& (fatherIdx == -1 || mat.get(fatherIdx) == Genotype.NOT_OBSERVED)
								&& (motherIdx == -1 || mat.get(motherIdx) == Genotype.NOT_OBSERVED))
							continue; // exclude if not observed in all from
										// maternal
						result.add(new Candidate(pat, mat));
					}

			}
			pIdx++;
		}

		return result;
	}

	private boolean isCompatibleWithTriosAroundAffected(Candidate c) {
		int pIdx = 0;
		for (Person p : pedigree.getMembers()) {
			if (p.getDisease() == Disease.AFFECTED) {
				//we have to check this for paternal,maternal and vice versa. 
				// Paternal maternal inheritance can be different for other parents in the pedigree. 
				if (!isCompatibleWithTriosAndMaternalPaternalInheritanceAroundAffected(pIdx, p, c.getPaternal(), c.getMaternal()))
					if (!isCompatibleWithTriosAndMaternalPaternalInheritanceAroundAffected(pIdx, p, c.getMaternal(), c.getPaternal()))
						return false;
			}
			pIdx++;
		}

		return true;
	}

	private boolean isCompatibleWithTriosAndMaternalPaternalInheritanceAroundAffected(int pIdx, Person p,
			ImmutableList<Genotype> paternal, ImmutableList<Genotype> maternal) {
		// none of the genotypes from the paternal or maternal call
		// lists may be homozygous in the index
		if (paternal != null) {
			final Genotype pGT = paternal.get(pIdx);
			if (pGT == Genotype.HOMOZYGOUS_ALT || pGT == Genotype.HOMOZYGOUS_REF)
				return false;
		}
		if (maternal != null) {
			final Genotype mGT = maternal.get(pIdx);
			if (mGT == Genotype.HOMOZYGOUS_ALT || mGT == Genotype.HOMOZYGOUS_REF)
				return false;
		}

		// the paternal variant may not be homozygous in the father of
		// p, if any
		if (paternal != null && p.getFather() != null) {
			final Genotype pGT = paternal.get(pedigree.getNameToMember().get(p.getFather().getName()).getIdx());
			if (pGT == Genotype.HOMOZYGOUS_ALT || pGT == Genotype.HOMOZYGOUS_REF)
				return false;
		}

		// the maternal variant may not be homozygous in the mother of
		// p, if any
		if (maternal != null && p.getMother() != null) {
			final Genotype mGT = maternal.get(pedigree.getNameToMember().get(p.getMother().getName()).getIdx());
			if (mGT == Genotype.HOMOZYGOUS_ALT || mGT == Genotype.HOMOZYGOUS_REF)
				return false;
		}

		// none of the unaffected siblings may have the same genotypes
		// as p
		if (siblings != null && !siblings.isEmpty() && siblings.containsKey(p))
		for (Person sibling : siblings.get(p))
			if (sibling.getDisease() == Disease.UNAFFECTED) {
				final Genotype pGT = paternal.get(pedigree.getNameToMember().get(sibling.getName()).getIdx());
				final Genotype mGT = maternal.get(pedigree.getNameToMember().get(sibling.getName()).getIdx());
				if (pGT == Genotype.HETEROZYGOUS && mGT == Genotype.HETEROZYGOUS)
					return false;
			}
		return true;
	}

}
