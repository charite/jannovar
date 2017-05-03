package de.charite.compbio.jannovar.hgvs.protein.change;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.Translator;
import de.charite.compbio.jannovar.hgvs.protein.ProteinPointLocation;

/**
 * Protein frame shift change.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ProteinFrameshift extends ProteinChange {

	/** no terminal is encountered */
	public static final int LEN_NO_TER = 0;
	/** short change description */
	public static final int LEN_SHORT = 1;

	/** first affected AA with position */
	private final ProteinPointLocation position;
	/** destination AA at position, can be null for short descriptions */
	private final String targetAA;
	/**
	 * number of amino acids until the next terminal is encountered, 0 if none is encountered and -1 if this is a short
	 * description
	 */
	private final int shiftLength;

	/** Build new {@link ProteinFrameshift} with full settings */
	public static ProteinFrameshift build(boolean onlyPredicted, String wtAA, int position, String targetAA,
			int shiftLength) {
		return new ProteinFrameshift(onlyPredicted, ProteinPointLocation.build(wtAA, position), targetAA, shiftLength);
	}

	/** Build new {@link ProteinFrameshift} with full settings */
	public static ProteinFrameshift build(boolean onlyPredicted, ProteinPointLocation position, String targetAA,
			int shiftLength) {
		return new ProteinFrameshift(onlyPredicted, position, targetAA, shiftLength);
	}

	/** Build new {@link ProteinFrameshift} short description */
	public static ProteinFrameshift buildShort(boolean onlyPredicted, String wtAA, int position) {
		return new ProteinFrameshift(onlyPredicted, ProteinPointLocation.build(wtAA, position), null, LEN_SHORT);
	}

	/** Build new {@link ProteinFrameshift} short description */
	public static ProteinFrameshift buildShort(boolean onlyPredicted, ProteinPointLocation position) {
		return new ProteinFrameshift(onlyPredicted, position, null, LEN_SHORT);
	}

	/** Build new {@link ProteinFrameshift} for the case that there is no terminal */
	public static ProteinFrameshift buildWithoutTerminal(boolean onlyPredicted, String wtAA, int position,
			String targetAA) {
		return new ProteinFrameshift(onlyPredicted, ProteinPointLocation.build(wtAA, position), targetAA, LEN_NO_TER);
	}

	/** Build new {@link ProteinFrameshift} for the case that there is no terminal */
	public static ProteinFrameshift buildWithoutTerminal(boolean onlyPredicted, ProteinPointLocation position,
			String targetAA) {
		return new ProteinFrameshift(onlyPredicted, position, targetAA, LEN_NO_TER);
	}

	/** Construct {@link ProteinFrameshift} with the given values */
	public ProteinFrameshift(boolean onlyPredicted, ProteinPointLocation position, String targetAA, int shiftLength) {
		super(onlyPredicted);
		this.position = position;
		this.targetAA = targetAA;
		this.shiftLength = shiftLength;
	}

	/** @return <code>true</code> if this object is a short description of a frame shift. */
	public boolean isShort() {
		return ((targetAA == null) || (shiftLength == LEN_SHORT));
	}

	/** @return <code>true</code> if this object describes a frameshift without a terminal at the end */
	public boolean isNoTerminalFrameshfit() {
		return (shiftLength == LEN_NO_TER);
	}

	/** @return position of first affected amino acid */
	public ProteinPointLocation getPosition() {
		return position;
	}

	/** @return amino acid at position after mutation or <code>null</code> if this is a short description */
	public String getTargetAA() {
		return targetAA;
	}

	/** @return shift length, or &lt;= 0 in the case of no terminal symbol (0) or short description (-1) */
	public int getShiftLength() {
		return shiftLength;
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		String targetAA = this.targetAA;
		if (!isShort() && code == AminoAcidCode.THREE_LETTER)
			targetAA = Translator.getTranslator().toLong(targetAA);

		if (isShort())
			return wrapIfOnlyPredicted(Joiner.on("").join(position.toHGVSString(code), "fs"));
		else if (shiftLength == LEN_NO_TER)
			return wrapIfOnlyPredicted(Joiner.on("").join(position.toHGVSString(code), targetAA, "fs*?"));
		else
			return wrapIfOnlyPredicted(Joiner.on("").join(position.toHGVSString(code), targetAA, "fs*", shiftLength));
	}

	@Override
	public String toString() {
		return "ProteinFrameshift [position=" + position + ", targetAA=" + targetAA + ", shiftLength=" + shiftLength + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + shiftLength;
		result = prime * result + ((targetAA == null) ? 0 : targetAA.hashCode());
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
		ProteinFrameshift other = (ProteinFrameshift) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (shiftLength != other.shiftLength)
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
		return new ProteinFrameshift(onlyPredicted, this.position, this.targetAA, this.shiftLength);
	}

}
