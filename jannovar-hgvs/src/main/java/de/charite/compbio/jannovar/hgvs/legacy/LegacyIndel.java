package de.charite.compbio.jannovar.hgvs.legacy;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;

/**
 * Representation of a legacy notation substitution.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class LegacyIndel extends LegacyChange {

	/** deleted string */
	private final NucleotideSeqDescription deletedSeq;
	/** inserted string */
	private final NucleotideSeqDescription insertedSeq;

	/** Construct new legacy substitution with the given values */
	public LegacyIndel(LegacyLocation location, NucleotideSeqDescription deletedSeq,
			NucleotideSeqDescription insertedSeq) {
		super(location);
		this.deletedSeq = deletedSeq;
		this.insertedSeq = insertedSeq;
	}

	/** @return replaced sequence */
	public NucleotideSeqDescription getDeletedSeq() {
		return deletedSeq;
	}

	/** @return sequence to replace with */
	public NucleotideSeqDescription getInsertedSeq() {
		return insertedSeq;
	}

	@Override
	public String toLegacyString() {
		return Joiner.on("").join(location.toLegacyString(), "del", deletedSeq.toHGVSString(), "ins",
				insertedSeq.toHGVSString());
	}

	@Override
	public String toString() {
		return "LegacySubstitution [location=" + location + ", fromSeq=" + deletedSeq + ", toSeq=" + insertedSeq + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deletedSeq == null) ? 0 : deletedSeq.hashCode());
		result = prime * result + ((insertedSeq == null) ? 0 : insertedSeq.hashCode());
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
		LegacyIndel other = (LegacyIndel) obj;
		if (deletedSeq == null) {
			if (other.deletedSeq != null)
				return false;
		} else if (!deletedSeq.equals(other.deletedSeq))
			return false;
		if (insertedSeq == null) {
			if (other.insertedSeq != null)
				return false;
		} else if (!insertedSeq.equals(other.insertedSeq))
			return false;
		return true;
	}

}
