package de.charite.compbio.jannovar.mendel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.Immutable;

/**
 * Representation of a genotype in an individual
 * 
 * Genotypes are represented by lists of integers identifying alleles from a {@link GenotypeCalls}. By convention, the
 * reference allele is represented by the integer <code>0</code>. <code>-1</code> encodes no-call.
 * 
 * Jannovar will define the zygosity in a very soft way. We do not want to throw too much things away. If all
 * {@link Genotype} are no-calls then teh gebnotype is not observed. But if we have one {@link Genotype} called and
 * another is a no-call (e.g 0/.) we will consider fo the no-call all possibilities. So it can be het or homRef. This
 * behaviour is different to HTSJDK VariantContexed where they have a additional mixed class for such genotypes.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:j.jacobsen@qmul.ac.uk">Jules Jacobsen</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
@Immutable
public class Genotype {

	public static final int NO_CALL = -1;
	public static final int REF_CALL = 0;

	/** List of allele numbers */
	private final ImmutableList<Integer> alleleNumbers;

	/**
	 * Construct {@link Genotype} with list of allele numbers
	 * 
	 * @param alleleNumbers
	 *            The allele numbers to initialize with
	 */
	public Genotype(Collection<Integer> alleleNumbers) {
		this.alleleNumbers = ImmutableList.copyOf(alleleNumbers);
	}

	/**
	 * @return {@link ImmutableList} of alleles in this genotype
	 */
	public ImmutableList<Integer> getAlleleNumbers() {
		return alleleNumbers;
	}

	/**
	 * @return Number of alleles in the genotype
	 */
	public int getPloidy() {
		return alleleNumbers.size();
	}

	/**
	 * @return <code>true</code> if the sample is diploid, <code>false</code> otherwise
	 */
	public boolean isDiploid() {
		return (getPloidy() == 2);
	}

	/**
	 * @return <code>true</code> if the sample is monoploid, <code>false</code> otherwise
	 */
	public boolean isMonoploid() {
		return (getPloidy() == 1);
	}

	/**
	 * @return <code>true</code> if the sample is heterozygous. One call can be no_call, <code>false</code> otherwise
	 */
	public boolean isHet() {
		if (!isDiploid())
			return false; // only diploid genotypes cann be heterozygous
		if (isNotObserved())
			return false; // we want to have at least one observed call
		return !alleleNumbers.get(0).equals(alleleNumbers.get(1));

	}

	/**
	 * @return <code>true</code> if the sample is homozygous ref. Can have exactly one {@value #NO_CALL},
	 *         <code>false</code> otherwise
	 */
	public boolean isHomRef() {
		if (alleleNumbers.isEmpty())
			return false; // empty calls are nothing
		if (isNotObserved())
			return false; // we want to have at least one observed call
		return alleleNumbers.stream().allMatch(x -> x == REF_CALL || x == NO_CALL);
	}

	/**
	 * @return <code>true</code> if the sample is homozygous alt, <code>false</code> otherwise
	 */
	public boolean isHomAlt() {
		if (alleleNumbers.isEmpty())
			return false; // empty calls are nothing
		if (isNotObserved())
			return false; // we want to have at least one observed call

		boolean noRefCall = alleleNumbers.stream().noneMatch(x -> x == REF_CALL);
		if (!noRefCall)
			return false;

		List<Integer> calledAlts = alleleNumbers.stream().filter(n -> n != NO_CALL).collect(Collectors.toList());
		Integer alt = calledAlts.get(0);
		for (Integer otherAlt : calledAlts) {
			if (!alt.equals(otherAlt))
				return false;
		}
		return true;
	}

	/**
	 * @return <code>true</code> if the genotype is not observed in all alleles
	 */
	public boolean isNotObserved() {
		return alleleNumbers.stream().allMatch(n -> n == NO_CALL);
	}

	@Override
	public String toString() {
		return "Genotype [alleleNumbers=" + alleleNumbers + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alleleNumbers == null) ? 0 : alleleNumbers.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Genotype other = (Genotype) obj;
		if (alleleNumbers == null) {
			if (other.alleleNumbers != null)
				return false;
		} else if (!alleleNumbers.equals(other.alleleNumbers))
			return false;
		return true;
	}

}
