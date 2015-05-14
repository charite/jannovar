package de.charite.compbio.jannovar.hgvs.protein;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.ConvertibleToHGVSString;
import de.charite.compbio.jannovar.hgvs.Translator;

/**
 * Represent one position in a protein.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class ProteinPointLocation implements ConvertibleToHGVSString {

	/** 0-based position in the protein */
	private final int pos;
	/** 1-letter code of the AA at this position */
	private final String aa;

	/**
	 * @param pos
	 *            0-based position in the protein
	 * @param aa
	 *            1-letter code of the amino acid
	 */
	public ProteinPointLocation(int pos, String aa) {
		this.pos = pos;
		this.aa = aa;
	}

	/** @return 0-based position in the amino acid */
	public int getPos() {
		return pos;
	}

	/** @return 1-letter code of the amino acid */
	public String getAA() {
		return aa;
	}

	@Override
	public String toHGVSString() {
		return toHGVSString(AminoAcidCode.THREE_LETTER);
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		if (code == AminoAcidCode.THREE_LETTER)
			return Translator.getTranslator().toLong(aa.charAt(0)) + (pos + 1);
		else
			return aa + (pos + 1);
	}

	@Override
	public String toString() {
		return "ProteinPointLocation [pos=" + pos + ", aa=" + aa + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aa == null) ? 0 : aa.hashCode());
		result = prime * result + pos;
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
		ProteinPointLocation other = (ProteinPointLocation) obj;
		if (aa == null) {
			if (other.aa != null)
				return false;
		} else if (!aa.equals(other.aa))
			return false;
		if (pos != other.pos)
			return false;
		return true;
	}

}
