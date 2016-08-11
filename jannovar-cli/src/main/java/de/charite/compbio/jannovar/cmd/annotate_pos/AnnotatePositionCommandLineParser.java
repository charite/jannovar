package de.charite.compbio.jannovar.cmd.annotate_pos;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationCommandLineParser;

/**
 * Parse the command line for the "annotate-position" command.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class AnnotatePositionCommandLineParser extends JannovarAnnotationCommandLineParser {

	@Override
	public JannovarOptions parse(String[] argv) throws ParseException, HelpRequestedException {
		// Parse the command line.
		CommandLine cmd = parser.parse(helpOptions, argv, true);
		printHelpIfOptionIsSet(cmd);
		cmd = parser.parse(options, argv);

		printHelpIfOptionIsSet(cmd);
		// Fill the resulting JannovarOptions.
		JannovarOptions result = new JannovarOptions();
		result.printProgressBars = true;
		result.command = JannovarOptions.Command.ANNOTATE_POSITION;

		if (cmd.hasOption("verbose"))
			result.verbosity = 2;
		if (cmd.hasOption("very-verbose"))
			result.verbosity = 3;

		result.dataFile = cmd.getOptionValue("database");
		result.useThreeLetterAminoAcidCode = cmd.hasOption("three-letter-amino-acid-code");

		for (String change : cmd.getOptionValues("change")) {
			result.chromosomalChanges.add(change);
		}

		return result;
	}

	@Override
	protected void initializeParser() {
		super.initializeParser();

		options.addOption(
				Option.builder("c").longOpt("change").required().hasArgs().desc("Genomic variant change").build());

		options.addOption(new Option(null, "three-letter-amino-acid-code", false,
				"use three-letter amino acid code instead of one-letter code"));
	}

	public void printHelp() {
		final String HEADER = new StringBuilder().append("Jannovar Command: annotate-pos\n\n")
				.append("Use this command to annotate a chromosomal change.\n\n")
				.append("Usage: java -jar de.charite.compbio.jannovar.jar annotate-pos [options] -d <database.ser> -c <CHANGE>\n\n")
				.toString();
		final String FOOTER = new StringBuilder()
				.append("\n\nExample: java -jar de.charite.compbio.jannovar.jar annotate-pos -d data/hg19_ucsc.ser -c 'chr1:12345C>A'\n\n")
				.toString();

		System.err.print(HEADER);

		HelpFormatter hf = new HelpFormatter();
		PrintWriter pw = new PrintWriter(System.err, true);
		hf.printOptions(pw, 78, options, 2, 2);

		System.err.print(FOOTER);
	}

}
