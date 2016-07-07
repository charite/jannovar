package de.charite.compbio.jannovar.vardbs.dbsnp;

/**
 * Code for a variant being suspicious
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public enum DBSNPVariantSuspectReasonCode {
	/** Code not specified */
	UNSPECIFIED,
	/** Variant in paralogous region */
	PARALOG,
	/** byEST */
	BY_EST,
	/** Old alignment */
	OLD_ALIGN,
	/** Para_EST */
	PARA_EST,
	/** 1kg_failed */
	G1K_FAILED,
	/** other */
	OTHER
}
