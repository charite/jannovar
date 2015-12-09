package de.charite.compbio.jannovar.hgvs.nts.change;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;

/**
 * Insertion into a Nucleotide sequence.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class NucleotideInsertion extends NucleotideChange {

	/** range of length one giving the insertion location */
	private final NucleotideRange range;
	/** specification of the inserted Nucleotide sequence */
	private final NucleotideSeqDescription seq;

	/** Build without any sequence description. */
	public static NucleotideInsertion buildWithOffsetWithoutSeqDescription(boolean onlyPredicted, int firstPos,
			int firstOffset, int lastPos, int lastOffset) {
		return new NucleotideInsertion(onlyPredicted,
				NucleotideRange.build(firstPos, firstOffset, lastPos, lastOffset), new NucleotideSeqDescription());
	}

	/** Build with length information */
	public static NucleotideInsertion buildWithOffsetWithLength(boolean onlyPredicted, int firstPos, int firstOffset,
			int lastPos, int lastOffset, int insertedLength) {
		return new NucleotideInsertion(onlyPredicted,
				NucleotideRange.build(firstPos, firstOffset, lastPos, lastOffset), new NucleotideSeqDescription(
						insertedLength));
	}

	/** Build with sequence */
	public static NucleotideInsertion buildWithOffsetWithSequence(boolean onlyPredicted, int firstPos, int firstOffset,
			int lastPos, int lastOffset, String seq) {
		return new NucleotideInsertion(onlyPredicted,
				NucleotideRange.build(firstPos, firstOffset, lastPos, lastOffset), new NucleotideSeqDescription(seq));
	}

	/** Build without offset and no sequence description. */
	public static NucleotideInsertion buildWithLength(boolean onlyPredicted, int firstPos, int lastPos) {
		return new NucleotideInsertion(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
				new NucleotideSeqDescription());
	}

	/** Build without offset and with length information */
	public static NucleotideInsertion buildWithLength(boolean onlyPredicted, int firstPos, int lastPos,
			int insertedLength) {
		return new NucleotideInsertion(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
				new NucleotideSeqDescription(insertedLength));
	}

	/** Build without offset and with sequence */
	public static NucleotideInsertion buildWithSequence(boolean onlyPredicted, int firstPos, int lastPos, String seq) {
		return new NucleotideInsertion(onlyPredicted, NucleotideRange.buildWithoutOffset(firstPos, lastPos),
				new NucleotideSeqDescription(seq));
	}

	/**
	 * @param onlyPredicted
	 *            whether the change was only predicted
	 * @param position
	 *            range of length one giving the insertion position
	 * @param seq
	 *            description of the inserted sequence
	 */
	public NucleotideInsertion(boolean onlyPredicted, NucleotideRange position, NucleotideSeqDescription seq) {
		super(onlyPredicted);

		// if (position.length() != 2)
		// throw new IllegalArgumentException("range describing insertion must have size 2 but was "
		// + position.length() + " range is " + position);

		this.range = position;
		this.seq = seq;
	}

	/** @return range of length 1 with the two bases between which the sequence is inserted */
	public NucleotideRange getRange() {
		return range;
	}

	/** @return inserted sequenced */
	public NucleotideSeqDescription getSeq() {
		return seq;
	}

	@Override
	public NucleotideInsertion withOnlyPredicted(boolean flag) {
		return new NucleotideInsertion(flag, range, seq);
	}

	@Override
	public String toHGVSString() {
		return wrapIfOnlyPredicted(range.toHGVSString() + "ins" + seq.toHGVSString());
	}

	@Override
	public String toString() {
		return "NucleotideInsertion [position=" + range + ", seq=" + seq + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((range == null) ? 0 : range.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		NucleotideInsertion other = (NucleotideInsertion) obj;
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
