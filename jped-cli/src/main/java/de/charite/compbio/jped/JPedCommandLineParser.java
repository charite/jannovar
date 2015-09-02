package de.charite.compbio.jped;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.pedigree.ModeOfInheritance;

/**
 * Command line parser for the jped-cli application for the pedigree-based filtration of VCF.
 */
public class JPedCommandLineParser {

	/** options representation for the Apache commons command line parser */
	protected Options options;
	/** the Apache commons command line parser */
	protected CommandLineParser parser;

	/**
	 * Calls initializeParser().
	 */
	public JPedCommandLineParser() {
		initializeParser();
	}

	public JPedOptions parse(String[] argv) throws ParseException, HelpRequestedException {
		// Parse the command line.
		CommandLine cmd = parser.parse(options, argv);

		// Fill the resulting JannovarOptions.
		JPedOptions result = new JPedOptions();

		if (cmd.hasOption("help")) {
			printHelp();
			throw new HelpRequestedException();
		}

		if (cmd.hasOption("verbose"))
			result.verbosity = 2;
		if (cmd.hasOption("very-verbose"))
			result.verbosity = 3;

		String args[] = cmd.getArgs(); // get remaining arguments
		if (args.length != 4)
			throw new ParseException("must have exactly four none-option arguments, had: " + args.length);

		ImmutableSet.Builder<ModeOfInheritance> inheritanceBuilder = new ImmutableSet.Builder<ModeOfInheritance>();
		for (String modeString : cmd.getOptionValues("inheritance-mode")) {
			ModeOfInheritance mode = ModeOfInheritance.valueOf(ModeOfInheritance.class, modeString);
			inheritanceBuilder.add(mode);
		}
		ImmutableSet<ModeOfInheritance> inheritances = inheritanceBuilder.build();
		if (!inheritances.isEmpty())
			result.modeOfInheritances = inheritances;

		result.geneWise = cmd.hasOption("gene-wise");
		if (cmd.getOptionValue("database") != null)
			result.jannovarDB = cmd.getOptionValue("database");

		result.jannovarDB = args[0];
		result.pedPath = args[1];
		result.inputPath = args[2];
		result.outputPath = args[3];

		return result;
	}

	protected void initializeParser() {
		options = new Options();
		options.addOption(new Option("h", "help", false, "show this help"));
		options.addOption(new Option("v", "verbose", false, "enable verbose output"));
		options.addOption(new Option("vv", "very-verbose", false, "enable very verbose output"));

		Option.builder("m").hasArgs().desc("Check for the given inheritance mode(s) (can be multiple)")
				.longOpt("inheritance-mode").build();
		options.addOption(new Option("g", "gene-wise", false,
				"gene-wise instead of variant-wise processing (required for compound heterozygous filtration)"));

		parser = new DefaultParser();
	}

	public void printHelp() {
		final String HEADER = new StringBuilder().append("Jannovar Filter Tool")
				.append("Use this command to filter VCF files.\n\n")
				.append("Usage: java -jar jped-cli.jar <DB.ser> <PED.ped> <IN.vcf> <OUT.vcf>\n\n").toString();
		final String FOOTER = new StringBuilder()
				.append("\n\nExample: java -jar jped-cli.jar -m AUTOSOMAL_DOMINANT data/hg19_ucsc.ser fam.ped 123.vcf 123.filtered.vcf\n")
				.append("         java -jar jped-cli.jar -g -m AUTOSOMAL_RECESSIVE data/hg19_ucsc.ser fam.ped 123.vcf 123.filtered.vcf\n\n")
				.append("Diseases\n\n")
				.append("The --inheritance-mode parameter can take one of the following values. When given")
				.append("then the variants will be filtered to those being compatible with the given mode")
				.append("of inheritance.\n\n").append("  AUTOSOMAL_DOMINANT\n").append("  AUTOSOMAL_RECESSIVE\n")
				.append("  X_RECESSIVE\n").append("  X_DOMINANT\n").append("  UNINITIALIZED (no filtration)\n")
				.toString();
		System.err.print(HEADER);

		HelpFormatter hf = new HelpFormatter();
		PrintWriter pw = new PrintWriter(System.err, true);
		hf.printOptions(pw, 78, options, 2, 2);

		System.err.print(FOOTER);
	}
}
