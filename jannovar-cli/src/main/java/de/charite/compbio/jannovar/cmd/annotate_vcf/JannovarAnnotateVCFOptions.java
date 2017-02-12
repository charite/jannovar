package de.charite.compbio.jannovar.cmd.annotate_vcf;

import java.util.function.BiFunction;

import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationOptions;
import de.charite.compbio.jannovar.cmd.JannovarBaseOptions;
import de.charite.compbio.jannovar.filter.facade.ThresholdFilterOptions;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
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

	/** Path to output VCF file */
	private String pathOutputVCF = null;

	/** Path to dbSNP VCF file to use for the annotation */
	public String pathVCFDBSNP = null;

	/** Prefix to use for dbSNP VCF INFO Fields */
	public String prefixDBSNP = null;

	/** Path to the reference FAI-indexed FASTA file (required for dbSNP/ExAC/UK10K-based annotation */
	public String pathFASTARef = null;

	/** Path to ExAC VCF file to use for the annotation */
	public String pathVCFExac;

	/** Prefix to use for ExAC VCF INFO Fields */
	public String prefixExac;

	/** Path to UK10K VCF file to use for the annotation */
	public String pathVCFUK10K;

	/** Prefix to use for UK10K VCF INFO Fields */
	public String prefixUK10K;

	/** Path to ClinVar VCF file to use for the annotation */
	public String pathClinVar;

	/** Prefix to use for ClinVar VCF INFO Fields */
	public String prefixClinVar;

	/** Path to pedigree file */
	public String pathPedFile;

	/** Whether or not to use threshold-based filters */
	public boolean useThresholdFilters;

	/** Threshold filter: minimal coverage at a site for heterozygous calls */
	private int threshFiltMinGtCovHet;

	/** Threshold filter: minimal coverage at a site for homozygous calls */
	private int threshFiltMinGtCovHomAlt;

	/** Threshold filter: maximal coverage at a site for any call */
	private int threshFiltMaxCov;

	/** Threshold filter: minimal genotype for calls */
	private int threshFiltMinGtGq;

	/** Threshold filter: minimal alternative allele fraction for heterozygous calls */
	private double threshFiltMinGtAafHet;

	/** Threshold filter: maximal alternative allele fraction for heterozygous calls */
	private double threshFiltMaxGtAafHet;

	/** Threshold filter: minimal alternative allele fraction for homozygous alternative calls */
	private double threshFiltMinGtAafHomAlt;

	/** Threshold filter: maximal alternative allele fraction for homozygous ref calls */
	private double threshFiltMaxGtAafHomRef;

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

		Subparser subParser = subParsers.addParser("annotate-vcf", true).help("annotate VCF files").setDefault("cmd",
				handler);
		subParser.description("Perform annotation of a single VCF file");

		ArgumentGroup requiredGroup = subParser.addArgumentGroup("Required arguments");
		requiredGroup.addArgument("-i", "--input-vcf").help("Path to input VCF file").required(true);
		requiredGroup.addArgument("-o", "--output-vcf").help("Path to output VCF file").required(true);
		requiredGroup.addArgument("-d", "--database").help("Path to database .ser file").required(true);

		ArgumentGroup annotationGroup = subParser.addArgumentGroup("Annotation Arguments (optional)");
		annotationGroup.addArgument("--pedigree-file").help("Pedigree file to use for Mendelian inheritance annotation")
				.required(false);
		annotationGroup.addArgument("--ref-fasta")
				.help("Path to FAI-indexed reference FASTA file, required for dbSNP/ExAC/UK10K-based annotation");
		annotationGroup.addArgument("--dbsnp-vcf").help("Path to dbSNP VCF file, activates dbSNP annotation")
				.required(false);
		annotationGroup.addArgument("--dbsnp-prefix").help("Prefix for dbSNP annotations").setDefault("DBSNP_")
				.required(false);
		annotationGroup.addArgument("--exac-vcf").help("Path to ExAC VCF file, activates ExAC annotation").nargs("?")
				.required(false);
		annotationGroup.addArgument("--exac-prefix").help("Prefix for ExAC annotations").setDefault("EXAC_")
				.required(false);
		annotationGroup.addArgument("--uk10k-vcf").help("Path to UK10K VCF file, activates UK10K annotation").nargs("?")
				.required(false);
		annotationGroup.addArgument("--uk10k-prefix").help("Prefix for UK10K annotations").setDefault("UK10K_")
				.required(false);
		annotationGroup.addArgument("--clinvar-vcf").help("Path to ClinVar file, activates ClinVar annotation")
				.nargs("?").required(false);
		annotationGroup.addArgument("--clinvar-prefix").help("Prefix for ClinVar annotations").setDefault("CLINVAR_")
				.required(false);

		ArgumentGroup threshFilterGroup = subParser.addArgumentGroup("Threshold-filter related arguments");
		threshFilterGroup.addArgument("--use-gt-threshold-filters").help("Use threshold-based filters on genotypes")
				.setDefault(false).action(Arguments.storeTrue());
		ThresholdFilterOptions threshDefaults = ThresholdFilterOptions.buildDefaultOptions();
		threshFilterGroup.addArgument("--gt-thresh-filt-min-cov-het").help("Minimal coverage for het. call")
				.setDefault(threshDefaults.getMinGtCovHet()).type(Integer.class);
		threshFilterGroup.addArgument("--gt-thresh-filt-min-cov-hom-alt").help("Minimal coverage for hom. alt calls")
				.setDefault(threshDefaults.getMinGtCovHomAlt()).type(Integer.class);
		threshFilterGroup.addArgument("--gt-thresh-filt-max-cov").help("Maximal coverage for a sample")
				.setDefault(threshDefaults.getMaxCov()).type(Integer.class);
		threshFilterGroup.addArgument("--gt-thresh-filt-min-gq").help("Minimal genotype call quality")
				.setDefault(threshDefaults.getMinGtGq()).type(Integer.class);
		threshFilterGroup.addArgument("--gt-thresh-filt-min-aaf-het")
				.help("Minimal het. call alternate allele fraction").setDefault(threshDefaults.getMinGtAafHet())
				.type(Double.class);
		threshFilterGroup.addArgument("--gt-thresh-filt-max-aaf-het")
				.help("Maximal het. call alternate allele fraction").setDefault(threshDefaults.getMaxGtAafHet())
				.type(Double.class);
		threshFilterGroup.addArgument("--gt-thresh-filt-min-aaf-hom-alt")
				.help("Minimal hom. alt call alternate allele fraction").setDefault(threshDefaults.getMinGtAafHomAlt())
				.type(Double.class);
		threshFilterGroup.addArgument("--gt-thresh-filt-max-aaf-hom-ref")
				.help("Maximal hom. ref call alternate allele fraction").setDefault(threshDefaults.getMaxGtAafHomRef())
				.type(Double.class);

		ArgumentGroup optionalGroup = subParser.addArgumentGroup("Other, optional Arguments");
		optionalGroup.addArgument("--no-escape-ann-field").help("Disable escaping of INFO/ANN field in VCF output")
				.dest("escape_ann_field").setDefault(true).action(Arguments.storeFalse());
		optionalGroup.addArgument("--show-all").help("Show all effects").setDefault(false)
				.action(Arguments.storeTrue());
		optionalGroup.addArgument("--no-3-prime-shifting").help("Disable shifting towards 3' of transcript")
				.dest("3_prime_shifting").setDefault(true).action(Arguments.storeFalse());
		optionalGroup.addArgument("--3-letter-amino-acids").help("Enable usage of 3 letter amino acid codes")
				.setDefault(false).action(Arguments.storeTrue());

		JannovarBaseOptions.setupParser(subParser);
	}

	@Override
	public void setFromArgs(Namespace args) throws CommandLineParsingException {
		super.setFromArgs(args);

		escapeAnnField = args.getBoolean("escape_ann_field");
		pathInputVCF = args.getString("input_vcf");
		pathOutputVCF = args.getString("output_vcf");
		pathPedFile = args.getString("pedigree_file");

		pathFASTARef = args.getString("ref_fasta");
		pathVCFDBSNP = args.getString("dbsnp_vcf");
		prefixDBSNP = args.getString("dbsnp_prefix");
		pathVCFExac = args.getString("exac_vcf");
		prefixExac = args.getString("exac_prefix");
		pathVCFUK10K = args.getString("uk10k_vcf");
		prefixUK10K = args.getString("uk10k_prefix");
		pathClinVar = args.getString("clinvar_vcf");
		prefixClinVar = args.getString("clinvar_prefix");

		useThresholdFilters = args.getBoolean("use_gt_threshold_filters");
		threshFiltMinGtCovHet = args.getInt("gt_thresh_filt_min_cov_het");
		threshFiltMinGtCovHomAlt = args.getInt("gt_thresh_filt_min_cov_hom_alt");
		threshFiltMaxCov = args.getInt("gt_thresh_filt_max_cov");
		threshFiltMinGtGq = args.getInt("gt_thresh_filt_min_gq");
		threshFiltMinGtAafHet = args.getDouble("gt_thresh_filt_min_aaf_het");
		threshFiltMaxGtAafHet = args.getDouble("gt_thresh_filt_max_aaf_het");
		threshFiltMinGtAafHomAlt = args.getDouble("gt_thresh_filt_min_aaf_hom_alt");
		threshFiltMaxGtAafHomRef = args.getDouble("gt_thresh_filt_max_aaf_hom_ref");

		if (pathFASTARef == null
				&& (pathVCFDBSNP != null || pathVCFExac != null || pathVCFUK10K != null || pathClinVar != null))
			throw new CommandLineParsingException(
					"Command --ref-fasta required when using dbSNP, ExAC, UK10K, or ClinVar annotations.");
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

	@Override
	public String toString() {
		return "JannovarAnnotateVCFOptions [escapeAnnField=" + escapeAnnField + ", pathInputVCF=" + pathInputVCF
				+ ", pathOutputVCF=" + pathOutputVCF + ", pathVCFDBSNP=" + pathVCFDBSNP + ", prefixDBSNP=" + prefixDBSNP
				+ ", pathFASTARef=" + pathFASTARef + ", pathVCFExac=" + pathVCFExac + ", prefixExac=" + prefixExac
				+ ", pathVCFUK10K=" + pathVCFUK10K + ", prefixUK10K=" + prefixUK10K + ", pathClinVar=" + pathClinVar
				+ ", prefixClinVar=" + prefixClinVar + ", pathPedFile=" + pathPedFile + ", useThresholdFilters="
				+ useThresholdFilters + ", threshFiltMinGtCovHet=" + threshFiltMinGtCovHet
				+ ", threshFiltMinGtCovHomAlt=" + threshFiltMinGtCovHomAlt + ", threshFiltMaxCov=" + threshFiltMaxCov
				+ ", threshFiltMinGtGq=" + threshFiltMinGtGq + ", threshFiltMinGtAafHet=" + threshFiltMinGtAafHet
				+ ", threshFiltMaxGtAafHet=" + threshFiltMaxGtAafHet + ", threshFiltMinGtAafHomAlt="
				+ threshFiltMinGtAafHomAlt + ", threshFiltMaxGtAafHomRef=" + threshFiltMaxGtAafHomRef + "]";
	}

}
