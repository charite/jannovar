package jannovar;

import jannovar.common.Constants;
import jannovar.exception.InvalidAttributException;
import jannovar.exception.JannovarException;
import jannovar.io.EnsemblFastaParser;
import jannovar.io.GFFparser;
import jannovar.io.SerializationManager;
import jannovar.reference.TranscriptModel;

import java.util.ArrayList;

/**
 * Class for downloading transcript data from Ensemble.
 *
 * @author Peter Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class EnsembleDownloadManager extends DownloadManager {

	EnsembleDownloadManager(JannovarOptions options) {
		super(options);
	}

	@Override
	public ArrayList<TranscriptModel> downloadAndBuildTranscriptModelList() throws JannovarException {
		// download the files
		downloadTranscriptFiles(jannovar.common.Constants.ENSEMBL, options.genomeRelease);

		// parse transcript model list from Ensemble and return it
		ArrayList<TranscriptModel> result = null;
		GFFparser gff = new GFFparser();
		String path;
		path = options.dirPath + options.genomeRelease.getUCSCString(options.genomeRelease);
		if (!path.endsWith(System.getProperty("file.separator")))
			path += System.getProperty("file.separator");
		switch (this.options.genomeRelease) {
		case MM9:
			path += Constants.ensembl_mm9;
			break;
		case MM10:
			path += Constants.ensembl_mm10;
			break;
		case HG18:
			path += Constants.ensembl_hg18;
			break;
		case HG19:
			path += Constants.ensembl_hg19;
			break;
		default:
			System.err.println("[ERROR] Unknown release: " + options.genomeRelease);
			System.exit(20);
			break;
		}
		gff.parse(path + Constants.ensembl_gtf);
		try {
			result = gff.getTranscriptModelBuilder().buildTranscriptModels();
			// System.out.println("[INFO] Got: "+this.transcriptModelList.size()
			// + " Ensembl transcripts");
		} catch (InvalidAttributException e) {
			System.err.println("[ERROR] Unable to input data from the Ensembl files");
			throw new JannovarException(e.getMessage());
		}

		// add sequences
		EnsemblFastaParser efp = new EnsemblFastaParser(path + Constants.ensembl_cdna, result);
		int before = result.size();
		result = efp.parse();
		int after = result.size();
		// System.out.println(String.format("[INFO] removed %d (%d --> %d) transcript models w/o rna sequence",
		// before-after,before, after));

		// TODO(holtgrem): change to logger
		System.err.println(String
				.format("[INFO] Found %d transcript models from Ensembl GFF resource, %d of which had sequences",
						before, after));
		return result;
	}

	/**
	 * Inputs the GFF data from Ensembl files, convert the resulting {@link jannovar.reference.TranscriptModel
	 * TranscriptModel} objects to {@link jannovar.interval.Interval Interval} objects, and store these in a serialized
	 * file.
	 *
	 * @throws jannovar.exception.JannovarException
	 *             on problems with the serialization process
	 */
	@Override
	public void serializeTranscriptModelList(ArrayList<TranscriptModel> lst) throws JannovarException {
		SerializationManager manager = new SerializationManager();
		System.err.println("[INFO] Serializing Ensembl data as "
				+ String.format(options.dirPath + Constants.EnsemblSerializationFileName,
						options.genomeRelease.getUCSCString(options.genomeRelease)));
		manager.serializeKnownGeneList(
				String.format(options.dirPath + Constants.EnsemblSerializationFileName,
						options.genomeRelease.getUCSCString(options.genomeRelease)), lst);

	}
}
