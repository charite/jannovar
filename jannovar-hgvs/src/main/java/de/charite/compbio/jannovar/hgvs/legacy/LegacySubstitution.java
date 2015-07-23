package de.charite.compbio.jannovar.hgvs.legacy;

import com.google.common.base.Joiner;

/**
 * Representation of a legacy notation substitution.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class LegacySubstitution extends LegacyChange {

	/** source string */
	private final String fromSeq;
	/** target string */
	private final String toSeq;

	/** Construct new legacy substitution with the given values */
	public LegacySubstitution(LegacyLocation location, String fromSeq, String toSeq) {
		super(location);
		this.fromSeq = fromSeq;
		this.toSeq = toSeq;
	}

	/** @return replaced sequence */
	public String getFromSeq() {
		return fromSeq;
	}

	/** @return sequence to replace with */
	public String getToSeq() {
		return toSeq;
	}

	@Override
	public String toLegacyString() {
		return Joiner.on("").join(location.toLegacyString(), fromSeq, ">", toSeq);
	}

	@Override
	public String toString() {
		return "LegacySubstitution [location=" + location + ", fromSeq=" + fromSeq + ", toSeq=" + toSeq + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromSeq == null) ? 0 : fromSeq.hashCode());
		result = prime * result + ((toSeq == null) ? 0 : toSeq.hashCode());
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
		LegacySubstitution other = (LegacySubstitution) obj;
		if (fromSeq == null) {
			if (other.fromSeq != null)
				return false;
		} else if (!fromSeq.equals(other.fromSeq))
			return false;
		if (toSeq == null) {
			if (other.toSeq != null)
				return false;
		} else if (!toSeq.equals(other.toSeq))
			return false;
		return true;
	}

}
