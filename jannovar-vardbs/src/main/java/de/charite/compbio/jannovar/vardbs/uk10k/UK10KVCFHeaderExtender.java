package de.charite.compbio.jannovar.vardbs.uk10k;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/**
 * Helper class for extending {@link VCFHeader}s for UK10K annotations.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class UK10KVCFHeaderExtender extends VCFHeaderExtender {

	public UK10KVCFHeaderExtender(DBAnnotationOptions options) {
		super(options);
	}

	@Override
	public String getDefaultPrefix() {
		return "UK10K_";
	}

	@Override
	public void addHeaders(VCFHeader header, String prefix) {
		addHeadersInfixes(header, prefix, "", "");
		if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching())
			addHeadersInfixes(header, prefix, "OVL_", " (requiring no genotype match, only position overlap)");
	}

	public void addHeadersInfixes(VCFHeader header, String prefix, String infix, String note) {
		VCFInfoHeaderLine anLine = new VCFInfoHeaderLine(prefix + infix + "AN", 1, VCFHeaderLineType.Integer,
				"Number of chromosomes with coverage in UK10K genomes" + note);
		header.addMetaDataLine(anLine);

		VCFInfoHeaderLine acLine = new VCFInfoHeaderLine(prefix + infix + "AC", VCFHeaderLineCount.A,
				VCFHeaderLineType.Integer, "Number of chromosomes showing the given allele in UK10K genomes" + note);
		header.addMetaDataLine(acLine);

		VCFInfoHeaderLine afLine = new VCFInfoHeaderLine(prefix + infix + "AF", VCFHeaderLineCount.A,
				VCFHeaderLineType.Integer, "Allele frequency in UK10K genomes" + note);
		header.addMetaDataLine(afLine);
	}

}
