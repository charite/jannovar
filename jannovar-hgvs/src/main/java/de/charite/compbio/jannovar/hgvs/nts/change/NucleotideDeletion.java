package de.charite.compbio.jannovar.hgvs.nts.change;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;

/**
 * Deletion in a nucleotide sequence.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class NucleotideDeletion extends NucleotideChange {

	/** deleted range of nucleotides */
	private final NucleotideRange range;
	/** description of the deleted nucleotide sequence */
	private final NucleotideSeqDescription seq;

	public static NucleotideDeletion build(boolean onlyPredicted, int firstPos, int firstPosOffset, int lastPos,
			int lastPosOffset, NucleotideSeqDescription seq) {
		return new NucleotideDeletion(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
				lastPosOffset), seq);
	}

	public static NucleotideDeletion buildWithSequence(boolean onlyPredicted, int firstPos, int firstPosOffset,
			int lastPos, int lastPosOffset, String nts) {
		return new NucleotideDeletion(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
				lastPosOffset), new NucleotideSeqDescription(nts));
	}

	public static NucleotideDeletion buildWithLength(boolean onlyPredicted, int firstPos, int firstPosOffset,
			int lastPos, int lastPosOffset, int seqLen) {
		return new NucleotideDeletion(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
				lastPosOffset), new NucleotideSeqDescription(seqLen));
	}

	public static NucleotideDeletion buildWithoutSeqDescription(boolean onlyPredicted, int firstPos,
			int firstPosOffset, int lastPos, int lastPosOffset) {
		return new NucleotideDeletion(onlyPredicted, NucleotideRange.build(firstPos, firstPosOffset, lastPos,
				lastPosOffset), new NucleotideSeqDescription());
	}

	public static NucleotideDeletion buildWithoutOffset(boolean onlyPredicted, int firstPos, int lastPos,
			NucleotideSeqDescription seq) {
		return new NucleotideDeletion(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos), seq);
	}

	// TODO(holtgrew): Homogenize names of static factory functions
	public static NucleotideDeletion buildWithoutOffsetWithSequence(boolean onlyPredicted, int firstPos, int lastPos,
			String nts) {
		return new NucleotideDeletion(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
				new NucleotideSeqDescription(nts));
	}

	public static NucleotideDeletion buildWithoutOffsetWithLength(boolean onlyPredicted, int firstPos, int lastPos,
			int seqLen) {
		return new NucleotideDeletion(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
				new NucleotideSeqDescription(seqLen));
	}

	public static NucleotideDeletion buildWithoutOffsetWithoutSeqDescription(boolean onlyPredicted, int firstPos,
			int lastPos) {
		return new NucleotideDeletion(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
				new NucleotideSeqDescription());
	}

	public NucleotideDeletion(boolean onlyPredicted, NucleotideRange range, NucleotideSeqDescription seq) {
		super(onlyPredicted);
		this.range = range;
		this.seq = seq;
	}

	public NucleotideRange getRange() {
		return range;
	}

	public NucleotideSeqDescription getSeq() {
		return seq;
	}

	@Override
	public String toHGVSString() {
		return wrapIfOnlyPredicted(Joiner.on("").join(range.toHGVSString(), "del", seq.toHGVSString()));
	}

	@Override
	public String toString() {
		return "NucleotideDeletion [range=" + range + ", seq=" + seq + ", toString()=" + super.toString() + "]";
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
		NucleotideDeletion other = (NucleotideDeletion) obj;
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
