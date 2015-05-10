package de.charite.compbio.jannovar.hgvs.change.protein;

import de.charite.compbio.jannovar.hgvs.change.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.change.ConvertibleToHGVSString;

/**
 * Specification for inserted protein sequence, e.g. deleted or inserted.
 *
 * This can be either just a count or an amino acid sequence.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class ProteinSeqSpecification implements ConvertibleToHGVSString {

	/** String of inserted amino acids, null if there are none */
	private final String aas;
	/** Length of amino acid string, fallback if {@link #aas} is empty */
	private final int aaCount;

	/**
	 * Construct with amino acid string length and <code>null</code> string.
	 */
	public ProteinSeqSpecification(int aaCount) {
		this.aas = null;
		this.aaCount = aaCount;
	}

	/**
	 * Construct with amino acid string length.
	 */
	public ProteinSeqSpecification(String aas) {
		this.aas = aas;
		this.aaCount = aas.length();
	}

	/** @return <code>true</code> if the amino acid sequence was specified */
	public boolean hasAminoAcids() {
		return this.aas != null;
	}

	/**
	 * Get amino acid string, only available if {@link #hasAminoAcids} returns <code>true</code>.
	 *
	 * @return String of amino acids in 1-character encoding
	 */
	public String getAminoAcids() {
		return this.aas;
	}

	/** @return length of amino acids */
	public int length() {
		return this.aaCount;
	}

	@Override
	public String toHGVSString() {
		return toHGVSString(AminoAcidCode.THREE_LETTER);
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		if (this.aas != null && code == AminoAcidCode.ONE_LETTER)
			return this.aas;
		else if (this.aas != null && code == AminoAcidCode.THREE_LETTER)
			return Translator.getTranslator().toLong(this.aas);
		else
			return Integer.toString(this.aaCount);
	}
}
