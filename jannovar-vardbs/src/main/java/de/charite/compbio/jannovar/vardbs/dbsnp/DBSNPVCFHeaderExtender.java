package de.charite.compbio.jannovar.vardbs.dbsnp;

import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/**
 * Helper class for extending {@link VCFHeader}s for DBSNP annotations.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class DBSNPVCFHeaderExtender extends VCFHeaderExtender {

	@Override
	public String getDefaultPrefix() {
		return "DBSNP_";
	}

	@Override
	public void addHeaders(VCFHeader header, String prefix) {
		VCFInfoHeaderLine infoDBSNPCommon = new VCFInfoHeaderLine(prefix + "COMMON", VCFHeaderLineCount.A,
				VCFHeaderLineType.Integer,
				"Flagged as common in dbSNP. Original description: RS is a common SNP.  A common SNP is "
						+ "one that has at least one 1000Genomes population with a minor allele "
						+ "of frequency >= 1% and for which 2 or more founders contribute to that minor allele "
						+ "frequency.");
		header.addMetaDataLine(infoDBSNPCommon);

		VCFInfoHeaderLine infoDBSNPFreqs = new VCFInfoHeaderLine(prefix + "CAF", VCFHeaderLineCount.R,
				VCFHeaderLineType.Float,
				"Allele frequencies from dbSNP. Original description: An ordered, comma delimited list "
						+ "of allele frequencies based on 1000Genomes, starting with the reference allele "
						+ "followed by alternate alleles as ordered in the ALT column. Where a 1000Genomes "
						+ "alternate allele is not in the dbSNPs alternate allele set, the allele is added "
						+ "to the ALT column.  The minor allele is the second largest value in the list, "
						+ "and was previuosly reported in VCF as the GMAF.  This is the GMAF reported on "
						+ "the RefSNP and EntrezSNP pages and VariationReporter");
		header.addMetaDataLine(infoDBSNPFreqs);

		VCFInfoHeaderLine infoG5 = new VCFInfoHeaderLine(prefix + "G5", VCFHeaderLineCount.A,
				VCFHeaderLineType.Integer,
				"Allele frequency >5% in all populations from dbSNP (yes: 1, no: 0). Original description: >5% "
						+ "minor allele frequency in 1+ populations");
		header.addMetaDataLine(infoG5);

		VCFInfoHeaderLine infoG5A = new VCFInfoHeaderLine(prefix + "G5A", VCFHeaderLineCount.A,
				VCFHeaderLineType.Integer,
				"Allele frequency >5% in all populations from dbSNP (yes: 1, no: 0). Original description: >5% "
						+ "minor allele frequency in each and all populations");
		header.addMetaDataLine(infoG5A);

		VCFInfoHeaderLine matchingRS = new VCFInfoHeaderLine(prefix + "MATCH", VCFHeaderLineCount.UNBOUNDED,
				VCFHeaderLineType.String, "dbSNP cluster identifiers with matching alternative positions and alleles");
		header.addMetaDataLine(matchingRS);

		// XXX TODO XXX
		VCFInfoHeaderLine overlappingRS = new VCFInfoHeaderLine(prefix + "OVERLAP", VCFHeaderLineCount.UNBOUNDED,
				VCFHeaderLineType.String,
				"dbSNP cluster identifiers with overlapping alternative positions, not necessarily matching alleles");
		// header.addMetaDataLine(overlappingRS);
	}

}
