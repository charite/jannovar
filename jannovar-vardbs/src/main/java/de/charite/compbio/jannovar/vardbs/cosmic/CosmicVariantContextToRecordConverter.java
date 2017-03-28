package de.charite.compbio.jannovar.vardbs.cosmic;

import de.charite.compbio.jannovar.vardbs.base.VariantContextToRecordConverter;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper class for the conversion of {@link VariantContext} to {@link CosmicRecord} objects
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final class CosmicVariantContextToRecordConverter implements VariantContextToRecordConverter<CosmicRecord> {

	@Override
	public CosmicRecord convert(VariantContext vc) {
		CosmicRecordBuilder builder = new CosmicRecordBuilder();

		// Column-level properties from VCF file
		builder.setContig(vc.getContig());
		builder.setPos(vc.getStart() - 1);
		builder.setID(vc.getID());
		builder.setRef(vc.getReference().getBaseString());
		for (Allele all : vc.getAlternateAlleles())
			builder.getAlt().add(all.getBaseString());

		// Fields from INFO VCF field

		builder.setSnp(vc.getAttributeAsBoolean("SNP", false));
		builder.setCnt(vc.getAttributeAsInt("CNT", 0));

		return builder.build();
	}

}
