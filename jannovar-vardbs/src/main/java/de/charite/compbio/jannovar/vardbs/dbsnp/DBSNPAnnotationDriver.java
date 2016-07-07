package de.charite.compbio.jannovar.vardbs.dbsnp;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;

/**
 * Annotation driver class for annotations using dbSNP
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
final public class DBSNPAnnotationDriver implements DBAnnotationDriver {

	/** Path to dbSNP VCF file */
	final private String vcfPath;
	
	/**
	 * Create annotation driver for a coordinate-sorted, bgzip-compressed, dbSNP VCF file
	 * 
	 * @param vcfPath
	 *            Path to VCF file with dbSNP.
	 */
	public DBSNPAnnotationDriver(String vcfPath) {
		this.vcfPath = vcfPath;
	}

	@Override
	public VCFHeaderExtender constructVCFHeaderExtender() {
		return new DBSNPVCFHeaderExtender();
	}

}
