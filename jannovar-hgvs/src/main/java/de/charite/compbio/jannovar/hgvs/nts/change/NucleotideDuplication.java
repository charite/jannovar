package de.charite.compbio.jannovar.hgvs.nts.change;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;

/**
 * Representation of a duplication on the nucleotide level.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class NucleotideDuplication extends NucleotideChange {

	/** deleted range of nucleotides */
	private final NucleotideRange range;
	/** description of the deleted nucleotide sequence */
	private final NucleotideSeqDescription seq;

	public static NucleotideDuplication buildWithOffset(boolean onlyPredicted, int firstPos, int firstPosOffset,
			int lastPos, int lastPosOffset, NucleotideSeqDescription seq) {
		return new NucleotideDuplication(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
				lastPosOffset), seq);
	}

	public static NucleotideDuplication buildWithOffsetWithSequence(boolean onlyPredicted, int firstPos,
			int firstPosOffset, int lastPos, int lastPosOffset, String nts) {
		return new NucleotideDuplication(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
				lastPosOffset), new NucleotideSeqDescription(nts));
	}

	public static NucleotideDuplication buildWithOffsetWithLength(boolean onlyPredicted, int firstPos,
			int firstPosOffset, int lastPos, int lastPosOffset, int seqLen) {
		return new NucleotideDuplication(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
				lastPosOffset), new NucleotideSeqDescription(seqLen));
	}

	public static NucleotideDuplication buildWithOffsetWithoutSeqDescription(boolean onlyPredicted, int firstPos,
			int firstPosOffset, int lastPos, int lastPosOffset) {
		return new NucleotideDuplication(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
				lastPosOffset), new NucleotideSeqDescription());
	}

	public static NucleotideDuplication build(boolean onlyPredicted, int firstPos, int lastPos,
			NucleotideSeqDescription seq) {
		return new NucleotideDuplication(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos), seq);
	}

	public static NucleotideDuplication buildWithSequence(boolean onlyPredicted, int firstPos, int lastPos, String nts) {
		return new NucleotideDuplication(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
				new NucleotideSeqDescription(nts));
	}

	public static NucleotideDuplication buildWithLength(boolean onlyPredicted, int firstPos, int lastPos, int seqLen) {
		return new NucleotideDuplication(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
				new NucleotideSeqDescription(seqLen));
	}

	public static NucleotideDuplication buildWithoutSeqDescription(boolean onlyPredicted, int firstPos, int lastPos) {
		return new NucleotideDuplication(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
				new NucleotideSeqDescription());
	}

	public NucleotideDuplication(boolean onlyPredicted, NucleotideRange range, NucleotideSeqDescription seq) {
		super(onlyPredicted);
		this.range = range;
		this.seq = seq;
	}

	@Override
	public NucleotideDuplication withOnlyPredicted(boolean flag) {
		return new NucleotideDuplication(flag, range, seq);
	}

	public NucleotideRange getRange() {
		return range;
	}

	public NucleotideSeqDescription getSeq() {
		return seq;
	}

	@Override
	public String toHGVSString() {
		return wrapIfOnlyPredicted(Joiner.on("").join(range.toHGVSString(), "dup", seq.toHGVSString()));
	}

	@Override
	public String toString() {
		return "NucleotideDuplication [range=" + range + ", seq=" + seq + "]";
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
		NucleotideDuplication other = (NucleotideDuplication) obj;
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
