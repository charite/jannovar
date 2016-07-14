package de.charite.compbio.jannovar.vardbs.exac;

import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/**
 * Helper class for extending {@link VCFHeader}s for ExAC annotations.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class ExacVCFHeaderExtender extends VCFHeaderExtender {

	@Override
	public String getDefaultPrefix() {
		return "EXAC_";
	}

	@Override
	public void addHeaders(VCFHeader header, String prefix) {
		for (ExacPopulation pop : ExacPopulation.values()) {
			addACHeader(header, prefix, pop);
			addANHeader(header, prefix, pop);
			addAFHeader(header, prefix, pop);
		}
		addBestAFHeader(header, prefix);
		addBestACHeader(header, prefix);
	}

	/** Add header for highest allele frequency */
	private void addBestAFHeader(VCFHeader header, String prefix) {
		VCFInfoHeaderLine line = new VCFInfoHeaderLine(prefix + "BEST_AF", VCFHeaderLineCount.A,
				VCFHeaderLineType.Float, "Highest allele frequency seen in any population");
		header.addMetaDataLine(line);
	}

	/** Add header for allele count with highest frequency */
	private void addBestACHeader(VCFHeader header, String prefix) {
		VCFInfoHeaderLine line = new VCFInfoHeaderLine(prefix + "BEST_AC", VCFHeaderLineCount.A,
				VCFHeaderLineType.Integer, "Allele count for population with highest frequency");
		header.addMetaDataLine(line);
	}

	/** Add header with allele frequency */
	private void addAFHeader(VCFHeader header, String prefix, ExacPopulation pop) {
		String popName;
		if (pop != ExacPopulation.ALL)
			popName = "all populations";
		else
			popName = pop + " / " + pop.getLabel() + " population";
		VCFInfoHeaderLine line = new VCFInfoHeaderLine(prefix + "AF_" + pop, VCFHeaderLineCount.R,
				VCFHeaderLineType.Float, "Frequency observed in ExAC data set in " + popName);
		header.addMetaDataLine(line);
	}

	/** Add header with chromosome count */
	private void addANHeader(VCFHeader header, String prefix, ExacPopulation pop) {
		String popName;
		if (pop != ExacPopulation.ALL)
			popName = "all populations";
		else
			popName = pop + " / " + pop.getLabel() + " population";
		VCFInfoHeaderLine line = new VCFInfoHeaderLine(prefix + "AN_" + pop, VCFHeaderLineCount.R,
				VCFHeaderLineType.Integer, "Overall number of positions/chromosomes with coverage in " + popName);
		header.addMetaDataLine(line);
	}

	/** Add header with allele counts */
	private void addACHeader(VCFHeader header, String prefix, ExacPopulation pop) {
		String popName;
		if (pop != ExacPopulation.ALL)
			popName = "all populations";
		else
			popName = pop + " / " + pop.getLabel() + " population";
		VCFInfoHeaderLine line = new VCFInfoHeaderLine(prefix + "AC_" + pop, VCFHeaderLineCount.R,
				VCFHeaderLineType.Integer, "Overall number of observed alleles in " + popName);
		header.addMetaDataLine(line);
	}

}
