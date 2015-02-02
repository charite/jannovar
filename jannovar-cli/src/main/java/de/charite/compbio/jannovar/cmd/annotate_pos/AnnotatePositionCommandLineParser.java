package de.charite.compbio.jannovar.cmd.annotate_pos;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationCommandLineParser;

/**
 * Parse the command line for the "annotate-position" command.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class AnnotatePositionCommandLineParser extends JannovarAnnotationCommandLineParser {

	@Override
	public JannovarOptions parse(String[] argv) throws ParseException, HelpRequestedException {
		// Parse the command line.
		CommandLine cmd = parser.parse(options, argv);

		// Fill the resulting JannovarOptions.
		JannovarOptions result = new JannovarOptions();
		result.printProgressBars = true;
		result.command = JannovarOptions.Command.ANNOTATE_POSITION;

		if (cmd.hasOption("help")) {
			printHelp();
			throw new HelpRequestedException();
		}

		if (cmd.hasOption("verbose"))
			result.verbosity = 2;
		if (cmd.hasOption("very-verbose"))
			result.verbosity = 3;

		String args[] = cmd.getArgs(); // get remaining arguments
		if (args.length < 3)
			throw new ParseException("must have at least two none-option argument, had: " + (args.length - 1));

		result.dataFile = args[1];

		for (int i = 2; i < args.length; ++i)
			result.chromosomalChanges.add(args[i]);

		return result;
	}

	public void printHelp() {
		final String HEADER = new StringBuilder().append("Jannovar Command: annotate-pos\n\n")
				.append("Use this command to annotate a chromosomal change.\n\n")
				.append("Usage: java -jar de.charite.compbio.jannovar.jar annotate-pos [options] <database.ser> <CHANGE>\n\n").toString();
		final String FOOTER = new StringBuilder().append(
				"\n\nExample: java -jar de.charite.compbio.jannovar.jar annotate-pos data/hg19_ucsc.ser 'chr1:12345C>A'\n\n").toString();

		System.err.print(HEADER);

		HelpFormatter hf = new HelpFormatter();
		PrintWriter pw = new PrintWriter(System.err, true);
		hf.printOptions(pw, 78, options, 2, 2);

		System.err.print(FOOTER);
	}

}
