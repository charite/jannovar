package de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.GenotypeList;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Pedigree.IndexedPerson;
import de.charite.compbio.jannovar.pedigree.Person;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerBase;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and autosomal recessive
 * homozygous mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * In the case of a single individual, we require {@link Genotype#HOMOZYGOUS_ALT}.
 *
 * In the case of multiple individuals, we require that the affects are compatible, that the unaffected parents of
 * affected individuals are not {@link Genotype#HOMOZYGOUS_REF} or {@link Genotype#HOMOZYGOUS_ALT} and that the
 * unaffected individuals are not {@link Genotype#HOMOZYGOUS_ALT}. The affected individuals are compatible if no
 * affected individual is {@link Genotype#HOMOZYGOUS_REF} or {@link Genotype#HETEROZYGOUS} and there is at least one
 * affected individual that is {@link Genotype#HOMOZYGOUS_ALT}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 * 
 * @deprecated use {@link VariantContextCompatibilityCheckerAutosomalDominant} instead.
 */
@Deprecated
public class CompatibilityCheckerAutosomalRecessiveHomozygous extends CompatibilityCheckerBase {

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
	public CompatibilityCheckerAutosomalRecessiveHomozygous(Pedigree pedigree, GenotypeList list)
			throws CompatibilityCheckerException {
		super(pedigree, list);
	}

	public boolean runSingleSampleCase() {
		for (ImmutableList<Genotype> gtList : list.getCalls())
			if (gtList.get(0) == Genotype.HOMOZYGOUS_ALT)
				return true;
		return false;
	}

	public boolean runMultiSampleCase() {
		for (ImmutableList<Genotype> gtList : list.getCalls())
			if (containsCompatibleHomozygousVariants(gtList))
				return true;
		return false;
	}

	private boolean containsCompatibleHomozygousVariants(ImmutableList<Genotype> gtList) {
		return (affectedsAreCompatible(gtList) && unaffectedParentsOfAffectedAreNotHomozygous(gtList) && unaffectedsAreNotHomozygousAlt(gtList));
	}

	private boolean unaffectedsAreNotHomozygousAlt(ImmutableList<Genotype> gtList) {
		for (Pedigree.IndexedPerson entry : pedigree.getNameToMember().values())
			if (entry.getPerson().getDisease() == Disease.UNAFFECTED && gtList.get(entry.getIdx()) == Genotype.HOMOZYGOUS_ALT)
				return false;
		return true;
	}

	private boolean unaffectedParentsOfAffectedAreNotHomozygous(ImmutableList<Genotype> gtList) {
		for (String name : getUnaffectedParentNamesOfAffecteds()) {
			IndexedPerson iPerson = pedigree.getNameToMember().get(name);
			// INVARIANT: iPerson cannot be null due to construction of Pedigree class
			if (gtList.get(iPerson.getIdx()) == Genotype.HOMOZYGOUS_ALT
					|| gtList.get(iPerson.getIdx()) == Genotype.HOMOZYGOUS_REF)
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

	private boolean affectedsAreCompatible(ImmutableList<Genotype> gtList) {
		int numHomozygousAlt = 0;

		for (Pedigree.IndexedPerson entry : pedigree.getNameToMember().values())
			if (entry.getPerson().getDisease() == Disease.AFFECTED) {
				if (gtList.get(entry.getIdx()) == Genotype.HOMOZYGOUS_REF || gtList.get(entry.getIdx()) == Genotype.HETEROZYGOUS)
					return false;
				else if (gtList.get(entry.getIdx()) == Genotype.HOMOZYGOUS_ALT)
					numHomozygousAlt += 1;
			}

		return (numHomozygousAlt > 0);
	}

}
