package de.charite.compbio.jannovar.hgvs.legacy;

import com.google.common.base.Joiner;

/**
 * Representation of a {@link LegacyChagne} on a reference sequence.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class LegacyVariant {

	/** name of reference that the variant is on */
	private final String reference;
	/** the change of the variant */
	private final LegacyChange change;

	/** Initialize object with the given values */
	public LegacyVariant(String reference, LegacyChange change) {
		super();
		this.reference = reference;
		this.change = change;
	}

	/** Return human readable legacy HGVS notation */
	public String toLegacyString() {
		return Joiner.on("").join(reference, ":", change.toLegacyString());
	}

	public String getReference() {
		return reference;
	}

	public LegacyChange getChange() {
		return change;
	}

	@Override
	public String toString() {
		return "LegacyVariant [reference=" + reference + ", change=" + change + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((change == null) ? 0 : change.hashCode());
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
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
		LegacyVariant other = (LegacyVariant) obj;
		if (change == null) {
			if (other.change != null)
				return false;
		} else if (!change.equals(other.change))
			return false;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
			return false;
		return true;
	}

}
