package jannovar.cmd.download;

import jannovar.JannovarOptions;
import jannovar.common.Constants.Release;
import jannovar.exception.FileDownloadException;
import jannovar.exception.JannovarException;
import jannovar.io.TranscriptDataDownloader;
import jannovar.reference.GenomeInterval;
import jannovar.reference.TranscriptInfo;
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
		serializeTranscriptModelList(cleanTranscriptModelList(downloadAndBuildTranscriptModelList()));
	}

	/**
	 * Clean list of {@link TranscriptModel} objects and return this.
	 *
	 * Inspect the downloaded data for inconsistencies and remove defective transcripts. Print warnings in case of
	 * defects. Currently, the following defects are checked for:
	 *
	 * <ul>
	 * <li>The sum of the exon lengths is longer than the sequence length. This happens for UCSC transcript uc003lkb.4,
	 * for example which has one exon of length 7kb but only has 3.5kb of RNA.</li>
	 * </ul>
	 *
	 * @param downloadAndBuildTranscriptModelList
	 * @return filtered list of {@link TranscriptModel} objects
	 */
	private ArrayList<TranscriptModel> cleanTranscriptModelList(ArrayList<TranscriptModel> input) {
		ArrayList<TranscriptModel> result = new ArrayList<TranscriptModel>();

		int numWarnings = 0;
		for (TranscriptModel tm : input) {
			TranscriptInfo transcript = new TranscriptInfo(tm);
			int lenSum = 0;
			for (GenomeInterval region: transcript.exonRegions)
				lenSum += region.length();
			if (lenSum > transcript.sequence.length()) {
				System.err.println("WARNING: Inconsistent transcript length for " + transcript.accession
						+ "! The length as indicated by transcript record is " + lenSum
						+ " and the length of the RNA sequence is " + transcript.sequence.length()
						+ " The record will be ignored.");
				numWarnings += 1;
			} else {
				result.add(tm);
			}
		}

		System.err.println("Number of warnings: " + numWarnings);

		return result;
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
