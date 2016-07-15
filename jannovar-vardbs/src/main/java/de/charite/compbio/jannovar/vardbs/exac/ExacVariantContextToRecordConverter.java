package de.charite.compbio.jannovar.vardbs.exac;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.charite.compbio.jannovar.vardbs.base.VariantContextToRecordConverter;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper class for the conversion of {@link VariantContext} to {@link ExacRecord} objects
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final class ExacVariantContextToRecordConverter implements VariantContextToRecordConverter<ExacRecord> {

	@Override
	public ExacRecord convert(VariantContext vc) {
		ExacRecordBuilder builder = new ExacRecordBuilder();

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
		int allAN = 0;
		for (ExacPopulation pop : ExacPopulation.values()) {
			if (pop == ExacPopulation.ALL)
				continue; // skip

			int an = vc.getAttributeAsInt("AN_" + pop, 0);
			builder.getChromCounts().put(pop, an);
			for (int i = 0; i < vc.getAlternateAlleles().size(); ++i)
				allAN += an;
		}
		builder.getChromCounts().put(ExacPopulation.ALL, allAN);

		// AC: Alternative allele count
		ArrayList<Integer> allAC = new ArrayList<>();
		for (int i = 0; i < vc.getAlternateAlleles().size(); ++i)
			allAC.add(0);
		for (ExacPopulation pop : ExacPopulation.values()) {
			if (pop == ExacPopulation.ALL)
				continue; // skip

			List<Integer> lst = vc.getAttributeAsList("AC_" + pop).stream().map(x -> Integer.parseInt((String) x))
					.collect(Collectors.toList());
			builder.getAlleleCounts().put(pop, lst);
			for (int i = 0; i < vc.getAlternateAlleles().size(); ++i)
				allAC.set(i, allAC.get(i) + lst.get(i));
		}
		builder.getAlleleCounts().put(ExacPopulation.ALL, allAC);

		return builder.build();
	}

}
