package de.charite.compbio.jannovar.hgvs.protein.change;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.Translator;
import de.charite.compbio.jannovar.hgvs.protein.ProteinPointLocation;

public class ProteinExtension extends ProteinChange {

	/** no terminal is encountered */
	public static final int LEN_NO_TER = 0;

	/** changed amino acid */
	private final ProteinPointLocation position;
	/** amino acid that the changed one is changed to */
	private final String targetAA;
	/** shift value, see {@link #getShift()} */
	private final int shift;

	/** Construct normal {@link ProteinExtension} */
	public static ProteinExtension build(boolean onlyPredicted, String wtAA, int pos, String targetAA, int shift) {
		return build(onlyPredicted, ProteinPointLocation.build(wtAA, pos), targetAA, shift);
	}

	/** Construct normal {@link ProteinExtension} */
	public static ProteinExtension build(boolean onlyPredicted, ProteinPointLocation position, String targetAA,
			int shift) {
		return new ProteinExtension(onlyPredicted, position, targetAA, shift);
	}

	/** Construct {@link ProteinExtension} without a terminal in the extension */
	public static ProteinExtension buildWithoutTerminal(boolean onlyPredicted, String wtAA, int pos, String targetAA) {
		return new ProteinExtension(onlyPredicted, ProteinPointLocation.build(wtAA, pos), targetAA, LEN_NO_TER);
	}

	/** Construct {@link ProteinExtension} without a terminal in the extension */
	public static ProteinExtension buildWithoutTerminal(boolean onlyPredicted, ProteinPointLocation position,
			String targetAA) {
		return new ProteinExtension(onlyPredicted, position, targetAA, LEN_NO_TER);
	}

	/** Construct {@link ProteinExtension} with the given values */
	public ProteinExtension(boolean onlyPredicted, ProteinPointLocation position, String targetAA, int shift) {
		super(onlyPredicted);
		this.position = position;
		this.targetAA = targetAA;
		this.shift = shift;
	}

	/** @return changed amino acid and its position */
	public ProteinPointLocation getPosition() {
		return position;
	}

	/** @return amino acid that the changed one is changed to */
	public String getTargetAA() {
		return targetAA;
	}

	/**
	 * The shift value number of extended amino acids if positive, shift into the 5' UTR in case of negative values, or
	 * {@link #LEN_NO_TER} in the case that no stop codon is encountered on the transcript.
	 *
	 * @return shift value of the extension
	 */
	public int getShift() {
		return shift;
	}

	/** @return <code>true</code> if this object describes an extension without a terminal at the end */
	public boolean isNoTerminalExtension() {
		return (shift == LEN_NO_TER);
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		String targetAA = this.targetAA;
		if (code == AminoAcidCode.THREE_LETTER)
			targetAA = Translator.getTranslator().toLong(targetAA);
		if (isNoTerminalExtension())
			return wrapIfOnlyPredicted(Joiner.on("").join(position.toHGVSString(code), targetAA, "ext*?"));
		else
			return wrapIfOnlyPredicted(Joiner.on("").join(position.toHGVSString(code), targetAA, "ext*", shift));
	}

	@Override
	public String toString() {
		return "ProteinExtension [position=" + position + ", targetAA=" + targetAA + ", shift=" + shift
				+ ", toHGVSString()=" + toHGVSString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + shift;
		result = prime * result + ((targetAA == null) ? 0 : targetAA.hashCode());
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
		ProteinExtension other = (ProteinExtension) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (shift != other.shift)
			return false;
		if (targetAA == null) {
			if (other.targetAA != null)
				return false;
		} else if (!targetAA.equals(other.targetAA))
			return false;
		return true;
	}

	@Override
	public ProteinChange withOnlyPredicted(boolean onlyPredicted) {
		return new ProteinExtension(onlyPredicted, this.position, this.targetAA, this.shift);
	}

}
