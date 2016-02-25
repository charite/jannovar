package de.charite.compbio.jannovar.hgvs.protein.change;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinRange;
import de.charite.compbio.jannovar.hgvs.protein.ProteinSeqDescription;

/**
 * In-frame substitution on the protein level with more than one base.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ProteinIndel extends ProteinChange {

	/** range that is to deleted */
	private final ProteinRange range;
	/** description of the to be deleted sequence */
	private final ProteinSeqDescription delSeq;
	/** description of the to be inserted sequence */
	private final ProteinSeqDescription insSeq;

	/** Build without any sequence description */
	public static ProteinIndel buildWithoutSeqDescription(boolean onlyPredicted, String firstAA, int firstPos,
			String lastAA, int lastPos) {
		return new ProteinIndel(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos),
				new ProteinSeqDescription(), new ProteinSeqDescription());
	}

	/** Build with length information */
	public static ProteinIndel buildWithLength(boolean onlyPredicted, String firstAA, int firstPos, String lastAA,
			int lastPos, int deletedLength, int insertedLength) {
		return new ProteinIndel(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos),
				new ProteinSeqDescription(deletedLength), new ProteinSeqDescription(insertedLength));
	}

	/** Build with sequence information */
	public static ProteinIndel buildWithSequence(boolean onlyPredicted, String firstAA, int firstPos, String lastAA,
			int lastPos, String deletedSeq, String insertedSeq) {
		return new ProteinIndel(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos),
				new ProteinSeqDescription(deletedSeq), new ProteinSeqDescription(insertedSeq));
	}

	/** Build with sequence description */
	public static ProteinIndel buildWithSeqDescription(boolean onlyPredicted, String firstAA, int firstPos,
			String lastAA, int lastPos, ProteinSeqDescription delDesc, ProteinSeqDescription insDesc) {
		return new ProteinIndel(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos), delDesc, insDesc);
	}

	public ProteinIndel(boolean onlyPredicted, ProteinRange range, ProteinSeqDescription delSeq,
			ProteinSeqDescription insSeq) {
		super(onlyPredicted);
		this.range = range;
		this.delSeq = delSeq;
		this.insSeq = insSeq;
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		String open = isOnlyPredicted() ? "(" : "";
		String close = isOnlyPredicted() ? ")" : "";
		return Joiner
				.on("")
				.skipNulls()
				.join(open, range.toHGVSString(code), "del", delSeq.toHGVSString(code), "ins",
						insSeq.toHGVSString(code), close);
	}

	@Override
	public String toString() {
		return "ProteinIndel [range=" + range + ", delSeq=" + delSeq + ", insSeq=" + insSeq + "]";
	}

	public ProteinRange getRange() {
		return range;
	}

	public ProteinSeqDescription getSeq() {
		return insSeq;
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
		ProteinIndel other = (ProteinIndel) obj;
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

	@Override
	public ProteinChange withOnlyPredicted(boolean onlyPredicted) {
		return new ProteinIndel(onlyPredicted, this.range, this.delSeq, this.insSeq);
	}

}
