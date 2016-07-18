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

	public void extendHeader(VCFHeader vcfHeader) {
		extendHeader(vcfHeader, "");
	}

	public static String key() {
		return key("");
	}

	public static String key(String prefix) {
		return prefix + "INHERITANCE";
	}

	public void extendHeader(VCFHeader vcfHeader, String prefix) {
		VCFInfoHeaderLine inheritanceLine = new VCFInfoHeaderLine(key(prefix), VCFHeaderLineCount.UNBOUNDED,
				VCFHeaderLineType.String, "Compatible Mendelian inheritance modes (AD, AR, XD, XR)");
		vcfHeader.addMetaDataLine(inheritanceLine);
	}

}
