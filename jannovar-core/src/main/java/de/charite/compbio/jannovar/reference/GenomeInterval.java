package de.charite.compbio.jannovar.reference;

import java.io.Serializable;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.util.StringUtil;

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
	private final Strand strand;
	/** the chromosome number, as index in chromosome dictionary */
	private final int chr;
	/** the begin position on the chromosome */
	private final int beginPos;
	/** the end position on the chromosome */
	private final int endPos;

	/** construct genome interval with zero-based coordinate system */
	public GenomeInterval(ReferenceDictionary refDict, Strand strand, int chr, int beginPos, int endPos) {
		this.refDict = refDict;
		this.strand = strand;
		this.chr = chr;
		this.beginPos = beginPos;
		this.endPos = endPos;
	}

	/** construct genome interval with selected coordinate system */
	public GenomeInterval(ReferenceDictionary refDict, Strand strand, int chr, int beginPos, int endPos,
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
	public GenomeInterval(GenomeInterval other, Strand strand) {
		this.refDict = other.refDict;
		this.strand = strand;
		this.chr = other.chr;

		// transform coordinate system
		if (strand == other.strand) {
			this.beginPos = other.beginPos;
			this.endPos = other.endPos;
		} else {
			int beginPos = refDict.getContigIDToLength().get(other.chr) - other.beginPos;
			int endPos = refDict.getContigIDToLength().get(other.chr) - other.endPos;
			this.endPos = beginPos;
			this.beginPos = endPos;
		}
	}

	/** construct genome interval from {@link GenomePosition} with a length towards 3' of pos' coordinate system */
	public GenomeInterval(GenomePosition pos, int length) {
		this.refDict = pos.getRefDict();
		this.strand = pos.getStrand();
		this.chr = pos.getChr();
		this.beginPos = pos.getPos();

		this.endPos = pos.getPos() + length;
	}

	/** @return {@link ReferenceDitionary} to use */
	public ReferenceDictionary getRefDict() {
		return refDict;
	}

	/** @return {@link Strand} of this {@link GenomeInterval} */
	public Strand getStrand() {
		return strand;
	}

	/** @return numeric chromosome id */
	public int getChr() {
		return chr;
	}

	/** @return 0-based begin position */
	public int getBeginPos() {
		return beginPos;
	}

	/** @return 0-based end position */
	public int getEndPos() {
		return endPos;
	}

	/** convert into GenomeInterval of the given strand */
	public GenomeInterval withStrand(Strand strand) {
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
	 * @return <tt>true</tt> if the interval is truly left of the base that base pos points to
	 */
	public boolean isLeftOf(GenomePosition pos) {
		if (chr != pos.getChr())
			return false; // wrong chromosome
		if (pos.getStrand() != strand)
			pos = pos.withStrand(strand); // ensure that we are on the correct strand
		return (pos.getPos() >= endPos);
	}

	/**
	 * @param pos
	 *            query position
	 * @return <tt>true</tt> if the interval is truly right of the base that base pos points to
	 */
	public boolean isRightOf(GenomePosition pos) {
		if (chr != pos.getChr())
			return false; // wrong chromosome
		if (pos.getStrand() != strand)
			pos = pos.withStrand(strand); // ensure that we are on the correct strand
		return (pos.getPos() < beginPos);
	}

	/**
	 * @param pos
	 *            query position
	 * @return <tt>true</tt> if the interval is truly left of the gap between bases that <code>pos</code> points at
	 */
	public boolean isLeftOfGap(GenomePosition pos) {
		if (chr != pos.getChr())
			return false; // wrong chromosome
		if (pos.getStrand() != strand)
			pos = pos.withStrand(strand); // ensure that we are on the correct strand
		return (pos.getPos() >= endPos);
	}

	/**
	 * @param pos
	 *            query position
	 * @return <tt>true</tt> if the interval is truly right of the gap between bases that <code>pos</code> points at
	 */
	public boolean isRightOfGap(GenomePosition pos) {
		if (chr != pos.getChr())
			return false; // wrong chromosome
		if (pos.getStrand() != strand)
			pos = pos.withStrand(strand); // ensure that we are on the correct strand
		return (pos.getPos() <= beginPos);
	}

	/**
	 * @param pos
	 *            query position
	 * @return <tt>true</tt> if the interval contains <tt>pos</tt>
	 */
	public boolean contains(GenomePosition pos) {
		if (chr != pos.getChr())
			return false; // wrong chromosome
		if (pos.getStrand() != strand)
			pos = pos.withStrand(strand); // ensure that we are on the correct strand
		return (pos.getPos() >= beginPos && pos.getPos() < endPos);
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
		return withMorePadding(padding, padding);
	}

	/**
	 * @return a {@link GenomeInterval} that has <code>padding</code> more bases towards either side as padding.
	 */
	public GenomeInterval withMorePadding(int paddingUpstream, int paddingDownstream) {
		// TODO(holtgrem): throw when going outside of chromosome?
		return new GenomeInterval(refDict, strand, chr, beginPos - paddingUpstream, endPos + paddingDownstream,
				PositionType.ZERO_BASED);
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
		other = other.withStrand(strand);
		return (other.beginPos < endPos && beginPos < other.endPos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (strand.isReverse())
			return withStrand(Strand.FWD).toString();

		return StringUtil.concatenate(refDict.getContigIDToName().get(chr), ":g.", beginPos + 1, "_", endPos);
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
		result = prime * result + beginPos;
		result = prime * result + chr;
		result = prime * result + endPos;
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
