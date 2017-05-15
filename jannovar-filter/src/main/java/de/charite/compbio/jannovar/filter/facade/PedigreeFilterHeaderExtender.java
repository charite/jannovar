package de.charite.compbio.jannovar.filter.facade;

import htsjdk.variant.vcf.VCFFilterHeaderLine;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineType;

/**
 * Code for adding headers for extended pedigree-based filters to VCF files
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class PedigreeFilterHeaderExtender {

	/** Seen alternative allelic depth above threshold in any parent */
	public static String FILTER_GT_DE_NOVO_PARENT_AD2 = "DeNovoParentAd2";

	/** Seen non-ref genotype in sibling */
	public static String FILTER_GT_DE_NOVO_IN_SIBLING = "DeNovoInSibling";

	/** Whether or not a genotype look de novo */
	public static String FORMAT_GT_DE_NOVO = "DN";

	/** Configuration */
	private final PedigreeFilterOptions options;

	public PedigreeFilterHeaderExtender(PedigreeFilterOptions options) {
		this.options = options;
	}

	/**
	 * Add header entries.
	 * 
	 * @param header
	 *            The {@link VCFHeader} to extend.
	 */
	public void addHeaders(VCFHeader header) {
		header.addMetaDataLine(new VCFFilterHeaderLine(FILTER_GT_DE_NOVO_PARENT_AD2,
				"Supporting read count for alternative allele in tentative de novo call > "
						+ options.getDeNovoMaxParentAd2()));
		header.addMetaDataLine(new VCFFilterHeaderLine(FILTER_GT_DE_NOVO_IN_SIBLING,
				"Non-ref genotype also seen in sibling"));

		header.addMetaDataLine(
				new VCFFormatHeaderLine(FORMAT_GT_DE_NOVO, 1, VCFHeaderLineType.Character,
						"Whether the variant looks de novo by genotype, one of {'Y', 'N'}."));
	}

}
