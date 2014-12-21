package jannovar.datasource;

import jannovar.exception.FileDownloadException;
import jannovar.exception.InvalidDataSourceException;
import jannovar.exception.TranscriptParseException;
import jannovar.io.JannovarData;
import jannovar.io.ReferenceDictionary;
import jannovar.parse.ReferenceDictParser;
import jannovar.reference.TranscriptInfo;
import jannovar.util.PathUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.common.collect.ImmutableList;

/**
 * Interface for data factories, allowing to create {@link JannovarData} objects from {@link DataSource}s.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public abstract class JannovarDataFactory {

	/** the {@link DataSource} to use */
	private final DataSource dataSource;

	/**
	 * Construct the factory with the given {@link DataSource}.
	 *
	 * @param dataSource
	 *            the data source to use.
	 */
	public JannovarDataFactory(DataSource dataSource) {
		this.dataSource = dataSource;
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

		// Download files.
		System.err.println("Downloading data...");
		try {
			for (String url : dataSource.getDownloadURLs()) {
				System.err.println("Downloading " + url);
				URL src = new URL(url);
				String fileName = new File(src.getPath()).getName();
				File dest = new File(PathUtil.join(targetDir, fileName));
				FileDownloader.copyURLToFile(src, dest);
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
		ReferenceDictParser dictParser = new ReferenceDictParser(chromInfoPath, chrToAccessionsPath);
		ReferenceDictionary refDict = dictParser.parse();
		refDict.print();

		// Parse transcript files.
		System.err.println("Parsing transcripts...");
		ImmutableList<TranscriptInfo> transcripts = parseTranscripts(refDict, targetDir);

		return new JannovarData(refDict, transcripts);
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
