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

	/** 1-letter code of the AA at this position */
	private final String aa;
	/** 0-based position in the protein */
	private final int pos;
	/** offset for changes in intronic or upstream regions, 0 for no offset */
	private final int offset;
	/** positions is downstream of the translational terminal codon, e.g. for <code>Gln*1</code> */
	private final boolean downstreamOfTerminal;

	public static ProteinPointLocation build(String aa, int pos) {
		return new ProteinPointLocation(aa, pos, 0, false);
	}

	public static ProteinPointLocation buildWithOffset(String aa, int pos, int offset) {
		return new ProteinPointLocation(aa, pos, offset, false);
	}

	public static ProteinPointLocation buildDownstreamOfTerminal(String aa, int pos) {
		return new ProteinPointLocation(aa, pos, 0, true);
	}


	/**
	 * @param aa
	 *            1-letter code of the amino acid
	 * @param pos
	 *            0-based position in the protein
	 * @param offset
	 *            for changes in intronic or upstream regions, 0 for no offset
	 */
	public ProteinPointLocation(String aa, int pos, int offset, boolean downstreamOfTerminal) {
		this.aa = aa;
		this.pos = pos;
		this.offset = offset;
		this.downstreamOfTerminal = downstreamOfTerminal;
	}

	/** @return 0-based position in the amino acid */
	public int getPos() {
		return pos;
	}

	/** @return 1-letter code of the amino acid */
	public String getAA() {
		return aa;
	}

	/** @return offset for changes in intronic or upstream regions, 0 for no offset */
	public int getOffset() {
		return offset;
	}

	/** @return positions is downstream of the translational terminal codon, e.g. for <code>Gln*1</code> */
	public boolean isDownstreamOfTerminal() {
		return downstreamOfTerminal;
	}

	@Override
	public String toHGVSString() {
		return toHGVSString(AminoAcidCode.THREE_LETTER);
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		String offsetStr = "";
		if (downstreamOfTerminal) {
			if (code == AminoAcidCode.THREE_LETTER)
				return Translator.getTranslator().toLong(aa.charAt(0)) + "*" + (pos + 1);
			else
				return aa + "*" + (pos + 1);
		} else if (offset > 0) {
			offsetStr = "+" + offset;
		} else if (offset < 0) {
			offsetStr = Integer.toString(offset);
		}
		
		if (code == AminoAcidCode.THREE_LETTER)
			return Translator.getTranslator().toLong(aa.charAt(0)) + (pos + 1) + offsetStr;
		else
			return aa + (pos + 1) + offsetStr;
	}

	@Override
	public String toString() {
		return "ProteinPointLocation [aa=" + aa + ", pos=" + pos + ", offset=" + offset + ", downstreamOfTerminal="
				+ downstreamOfTerminal + ", toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aa == null) ? 0 : aa.hashCode());
		result = prime * result + (downstreamOfTerminal ? 1231 : 1237);
		result = prime * result + offset;
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
		if (downstreamOfTerminal != other.downstreamOfTerminal)
			return false;
		if (offset != other.offset)
			return false;
		if (pos != other.pos)
			return false;
		return true;
	}

}
