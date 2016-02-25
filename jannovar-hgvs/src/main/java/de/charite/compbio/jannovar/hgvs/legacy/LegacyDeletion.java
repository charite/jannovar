package de.charite.compbio.jannovar.hgvs.legacy;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;

/**
 * Representation of a legacy notation deletion.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class LegacyDeletion extends LegacyChange {

	/** deleted string */
	private final NucleotideSeqDescription deletedSeq;

	/** Construct new legacy deletion with the given values */
	public LegacyDeletion(LegacyLocation location, NucleotideSeqDescription deletedSeq) {
		super(location);
		this.deletedSeq = deletedSeq;
	}

	/** @return deleted sequence */
	public NucleotideSeqDescription getDeletedSeq() {
		return deletedSeq;
	}

	@Override
	public String toLegacyString() {
		return Joiner.on("").join(location.toLegacyString(), "del", deletedSeq.toHGVSString());
	}

	@Override
	public String toString() {
		return "LegacyDeletion [deletedSeq=" + deletedSeq.toHGVSString() + ", location=" + location + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((deletedSeq == null) ? 0 : deletedSeq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LegacyDeletion other = (LegacyDeletion) obj;
		if (deletedSeq == null) {
			if (other.deletedSeq != null)
				return false;
		} else if (!deletedSeq.equals(other.deletedSeq))
			return false;
		return true;
	}

}
