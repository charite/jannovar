package de.charite.compbio.jannovar.hgvs.nts.variant;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.ConvertibleToHGVSString;
import de.charite.compbio.jannovar.hgvs.SequenceType;

/**
 * Base class for nucleotide changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public abstract class NucleotideVariant implements ConvertibleToHGVSString {

	/** type fo the underlying sequence */
	protected final SequenceType seqType;
	/** reference/transcript ID */
	protected final String seqID;

	/** Initialize with the given {@link SequenceType} and sequence ID */
	public NucleotideVariant(SequenceType seqType, String seqID) {
		this.seqType = seqType;
		this.seqID = seqID;
	}

	/** @return type of sequence that the change is one */
	public SequenceType getSeqType() {
		return seqType;
	}

	/** @return the reference/transript ID */
	public String getSeqID() {
		return seqID;
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		return toHGVSString();
	}

	@Override
	public String toString() {
		return "NucleotideVariant [seqType=" + seqType + ", seqID=" + seqID + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seqID == null) ? 0 : seqID.hashCode());
		result = prime * result + ((seqType == null) ? 0 : seqType.hashCode());
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
		if (seqID == null) {
			if (other.seqID != null)
				return false;
		} else if (!seqID.equals(other.seqID))
			return false;
		if (seqType != other.seqType)
			return false;
		return true;
	}

}
