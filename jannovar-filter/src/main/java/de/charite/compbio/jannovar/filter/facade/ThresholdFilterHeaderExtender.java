package de.charite.compbio.jannovar.filter.facade;

import htsjdk.variant.vcf.VCFFilterHeaderLine;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineType;

/**
 * Code for adding headers for threshold-based filters to VCF files
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ThresholdFilterHeaderExtender {

	// Genotype-wise filter strings

	/** Maximal coverage */
	public static String FILTER_GT_MAX_COV = "MaxCov";
	/** Minimal coverage in case of het call */
	public static String FILTER_GT_MIN_COV_HET = "MinCovHet";
	/** Minimal coverage in case of hom alt call */
	public static String FILTER_GT_MIN_COV_HOM_ALT = "MinCovHomAlt";
	/** Minimal genotyp quality */
	public static String FILTER_GT_MIN_GQ = "MinGq";
	/** Minimal alternative allele fraction for het */
	public static String FILTER_GT_MIN_AAF_HET = "MinAafHet";
	/** Maximal alternative allele fraction for het */
	public static String FILTER_GT_MAX_AAF_HET = "MaxAafHet";
	/** Minimal alternative allele fraction for hom alt */
	public static String FILTER_GT_MIN_AAF_HOM_ALT = "MinAafHomAlt";
	/** Minimal alternative allele fraction for hom ref */
	public static String FILTER_GT_MAX_AAF_HOM_REF = "MaxAafHomRef";

	// Variant-wise filter strings

	/** All affected individual's genotypes are filtered */
	public static String FILTER_VAR_ALL_AFFECTED_GTS_FILTERED = "AllAffGtFiltered";

	/** Highest frequency in any population higher than threshold for AD */
	public static String FILTER_VAR_MAX_FREQUENCY_AD = "MaxFreqAd";

	/** Highest frequency in any population higher than threshold for AR */
	public static String FILTER_VAR_MAX_FREQUENCY_AR = "MaxFreqAr";

	/** Configuration */
	private final ThresholdFilterOptions options;

	public ThresholdFilterHeaderExtender(ThresholdFilterOptions options) {
		this.options = options;
	}

	/**
	 * Add header entries.
	 * 
	 * @param header
	 *            The {@link VCFHeader} to extend.
	 */
	public void addHeaders(VCFHeader header) {
		if (!header.hasFormatLine("FT"))
			header.addMetaDataLine(
					new VCFFormatHeaderLine("FT", 1, VCFHeaderLineType.String, "Filters applied to genotype call"));

		header.addMetaDataLine(
				new VCFFilterHeaderLine(FILTER_GT_MAX_COV, "Genotype has coverage >" + options.getMaxCov()));
		header.addMetaDataLine(new VCFFilterHeaderLine(FILTER_GT_MIN_COV_HET,
				"Het. genotype call has coverage <" + options.getMinGtCovHet()));
		header.addMetaDataLine(new VCFFilterHeaderLine(FILTER_GT_MIN_COV_HOM_ALT,
				"Hom. alt genotype call has coverage <" + options.getMinGtCovHomAlt()));
		header.addMetaDataLine(
				new VCFFilterHeaderLine(FILTER_GT_MIN_GQ, "Genotype has quality (GQ) <" + options.getMinGtGq()));
		header.addMetaDataLine(new VCFFilterHeaderLine(FILTER_GT_MIN_AAF_HET,
				"Het. genotype has alternative allele fraction <" + options.getMinGtAafHet()));
		header.addMetaDataLine(new VCFFilterHeaderLine(FILTER_GT_MAX_AAF_HET,
				"Het. genotype has alternative allele fraction >" + options.getMaxGtAafHet()));
		header.addMetaDataLine(new VCFFilterHeaderLine(FILTER_GT_MIN_AAF_HOM_ALT,
				"Hom. alt genotype has alternative allele fraction <" + options.getMinGtAafHomAlt()));
		header.addMetaDataLine(new VCFFilterHeaderLine(FILTER_GT_MAX_AAF_HOM_REF,
				"Wild-type genotype has AAF >" + options.getMaxGtAafHomRef()));

		header.addMetaDataLine(new VCFFilterHeaderLine(FILTER_VAR_ALL_AFFECTED_GTS_FILTERED,
				"The genotype calls of all affected individuals have been filtered for this variant."));

		header.addMetaDataLine(new VCFFilterHeaderLine(FILTER_VAR_MAX_FREQUENCY_AD,
				"Variant frequency >" + options.getMaxAlleleFrequencyAd() + " (threshold for AD inheritance)"));
		header.addMetaDataLine(new VCFFilterHeaderLine(FILTER_VAR_MAX_FREQUENCY_AR,
				"Variant frequency >" + options.getMaxAlleleFrequencyAd() + " (threshold for AR inheritance)"));
	}

}
