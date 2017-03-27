package de.charite.compbio.jannovar.mendel.bridge;

import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/**
 * Helper class for extending VCF header for mendelian inheritance annotation
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class MendelVCFHeaderExtender {

	/** Constant for autosomal dominant */
	public static String AD = "AD";
	/** Constant for autosomal recessive */
	public static String AR = "AR";
	/** Constant for X dominant */
	public static String XD = "XD";
	/** Constant for X recessive */
	public static String XR = "XR";

	/** Constant for autosomal recessive hom. alt. */
	public static String AR_HOM_ALT = "AR_HOM_ALT";
	/** Constant for autosomal recessive compound het. */
	public static String AR_COMP_HET = "AR_COMP_HET";
	/** Constant for X recessive hom. alt. */
	public static String XR_HOM_ALT = "XR_HOM_ALT";
	/** Constant for X recessive compound het. */
	public static String XR_COMP_HET = "XR_COMP_HET";

	public void extendHeader(VCFHeader vcfHeader) {
		extendHeader(vcfHeader, "");
	}

	public static String key() {
		return key("");
	}

	public static String key(String prefix) {
		return prefix + "INHERITANCE";
	}

	public static String keySub() {
		return keySub("");
	}

	public static String keySub(String prefix) {
		return prefix + "INHERITANCE_RECESSIVE_DETAIL";
	}

	public void extendHeader(VCFHeader vcfHeader, String prefix) {
		VCFInfoHeaderLine inheritanceLine = new VCFInfoHeaderLine(key(prefix), VCFHeaderLineCount.UNBOUNDED,
				VCFHeaderLineType.String, "Compatible Mendelian inheritance modes (AD, AR, XD, XR)");
		vcfHeader.addMetaDataLine(inheritanceLine);
		VCFInfoHeaderLine subInheritanceLine = new VCFInfoHeaderLine(keySub(prefix), VCFHeaderLineCount.UNBOUNDED,
				VCFHeaderLineType.String,
				"Extra annotation for recessive inheritance sub type (AR_HOM_ALT, AR_COMP_HET, XR_HOM_ALT, XR_COMP_HET)");
		vcfHeader.addMetaDataLine(subInheritanceLine);
	}

}
