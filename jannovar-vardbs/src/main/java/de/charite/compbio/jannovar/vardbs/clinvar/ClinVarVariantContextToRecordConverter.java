package de.charite.compbio.jannovar.vardbs.clinvar;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import de.charite.compbio.jannovar.vardbs.base.VariantContextToRecordConverter;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper class for the conversion of {@link VariantContext} to {@link ClinVarRecord} objects
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final class ClinVarVariantContextToRecordConverter implements VariantContextToRecordConverter<ClinVarRecord> {

	@SuppressWarnings("unchecked")
	@Override
	public ClinVarRecord convert(VariantContext vc) {
		ClinVarRecordBuilder builder = new ClinVarRecordBuilder();

		// Column-level properties from VCF file
		builder.setContig(vc.getContig());
		builder.setPos(vc.getStart() - 1);
		builder.setID(vc.getID());
		builder.setRef(vc.getReference().getBaseString());
		for (Allele all : vc.getAlternateAlleles())
			builder.getAlt().add(all.getBaseString());
		builder.getFilter().addAll(vc.getFilters());

		TreeMap<Integer, ArrayList<ClinVarAnnotationBuilder>> annoBuilders = new TreeMap<>();

		// Fields from INFO VCF field

		// CLNALLE
		ArrayList<Integer> alleles = new ArrayList<>();
		for (Object s : vc.getAttributeAsList("CLNALLE"))
			alleles.add(Integer.parseInt((String) s));
		for (int idx = 0; idx < alleles.size(); ++idx)
			annoBuilders.put(idx, new ArrayList<>());

		// CLNHGVS
		List<Object> hgvs = vc.getAttributeAsList("CLNHGVS");
		for (int idx = 0; idx < alleles.size(); ++idx) {
			final int alleleID = alleles.get(idx);
			if (alleleID == -1)
				continue; // ignore
			ArrayList<String> lst = Lists.newArrayList(((String) hgvs.get(idx)).split("\\|"));
			for (int i = 0; i < lst.size(); ++i) {
				annoBuilders.get(idx).add(new ClinVarAnnotationBuilder());
				annoBuilders.get(idx).get(i).setAlleleMapping(alleleID);
				annoBuilders.get(idx).get(i).setHgvsVariant(lst.get(i));
			}
		}
		
		// Build annotations into builder
		for (int idx : annoBuilders.keySet().stream().sorted().toArray(Integer[]::new)) {
			for (ClinVarAnnotationBuilder annoBuilder : annoBuilders.get(idx)) {
				builder.getAnnotations().put(idx, annoBuilder.build());
			}
		}

		return builder.build();
	}

}
