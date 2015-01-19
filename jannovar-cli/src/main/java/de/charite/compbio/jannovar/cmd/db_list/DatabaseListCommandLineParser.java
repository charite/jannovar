package de.charite.compbio.jannovar.cmd.db_list;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;

public class DatabaseListCommandLineParser {

	/** options representation for the Apache commons command line parser */
	protected Options options;
	/** the Apache commons command line parser */
	protected Parser parser;

	/**
	 * Calls initializeParser().
	 */
	public DatabaseListCommandLineParser() {
		initializeParser();
	}

	/**
	 * Initialize {@link #parser} and {@link #options}.
	 */
	@SuppressWarnings("static-access")
	// OptionBuilder causes this warning.
	private void initializeParser() {
		options = new Options();
		options.addOption(OptionBuilder.withDescription("show this help").withLongOpt("help").create("h"));
		options.addOption(OptionBuilder.withDescription("INI file with data source list").hasArgs(1)
				.withLongOpt("data-source-list").create("s"));

		parser = new GnuParser();
	}

	/**
	 * Parse the command line and
	 *
	 * @throws ParseException
	 *             on problems with the command line
	 * @throws HelpRequestedException
	 *             if the user requested help on the command line
	 */
	public JannovarOptions parse(String argv[]) throws ParseException, HelpRequestedException {
		// Parse the command line.
		CommandLine cmd = parser.parse(options, argv);

		// Fill the resulting JannovarOptions.
		JannovarOptions result = new JannovarOptions();
		result.printProgressBars = true;
		result.command = JannovarOptions.Command.DB_LIST;

		if (cmd.hasOption("verbose"))
			result.verbosity = 2;
		if (cmd.hasOption("very-verbose"))
			result.verbosity = 3;

		if (cmd.hasOption("help")) {
			printHelp();
			throw new HelpRequestedException();
		}

		// Get data source (INI) file paths.
		ImmutableList.Builder<String> dsfBuilder = new ImmutableList.Builder<String>();
		String[] dataSourceLists = cmd.getOptionValues("data-source-list");
		if (dataSourceLists != null)
			for (int i = 0; i < dataSourceLists.length; ++i)
				dsfBuilder.add(dataSourceLists[i]);
		dsfBuilder.add("bundle:///default_sources.ini");
		result.dataSourceFiles = dsfBuilder.build();

		return result;
	}


	private void printHelp() {
		final String HEADER = new StringBuilder().append("Jannovar Command: db-list\n\n")
				.append("Use this command to list known downloadable database names\n\n")
				.append("Usage: java -jar de.charite.compbio.jannovar.jar db-list [options]\n\n").toString();

		System.err.print(HEADER);

		HelpFormatter hf = new HelpFormatter();
		PrintWriter pw = new PrintWriter(System.err, true);
		hf.printOptions(pw, 78, options, 2, 2);
	}

}
