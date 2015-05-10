package de.charite.compbio.jannovar.hgvs.change.protein;

import de.charite.compbio.jannovar.hgvs.change.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.change.ConvertibleToHGVSString;

/**
 * Represents a missense protein substitution, for example "Trp2Ala" or "T2A".
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class ProteinSubstitution implements ConvertibleToHGVSString {

	/** location of the substituted amino acid */
	private final ProteinPointLocation location;
	/** target amino acid */
	private final String targetAA;

	/**
	 * @param location
	 *            location of the changed AA
	 * @param targetAA
	 *            amino acid to change to
	 */
	public ProteinSubstitution(ProteinPointLocation location, String targetAA) {
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
	public String toHGVSString() {
		return toHGVSString(AminoAcidCode.THREE_LETTER);
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		if (code == AminoAcidCode.THREE_LETTER)
			return location.toHGVSString(code) + Translator.getTranslator().toLong(targetAA);
		else
			return location.toHGVSString(code) + targetAA;
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

}
