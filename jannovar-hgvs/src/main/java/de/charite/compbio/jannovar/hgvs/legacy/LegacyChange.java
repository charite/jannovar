package de.charite.compbio.jannovar.hgvs.legacy;

/**
 * Base class for legacy changes.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public abstract class LegacyChange {

	/** location of the legacy change */
	protected final LegacyLocation location;

	/** Initialize members with the given parameters */
	public LegacyChange(LegacyLocation location) {
		super();
		this.location = location;
	}

	/** @return {@link LegacyLocation} of the change */
	public LegacyLocation getLocation() {
		return location;
	}

	/** @return legacy string descrition of the change */
	public abstract String toLegacyString();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
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
		LegacyChange other = (LegacyChange) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		return true;
	}

}
