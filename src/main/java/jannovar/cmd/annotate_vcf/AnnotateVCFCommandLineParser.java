package jannovar.cmd.annotate_vcf;

import jannovar.JannovarOptions;
import jannovar.cmd.JannovarAnnotationCommandLineParser;
import jannovar.exception.HelpRequestedException;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

public class AnnotateVCFCommandLineParser extends JannovarAnnotationCommandLineParser {

	@Override
	public JannovarOptions parse(String[] argv) throws ParseException, HelpRequestedException {
		// Parse the command line.
		CommandLine cmd = parser.parse(options, argv);

		// Fill the resulting JannovarOptions.
		JannovarOptions result = new JannovarOptions();

		if (cmd.hasOption("help")) {
			printHelp();
			throw new HelpRequestedException();
		}

		if (cmd.hasOption("data-file"))
			result.dataFile = cmd.getOptionValue("data-file");
		else
			throw new ParseException("You must specify a data file via -d/--data-file!");

		result.jannovarFormat = cmd.hasOption("janno");

		if (cmd.hasOption("output"))
			result.outVCFFolder = cmd.getOptionValue("output");

		if (cmd.hasOption("vcf"))
			result.vcfFilePath = cmd.getOptionValue("vcf");
		else
			throw new ParseException("You must specify a VCF input vial via -v/--vcf!");

		result.jannovarFormat = cmd.hasOption("jannovar");
		result.showAll = cmd.hasOption("showall");

		String args[] = cmd.getArgs(); // get remaining arguments
		if (args.length != 1)
			throw new ParseException("must have no none-option argument, had: " + (args.length - 1));

		return result;
	}

	@Override
	protected void initializeParser() {
		super.initializeParser();

		options.addOption(new Option("V", "vcf", true, "path to VCF input file"));
		options.addOption(new Option("J", "jannovar", false, "write result in Jannovar output"));
		options.addOption(new Option("a", "showall", false, "report annotations for all affected transcripts"));
	}

	private void printHelp() {
		final String HEADER = new StringBuilder().append("Jannovar Command: annotate\n\n")
				.append("Use this command to annotate a VCF file.\n\n")
				.append("Usage: java -jar jannovar.jar annotate [options] -d <database> -V <IN.VCF>\n\n").toString();
		final String FOOTER = new StringBuilder().append(
				"\n\nExample: java -jar jannovar.jar annotate -d ucsc IN.vcf\n\n").toString();

		System.err.print(HEADER);

		HelpFormatter hf = new HelpFormatter();
		PrintWriter pw = new PrintWriter(System.err, true);
		hf.printOptions(pw, 78, options, 2, 2);

		System.err.print(FOOTER);
	}
}
