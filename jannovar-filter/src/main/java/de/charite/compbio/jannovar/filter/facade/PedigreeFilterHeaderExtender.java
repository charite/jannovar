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

	/** Whether or not parents are reference */
	public static String FORMAT_PARENTS_REF = "PR";

	/** One parent was filtered out. */
	public static String FILTER_GT_ONE_PARENT_FILTERED = "OneParentGtFiltered";

	/** Both parents were filtered out. */
	public static String FILTER_GT_BOTH_PARENTS_FILTERED = "BothParentsGtFiltered";

	/** Configuration */
	private final PedigreeFilterOptions options;

	public PedigreeFilterHeaderExtender(PedigreeFilterOptions options) {
		this.options = options;
	}

	/**
	 * Add header entries.
	 * 
	 * @param header The {@link VCFHeader} to extend.
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
		header.addMetaDataLine(
				new VCFFormatHeaderLine(FORMAT_PARENTS_REF, 1, VCFHeaderLineType.Character,
						"Whether both parent's genotype is reference, one of {'Y', 'N'}."));

		header.addMetaDataLine(new VCFFilterHeaderLine(FILTER_GT_ONE_PARENT_FILTERED,
				"One parent was filtered or no-call, filter child as well, important for inheritance "
						+ "filtration as filtered variants count as no-call which counts as "
						+ "wild-card by default; \"one/both parents filtered\" don't count. (enabled: "
						+ options.isApplyParentGtFilteredFilters() + ")"));
		header.addMetaDataLine(new VCFFilterHeaderLine(FILTER_GT_BOTH_PARENTS_FILTERED,
				"Both parents are filtered or no-call, filter child as well, important for inheritance "
						+ "filtration as filtered variants count as no-call which counts as "
						+ "wild-card by default; \"one/both parents filtered\" don't count. (enabled: "
						+ options.isApplyParentGtFilteredFilters() + ")"));
	}

}
