package de.charite.compbio.jannovar.vardbs.dbsnp;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Annotation driver class for annotations using dbSNP
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
final public class DBSNPAnnotationDriver implements DBAnnotationDriver {

	/** Path to dbSNP VCF file */
	final private String vcfPath;
	/** Configuration */
	final private DBAnnotationOptions options;

	/**
	 * Create annotation driver for a coordinate-sorted, bgzip-compressed, dbSNP VCF file
	 * 
	 * @param vcfPath
	 *            Path to VCF file with dbSNP.
	 */
	public DBSNPAnnotationDriver(String vcfPath, DBAnnotationOptions options) {
		this.vcfPath = vcfPath;
		this.options = options;
	}

	@Override
	public VCFHeaderExtender constructVCFHeaderExtender() {
		return new DBSNPVCFHeaderExtender();
	}

	@Override
	public void annotateVariantContext(VariantContext vc) {
	}

}
