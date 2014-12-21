package jannovar.datasource;

import jannovar.JannovarOptions;
import jannovar.exception.FileDownloadException;
import jannovar.exception.InvalidDataSourceException;
import jannovar.exception.TranscriptParseException;
import jannovar.impl.parse.ReferenceDictParser;
import jannovar.io.JannovarData;
import jannovar.io.ReferenceDictionary;
import jannovar.reference.TranscriptInfo;
import jannovar.util.PathUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

/**
 * Interface for data factories, allowing to create {@link JannovarData} objects from {@link DataSource}s.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public abstract class JannovarDataFactory {

	/** the {@link JannovarOptions} to use for proxy settings */
	private final JannovarOptions options;
	/** the {@link DataSource} to use */
	private final DataSource dataSource;
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
	 * @return {@link JannovarData} object for the factory's state.
	 * @throws InvalidDataSourceException
	 *             on problems with the data source or data source file
	 * @throws TranscriptParseException
	 *             on problems with processing the transcript and reference dictionary data
	 * @throws FileDownloadException
	 *             on problems while downloading files.
	 */
	public final JannovarData build(String downloadDir) throws InvalidDataSourceException, TranscriptParseException,
			FileDownloadException {
		String targetDir = PathUtil.join(downloadDir, dataSource.getName());

		FileDownloader downloader = new FileDownloader(buildOptions());

		// Download files.
		System.err.println("Downloading data...");
		try {
			for (String url : dataSource.getDownloadURLs()) {
				System.err.println("Downloading " + url);
				URL src = new URL(url);
				String fileName = new File(src.getPath()).getName();
				File dest = new File(PathUtil.join(targetDir, fileName));
				downloader.copyURLToFile(src, dest);
			}
		} catch (MalformedURLException e) {
			throw new FileDownloadException("Invalid URL: " + e.getMessage());
		}

		// Parse files for building ReferenceDictionary objects.
		System.err.println("Building ReferenceDictionary...");
		final String chromInfoPath = PathUtil.join(downloadDir, dataSource.getName(),
				dataSource.getFileName("chromInfo"));
		final String chrToAccessionsPath = PathUtil.join(downloadDir, dataSource.getName(),
				dataSource.getFileName("chrToAccessions"));
		ReferenceDictParser dictParser = new ReferenceDictParser(chromInfoPath, chrToAccessionsPath, iniSection);
		ReferenceDictionary refDict = dictParser.parse();
		refDict.print(System.err);

		// Parse transcript files.
		System.err.println("Parsing transcripts...");
		ImmutableList<TranscriptInfo> transcripts = parseTranscripts(refDict, targetDir);

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
			System.err.println("WARNING: Could not parse HTTP_PROXY value " + envValue + " as URL.");
		}
		return result;
	}

	/**
	 * @return {@link FileDownloader.Options} with proxy settings from {@link #options} and environment.
	 */
	private FileDownloader.Options buildOptions() {
		FileDownloader.Options result = new FileDownloader.Options();

		// Get proxy settings from environment variables.
		Map<String, String> env = System.getenv();
		result.http = buildProxyOptions(env.get("HTTP_PROXY"));
		result.https = buildProxyOptions(env.get("HTTPS_PROXY"));
		result.ftp = buildProxyOptions(env.get("FTP_PROXY"));

		// Get proxy settings from options.
		if (options.proxy != null && options.proxy.getHostText() != null && !options.proxy.getHostText().equals("")) {
			result.http.host = options.proxy.getHostText();
			result.https.host = options.proxy.getHostText();
			result.ftp.host = options.proxy.getHostText();
		}
		if (options.proxy != null && options.proxy.hasPort()) {
			result.http.port = options.proxy.getPort();
			result.https.port = options.proxy.getPort();
			result.ftp.port = options.proxy.getPort();
		}

		return result;
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
	protected abstract ImmutableList<TranscriptInfo> parseTranscripts(ReferenceDictionary refDict, String targetDir)
			throws TranscriptParseException;

}
