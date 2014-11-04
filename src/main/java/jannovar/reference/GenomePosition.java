package jannovar.reference;

import jannovar.common.ChromosomeMap;

/**
 * Representation of a position on a genome (chromosome, position).
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class GenomePosition {
	/** the used position type */
	private final PositionType positionType;
	/** the strand that the position is located on */
	private final char strand;
	/** the chromosome number, as index in chromosome dictionary */
	private final int chr;
	/** the position on the chromosome */
	private final int pos;

	/** construct genome position with one-based coordinate system */
	GenomePosition(char strand, int chr, int pos) {
		this.positionType = PositionType.ONE_BASED;
		this.strand = strand;
		this.chr = chr;
		this.pos = pos;
	}

	/** construct genome position with selected coordinate system */
	GenomePosition(char strand, int chr, int pos, PositionType positionType) {
		this.positionType = positionType;
		this.strand = strand;
		this.chr = chr;
		this.pos = pos;
	}

	/** construct genome position from other with selected coordinate system */
	GenomePosition(GenomePosition other, PositionType positionType) {
		this.positionType = positionType;
		this.strand = other.strand;
		this.chr = other.chr;
		// transform coordinate system
		int delta = 0;
		if (other.positionType == PositionType.ZERO_BASED && this.positionType == PositionType.ONE_BASED)
			delta = 1;
		else if (other.positionType == PositionType.ONE_BASED && this.positionType == PositionType.ZERO_BASED)
			delta = -1;
		this.pos = other.pos + delta;
	}

	/** construct genome position from other with the selected strand */
	GenomePosition(GenomePosition other, char strand) {
		this.positionType = other.positionType;
		this.strand = strand;
		this.chr = other.chr;

		if (strand == other.strand) {
			this.pos = other.pos;
		} else {
			int delta = 0;
			if (this.positionType == PositionType.ONE_BASED)
				delta = 1;
			this.pos = ChromosomeMap.chromosomLength.get((byte) other.chr) - other.pos + delta;
		}
	}

	/** convert into GenomePosition of the given strand */
	GenomePosition withStrand(char strand) {
		return new GenomePosition(this, strand);
	}

	/** convert into GenomePosition of the given position type */
	GenomePosition withPositionType(PositionType positionType) {
		return new GenomePosition(this, positionType);
	}

	// TODO(holtgrem): is* functions are untested at the moment

	/** @return <tt>true</tt> if this position is left of the other (on this strand). */
	public boolean isLt(GenomePosition other) {
		if (other.strand != strand)
			other = other.withStrand(strand);
		if (other.positionType != positionType)
			other = other.withPositionType(positionType);
		return (pos < other.pos);
	}

	/** @return <tt>true</tt> if this position is left of or equal to the other (on this strand). */
	public boolean isLeq(GenomePosition other) {
		if (other.chr != chr)
			return false;
		if (other.strand != strand)
			other = other.withStrand(strand);
		if (other.positionType != positionType)
			other = other.withPositionType(positionType);
		return (pos <= other.pos);
	}

	/** @return <tt>true</tt> if this position is right of the other (on this strand). */
	public boolean isGt(GenomePosition other) {
		if (other.chr != chr)
			return false;
		if (other.strand != strand)
			other = other.withStrand(strand);
		if (other.positionType != positionType)
			other = other.withPositionType(positionType);
		return (pos > other.pos);
	}

	/** @return <tt>true</tt> if this position is right of or equal to the other (on this strand). */
	public boolean isGeq(GenomePosition other) {
		if (other.chr != chr)
			return false;
		if (other.strand != strand)
			other = other.withStrand(strand);
		if (other.positionType != positionType)
			other = other.withPositionType(positionType);
		return (pos >= other.pos);
	}

	/** @return <tt>true</tt> if this position is equal to the other (on this strand). */
	public boolean isEq(GenomePosition other) {
		if (other.chr != chr)
			return false;
		if (other.strand != strand)
			other = other.withStrand(strand);
		if (other.positionType != positionType)
			other = other.withPositionType(positionType);
		return (pos == other.pos);
	}

	/**
	 * @return the strand
	 */
	public char getStrand() {
		return strand;
	}

	/**
	 * Return shifted GenomePosition.
	 *
	 * @param delta
	 *            the value to add to the position
	 * @return the position shifted by <tt>delta</tt>
	 */
	public GenomePosition shifted(int delta) {
		return new GenomePosition(strand, chr, pos + delta, positionType);
	}

	/**
	 * @return the chromosome id
	 */
	public int getChr() {
		return chr;
	}

	/**
	 * @return the position
	 */
	public int getPos() {
		return pos;
	}

	/**
	 * @return the positionType
	 */
	public PositionType getPositionType() {
		return positionType;
	}

	/*
	 * String representation with one-based positions, on forward strand.
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (strand == '-')
			return withStrand('+').toString();

		// TODO(holtgrem): Update once we have better chromosome id to reference name mapping.
		int pos = this.pos + (positionType == PositionType.ZERO_BASED ? 1 : 0);
		return String.format("chr%d:%d", chr, pos);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + chr;
		result = prime * result + pos;
		result = prime * result + ((positionType == null) ? 0 : positionType.hashCode());
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
		GenomePosition other = (GenomePosition) obj;
		if (chr != other.chr)
			return false;
		if (pos != other.pos)
			return false;
		if (positionType != other.positionType)
			return false;
		return true;
	}
}
