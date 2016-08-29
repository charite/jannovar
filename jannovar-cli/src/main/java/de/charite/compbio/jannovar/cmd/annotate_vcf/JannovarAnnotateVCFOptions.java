package de.charite.compbio.jannovar.cmd.annotate_vcf;

import java.util.function.BiFunction;

import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationOptions;
import de.charite.compbio.jannovar.cmd.JannovarBaseOptions;
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

	/** Path to pedigree file */
	public String pathPedFile;

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

		subParser.addArgument("-i", "--input-vcf").help("Path to input VCF file").required(true);
		subParser.addArgument("-o", "--output-vcf").help("Path to output VCF file").required(true);
		subParser.addArgument("-d", "--database").help("Path to database .ser file").required(true);

		ArgumentGroup annotationGroup = subParser.addArgumentGroup("Annotation Arguments (optional)");
		annotationGroup.addArgument("--pedigree-file").help("Pedigree file to use for Mendelian inheritance annotation")
				.required(false);
		annotationGroup.addArgument("--ref-fasta")
				.help("Path to FAI-indexed reference FASTA file, required for dbSNP/ExAC/UK10K-based annotation");
		annotationGroup.addArgument("--dbsnp-vcf").help("Path to dbSNP VCF file, activates dbSNP annotation")
				.required(false);
		annotationGroup.addArgument("--dbsnp-prefix").help("Prefix for dbSNP annotations").setDefault("DBSNP_")
				.required(false);
		annotationGroup.addArgument("--exac-vcf").help("Path to ExAC VCF file, activates ExAC annotation")
				.required(false);
		annotationGroup.addArgument("--exac-prefix").help("Prefix for ExAC annotations").setDefault("EXAC_")
				.required(false);
		annotationGroup.addArgument("--uk10k-vcf").help("Path to UK10K VCF file, activates UK10K annotation")
				.required(false);
		annotationGroup.addArgument("--uk10k-prefix").help("Prefix for UK10K annotations").setDefault("UK10K_")
				.required(false);

		ArgumentGroup optionalGroup = subParser.addArgumentGroup("Other, optional Arguments");
		optionalGroup.addArgument("--no-escape-ann-field").help("Disable escaping of INFO/ANN field in VCF output")
				.dest("escape_ann_field").setDefault(true).action(Arguments.storeFalse());
		optionalGroup.addArgument("--show-all").help("Show all effects").setDefault(false);
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

	@Override
	public String toString() {
		return "JannovarAnnotateVCFOptions [escapeAnnField=" + escapeAnnField + ", pathInputVCF=" + pathInputVCF
				+ ", pathOutputVCF=" + pathOutputVCF + ", pathVCFDBSNP=" + pathVCFDBSNP + ", prefixDBSNP=" + prefixDBSNP
				+ ", pathFASTARef=" + pathFASTARef + ", pathVCFExac=" + pathVCFExac + ", prefixExac=" + prefixExac
				+ ", pathVCFUK10K=" + pathVCFUK10K + ", prefixUK10K=" + prefixUK10K + ", pathPedFile=" + pathPedFile
				+ ", isUseThreeLetterAminoAcidCode()=" + isUseThreeLetterAminoAcidCode() + ", isNt3PrimeShifting()="
				+ isNt3PrimeShifting() + ", getDatabaseFilePath()=" + getDatabaseFilePath() + ", isReportProgress()="
				+ isReportProgress() + ", getHttpProxy()=" + getHttpProxy() + ", getHttpsProxy()=" + getHttpsProxy()
				+ ", getFtpProxy()=" + getFtpProxy() + "]";
	}

}
