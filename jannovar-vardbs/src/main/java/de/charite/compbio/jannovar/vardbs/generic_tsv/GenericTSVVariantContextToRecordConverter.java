package de.charite.compbio.jannovar.vardbs.generic_tsv;

import de.charite.compbio.jannovar.vardbs.base.VariantContextToRecordConverter;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper class for converting {@link VariantContext} to {@link GenericTSVRecord}.
 * 
 * <p>
 * It is a bit complex to first generate a {@link VariantContext} from TSV and then use this for
 * converting back into {@link GenericTSVRecord}. However, this is the easiest way to use the
 * existing machinery for annotation and allele matching.
 * </p>
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final class GenericTSVVariantContextToRecordConverter
		implements VariantContextToRecordConverter<GenericTSVRecord> {

	private GenericTSVAnnotationOptions tsvOptions;

	public GenericTSVVariantContextToRecordConverter(GenericTSVAnnotationOptions options) {
		this.tsvOptions = options;
	}

	@Override
	public GenericTSVRecord convert(VariantContext vc) {
		GenericTSVRecordBuilder builder = new GenericTSVRecordBuilder();

		if (vc.getAlternateAlleles().size() > 1) {
			throw new RuntimeException(
					"Must have exactly zero or one ALT allele but this == " + this.toString());
		}

		builder.setContig(vc.getContig());
		builder.setPos(vc.getStart() - 1);
		if (vc.getAlternateAlleles().size() == 0) {
			builder.setRef("N");
			builder.setAlt("N");
		} else {
			builder.setRef(vc.getReference().toString());
			builder.setAlt(vc.getAlternateAllele(0).toString());
		}
		for (String colName : tsvOptions.getColumnNames()) {
			final GenericTSVValueColumnDescription desc = tsvOptions.getValueColumnDescriptions()
					.get(colName);
			builder.getValues().add(vc.getAttribute(desc.getFieldName()));
		}

		return builder.build();
	}

}
