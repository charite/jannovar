package de.charite.compbio.jannovar.cmd.annotate_vcf;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationCommandLineParser;

/**
 * Parser for annotate-vcf command line.
 * 
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public class AnnotateVCFCommandLineParser extends JannovarAnnotationCommandLineParser {

	@Override
	public JannovarOptions parse(String[] argv) throws ParseException, HelpRequestedException {
		// check if Help is set
		CommandLine cmd = parser.parse(helpOptions, argv, true);
		printHelpIfOptionIsSet(cmd);
		// Parse the command line.
		cmd = parser.parse(options, argv);
		printHelpIfOptionIsSet(cmd);

		// Fill the resulting JannovarOptions.
		JannovarOptions result = new JannovarOptions();
		result.printProgressBars = true;
		result.command = JannovarOptions.Command.ANNOTATE_VCF;

		if (cmd.hasOption("verbose"))
			result.verbosity = 2;
		if (cmd.hasOption("very-verbose"))
			result.verbosity = 3;

		if (cmd.hasOption("output-dir"))
			result.outVCFFolder = cmd.getOptionValue("output-dir");

		if (cmd.hasOption("reference-fasta"))
			result.pathFASTARef = cmd.getOptionValue("reference-fasta");

		if (cmd.hasOption("dbsnp-vcf") && !cmd.hasOption("reference-fasta"))
			throw new HelpRequestedException("Argument --reference-fasta is required if --dbsnp-vcf is given");
		if (cmd.hasOption("dbsnp-vcf"))
			result.pathVCFDBSNP = cmd.getOptionValue("dbsnp-vcf");
		if (cmd.hasOption("dbsnp-prefix"))
			result.prefixDBSNP = cmd.getOptionValue("dbsnp-prefix");
		else
			result.prefixDBSNP = "DBSNP_";

		if (cmd.hasOption("exac-vcf") && !cmd.hasOption("reference-fasta"))
			throw new HelpRequestedException("Argument --reference-fasta is required if --exac-vcf is given");
		if (cmd.hasOption("exac-vcf"))
			result.pathVCFExac = cmd.getOptionValue("exac-vcf");
		if (cmd.hasOption("exac-prefix"))
			result.prefixExac = cmd.getOptionValue("exac-prefix");
		else
			result.prefixExac = "EXAC_";

		if (cmd.hasOption("uk10k-vcf") && !cmd.hasOption("reference-fasta"))
			throw new HelpRequestedException("Argument --reference-fasta is required if --uk10k-vcf is given");
		if (cmd.hasOption("uk10k-vcf"))
			result.pathVCFUK10K = cmd.getOptionValue("uk10k-vcf");
		if (cmd.hasOption("uk10k-prefix"))
			result.prefixUK10K = cmd.getOptionValue("uk10k-prefix");
		else
			result.prefixUK10K = "UK10K_";

		if (cmd.hasOption("pedigree-file"))
			result.pathPedFile = cmd.getOptionValue("pedigree-file");

		result.showAll = cmd.hasOption("showall");

		result.writeVCFAnnotationStandardInfoFields = !cmd.hasOption("no-new-info-field");
		result.escapeAnnField = !cmd.hasOption("no-escape-ann-field");
		result.nt3PrimeShifting = !cmd.hasOption("no-3-prime-shifting");
		if (cmd.hasOption("output-infix"))
			result.outputInfix = cmd.getOptionValue("output-infix");

		result.dataFile = cmd.getOptionValue("database");
		result.useThreeLetterAminoAcidCode = cmd.hasOption("three-letter-amino-acid-code");

		for (String vcfPath : cmd.getOptionValues("vcf-in")) {
			result.vcfFilePaths.add(vcfPath);
		}

		return result;
	}

	@Override
	protected void initializeParser() {
		super.initializeParser();

		options.addOption(new Option("a", "showall", false,
				"report annotations for all affected transcripts (by default only one "
						+ "with the highest impact is shown for each alternative allele)"));
		options.addOption(
				new Option("o", "output-dir", true, "output directory (default is to write parallel to input file)"));

		options.addOption(new Option(null, "no-new-info-field", false,
				"do not write out the new VCF annotation standard INFO field \"ANN\" (default is on)"));
		options.addOption(new Option(null, "no-escape-ann-field", false,
				"do not escape characters in INFO field \"ANN\" (default is on)"));
		options.addOption(new Option(null, "no-3-prime-shifting", false,
				"disable shifting of variants towards the 3' end of the transcript (default is on)"));
		options.addOption(new Option(null, "output-infix", true,
				"output infix to place before .vcf/.vcf.gz/.bcf in output file name (default is \".jv\")"));
		options.addOption(Option.builder("i").longOpt("vcf-in").required().hasArgs()
				.desc("VCF file to annotate (.vcf/.gz)").argName("IN.vcf").build());

		options.addOption(new Option(null, "reference-fasta", true,
				"path to FAI-indexed FASTA reference, required for dbSNP annotation"));

		options.addOption(new Option(null, "dbsnp-vcf", true,
				"path to indexed, bgzip-compressed, and normalized dbSNP VCF file"));
		options.addOption(new Option(null, "dbsnp-prefix", true, "prefix to use for dbSNP-based VCF INFO fields"));

		options.addOption(
				new Option(null, "exac-vcf", true, "path to indexed, bgzip-compressed, and normalized ExAC VCF file"));
		options.addOption(new Option(null, "exac-prefix", true, "prefix to use for ExAC-based VCF INFO fields"));

		options.addOption(new Option(null, "uk10k-vcf", true,
				"path to indexed, bgzip-compressed, and normalized UK10K COHORT VCF file"));
		options.addOption(new Option(null, "uk10k-prefix", true, "prefix to use for UK10K-based VCF INFO fields"));

		options.addOption(new Option(null, "pedigree-file", true, "path to pedigree file"));

		options.addOption(new Option(null, "three-letter-amino-acid-code", false,
				"use three-letter amino acid code instead of one-letter code"));
	}

	protected void printHelp() {
		final String HEADER = new StringBuilder().append("Jannovar Command: annotate\n\n")
				.append("Use this command to annotate a VCF file.\n\n")
				.append("Usage: java -jar de.charite.compbio.jannovar.jar annotate [options] -d <database> -i [<IN.vcf>]+\n\n")
				.toString();
		final String FOOTER = new StringBuilder()
				.append("\n\nExample: java -jar de.charite.compbio.jannovar.jar annotate -d data/hg19_ucsc.ser -i IN.vcf\n\n")
				.toString();

		System.err.print(HEADER);

		HelpFormatter hf = new HelpFormatter();
		PrintWriter pw = new PrintWriter(System.err, true);
		hf.printOptions(pw, 78, options, 2, 2);

		System.err.print(FOOTER);
	}
}
