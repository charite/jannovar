package jannovar.cmd.annotate_pos;

import jannovar.JannovarOptions;
import jannovar.cmd.JannovarAnnotationCommandLineParser;
import jannovar.exception.HelpRequestedException;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

/**
 * Parse the command line for the "annotate-position" command.
 *
 * Although public, this class is not meant to be part of the public Jannovar intervace. It can be changed or removed at
 * any point.
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
		result.command = JannovarOptions.Command.ANNOTATE_POSITION;

		if (cmd.hasOption("help")) {
			printHelp();
			throw new HelpRequestedException();
		}

		if (cmd.hasOption("data-file"))
			result.dataFile = cmd.getOptionValue("data-file");
		else
			throw new ParseException("You must specify a data file via -d/--data-file!");

		String args[] = cmd.getArgs(); // get remaining arguments
		if (args.length != 2)
			throw new ParseException("must have one none-option argument, had: " + (args.length - 1));
		result.chromosomalChange = args[1];

		return result;
	}

	public void printHelp() {
		final String HEADER = new StringBuilder().append("Jannovar Command: annotate-pos\n\n")
				.append("Use this command to annotate a chromosomal change.\n\n")
				.append("Usage: java -jar jannovar.jar annotate-pos [options] -D <database> <CHANGE>\n\n").toString();
		final String FOOTER = new StringBuilder().append(
				"\n\nExample: java -jar jannovar.jar annotate -D ucsc_hg19.ser 'chr1:12345C>A'\n\n").toString();

		System.err.print(HEADER);

		HelpFormatter hf = new HelpFormatter();
		PrintWriter pw = new PrintWriter(System.err, true);
		hf.printOptions(pw, 78, options, 2, 2);

		System.err.print(FOOTER);
	}

}
