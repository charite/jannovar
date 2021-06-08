package de.charite.compbio.jannovar.hgvs.nts.change;

import com.google.common.base.Joiner;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;

/**
 * Unchanged in a nucleotide sequence.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class NucleotideUnchanged extends NucleotideChange {

	/**
	 * unchanged range of nucleotides
	 */
	private final NucleotideRange range;
	/**
	 * description of the deleted nucleotide sequence
	 */
	private final NucleotideSeqDescription seq;

	public static NucleotideUnchanged buildWithOffset(boolean onlyPredicted, int firstPos, int firstPosOffset,
                                                      int lastPos, int lastPosOffset, NucleotideSeqDescription seq) {
		return new NucleotideUnchanged(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
			lastPosOffset), seq);
	}

	public static NucleotideUnchanged buildWithOffsetWithSequence(boolean onlyPredicted, int firstPos,
                                                                  int firstPosOffset, int lastPos, int lastPosOffset, String nts) {
		return new NucleotideUnchanged(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
			lastPosOffset), new NucleotideSeqDescription(nts));
	}

	public static NucleotideUnchanged buildWithOffsetWithLength(boolean onlyPredicted, int firstPos, int firstPosOffset,
                                                                int lastPos, int lastPosOffset, int seqLen) {
		return new NucleotideUnchanged(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
			lastPosOffset), new NucleotideSeqDescription(seqLen));
	}

	public static NucleotideUnchanged buildWithOffsetWithoutSeqDescription(boolean onlyPredicted, int firstPos,
                                                                           int firstPosOffset, int lastPos, int lastPosOffset) {
		return new NucleotideUnchanged(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
			lastPosOffset), new NucleotideSeqDescription());
	}

	public static NucleotideUnchanged build(boolean onlyPredicted, int firstPos, int lastPos,
                                            NucleotideSeqDescription seq) {
		return new NucleotideUnchanged(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos), seq);
	}

	public static NucleotideUnchanged buildWithSequence(boolean onlyPredicted, int firstPos, int lastPos, String nts) {
		return new NucleotideUnchanged(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
			new NucleotideSeqDescription(nts));
	}

	public static NucleotideUnchanged buildWithLength(boolean onlyPredicted, int firstPos, int lastPos, int seqLen) {
		return new NucleotideUnchanged(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
			new NucleotideSeqDescription(seqLen));
	}

	public static NucleotideUnchanged buildWithoutSeqDescription(boolean onlyPredicted, int firstPos, int lastPos) {
		return new NucleotideUnchanged(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
			new NucleotideSeqDescription());
	}

	public NucleotideUnchanged(boolean onlyPredicted, NucleotideRange range, NucleotideSeqDescription seq) {
		super(onlyPredicted);
		this.range = range;
		this.seq = seq;
	}

	@Override
	public NucleotideUnchanged withOnlyPredicted(boolean flag) {
		return new NucleotideUnchanged(flag, range, seq);
	}

	public NucleotideRange getRange() {
		return range;
	}

	public NucleotideSeqDescription getSeq() {
		return seq;
	}

	@Override
	public String toHGVSString() {
		return wrapIfOnlyPredicted(Joiner.on("").join(range.toHGVSString(), seq.toHGVSString(), "="));
	}

	@Override
	public String toString() {
		return "NucleotideUnchanged [range=" + range + ", seq=" + seq + "]";
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
		NucleotideUnchanged other = (NucleotideUnchanged) obj;
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
