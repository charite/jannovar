package de.charite.compbio.jannovar.cmd.download;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;

/**
 * Helper class for parsing the commandline of the download command.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */

public final class DownloadCommandLineParser {

	/** options representation for the Apache commons command line parser */
	protected Options options;
	protected Options helpOptions;
	/** the Apache commons command line parser */
	protected DefaultParser parser;

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
		helpOptions = new Options();

		Option helpOption = Option.builder("h").desc("show this help").longOpt("help").build();
		helpOptions.addOption(helpOption);

		options.addOption(helpOption);
		options.addOption(Option.builder("v").desc("create verbose output").longOpt("verbose").build());
		options.addOption(Option.builder("vv").desc("create very verbose output").longOpt("very-verbose").build());
		options.addOption(Option.builder("s").desc("INI file with data source list").numberOfArgs(1)
				.longOpt("data-source-list").build());
		options.addOption(
				Option.builder("dir").desc("target folder for downloaded and serialized files, defaults to \"data\"")
						.numberOfArgs(1).longOpt("data-dir").build());
		options.addOption(
				Option.builder()
						.desc("proxy to use for HTTP/HTTPS/FTP downloads (lower precedence than "
								+ "the other proxy options)")
						.numberOfArgs(1).longOpt("proxy").argName("proxy").build());
		options.addOption(Option.builder().desc("proxy to use for HTTP downloads as \"<PROTOCOL>://<HOST>[:<PORT>]\"")
				.numberOfArgs(1).longOpt("http-proxy").argName("http-proxy").build());
		options.addOption(Option.builder().desc("proxy to use for HTTPS downloads as \"<PROTOCOL>://<HOST>[:<PORT>]\"")
				.numberOfArgs(1).longOpt("https-proxy").argName("https-proxy").build());
		options.addOption(Option.builder().desc("proxy to use for FTP downloads as \"<PROTOCOL>://<HOST>[:<PORT>]\"")
				.numberOfArgs(1).longOpt("ftp-proxy").argName("ftp-proxy").build());
		options.addOption(Option.builder("d").desc("Datasource for the transcipt database").hasArgs().required()
				.longOpt("datasource").hasArgs().argName("datasource").build());

		parser = new DefaultParser();
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
		//Parse the help
		CommandLine cmd = parser.parse(helpOptions, argv, true);
		printHelpIfOptionIsSet(cmd);
		// Parse the command line.
		cmd = parser.parse(options, argv);
		printHelpIfOptionIsSet(cmd);

		// Fill the resulting JannovarOptions.
		JannovarOptions result = new JannovarOptions();
		result.printProgressBars = true;
		result.command = JannovarOptions.Command.DOWNLOAD;

		if (cmd.hasOption("verbose"))
			result.verbosity = 2;
		if (cmd.hasOption("very-verbose"))
			result.verbosity = 3;

		if (cmd.hasOption("data-dir"))
			result.downloadPath = cmd.getOptionValue("data-dir");

		// Get data source names from args.
		ImmutableList.Builder<String> dsBuilder = new ImmutableList.Builder<String>();
		for (String datasource : cmd.getOptionValues("datasource")) {
			dsBuilder.add(datasource);
		}
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
	
	private void printHelpIfOptionIsSet(CommandLine cmd) throws HelpRequestedException {
		if (cmd.hasOption("help")) {
			printHelp();
			throw new HelpRequestedException();
		}
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
				.append("Usage: java -jar de.charite.compbio.jannovar.jar download [options] -d <datasource>+\n\n")
				.toString();
		// TODO(holtgrem): Explain data sources and refer to manual.

		final String FOOTER = new StringBuilder()
				.append("\n\nExample: java -jar de.charite.compbio.jannovar.jar download -d hg19/ucsc\n\n")
				.append("Note that Jannovar also interprets the environment variables\n")
				.append("HTTP_PROXY, HTTPS_PROXY and FTP_PROXY for downloading files.\n").toString();

		System.err.print(HEADER);

		HelpFormatter hf = new HelpFormatter();
		PrintWriter pw = new PrintWriter(System.err, true);
		hf.printOptions(pw, 78, options, 2, 2);

		System.err.print(FOOTER);
	}

}
