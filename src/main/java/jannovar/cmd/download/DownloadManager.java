package jannovar.cmd.download;

import jannovar.JannovarOptions;
import jannovar.common.Constants.Release;
import jannovar.exception.FileDownloadException;
import jannovar.exception.JannovarException;
import jannovar.io.TranscriptDataDownloader;
import jannovar.reference.TranscriptModel;

import java.util.ArrayList;

/**
 * Base class for the other DownloadOrchestrators.
 *
 * A Downloader downloads and serializes transcript data.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public abstract class DownloadManager {
	/** configuration to use for downloading/serializing the data */
	protected JannovarOptions options;

	/**
	 * @param options
	 *            configuration to use
	 */
	DownloadManager(JannovarOptions options) {
		this.options = options;
	}

	/**
	 * Perform download orchestration.
	 *
	 * @throws JannovarException
	 *             on problems with the serialization process
	 * @throws FileDownloadException
	 *             on problems with the file download
	 */
	public final void run() throws FileDownloadException, JannovarException {
		serializeTranscriptModelList(downloadAndBuildTranscriptModelList());
	}

	/**
	 * Download the transcript files and build transcript.
	 *
	 * @throws FileDownloadException
	 *             on problems with the file download
	 * @throws JannovarException
	 *             on problems with parsing the file.
	 */
	public abstract ArrayList<TranscriptModel> downloadAndBuildTranscriptModelList() throws FileDownloadException,
			JannovarException;

	/**
	 * Serialize the given transcript model list.
	 *
	 * @param lst
	 *            the transcript model list to serialize
	 * @throws JannovarException
	 *             on problems with the serialization process
	 */
	public abstract void serializeTranscriptModelList(ArrayList<TranscriptModel> lst) throws JannovarException;

	/**
	 * This function creates a {@link TranscriptDataDownloader} object in order to download the required transcript data
	 * files. If the user has set the proxy and proxy port via the command line, we use these to download the files.
	 *
	 * @param source
	 *            the source of the transcript data (e.g. RefSeq, Ensembl, UCSC)
	 * @param rel
	 *            the genome {@link Release}
	 * @throws FileDownloadException
	 *             on file download problems
	 */
	public final void downloadTranscriptFiles(int source, Release rel) throws FileDownloadException {
		TranscriptDataDownloader downloader;
		if (options.proxy != null && options.proxyPort != null) {
			downloader = new TranscriptDataDownloader(options.dirPath
					+ options.genomeRelease.getUCSCString(options.genomeRelease), options.proxy, options.proxyPort);
		} else {
			downloader = new TranscriptDataDownloader(options.dirPath
					+ options.genomeRelease.getUCSCString(options.genomeRelease));
		}
		downloader.downloadTranscriptFiles(source, rel);
	}
}
