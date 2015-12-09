package de.charite.compbio.jannovar.hgvs.legacy;

import com.google.common.base.Joiner;

/**
 * Exonic location for legacy variants, e.g. <code>"IVS3+3"</code>.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class LegacyLocation {

	/** the location type */
	private final LegacyLocationType locationType;
	/** intron or exon number, start counting at 1 */
	private final int featureNo;
	/** offset, positive numbers mean "+x" */
	private final int baseOffset;

	/** Construct {@link LegacyLocationType#INTRONIC} location */
	public static LegacyLocation buildIntronicLocation(int intronNo, int baseOffset) {
		return new LegacyLocation(LegacyLocationType.INTRONIC, intronNo, baseOffset);
	}

	/** Construct {@link LegacyLocationType#EXONIC} location */
	public static LegacyLocation buildExonicLocation(int exonNo, int baseOffset) {
		return new LegacyLocation(LegacyLocationType.EXONIC, exonNo, baseOffset);
	}

	/** Initialize object with the given values */
	public LegacyLocation(LegacyLocationType locationType, int featureNo, int baseOffset) {
		super();
		this.locationType = locationType;
		this.featureNo = featureNo;
		this.baseOffset = baseOffset;
	}

	/** @return location type */
	public LegacyLocationType getLocationType() {
		return locationType;
	}

	/** @return intron or exon number, counting starts at 1 */
	public int getFeatureNo() {
		return featureNo;
	}

	/** @return offset, positive numbers mean "+x" */
	public int getBaseOffset() {
		return baseOffset;
	}

	/** @return legacy string for the legacy location */
	public String toLegacyString() {
		String maybePlus = (baseOffset > 0) ? "+" : "";
		return Joiner.on("").skipNulls().join(locationType.getLegacyString(), featureNo, maybePlus, baseOffset);
	}

	@Override
	public String toString() {
		return "LegacyLocation [locationType=" + locationType + ", featureNo=" + featureNo + ", baseOffset="
				+ baseOffset + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + baseOffset;
		result = prime * result + featureNo;
		result = prime * result + ((locationType == null) ? 0 : locationType.hashCode());
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
		LegacyLocation other = (LegacyLocation) obj;
		if (baseOffset != other.baseOffset)
			return false;
		if (featureNo != other.featureNo)
			return false;
		if (locationType != other.locationType)
			return false;
		return true;
	}

}
