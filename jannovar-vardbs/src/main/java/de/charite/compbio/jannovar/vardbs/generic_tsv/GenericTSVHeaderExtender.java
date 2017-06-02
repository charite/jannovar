package de.charite.compbio.jannovar.vardbs.generic_tsv;

import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/**
 * Helper class for extending {@link VCFHeader}s for generic TSV annotations.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericTSVHeaderExtender extends VCFHeaderExtender {

	private GenericTSVAnnotationOptions tsvOptions;

	public GenericTSVHeaderExtender(GenericTSVAnnotationOptions options) {
		super(options);
		this.tsvOptions = options;
	}

	@Override
	public String getDefaultPrefix() {
		return "TSV_";
	}

	@Override
	public void addHeaders(VCFHeader header, String prefix) {
		addHeadersInfixes(header, prefix, "", "");

		// Only add the overlapping annotations if the REF and ALT columns are
		// configured at all
		if (tsvOptions.getAltAlleleColumnIndex() > 0 && tsvOptions.getRefAlleleColumnIndex() > 0) {
			if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching())
				addHeadersInfixes(header, prefix, "OVL_",
						" (requiring no genotype match, only position overlap)");
		}
	}

	/**
	 * Add INFO header lines.
	 * 
	 * <p>
	 * In the case of annotating a TSV file with REF and ALT columns, annotate reference and
	 * alternative alleles. Otherwise, annotate with a list of values.
	 * </p>
	 */
	private void addHeadersInfixes(VCFHeader header, String prefix, String infix, String note) {
		for (String colName : tsvOptions.getColumnNames()) {
			final GenericTSVValueColumnDescription desc = tsvOptions.getValueColumnDescriptions()
					.get(colName);
			final VCFHeaderLineCount count;
			if (tsvOptions.getAltAlleleColumnIndex() > 0 && tsvOptions.getRefAlleleColumnIndex() > 0
					&& options.isReportOverlapping() && !options.isReportOverlappingAsMatching()) {
				if (tsvOptions.isRefAlleleAnnotated()) {
					count = VCFHeaderLineCount.R;
				} else {
					count = VCFHeaderLineCount.A;
				}
			} else {
				count = VCFHeaderLineCount.UNBOUNDED;
			}

			final VCFInfoHeaderLine headerLine = new VCFInfoHeaderLine(
					prefix + infix + desc.getFieldName(), count, desc.getValueType(),
					desc.getFieldDescription() + note);
			header.addMetaDataLine(headerLine);
		}
	}

}
