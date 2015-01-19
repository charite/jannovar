package de.charite.compbio.jannovar.cmd.download;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

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
		options.addOption(OptionBuilder.withDescription("create verbose output").withLongOpt("verbose").create("v"));
		options.addOption(OptionBuilder.withDescription("create very verbose output").withLongOpt("very-verbose")
				.create("vv"));
		options.addOption(OptionBuilder.withDescription("INI file with data source list").hasArgs(1)
				.withLongOpt("data-source-list").create("s"));
		options.addOption(OptionBuilder
				.withDescription("target folder for downloaded and serialized files, defaults to \"data\"").hasArgs(1)
				.withLongOpt("data-dir").create("d"));
		options.addOption(OptionBuilder
				.withDescription(
						"proxy to use for HTTP/HTTPS/FTP downloads (lower precedence than "
								+ "the other proxy options)").hasArgs(1).withLongOpt("proxy").withArgName("proxy")
								.create());
		options.addOption(OptionBuilder
				.withDescription("proxy to use for HTTP downloads as \"<PROTOCOL>://<HOST>[:<PORT>]\"").hasArgs(1)
				.withLongOpt("http-proxy").withArgName("http-proxy").create());
		options.addOption(OptionBuilder
				.withDescription("proxy to use for HTTPS downloads as \"<PROTOCOL>://<HOST>[:<PORT>]\"").hasArgs(1)
				.withLongOpt("https-proxy").withArgName("https-proxy").create());
		options.addOption(OptionBuilder
				.withDescription("proxy to use for FTP downloads as \"<PROTOCOL>://<HOST>[:<PORT>]\"").hasArgs(1)
				.withLongOpt("ftp-proxy").withArgName("ftp-proxy").create());

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
		result.command = JannovarOptions.Command.DOWNLOAD;

		if (cmd.hasOption("help")) {
			printHelp();
			throw new HelpRequestedException();
		}

		if (cmd.hasOption("verbose"))
			result.verbosity = 2;
		if (cmd.hasOption("very-verbose"))
			result.verbosity = 3;

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

		// get proxy settings from system environment if possible
		Map<String, String> env = System.getenv();
		if (getProxyURL(env.get("HTTP_PROXY")) != null)
			result.httpProxy = getProxyURL(env.get("HTTP_PROXY"));
		if (getProxyURL(env.get("http_proxy")) != null)
			result.httpProxy = getProxyURL(env.get("http_proxy"));
		if (getProxyURL(env.get("HTTPS_PROXY")) != null)
			result.httpsProxy = getProxyURL(env.get("HTTPS_PROXY"));
		if (getProxyURL(env.get("https_proxy")) != null)
			result.httpsProxy = getProxyURL(env.get("https_proxy"));
		if (getProxyURL(env.get("FTP_PROXY")) != null)
			result.ftpProxy = getProxyURL(env.get("FTP_PROXY"));
		if (getProxyURL(env.get("ftp_proxy")) != null)
			result.ftpProxy = getProxyURL(env.get("ftp_proxy"));

		// get proxy settings from the command line (--proxy), can be overwritten below
		if (cmd.hasOption("proxy")) {
			result.httpProxy = getProxyURL(cmd.getOptionValue("proxy"));
			result.httpsProxy = getProxyURL(cmd.getOptionValue("proxy"));
			result.ftpProxy = getProxyURL(cmd.getOptionValue("proxy"));
		}

		// get proxy settings from the command line, overriding the environment settings
		if (cmd.hasOption("http-proxy"))
			result.httpProxy = getProxyURL(cmd.getOptionValue("http-proxy"));
		if (cmd.hasOption("https-proxy"))
			result.httpsProxy = getProxyURL(cmd.getOptionValue("https-proxy"));
		if (cmd.hasOption("ftp-proxy"))
			result.ftpProxy = getProxyURL(cmd.getOptionValue("ftp-proxy"));

		return result;
	}

	/**
	 * Build {@link URL} from an environment proxy configuration
	 *
	 * @param envValue
	 *            environment value with proxy host and port as URL
	 * @return {@link URL} with configuration from <code>envValue</code> or <code>null</code> if not set or not
	 *         successful
	 */
	private URL getProxyURL(String envValue) {
		if (envValue == null)
			return null;

		try {
			return new URL(envValue);
		} catch (MalformedURLException e) {
			System.err.println("WARNING: Could not parse proxy value " + envValue + " as URL.");
			return null;
		}
	}

	private void printHelp() {
		final String HEADER = new StringBuilder().append("Jannovar Command: download\n\n")
				.append("Use this command to download a transcript database and build a serialization file \n")
				.append("of it. This file can then be later loaded by the annotation commands.\n\n")
				.append("Usage: java -jar de.charite.compbio.jannovar.jar download [options] <datasource>+\n\n").toString();
		// TODO(holtgrem): Explain data sources and refer to manual.

		final String FOOTER = new StringBuilder().append("\n\nExample: java -jar de.charite.compbio.jannovar.jar download hg19/ucsc\n\n")
				.append("Note that Jannovar also interprets the environment variables\n")
				.append("HTTP_PROXY, HTTPS_PROXY and FTP_PROXY for downloading files.\n").toString();

		System.err.print(HEADER);

		HelpFormatter hf = new HelpFormatter();
		PrintWriter pw = new PrintWriter(System.err, true);
		hf.printOptions(pw, 78, options, 2, 2);

		System.err.print(FOOTER);
	}

}
