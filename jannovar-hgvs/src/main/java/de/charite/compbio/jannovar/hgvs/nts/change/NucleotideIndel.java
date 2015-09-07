package de.charite.compbio.jannovar.hgvs.nts.change;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;

public class NucleotideIndel extends NucleotideChange {

	/** range that is to deleted */
	private final NucleotideRange range;
	/** description of the to be deleted sequence */
	private final NucleotideSeqDescription delSeq;
	/** description of the to be inserted sequence */
	private final NucleotideSeqDescription insSeq;

	/** Build without any sequence description */
	public static NucleotideIndel buildWithOffsetWithoutSeqDescription(boolean onlyPredicted, int firstPos,
			int firstOffset, int lastPos, int lastOffset) {
		return new NucleotideIndel(onlyPredicted, NucleotideRange.build(firstPos, firstOffset, lastPos, lastOffset),
				new NucleotideSeqDescription(), new NucleotideSeqDescription());
	}

	/** Build with length information */
	public static NucleotideIndel buildWithOffsetWithLength(boolean onlyPredicted, int firstPos, int firstOffset,
			int lastPos, int lastOffset, int deletedLength, int insertedLength) {
		return new NucleotideIndel(onlyPredicted, NucleotideRange.build(firstPos, firstOffset, lastPos, lastOffset),
				new NucleotideSeqDescription(deletedLength), new NucleotideSeqDescription(insertedLength));
	}

	/** Build with sequence information */
	public static NucleotideIndel buildWithOffsetWithSequence(boolean onlyPredicted, int firstPos, int firstOffset,
			int lastPos, int lastOffset, String deletedSeq, String insertedSeq) {
		return new NucleotideIndel(onlyPredicted, NucleotideRange.build(firstPos, firstOffset, lastPos, lastOffset),
				new NucleotideSeqDescription(deletedSeq), new NucleotideSeqDescription(insertedSeq));
	}

	/** Build with sequence description */
	public static NucleotideIndel buildWithOffsetWithSeqDescription(boolean onlyPredicted, int firstPos,
			int firstOffset, int lastPos, int lastOffset, NucleotideSeqDescription delDesc,
			NucleotideSeqDescription insDesc) {
		return new NucleotideIndel(onlyPredicted, NucleotideRange.build(firstPos, firstOffset, lastPos, lastOffset),
				delDesc, insDesc);
	}

	/** Build without offset and any sequence description */
	public static NucleotideIndel buildWithoutSeqDescription(boolean onlyPredicted, int firstPos, int lastPos) {
		return new NucleotideIndel(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
				new NucleotideSeqDescription(), new NucleotideSeqDescription());
	}

	/** Build without offset and with length information */
	public static NucleotideIndel buildWithLength(boolean onlyPredicted, int firstPos, int lastPos, int deletedLength,
			int insertedLength) {
		return new NucleotideIndel(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
				new NucleotideSeqDescription(deletedLength), new NucleotideSeqDescription(insertedLength));
	}

	/** Build without offset and with sequence information */
	public static NucleotideIndel buildWithSequence(boolean onlyPredicted, int firstPos, int lastPos,
			String deletedSeq, String insertedSeq) {
		return new NucleotideIndel(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
				new NucleotideSeqDescription(deletedSeq), new NucleotideSeqDescription(insertedSeq));
	}

	/** Build without offset and with sequence description */
	public static NucleotideIndel buildWithSeqDescription(boolean onlyPredicted, int firstPos, int lastPos,
			NucleotideSeqDescription delDesc, NucleotideSeqDescription insDesc) {
		return new NucleotideIndel(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos), delDesc,
				insDesc);
	}

	public NucleotideIndel(boolean onlyPredicted, NucleotideRange range, NucleotideSeqDescription delSeq,
			NucleotideSeqDescription insSeq) {
		super(onlyPredicted);
		this.range = range;
		this.delSeq = delSeq;
		this.insSeq = insSeq;
	}

	@Override
	public NucleotideIndel withOnlyPredicted(boolean flag) {
		return new NucleotideIndel(flag, range, delSeq, insSeq);
	}

	@Override
	public String toHGVSString() {
		String open = isOnlyPredicted() ? "(" : "";
		String close = isOnlyPredicted() ? ")" : "";
		return Joiner.on("").skipNulls()
				.join(open, range.toHGVSString(), "del", delSeq.toHGVSString(), "ins", insSeq.toHGVSString(), close);
	}

	@Override
	public String toString() {
		return "NucleotideIndel [range=" + range + ", delSeq=" + delSeq + ", insSeq=" + insSeq + "]";
	}

	public NucleotideRange getRange() {
		return range;
	}

	public NucleotideSeqDescription getInsSeq() {
		return insSeq;
	}

	public NucleotideSeqDescription getDelSeq() {
		return delSeq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((delSeq == null) ? 0 : delSeq.hashCode());
		result = prime * result + ((insSeq == null) ? 0 : insSeq.hashCode());
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
		NucleotideIndel other = (NucleotideIndel) obj;
		if (delSeq == null) {
			if (other.delSeq != null)
				return false;
		} else if (!delSeq.equals(other.delSeq))
			return false;
		if (insSeq == null) {
			if (other.insSeq != null)
				return false;
		} else if (!insSeq.equals(other.insSeq))
			return false;
		if (range == null) {
			if (other.range != null)
				return false;
		} else if (!range.equals(other.range))
			return false;
		return true;
	}
}
