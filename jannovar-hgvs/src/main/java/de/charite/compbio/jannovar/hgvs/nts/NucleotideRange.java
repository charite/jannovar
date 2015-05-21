package de.charite.compbio.jannovar.hgvs.nts;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.ConvertibleToHGVSString;

// TODO(holtgrew): allowing "?" for unclear positions and offsets?

/**
 * Range in a nucleotide sequence.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class NucleotideRange implements ConvertibleToHGVSString {

	/** position of the first base */
	private final NucleotidePointLocation firstPos;
	/** position of the last base */
	private final NucleotidePointLocation lastPos;

	public static NucleotideRange build(int firstPos, int firstPosOffset, int lastPos, int lastPosOffset) {
		return new NucleotideRange(NucleotidePointLocation.buildWithOffset(firstPos, firstPosOffset),
				NucleotidePointLocation.buildWithOffset(lastPos, lastPosOffset));
	}

	public static NucleotideRange buildWithoutOffset(int firstPos, int lastPos) {
		return new NucleotideRange(NucleotidePointLocation.build(firstPos), NucleotidePointLocation.build(lastPos));
	}

	/**
	 * @param firstPos
	 * @param lastPost
	 */
	public NucleotideRange(NucleotidePointLocation firstPos, NucleotidePointLocation lastPost) {
		super();
		this.firstPos = firstPos;
		this.lastPos = lastPost;
	}

	public NucleotidePointLocation getFirstPos() {
		return firstPos;
	}

	public NucleotidePointLocation getLastPos() {
		return lastPos;
	}

	@Override
	public String toHGVSString() {
		if (firstPos.equals(lastPos))
			return firstPos.toHGVSString();
		else
			return Joiner.on("").join(firstPos.toHGVSString(), "_", lastPos.toHGVSString());
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		return toHGVSString();
	}

	@Override
	public String toString() {
		return "NucleotideRange [firstPos=" + firstPos + ", lastPos=" + lastPos + ", toString()=" + super.toString()
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstPos == null) ? 0 : firstPos.hashCode());
		result = prime * result + ((lastPos == null) ? 0 : lastPos.hashCode());
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
		NucleotideRange other = (NucleotideRange) obj;
		if (firstPos == null) {
			if (other.firstPos != null)
				return false;
		} else if (!firstPos.equals(other.firstPos))
			return false;
		if (lastPos == null) {
			if (other.lastPos != null)
				return false;
		} else if (!lastPos.equals(other.lastPos))
			return false;
		return true;
	}

}
