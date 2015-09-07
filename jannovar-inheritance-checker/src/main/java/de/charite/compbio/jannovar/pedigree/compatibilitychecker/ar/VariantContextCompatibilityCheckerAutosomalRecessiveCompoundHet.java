package de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.InheritanceVariantContext;
import de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Person;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.AbstractVariantContextCompatibilityChecker;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;

/**
 * Helper class for checking a {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} for compatibility with a {@link de.charite.compbio.jannovar.pedigree.Pedigree} and
 * autosomal recessive compound het mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * In the case of a single individual, we require at least two {@link de.charite.compbio.jannovar.pedigree.Genotype#HETEROZYGOUS} genotype calls.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @version 0.15-SNAPSHOT
 */
public class VariantContextCompatibilityCheckerAutosomalRecessiveCompoundHet
		extends AbstractVariantContextCompatibilityChecker {

	/** list of siblings for each person in {@link #pedigree} */
	private final ImmutableMap<Person, ImmutableList<Person>> siblings;

	/**
	 * Initialize compatibility checker for autosomal recessive compund het and perform some sanity checks.
	 *
	 * @see AbstractVariantContextCompatibilityChecker#AbstractVariantContextCompatibilityChecker(Pedigree,
	 *      InheritanceVariantContextList)
	 * @param pedigree
	 *            the {@link de.charite.compbio.jannovar.pedigree.Pedigree} to use for the initialize
	 * @param list
	 *            the {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} to use for the initialization
	 * @throws de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public VariantContextCompatibilityCheckerAutosomalRecessiveCompoundHet(Pedigree pedigree,
			InheritanceVariantContextList list) throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);
		this.siblings = buildSiblings(pedigree);
	}
	
	public void runSingleSampleCase() {
		List<Integer> hets = new ArrayList<Integer>();
		for (int i = 0; i < list.getVcList().size(); i++) {
			if (list.getVcList().get(i).getSingleSampleGenotype() == Genotype.HETEROZYGOUS)
				hets.add(i);
		}
		if (hets.size() > 1) {
			for (Integer i : hets) {
				list.getVcList().get(i).setMatchInheritance(true);
			}
		}
	}

	public void runMultiSampleCase() {
		// First, collect candidate genotype call lists from trios around
		// affected individuals.
		ArrayList<Candidate> candidates = collectTrioCandidates();

		// Then, check the candidates for all trios around affected individuals.
		for (Candidate c : candidates)
			if (isCompatibleWithTriosAroundAffected(c))
				// If candidate holds, check all unaffected for not being
				// homozygous alt.
				if (isCompatibleWithUnaffected(c)) {
					c.getPaternal().setMatchInheritance(true);
					c.getMaternal().setMatchInheritance(true);
				}
	}

	private boolean isCompatibleWithUnaffected(Candidate c) {
		for (Person p : pedigree.getMembers()) {
			if (p.getDisease() == Disease.UNAFFECTED) {
				boolean patHet = false;
				boolean matHet = false;
				// none of the genotypes from the paternal or maternal call
				// lists may be homozygous in the index
				if (c.getPaternal() != null) {
					final Genotype pGT = c.getPaternal().getGenotype(p);
					if (pGT == Genotype.HOMOZYGOUS_ALT)
						return false;
					if (pGT == Genotype.HETEROZYGOUS)
						patHet = true;
				}
				if (c.getMaternal() != null) {
					final Genotype mGT = c.getMaternal().getGenotype(p);
					if (mGT == Genotype.HOMOZYGOUS_ALT)
						return false;
					if (mGT == Genotype.HETEROZYGOUS)
						matHet = true;
				}

				// if mat and pat variant are heterozygous in an unaffected,
				// check if they are on the same allele or not
				if (patHet && matHet) {
					if (c.getPaternal() != null && p.getFather() != null && c.getMaternal() != null
							&& p.getMother() != null) {
						final Genotype ppGT = c.getPaternal().getGenotype(p.getFather());
						final Genotype mpGT = c.getPaternal().getGenotype(p.getMother());
						final Genotype pmGT = c.getMaternal().getGenotype(p.getFather());
						final Genotype mmGT = c.getMaternal().getGenotype(p.getMother());
						// way one (paternal and maternal can now be switched around!
						if (ppGT == Genotype.HETEROZYGOUS && mpGT == Genotype.HOMOZYGOUS_REF
								&& pmGT == Genotype.HOMOZYGOUS_REF && mmGT == Genotype.HETEROZYGOUS)
							return false;
						if (ppGT == Genotype.HOMOZYGOUS_REF && mpGT == Genotype.HETEROZYGOUS
								&& pmGT == Genotype.HETEROZYGOUS && mmGT == Genotype.HOMOZYGOUS_REF)
							return false;
					}

				}
			}
		}

		return true;
	}

	private ArrayList<Candidate> collectTrioCandidates() {
		ArrayList<Candidate> result = new ArrayList<Candidate>();

		for (Person p : pedigree.getMembers()) {
			if (p.getDisease() == Disease.AFFECTED && (p.getFather() != null || p.getMother() != null)) {
				List<InheritanceVariantContext> paternal = new ArrayList<InheritanceVariantContext>();
				List<InheritanceVariantContext> maternal = new ArrayList<InheritanceVariantContext>();

				// collect candidates towards the paternal side (heterozygous or
				// not observed in child and father. Not
				// hom_alt or het in mother)
				for (InheritanceVariantContext vc : list.getVcList())
					if ((vc.getGenotype(p) == Genotype.HETEROZYGOUS || vc.getGenotype(p) == Genotype.NOT_OBSERVED)
							&& (p.getFather() == null || vc.getGenotype(p.getFather()) == Genotype.HETEROZYGOUS
									|| vc.getGenotype(p.getFather()) == Genotype.NOT_OBSERVED)
							&& (p.getMother() == null || vc.getGenotype(p.getMother()) == Genotype.NOT_OBSERVED
									|| vc.getGenotype(p.getMother()) == Genotype.HOMOZYGOUS_REF))
						paternal.add(vc);
				// collect candidates towards the paternal side (heterozygous or
				// not observed in child and mother. Not
				// hom_alt or het in father)
				for (InheritanceVariantContext vc : list.getVcList())
					if ((vc.getGenotype(p) == Genotype.HETEROZYGOUS || vc.getGenotype(p) == Genotype.NOT_OBSERVED)
							&& (p.getMother() == null || vc.getGenotype(p.getMother()) == Genotype.HETEROZYGOUS
									|| vc.getGenotype(p.getMother()) == Genotype.NOT_OBSERVED)
							&& (p.getFather() == null || vc.getGenotype(p.getFather()) == Genotype.NOT_OBSERVED
									|| vc.getGenotype(p.getFather()) == Genotype.HOMOZYGOUS_REF))
						maternal.add(vc);

				// combine compatible paternal and maternal heterozygous
				// variants
				for (InheritanceVariantContext pat : paternal)
					for (InheritanceVariantContext mat : maternal) {
						if (pat == mat) // FIXME what means this NOW?
							continue; // exclude if variants are identical
						if (pat.getGenotype(p) == Genotype.NOT_OBSERVED
								&& (p.getFather() == null || pat.getGenotype(p.getFather()) == Genotype.NOT_OBSERVED)
								&& (p.getMother() == null || pat.getGenotype(p.getMother()) == Genotype.NOT_OBSERVED))
							continue; // exclude if not observed in all from
										// paternal
						if (mat.getGenotype(p) == Genotype.NOT_OBSERVED
								&& (p.getFather() == null || mat.getGenotype(p.getFather()) == Genotype.NOT_OBSERVED)
								&& (p.getMother() == null || mat.getGenotype(p.getMother()) == Genotype.NOT_OBSERVED))
							continue; // exclude if not observed in all from
										// maternal
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
				// Paternal maternal inheritance can be different for other parents in the pedigree.
				if (!isCompatibleWithTriosAndMaternalPaternalInheritanceAroundAffected(p, c.getPaternal(),
						c.getMaternal()))
					if (!isCompatibleWithTriosAndMaternalPaternalInheritanceAroundAffected(p, c.getMaternal(),
							c.getPaternal()))
						return false;
			}
		}

		return true;
	}

	private boolean isCompatibleWithTriosAndMaternalPaternalInheritanceAroundAffected(Person p,
			InheritanceVariantContext paternal, InheritanceVariantContext maternal) {
		// none of the genotypes from the paternal or maternal call
		// lists may be homozygous in the index
		if (paternal != null) {
			final Genotype pGT = paternal.getGenotype(p);
			if (pGT == Genotype.HOMOZYGOUS_ALT || pGT == Genotype.HOMOZYGOUS_REF)
				return false;
		}
		if (maternal != null) {
			final Genotype mGT = maternal.getGenotype(p);
			if (mGT == Genotype.HOMOZYGOUS_ALT || mGT == Genotype.HOMOZYGOUS_REF)
				return false;
		}

		// the paternal variant may not be homozygous in the father of
		// p, if any
		if (paternal != null && p.getFather() != null) {
			final Genotype pGT = paternal.getGenotype(p.getFather());
			if (pGT == Genotype.HOMOZYGOUS_ALT || pGT == Genotype.HOMOZYGOUS_REF)
				return false;
		}

		// the maternal variant may not be homozygous in the mother of
		// p, if any
		if (maternal != null && p.getMother() != null) {
			final Genotype mGT = maternal.getGenotype(p.getMother());
			if (mGT == Genotype.HOMOZYGOUS_ALT || mGT == Genotype.HOMOZYGOUS_REF)
				return false;
		}

		// none of the unaffected siblings may have the same genotypes
		// as p
		if (siblings != null && !siblings.isEmpty() && siblings.containsKey(p))
			for (Person sibling : siblings.get(p))
				if (sibling.getDisease() == Disease.UNAFFECTED) {
					final Genotype pGT = paternal.getGenotype(sibling);
					final Genotype mGT = maternal.getGenotype(sibling);
					if (pGT == Genotype.HETEROZYGOUS && mGT == Genotype.HETEROZYGOUS)
						return false;
				}
		return true;
	}

}
