package de.charite.compbio.jannovar.hgvs.nts;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.ConvertibleToHGVSString;

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
	/**
	 * <code>true</code> if the position if downstream of the CDS end (3' UTR), use negative {@link #basePos} for 5'
	 * UTR.
	 */
	final boolean downstreamOfCDS;

	public static NucleotidePointLocation build(int basePos) {
		return new NucleotidePointLocation(basePos, 0, false);
	}

	public static NucleotidePointLocation buildWithOffset(int basePos, int offset) {
		return new NucleotidePointLocation(basePos, offset, false);
	}

	public static NucleotidePointLocation buildDownstreamOfCDS(int basePos) {
		return new NucleotidePointLocation(basePos, 0, true);
	}

	/**
	 * Construct with given base position.
	 *
	 * @param basePos
	 *            0-based position in the nucleotide string
	 */
	@Deprecated
	// in favour of full constructor
	public NucleotidePointLocation(int basePos) {
		super();
		this.basePos = basePos;
		this.offset = 0;
		this.downstreamOfCDS = false;
	}

	/**
	 * Construct with given base position and offset
	 *
	 * @param basePos
	 *            0-based position in the nucleotide string
	 * @param offset
	 *            1-based offset (only non-0 values are allowed)
	 * @param downstreamOfCDS
	 *            <code>true</code> if downstream of CDS
	 */
	public NucleotidePointLocation(int basePos, int offset, boolean downstreamOfCDS) {
		super();
		this.basePos = basePos;
		this.offset = offset;
		this.downstreamOfCDS = downstreamOfCDS;
	}

	public int getBasePos() {
		return basePos;
	}

	public int getOffset() {
		return offset;
	}

	public boolean isDownstreamOfCDS() {
		return downstreamOfCDS;
	}

	@Override
	public String toHGVSString() {
		final int shift = (this.basePos >= 0) ? 1 : 0;
		final String prefix = downstreamOfCDS ? "*" : "";

		if (offset == 0)
			return prefix + Integer.toString(this.basePos + shift);
		else if (offset > 0)
			return prefix + Joiner.on("").join(this.basePos + shift, "+", offset);
		else
			// if (offset < 0)
			return prefix + Joiner.on("").join(this.basePos + shift, "-", -offset);
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		return toHGVSString();
	}

	@Override
	public String toString() {
		return "NucleotidePointLocation [basePos=" + basePos + ", offset=" + offset + ", downstreamOfCDS="
				+ downstreamOfCDS + ", toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + basePos;
		result = prime * result + (downstreamOfCDS ? 1231 : 1237);
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
		if (downstreamOfCDS != other.downstreamOfCDS)
			return false;
		if (offset != other.offset)
			return false;
		return true;
	}

}
