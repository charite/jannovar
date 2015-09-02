package de.charite.compbio.jannovar.pedigree.compatibilitychecker.xr;

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
import de.charite.compbio.jannovar.pedigree.Sex;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.AbstractVariantContextCompatibilityChecker;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar.VariantContextCompatibilityCheckerAutosomalRecessiveCompoundHet;

/**
 * Helper class for checking a {@link InheritanceVariantContextList} for compatibility with a {@link Pedigree} and X
 * recessive compound het mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * In the case of a single individual, we require at least two {@link Genotype#HETEROZYGOUS} genotype calls.
 *
 * In the case of multiple individuals, we require that the affects are compatible, that the parents of affected
 * individuals. Be careful, that the father must be {@link Disease#AFFECTED}. Therefore the father should be
 * {@link Disease#AFFECTED} and {@link Genotype#HOMOZYGOUS_ALT}. We will allow {@link Genotype#HETEROZYGOUS} because of
 * miscalls.
 * 
 * Unaffected mothers are not are not {@link Genotype#HOMOZYGOUS_REF} for one allele, and that all unaffected
 * individuals are not {@link Genotype#HOMOZYGOUS_ALT} and should not have it comp. het (only possible if parents are
 * available).
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @version 0.15-SNAPSHOT
 */
class VariantContextCompatibilityCheckerXRecessiveCompoundHet extends AbstractVariantContextCompatibilityChecker {

	/** list of siblings for each person in {@link #pedigree} */
	public final ImmutableMap<Person, ImmutableList<Person>> siblings;

	/**
	 * Initialize compatibility checker for X recessive compund heterozygous and perform some sanity checks.
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
	public VariantContextCompatibilityCheckerXRecessiveCompoundHet(Pedigree pedigree,
			InheritanceVariantContextList list) throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);
		this.siblings = buildSiblings(pedigree);
	}
	
	public void runSingleSampleCase() throws InheritanceCompatibilityCheckerException {
		// for female single case samples, allow autosomal recessive compound
		// heterozygous
		if (pedigree.getMembers().get(0).getSex() != Sex.MALE)
			new VariantContextCompatibilityCheckerAutosomalRecessiveCompoundHet(pedigree, list).run();

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

	private ArrayList<Candidate> collectTrioCandidates() {
		ArrayList<Candidate> result = new ArrayList<Candidate>();

		for (Person p : pedigree.getMembers()) {
			if (p.getDisease() == Disease.AFFECTED && (p.getFather() != null || p.getMother() != null)) {
				List<InheritanceVariantContext> paternal = new ArrayList<InheritanceVariantContext>();
				List<InheritanceVariantContext> maternal = new ArrayList<InheritanceVariantContext>();

				for (InheritanceVariantContext vc : list.getVcList()) {

					// Child is heterozygous. male child/ukn can be homozygous
					if ((vc.getGenotype(p) == Genotype.HETEROZYGOUS || vc.getGenotype(p) == Genotype.NOT_OBSERVED
							|| (p.getSex() != Sex.FEMALE && vc.getGenotype(p) == Genotype.HOMOZYGOUS_ALT))) {
						// collect candidates towards the paternal side
						// (heterozygous (false call in father) or not observed
						// in child and father. Not hom_alt or het in mother).
						if ((p.getFather() == null || vc.getGenotype(p.getFather()) == Genotype.HETEROZYGOUS
								|| vc.getGenotype(p.getFather()) == Genotype.NOT_OBSERVED
								|| vc.getGenotype(p.getFather()) == Genotype.HOMOZYGOUS_ALT)
								&& (p.getMother() == null || vc.getGenotype(p.getMother()) == Genotype.NOT_OBSERVED
										|| vc.getGenotype(p.getMother()) == Genotype.HOMOZYGOUS_REF))
							paternal.add(vc);
						// collect candidates towards the maternal side
						// (heterozygous or not observed in child and mother.
						// For father no restriction, cause father should be affected if present.
						if ((p.getMother() == null || vc.getGenotype(p.getMother()) == Genotype.HETEROZYGOUS
								|| vc.getGenotype(p.getMother()) == Genotype.NOT_OBSERVED))
							maternal.add(vc);
					}
				}

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

	private boolean isCompatibleWithTriosAndMaternalPaternalInheritanceAroundAffected(Person p,
			InheritanceVariantContext paternal, InheritanceVariantContext maternal) {
		// none of the genotypes from the paternal or maternal call
		// lists may be homozygous in a female index. can be homozygous else.
		if (paternal != null) {
			final Genotype pGT = paternal.getGenotype(p);
			if ((pGT == Genotype.HOMOZYGOUS_ALT && p.getSex() == Sex.FEMALE) || pGT == Genotype.HOMOZYGOUS_REF)
				return false;
		}
		if (maternal != null) {
			final Genotype mGT = maternal.getGenotype(p);
			if (p.getSex() == Sex.FEMALE && (mGT == Genotype.HOMOZYGOUS_ALT && mGT == Genotype.HOMOZYGOUS_REF))
				return false;
		}

		// the paternal variant may not be homozygous REF in the father of
		// p, if any
		if (paternal != null && p.getFather() != null) {
			final Genotype pGT = paternal.getGenotype(p.getFather());
			if (pGT == Genotype.HOMOZYGOUS_REF)
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

	private boolean isCompatibleWithUnaffected(Candidate c) {
		for (Person p : pedigree.getMembers()) {
			if (p.getDisease() == Disease.UNAFFECTED) {
				boolean patHet = false;
				boolean matHet = false;
				// none of the genotypes from the paternal or maternal call
				// lists may be homozygous in the index
				if (c.getPaternal() != null) {
					final Genotype pGT = c.getPaternal().getGenotype(p);
					if (pGT == Genotype.HOMOZYGOUS_ALT || (p.getSex() == Sex.MALE && pGT == Genotype.HETEROZYGOUS))
						return false;
					if (pGT == Genotype.HETEROZYGOUS)
						patHet = true;
				}
				if (c.getMaternal() != null) {
					final Genotype mGT = c.getMaternal().getGenotype(p);
					if (mGT == Genotype.HOMOZYGOUS_ALT || (p.getSex() == Sex.MALE && mGT == Genotype.HETEROZYGOUS))
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
						// way one (paternal and maternal can now be switched
						// around!
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

}
