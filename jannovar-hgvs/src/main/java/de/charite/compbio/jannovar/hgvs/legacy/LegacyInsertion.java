package de.charite.compbio.jannovar.hgvs.legacy;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;

/**
 * Representation of a legacy notation insertion.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class LegacyInsertion extends LegacyChange {

	/** deleted string */
	private final NucleotideSeqDescription insertedSeq;

	/** Construct new legacy substitution with the given values */
	public LegacyInsertion(LegacyLocation location, NucleotideSeqDescription insertedSeq) {
		super(location);
		this.insertedSeq = insertedSeq;
	}

	/** @return deleted sequence */
	public NucleotideSeqDescription getDeletedSeq() {
		return insertedSeq;
	}

	@Override
	public String toLegacyString() {
		return Joiner.on("").join(location.toLegacyString(), "ins", insertedSeq.toHGVSString());
	}

	@Override
	public String toString() {
		return "LegacyDeletion [insertedSeq=" + insertedSeq + ", location=" + location + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((insertedSeq == null) ? 0 : insertedSeq.hashCode());
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
		LegacyInsertion other = (LegacyInsertion) obj;
		if (insertedSeq == null) {
			if (other.insertedSeq != null)
				return false;
		} else if (!insertedSeq.equals(other.insertedSeq))
			return false;
		return true;
	}

}
