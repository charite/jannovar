package de.charite.compbio.jannovar.hgvs.nts;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.ConvertibleToHGVSString;

/**
 * Specification for a nucleotide sequence, e.g. deleted or inserted.
 *
 * This can be either just a count or a nucleotide sequence.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class NucleotideSeqDescription implements ConvertibleToHGVSString {

	/** sentinel value for invalid nucleotide count */
	public static final int INVALID_NT_COUNT = -1;

	/** String of nucleotides, null if there are none */
	private final String nts;
	/** Length of nucleotide string, fallback if {@link #nts} is empty */
	private final int ntCount;

	/**
	 * Construct as reporting the empty string.
	 */
	public NucleotideSeqDescription() {
		this.nts = null;
		this.ntCount = -1;
	}

	/**
	 * Construct with nucleotide string length and <code>null</code> string.
	 */
	public NucleotideSeqDescription(int ntCount) {
		this.nts = null;
		this.ntCount = ntCount;
	}

	/**
	 * Construct with nucleotide string length.
	 */
	public NucleotideSeqDescription(String nts) {
		this.nts = nts;
		this.ntCount = nts.length();
	}

	/** @return <code>true</code> if the nucleotide sequence was specified */
	public boolean hasNucleotides() {
		return this.nts != null;
	}

	/**
	 * Get nucleotidestring, only available if {@link #hasNucleotides} returns <code>true</code>.
	 *
	 * @return String of nucleotides in 1-character encoding
	 */
	public String getNucleotides() {
		return this.nts;
	}

	/** @return <code>true</code> if nothing is to be printed. */
	public boolean isBlank() {
		return ntCount == -1;
	}

	/** @return length of amino acids */
	public int length() {
		return this.ntCount;
	}

	@Override
	public String toHGVSString() {
		return toHGVSString(AminoAcidCode.THREE_LETTER);
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		if (isBlank())
			return "";
		else if (this.nts != null)
			return this.nts;
		else
			return Integer.toString(this.ntCount);
	}

	@Override
	public String toString() {
		return "NucleotideSeqDescription [nts=" + nts + ", ntCount=" + ntCount + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ntCount;
		result = prime * result + ((nts == null) ? 0 : nts.hashCode());
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
		NucleotideSeqDescription other = (NucleotideSeqDescription) obj;
		if (ntCount != other.ntCount)
			return false;
		if (nts == null) {
			if (other.nts != null)
				return false;
		} else if (!nts.equals(other.nts))
			return false;
		return true;
	}

}
