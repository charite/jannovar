package de.charite.compbio.jannovar.mendel.impl;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.mendel.*;
import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Person;

import java.util.Collection;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper class for checking a {@link GenotypeCalls} for compatibility with a
 * pedigree and mitochondrial inheritance
 *
 * <h2>Compatibility Check</h2>
 *
 * In the case of a single individual, we require merely that a variant is
 * located on the mitochondrion.
 *
 * In the case of multiple individuals, we require that all affecteds have the
 * called variant and that there is is no transmission from an affected father
 * to children (this would mean that the case is definitely not related to a
 * mitochondrial mutation, whatever the distribution of variants may be!).
 * <P>
 * Note that mitochondrial inheritance is considered to be Nonmendelian, so that
 * this class is named {@code InheritanceCheckerMT} rather than
 * {@code MendelianCheckerMT} as the other classes in this package.
 * </P>
 *
 * @author <a href="mailto:Peter.Robinson@jax.org">Peter N Robinson</a>
 * @author <a href="mailto:max.schubach@bihealth.de">Max Schubach</a>
 * 
 * @since version 0.24 (September 15, 2017)
 */
public class InheritanceCheckerMT extends AbstractMendelianChecker {

	public InheritanceCheckerMT(MendelianInheritanceChecker parent) {
		super(parent);
	}

	@Override
	public ImmutableList<GenotypeCalls> filterCompatibleRecords(Collection<GenotypeCalls> calls)
		throws IncompatiblePedigreeException {

		// Filter to calls on the mitochondrion
		Stream<GenotypeCalls> mitoCalls = calls.stream()
			.filter(call -> call.getChromType() == ChromosomeType.MITOCHONDRIAL);

		// Filter to calls compatible with mitochondrial inheritance
		Stream<GenotypeCalls> compatibleCalls;
		if (this.pedigree.getNMembers() == 1)
			compatibleCalls = mitoCalls.filter(this::isCompatibleSingleton);
		else
			compatibleCalls = mitoCalls.filter(this::isCompatibleFamily);
		return ImmutableList.copyOf(compatibleCalls.collect(Collectors.toList()));

	}

	/**
	 * The rules of mitochondrial inheritance   complicated because of heteroplasmy. We will say that
	 * a variant is compatible with mitochondrial inheritance if all affecteds have the mutation and if the
	 * mutation was not found to be transmitted by a male to an affected. We will not rule out a mutation that
	 * is found in an unaffected because of the possiblity that the unaffected has the mutation in a low copy
	 * number and thus is not substantially affected clinically.
	 * @return whether <code>calls</code> is compatible with mitochondrial inheritance in the case of multiple
	 *         individuals in the pedigree
	 */
	private boolean isCompatibleFamily(GenotypeCalls calls) {
		return (affectedsAreCompatible(calls) && parentsAreCompatible(calls) && unaffectedAreCompatible(calls));
	}

	/**
	 * All affecteds should carry the disease-causing variant. Because of heteroplasmy,
	 * affecteds may carry different proportions of variant mtDNA, and thus we do not
	 * demand that affecteds are called with a heterozyogus or homozygous ALT genotype.
	 * However, if an affected is homozygous for the wildtype sequence, we rule out the
	 * candidate variant. Variant calling on the mito doesnot currently assess heteroplasmy, but any amount
	 // of called mutation will be assessed as potentially disease causing here.
	 * @param calls
	 * @return true if no affected is homozygous wildtype
	 */
	private boolean affectedsAreCompatible(GenotypeCalls calls) {
		int numHetOrHomAlt = 0;
		
		for (Pedigree.IndexedPerson entry : pedigree.getNameToMember().values()) {
			if (entry.getPerson().getDisease() == Disease.AFFECTED) {
				final Genotype gt = calls.getGenotypeForSample(entry.getPerson().getName());
				if (gt.isHomRef())
					return false;
				else if (gt.isHomAlt() || gt.isHet())
					numHetOrHomAlt += 1;
			}
		}
		return (numHetOrHomAlt > 0); // no affected is homozygous wildtype and at least one has a call
	}
	
	private boolean unaffectedAreCompatible(GenotypeCalls calls) {
		for (Person p : pedigree.getMembers()) {
			final String name = p.getName();
			final Genotype gt = calls.getGenotypeForSample(name);
			if (p.getDisease() == Disease.UNAFFECTED && gt.isHomAlt())
				return false;
		}
		return true; // no unaffected is homozygous alternative
	}

	/**
	 * Fathers do not transmit mitochondria to offspring, and so any apparent transmission of a
	 * mutation from father to offspring would not be compatible with mitochondrial inheritance
	 * (Should never happen actually). If a mother of an affected is affected, the we return
	 * false if the mother has a homozygous wildtype genotype.
	 * @return true unless we observe father to child inheritance of a called variant
	 */
	private boolean parentsAreCompatible(GenotypeCalls calls) {
		for (Person p : pedigree.getMembers()) {
			if (p.getDisease() == Disease.AFFECTED) {
				if (p.getMother() != null && p.getMother().isUnaffected()) {
					return false;
				}
			}
		}
		return true;
	}




	/**
	 * Males and females can be affected by mitochrondial mutations, and so if there is any call from a variant
	 * on the mitochondrion, a singleton sample is compatible with mitochondrial inheritance.
	 * @return whether <code>calls</code> is compatible with mitochondrial inheritance in the case of a single
	 *         individual in the pedigree
	 */
	private boolean isCompatibleSingleton(GenotypeCalls calls) {
		if (calls.getNSamples() == 0)
			return false; // no calls!
		
		return calls.getGenotypeBySampleNo(0).isHet() || calls.getGenotypeBySampleNo(0).isHomAlt();
	}

}
