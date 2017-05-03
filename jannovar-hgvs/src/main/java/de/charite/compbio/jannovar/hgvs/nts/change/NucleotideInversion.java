package de.charite.compbio.jannovar.hgvs.nts.change;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;

public class NucleotideInversion extends NucleotideChange {

	/** deleted range of nucleotides */
	private final NucleotideRange range;
	/** description of the inverted nucleotide sequence */
	private final NucleotideSeqDescription seq;

	public static NucleotideInversion buildWithOffset(boolean onlyPredicted, int firstPos, int firstPosOffset,
			int lastPos, int lastPosOffset, NucleotideSeqDescription seq) {
		return new NucleotideInversion(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
				lastPosOffset), seq);
	}

	public static NucleotideInversion buildWithOffsetWithSequence(boolean onlyPredicted, int firstPos,
			int firstPosOffset, int lastPos, int lastPosOffset, String nts) {
		return new NucleotideInversion(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
				lastPosOffset), new NucleotideSeqDescription(nts));
	}

	public static NucleotideInversion buildWithOffsetWithLength(boolean onlyPredicted, int firstPos,
			int firstPosOffset, int lastPos, int lastPosOffset, int seqLen) {
		return new NucleotideInversion(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
				lastPosOffset), new NucleotideSeqDescription(seqLen));
	}

	public static NucleotideInversion buildWithOffsetWithoutSeqDescription(boolean onlyPredicted, int firstPos,
			int firstPosOffset, int lastPos, int lastPosOffset) {
		return new NucleotideInversion(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
				lastPosOffset), new NucleotideSeqDescription());
	}

	public static NucleotideInversion build(boolean onlyPredicted, int firstPos, int lastPos,
			NucleotideSeqDescription seq) {
		return new NucleotideInversion(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos), seq);
	}

	public static NucleotideInversion buildWithSequence(boolean onlyPredicted, int firstPos, int lastPos, String nts) {
		return new NucleotideInversion(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
				new NucleotideSeqDescription(nts));
	}

	public static NucleotideInversion buildWithLength(boolean onlyPredicted, int firstPos, int lastPos, int seqLen) {
		return new NucleotideInversion(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
				new NucleotideSeqDescription(seqLen));
	}

	public static NucleotideInversion buildWithoutSeqDescription(boolean onlyPredicted, int firstPos, int lastPos) {
		return new NucleotideInversion(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
				new NucleotideSeqDescription());
	}

	public NucleotideInversion(boolean onlyPredicted, NucleotideRange range, NucleotideSeqDescription seq) {
		super(onlyPredicted);
		this.range = range;
		this.seq = seq;
	}

	@Override
	public NucleotideInversion withOnlyPredicted(boolean flag) {
		return new NucleotideInversion(flag, range, seq);
	}

	public NucleotideRange getRange() {
		return range;
	}

	public NucleotideSeqDescription getSeq() {
		return seq;
	}

	@Override
	public String toHGVSString() {
		return wrapIfOnlyPredicted(Joiner.on("").join(range.toHGVSString(), "inv", seq.toHGVSString()));
	}

	@Override
	public String toString() {
		return "NucleotideInversion [range=" + range + ", seq=" + seq + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((range == null) ? 0 : range.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		NucleotideInversion other = (NucleotideInversion) obj;
		if (range == null) {
			if (other.range != null)
				return false;
		} else if (!range.equals(other.range))
			return false;
		if (seq == null) {
			if (other.seq != null)
				return false;
		} else if (!seq.equals(other.seq))
			return false;
		return true;
	}
}
