package de.charite.compbio.jannovar.hgvs.protein;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.ConvertibleToHGVSString;

/**
 * Represent a range in a protein.
 *
 * Note that contrary to Java-style, we represent the <b>first</b> and <b>last</b> character of the range instead of the
 * begin and end position. Thus, we use inclusive positions. This is also reflected by the members being named first and
 * last instead of begin and end.
 *
 * In the case of the first position being equal to the last one, the {@link ProteinRange} degrades to a point when
 * {@link #toHGVSString()} is called.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ProteinRange implements ConvertibleToHGVSString {

	/** location of the first amino acid in the range */
	private final ProteinPointLocation first;
	/** location of the last amino acid in the range */
	private final ProteinPointLocation last;

	public static ProteinRange build(String firstAA, int first, String lastAA, int last) {
		return new ProteinRange(ProteinPointLocation.build(firstAA, first), ProteinPointLocation.build(lastAA, last));
	}

	public static ProteinRange buildWithOffset(String firstAA, int first, int firstOffset, String lastAA, int last,
			int lastOffset) {
		return new ProteinRange(ProteinPointLocation.buildWithOffset(firstAA, first, firstOffset),
				ProteinPointLocation.buildWithOffset(lastAA, last, lastOffset));
	}

	public static ProteinRange buildDownstreamOfTerminal(String firstAA, int first, String lastAA, int last) {
		return new ProteinRange(ProteinPointLocation.buildWithOffset(firstAA, first, 0),
				ProteinPointLocation.buildWithOffset(lastAA, last, 0));
	}

	/**
	 * @param first
	 *            first position of the range
	 * @param last
	 *            last position of the range
	 */
	public ProteinRange(ProteinPointLocation first, ProteinPointLocation last) {
		super();
		this.first = first;
		this.last = last;
	}

	/** @return location of the first amino acid in the range */
	public ProteinPointLocation getFirst() {
		return first;
	}

	/** @return location of the last amino acid in the range */
	public ProteinPointLocation getLast() {
		return last;
	}

	/** @return length of the range */
	public int length() {
		return last.getPos() - first.getPos() + 1;
	}

	@Override
	public String toHGVSString() {
		return toHGVSString(AminoAcidCode.THREE_LETTER);
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		if (first.equals(last))
			return first.toHGVSString(code);
		else
			return first.toHGVSString(code) + "_" + last.toHGVSString(code);
	}

	@Override
	public String toString() {
		return "ProteinRange [first=" + first + ", last=" + last + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((last == null) ? 0 : last.hashCode());
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
		ProteinRange other = (ProteinRange) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (last == null) {
			if (other.last != null)
				return false;
		} else if (!last.equals(other.last))
			return false;
		return true;
	}

}
