package de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar;

import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.InheritanceVariantContext;
import de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList;
import de.charite.compbio.jannovar.pedigree.ModeOfInheritance;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Pedigree.IndexedPerson;
import de.charite.compbio.jannovar.pedigree.Person;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.AbstractVariantContextCompatibilityChecker;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;

/**
 * Helper class for checking a {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} for compatibility with a {@link de.charite.compbio.jannovar.pedigree.Pedigree} and autosomal
 * recessive homozygous mode of inheritance.
 *
 * <h2>Compatibility Check</h2>
 *
 * In the case of a single individual, we require {@link de.charite.compbio.jannovar.pedigree.Genotype#HOMOZYGOUS_ALT}.
 *
 * In the case of multiple individuals, we require that the affects are compatible, that the unaffected parents of
 * affected individuals are not {@link de.charite.compbio.jannovar.pedigree.Genotype#HOMOZYGOUS_REF} or {@link de.charite.compbio.jannovar.pedigree.Genotype#HOMOZYGOUS_ALT} and that the
 * unaffected individuals are not {@link de.charite.compbio.jannovar.pedigree.Genotype#HOMOZYGOUS_ALT}. The affected individuals are compatible if no
 * affected individual is {@link de.charite.compbio.jannovar.pedigree.Genotype#HOMOZYGOUS_REF} or {@link de.charite.compbio.jannovar.pedigree.Genotype#HETEROZYGOUS} and there is at least one
 * affected individual that is {@link de.charite.compbio.jannovar.pedigree.Genotype#HOMOZYGOUS_ALT}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @version 0.15-SNAPSHOT
 */
public class VariantContextCompatibilityCheckerAutosomalRecessiveHomozygous extends AbstractVariantContextCompatibilityChecker {

	/**
	 * Initialize compatibility checker for autosomal recessive homozygout and perform some sanity checks.
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
	public VariantContextCompatibilityCheckerAutosomalRecessiveHomozygous(Pedigree pedigree, InheritanceVariantContextList list)
			throws InheritanceCompatibilityCheckerException {
		super(pedigree, list);
	}
	
	public void runSingleSampleCase() {
		for (InheritanceVariantContext vc : list.getVcList())
			if (vc.getSingleSampleGenotype() == Genotype.HOMOZYGOUS_ALT)
				vc.addMatchInheritance(ModeOfInheritance.AUTOSOMAL_RECESSIVE);
	}

	public void runMultiSampleCase() {
		for (InheritanceVariantContext vc : list.getVcList())
			if (containsCompatibleHomozygousVariants(vc))
				vc.addMatchInheritance(ModeOfInheritance.AUTOSOMAL_RECESSIVE);
	}

	private boolean containsCompatibleHomozygousVariants(InheritanceVariantContext vc) {
		return (affectedsAreCompatible(vc) && unaffectedParentsOfAffectedAreNotHomozygous(vc)
				&& unaffectedsAreNotHomozygousAlt(vc));
	}

	private boolean unaffectedsAreNotHomozygousAlt(InheritanceVariantContext vc) {
		for (Pedigree.IndexedPerson entry : pedigree.getNameToMember().values())
			if (entry.getPerson().getDisease() == Disease.UNAFFECTED
					&& vc.getGenotype(entry.getPerson()) == Genotype.HOMOZYGOUS_ALT)
				return false;
		return true;
	}

	private boolean unaffectedParentsOfAffectedAreNotHomozygous(InheritanceVariantContext vc) {
		for (String name : getUnaffectedParentNamesOfAffecteds()) {
			IndexedPerson iPerson = pedigree.getNameToMember().get(name);
			// INVARIANT: iPerson cannot be null due to construction of Pedigree class
			if (vc.getGenotype(iPerson.getPerson()) == Genotype.HOMOZYGOUS_ALT
					|| vc.getGenotype(iPerson.getPerson()) == Genotype.HOMOZYGOUS_REF)
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

	private boolean affectedsAreCompatible(InheritanceVariantContext vc) {
		int numHomozygousAlt = 0;

		for (Pedigree.IndexedPerson entry : pedigree.getNameToMember().values())
			if (entry.getPerson().getDisease() == Disease.AFFECTED) {
				if (vc.getGenotype(entry.getPerson()) == Genotype.HOMOZYGOUS_REF
						|| vc.getGenotype(entry.getPerson()) == Genotype.HETEROZYGOUS)
					return false;
				else if (vc.getGenotype(entry.getPerson()) == Genotype.HOMOZYGOUS_ALT)
					numHomozygousAlt += 1;
			}

		return (numHomozygousAlt > 0);
	}

}
