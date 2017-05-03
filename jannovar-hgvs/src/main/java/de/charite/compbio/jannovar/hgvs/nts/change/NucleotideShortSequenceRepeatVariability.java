package de.charite.compbio.jannovar.hgvs.nts.change;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;

public class NucleotideShortSequenceRepeatVariability extends NucleotideChange {

	/** range in the Nucleotide that has variable length */
	private final NucleotideRange range;
	/** lower bound on length, inclusive */
	private final int minCount;
	/** upper bound on length, inclusive */
	private final int maxCount;

	/** Factory forwards to {@link #NucleotideShortSequenceRepeatVariability(boolean, NucleotideRange, int, int)}. */
	public static NucleotideShortSequenceRepeatVariability build(boolean onlyPredicted, NucleotideRange range,
			int minCount, int maxCount) {
		return new NucleotideShortSequenceRepeatVariability(onlyPredicted, range, minCount, maxCount);
	}

	/** Construct with the given values */
	public NucleotideShortSequenceRepeatVariability(boolean onlyPredicted, NucleotideRange range, int minCount,
			int maxCount) {
		super(onlyPredicted);
		this.range = range;
		this.minCount = minCount;
		this.maxCount = maxCount;
	}

	@Override
	public NucleotideShortSequenceRepeatVariability withOnlyPredicted(boolean flag) {
		return new NucleotideShortSequenceRepeatVariability(flag, range, minCount, maxCount);
	}

	/** @return range of repeat */
	public NucleotideRange getRange() {
		return range;
	}

	/** @return lower bound on count, inclusive */
	public int getMinCount() {
		return minCount;
	}

	/** @return upper bound on count, inclusive */
	public int getMaxCount() {
		return maxCount;
	}

	@Override
	public String toHGVSString() {
		return wrapIfOnlyPredicted(Joiner.on("").join(range.toHGVSString(), "(", minCount, "_", maxCount, ")"));
	}

	@Override
	public String toString() {
		return "NucleotideShortSequenceRepeatVariability [range=" + range + ", minCount=" + minCount + ", maxCount="
				+ maxCount + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + maxCount;
		result = prime * result + minCount;
		result = prime * result + ((range == null) ? 0 : range.hashCode());
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
		NucleotideShortSequenceRepeatVariability other = (NucleotideShortSequenceRepeatVariability) obj;
		if (maxCount != other.maxCount)
			return false;
		if (minCount != other.minCount)
			return false;
		if (range == null) {
			if (other.range != null)
				return false;
		} else if (!range.equals(other.range))
			return false;
		return true;
	}

}
