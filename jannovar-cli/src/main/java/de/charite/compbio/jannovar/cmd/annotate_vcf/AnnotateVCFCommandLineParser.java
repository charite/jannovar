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

		result.jannovarFormat = cmd.hasOption("jannovar");
		result.showAll = cmd.hasOption("showall");

		result.writeJannovarInfoFields = cmd.hasOption("old-info-fields");
		result.writeVCFAnnotationStandardInfoFields = !cmd.hasOption("no-new-info-field");
		result.escapeAnnField = !cmd.hasOption("no-escape-ann-field");
		result.nt3PrimeShifting = !cmd.hasOption("no-3-prime-shifting");
		if (cmd.hasOption("output-infix"))
			result.outputInfix = cmd.getOptionValue("output-infix");

		result.dataFile = cmd.getOptionValue("database");

		for (String vcfPath : cmd.getOptionValues("vcf-in")) {
			result.vcfFilePaths.add(vcfPath);
		}

		return result;
	}

	@Override
	protected void initializeParser() {
		super.initializeParser();

		options.addOption(new Option("J", "jannovar", false, "write result in Jannovar output"));
		options.addOption(new Option("a", "showall", false,
				"report annotations for all affected transcripts (by default only one "
						+ "with the highest impact is shown for each alternative allele)"));
		options.addOption(
				new Option("o", "output-dir", true, "output directory (default is to write parallel to input file)"));

		options.addOption(new Option(null, "old-info-fields", false,
				"write out old Jannovar VCF INFO fields \"EFFECT\" and \"HGVS\" (default is off)"));
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
