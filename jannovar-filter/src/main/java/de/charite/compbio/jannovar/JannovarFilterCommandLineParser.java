package de.charite.compbio.jannovar;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;

import de.charite.compbio.jannovar.pedigree.ModeOfInheritance;

/**
 * Command line parser for the jannovar-filter app.
 */
public class JannovarFilterCommandLineParser {
	/** options representation for the Apache commons command line parser */
	protected Options options;
	/** the Apache commons command line parser */
	protected Parser parser;

	/**
	 * Calls initializeParser().
	 */
	public JannovarFilterCommandLineParser() {
		initializeParser();
	}

	public JannovarFilterOptions parse(String[] argv) throws ParseException, HelpRequestedException {
		// Parse the command line.
		CommandLine cmd = parser.parse(options, argv);

		// Fill the resulting JannovarOptions.
		JannovarFilterOptions result = new JannovarFilterOptions();

		if (cmd.hasOption("help")) {
			printHelp();
			throw new HelpRequestedException();
		}

		if (cmd.hasOption("verbose"))
			result.verbosity = 2;
		if (cmd.hasOption("very-verbose"))
			result.verbosity = 3;

		String args[] = cmd.getArgs(); // get remaining arguments
		if (args.length != 2)
			throw new ParseException("must exactly two none-option arguments, had: " + args.length);

		if (cmd.getOptionValue("inheritance-mode") != null)
			result.inheritanceMode = ModeOfInheritance.valueOf(ModeOfInheritance.class,
					cmd.getOptionValue("inheritance-mode"));
		result.inputPath = args[0];
		result.outputPath = args[1];

		return result;
	}

	protected void initializeParser() {
		options = new Options();
		options.addOption(new Option("h", "help", false, "show this help"));
		options.addOption(new Option("v", "verbose", false, "enable verbose output"));
		options.addOption(new Option("vv", "very-verbose", false, "enable very verbose output"));

		parser = new GnuParser();
	}

	private void printHelp() {
		final String HEADER = new StringBuilder().append("Jannovar Filter Tool")
				.append("Use this command to filter VCF files.\n\n")
				.append("Usage: java -jar jannovar-filter.jar <IN.vcf> <OUT.vcf>\n\n").toString();
		final String FOOTER = new StringBuilder()
		.append("\n\nExample: java -jar jannovar-filter.jar 123.vcf 123.filtered.vcf\n\n")
		.append("Diseases\n\n")
		.append("The --inheritance-mode parameter can take one of the following values. When given")
		.append("then the variants will be filtered to those being compatible with the given mode")
		.append("of inheritance.\n\n").toString();

		System.err.print(HEADER);

		HelpFormatter hf = new HelpFormatter();
		PrintWriter pw = new PrintWriter(System.err, true);
		hf.printOptions(pw, 78, options, 2, 2);

		System.err.print(FOOTER);
	}
}
