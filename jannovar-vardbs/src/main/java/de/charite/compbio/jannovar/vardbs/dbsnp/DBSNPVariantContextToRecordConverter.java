package de.charite.compbio.jannovar.vardbs.dbsnp;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import de.charite.compbio.jannovar.vardbs.base.VariantContextToRecordConverter;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Conversion of {@link VariantContext} to {@link DBSNPRecord} objects
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final class DBSNPVariantContextToRecordConverter implements VariantContextToRecordConverter<DBSNPRecord> {

	/**
	 * Convert {@link VariantContext} to {@link DBSNPRecord}
	 * 
	 * @param vc
	 *            {@link VariantContext} to convert
	 * @return Resulting {@link DBSNPRecord}
	 */
	public DBSNPRecord convert(VariantContext vc) {
		DBSNPRecordBuilder builder = new DBSNPRecordBuilder();

		// Column-level properties from VCF file
		builder.setContig(vc.getContig());
		builder.setPos(vc.getStart() - 1);
		builder.setID(vc.getID());
		builder.setRef(vc.getReference().getBaseString());
		for (Allele all : vc.getAlternateAlleles())
			builder.getAlt().add(all.getBaseString());
		builder.getFilter().addAll(vc.getFilters());

		// Fields from INFO VCF field
		builder.setRSID(vc.getAttributeAsInt("RS", -1));
		builder.setRSPos(vc.getAttributeAsInt("RSPOS", -1));
		builder.setReversed(vc.hasAttribute("RV"));
		builder.setVariantProperty(null); // TODO
		for (String strGeneInfo : Splitter.on("|").split(vc.getAttributeAsString("GENEINFO", ""))) {
			strGeneInfo = strGeneInfo.trim();
			if (strGeneInfo.isEmpty())
				continue;
			ArrayList<String> arr = Lists.newArrayList(Splitter.on(":").split(strGeneInfo));
			assert arr.size() == 2;
			builder.getGeneInfos().add(new DBSNPGeneInfo(arr.get(0), Integer.parseInt(arr.get(1))));
		}
		builder.setDbSNPBuildID(vc.getAttributeAsInt("dbSNPBuildID", -1));

		// TODO: can be cleaned up by having methods in Enum
		switch (vc.getAttributeAsInt("SAO", 0)) {
		case 0:
			builder.setVariantAlleleOrigin(DBSNPVariantAlleleOrigin.UNSPECIFIED);
			break;
		case 1:
			builder.setVariantAlleleOrigin(DBSNPVariantAlleleOrigin.GERMLINE);
			break;
		case 2:
			builder.setVariantAlleleOrigin(DBSNPVariantAlleleOrigin.SOMATIC);
			break;
		case 3:
			builder.setVariantAlleleOrigin(DBSNPVariantAlleleOrigin.BOTH);
			break;
		}

		// TODO: can be cleaned up by having methods in Enum
		int suspectCode = vc.getAttributeAsInt("SSR", 0);
		if (suspectCode == 0) {
			builder.getVariantSuspectReasonCode().add(DBSNPVariantSuspectReasonCode.UNSPECIFIED);
		} else {
			if ((suspectCode & 1) == 1)
				builder.getVariantSuspectReasonCode().add(DBSNPVariantSuspectReasonCode.PARALOG);
			if ((suspectCode & 2) == 2)
				builder.getVariantSuspectReasonCode().add(DBSNPVariantSuspectReasonCode.BY_EST);
			if ((suspectCode & 4) == 4)
				builder.getVariantSuspectReasonCode().add(DBSNPVariantSuspectReasonCode.OLD_ALIGN);
			if ((suspectCode & 8) == 8)
				builder.getVariantSuspectReasonCode().add(DBSNPVariantSuspectReasonCode.PARA_EST);
			if ((suspectCode & 16) == 16)
				builder.getVariantSuspectReasonCode().add(DBSNPVariantSuspectReasonCode.G1K_FAILED);
			if ((suspectCode & 1024) == 1024)
				builder.getVariantSuspectReasonCode().add(DBSNPVariantSuspectReasonCode.OTHER);
		}

		builder.setWeights(vc.getAttributeAsInt("WGT", 0));
		builder.setVariationClass(vc.getAttributeAsString("VC", null));

		builder.setPrecious(vc.hasAttribute("PM"));
		builder.setThirdPartyAnnotation(vc.hasAttribute("TPA"));
		builder.setPubMedCentral(vc.hasAttribute("PMC"));
		builder.setThreeDStructure(vc.hasAttribute("S3D"));
		builder.setSubmitterLinkOut(vc.hasAttribute("SLO"));
		builder.setNonSynonymousFrameShift(vc.hasAttribute("NSF"));
		builder.setNonSynonymousMissense(vc.hasAttribute("NSM"));
		builder.setNonSynonymousNonsense(vc.hasAttribute("NSN"));
		builder.setReference(vc.hasAttribute("REF"));
		builder.setInThreePrimeUTR(vc.hasAttribute("U3"));
		builder.setInFivePrimeUTR(vc.hasAttribute("U5"));
		builder.setInAcceptor(vc.hasAttribute("ASS"));
		builder.setInDonor(vc.hasAttribute("DSS"));
		builder.setInIntron(vc.hasAttribute("INT"));
		builder.setInThreePrime(vc.hasAttribute("R3"));
		builder.setInFivePrime(vc.hasAttribute("R5"));
		builder.setOtherVariant(vc.hasAttribute("OTH"));
		builder.setAssemblySpecific(vc.hasAttribute("ASP"));
		builder.setAssemblyConflict(vc.hasAttribute("CFL"));
		builder.setMutation(vc.hasAttribute("MUT"));
		builder.setValidated(vc.hasAttribute("VLD"));
		builder.setFivePercentAll(vc.hasAttribute("G5A"));
		builder.setFivePercentOne(vc.hasAttribute("G5"));
		builder.setGenotypesAvailable(vc.hasAttribute("GNO"));
		builder.setG1kPhase1(vc.hasAttribute("KGPhase1"));
		builder.setG1kPhase3(vc.hasAttribute("GKPhase3"));
		builder.setClinicalDiagnosticAssay(vc.hasAttribute("CDA"));
		builder.setLocusSpecificDatabase(vc.hasAttribute("LSD"));
		builder.setMicroattributionThirdParty(vc.hasAttribute("MTP"));
		builder.setHasOMIMOrOMIA(vc.hasAttribute("OM"));
		builder.setContigAlelleNotVariant(vc.hasAttribute("NOC"));
		builder.setWithdrawn(vc.hasAttribute("WTD"));
		builder.setNonOverlappingAlleleSet(vc.hasAttribute("NOV"));
		builder.getAlleleFrequenciesG1K().addAll(vc.getAttributeAsList("CAF").stream().map(x -> {
			if (".".equals(x))
				return 0.0;
			else
				return (Double) Double.parseDouble((String) x);
		}).collect(Collectors.toList()));
		if (!builder.getAlleleFrequenciesG1K().isEmpty())
			builder.getAlleleFrequenciesG1K().subList(0, 1).clear();
		builder.setCommon(vc.hasAttribute("COMMON"));
		builder.getOldVariants().addAll(
				vc.getAttributeAsList("OLD_VARIANT").stream().map(x -> (String) x).collect(Collectors.toList()));

		return builder.build();
	}

}
