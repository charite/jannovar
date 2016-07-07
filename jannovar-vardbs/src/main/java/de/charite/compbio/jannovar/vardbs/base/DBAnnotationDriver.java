package de.charite.compbio.jannovar.vardbs.base;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * Interface for annotation drivers by variant databases.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public interface DBAnnotationDriver {

	/**
	 * @return The {@link VCFHeaderExtender} to use.
	 */
	VCFHeaderExtender constructVCFHeaderExtender();

	/**
	 * Annotate the {@link VariantContext} object using the information in the database.
	 *
	 * Note that the annotation in jannovar-vardbs makes a distinction between variants that are in the database and
	 * variants that are only at the same position or overlap.
	 *
	 * @param vc
	 *            {@link VariantContext} to annotate
	 */
	public void annotateVariantContext(VariantContext vc);

}
