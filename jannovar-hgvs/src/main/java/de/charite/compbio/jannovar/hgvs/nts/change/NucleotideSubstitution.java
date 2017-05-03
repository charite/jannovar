package de.charite.compbio.jannovar.hgvs.nts.change;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.nts.NucleotidePointLocation;

/**
 * Substitution of one nucleotide.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class NucleotideSubstitution extends NucleotideChange {

	/** position of the substituted base */
	private final NucleotidePointLocation position;
	/** String of length 1 with the original base */
	private final String fromNT;
	/** String of length 1 with the changed based */
	private final String toNT;

	/** Build {@link NucleotideSubstitution} with nucleotide positions with offset values. */
	public static NucleotideSubstitution buildWithOffset(boolean onlyPredicted, int basePos, int posOffset,
			String fromNT, String toNT) {
		return new NucleotideSubstitution(onlyPredicted, NucleotidePointLocation.buildWithOffset(basePos, posOffset),
				fromNT, toNT);
	}

	/** Build {@link NucleotideSubstitution} with nucleotide positions without offset values. */
	public static NucleotideSubstitution build(boolean onlyPredicted, int basePos, String fromNT, String toNT) {
		return new NucleotideSubstitution(onlyPredicted, NucleotidePointLocation.build(basePos), fromNT, toNT);
	}

	/** Construct with the given values */
	public NucleotideSubstitution(boolean onlyPredicted, NucleotidePointLocation position, String fromNT, String toNT) {
		super(onlyPredicted);
		this.position = position;
		this.fromNT = fromNT;
		this.toNT = toNT;
	}

	@Override
	public NucleotideSubstitution withOnlyPredicted(boolean flag) {
		return new NucleotideSubstitution(flag, position, fromNT, toNT);
	}

	/** @return {@link NucleotidePointLocation} of the changed nucleotide */
	public NucleotidePointLocation getPosition() {
		return position;
	}

	/** @return reference nucleotide */
	public String getFromNT() {
		return fromNT;
	}

	/** @return variant nucleotide */
	public String getToNT() {
		return toNT;
	}

	@Override
	public String toHGVSString() {
		return wrapIfOnlyPredicted(Joiner.on("").join(position.toHGVSString(), fromNT, ">", toNT));
	}

	@Override
	public String toString() {
		return "NucleotideSubstitution [position=" + position + ", fromNT=" + fromNT + ", toNT=" + toNT + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((fromNT == null) ? 0 : fromNT.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((toNT == null) ? 0 : toNT.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		NucleotideSubstitution other = (NucleotideSubstitution) obj;
		if (fromNT == null) {
			if (other.fromNT != null)
				return false;
		} else if (!fromNT.equals(other.fromNT))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (toNT == null) {
			if (other.toNT != null)
				return false;
		} else if (!toNT.equals(other.toNT))
			return false;
		return true;
	}

}
