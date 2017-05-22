package de.charite.compbio.jannovar.vardbs.generic_vcf;

import de.charite.compbio.jannovar.vardbs.base.VariantContextToRecordConverter;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper class for the conversion of {@link VariantContext} to {@link GenericVCFRecord} objects
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final class GenericVCFVariantContextToRecordConverter implements VariantContextToRecordConverter<VariantContext> {

	@Override
	public VariantContext convert(VariantContext vc) {
		return vc;
	}

}
