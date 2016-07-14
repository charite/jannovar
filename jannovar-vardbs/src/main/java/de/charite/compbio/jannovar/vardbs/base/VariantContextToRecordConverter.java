package de.charite.compbio.jannovar.vardbs.base;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * Conversion of {@link VariantContext} objects to record objects.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public interface VariantContextToRecordConverter<RecordType> {

	/**
	 * Convert {@link VariantContext} into record type <code>T</code>
	 * 
	 * @param vc
	 *            {@link VariantContext} to convert
	 * @return Resulting record object of type <code>T</code>
	 */
	public RecordType convert(VariantContext vc);

}
