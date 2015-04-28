package de.charite.compbio.jannovar.reference;

import java.io.Serializable;

import com.google.common.collect.ComparisonChain;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.util.StringUtil;

/**
 * Representation of a position on a genome (chromosome, position).
 *
 * Internally, positions are always stored zero-based, but the position type can be explicitely given to the constructor
 * of {@link GenomePosition}.
 *
 * In the case of one-based position, {@link #pos} points to the {@link #pos}-th base in string from the left when
 * starting to count at 1. In the case of zero-based positions, {@link #pos} points to the gap left of the character in
 * the case of positions on the forward strand and to the gap right of the character in the case of positions on the
 * reverse strand. When interpreting this for the reverse strand (i.e. counting from the right), the position right of a
 * character is interpreted as the gap <b>before</b> the character.
 *
 * Reverse-complementing a zero-based GenomePosition must be equivalent to reverse-complementing its one-based position
 * representation. Thus, they are shifted towards the right gap besides the character they point at when changing the
 * strand.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class GenomePosition implements Serializable, Comparable<GenomePosition> {

	private static final long serialVersionUID = 2L;

	/** reference dictionary to use for coordinate translation */
	private final ReferenceDictionary refDict;

	/** the strand that the position is located on */
	private final Strand strand;
	/** the chromosome number, as index in chromosome dictionary */
	private final int chr;
	/** the position on the chromosome */
	private final int pos;

	/** construct genome position with zero-based coordinate system */
	public GenomePosition(ReferenceDictionary refDict, Strand strand, int chr, int pos) {
		this.refDict = refDict;
		this.strand = strand;
		this.chr = chr;
		this.pos = pos;
	}

	/** construct genome position with selected coordinate system */
	public GenomePosition(ReferenceDictionary refDict, Strand strand, int chr, int pos, PositionType positionType) {
		this.refDict = refDict;
		this.strand = strand;
		this.chr = chr;
		if (positionType == PositionType.ONE_BASED)
			this.pos = pos - 1;
		else
			this.pos = pos;
	}

	/** construct genome position from other with selected coordinate system */
	public GenomePosition(GenomePosition other) {
		this.refDict = other.refDict;
		this.strand = other.strand;
		this.chr = other.chr;
		this.pos = other.pos;
	}

	/** construct genome position from other with the selected strand */
	public GenomePosition(GenomePosition other, Strand strand) {
		this.refDict = other.refDict;
		this.strand = strand;
		this.chr = other.chr;

		// transform coordinate system
		if (strand == other.strand)
			this.pos = other.pos;
		else
			this.pos = refDict.getContigIDToLength().get(other.chr) - other.pos - 1;
	}

	/** @return reference dictionary to use for coordinate translation */
	public ReferenceDictionary getRefDict() {
		return refDict;
	}

	/** @return the strand that the position is located on */
	public Strand getStrand() {
		return strand;
	}

	/** @return the chromosome number, as index in chromosome dictionary */
	public int getChr() {
		return chr;
	}

	/** @return the position on the chromosome */
	public int getPos() {
		return pos;
	}

	/** convert into GenomePosition of the given strand */
	public GenomePosition withStrand(Strand strand) {
		return new GenomePosition(this, strand);
	}

	/**
	 * @return <tt>true</tt> if this position is left of the other (on this strand).
	 */
	public boolean isLt(GenomePosition other) {
		if (other.strand != strand)
			other = other.withStrand(strand);
		return (pos < other.pos);
	}

	/**
	 * @return <tt>true</tt> if this position is left of or equal to the other (on this strand).
	 */
	public boolean isLeq(GenomePosition other) {
		if (other.chr != chr)
			return false;
		if (other.strand != strand)
			other = other.withStrand(strand);
		return (pos <= other.pos);
	}

	/**
	 * @return <tt>true</tt> if this position is right of the other (on this strand).
	 */
	public boolean isGt(GenomePosition other) {
		if (other.chr != chr)
			return false;
		if (other.strand != strand)
			other = other.withStrand(strand);
		return (pos > other.pos);
	}

	/**
	 * @return <tt>true</tt> if this position is right of or equal to the other (on this strand).
	 */
	public boolean isGeq(GenomePosition other) {
		if (other.chr != chr)
			return false;
		if (other.strand != strand)
			other = other.withStrand(strand);
		return (pos >= other.pos);
	}

	/**
	 * @return <tt>true</tt> if this position is equal to the other (on this strand).
	 */
	public boolean isEq(GenomePosition other) {
		if (other.chr != chr)
			return false;
		if (other.strand != strand)
			other = other.withStrand(strand);
		return (pos == other.pos);
	}

	/**
	 * @param pos
	 *            other position to compute difference to
	 * @return the result of <code>(this.pos - pos.pos)</code> (<code>pos</code> is adjusted to the coordinate system
	 *         and strand of <code>this</code>)
	 * @throws InvalidCoordinateException
	 *             if <code>this</code> and <code>pos</code> are on different chromosomes
	 */
	public int differenceTo(GenomePosition pos) {
		if (chr != pos.chr)
			throw new InvalidCoordinateException("Coordinates are on different chromosomes " + this + " vs. " + pos);
		if (pos.strand != strand)
			pos = pos.withStrand(strand);
		return (this.pos - pos.pos);
	}

	/**
	 * @param itv
	 *            interval to compute difference to
	 * @return difference to closest base in <code>itv</code>, as would be returned by
	 *         {@link #differenceTo(GenomePosition)}), <code>0</code> if <code>this</code> lies in <code>itv</code>
	 * @throws InvalidCoordinateException
	 *             if <code>this</code> and <code>itv</code> are on different chromosomes
	 */
	public int differenceTo(GenomeInterval itv) {
		if (chr != itv.getChr())
			throw new InvalidCoordinateException("Coordinates are on different chromosomes " + this + " vs. " + itv);
		if (itv.getStrand() != strand)
			itv = itv.withStrand(strand);

		if (itv.contains(this))
			return 0;

		int a = differenceTo(itv.getGenomeBeginPos());
		if (itv.length() == 0)
			return a;
		int b = differenceTo(itv.getGenomeEndPos().shifted(-1));
		if (Math.abs(a) < Math.abs(b))
			return a;
		else
			return b;
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
		return new GenomePosition(refDict, strand, chr, pos + delta, PositionType.ZERO_BASED);
	}

	/*
	 * String representation with one-based positions, on forward strand.
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (strand.isReverse())
			return withStrand(Strand.FWD).toString();

		return StringUtil.concatenate(refDict.getContigIDToName().get(chr), ":g.", pos + 1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (strand.isReverse())
			return withStrand(Strand.FWD).hashCode();

		final int prime = 31;
		int result = 1;
		result = prime * result + chr;
		result = prime * result + pos;
		result = prime * result + strand.hashCode();
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

		if (strand.isReverse())
			return withStrand(Strand.FWD).equals(obj);
		GenomePosition other = (GenomePosition) obj;
		other = other.withStrand(Strand.FWD);

		if (strand != other.strand)
			return false;
		if (chr != other.chr)
			return false;
		if (pos != other.pos)
			return false;
		return true;
	}

	public int compareTo(GenomePosition other) {
		return ComparisonChain.start().compare(chr, other.chr).compare(pos, other.pos).result();
	}

}
