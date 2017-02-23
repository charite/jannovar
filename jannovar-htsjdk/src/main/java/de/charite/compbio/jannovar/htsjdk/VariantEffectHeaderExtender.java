package de.charite.compbio.jannovar.htsjdk;

import de.charite.compbio.jannovar.annotation.Annotation;
import htsjdk.variant.vcf.VCFFilterHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/**
 * Code for adding headers for variant effect to VCF files
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class VariantEffectHeaderExtender {

	public static String FILTER_EFFECT_OFF_EXOME = "OffExome";

	/**
	 * Add header entries.
	 * 
	 * @param header
	 *            The {@link VCFHeader} to extend.
	 */
	public void addHeaders(VCFHeader header) {
		// add INFO line for standardized ANN field
		header.addMetaDataLine(
				new VCFInfoHeaderLine("ANN", 1, VCFHeaderLineType.String, Annotation.VCF_ANN_DESCRIPTION_STRING));
		// add FILTER line for standardized OffExome filter
		header.addMetaDataLine(
				new VCFFilterHeaderLine(FILTER_EFFECT_OFF_EXOME, "Variant off-exome in all effect predictions"));
	}

}
