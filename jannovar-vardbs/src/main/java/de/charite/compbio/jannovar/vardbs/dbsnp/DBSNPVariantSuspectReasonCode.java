package de.charite.compbio.jannovar.vardbs.dbsnp;

/**
 * Code for a variant being suspicious
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
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
