package jannovar.cmd.download;

import jannovar.JannovarOptions;
import jannovar.common.Constants;
import jannovar.exception.FeatureFormatException;
import jannovar.exception.InvalidAttributException;
import jannovar.exception.JannovarException;
import jannovar.gff.Feature;
import jannovar.gff.GFFParser;
import jannovar.gff.TranscriptModelBuilder;
import jannovar.io.EnsemblFastaParser;
import jannovar.io.SerializationManager;
import jannovar.reference.TranscriptInfo;
import jannovar.reference.TranscriptInfoBuilder;
import jannovar.util.PathUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for downloading transcript data from Ensembl.
 *
 * @author Peter Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class EnsemblDownloadManager extends DownloadManager {

	/** {@link Logger} to use for logging */
	private static final Logger LOGGER = Logger.getLogger(GFFParser.class.getSimpleName());

	EnsemblDownloadManager(JannovarOptions options) {
		super(options);
	}

	@Override
	public ArrayList<TranscriptInfo> downloadAndBuildTranscriptModelList() throws JannovarException {
		// download the files
		downloadTranscriptFiles(jannovar.common.Constants.ENSEMBL, options.genomeRelease);

		// parse transcript model list from Ensemble and return it
		String path;
		path = PathUtil.join(options.downloadPath, options.genomeRelease.getUCSCString(options.genomeRelease));
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
			throw new JannovarException("Unknown release: " + options.genomeRelease);
		}
		// Parse GFF file, yielding a list of features.
		GFFParser gffParser;
		try {
			gffParser = new GFFParser(path + Constants.ensembl_gtf);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to load GFF data from Ensembl files: {0}", e.getMessage());
			throw new JannovarException(e.getMessage());
		}
		ArrayList<Feature> features = gffParser.parse();

		// Build ArrayList of TranscriptModelBuilder objects from feature list.
		ArrayList<TranscriptInfoBuilder> builders;
		try {
			builders = new TranscriptModelBuilder(gffParser.gffVersion, features).make();
		} catch (InvalidAttributException e) {
			LOGGER.log(Level.SEVERE, "Unable to load data from Ensembl files: {0}", e.getMessage());
			throw new JannovarException(e.getMessage());
		} catch (FeatureFormatException e) {
			LOGGER.log(Level.SEVERE, "Unable to load data from Ensembl files: {0}", e.getMessage());
			throw new JannovarException(e.getMessage());
		}

		// Load sequences.
		EnsemblFastaParser efp = new EnsemblFastaParser(path + Constants.ensembl_cdna, builders);
		int before = builders.size();
		builders = efp.parse();
		int after = builders.size();

		// Log success and statistics.
		Object params[] = { before, after };
		LOGGER.log(Level.INFO, "Found {0} transcript models from Ensembl GFF resource, {1} of which had sequences",
				params);

		// Create final list of TranscriptInfos.
		ArrayList<TranscriptInfo> result = new ArrayList<TranscriptInfo>();
		for (TranscriptInfoBuilder builder : builders)
			result.add(builder.make());

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
	public void serializeTranscriptModelList(ArrayList<TranscriptInfo> lst) throws JannovarException {
		SerializationManager manager = new SerializationManager();
		System.err.println("[INFO] Serializing Ensembl data as "
				+ String.format(PathUtil.join(options.downloadPath, Constants.EnsemblSerializationFileName),
						options.genomeRelease.getUCSCString(options.genomeRelease)));
		manager.serializeKnownGeneList(String.format(
				PathUtil.join(options.downloadPath, Constants.EnsemblSerializationFileName),
				options.genomeRelease.getUCSCString(options.genomeRelease)), lst);

	}
}
