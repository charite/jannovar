package de.charite.compbio.jannovar.vardbs.facade;

import java.util.Collection;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;

/**
 * Facade class for easy annotation of {@link VariantContext} objects using databases
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final public class DBVariantContextAnnotator {

	/** The DB annotation driver to use */
	final private DBAnnotationDriver driver;
	/** The options to use */
	final private DBAnnotationOptions options;

	/**
	 * Initialize the annotator using the given {@link DBAnnotationDriver}
	 * 
	 * @param driver
	 */
	public DBVariantContextAnnotator(DBAnnotationDriver driver, DBAnnotationOptions options) {
		this.driver = driver;
		this.options = options;
	}

	/**
	 * Extend VCF header with the {@link VCFHeaderExtender} for the annotator
	 *
	 * @param vcfHeader
	 *            Extend VCF header
	 * @return The modified <code>vcfHeader</code>
	 */
	public VCFHeader extendHeader(VCFHeader vcfHeader) {
		driver.constructVCFHeaderExtender().addHeaders(vcfHeader, options.getVCFIdentifierPrefix());
		return vcfHeader;
	}

	/**
	 * Annotate one {@link VariantContext} with information from a database
	 *
	 * @param vc
	 *            {@link VariantContext} to annotate
	 * @return the updated {@link VariantContext} <code>vc</code>
	 */
	public VariantContext annotateVariantContext(VariantContext vc) {
		return driver.annotateVariantContext(vc);
	}

	/**
	 * Convenience method for bulk-annotating multiple {@link VariantContext} objects
	 *
	 * @param vcs
	 *            {@link Collection} of {@link VariantContext} objects to annotate
	 */
	public void annotateVariantContexts(Collection<VariantContext> vcs) {
		for (VariantContext vc : vcs)
			annotateVariantContext(vc);
	}

}
