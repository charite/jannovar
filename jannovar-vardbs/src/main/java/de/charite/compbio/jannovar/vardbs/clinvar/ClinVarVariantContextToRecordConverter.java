package de.charite.compbio.jannovar.vardbs.clinvar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import com.google.common.collect.ImmutableList;
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

	private static <T> T getFromList(List<T> lst, int idx, T defaultValue) {
		if (idx >= lst.size())
			return defaultValue;
		else
			return lst.get(idx);
	}

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

		// CLNALLE -- clinically relevant allele ids
		//
		// The number of entries must be consistent with everything downstream
		ArrayList<Integer> alleles = new ArrayList<>();
		for (Object s : vc.getAttributeAsList("CLNALLE"))
			alleles.add(Integer.parseInt((String) s));
		for (int idx = 0; idx < alleles.size(); ++idx)
			annoBuilders.put(idx, new ArrayList<>());

		// Create shortcuts to allele-wise lists of the INFO atributes we will try to interpret
		List<Object> hgvs = vc.getAttributeAsList("CLNHGVS");
		List<Object> clnOrigin = vc.getAttributeAsList("CLNORIGIN");

		List<Object> clnSrc = vc.getAttributeAsList("CLNSRC");
		List<Object> clnSrcId = vc.getAttributeAsList("CLNSRCID");

		List<Object> clnSig = vc.getAttributeAsList("CLNSIG");
		List<Object> clnDiseaseDb = vc.getAttributeAsList("CLNDSDB");
		List<Object> clnDiseaseDbId = vc.getAttributeAsList("CLNDSDBID");
		List<Object> clnDiseaseDbName = vc.getAttributeAsList("CLNDBN");
		List<Object> clnRevStat = vc.getAttributeAsList("CLNREVSTAT");
		List<Object> clnAccession = vc.getAttributeAsList("CLNACC");

		for (int idx = 0; idx < alleles.size(); ++idx) {
			final int alleleID = alleles.get(idx);
			if (alleleID == -1)
				continue; // ignore

			// One element: CLNHGVS, CLNORIGIN
			ArrayList<String> hgvsList = Lists.newArrayList(((String) hgvs.get(idx)).split("\\|"));
			ArrayList<String> clnOriginList = Lists.newArrayList(((String) clnOrigin.get(idx)).split("\\|"));

			// Variant source information: CLNSRC, CLNSRCID
			ArrayList<String> clnSrcList = Lists.newArrayList(((String) clnSrc.get(idx)).split("\\|"));
			ArrayList<String> clnSrcIdList = Lists.newArrayList(((String) clnSrcId.get(idx)).split("\\|"));

			// Variant disease information: CLNSIG, CLNDSDB, CLINDSDBID, CLDSDBN, CLNREVSTAT, CLNACC
			ArrayList<String> clnSigList = Lists.newArrayList(((String) clnSig.get(idx)).split("\\|"));
			ArrayList<String> clnDiseaseDbList = Lists.newArrayList(((String) clnDiseaseDb.get(idx)).split("\\|"));
			ArrayList<String> clnDiseaseDbIdList = Lists.newArrayList(((String) clnDiseaseDbId.get(idx)).split("\\|"));
			ArrayList<String> clnDiseaseDbNameList = Lists
					.newArrayList(((String) clnDiseaseDbName.get(idx)).split("\\|"));
			ArrayList<String> clnRevStatList = Lists.newArrayList(((String) clnRevStat.get(idx)).split("\\|"));
			ArrayList<String> clnAccessionList = Lists.newArrayList(((String) clnAccession.get(idx)).split("\\|"));

			// Construct annotation builder
			ClinVarAnnotationBuilder annoBuilder = new ClinVarAnnotationBuilder();
			annoBuilders.get(idx).add(annoBuilder);

			// Set one-element lists into annoBuilder
			annoBuilder.setAlleleMapping(alleleID);
			if (hgvsList.size() != 1)
				throw new RuntimeException("Invalid HGVS size, must be 1");
			annoBuilder.setHgvsVariant(hgvsList.get(0));
			if (clnOriginList.size() != 1)
				throw new RuntimeException("Invalid CLNORIGIN size, must be 1");
			annoBuilder.setOrigin(ClinVarOrigin.fromInteger(Integer.parseInt(clnOriginList.get(0))));

			// Construct variant source information
			List<ClinVarSourceInfo> sourceInfos = new ArrayList<>();
			if (clnSrcList.size() != clnSrcIdList.size())
				throw new RuntimeException("length of CLNSRC differ CLNSRCID");
			for (int i = 0; i < clnSrcList.size(); ++i)
				sourceInfos.add(new ClinVarSourceInfo(clnSrcList.get(i), clnSrcIdList.get(i)));
			annoBuilder.setSourceInfos(sourceInfos);

			// Construct variant disease information
			List<ClinVarDiseaseInfo> diseaseInfos = new ArrayList<>();
			int numDiseaseAlleles = Collections
					.max(ImmutableList.of(clnSigList.size(), clnDiseaseDbList.size(), clnDiseaseDbIdList.size(),
							clnDiseaseDbNameList.size(), clnRevStatList.size(), clnAccessionList.size()));
			for (int i = 0; i < numDiseaseAlleles; ++i)
				diseaseInfos.add(new ClinVarDiseaseInfo(
						ClinVarSignificance.fromInteger(Integer.parseInt(getFromList(clnSigList, i, "255"))),
						getFromList(clnDiseaseDbList, i, ""), getFromList(clnDiseaseDbIdList, i, ""),
						getFromList(clnDiseaseDbNameList, i, ""),
						ClinVarRevisionStatus.fromString(getFromList(clnRevStatList, i, "no_assertion")),
						getFromList(clnAccessionList, i, "")));
			annoBuilder.setDiseaseInfos(diseaseInfos);
		}

		// Set annotations into builder
		for (int idx : annoBuilders.keySet().stream().sorted().toArray(Integer[]::new)) {
			for (ClinVarAnnotationBuilder annoBuilder : annoBuilders.get(idx)) {
				builder.getAnnotations().put(idx, annoBuilder.build());
			}
		}

		return builder.build();
	}

}
