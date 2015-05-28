package de.charite.compbio.jannovar.hgvs.protein.change;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinRange;
import de.charite.compbio.jannovar.hgvs.protein.ProteinSeqDescription;

/**
 * Insertion into a protein sequence.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class ProteinInsertion extends ProteinChange {

	/** range of length one giving the insertion location */
	private final ProteinRange position;
	/** specification of the inserted protein sequence */
	private final ProteinSeqDescription seq;

	/** Build without any sequence description. */
	public static ProteinInsertion buildWithLength(boolean onlyPredicted, String firstAA, int firstPos, String lastAA,
			int lastPos) {
		return new ProteinInsertion(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos),
				new ProteinSeqDescription());
	}

	/** Build with length information */
	public static ProteinInsertion buildWithLength(boolean onlyPredicted, String firstAA, int firstPos, String lastAA,
			int lastPos, int insertedLength) {
		return new ProteinInsertion(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos),
				new ProteinSeqDescription(insertedLength));
	}

	/** Build with sequence */
	public static ProteinInsertion buildWithSequence(boolean onlyPredicted, String firstAA, int firstPos,
			String lastAA, int lastPos, String seq) {
		return new ProteinInsertion(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos),
				new ProteinSeqDescription(seq));
	}

	/** Build with SeqDescription */
	public static ProteinInsertion buildWithSeqDescription(boolean onlyPredicted, String firstAA, int firstPos,
			String lastAA, int lastPos, ProteinSeqDescription seqDescription) {
		return new ProteinInsertion(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos),
				seqDescription);
	}

	/** Build without any SeqDescription */
	public static ProteinInsertion buildWithoutSeqDescription(boolean onlyPredicted, String firstAA, int firstPos,
			String lastAA, int lastPos) {
		return new ProteinInsertion(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos),
				new ProteinSeqDescription());
	}

	/**
	 * @param onlyPredicted
	 *            whether the change was only predicted
	 * @param position
	 *            range of length one giving the insertion position
	 * @param seq
	 *            description of the inserted sequence
	 */
	public ProteinInsertion(boolean onlyPredicted, ProteinRange position, ProteinSeqDescription seq) {
		super(onlyPredicted);

		// if (position.length() != 2)
		// throw new IllegalArgumentException("range describing insertion must have size 2 but was "
		// + position.length() + " range is " + position);

		this.position = position;
		this.seq = seq;
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		return wrapIfOnlyPredicted(position.toHGVSString(code) + "ins" + seq.toHGVSString(code));
	}

	@Override
	public String toString() {
		return "ProteinInsertion [position=" + position + ", seq=" + seq + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
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
		ProteinInsertion other = (ProteinInsertion) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (seq == null) {
			if (other.seq != null)
				return false;
		} else if (!seq.equals(other.seq))
			return false;
		return true;
	}

}
