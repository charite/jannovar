package de.charite.compbio.jannovar.vardbs.uk10k;

import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/**
 * Helper class for extending {@link VCFHeader}s for UK10K annotations.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class UK10KVCFHeaderExtender extends VCFHeaderExtender {

	@Override
	public String getDefaultPrefix() {
		return "UK10K_";
	}

	@Override
	public void addHeaders(VCFHeader header, String prefix) {
		// TODO: change counts to 1 for AN?
		VCFInfoHeaderLine anLine = new VCFInfoHeaderLine(prefix + "AN", 1, VCFHeaderLineType.Integer,
				"Number of chromosomes with coverage in UK10K genomes");
		header.addMetaDataLine(anLine);

		VCFInfoHeaderLine acLine = new VCFInfoHeaderLine(prefix + "AC", VCFHeaderLineCount.A, VCFHeaderLineType.Integer,
				"Number of chromosomes showing the given allele in UK10K genomes");
		header.addMetaDataLine(acLine);

		VCFInfoHeaderLine afLine = new VCFInfoHeaderLine(prefix + "AF", VCFHeaderLineCount.A, VCFHeaderLineType.Integer,
				"Allele frequency in UK10K genomes");
		header.addMetaDataLine(afLine);
	}

}
