package de.charite.compbio.jannovar.hgvs.protein.change;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinRange;

/**
 * Describes short sequence repeat (SSR) variability.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ProteinShortSequenceRepeatVariability extends ProteinChange {

	/** range in the protein that has variable length */
	private final ProteinRange range;
	/** lower bound on length, inclusive */
	private final int minCount;
	/** upper bound on length, inclusive */
	private final int maxCount;

	/** Factory forwards to {@link #build(boolean, ProteinRange, int, int)} */
	public static ProteinShortSequenceRepeatVariability build(boolean onlyPredicted, String firstAA, int firstPos,
			String lastAA, int lastPos, int minCount, int maxCount) {
		return new ProteinShortSequenceRepeatVariability(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA,
				lastPos), minCount, maxCount);
	}

	/** Factory forwards to {@link #ProteinShortSequenceRepeatVariability(boolean, ProteinRange, int, int)} */
	public static ProteinShortSequenceRepeatVariability build(boolean onlyPredicted, ProteinRange range, int minCount,
			int maxCount) {
		return new ProteinShortSequenceRepeatVariability(onlyPredicted, range, minCount, maxCount);
	}

	/** Construct with the given values */
	public ProteinShortSequenceRepeatVariability(boolean onlyPredicted, ProteinRange range, int minCount, int maxCount) {
		super(onlyPredicted);
		this.range = range;
		this.minCount = minCount;
		this.maxCount = maxCount;
	}

	/** @return range of repeat */
	public ProteinRange getRange() {
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
	public String toHGVSString(AminoAcidCode code) {
		return wrapIfOnlyPredicted(Joiner.on("").join(range.toHGVSString(code), "(", minCount, "_", maxCount, ")"));
	}

	@Override
	public String toString() {
		return "ProteinShortSequenceRepeatVariability [range=" + range + ", minCount=" + minCount + ", maxCount="
				+ maxCount + ", toString()=" + super.toString() + "]";
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
		ProteinShortSequenceRepeatVariability other = (ProteinShortSequenceRepeatVariability) obj;
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

	@Override
	public ProteinChange withOnlyPredicted(boolean onlyPredicted) {
		return new ProteinShortSequenceRepeatVariability(onlyPredicted, this.range, this.minCount, this.maxCount);
	}

}
