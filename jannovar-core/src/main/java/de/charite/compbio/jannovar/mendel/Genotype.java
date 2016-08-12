package de.charite.compbio.jannovar.mendel;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.Immutable;

/**
 * Representation of a genotype in an individual
 * 
 * Genotypes are represented by lists of integers identifying alleles from a {@link GenotypeCalls}. By convention, the
 * reference allele is represented by the integer <code>0</code>. <code>-1</code> encodes no-call.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
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
	 * @param alleleNumbers The allele numbers to initialize with
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
	 * @return <code>true</code> if the sample is heterozygous, <code>false</code> otherwise
	 */
	public boolean isHet() {
		if (!isDiploid())
			return false; // only diploid genotypes cann be heterozygous
		return ((alleleNumbers.get(0) == REF_CALL && alleleNumbers.get(1) != REF_CALL
				&& alleleNumbers.get(1) != NO_CALL)
				|| (alleleNumbers.get(0) != REF_CALL && alleleNumbers.get(0) != NO_CALL
						&& alleleNumbers.get(1) == REF_CALL));
	}

	/**
	 * @return <code>true</code> if the sample is homozygous ref, <code>false</code> otherwise
	 */
	public boolean isHomRef() {
		if (alleleNumbers.isEmpty())
			return false; // empty calls are nothing
		return alleleNumbers.stream().allMatch(x -> (x == REF_CALL));
	}

	/**
	 * @return <code>true</code> if the sample is homozygous alt, <code>false</code> otherwise
	 */
	public boolean isHomAlt() {
		if (alleleNumbers.isEmpty())
			return false; // empty calls are nothing
		return alleleNumbers.stream().allMatch(x -> (x != REF_CALL && x != NO_CALL));
	}

	/**
	 * @return <code>true</code> if the genotype is not observed in all alleles
	 */
	public boolean isNotObserved() {
		return alleleNumbers.stream().allMatch(n -> (n == NO_CALL));
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
