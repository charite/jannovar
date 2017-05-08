package de.charite.compbio.jannovar.filter.impl.gt;

import htsjdk.variant.variantcontext.Genotype;

/**
 * Enumeration of suported variant caller
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum SupportedVarCaller {
	/** GATK UG or HC */
	GATK_CALLER,
	/** Bcftools */
	BCFTOOLS,
	/** Freebayes */
	FREEBAYES,
	/** Platypus */
	PLATYPUS;

	/**
	 * @return {@link SupportedVarCaller} as guessed from the FORMAT fields of <code>gt</code>
	 */
	public static SupportedVarCaller guessFromGenotype(Genotype gt) {
		if (gt.hasAnyAttribute("DP") && gt.hasAnyAttribute("DV") && gt.hasAnyAttribute("DPR")) {
			return BCFTOOLS;
		} else if (gt.hasAnyAttribute("GT") && gt.hasAnyAttribute("GQ") && gt.hasAnyAttribute("RO")
				&& gt.hasAnyAttribute("QR") && gt.hasAnyAttribute("AO") && gt.hasAnyAttribute("QA")) {
			return FREEBAYES;
		} else if (gt.hasAnyAttribute("GT") && gt.hasAnyAttribute("AD") && gt.hasAnyAttribute("DP")
				&& gt.hasAnyAttribute("GQ") && gt.hasAnyAttribute("PL")) {
			return GATK_CALLER;
		} else if (gt.hasAnyAttribute("GT") && gt.hasAnyAttribute("GQ") && gt.hasAnyAttribute("NR")
				&& gt.hasAnyAttribute("NV")) {
			return PLATYPUS;
		} else {
			return GATK_CALLER;  // sometimes GATK does not write out anything here... :(
		}
	}

}
