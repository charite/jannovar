package jannovar.cmd.download;

import jannovar.JannovarOptions;
import jannovar.exception.HelpRequestedException;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;

/**
 * Helper class for parsing the commandline of the download command.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class DownloadCommandLineParser {

	/** options representation for the Apache commons command line parser */
	protected Options options;
	/** the Apache commons command line parser */
	protected Parser parser;

	/**
	 * Calls initializeParser().
	 */
	public DownloadCommandLineParser() {
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
		options.addOption(OptionBuilder
				.withDescription("target folder for downloaded and serialized files, defaults to \"data\"").hasArgs(1)
				.withLongOpt("data-dir").create("d"));
		options.addOption(OptionBuilder.withDescription("proxy to use for download as \"<HOST>:<PORT>\"").hasArgs(1)
				.withLongOpt("proxy").withArgName("proxy").create());

		parser = new GnuParser();
	}

	/**
	 * Parse the command line and
	 *
	 * @throws ParseException
	 *             on problems with the command line
	 */
	public JannovarOptions parse(String argv[]) throws ParseException, HelpRequestedException {
		// Parse the command line.
		CommandLine cmd = parser.parse(options, argv);

		// Fill the resulting JannovarOptions.
		JannovarOptions result = new JannovarOptions();
		result.command = JannovarOptions.Command.DOWNLOAD;

		if (cmd.hasOption("help")) {
			printHelp();
			throw new HelpRequestedException();
		}

		if (cmd.hasOption("data-dir"))
			result.downloadPath = cmd.getOptionValue("data-dir");

		// Get data source names from args.
		String args[] = cmd.getArgs(); // get remaining arguments
		if (args.length <= 1)
			throw new ParseException("You must specify at least one data source name to download from.");
		ImmutableList.Builder<String> dsBuilder = new ImmutableList.Builder<String>();
		for (int i = 1; i < args.length; ++i)
			dsBuilder.add(args[i]);
		result.dataSourceNames = dsBuilder.build();

		// Get data source (INI) file paths.
		ImmutableList.Builder<String> dsfBuilder = new ImmutableList.Builder<String>();
		String[] dataSourceLists = cmd.getOptionValues("data-source-list");
		if (dataSourceLists != null)
			for (int i = 0; i < dataSourceLists.length; ++i)
				dsfBuilder.add(dataSourceLists[i]);
		dsfBuilder.add("bundle:///default_sources.ini");
		result.dataSourceFiles = dsfBuilder.build();

		try {
			if (cmd.hasOption("proxy"))
				result.proxy = HostAndPort.fromString(cmd.getOptionValue("proxy")).withDefaultPort(8080);
		} catch (IllegalArgumentException e) {
			throw new ParseException("could not parse proxy from " + cmd.getOptionValue("proxy"));
		}

		return result;
	}

	private void printHelp() {
		final String HEADER = new StringBuilder().append("Jannovar Command: download\n\n")
				.append("Use this command to download a transcript database and build a serialization file \n")
				.append("of it. This file can then be later loaded by the annotation commands.\n\n")
				.append("Usage: java -jar jannovar.jar download [options] <datasource>+\n\n").toString();
		// TODO(holtgrem): Explain data sources and refer to manual.

		final String FOOTER = new StringBuilder().append("\n\nExample: java -jar jannovar.jar download hg19/ucsc\n\n")
				.append("Note that Jannovar also interprets the environment variables\n")
				.append("HTTP_PROXY, HTTPS_PROXY and FTP_PROXY for downloading files.\n")
				.append("There, you can also specify a username and passsword for the\n").append("proxy.\n\n")
				.toString();

		System.err.print(HEADER);

		HelpFormatter hf = new HelpFormatter();
		PrintWriter pw = new PrintWriter(System.err, true);
		hf.printOptions(pw, 78, options, 2, 2);

		System.err.print(FOOTER);
	}
}
