package de.charite.compbio.jannovar.vardbs.exac;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/**
 * Helper class for extending {@link VCFHeader}s for ExAC annotations.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ExacVCFHeaderExtender extends VCFHeaderExtender {

	public ExacVCFHeaderExtender(DBAnnotationOptions options) {
		super(options);
	}

	@Override
	public String getDefaultPrefix() {
		return "EXAC_";
	}

	@Override
	public void addHeaders(VCFHeader header, String prefix) {
		// Add headers for exactly matching variants
		for (ExacPopulation pop : ExacPopulation.values()) {
			addACHeader(header, prefix, "", "", pop);
			addANHeader(header, prefix, "", "", pop);
			addAFHeader(header, prefix, "", "", pop);
			addACHetHeader(header, prefix, "", "", pop);
			addACHomHeader(header, prefix, "", "", pop);
			addACHemiHeader(header, prefix, "", "", pop);
		}
		addBestAFHeader(header, prefix, "", "");
		addBestACHeader(header, prefix, "", "");

		if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching()) {
			// Add headers for overlapping variants
			final String note = " (requiring no genotype match, only position overlap)";
			for (ExacPopulation pop : ExacPopulation.values()) {
				addACHeader(header, prefix, "OVL_", note, pop);
				addANHeader(header, prefix, "OVL_", note, pop);
				addAFHeader(header, prefix, "OVL_", note, pop);
				addACHetHeader(header, prefix, "OVL_", note, pop);
				addACHomHeader(header, prefix, "OVL_", note, pop);
				addACHemiHeader(header, prefix, "OVL_", note, pop);
			}
			addBestAFHeader(header, prefix, "OVL_", note);
			addBestACHeader(header, prefix, "OVL_", note);
		}
	}

	/** Add header for highest allele frequency */
	private void addBestAFHeader(VCFHeader header, String prefix, String idInfix, String noteInfix) {
		VCFInfoHeaderLine line = new VCFInfoHeaderLine(prefix + idInfix + "BEST_AF", VCFHeaderLineCount.A,
				VCFHeaderLineType.Float, "Highest allele frequency seen in any population" + noteInfix);
		header.addMetaDataLine(line);
	}

	/** Add header for allele count with highest frequency */
	private void addBestACHeader(VCFHeader header, String prefix, String idInfix, String noteInfix) {
		VCFInfoHeaderLine line = new VCFInfoHeaderLine(prefix + idInfix + "BEST_AC", VCFHeaderLineCount.A,
				VCFHeaderLineType.Integer, "Allele count for population with highest frequency" + noteInfix);
		header.addMetaDataLine(line);
	}

	/** Add header with allele frequency */
	private void addAFHeader(VCFHeader header, String prefix, String idInfix, String noteInfix, ExacPopulation pop) {
		String popName;
		if (pop == ExacPopulation.ALL)
			popName = "all populations";
		else
			popName = pop + " / " + pop.getLabel() + " population";
		VCFInfoHeaderLine line = new VCFInfoHeaderLine(prefix + idInfix + "AF_" + pop, VCFHeaderLineCount.A,
				VCFHeaderLineType.Float, "Frequency observed in ExAC data set in " + popName + noteInfix);
		header.addMetaDataLine(line);
	}

	/** Add header with chromosome count */
	private void addANHeader(VCFHeader header, String prefix, String idInfix, String noteInfix, ExacPopulation pop) {
		// TODO: change counts to 1 for AN?
		String popName;
		if (pop == ExacPopulation.ALL)
			popName = "all populations";
		else
			popName = pop + " / " + pop.getLabel() + " population";
		VCFInfoHeaderLine line = new VCFInfoHeaderLine(prefix + idInfix + "AN_" + pop, 1, VCFHeaderLineType.Integer,
				"Overall number of positions/chromosomes with coverage in " + popName + noteInfix);
		header.addMetaDataLine(line);
	}

	/** Add header with allele counts */
	private void addACHeader(VCFHeader header, String prefix, String idInfix, String noteInfix, ExacPopulation pop) {
		String popName;
		if (pop == ExacPopulation.ALL)
			popName = "all populations";
		else
			popName = pop + " / " + pop.getLabel() + " population";
		VCFInfoHeaderLine line = new VCFInfoHeaderLine(prefix + idInfix + "AC_" + pop, VCFHeaderLineCount.A,
				VCFHeaderLineType.Integer, "Overall number of observed alleles in " + popName + noteInfix);
		header.addMetaDataLine(line);
	}
	
	/** Add header with het allele counts */
	private void addACHetHeader(VCFHeader header, String prefix, String idInfix, String noteInfix, ExacPopulation pop) {
		String popName;
		if (pop != ExacPopulation.ALL)
			popName = "all populations";
		else
			popName = pop + " / " + pop.getLabel() + " population";
		VCFInfoHeaderLine line = new VCFInfoHeaderLine(prefix + idInfix + "HET_" + pop, VCFHeaderLineCount.A,
				VCFHeaderLineType.Integer, "Overall number of observed heterozygous alleles in " + popName + noteInfix);
		header.addMetaDataLine(line);
	}
	
	/** Add header with hom allele counts */
	private void addACHomHeader(VCFHeader header, String prefix, String idInfix, String noteInfix, ExacPopulation pop) {
		String popName;
		if (pop == ExacPopulation.ALL)
			popName = "all populations";
		else
			popName = pop + " / " + pop.getLabel() + " population";
		VCFInfoHeaderLine line = new VCFInfoHeaderLine(prefix + idInfix + "HOM_" + pop, VCFHeaderLineCount.A,
				VCFHeaderLineType.Integer, "Overall number of observed homozygous alleles in " + popName + noteInfix);
		header.addMetaDataLine(line);
	}
	
	/** Add header with hemi allele counts */
	private void addACHemiHeader(VCFHeader header, String prefix, String idInfix, String noteInfix, ExacPopulation pop) {
		String popName;
		if (pop == ExacPopulation.ALL)
			popName = "all populations";
		else
			popName = pop + " / " + pop.getLabel() + " population";
		VCFInfoHeaderLine line = new VCFInfoHeaderLine(prefix + idInfix + "HEMI_" + pop, VCFHeaderLineCount.A,
				VCFHeaderLineType.Integer, "Overall number of observed hemizygous alleles in " + popName + noteInfix);
		header.addMetaDataLine(line);
	}

}
