package de.charite.compbio.jannovar.mendel;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.Immutable;

import java.util.Arrays;
import java.util.Collection;

/**
 * Representation of a genotype in an individual
 * <p>
 * Genotypes are represented by lists of integers identifying alleles from a {@link GenotypeCalls}. By convention, the
 * reference allele is represented by the integer <code>0</code>. <code>-1</code> encodes no-call.
 * <p>
 * Jannovar will define the zygosity in a very soft way. We do not want to throw too much things away. If all
 * {@link Genotype} are no-calls then the genotype is not observed. But if we have one {@link Genotype} called and
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

	/**
	 * List of allele numbers/calls
	 */
	private final int[] alleleNumbers;
	private final int ploidy;
	private final boolean hasNoObservedCalls;

	private final boolean isHet;
	private final boolean isHomRef;
	private final boolean isHomAlt;

	/**
	 * Construct {@link Genotype} with list of allele numbers
	 *
	 * @param alleleNumbers The allele numbers to initialize with
	 */
	public Genotype(Collection<Integer> alleleNumbers) {
		// one-time unboxing
		this.alleleNumbers = alleleNumbers.stream().mapToInt(i -> i).toArray();
		// now we're in a world of primitives
		this.ploidy = this.alleleNumbers.length;
		// These values are pre-calculated and cached as they are called many times during the
		// inheritance mode analysis which leads to huge GC and autoboxing overhead when
		// calculating them using the streams API. Doing so leads to very long hangs of several
		// minutes when using the MendelianInheritanceChecker on 20K variants where ~40% of CPU was
		// spent on calling isNotObserved().
		int[] observedCalls = findObservedCalls(ploidy, this.alleleNumbers);
		this.hasNoObservedCalls = observedCalls.length == 0;
		this.isHet = calculateIsHet(ploidy, hasNoObservedCalls, this.alleleNumbers);
		this.isHomRef = calculateIsHomRef(hasNoObservedCalls, this.alleleNumbers);
		this.isHomAlt = calculateIsHomAlt(hasNoObservedCalls, observedCalls);
	}

	private int[] findObservedCalls(int numCalls, int[] alleleNumbers) {
		int numObservedCalls = 0;
		int[] temp = new int[numCalls];
		for (int i = 0; i < numCalls ; i++) {
			int call = alleleNumbers[i];
			if (call != NO_CALL) {
				temp[numObservedCalls++] = call;
			}
		}

		if (numObservedCalls == numCalls) {
			// all calls were observed, return original array
			return alleleNumbers;
		}
		// return shortened array containing only observed calls
		int[] observedCalls = new int[numObservedCalls];
		System.arraycopy(temp, 0, observedCalls, 0, numObservedCalls);
		return observedCalls;
	}

	/**
	 * @return <code>true</code> if the sample is heterozygous. One call can be no_call, <code>false</code> otherwise
	 */
	private boolean calculateIsHet(int ploidy, boolean hasNoObservedCalls, int[] alleleNumbers) {
		if (ploidy != 2 || hasNoObservedCalls) {
			// only diploid genotypes can be heterozygous
			// we want to have at least one observed call
			return false;
		}
		return !(alleleNumbers[0] == alleleNumbers[1]);
	}

	/**
	 * @return <code>true</code> if the sample is homozygous ref. Can have exactly one {@value #NO_CALL},
	 * <code>false</code> otherwise
	 */
	private boolean calculateIsHomRef(boolean hasNoObservedCalls, int[] alleleNumbers) {
		// we want to have at least one observed call
		if (hasNoObservedCalls) {
			return false;
		}
		// all alleles must be REF
		for (int x : alleleNumbers) {
			if (x != REF_CALL && x != NO_CALL) {
				return false;
			}
		}
		return true;
	}

	private boolean calculateIsHomAlt(boolean hasNoObservedCalls, int[] observedCalls) {
		// we want to have at least one observed call
		// hom alt cannot have a ref call
		if (hasNoObservedCalls || hasRefCall(observedCalls)) {
			return false;
		}
		int alt = observedCalls[0];
		for (int i = 1; i < observedCalls.length ; i++) {
			if (alt != observedCalls[i]) {
				return false;
			}
		}
		return true;
	}

	private boolean hasRefCall(int[] observedCalls) {
		for (int x : observedCalls) {
			if (x == REF_CALL) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return {@link ImmutableList} of alleles in this genotype
	 */
	public ImmutableList<Integer> getAlleleNumbers() {
		return Arrays.stream(alleleNumbers).boxed().collect(ImmutableList.toImmutableList());
	}

	/**
	 * @return Number of alleles in the genotype
	 */
	public int getPloidy() {
		return ploidy;
	}

	/**
	 * @return <code>true</code> if the sample is diploid, <code>false</code> otherwise
	 */
	public boolean isDiploid() {
		return ploidy == 2;
	}

	/**
	 * @return <code>true</code> if the sample is monoploid, <code>false</code> otherwise
	 */
	public boolean isMonoploid() {
		return ploidy == 1;
	}

	/**
	 * @return <code>true</code> if the sample is heterozygous. One call can be no_call, <code>false</code> otherwise
	 */
	public boolean isHet() {
		return isHet;
	}

	/**
	 * @return <code>true</code> if the sample is homozygous ref. Can have exactly one {@value #NO_CALL},
	 * <code>false</code> otherwise
	 */
	public boolean isHomRef() {
		return isHomRef;
	}

	/**
	 * @return <code>true</code> if the sample is homozygous alt, <code>false</code> otherwise
	 */
	public boolean isHomAlt() {
		return isHomAlt;
	}

	/**
	 * @return <code>true</code> if the genotype is not observed in all alleles
	 */
	public boolean isNotObserved() {
		return hasNoObservedCalls;
	}

	@Override
	public String toString() {
		String alleleNumbersString = makeAlleleNumbersString();
		return "Genotype [alleleNumbers=" + alleleNumbersString + "]";
	}

	private String makeAlleleNumbersString() {
		if (ploidy == 0) {
			return "[]";
		}
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0; i < ploidy; i++) {
			sb.append(alleleNumbers[i]);
			if (i == ploidy - 1) {
				break;
			}
			sb.append(',').append(' ');
		}
		return sb.append(']').toString();
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(alleleNumbers);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Genotype)) return false;
		Genotype genotype = (Genotype) o;
		return Arrays.equals(alleleNumbers, genotype.alleleNumbers);
	}
}
