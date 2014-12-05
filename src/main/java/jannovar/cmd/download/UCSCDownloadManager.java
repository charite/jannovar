package jannovar.cmd.download;

import jannovar.JannovarOptions;
import jannovar.common.Constants;
import jannovar.exception.FileDownloadException;
import jannovar.exception.JannovarException;
import jannovar.exception.KGParseException;
import jannovar.io.SerializationManager;
import jannovar.parse.UCSCParser;
import jannovar.reference.TranscriptInfo;
import jannovar.util.PathUtil;

import java.util.ArrayList;

/**
 * Class for downloading transcript data from UCSC.
 *
 * @author Peter Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */

public final class UCSCDownloadManager extends DownloadManager {

	UCSCDownloadManager(JannovarOptions options) {
		super(options);
	}

	/**
	 * Download transcript model and build it.
	 *
	 * Uses configuration from this.options.
	 *
	 * @throws FileDownloadException
	 *             on problems with the file download
	 * @throws KGParseException
	 *             on problems with parsing
	 */
	@Override
	public ArrayList<TranscriptInfo> downloadAndBuildTranscriptModelList() throws FileDownloadException,
			KGParseException {
		// download the files
		downloadTranscriptFiles(jannovar.common.Constants.UCSC, options.genomeRelease);

		// parse transcript model list from UCSC and return
		String path = PathUtil.join(options.downloadPath, options.genomeRelease.getUCSCString(options.genomeRelease));
		if (!path.endsWith(System.getProperty("file.separator")))
			path += System.getProperty("file.separator");
		UCSCParser parser = new UCSCParser(path);
		return parser.run();
	}

	/**
	 * Inputs the KnownGenes data from UCSC files, convert the resulting {@link TranscriptInfo} objects to
	 * {@link Interval} objects, and store these in a serialized file.
	 *
	 * @throws jannovar.exception.JannovarException
	 */
	@Override
	public void serializeTranscriptModelList(ArrayList<TranscriptInfo> lst) throws JannovarException {
		SerializationManager manager = new SerializationManager();
		System.err.println("[INFO] Serializing UCSC data as "
				+ String.format(PathUtil.join(options.downloadPath, Constants.UCSCserializationFileName),
						options.genomeRelease.getUCSCString(options.genomeRelease)));
		manager.serializeKnownGeneList(String.format(
				PathUtil.join(options.downloadPath, Constants.UCSCserializationFileName),
				options.genomeRelease.getUCSCString(options.genomeRelease)), lst);
	}
}
