package jannovar.reference;

import jannovar.Immutable;
import jannovar.impl.util.StringUtil;
import jannovar.io.ReferenceDictionary;

import java.io.Serializable;

/**
 * Representation of a genomic interval (chromsome, begin, end).
 *
 * Internally, positions are always stored zero-based, but the position type can be explicitely given to the constructor
 * of {@link GenomeInterval}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class GenomeInterval implements Serializable {

	private static final long serialVersionUID = 2L;

	/** reference dictionary to use for coordinate translation */
	final ReferenceDictionary refDict;

	/** the strand that the position is located on */
	public char strand;
	/** the chromosome number, as index in chromosome dictionary */
	public int chr;
	/** the begin position on the chromosome */
	public int beginPos;
	/** the end position on the chromosome */
	public int endPos;

	// TODO(holtgrew): Add back with zero-based coordinate system
	/** construct genome interval with zero-based coordinate system */
	// public GenomeInterval(ReferenceDictionary refDict, char strand, int chr, int beginPos, int endPos) {
	// this.refDict = refDict;
	// this.strand = strand;
	// this.positionType = PositionType.ONE_BASED;
	// this.chr = chr;
	// this.beginPos = beginPos;
	// this.endPos = endPos;
	// }

	/** construct genome interval with selected coordinate system */
	public GenomeInterval(ReferenceDictionary refDict, char strand, int chr, int beginPos, int endPos,
			PositionType positionType) {
		this.refDict = refDict;
		this.strand = strand;
		this.chr = chr;
		if (positionType == PositionType.ONE_BASED)
			this.beginPos = beginPos - 1;
		else
			this.beginPos = beginPos;
		this.endPos = endPos;
	}

	/** construct genome interval from other with selected coordinate system */
	public GenomeInterval(GenomeInterval other) {
		this.refDict = other.refDict;
		this.strand = other.strand;
		this.chr = other.chr;
		this.beginPos = other.beginPos;
		this.endPos = other.endPos;
	}

	/** construct genome interval from other with selected strand */
	public GenomeInterval(GenomeInterval other, char strand) {
		this.refDict = other.refDict;
		this.strand = strand;
		this.chr = other.chr;

		// transform coordinate system
		if (strand == other.strand) {
			this.beginPos = other.beginPos;
			this.endPos = other.endPos;
		} else {
			int beginPos = refDict.contigLength.get(other.chr) - other.beginPos;
			int endPos = refDict.contigLength.get(other.chr) - other.endPos;
			this.endPos = beginPos;
			this.beginPos = endPos;
		}
	}

	/** construct genome interval from {@link GenomePosition} with a length towards 3' of pos' coordinate system */
	public GenomeInterval(GenomePosition pos, int length) {
		this.refDict = pos.refDict;
		this.strand = pos.strand;
		this.chr = pos.chr;
		this.beginPos = pos.pos;

		this.endPos = pos.pos + length;
	}

	/** convert into GenomeInterval of the given strand */
	public GenomeInterval withStrand(char strand) {
		return new GenomeInterval(this, strand);
	}

	/** return the genome begin position */
	public GenomePosition getGenomeBeginPos() {
		return new GenomePosition(refDict, strand, chr, beginPos, PositionType.ZERO_BASED);
	}

	/** return the genome end position */
	public GenomePosition getGenomeEndPos() {
		return new GenomePosition(refDict, strand, chr, endPos, PositionType.ZERO_BASED);
	}

	/** returns length of the interval */
	public int length() {
		return this.endPos - this.beginPos;
	}

	/**
	 * @return GenomeInterval with intersection of <code>this</code> and <code>other</code>
	 */
	public GenomeInterval intersection(GenomeInterval other) {
		if (chr != other.chr)
			return new GenomeInterval(refDict, strand, chr, beginPos, beginPos, PositionType.ZERO_BASED);
		other = other.withStrand(strand);

		int beginPos = Math.max(this.beginPos, other.beginPos);
		int endPos = Math.min(this.endPos, other.endPos);
		if (endPos < beginPos)
			beginPos = endPos;

		return new GenomeInterval(refDict, strand, chr, beginPos, endPos, PositionType.ZERO_BASED);
	}

	/**
	 * @param pos
	 *            query position
	 * @return <tt>true</tt> if the interval is truly left of the position
	 */
	public boolean isLeftOf(GenomePosition pos) {
		if (chr != pos.chr)
			return false; // wrong chromosome
		if (pos.strand != strand)
			pos = pos.withStrand(strand); // ensure that we are on the correct strand
		return (pos.pos >= endPos);
	}

	/**
	 * @param pos
	 *            query position
	 * @return <tt>true</tt> if the interval is truly right of the position
	 */
	public boolean isRightOf(GenomePosition pos) {
		if (chr != pos.chr)
			return false; // wrong chromosome
		if (pos.strand != strand)
			pos = pos.withStrand(strand); // ensure that we are on the correct strand
		return (pos.pos < beginPos);
	}

	/**
	 * @param pos
	 *            query position
	 * @return <tt>true</tt> if the interval contains <tt>pos</tt>
	 */
	public boolean contains(GenomePosition pos) {
		if (chr != pos.chr)
			return false; // wrong chromosome
		if (pos.strand != strand)
			pos = pos.withStrand(strand); // ensure that we are on the correct strand
		return (pos.pos >= beginPos && pos.pos < endPos);
	}

	/**
	 * @param other
	 *            other {@link GenomeInterval} to use for querying
	 * @return <tt>true</tt> if the interval contains <code>other</code>
	 */
	public boolean contains(GenomeInterval other) {
		// TODO(holtgrem): Test this.
		if (chr != other.chr)
			return false; // wrong chromosome
		if (other.strand != strand)
			other = other.withStrand(strand); // ensure that we are on the correct strand
		return (other.beginPos >= beginPos && other.endPos <= endPos);
	}

	/**
	 * @return a {@link GenomeInterval} that has <code>padding</code> more bases towards each side as padding
	 */
	public GenomeInterval withMorePadding(int padding) {
		// TODO(holtgrem): throw when going outside of chromosome?
		return new GenomeInterval(refDict, strand, chr, beginPos - padding, endPos + padding, PositionType.ZERO_BASED);
	}

	/**
	 * @param other
	 *            other {@link GenomeInterval} to check with overlap for
	 * @return whether <code>other</code> overlaps with <code>this</code>
	 */
	public boolean overlapsWith(GenomeInterval other) {
		// TODO(holtgrem): add test for this
		if (chr != other.chr)
			return false;
		return (other.beginPos < endPos && beginPos < other.endPos);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (strand == '-')
			return withStrand('+').toString();

		return StringUtil.concatenate(refDict.contigName.get(chr), ":", beginPos + 1, "-", endPos);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (strand != '+')
			return withStrand('+').hashCode();

		final int prime = 31;
		int result = 1;
		result = prime * result + beginPos;
		result = prime * result + chr;
		result = prime * result + endPos;
		result = prime * result + strand;
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenomeInterval other = (GenomeInterval) obj;
		other = other.withStrand(strand);
		if (beginPos != other.beginPos)
			return false;
		if (chr != other.chr)
			return false;
		if (endPos != other.endPos)
			return false;
		if (strand != other.strand)
			return false;
		return true;
	}

}
