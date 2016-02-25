package de.charite.compbio.jannovar.cmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.charite.compbio.jannovar.JannovarOptions;

/**
 * Base class for the command line parser for the annotation commands.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */

public abstract class JannovarAnnotationCommandLineParser {
	/** options representation for the Apache commons command line parser */
	protected Options options;
	/** option to catch the help option if set */
	protected Options helpOptions;
	/** the Apache commons command line parser */
	protected DefaultParser parser;

	/**
	 * Calls initializeParser().
	 */
	public JannovarAnnotationCommandLineParser() {
		initializeParser();
	}

	/**
	 * Initialize {@link #parser} and {@link #options}.
	 *
	 * override to add more options to the parser
	 */
	protected void initializeParser() {
		options = new Options();
		helpOptions = new Options();
		Option helpOption = new Option("h", "help", false, "show this help");
		options.addOption(helpOption);
		helpOptions.addOption(helpOption);

		options.addOption(Option.builder("d").longOpt("database").required().hasArg().desc("Jannovar database")
				.argName("database.ser").build());
		
		options.addOption(new Option("v", "verbose", false, "enable verbose output"));
		options.addOption(new Option("vv", "very-verbose", false, "enable very verbose output"));

		parser = new DefaultParser();
	}
	
	protected void printHelpIfOptionIsSet(CommandLine cmd) throws HelpRequestedException {
		if (cmd.hasOption("help")) {
			printHelp();
			throw new HelpRequestedException();
		}
	}

	/**
	 * Parse command line arguments and return {@link JannovarOptions}.
	 *
	 * @param argv
	 *            arguments to parse
	 * @return configuration, as parsed from the argument values
	 * @throws ParseException
	 *             on problems with the arguments
	 */
	public abstract JannovarOptions parse(String argv[]) throws ParseException, HelpRequestedException;
	
	protected abstract void printHelp();
}
