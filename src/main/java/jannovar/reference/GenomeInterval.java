package jannovar.reference;

import jannovar.common.ChromosomeMap;

/**
 * Representation of a genomic interval (chromsome, begin, end) with explicit coordinate system (0-/1-based)
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class GenomeInterval {
	/** the used position type */
	private final PositionType positionType;
	/** the strand that the position is located on */
	private char strand;
	/** the chromosome number, as index in chromosome dictionary */
	private int chr;
	/** the begin position on the chromosome */
	private int beginPos;
	/** the end position on the chromosome */
	private int endPos;

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

	/** convert into GenomePosition of the given strand */
	public GenomeInterval withStrand(char strand) {
		return new GenomeInterval(this, strand);
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

	// TODO(holtgrem): is* functions are untested at the moment

	/**
	 * @param pos
	 *            query position
	 * @return <tt>true</tt> if the interval is truly left of the position
	 */
	public boolean isLeftOf(GenomePosition pos) {
		if (chr != pos.getChr())
			return false; // wrong chromosome
		if (pos.getStrand() != strand)
			pos = pos.withStrand(strand); // ensure that we are on the correct strand
		if (pos.getPositionType() != positionType)
			pos = pos.withPositionType(positionType);
		if (positionType == PositionType.ONE_BASED)
			return (pos.getPos() > endPos);
		else
			return (pos.getPos() >= endPos);
	}

	/**
	 * @param pos
	 *            query position
	 * @return <tt>true</tt> if the interval is truly right of the position
	 */
	public boolean isRightOf(GenomePosition pos) {
		if (chr != pos.getChr())
			return false; // wrong chromosome
		if (pos.getStrand() != strand)
			pos = pos.withStrand(strand); // ensure that we are on the correct strand
		if (pos.getPositionType() != positionType)
			pos = pos.withPositionType(positionType);
		return (pos.getPos() < beginPos);
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
		if (pos.getPositionType() != positionType)
			pos = pos.withPositionType(positionType);
		if (positionType == PositionType.ONE_BASED)
			return (pos.getPos() >= beginPos && pos.getPos() <= endPos);
		else
			return (pos.getPos() >= beginPos && pos.getPos() < endPos);
	}

	/**
	 * @return the strand
	 */
	public char getStrand() {
		return strand;
	}

	/**
	 * @param strand
	 *            the strand to set
	 */
	public void setStrand(char strand) {
		this.strand = strand;
	}

	/**
	 * @return the chr
	 */
	public int getChr() {
		return chr;
	}

	/**
	 * @param chr
	 *            the chr to set
	 */
	public void setChr(int chr) {
		this.chr = chr;
	}

	/**
	 * @return the beginPos
	 */
	public int getBeginPos() {
		return beginPos;
	}

	/**
	 * @param beginPos
	 *            the beginPos to set
	 */
	public void setBeginPos(int beginPos) {
		this.beginPos = beginPos;
	}

	/**
	 * @return the endPos
	 */
	public int getEndPos() {
		return endPos;
	}

	/**
	 * @param endPos
	 *            the endPos to set
	 */
	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	/**
	 * @return the positionType
	 */
	public PositionType getPositionType() {
		return positionType;
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

}
