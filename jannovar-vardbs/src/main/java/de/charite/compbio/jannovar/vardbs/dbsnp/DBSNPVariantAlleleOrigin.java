package de.charite.compbio.jannovar.vardbs.dbsnp;

/**
 * Enum describing variant allele origin
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public enum DBSNPVariantAlleleOrigin {
	/** Unspecified variant allele origin */
	UNSPECIFIED,
	/** Has been reported as germline variant */
	GERMLINE,
	/** Has been reported as somatic variant */
	SOMATIC,
	/** Has been reported both as germline and somatic variant */
	BOTH
}
