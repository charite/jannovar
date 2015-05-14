package de.charite.compbio.jannovar.hgvs.protein.variant;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.ConvertibleToHGVSString;

/**
 * Base class for protein changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public abstract class ProteinVariant implements ConvertibleToHGVSString {

	/** protein ID */
	protected final String proteinID;

	/** Set variant's protein ID to the given value */
	public ProteinVariant(String proteinID) {
		this.proteinID = proteinID;
	}

	@Override
	public String toHGVSString() {
		return toHGVSString(AminoAcidCode.THREE_LETTER);
	}

	/** @return the protein ID */
	public String getProteinID() {
		return proteinID;
	}

	@Override
	public String toString() {
		return "ProteinVariant [proteinID=" + proteinID + ", toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((proteinID == null) ? 0 : proteinID.hashCode());
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
		ProteinVariant other = (ProteinVariant) obj;
		if (proteinID == null) {
			if (other.proteinID != null)
				return false;
		} else if (!proteinID.equals(other.proteinID))
			return false;
		return true;
	}

}
