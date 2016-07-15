package de.charite.compbio.jannovar.vardbs.uk10k;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import de.charite.compbio.jannovar.vardbs.base.VariantContextToRecordConverter;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper class for the conversion of {@link VariantContext} to {@link UK10KRecord} objects
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final class UK10KVariantContextToRecordConverter implements VariantContextToRecordConverter<UK10KRecord> {

	@Override
	public UK10KRecord convert(VariantContext vc) {
		UK10KRecordBuilder builder = new UK10KRecordBuilder();

		// Column-level properties from VCF file
		builder.setContig(vc.getContig());
		builder.setPos(vc.getStart() - 1);
		builder.setID(vc.getID());
		builder.setRef(vc.getReference().getBaseString());
		for (Allele all : vc.getAlternateAlleles())
			builder.getAlt().add(all.getBaseString());
		builder.getFilter().addAll(vc.getFilters());

		// Fields from INFO VCF field

		// AN: Chromosome count
		int an = vc.getAttributeAsInt("AN", 0);
		builder.setChromCount(an);

		// AC: Alternative allele count
		ArrayList<Integer> counts = Lists.newArrayList(vc.getAttributeAsList("AC").stream()
				.map(x -> Integer.parseInt((String) x)).collect(Collectors.toList()));
		builder.setAlleleCounts(counts);

		// AC: Alternative allele count
		builder.setAlleleFrequencies(
				Lists.newArrayList(counts.stream().map(x -> (1.0 * x) / an).collect(Collectors.toList())));

		return builder.build();
	}

}
