package jannovar.cmd.download;

import jannovar.JannovarOptions;
import jannovar.exception.HelpRequestedException;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
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
	private void initializeParser() {
		options = new Options();
		options.addOption(new Option("h", "help", false, "show this help"));
		options.addOption(new Option("d", "data-dir", true,
				"target folder for downloaded and serialized files, defaults to \"data\""));
		options.addOption(new Option(null, "proxy", true, "proxy to use for download as <HOST>:<PORT>"));

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
		// TODO(holtgrem): Allow specifying more files here.
		ImmutableList.Builder<String> dsfBuilder = new ImmutableList.Builder<String>();
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
				.toString();

		System.err.print(HEADER);

		HelpFormatter hf = new HelpFormatter();
		PrintWriter pw = new PrintWriter(System.err, true);
		hf.printOptions(pw, 78, options, 2, 2);

		System.err.print(FOOTER);
	}
}
