package jannovar.cmd.download;

import jannovar.JannovarOptions;
import jannovar.common.Constants.Release;
import jannovar.exception.FileDownloadException;
import jannovar.exception.JannovarException;
import jannovar.io.TranscriptDataDownloader;
import jannovar.reference.GenomeInterval;
import jannovar.reference.TranscriptInfo;
import jannovar.reference.TranscriptModel;
import jannovar.util.PathUtil;

import java.util.ArrayList;

/**
 * Base class for the other DownloadOrchestrators.
 *
 * A Downloader downloads and serializes transcript data.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
abstract class DownloadManager {
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
	 * <li>The CDS sequence starts before the transcript sequence. This happens for ENSEMBL in the case of 5' UTR
	 * truncation.</li>
	 * <li>Likewise, the CDS sequence ends after the transcript sequence. This happens for ENSEMBL in the case of 3' UTR
	 * truncation.</li>
	 * </ul>
	 *
	 * @param downloadAndBuildTranscriptModelList
	 * @return filtered list of {@link TranscriptModel} objects
	 */
	private ArrayList<TranscriptInfo> cleanTranscriptModelList(ArrayList<TranscriptInfo> input) {
		ArrayList<TranscriptInfo> result = new ArrayList<TranscriptInfo>();

		int numWarnings = 0;
		for (TranscriptInfo transcript : input) {
			boolean exonLengthSumOK = !hasInconsistentExonLengthSum(transcript);
			boolean cdsStartOK = !hasInconsistentCDSStart(transcript);
			boolean cdsEndOK = !hasInconsistentCDSEnd(transcript);

			if (!exonLengthSumOK || !cdsStartOK || !cdsEndOK)
				numWarnings += 1;
			else
				result.add(transcript);
		}

		System.err.println("Number of warnings: " + numWarnings);

		return result;
	}

	/**
	 * @return <code>true</code> if transcript is coding and the exon length sum indicate a greater length than the
	 *         underlying sequence
	 */
	private boolean hasInconsistentExonLengthSum(TranscriptInfo transcript) {
		int lenSum = 0;
		for (GenomeInterval region : transcript.exonRegions)
			lenSum += region.length();
		if (transcript.isCoding() && lenSum > transcript.sequence.length()) {
			System.err.println("WARNING: Inconsistent transcript length for " + transcript.accession
					+ "! The length as indicated by transcript record is " + lenSum
					+ " and the length of the RNA sequence is " + transcript.sequence.length()
					+ " The record will be ignored.");
			return true;
		}
		return false;
	}

	/**
	 * @return <code>true</code> if the CDS starts before the transcript start
	 */
	private boolean hasInconsistentCDSStart(TranscriptInfo transcript) {
		if (transcript.cdsRegion.getGenomeEndPos().isGt(transcript.txRegion.getGenomeEndPos())) {
			System.err.println("WARNING: CDS ends right of the transcript for " + transcript.accession
					+ " The record will be ignored.");
			return true;
		}
		return false;
	}

	/**
	 * @return <code>true</code> if the CDS ends after the transcript start
	 */
	private boolean hasInconsistentCDSEnd(TranscriptInfo transcript) {
		if (transcript.cdsRegion.getGenomeBeginPos().isLt(transcript.txRegion.getGenomeBeginPos())) {
			System.err.println("WARNING: CDS begins left of the transcript for " + transcript.accession
					+ " The record will be ignored.");
			return true;
		}
		return false;
	}

	/**
	 * Download the transcript files and build transcript.
	 *
	 * @throws FileDownloadException
	 *             on problems with the file download
	 * @throws JannovarException
	 *             on problems with parsing the file.
	 */
	public abstract ArrayList<TranscriptInfo> downloadAndBuildTranscriptModelList()
			throws FileDownloadException,
			JannovarException;

	/**
	 * Serialize the given transcript list.
	 *
	 * @param lst
	 *            the transcript list to serialize
	 * @throws JannovarException
	 *             on problems with the serialization process
	 */
	public abstract void serializeTranscriptModelList(ArrayList<TranscriptInfo> lst) throws JannovarException;

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
		String path = PathUtil.join(options.downloadPath, options.genomeRelease.getUCSCString(options.genomeRelease));
		if (options.proxy != null) {
			downloader = new TranscriptDataDownloader(path, options.proxy.getHostText(), ""
					+ options.proxy.getPort());
		} else {
			downloader = new TranscriptDataDownloader(path);
		}
		downloader.downloadTranscriptFiles(source, rel);
	}
}
