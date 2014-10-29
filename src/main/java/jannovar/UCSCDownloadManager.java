package jannovar;

import jannovar.common.Constants;
import jannovar.exception.FileDownloadException;
import jannovar.exception.JannovarException;
import jannovar.io.SerializationManager;
import jannovar.io.UCSCKGParser;
import jannovar.reference.TranscriptModel;

import java.util.ArrayList;

/**
 * Class for downloading transcript data from UCSC.
 *
 * @author Peter Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */

public class UCSCDownloadManager extends DownloadManager {

	UCSCDownloadManager(JannovarOptions options) {
		super(options);
	}

	/**
	 * Download transcript model and build it.
	 *
	 * Uses configuration from this.options.
	 */
	@Override
	public ArrayList<TranscriptModel> downloadAndBuildTranscriptModelList() throws FileDownloadException {
		// download the files
		downloadTranscriptFiles(jannovar.common.Constants.UCSC, options.genomeRelease);

		// parse transcript model list from UCSC and return
		String path = options.dirPath + options.genomeRelease.getUCSCString(options.genomeRelease);
		if (!path.endsWith(System.getProperty("file.separator")))
			path += System.getProperty("file.separator");
		UCSCKGParser parser = new UCSCKGParser(path);
		parser.parseUCSCFiles();
		return parser.getKnownGeneList();
	}

	/**
	 * Inputs the KnownGenes data from UCSC files, convert the resulting {@link jannovar.reference.TranscriptModel
	 * TranscriptModel} objects to {@link jannovar.interval.Interval Interval} objects, and store these in a serialized
	 * file.
	 *
	 * @throws jannovar.exception.JannovarException
	 */
	@Override
	public void serializeTranscriptModelList(ArrayList<TranscriptModel> lst) throws JannovarException {
		SerializationManager manager = new SerializationManager();
		System.err.println("[INFO] Serializing UCSC data as "
				+ String.format(options.dirPath + Constants.UCSCserializationFileName,
						options.genomeRelease.getUCSCString(options.genomeRelease)));
		manager.serializeKnownGeneList(
				String.format(options.dirPath + Constants.UCSCserializationFileName,
						options.genomeRelease.getUCSCString(options.genomeRelease)), lst);
	}
}
