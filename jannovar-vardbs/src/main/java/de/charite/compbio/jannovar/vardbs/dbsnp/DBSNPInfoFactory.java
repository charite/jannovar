package de.charite.compbio.jannovar.vardbs.dbsnp;

import htsjdk.variant.vcf.VCFHeader;

/**
 * Build {@link DBSNPInfo} from a {@link VCFHeader}
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class DBSNPInfoFactory {
	
	public DBSNPInfo build(VCFHeader vcfHeader) {
		String fileDate = vcfHeader.getMetaDataLine("fileDate").getValue();
		String source = vcfHeader.getMetaDataLine("source").getValue();
		int dbSNPBuildID = Integer.parseInt(vcfHeader.getMetaDataLine("dbSNP_BUILD_ID").getValue());
		String reference = vcfHeader.getMetaDataLine("reference").getValue();
		String phasing = vcfHeader.getMetaDataLine("phasing").getValue();
		return new DBSNPInfo(fileDate, source, dbSNPBuildID, reference, phasing);
	}

}
