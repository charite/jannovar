package de.charite.compbio.jannovar.hgvs.nts;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.ConvertibleToHGVSString;

// TODO(holtgrewe): coding location is missing the case for 3' and 5' UTR

/**
 * Position in a nucleotide string.
 *
 * Characterized by a 1-based position in the current coordinate system (e.g. CDS) with an offset into the exon/intron.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class NucleotidePointLocation implements ConvertibleToHGVSString {

	/** 0-based base position */
	final int basePos;
	/** 1-based offset into the "gaps" of the coordinate system */
	final int offset;

	public static NucleotidePointLocation build(int basePos, int offset) {
		return new NucleotidePointLocation(basePos, offset);
	}

	public static NucleotidePointLocation buildWithoutOffset(int basePos) {
		return new NucleotidePointLocation(basePos);
	}

	/**
	 * Construct with given base position.
	 *
	 * @param basePos
	 *            0-based position in the nucleotide string
	 */
	public NucleotidePointLocation(int basePos) {
		super();
		this.basePos = basePos;
		this.offset = 0;
	}

	/**
	 * Construct with given base position and offset
	 *
	 * @param basePos
	 *            0-based position in the nucleotide string
	 * @param offset
	 *            1-based offset (only non-0 values are allowed)
	 */
	public NucleotidePointLocation(int basePos, int offset) {
		super();
		this.basePos = basePos;
		this.offset = offset;
	}

	public int getBasePos() {
		return basePos;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public String toHGVSString() {
		if (offset == 0)
			return Integer.toString(this.basePos + 1);
		else if (offset > 0)
			return Joiner.on("").join(this.basePos + 1, "+", offset);
		else
			// if (offset < 0)
			return Joiner.on("").join(this.basePos + 1, "-", -offset);
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		return toHGVSString();
	}

	@Override
	public String toString() {
		return "NucleotidePosition [basePos=" + basePos + ", offset=" + offset + ", toString()=" + super.toString()
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + basePos;
		result = prime * result + offset;
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
		NucleotidePointLocation other = (NucleotidePointLocation) obj;
		if (basePos != other.basePos)
			return false;
		if (offset != other.offset)
			return false;
		return true;
	}

}
