package de.charite.compbio.jannovar.datasource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.datasource.FileDownloader.ProxyOptions;
import de.charite.compbio.jannovar.impl.parse.ReferenceDictParser;
import de.charite.compbio.jannovar.impl.parse.TranscriptParseException;
import de.charite.compbio.jannovar.impl.util.PathUtil;
import de.charite.compbio.jannovar.io.JannovarData;
import de.charite.compbio.jannovar.io.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Interface for data factories, allowing to create {@link JannovarData} objects from {@link DataSource}s.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public abstract class JannovarDataFactory {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(JannovarDataFactory.class);

	/** the {@link JannovarOptions} to use for proxy settings */
	protected final JannovarOptions options;
	/** the {@link DataSource} to use */
	protected final DataSource dataSource;
	/** configuration section from INI file */
	protected final Section iniSection;

	/**
	 * Construct the factory with the given {@link DataSource}.
	 *
	 * @param options
	 *            configuration for proxy settings
	 * @param dataSource
	 *            the data source to use.
	 * @param iniSection
	 *            {@link Section} with configuration from INI file
	 */
	public JannovarDataFactory(JannovarOptions options, DataSource dataSource, Section iniSection) {
		this.options = options;
		this.dataSource = dataSource;
		this.iniSection = iniSection;
	}

	/**
	 * @param downloadDir
	 *            path of directory to download files to
	 * @param printProgressBars
	 *            whether or not to print progress bars
	 * @return {@link JannovarData} object for the factory's state.
	 * @throws InvalidDataSourceException
	 *             on problems with the data source or data source file
	 * @throws TranscriptParseException
	 *             on problems with processing the transcript and reference dictionary data
	 * @throws FileDownloadException
	 *             on problems while downloading files.
	 */
	public final JannovarData build(String downloadDir, boolean printProgressBars) throws InvalidDataSourceException,
	TranscriptParseException,
	FileDownloadException {
		String targetDir = PathUtil.join(downloadDir, dataSource.getName());

		FileDownloader downloader = new FileDownloader(buildOptions(printProgressBars));

		// Download files.
		LOGGER.info("Downloading data...");
		try {
			for (String url : dataSource.getDownloadURLs()) {
				LOGGER.info("Downloading {}", url);
				URL src = new URL(url);
				String fileName = new File(src.getPath()).getName();
				File dest = new File(PathUtil.join(targetDir, fileName));
				downloader.copyURLToFile(src, dest);
			}
		} catch (MalformedURLException e) {
			throw new FileDownloadException("Invalid URL.", e);
		}

		// Parse files for building ReferenceDictionary objects.
		LOGGER.info("Building ReferenceDictionary...");
		final String chromInfoPath = PathUtil.join(downloadDir, dataSource.getName(),
				dataSource.getFileName("chromInfo"));
		final String chrToAccessionsPath = PathUtil.join(downloadDir, dataSource.getName(),
				dataSource.getFileName("chrToAccessions"));
		ReferenceDictParser dictParser = new ReferenceDictParser(chromInfoPath, chrToAccessionsPath, iniSection);
		ReferenceDictionary refDict = dictParser.parse();
		// refDict.print(System.err);

		// Parse transcript files.
		LOGGER.info("Parsing transcripts...");
		ImmutableList<TranscriptModel> transcripts = parseTranscripts(refDict, targetDir);

		return new JannovarData(refDict, transcripts);
	}

	/**
	 * Build {@link FileDownloader.ProxyOptions} from an environment proxy configuration
	 *
	 * @param envValue
	 *            environment value with proxy host and port as URL
	 * @return {@link FileDownloader.ProxyOptions} with configuration from <code>envValue</code.
	 */
	private FileDownloader.ProxyOptions buildProxyOptions(String envValue) {
		FileDownloader.ProxyOptions result = new FileDownloader.ProxyOptions();
		if (envValue == null)
			return result;

		try {
			URL url = new URL(envValue);
			result.host = url.getHost();
			if (url.getPort() != -1)
				result.port = url.getPort();
			String userInfo = url.getUserInfo();
			if (userInfo != null) {
				if (userInfo.contains(":")) {
					String[] tokens = userInfo.split(":");
					result.user = tokens[0];
					result.password = tokens[1];
				} else {
					result.user = userInfo;
				}
			}
		} catch (MalformedURLException e) {
			LOGGER.warn("Could not parse HTTP_PROXY value {} as URL.", envValue);
		}
		return result;
	}

	/**
	 * @return {@link FileDownloader.Options} with proxy settings from {@link #options} and environment.
	 */
	private FileDownloader.Options buildOptions(boolean printProgressBars) {
		FileDownloader.Options result = new FileDownloader.Options();

		// Get proxy settings from options.
		result.printProgressBar = printProgressBars;
		updateProxyOptions(result.http, options.httpProxy);
		updateProxyOptions(result.https, options.httpsProxy);
		updateProxyOptions(result.ftp, options.ftpProxy);

		return result;
	}

	private void updateProxyOptions(ProxyOptions proxyOptions, URL url) {
		if (url != null && url.getHost() != null && !url.getHost().equals("")) {
			proxyOptions.host = url.getHost();
			proxyOptions.port = url.getPort();
			if (proxyOptions.port == -1)
				proxyOptions.port = 80;
			String userInfo = url.getUserInfo();
			if (userInfo != null && userInfo.indexOf(':') != -1) {
				String[] userPass = userInfo.split(":", 2);
				proxyOptions.user = userPass[0];
				proxyOptions.password = userPass[1];
			}
		}
	}

	/**
	 * @param refDict
	 *            {@link ReferenceDictionary} to use
	 * @param targetDir
	 *            path where the downloaded files are
	 * @return list of {@link TranscriptInfo} objects that are parsed from the files in <code>targetDir</code>
	 * @throws TranscriptParseException
	 *             on problems with parsing the transcript database
	 */
	protected abstract ImmutableList<TranscriptModel> parseTranscripts(ReferenceDictionary refDict, String targetDir)
			throws TranscriptParseException;

}
