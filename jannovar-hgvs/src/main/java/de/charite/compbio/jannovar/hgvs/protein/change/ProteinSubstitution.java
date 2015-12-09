package de.charite.compbio.jannovar.hgvs.protein.change;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.Translator;
import de.charite.compbio.jannovar.hgvs.protein.ProteinPointLocation;

/**
 * Represents a missense protein substitution, for example "Trp2Ala" or "T2A".
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ProteinSubstitution extends ProteinChange {

	/** location of the substituted amino acid */
	private final ProteinPointLocation location;
	/** target amino acid */
	private final String targetAA;

	/**
	 * Factory method for direct construction from source AA, position, and targetAA.
	 */
	public static ProteinSubstitution build(boolean onlyPredicted, String sourceAA, int pos, String targetAA) {
		return new ProteinSubstitution(onlyPredicted, ProteinPointLocation.build(sourceAA, pos), targetAA);
	}

	public static ProteinSubstitution buildWithOffset(boolean onlyPredicted, String sourceAA, int pos, int offset,
			String targetAA) {
		return new ProteinSubstitution(onlyPredicted, ProteinPointLocation.buildWithOffset(sourceAA, pos, offset),
				targetAA);
	}

	public static ProteinSubstitution buildDownstreamOfTerminal(boolean onlyPredicted, String sourceAA, int pos,
			String targetAA) {
		return new ProteinSubstitution(onlyPredicted, ProteinPointLocation.buildDownstreamOfTerminal(sourceAA, pos),
				targetAA);
	}

	/**
	 * @param location
	 *            location of the changed AA
	 * @param targetAA
	 *            amino acid to change to
	 */
	public ProteinSubstitution(boolean onlyPredicted, ProteinPointLocation location, String targetAA) {
		super(onlyPredicted);
		this.location = location;
		this.targetAA = targetAA;
	}

	/** @return location of the changed protein */
	public ProteinPointLocation getLocation() {
		return location;
	}

	/** @return amino acid to change to */
	public String getTargetAA() {
		return targetAA;
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		if (code == AminoAcidCode.THREE_LETTER)
			return wrapIfOnlyPredicted(location.toHGVSString(code) + Translator.getTranslator().toLong(targetAA));
		else
			return wrapIfOnlyPredicted(location.toHGVSString(code) + targetAA);
	}

	@Override
	public String toString() {
		return "ProteinSubstitution [location=" + location + ", targetAA=" + targetAA + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
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
		ProteinSubstitution other = (ProteinSubstitution) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
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
		return new ProteinSubstitution(onlyPredicted, this.location, this.targetAA);
	}

}
