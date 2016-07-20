package de.charite.compbio.jannovar.cmd.download;

import java.util.Map;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;
import de.charite.compbio.jannovar.cmd.JannovarCommand;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.datasource.DataSourceFactory;
import de.charite.compbio.jannovar.datasource.DatasourceOptions;
import de.charite.compbio.jannovar.impl.util.PathUtil;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

/**
 * Implementation of download step in Jannovar.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class DownloadCommand extends JannovarCommand {

	public DownloadCommand(String[] argv) throws CommandLineParsingException, HelpRequestedException {
		super(argv);
	}

	/**
	 * Perform the downloading.
	 */
	@Override
	public void run() throws JannovarException {
		System.err.println("Options");
		options.print(System.err);

		DatasourceOptions dsOptions = new DatasourceOptions(options.httpProxy, options.httpsProxy, options.ftpProxy,
				options.printProgressBars);

		DataSourceFactory factory = new DataSourceFactory(dsOptions, options.dataSourceFiles);
		for (String name : options.dataSourceNames) {
			System.err.println("Downloading/parsing for data source \"" + name + "\"");
			JannovarData data = factory.getDataSource(name).getDataFactory().build(options.downloadPath,
					options.printProgressBars);
			String filename = PathUtil.join(options.downloadPath, name.replace('/', '_').replace('\\', '_') + ".ser");
			JannovarDataSerializer serializer = new JannovarDataSerializer(filename);
			serializer.save(data);
		}
	}

	@Override
	protected JannovarOptions parseCommandLine(String[] argv)
			throws CommandLineParsingException, HelpRequestedException {
		try {
			return new DownloadCommandLineParser().parse(argv);
		} catch (ParseException e) {
			throw new CommandLineParsingException("Could not parse the command line.", e);
		}
	}

	/**
	 * Add sub parser for download command in <code>parser</code>
	 *
	 * @param subparsers
	 *            {@link ArgumentParser} to add sub command to
	 */
	public static void addSubparser(Subparsers subparsers) {
		Subparser parser = subparsers.addParser("download").help("Download database");

		// Path to INI file with source list
		parser.addArgument("--data-source-list").type(String.class).setDefault("bundle:///default_sources.ini")
				.help("Path to INI file with data source list");
		parser.addArgument("--data-dir").type(String.class).setDefault("data")
				.help("target folder for downloaded and serialized files, defaults to \"data\"");

		// Required argument: name of data source to download
		parser.addArgument("-d", "--datasource").required(true).type(String.class).required(true)
				.help("Name of the data source to download, get with 'db-list' command");

		// Get proxy settings from system environment if possible
		Map<String, String> env = System.getenv();
		String httpProxy = null;
		if (env.get("HTTP_PROXY") != null)
			httpProxy = env.get("HTTP_PROXY");
		if (env.get("http_proxy") != null)
			httpProxy = env.get("http_proxy");
		String httpsProxy = null;
		if (env.get("HTTPS_PROXY") != null)
			httpsProxy = env.get("HTTPS_PROXY");
		if (env.get("https_proxy") != null)
			httpsProxy = env.get("https_proxy");
		String ftpProxy = null;
		if (env.get("FTP_PROXY") != null)
			ftpProxy = env.get("FTP_PROXY");
		if (env.get("ftp_proxy") != null)
			ftpProxy = env.get("ftp_proxy");

		// Proxy configuration
		ArgumentGroup proxyAG = parser.addArgumentGroup("Proxy settings");
		proxyAG.description("Proxy settings, should have the format <proto>://<host>[:port]/");
		proxyAG.addArgument("--proxy").type(String.class).help("Overall proxy, overridden by other more specific ones");
		proxyAG.addArgument("--http-proxy").setDefault(httpProxy).type(String.class).help("HTTP proxy");
		proxyAG.addArgument("--https-proxy").setDefault(httpsProxy).type(String.class).help("HTTPS proxy");
		proxyAG.addArgument("--ftp-proxy").setDefault(ftpProxy).type(String.class).help("FTP proxy to use");

		parser.description("Download database from ENSEMBL/UCSC/RefSeq etc. By default, the environment variables "
				+ "http_proxy, https_proxy, ftp_proxy and the upper case variants are used if set.");

		parser.epilog("Use this command to download a transcript database, e.g., using '-d hg19/refseq'");
	}

}
