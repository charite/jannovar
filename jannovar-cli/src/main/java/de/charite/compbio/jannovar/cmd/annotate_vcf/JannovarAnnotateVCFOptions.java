package de.charite.compbio.jannovar.cmd.annotate_vcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationOptions;
import de.charite.compbio.jannovar.cmd.JannovarBaseOptions;
import de.charite.compbio.jannovar.filter.facade.PedigreeFilterOptions;
import de.charite.compbio.jannovar.filter.facade.ThresholdFilterOptions;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

/**
 * Options for annotating VCF files
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class JannovarAnnotateVCFOptions extends JannovarAnnotationOptions {

	/** Whether or not to escape ANN field */
	private boolean escapeAnnField = true;

	/** Path to input VCF file */
	private String pathInputVCF = null;

	/** Interval to annotate */
	private String interval = "";

	/** Path to output VCF file */
	private String pathOutputVCF = null;

	/** Path to dbSNP VCF file to use for the annotation */
	public String pathVCFDBSNP = null;

	/** Prefix to use for dbSNP VCF INFO Fields */
	public String prefixDBSNP = null;

	/**
	 * Path to the reference FAI-indexed FASTA file (required for dbSNP/ExAC/UK10K-based annotation
	 */
	public String pathFASTARef = null;

	/** Path to ExAC VCF file to use for the annotation */
	public String pathVCFExac;

	/** Prefix to use for ExAC VCF INFO Fields */
	public String prefixExac;

	/** Path to gnomAD exomes VCF file to use for the annotation */
	public String pathVCFGnomadExomes;

	/** Prefix to use for gnomAD exomes INFO fields */
	public String prefixGnomadExomes;

	/** Path to gnomAD genomes VCF file to use for the annotation */
	public String pathVCFGnomadGenomes;

	/** Prefix to use for gnomAD genomes INFO fields */
	public String prefixGnomadGenomes;

	/** Path to UK10K VCF file to use for the annotation */
	public String pathVCFUK10K;

	/** Prefix to use for UK10K VCF INFO Fields */
	public String prefixUK10K;

	/** Path to ClinVar VCF file to use for the annotation */
	public String pathClinVar;

	/** Prefix to use for ClinVar VCF INFO Fields */
	public String prefixClinVar;

	/** Path to COSMIC VCF file to use for the annotation */
	public String pathCosmic;

	/** Prefix to use for COSMIC VCF INFO Fields */
	public String prefixCosmic;

	/** Path to pedigree file */
	public String pathPedFile;

	/**
	 * Whether or not to perform compatible inheritance mode annotation with the assumption that the
	 * single individual is the affected index.
	 */
	public boolean annotateAsSingletonPedigree;

	/** Whether or not to use threshold-based filters */
	public boolean useThresholdFilters;

	/**
	 * Whether or not to use the advanced pedigree filters (mainly useful for de novo variants)
	 */
	public boolean useAdvancedPedigreeFilters;

	/** Threshold filter: minimal coverage at a site for heterozygous calls */
	private int threshFiltMinGtCovHet;

	/** Threshold filter: minimal coverage at a site for homozygous calls */
	private int threshFiltMinGtCovHomAlt;

	/** Threshold filter: maximal coverage at a site for any call */
	private int threshFiltMaxCov;

	/** Threshold filter: minimal genotype for calls */
	private int threshFiltMinGtGq;

	/**
	 * Threshold filter: minimal alternative allele fraction for heterozygous calls
	 */
	private double threshFiltMinGtAafHet;

	/**
	 * Threshold filter: maximal alternative allele fraction for heterozygous calls
	 */
	private double threshFiltMaxGtAafHet;

	/**
	 * Threshold filter: minimal alternative allele fraction for homozygous alternative calls
	 */
	private double threshFiltMinGtAafHomAlt;

	/**
	 * Threshold filter: maximal alternative allele fraction for homozygous ref calls
	 */
	private double threshFiltMaxGtAafHomRef;

	/**
	 * Threshold filter: maximal allele frequency for autosomal dominant inheritance mode
	 */
	private double threshFiltMaxAlleleFrequencyAd;

	/**
	 * Threshold filter: maximal allele frequency for autosomal recessive inheritance mode
	 */
	private double threshFiltMaxAlleleFrequencyAr;

	/** Enable off target filter */
	private boolean offTargetFilterEnabled;

	/** Count UTR as off-target */
	private boolean offTargetFilterUtrIsOffTarget;

	/** Count intronic splice region (non-consensus) as off-target */
	private boolean offTargetFilterIntronicSpliceIsOffTarget;

	/**
	 * Whether or not to use the variant-wise (AllAffGtFiltered, MaxFreqAd, MaxFreqAr, and OffExome)
	 * and genotype-wise filters (MaxCov, MinCovHet, MinCovHomAlt, MinGq, MinAafHet, MaxAafHet,
	 * MinAafHomAlt, MinAafHomRef) in inheritance mode compatibility annotation.
	 */
	private boolean inheritanceAnnoUseFilters;

	/** Maximal support of alternative allele in parent for de novo variant. */
	private Integer threshDeNovoParentAd2;

	/** Configuration for annotation with BED files. */
	private List<BedAnnotationOptions> bedAnnotationOptions = new ArrayList<>();

	/**
	 * Setup {@link ArgumentParser}
	 * 
	 * @param subParsers
	 *            {@link Subparsers} to setup
	 */
	public static void setupParser(Subparsers subParsers) {
		BiFunction<String[], Namespace, AnnotateVCFCommand> handler = (argv, args) -> {
			try {
				return new AnnotateVCFCommand(argv, args);
			} catch (CommandLineParsingException e) {
				throw new UncheckedJannovarException("Could not parse command line", e);
			}
		};

		Subparser subParser = subParsers.addParser("annotate-vcf", true).help("annotate VCF files")
				.setDefault("cmd", handler);
		subParser.description("Perform annotation of a single VCF file");

		ArgumentGroup requiredGroup = subParser.addArgumentGroup("Required arguments");
		requiredGroup.addArgument("-i", "--input-vcf").help("Path to input VCF file")
				.required(true);
		requiredGroup.addArgument("-o", "--output-vcf").help("Path to output VCF file")
				.required(true);
		requiredGroup.addArgument("-d", "--database").help("Path to database .ser file")
				.required(true);

		ArgumentGroup annotationGroup = subParser
				.addArgumentGroup("Annotation Arguments (optional)");
		requiredGroup.addArgument("--interval").help("Interval with regions to annotate (optional)")
				.required(false).setDefault("");
		annotationGroup.addArgument("--pedigree-file")
				.help("Pedigree file to use for Mendelian inheritance annotation").required(false);
		annotationGroup.addArgument("--annotate-as-singleton-pedigree")
				.help("Annotate VCF file with single individual as singleton pedigree (singleton assumed to be affected)")
				.required(false).setDefault(false).action(Arguments.storeTrue());
		annotationGroup.addArgument("--ref-fasta").help(
				"Path to FAI-indexed reference FASTA file, required for dbSNP/ExAC/UK10K-based annotation");
		annotationGroup.addArgument("--dbsnp-vcf")
				.help("Path to dbSNP VCF file, activates dbSNP annotation").required(false);
		annotationGroup.addArgument("--dbsnp-prefix").help("Prefix for dbSNP annotations")
				.setDefault("DBSNP_").required(false);
		annotationGroup.addArgument("--exac-vcf")
				.help("Path to ExAC VCF file, activates ExAC annotation").required(false);
		annotationGroup.addArgument("--exac-prefix").help("Prefix for ExAC annotations")
				.setDefault("EXAC_").required(false);
		annotationGroup.addArgument("--gnomad-exomes-vcf")
				.help("Path to gnomAD exomes VCF file, activates gnomAD exomes annotation")
				.required(false);
		annotationGroup.addArgument("--gnomad-exomes-prefix")
				.help("Prefix for ExgnomAD exomes AC annotations").setDefault("GNOMAD_EXOMES_")
				.required(false);
		annotationGroup.addArgument("--gnomad-genomes-vcf")
				.help("Path to gnomAD genomes VCF file, activates gnomAD genomes annotation")
				.required(false);
		annotationGroup.addArgument("--gnomad-genomes-prefix")
				.help("Prefix for ExgnomAD genomes AC annotations").setDefault("GNOMAD_GENOMES_")
				.required(false);
		annotationGroup.addArgument("--uk10k-vcf")
				.help("Path to UK10K VCF file, activates UK10K annotation").required(false);
		annotationGroup.addArgument("--uk10k-prefix").help("Prefix for UK10K annotations")
				.setDefault("UK10K_").required(false);
		annotationGroup.addArgument("--clinvar-vcf")
				.help("Path to ClinVar file, activates ClinVar annotation").required(false);
		annotationGroup.addArgument("--clinvar-prefix").help("Prefix for ClinVar annotations")
				.setDefault("CLINVAR_").required(false);
		annotationGroup.addArgument("--cosmic-vcf")
				.help("Path to COSMIC file, activates COSMIC annotation").required(false);
		annotationGroup.addArgument("--cosmic-prefix").help("Prefix for COSMIC annotations")
				.setDefault("COSMIC_").required(false);
		annotationGroup.addArgument("--inheritance-anno-use-filters")
				.help("Use filters in inheritance mode annotation").setDefault(false)
				.action(Arguments.storeTrue());

		ArgumentGroup bedAnnotationGroup = subParser
				.addArgumentGroup("BED-based Annotation (optional)");
		bedAnnotationGroup.addArgument("--bed-annotation")
				.help("Add BED file to use for annotating. The value must be of the format "
						+ "\"pathToBed:infoField:description[:colNo]\".")
				.action(Arguments.append());

		ArgumentGroup threshFilterGroup = subParser
				.addArgumentGroup("Threshold-filter related arguments");
		threshFilterGroup.addArgument("--use-threshold-filters").help("Use threshold-based filters")
				.setDefault(false).action(Arguments.storeTrue());
		ThresholdFilterOptions threshDefaults = ThresholdFilterOptions.buildDefaultOptions();
		threshFilterGroup.addArgument("--gt-thresh-filt-min-cov-het")
				.help("Minimal coverage for het. call").setDefault(threshDefaults.getMinGtCovHet())
				.type(Integer.class);
		threshFilterGroup.addArgument("--gt-thresh-filt-min-cov-hom-alt")
				.help("Minimal coverage for hom. alt calls")
				.setDefault(threshDefaults.getMinGtCovHomAlt()).type(Integer.class);
		threshFilterGroup.addArgument("--gt-thresh-filt-max-cov")
				.help("Maximal coverage for a sample").setDefault(threshDefaults.getMaxCov())
				.type(Integer.class);
		threshFilterGroup.addArgument("--gt-thresh-filt-min-gq")
				.help("Minimal genotype call quality").setDefault(threshDefaults.getMinGtGq())
				.type(Integer.class);
		threshFilterGroup.addArgument("--gt-thresh-filt-min-aaf-het")
				.help("Minimal het. call alternate allele fraction")
				.setDefault(threshDefaults.getMinGtAafHet()).type(Double.class);
		threshFilterGroup.addArgument("--gt-thresh-filt-max-aaf-het")
				.help("Maximal het. call alternate allele fraction")
				.setDefault(threshDefaults.getMaxGtAafHet()).type(Double.class);
		threshFilterGroup.addArgument("--gt-thresh-filt-min-aaf-hom-alt")
				.help("Minimal hom. alt call alternate allele fraction")
				.setDefault(threshDefaults.getMinGtAafHomAlt()).type(Double.class);
		threshFilterGroup.addArgument("--gt-thresh-filt-max-aaf-hom-ref")
				.help("Maximal hom. ref call alternate allele fraction")
				.setDefault(threshDefaults.getMaxGtAafHomRef()).type(Double.class);
		threshFilterGroup.addArgument("--var-thresh-max-allele-freq-ad")
				.help("Maximal allele fraction for autosomal dominant inheritance mode")
				.setDefault(threshDefaults.getMaxAlleleFrequencyAd()).type(Double.class);
		threshFilterGroup.addArgument("--var-thresh-max-allele-freq-ar")
				.help("Maximal allele fraction for autosomal recessive inheritance mode")
				.setDefault(threshDefaults.getMaxAlleleFrequencyAr()).type(Double.class);
		PedigreeFilterOptions pedDefaults = PedigreeFilterOptions.buildDefaultOptions();
		threshFilterGroup.addArgument("--use-advanced-pedigree-filters")
				.help("Use advanced pedigree-based filters (mainly useful for de novo variants)")
				.setDefault(false).action(Arguments.storeTrue());
		threshFilterGroup.addArgument("--de-novo-max-parent-ad2")
				.help("Maximal support of alternative allele in parent for de novo variants.")
				.type(Integer.class).setDefault(pedDefaults.getDeNovoMaxParentAd2());

		ArgumentGroup offTargetGroup = subParser.addArgumentGroup("Exome on/off target filters");
		offTargetGroup.addArgument("--enable-off-target-filter")
				.help("Enable filter for on/off-target based on effect impact").setDefault(false)
				.action(Arguments.storeTrue());
		offTargetGroup.addArgument("--utr-is-off-target")
				.help("Make UTR count as off-target (default is to count UTR as on-target)")
				.setDefault(false).action(Arguments.storeTrue());
		offTargetGroup.addArgument("--intronic-splice-is-off-target")
				.help("Make intronic (non-consensus site) splice region count as off-target (default is to count as on-target)")
				.setDefault(false).action(Arguments.storeTrue());

		ArgumentGroup optionalGroup = subParser.addArgumentGroup("Other, optional Arguments");
		optionalGroup.addArgument("--no-escape-ann-field")
				.help("Disable escaping of INFO/ANN field in VCF output").dest("escape_ann_field")
				.setDefault(true).action(Arguments.storeFalse());
		optionalGroup.addArgument("--show-all").help("Show all effects").setDefault(false)
				.action(Arguments.storeTrue());
		optionalGroup.addArgument("--no-3-prime-shifting")
				.help("Disable shifting towards 3' of transcript").dest("3_prime_shifting")
				.setDefault(true).action(Arguments.storeFalse());
		optionalGroup.addArgument("--3-letter-amino-acids")
				.help("Enable usage of 3 letter amino acid codes").setDefault(false)
				.action(Arguments.storeTrue());

		JannovarBaseOptions.setupParser(subParser);
	}

	@Override
	public void setFromArgs(Namespace args) throws CommandLineParsingException {
		super.setFromArgs(args);

		interval = args.getString("interval");
		escapeAnnField = args.getBoolean("escape_ann_field");
		pathInputVCF = args.getString("input_vcf");
		pathOutputVCF = args.getString("output_vcf");
		pathPedFile = args.getString("pedigree_file");
		annotateAsSingletonPedigree = args.getBoolean("annotate_as_singleton_pedigree");

		pathFASTARef = args.getString("ref_fasta");
		pathVCFDBSNP = args.getString("dbsnp_vcf");
		prefixDBSNP = args.getString("dbsnp_prefix");
		pathVCFExac = args.getString("exac_vcf");
		prefixExac = args.getString("exac_prefix");
		pathVCFGnomadExomes = args.getString("gnomad_exomes_vcf");
		prefixGnomadExomes = args.getString("gnomad_exomes_prefix");
		pathVCFGnomadGenomes = args.getString("gnomad_genomes_vcf");
		prefixGnomadGenomes = args.getString("gnomad_genomes_prefix");
		pathVCFUK10K = args.getString("uk10k_vcf");
		prefixUK10K = args.getString("uk10k_prefix");
		pathClinVar = args.getString("clinvar_vcf");
		prefixClinVar = args.getString("clinvar_prefix");
		pathCosmic = args.getString("cosmic_vcf");
		prefixCosmic = args.getString("cosmic_prefix");
		inheritanceAnnoUseFilters = args.getBoolean("inheritance_anno_use_filters");
		
		for (Object o : args.getList("bed_annotation")) {
			final String s = (String)o;
			bedAnnotationOptions.add(BedAnnotationOptions.parseFrom(s));
		}

		useThresholdFilters = args.getBoolean("use_threshold_filters");
		threshFiltMinGtCovHet = args.getInt("gt_thresh_filt_min_cov_het");
		threshFiltMinGtCovHomAlt = args.getInt("gt_thresh_filt_min_cov_hom_alt");
		threshFiltMaxCov = args.getInt("gt_thresh_filt_max_cov");
		threshFiltMinGtGq = args.getInt("gt_thresh_filt_min_gq");
		threshFiltMinGtAafHet = args.getDouble("gt_thresh_filt_min_aaf_het");
		threshFiltMaxGtAafHet = args.getDouble("gt_thresh_filt_max_aaf_het");
		threshFiltMinGtAafHomAlt = args.getDouble("gt_thresh_filt_min_aaf_hom_alt");
		threshFiltMaxGtAafHomRef = args.getDouble("gt_thresh_filt_max_aaf_hom_ref");
		threshFiltMaxAlleleFrequencyAd = args.getDouble("var_thresh_max_allele_freq_ad");
		threshFiltMaxAlleleFrequencyAr = args.getDouble("var_thresh_max_allele_freq_ar");
		useAdvancedPedigreeFilters = args.getBoolean("use_advanced_pedigree_filters");
		setThreshDeNovoParentAd2(args.getInt("de_novo_max_parent_ad2"));

		offTargetFilterEnabled = args.getBoolean("enable_off_target_filter");
		offTargetFilterUtrIsOffTarget = args.getBoolean("utr_is_off_target");
		offTargetFilterIntronicSpliceIsOffTarget = args.getBoolean("intronic_splice_is_off_target");

		if (pathFASTARef == null && (pathVCFDBSNP != null || pathVCFExac != null
				|| pathVCFUK10K != null || pathClinVar != null || pathCosmic != null
				|| pathVCFGnomadExomes != null || pathVCFGnomadGenomes != null))
			throw new CommandLineParsingException(
					"Command --ref-fasta required when using dbSNP, ExAC, UK10K, ClinVar, or COSMIC annotations.");
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public String getPathInputVCF() {
		return pathInputVCF;
	}

	public void setPathInputVCF(String pathInputVCF) {
		this.pathInputVCF = pathInputVCF;
	}

	public String getPathOutputVCF() {
		return pathOutputVCF;
	}

	public void setPathOutputVCF(String pathOutputVCF) {
		this.pathOutputVCF = pathOutputVCF;
	}

	public boolean isEscapeAnnField() {
		return escapeAnnField;
	}

	public void setEscapeAnnField(boolean escapeAnnField) {
		this.escapeAnnField = escapeAnnField;
	}

	public String getPathVCFDBSNP() {
		return pathVCFDBSNP;
	}

	public void setPathVCFDBSNP(String pathVCFDBSNP) {
		this.pathVCFDBSNP = pathVCFDBSNP;
	}

	public String getPrefixDBSNP() {
		return prefixDBSNP;
	}

	public void setPrefixDBSNP(String prefixDBSNP) {
		this.prefixDBSNP = prefixDBSNP;
	}

	public String getPathFASTARef() {
		return pathFASTARef;
	}

	public void setPathFASTARef(String pathFASTARef) {
		this.pathFASTARef = pathFASTARef;
	}

	public String getPathVCFExac() {
		return pathVCFExac;
	}

	public void setPathVCFExac(String pathVCFExac) {
		this.pathVCFExac = pathVCFExac;
	}

	public String getPrefixExac() {
		return prefixExac;
	}

	public void setPrefixExac(String prefixExac) {
		this.prefixExac = prefixExac;
	}

	public String getPathVCFUK10K() {
		return pathVCFUK10K;
	}

	public void setPathVCFUK10K(String pathVCFUK10K) {
		this.pathVCFUK10K = pathVCFUK10K;
	}

	public String getPrefixUK10K() {
		return prefixUK10K;
	}

	public void setPrefixUK10K(String prefixUK10K) {
		this.prefixUK10K = prefixUK10K;
	}

	public String getPathClinVar() {
		return pathClinVar;
	}

	public void setPathClinVar(String pathClinVar) {
		this.pathClinVar = pathClinVar;
	}

	public String getPrefixClinVar() {
		return prefixClinVar;
	}

	public void setPrefixClinVar(String prefixClinVar) {
		this.prefixClinVar = prefixClinVar;
	}

	public String getPathCosmic() {
		return pathCosmic;
	}

	public void setPathCosmic(String pathCosmic) {
		this.pathCosmic = pathCosmic;
	}

	public String getPrefixCosmic() {
		return prefixCosmic;
	}

	public void setPrefixCosmic(String prefixCosmic) {
		this.prefixCosmic = prefixCosmic;
	}

	public String getPathVCFGnomadExomes() {
		return pathVCFGnomadExomes;
	}

	public void setPathVCFGnomadExomes(String pathVCFGnomadExomes) {
		this.pathVCFGnomadExomes = pathVCFGnomadExomes;
	}

	public String getPrefixGnomadExomes() {
		return prefixGnomadExomes;
	}

	public void setPrefixGnomadExomes(String prefixGnomadExomes) {
		this.prefixGnomadExomes = prefixGnomadExomes;
	}

	public String getPathVCFGnomadGenomes() {
		return pathVCFGnomadGenomes;
	}

	public void setPathVCFGnomadGenomes(String pathVCFGnomadGenomes) {
		this.pathVCFGnomadGenomes = pathVCFGnomadGenomes;
	}

	public String getPrefixGnomadGenomes() {
		return prefixGnomadGenomes;
	}

	public void setPrefixGnomadGenomes(String prefixGnomadGenomes) {
		this.prefixGnomadGenomes = prefixGnomadGenomes;
	}

	public boolean isAnnotateAsSingletonPedigree() {
		return annotateAsSingletonPedigree;
	}

	public void setAnnotateAsSingletonPedigree(boolean annotateAsSingletonPedigree) {
		this.annotateAsSingletonPedigree = annotateAsSingletonPedigree;
	}

	public String getPathPedFile() {
		return pathPedFile;
	}

	public void setPathPedFile(String pathPedFile) {
		this.pathPedFile = pathPedFile;
	}

	public boolean isUseThresholdFilters() {
		return useThresholdFilters;
	}

	public void setUseThresholdFilters(boolean useThresholdFilters) {
		this.useThresholdFilters = useThresholdFilters;
	}

	public int getThreshFiltMinGtCovHet() {
		return threshFiltMinGtCovHet;
	}

	public void setThreshFiltMinGtCovHet(int threshFiltMinGtCovHet) {
		this.threshFiltMinGtCovHet = threshFiltMinGtCovHet;
	}

	public int getThreshFiltMinGtCovHomAlt() {
		return threshFiltMinGtCovHomAlt;
	}

	public void setThreshFiltMinGtCovHomAlt(int threshFiltMinGtCovHomAlt) {
		this.threshFiltMinGtCovHomAlt = threshFiltMinGtCovHomAlt;
	}

	public int getThreshFiltMaxCov() {
		return threshFiltMaxCov;
	}

	public void setThreshFiltMaxCov(int threshFiltMaxCov) {
		this.threshFiltMaxCov = threshFiltMaxCov;
	}

	public int getThreshFiltMinGtGq() {
		return threshFiltMinGtGq;
	}

	public void setThreshFiltMinGtGq(int threshFiltMinGtGq) {
		this.threshFiltMinGtGq = threshFiltMinGtGq;
	}

	public double getThreshFiltMinGtAafHet() {
		return threshFiltMinGtAafHet;
	}

	public void setThreshFiltMinGtAafHet(double threshFiltMinGtAafHet) {
		this.threshFiltMinGtAafHet = threshFiltMinGtAafHet;
	}

	public double getThreshFiltMaxGtAafHet() {
		return threshFiltMaxGtAafHet;
	}

	public void setThreshFiltMaxGtAafHet(double threshFiltMaxGtAafHet) {
		this.threshFiltMaxGtAafHet = threshFiltMaxGtAafHet;
	}

	public double getThreshFiltMinGtAafHomAlt() {
		return threshFiltMinGtAafHomAlt;
	}

	public void setThreshFiltMinGtAafHomAlt(double threshFiltMinGtAafHomAlt) {
		this.threshFiltMinGtAafHomAlt = threshFiltMinGtAafHomAlt;
	}

	public double getThreshFiltMaxGtAafHomRef() {
		return threshFiltMaxGtAafHomRef;
	}

	public void setThreshFiltMaxGtAafHomRef(double threshFiltMaxGtAafHomRef) {
		this.threshFiltMaxGtAafHomRef = threshFiltMaxGtAafHomRef;
	}

	public double getThreshFiltMaxAlleleFrequencyAd() {
		return threshFiltMaxAlleleFrequencyAd;
	}

	public void setThreshFiltMaxAlleleFrequencyAD(double threshFiltMaxAlleleFrequencyAd) {
		this.threshFiltMaxAlleleFrequencyAd = threshFiltMaxAlleleFrequencyAd;
	}

	public double getThreshFiltMaxAlleleFrequencyAr() {
		return threshFiltMaxAlleleFrequencyAr;
	}

	public void setThreshFiltMaxAlleleFrequencyAR(double threshFiltMaxAlleleFrequencyAr) {
		this.threshFiltMaxAlleleFrequencyAr = threshFiltMaxAlleleFrequencyAr;
	}

	public boolean isOffTargetFilterEnabled() {
		return offTargetFilterEnabled;
	}

	public void setOffTargetFilterEnabled(boolean offTargetFilterEnabled) {
		this.offTargetFilterEnabled = offTargetFilterEnabled;
	}

	public boolean isOffTargetFilterUtrIsOffTarget() {
		return offTargetFilterUtrIsOffTarget;
	}

	public void setOffTargetFilterUtrIsOffTarget(boolean offTargetFilterUtrIsOffTarget) {
		this.offTargetFilterUtrIsOffTarget = offTargetFilterUtrIsOffTarget;
	}

	public boolean isOffTargetFilterIntronicSpliceIsOffTarget() {
		return offTargetFilterIntronicSpliceIsOffTarget;
	}

	public void setOffTargetFilterIntronicSpliceIsOffTarget(
			boolean offTargetFilterIntronicSpliceIsOffTarget) {
		this.offTargetFilterIntronicSpliceIsOffTarget = offTargetFilterIntronicSpliceIsOffTarget;
	}

	public void setThreshFiltMaxAlleleFrequencyAd(double threshFiltMaxAlleleFrequencyAd) {
		this.threshFiltMaxAlleleFrequencyAd = threshFiltMaxAlleleFrequencyAd;
	}

	public void setThreshFiltMaxAlleleFrequencyAr(double threshFiltMaxAlleleFrequencyAr) {
		this.threshFiltMaxAlleleFrequencyAr = threshFiltMaxAlleleFrequencyAr;
	}

	public boolean isInheritanceAnnoUseFilters() {
		return inheritanceAnnoUseFilters;
	}

	public void setInheritanceAnnoUseFilters(boolean inheritanceAnnoUseFilters) {
		this.inheritanceAnnoUseFilters = inheritanceAnnoUseFilters;
	}

	public boolean isUseAdvancedPedigreeFilters() {
		return useAdvancedPedigreeFilters;
	}

	public void setUseAdvancedPedigreeFilters(boolean useAdvancedPedigreeFilters) {
		this.useAdvancedPedigreeFilters = useAdvancedPedigreeFilters;
	}

	public List<BedAnnotationOptions> getBedAnnotationOptions() {
		return bedAnnotationOptions;
	}

	public void setBedAnnotationOptions(List<BedAnnotationOptions> bedAnnotationOptions) {
		this.bedAnnotationOptions = bedAnnotationOptions;
	}

	@Override
	public String toString() {
		return "JannovarAnnotateVCFOptions [escapeAnnField=" + escapeAnnField + ", pathInputVCF="
				+ pathInputVCF + ", interval=" + interval + ", pathOutputVCF=" + pathOutputVCF
				+ ", pathVCFDBSNP=" + pathVCFDBSNP + ", prefixDBSNP=" + prefixDBSNP
				+ ", pathFASTARef=" + pathFASTARef + ", pathVCFExac=" + pathVCFExac
				+ ", prefixExac=" + prefixExac + ", pathVCFGnomadExomes=" + pathVCFGnomadExomes
				+ ", prefixGnomadExomes=" + prefixGnomadExomes + ", pathVCFGnomadGenomes="
				+ pathVCFGnomadGenomes + ", prefixGnomadGenomes=" + prefixGnomadGenomes
				+ ", pathVCFUK10K=" + pathVCFUK10K + ", prefixUK10K=" + prefixUK10K
				+ ", pathClinVar=" + pathClinVar + ", prefixClinVar=" + prefixClinVar
				+ ", pathCosmic=" + pathCosmic + ", prefixCosmic=" + prefixCosmic + ", pathPedFile="
				+ pathPedFile + ", annotateAsSingletonPedigree=" + annotateAsSingletonPedigree
				+ ", useThresholdFilters=" + useThresholdFilters + ", useAdvancedPedigreeFilters="
				+ useAdvancedPedigreeFilters + ", threshFiltMinGtCovHet=" + threshFiltMinGtCovHet
				+ ", threshFiltMinGtCovHomAlt=" + threshFiltMinGtCovHomAlt + ", threshFiltMaxCov="
				+ threshFiltMaxCov + ", threshFiltMinGtGq=" + threshFiltMinGtGq
				+ ", threshFiltMinGtAafHet=" + threshFiltMinGtAafHet + ", threshFiltMaxGtAafHet="
				+ threshFiltMaxGtAafHet + ", threshFiltMinGtAafHomAlt=" + threshFiltMinGtAafHomAlt
				+ ", threshFiltMaxGtAafHomRef=" + threshFiltMaxGtAafHomRef
				+ ", threshFiltMaxAlleleFrequencyAd=" + threshFiltMaxAlleleFrequencyAd
				+ ", threshFiltMaxAlleleFrequencyAr=" + threshFiltMaxAlleleFrequencyAr
				+ ", offTargetFilterEnabled=" + offTargetFilterEnabled
				+ ", offTargetFilterUtrIsOffTarget=" + offTargetFilterUtrIsOffTarget
				+ ", offTargetFilterIntronicSpliceIsOffTarget="
				+ offTargetFilterIntronicSpliceIsOffTarget + ", inheritanceAnnoUseFilters="
				+ inheritanceAnnoUseFilters + ", threshDeNovoParentAd2=" + threshDeNovoParentAd2
				+ ", bedAnnotationOptions=" + bedAnnotationOptions + "]";
	}

	public Integer getThreshDeNovoParentAd2() {
		return threshDeNovoParentAd2;
	}

	public void setThreshDeNovoParentAd2(Integer threshDeNovoParentAd2) {
		this.threshDeNovoParentAd2 = threshDeNovoParentAd2;
	}

	/**
	 * Configuration for annotation with BED file.
	 */
	public static class BedAnnotationOptions {

		/**
		 * Construct new BED annotation from command line option value.
		 * 
		 * <p>
		 * The value must have the format: <code>pathToBed:infoField:description[:colNo]</code>
		 * </p>
		 * 
		 * @param strValue
		 *            String to parse from
		 * @return Constructed {@link BedAnnotationOptions} from the given string value.
		 */
		public static BedAnnotationOptions parseFrom(String strValue) {
			String tokens[] = strValue.split(":", 4);
			if (tokens.length < 3) {
				throw new RuntimeException("Could not parse BED annotation from " + strValue);
			} else if (tokens.length == 3) {
				return new BedAnnotationOptions(tokens[0], tokens[1], tokens[2]);
			} else {
				return new BedAnnotationOptions(tokens[0], tokens[1], tokens[2],
						Integer.parseInt(tokens[3]));
			}
		}

		/** Path to BED file */
		private final String pathBed;

		/** Label to use for INFO field. */
		private final String infoField;

		/** Description to use for INFO field. */
		private final String description;

		/** 0-based column to write into VCF file, if any, <code>-1</code> for none */
		private final int colNo;

		public BedAnnotationOptions(String pathBed, String infoField, String description) {
			this(pathBed, infoField, description, -1);
		}

		public BedAnnotationOptions(String pathBed, String infoField, String description,
				int colNo) {
			this.pathBed = pathBed;
			this.infoField = infoField;
			this.description = description;
			this.colNo = colNo;
		}

		public String getPathBed() {
			return pathBed;
		}

		public int getColNo() {
			return colNo;
		}

		public String getInfoField() {
			return infoField;
		}

		public String getDescription() {
			return description;
		}

		@Override
		public String toString() {
			return "BedAnnotationOptions [pathBed=" + pathBed + ", colNo=" + colNo + ", infoField="
					+ infoField + ", description=" + description + "]";
		}

	}

}
