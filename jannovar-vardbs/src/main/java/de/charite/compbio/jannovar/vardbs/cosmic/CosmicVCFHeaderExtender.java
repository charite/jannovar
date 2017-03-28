package de.charite.compbio.jannovar.vardbs.cosmic;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/**
 * Helper class for extending {@link VCFHeader}s for Cosmic annotations.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class CosmicVCFHeaderExtender extends VCFHeaderExtender {

	public CosmicVCFHeaderExtender(DBAnnotationOptions options) {
		super(options);
	}

	@Override
	public String getDefaultPrefix() {
		return "COSMIC_";
	}

	@Override
	public void addHeaders(VCFHeader header, String prefix) {
		addHeadersInfixes(header, prefix, "", "");
		if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching())
			addHeadersInfixes(header, prefix, "OVL_", " (requiring no genotype match, only position overlap)");
	}

	public void addHeadersInfixes(VCFHeader header, String prefix, String infix, String note) {
		VCFInfoHeaderLine cntLine = new VCFInfoHeaderLine(prefix + infix + "CNT", 1, VCFHeaderLineType.Integer,
				"Number of samples in COSMIC having this mutation" + note);
		header.addMetaDataLine(cntLine);

		VCFInfoHeaderLine snpLine = new VCFInfoHeaderLine(prefix + infix + "SNP", 0, VCFHeaderLineType.Flag,
				"Classified as SNP (polymorphism) in COSMIC");
		header.addMetaDataLine(snpLine);

		if ("OVL_".equals(infix)) {
			VCFInfoHeaderLine matchingID = new VCFInfoHeaderLine(prefix + "OVL_IDS", VCFHeaderLineCount.A,
					VCFHeaderLineType.String, "COSMIC IDs with overlapping alternative positions, "
							+ "not necessarily matching alleles, for each alternative allele, separated '|'");
			header.addMetaDataLine(matchingID);
		} else {
			VCFInfoHeaderLine matchingID = new VCFInfoHeaderLine(prefix + "IDS", VCFHeaderLineCount.A,
					VCFHeaderLineType.String,
					"COSMIC IDs with matching alternative positions and alleles, for each "
							+ "alternative alleles, separated by '|'");
			header.addMetaDataLine(matchingID);
		}
	}

}
