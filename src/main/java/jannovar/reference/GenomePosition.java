package jannovar.reference;

import jannovar.exception.InvalidCoordinateException;
import jannovar.io.ReferenceDictionary;
import jannovar.util.Immutable;

import java.io.Serializable;

/**
 * Representation of a position on a genome (chromosome, position).
 *
 * In the case of one-based position, {@link #pos} points to the {@link #pos}-th base in string from the left when
 * starting to count at 1. In the case of zero-based positions, {@link #pos} points to the gap left of the character in
 * the case of positions on the forward strand and to the gap right of the character in the case of positions on the
 * reverse strand. When interpreting this for the reverse strand (i.e. counting from the right), the position right of a
 * character is interpreted as the gap <b>before</b> the character.
 *
 * Reverse-complementing a zero-based GenomePosition must be equivalent to reverse-complementing one-based positions.
 * Thus, they are shifted towards teh right gap besides the character they point at when changing the strand.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class GenomePosition implements Serializable {

	private static final long serialVersionUID = 1L;

	/** reference dictionary to use for coordinate translation */
	final ReferenceDictionary refDict;

	/** the used position type */
	public final PositionType positionType;
	/** the strand that the position is located on */
	public final char strand;
	/** the chromosome number, as index in chromosome dictionary */
	public final int chr;
	/** the position on the chromosome */
	public final int pos;

	/** construct genome position with one-based coordinate system */
	public GenomePosition(ReferenceDictionary refDict, char strand, int chr, int pos) {
		this.refDict = refDict;
		this.positionType = PositionType.ONE_BASED;
		this.strand = strand;
		this.chr = chr;
		this.pos = pos;
	}

	/** construct genome position with selected coordinate system */
	public GenomePosition(ReferenceDictionary refDict, char strand, int chr, int pos, PositionType positionType) {
		this.refDict = refDict;
		this.positionType = positionType;
		this.strand = strand;
		this.chr = chr;
		this.pos = pos;
	}

	/** construct genome position from other with selected coordinate system */
	public GenomePosition(GenomePosition other, PositionType positionType) {
		this.refDict = other.refDict;
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
	public GenomePosition(GenomePosition other, char strand) {
		this.refDict = other.refDict;
		this.positionType = other.positionType;
		this.strand = strand;
		this.chr = other.chr;

		// transform coordinate system
		int delta = (positionType == PositionType.ONE_BASED) ? 1 : -1;
		if (strand == other.strand)
			this.pos = other.pos;
		else
			this.pos = refDict.contigLength.get(other.chr) - other.pos + delta;
	}

	/** convert into GenomePosition of the given strand */
	public GenomePosition withStrand(char strand) {
		return new GenomePosition(this, strand);
	}

	/** convert into GenomePosition of the given position type */
	public GenomePosition withPositionType(PositionType positionType) {
		return new GenomePosition(this, positionType);
	}

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
	 * @param pos
	 *            other position to compute distance to
	 * @return the result of <code>(this.pos - pos.pos)</code> (<code>pos</code> is adjusted to the coordinate system
	 *         and strand of <code>this</code>)
	 * @throws InvalidCoordinateException
	 *             if <code>this</code> and <code>pos</code> are on different chromosomes
	 */
	// TODO(holtgrem): test this!
	public int differenceTo(GenomePosition pos) {
		if (chr != pos.chr)
			throw new InvalidCoordinateException("Coordinates are on different chromosomes " + this + " vs. " + pos);
		if (pos.strand != strand)
			pos = pos.withStrand(strand);
		if (pos.positionType != positionType)
			pos = pos.withPositionType(positionType);
		return (this.pos - pos.pos);
	}

	/**
	 * Return shifted GenomePosition.
	 *
	 * The position is shifted towards the 3' end of current strand if <code>delta &gt; 0</code> and towards the 5' end
	 * otherwise.
	 *
	 * @param delta
	 *            the value to add to the position
	 * @return the position shifted by <tt>delta</tt>
	 */
	public GenomePosition shifted(int delta) {
		return new GenomePosition(refDict, strand, chr, pos + delta, positionType);
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
		if (this.positionType == PositionType.ONE_BASED || strand != '+')
			return withPositionType(PositionType.ZERO_BASED).withStrand('+').hashCode();

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

		if (positionType == PositionType.ONE_BASED || strand == '-')
			return this.withPositionType(PositionType.ZERO_BASED).withStrand('+').equals(obj);
		GenomePosition other = (GenomePosition) obj;
		other = other.withPositionType(PositionType.ZERO_BASED).withStrand('+');

		if (strand != other.strand)
			return false;
		if (chr != other.chr)
			return false;
		if (pos != other.pos)
			return false;
		if (positionType != other.positionType)
			return false;
		return true;
	}
}
