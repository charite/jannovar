package de.charite.compbio.jannovar.reference;

import com.google.common.collect.ComparisonChain;
import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.data.Contig;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.util.StringUtil;

import java.io.Serializable;

/**
 * Representation of a genomic interval (chromsome, begin, end).
 * <p>
 * Internally, positions are always stored zero-based, but the position type can be explicitly given to the constructor
 * of {@link GenomeInterval}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@Immutable
public final class GenomeInterval implements Serializable, Comparable<GenomeInterval> {

	private static final long serialVersionUID = 2L;

	/**
	 * the strand that the position is located on
	 */
	private final Strand strand;
	/**
	 * The contig on which this interval is found.
	 */
	private final Contig contig;

	/**
	 * the begin position on the chromosome
	 */
	private final int beginPos;
	/**
	 * the end position on the chromosome
	 */
	private final int endPos;

	/**
	 * construct genome interval with zero-based coordinate system
	 *
	 * @deprecated use the alternative constructor using the {@link Contig}
	 */
	public GenomeInterval(ReferenceDictionary refDict, Strand strand, int chr, int beginPos, int endPos) {
		this(refDict.getContigById(chr), strand, beginPos, endPos, PositionType.ZERO_BASED);
	}

	/**
	 * construct genome interval with selected coordinate system
	 *
	 * @deprecated use the alternative constructor using the {@link Contig}
	 */
	public GenomeInterval(ReferenceDictionary refDict, Strand strand, int chr, int beginPos, int endPos, PositionType positionType) {
		this(refDict.getContigById(chr), strand, beginPos, endPos, positionType);
	}

	/**
	 * construct genome interval with zero-based coordinate system
	 */
	public GenomeInterval(Contig contig, Strand strand, int beginPos, int endPos) {
		this(contig, strand, beginPos, endPos, PositionType.ZERO_BASED);
	}

	/**
	 * construct genome interval with selected coordinate system
	 */
	public GenomeInterval(Contig contig, Strand strand, int beginPos, int endPos, PositionType positionType) {
		this.strand = strand;
		this.contig = contig;
		this.beginPos = positionType == PositionType.ONE_BASED ? beginPos - 1 : beginPos;
		this.endPos = endPos;
	}

	/**
	 * construct genome interval from {@link GenomePosition} with a length towards 3' of pos' coordinate system
	 */
	public GenomeInterval(GenomePosition pos, int length) {
		this(pos.getContig(), pos.getStrand(), pos.getPos(), pos.getPos() + length);
	}

	/**
	 * @return the {@link Contig} that the interval is located on
	 */
	public Contig getContig() {
		return contig;
	}

	/**
	 * @return {@link Strand} of this {@link GenomeInterval}
	 */
	public Strand getStrand() {
		return strand;
	}

	/**
	 * @return numeric chromosome id
	 */
	public int getChr() {
		return contig.getId();
	}

	public String getContigName() {
		return contig.getName();
	}

	/**
	 * @return 0-based begin position
	 */
	public int getBeginPos() {
		return beginPos;
	}

	/**
	 * @return 0-based end position
	 */
	public int getEndPos() {
		return endPos;
	}

	/**
	 * convert into GenomeInterval of the given strand
	 */
	public GenomeInterval withStrand(Strand strand) {
		if (this.strand == strand) {
			return this;
		}

		int contigLength = contig.getLength();
		// reverse start and end positions when on the opposite strand
		int bp = contigLength - beginPos;
		int ep = contigLength - endPos;

 		return new GenomeInterval(contig, strand, ep, bp);
	}

	/**
	 * return the genome begin position
	 */
	public GenomePosition getGenomeBeginPos() {
		// note - it is not worth caching this as an instance field as this leads to worse GC performance
		return new GenomePosition(contig, strand, beginPos, PositionType.ZERO_BASED);
	}

	/**
	 * return the genome end position
	 */
	public GenomePosition getGenomeEndPos() {
		// note - it is not worth caching this as an instance field as this leads to worse GC performance
		return new GenomePosition(contig, strand, endPos, PositionType.ZERO_BASED);
	}

	/**
	 * returns length of the interval
	 */
	public int length() {
		return this.endPos - this.beginPos;
	}

	/**
	 * @return GenomeInterval with intersection of <code>this</code> and <code>other</code>
	 */
	public GenomeInterval intersection(GenomeInterval other) {
		if (getChr()!= other.getChr()) {
			return new GenomeInterval(contig, strand, beginPos, beginPos, PositionType.ZERO_BASED);
		}
		other = other.withStrand(strand);

		int beginPos = Math.max(this.beginPos, other.beginPos);
		int endPos = Math.min(this.endPos, other.endPos);
		if (endPos < beginPos)
			beginPos = endPos;

		return new GenomeInterval(contig, strand, beginPos, endPos, PositionType.ZERO_BASED);
	}

	// TODO(holtgrewe): Test me!

	/**
	 * @return GenomeInterval with union of <code>this</code> and <code>other</code> and everything in between,
	 * <code>this</code> if on different chromosomes. Result will be on the same strand as <code>this</code>.
	 */
	public GenomeInterval union(GenomeInterval other) {
		if (contig.getId() != other.contig.getId()) {
			return new GenomeInterval(contig, strand, beginPos, beginPos, PositionType.ZERO_BASED);
		}
		other = other.withStrand(strand);

		int beginPos = Math.min(this.beginPos, other.beginPos);
		int endPos = Math.max(this.endPos, other.endPos);
		if (endPos < beginPos)
			beginPos = endPos;

		return new GenomeInterval(contig, strand, beginPos, endPos, PositionType.ZERO_BASED);
	}

	/**
	 * @param pos query position
	 * @return <tt>true</tt> if the interval is truly left of the base that base pos points to
	 */
	public boolean isLeftOf(GenomePosition pos) {
		if (this.getChr() != pos.getChr())
			return false; // wrong chromosome
		pos = ensureSameStrand(pos);
		return (pos.getPos() >= endPos);
	}

	/**
	 * @param pos query position
	 * @return <tt>true</tt> if the interval is truly right of the base that base pos points to
	 */
	public boolean isRightOf(GenomePosition pos) {
		if (this.getChr() != pos.getChr())
			return false; // wrong chromosome
		pos = ensureSameStrand(pos);
		return (pos.getPos() < beginPos);
	}

	/**
	 * @param pos query position
	 * @return <tt>true</tt> if the interval is truly left of the gap between bases that <code>pos</code> points at
	 */
	public boolean isLeftOfGap(GenomePosition pos) {
		if (this.getChr() != pos.getChr())
			return false; // wrong chromosome
		pos = ensureSameStrand(pos);
		return (pos.getPos() >= endPos);
	}

	private GenomePosition ensureSameStrand(GenomePosition pos) {
		return pos.withStrand(strand);
	}

	/**
	 * @param pos query position
	 * @return <tt>true</tt> if the interval is truly right of the gap between bases that <code>pos</code> points at
	 */
	public boolean isRightOfGap(GenomePosition pos) {
		if (this.getChr() != pos.getChr())
			return false; // wrong chromosome
		pos = ensureSameStrand(pos);
		return (pos.getPos() <= beginPos);
	}

	/**
	 * @param pos query position
	 * @return <tt>true</tt> if the interval contains <tt>pos</tt>
	 */
	public boolean contains(GenomePosition pos) {
		if (this.getChr() != pos.getChr())
			return false; // wrong chromosome
		pos = ensureSameStrand(pos);
		return (pos.getPos() >= beginPos && pos.getPos() < endPos);
	}

	/**
	 * @param other other {@link GenomeInterval} to use for querying
	 * @return <tt>true</tt> if the interval contains <code>other</code>
	 */
	public boolean contains(GenomeInterval other) {
		// TODO(holtgrem): Test this.
		if (this.getChr() != other.getChr())
			return false; // wrong chromosome
		other = ensureSameStrand(other);
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
		return new GenomeInterval(contig, strand, beginPos - paddingUpstream, endPos + paddingDownstream,
			PositionType.ZERO_BASED);
	}

	/**
	 * @param other other {@link GenomeInterval} to check with overlap for
	 * @return whether <code>other</code> overlaps with <code>this</code>
	 */
	public boolean overlapsWith(GenomeInterval other) {
		// TODO(holtgrem): add test for this
		if (this.getChr() != other.getChr())
			return false;
		other = ensureSameStrand(other);
		return (other.beginPos < endPos && beginPos < other.endPos);
	}

	private GenomeInterval ensureSameStrand(GenomeInterval other) {
		return other.withStrand(strand);
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

		return StringUtil.concatenate(contig.getName(), ":g.", beginPos + 1, "_", endPos);
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
		result = prime * result + getChr();
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
		if (getChr() != other.getChr())
			return false;
		if (endPos != other.endPos)
			return false;
		if (strand != other.strand)
			return false;
		return true;
	}

	@Override
	public int compareTo(GenomeInterval other) {
		other = other.withStrand(strand);
		return ComparisonChain.start().compare(getChr(), other.getChr()).compare(getBeginPos(), other.getBeginPos())
			.compare(getEndPos(), other.getEndPos()).result();
	}

}
