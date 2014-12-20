package jannovar.reference;

import jannovar.common.ChromosomeMap;
import jannovar.common.Immutable;

import java.io.Serializable;

/**
 * Representation of a genomic interval (chromsome, begin, end) with explicit coordinate system (0-/1-based)
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class GenomeInterval implements Serializable {

	private static final long serialVersionUID = 1L;

	/** the used position type */
	public final PositionType positionType;
	/** the strand that the position is located on */
	public char strand;
	/** the chromosome number, as index in chromosome dictionary */
	public int chr;
	/** the begin position on the chromosome */
	public int beginPos;
	/** the end position on the chromosome */
	public int endPos;

	/** construct genome interval with one-based coordinate system */
	public GenomeInterval(char strand, int chr, int beginPos, int endPos) {
		this.strand = strand;
		this.positionType = PositionType.ONE_BASED;
		this.chr = chr;
		this.beginPos = beginPos;
		this.endPos = endPos;
	}

	/** construct genome interval with selected coordinate system */
	public GenomeInterval(char strand, int chr, int beginPos, int endPos, PositionType positionType) {
		this.strand = strand;
		this.positionType = positionType;
		this.chr = chr;
		this.beginPos = beginPos;
		this.endPos = endPos;
	}

	/** construct genome interval from other with selected coordinate system */
	public GenomeInterval(GenomeInterval other, PositionType positionType) {
		this.strand = other.strand;
		this.positionType = positionType;
		this.chr = other.chr;
		this.beginPos = other.beginPos;
		this.endPos = other.endPos;

		// transform coordinate system
		if (other.positionType == PositionType.ZERO_BASED && this.positionType == PositionType.ONE_BASED)
			this.beginPos += 1;
		else if (other.positionType == PositionType.ONE_BASED && this.positionType == PositionType.ZERO_BASED)
			this.beginPos -= 1;
	}

	/** construct genome interval from other with selected strand */
	public GenomeInterval(GenomeInterval other, char strand) {
		this.positionType = other.positionType;
		this.strand = strand;
		this.chr = other.chr;

		// transform coordinate system
		int delta = (positionType == PositionType.ONE_BASED) ? 1 : 0;
		if (strand == other.strand) {
			this.beginPos = other.beginPos;
			this.endPos = other.endPos;
		} else {
			int beginPos = ChromosomeMap.chromosomLength.get((byte) other.chr) - other.beginPos + delta;
			int endPos = ChromosomeMap.chromosomLength.get((byte) other.chr) - other.endPos + delta;
			this.endPos = beginPos;
			this.beginPos = endPos;
		}
	}

	/** construct genome interval from {@link GenomePosition} with a length towards 3' of pos' coordinate system */
	public GenomeInterval(GenomePosition pos, int length) {
		this.positionType = pos.positionType;
		this.strand = pos.strand;
		this.chr = pos.chr;
		this.beginPos = pos.pos;

		int delta = (positionType == PositionType.ZERO_BASED) ? 0 : -1;
		this.endPos = pos.pos + length + delta;
	}

	/** convert into GenomeInterval of the given strand */
	public GenomeInterval withStrand(char strand) {
		return new GenomeInterval(this, strand);
	}

	/** convert into GenomeInterval with the given position type */
	public GenomeInterval withPositionType(PositionType positionType) {
		return new GenomeInterval(this, positionType);
	}

	/** return the genome begin position */
	public GenomePosition getGenomeBeginPos() {
		return new GenomePosition(strand, chr, beginPos, positionType);
	}

	/** return the genome end position */
	public GenomePosition getGenomeEndPos() {
		return new GenomePosition(strand, chr, endPos, positionType);
	}

	/** returns length of the interval */
	public int length() {
		return this.endPos - this.beginPos + (positionType == PositionType.ONE_BASED ? 1 : 0);
	}

	/**
	 * @return GenomeInterval with intersection of <code>this</code> and <code>other</code>
	 */
	public GenomeInterval intersection(GenomeInterval other) {
		if (chr != other.chr)
			return new GenomeInterval(strand, chr, beginPos, beginPos, PositionType.ZERO_BASED);
		other = other.withStrand(strand).withPositionType(PositionType.ZERO_BASED);
		GenomeInterval me = withPositionType(PositionType.ZERO_BASED);

		int beginPos = Math.max(me.beginPos, other.beginPos);
		int endPos = Math.min(me.endPos, other.endPos);
		if (endPos < beginPos)
			beginPos = endPos;

		return new GenomeInterval(me.strand, me.chr, beginPos, endPos, PositionType.ZERO_BASED);
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
		if (pos.positionType != positionType)
			pos = pos.withPositionType(positionType);
		if (positionType == PositionType.ONE_BASED)
			return (pos.pos > endPos);
		else
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
		if (pos.positionType != positionType)
			pos = pos.withPositionType(positionType);
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
		if (pos.positionType != positionType)
			pos = pos.withPositionType(positionType);
		if (positionType == PositionType.ONE_BASED)
			return (pos.pos >= beginPos && pos.pos <= endPos);
		else
			return (pos.pos >= beginPos && pos.pos < endPos);
	}

	/**
	 * @param pos
	 *            query position
	 * @return <tt>true</tt> if the interval contains <code>other</code>
	 */
	public boolean contains(GenomeInterval other) {
		// TODO(holtgrem): Test this.
		if (chr != other.chr)
			return false; // wrong chromosome
		if (other.strand != strand)
			other = other.withStrand(strand); // ensure that we are on the correct strand
		if (other.positionType != positionType)
			other = other.withPositionType(positionType);
		return (other.beginPos >= beginPos && other.endPos <= endPos);
	}

	/**
	 * @return a {@link GenomeInterval} that has <code>padding</code> more bases towards each side as padding
	 */
	public GenomeInterval withMorePadding(int padding) {
		// TODO(holtgrem): throw when going outside of chromosome?
		return new GenomeInterval(strand, chr, beginPos - padding, endPos + padding, positionType);
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
		GenomeInterval thisZero = withPositionType(PositionType.ZERO_BASED);
		GenomeInterval otherZero = other.withStrand(strand).withPositionType(PositionType.ZERO_BASED);
		return (otherZero.beginPos < thisZero.endPos && this.beginPos < other.endPos);
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

		// TODO(holtgrem): Update once we have better chromosome id to reference name mapping.
		int beginPos = this.beginPos + (positionType == PositionType.ZERO_BASED ? 1 : 0);
		return String.format("chr%d:%d-%d", chr, beginPos, this.endPos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (this.positionType == PositionType.ONE_BASED || strand != '+')
			return withPositionType(PositionType.ZERO_BASED).withStrand('+').hashCode();

		final int prime = 31;
		int result = 1;
		result = prime * result + beginPos;
		result = prime * result + chr;
		result = prime * result + endPos;
		result = prime * result + ((positionType == null) ? 0 : positionType.hashCode());
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
		if (positionType == PositionType.ONE_BASED)
			return this.withPositionType(PositionType.ZERO_BASED).equals(obj);
		GenomeInterval other = (GenomeInterval) obj;
		other = other.withPositionType(PositionType.ZERO_BASED).withStrand(strand);
		if (beginPos != other.beginPos)
			return false;
		if (chr != other.chr)
			return false;
		if (endPos != other.endPos)
			return false;
		if (positionType != other.positionType)
			return false;
		if (strand != other.strand)
			return false;
		return true;
	}

}
