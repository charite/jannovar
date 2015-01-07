package jannovar.pedigree;

import jannovar.reference.GenomePosition;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and X recessive mode of
 * inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * If the pedigree has only one sample then the check is as follows. If the index is female, the checker returns true if
 * the genotype call list is compatible with autosomal recessive compound heterozygous inheritance or if the list
 * contains a homozygous alt call. If the index is male then return true if the list contains a homozygous alt call.
 *
 * If the pedigree has more samples, the checks are more involved.
 *
 * <b>Note</b> that the case of X-chromosomal compound heterozygous mutations is not handled. We assume that female
 * individuals are not affected.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
class CompatibilityCheckerXRecessive {

	/** the pedigree to use for the checking */
	public final Pedigree pedigree;

	/** the genotype call list to use for the checking */
	public final GenotypeList list;

	/** decorator for getting unaffected individuals and such from the {@link Pedigree} */
	private final PedigreeQueryDecorator queryDecorator;

	/**
	 * Initialize compatibility checker and perform some sanity checks.
	 *
	 * The {@link GenotypeList} object passed to the constructor is expected to represent all of the variants found in a
	 * certain gene (possibly after filtering for rarity or predicted pathogenicity). The samples represented by the
	 * {@link GenotypeList} must be in the same order as the list of individuals contained in this pedigree.
	 *
	 * @param pedigree
	 *            the {@link Pedigree} to use for the initialize
	 * @param list
	 *            the {@link GenotypeList} to use for the initialization
	 * @throws CompatibilityCheckerException
	 *             if the pedigree or variant list is invalid
	 */
	public CompatibilityCheckerXRecessive(Pedigree pedigree, GenotypeList list) throws CompatibilityCheckerException {
		if (pedigree.members.size() == 0)
			throw new CompatibilityCheckerException("Invalid pedigree of size 1.");
		if (list.namesEqual(pedigree))
			throw new CompatibilityCheckerException("Incompatible names in pedigree and genotype list.");
		if (list.calls.get(0).size() == 0)
			throw new CompatibilityCheckerException("Genotype call list must not be empty!");

		this.pedigree = pedigree;
		this.list = list;
		this.queryDecorator = new PedigreeQueryDecorator(pedigree);
	}

	public boolean run() throws CompatibilityCheckerException {
		// perform basic sanity check, regarding X chromsome
		final GenomePosition txBegin = list.transcript.txRegion.getGenomeBeginPos();
		Integer chrXID = txBegin.refDict.contigID.get("X");
		if (chrXID == null)
			return false; // this organism type does not have X chromosome
		if (chrXID.intValue() != txBegin.chr)
			return false; // transcript of genotype list is not on X chromosome

		if (pedigree.members.size() == 1)
			return runSingleSampleCase();
		else
			return runMultiSampleCase();
	}

	private boolean runSingleSampleCase() throws CompatibilityCheckerException {
		// for female single case samples, allow autosomal recessive compound heterozygous
		if (pedigree.members.get(0).sex == Sex.FEMALE)
			if (new CompatibilityCheckerAutosomalRecessiveCompoundHet(pedigree, list).run())
				return true;

		// for both male and female subjects, return true if homozygous alt
		for (ImmutableList<Genotype> gtList : list.calls)
			if (gtList.get(0) == Genotype.HOMOZYGOUS_ALT)
				return true;

		return false;
	}

	private boolean runMultiSampleCase() {
		for (ImmutableList<Genotype> gtList : list.calls) {
			// Check whether this list of genotype calls is compatible when with the set of affected individuals, the
			// parents, and the unaffected individuals.
			if (checkCompatibilityAffected(gtList) && checkCompatibilityParents(gtList)
					&& checkCompatibilityUnaffected(gtList))
				return true;
		}

		return false;
	}

	private boolean checkCompatibilityAffected(ImmutableList<Genotype> gtList) {
		int i = 0;
		for (Person person : pedigree.members) {
			if (person.sex == Sex.MALE && person.disease == Disease.AFFECTED
					&& gtList.get(i) == Genotype.HOMOZYGOUS_REF)
				return false; // cannot be disease-causing mutation, an affected male does not have it
			++i;
		}

		return true;
	}

	private boolean checkCompatibilityParents(ImmutableList<Genotype> gtList) {
		final ImmutableSet<String> parentNames = queryDecorator.getParentNames();
		int i = 0;
		for (Person person : pedigree.members) {
			if (parentNames.contains(person.name)) {
				final Genotype gt = gtList.get(i);
				if (person.sex == Sex.MALE && person.disease == Disease.UNAFFECTED && gt != Genotype.HOMOZYGOUS_REF)
					return false; // cannot be disease-causing mutation if an unaffected father has it
				if (person.sex == Sex.FEMALE && gt == Genotype.HOMOZYGOUS_ALT)
					return false; // cannot be disease-causing mutation if mother of patient is homozygous
			}
			++i;
		}

		return true;
	}

	private boolean checkCompatibilityUnaffected(ImmutableList<Genotype> gtList) {
		final ImmutableSet<String> unaffectedNames = queryDecorator.getUnaffectedNames();
		int i = 0;
		for (Person person : pedigree.members) {
			if (unaffectedNames.contains(person.name)) {
				final Genotype gt = gtList.get(i);
				// Males with a hemizygous mutation have to be encoded as HOMOZYGOUS_ALT in the VCF. Thus, the handling
				// of Kleinefelder here is not comprehensive for simplicity's sake.
				if (gt == Genotype.HOMOZYGOUS_ALT)
					return false; // cannot be disease-causing mutation, an unaffected individual has it
			}
			++i;
		}

		return true;
	}

}
