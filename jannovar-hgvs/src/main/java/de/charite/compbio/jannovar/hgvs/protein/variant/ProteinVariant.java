package de.charite.compbio.jannovar.hgvs.protein.variant;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.HGVSVariant;

/**
 * Base class for protein changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public abstract class ProteinVariant extends HGVSVariant {

	public static final int NO_PROTEIN_ISOFORM = -1;

	/** reference ID */
	protected final String refID;
	/** protein ID */
	protected final String proteinID;
	/** protein isoform, {@link #NO_PROTEIN_ISOFORM} for no isoform */
	protected final int proteinIsoform;

	/** Set variant's reference ID, protein ID is null, isoform is {@link #NO_PROTEIN_ISOFORM}. */
	public ProteinVariant(String refID) {
		this.refID = refID;
		this.proteinID = null;
		this.proteinIsoform = NO_PROTEIN_ISOFORM;
	}

	/** Set variant's reference ID, protein ID, and protein isoform to the given value */
	public ProteinVariant(String refID, String proteinID, int proteinIsoform) {
		this.refID = refID;
		this.proteinID = proteinID;
		this.proteinIsoform = proteinIsoform;
	}

	@Override
	public String toHGVSString() {
		return toHGVSString(AminoAcidCode.THREE_LETTER);
	}

	/** @return the protein ID */
	public String getProteinID() {
		return proteinID;
	}

	/** @return sequence name prefix, e.g. <code>"NM_000109.3(DMD_v2)"</code>, or <code>"NM_000109.3"</code>. */
	public String getSequenceNamePrefix() {
		String proteinID = this.proteinID;
		if (proteinID != null && proteinIsoform != NO_PROTEIN_ISOFORM)
			proteinID += "_i" + proteinIsoform;

		if (proteinID == null)
			return refID;
		else
			return Joiner.on("").join(refID, "(", proteinID, ")");
	}

	@Override
	public String toString() {
		return "ProteinVariant [refID=" + refID + ", proteinID=" + proteinID + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((proteinID == null) ? 0 : proteinID.hashCode());
		result = prime * result + proteinIsoform;
		result = prime * result + ((refID == null) ? 0 : refID.hashCode());
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
		if (proteinIsoform != other.proteinIsoform)
			return false;
		if (refID == null) {
			if (other.refID != null)
				return false;
		} else if (!refID.equals(other.refID))
			return false;
		return true;
	}

}
