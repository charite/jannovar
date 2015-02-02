package de.charite.compbio.jannovar.htsjdk;

/**
 * Describes selection of info fields.
 */
public enum InfoFields {
	/** new standard field <code>ANN</code> and corresponding header */
	VCF_ANN,
	/** old Jannovar fields <code>EFFECT</code> and <code>HGVS</code> and corresponding headers */
	EFFECT_HGVS,
	/** all three <code>ANN</code>, <code>EFFECT</code>, and <code>HGVS</code> */
	BOTH,
	/** none of them */
	NONE;

	/**
	 * @return value corresponding to the selection by flags
	 */
	public static InfoFields build(boolean useAnn, boolean useEffectHGVS) {
		if (!useAnn && !useEffectHGVS)
			return NONE;
		else if (!useAnn && useEffectHGVS)
			return EFFECT_HGVS;
		else if (useAnn && !useEffectHGVS)
			return VCF_ANN;
		else
			return BOTH;
	}
}