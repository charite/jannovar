package de.charite.compbio.jannovar.hgvs.nts.variant;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.SequenceType;

/**
 * Base class for nucleotide changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public abstract class NucleotideVariant extends HGVSVariant {

	public static final int NO_TRANSCRIPT_VERSION = -1;

	/** type fo the underlying sequence */
	protected final SequenceType seqType;
	/** reference ID */
	protected final String refID;
	/** protein ID */
	protected final String proteinID;
	/** protein version, {@link #NO_TRANSCRIPT_VERSION} for no version */
	protected final int transcriptVersion;

	/** Set variant's reference ID, protein ID is null, version is {@link #NO_PROTEIN_VERSION}. */
	public NucleotideVariant(SequenceType seqType, String refID) {
		this.seqType = seqType;
		this.refID = refID;
		this.proteinID = null;
		this.transcriptVersion = NO_TRANSCRIPT_VERSION;
	}

	/** Set variant's reference ID, protein ID, and protein version to the given value */
	public NucleotideVariant(SequenceType seqType, String refID, String proteinID, int transcriptVersion) {
		this.seqType = seqType;
		this.refID = refID;
		this.proteinID = proteinID;
		this.transcriptVersion = transcriptVersion;
	}

	/** @return type of sequence that the change is one */
	public SequenceType getSeqType() {
		return seqType;
	}

	/** @return the reference/transcript ID */
	public String getRefID() {
		return refID;
	}

	/** @return transcript version, {@link #NO_TRANSCRIPT_VERSION} if no version is given */
	public int getTranscriptVersion() {
		return transcriptVersion;
	}

	/** @return protein ID or <code>null</code> if none */
	public String getProteinID() {
		return proteinID;
	}

	/** @return the reference/transcript ID with version, if set */
	public String getRefIDWithVersion() {
		if (transcriptVersion == NO_TRANSCRIPT_VERSION)
			return getRefID();
		else
			return Joiner.on("").join(getRefID(), ".", transcriptVersion);
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		return toHGVSString();
	}

	/** @return sequence name prefix, e.g. <code>"NM_000109.3(DMD)"</code>, or <code>"NM_000109.3"</code>. */
	public String getSequenceNamePrefix() {
		if (proteinID == null)
			return getRefIDWithVersion();
		else
			return Joiner.on("").join(getRefIDWithVersion(), "(", proteinID, ")");
	}

	@Override
	public String toString() {
		return "NucleotideVariant [seqType=" + seqType + ", refID=" + refID + ", proteinID=" + proteinID
				+ ", transcriptVersion=" + transcriptVersion + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((proteinID == null) ? 0 : proteinID.hashCode());
		result = prime * result + ((refID == null) ? 0 : refID.hashCode());
		result = prime * result + ((seqType == null) ? 0 : seqType.hashCode());
		result = prime * result + transcriptVersion;
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
		NucleotideVariant other = (NucleotideVariant) obj;
		if (proteinID == null) {
			if (other.proteinID != null)
				return false;
		} else if (!proteinID.equals(other.proteinID))
			return false;
		if (refID == null) {
			if (other.refID != null)
				return false;
		} else if (!refID.equals(other.refID))
			return false;
		if (seqType != other.seqType)
			return false;
		if (transcriptVersion != other.transcriptVersion)
			return false;
		return true;
	}

}
