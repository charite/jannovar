package jannovar.cmd;

import jannovar.JannovarOptions;
import jannovar.exception.HelpRequestedException;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;

/**
 * Base class for the command line parser for the annotation commands.
 *
 * Although public, this class is not meant to be part of the public Jannovar intervace. It can be changed or removed at
 * any point.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public abstract class JannovarAnnotationCommandLineParser {
	/** options representation for the Apache commons command line parser */
	protected Options options;
	/** the Apache commons command line parser */
	protected Parser parser;

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
		options.addOption(new Option("h", "help", false, "show this help"));
		options.addOption(new Option("D", "data-file", true, "path to serialized data file"));

		parser = new GnuParser();
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
}
