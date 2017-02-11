package de.charite.compbio.jannovar.vardbs.clinvar;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;

import de.charite.compbio.jannovar.vardbs.base.AlleleMatcher;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.GenotypeMatch;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import de.charite.compbio.jannovar.vardbs.base.VariantContextToRecordConverter;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFFileReader;

// TODO: handle MNVs appropriately

/**
 * Annotation driver class for annotations using ClinVar
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ClinVarAnnotationDriver implements DBAnnotationDriver {

	/** Path to dbSNP VCF file */
	protected final String vcfPath;
	/** Helper objects for matching alleles */
	protected final AlleleMatcher matcher;
	/** Helper for converting from VariantContex to DBSNP record */
	protected final VariantContextToRecordConverter<ClinVarRecord> vcToRecord;
	/** Configuration */
	protected final DBAnnotationOptions options;
	/** VCFReader to use for loading the VCF records */
	protected final VCFFileReader vcfReader;

	/**
	 * Create annotation driver for a coordinate-sorted, bgzip-compressed, VCF file
	 * 
	 * @param fastaPath
	 *            FAI-indexed FASTA file with reference
	 * @param vcfPath
	 *            Path to VCF file with dbSNP.
	 * @param options
	 *            configuration
	 * @throws JannovarVarDBException
	 *             on problems loading the reference FASTA/FAI file or incompatible dbSNP version
	 */
	public ClinVarAnnotationDriver(String vcfPath, String fastaPath, DBAnnotationOptions options)
			throws JannovarVarDBException {
		this.vcfPath = vcfPath;
		this.matcher = new AlleleMatcher(fastaPath);
		this.vcToRecord = new ClinVarVariantContextToRecordConverter();
		this.vcfReader = new VCFFileReader(new File(this.vcfPath), true);
		this.options = options;
	}

	@Override
	public VCFHeaderExtender constructVCFHeaderExtender() {
		return new ClinVarVCFHeaderExtender(options);
	}

	@Override
	public VariantContext annotateVariantContext(VariantContext obsVC) {
		try (CloseableIterator<VariantContext> iter = vcfReader.query(obsVC.getContig(), obsVC.getStart(),
				obsVC.getEnd())) {
			// Fetch all overlapping and matching genotypes from database and pair them with the correct allele from vc.
			List<GenotypeMatch> genotypeMatches = new ArrayList<>();
			List<GenotypeMatch> positionOverlaps = new ArrayList<>();
			while (iter.hasNext()) {
				final VariantContext dbVC = iter.next();
				genotypeMatches.addAll(matcher.matchGenotypes(obsVC, dbVC));
				// TODO: what to do about non-reference/non-alt ClinVar annotation "-1"?
				if (options.isReportOverlapping() || options.isReportOverlappingAsMatching())
					positionOverlaps.addAll(matcher.positionOverlaps(obsVC, dbVC));
			}

			List<GenotypeMatch> emptyList = new ArrayList<>();

			// Use these records to annotate the variant call in obsVC (record-wise but also per alternative allele)
			if (options.isReportOverlappingAsMatching())
				return annotateWithDBRecords(obsVC, positionOverlaps, emptyList);
			else if (options.isReportOverlapping())
				return annotateWithDBRecords(obsVC, genotypeMatches, positionOverlaps);
			else
				return annotateWithDBRecords(obsVC, genotypeMatches, emptyList);
		}
	}

	/**
	 * Perform annotation with DB records
	 * 
	 * @param obsVC
	 *            The observed {@link VariantContext}
	 * @param genotypeMatches
	 *            list of matches with genotypes
	 * @param positionOverlaps
	 *            list of matches with genotype overlaps only
	 * @return annotated {@link VariantContext}
	 */
	private VariantContext annotateWithDBRecords(VariantContext obsVC, List<GenotypeMatch> genotypeMatches,
			List<GenotypeMatch> positionOverlaps) {
		ClinVarVariantContextToRecordConverter converter = new ClinVarVariantContextToRecordConverter();
		VariantContextBuilder builder = new VariantContextBuilder(obsVC);

		ArrayListMultimap<Integer, ClinVarAnnotation> matchMap = ArrayListMultimap.create();
		for (GenotypeMatch m : genotypeMatches) {
			final ClinVarRecord clinVarRecord = converter.convert(m.getDBVC());
			matchMap.putAll(m.getObservedAllele(), clinVarRecord.getAnnotations().get(m.getDbAllele()));
		}
		annotateBuilder(builder, matchMap, "");

		if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching()) {
			ArrayListMultimap<Integer, ClinVarAnnotation> overlapMap = ArrayListMultimap.create();
			for (GenotypeMatch m : positionOverlaps) {
				final ClinVarRecord clinVarRecord = converter.convert(m.getDBVC());
				overlapMap.putAll(m.getObservedAllele(), clinVarRecord.getAnnotations().get(m.getDbAllele()));
			}
			annotateBuilder(builder, matchMap, "OVL_");
		}

		return builder.make();
	}

	private void annotateBuilder(VariantContextBuilder builder, ArrayListMultimap<Integer, ClinVarAnnotation> matchMap,
			String infix) {
		if (matchMap.isEmpty())
			return; // skip

		List<String> basicInfo = buildBasicInfo(builder, matchMap);
		if (!basicInfo.isEmpty())
			builder.attribute(options.getVCFIdentifierPrefix() + infix + "BASIC_INFO", basicInfo);
		List<String> varInfo = buildVarInfo(builder, matchMap);
		if (!varInfo.isEmpty())
			builder.attribute(options.getVCFIdentifierPrefix() + infix + "VAR_INFO", varInfo);
		List<String> diseaseInfo = buildDiseaseInfo(builder, matchMap);
		if (!diseaseInfo.isEmpty())
			builder.attribute(options.getVCFIdentifierPrefix() + infix + "DISEASE_INFO", diseaseInfo);
	}

	private static String encode(String s) {
		if (s == null)
			return "";
		try {
			return URLEncoder.encode(s, "utf-8").replaceAll("=", "%3D");
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

	private ArrayList<String> buildBasicInfo(VariantContextBuilder builder,
			ArrayListMultimap<Integer, ClinVarAnnotation> matchMap) {
		ArrayList<String> result = new ArrayList<>();
		for (int alleleNo : matchMap.keySet().stream().sorted().toArray(Integer[]::new)) {
			for (ClinVarAnnotation anno : matchMap.get(alleleNo)) {
				ArrayList<String> tmp = new ArrayList<>();
				tmp.add(encode(builder.getAlleles().get(alleleNo).toString()));
				tmp.add(encode(anno.getHgvsVariant()));
				tmp.add(Joiner.on("&").join(anno.getOrigin()));

				result.add(Joiner.on('|').useForNull("").join(tmp));
			}
		}
		return result;
	}

	private ArrayList<String> buildVarInfo(VariantContextBuilder builder,
			ArrayListMultimap<Integer, ClinVarAnnotation> matchMap) {
		ArrayList<String> result = new ArrayList<>();
		for (int alleleNo : matchMap.keySet().stream().sorted().toArray(Integer[]::new)) {
			for (ClinVarAnnotation anno : matchMap.get(alleleNo)) {
				for (ClinVarSourceInfo srcInfo : anno.getSourceInfos()) {
					ArrayList<String> tmp = new ArrayList<>();
					tmp.add(encode(builder.getAlleles().get(alleleNo).toString()));
					tmp.add(encode(srcInfo.getDbName()));
					tmp.add(encode(srcInfo.getDbId()));
					tmp.add(encode(Joiner.on("&").join(anno.getOrigin())));

					result.add(Joiner.on('|').useForNull("").join(tmp));
				}
			}
		}
		return result;
	}

	private ArrayList<String> buildDiseaseInfo(VariantContextBuilder builder,
			ArrayListMultimap<Integer, ClinVarAnnotation> matchMap) {
		ArrayList<String> result = new ArrayList<>();
		for (int alleleNo : matchMap.keySet().stream().sorted().toArray(Integer[]::new)) {
			for (ClinVarAnnotation anno : matchMap.get(alleleNo)) {
				for (ClinVarDiseaseInfo diseaseInfo : anno.getDiseaseInfos()) {
					ArrayList<String> tmp = new ArrayList<>();
					tmp.add(encode(builder.getAlleles().get(alleleNo).toString()));
					tmp.add(encode(diseaseInfo.getSignificance().getLabel()));
					tmp.add(encode(diseaseInfo.getDiseaseDB()));
					tmp.add(encode(diseaseInfo.getDiseaseDBID()));
					tmp.add(encode(diseaseInfo.getDiseaseDBName()));
					tmp.add(encode(diseaseInfo.getRevisionStatus().getLabel()));
					tmp.add(encode(diseaseInfo.getClinicalAccession()));

					result.add(Joiner.on('|').useForNull("").join(tmp));
				}
			}
		}
		return result;
	}

}
