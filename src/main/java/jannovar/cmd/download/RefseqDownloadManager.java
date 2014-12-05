package jannovar.cmd.download;

import jannovar.JannovarOptions;
import jannovar.common.Constants;
import jannovar.exception.FeatureFormatException;
import jannovar.exception.InvalidAttributException;
import jannovar.exception.JannovarException;
import jannovar.gff.Feature;
import jannovar.gff.GFFParser;
import jannovar.gff.TranscriptModelBuilder;
import jannovar.io.FastaParser;
import jannovar.io.RefSeqFastaParser;
import jannovar.io.SerializationManager;
import jannovar.reference.TranscriptInfo;
import jannovar.reference.TranscriptInfoBuilder;
import jannovar.util.PathUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for downloading transcript data from RefSeq.
 *
 * @author Peter Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class RefseqDownloadManager extends DownloadManager {

	/** {@link Logger} to use for logging */
	private static final Logger LOGGER = Logger.getLogger(GFFParser.class.getSimpleName());

	RefseqDownloadManager(JannovarOptions options) {
		super(options);
	}

	@Override
	public ArrayList<TranscriptInfo> downloadAndBuildTranscriptModelList() throws JannovarException {
		// download data
		downloadTranscriptFiles(jannovar.common.Constants.REFSEQ, options.genomeRelease);

		// parse GFF/GTF
		String path = PathUtil.join(options.downloadPath, options.genomeRelease.getUCSCString(options.genomeRelease));
		switch (this.options.genomeRelease) {
		case MM9:
			path = PathUtil.join(path, Constants.refseq_gff_mm9);
			break;
		case MM10:
			path = PathUtil.join(path, Constants.refseq_gff_mm10);
			break;
		case HG18:
			path = PathUtil.join(path, Constants.refseq_gff_hg18);
			break;
		case HG19:
			path = PathUtil.join(path, Constants.refseq_gff_hg19);
			break;
		case HG38:
			path = PathUtil.join(path, Constants.refseq_gff_hg38);
			break;
		default:
			throw new JannovarException("Unknown release: " + options.genomeRelease);
		}

		// Parse GFF file, yielding a list of features.
		GFFParser gffParser;
		try {
			gffParser = new GFFParser(path);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to load data from Ensembl files: {0}", e.getMessage());
			throw new JannovarException(e.getMessage());
		}
		ArrayList<Feature> features = gffParser.parse();

		// Build ArrayList of TranscriptModel objects from feature list.
		boolean onlyCurated = (options.dataSource == JannovarOptions.DataSource.REFSEQ_CURATED);
		ArrayList<TranscriptInfoBuilder> builders;
		try {
			builders = new TranscriptModelBuilder(gffParser.gffVersion, features).make(onlyCurated);
		} catch (InvalidAttributException e) {
			LOGGER.log(Level.SEVERE, "Unable to load data from Ensembl files: {0}", e.getMessage());
			throw new JannovarException(e.getMessage());
		} catch (FeatureFormatException e) {
			LOGGER.log(Level.SEVERE, "Unable to load data from Ensembl files: {0}", e.getMessage());
			throw new JannovarException(e.getMessage());
		}

		// Load sequences.
		String refSeqPath = PathUtil.join(options.downloadPath,
				options.genomeRelease.getUCSCString(options.genomeRelease), Constants.refseq_rna);
		System.err.println("path " + refSeqPath);
		FastaParser efp = new RefSeqFastaParser(refSeqPath, builders);
		int before = builders.size();
		builders = efp.parse();
		int after = builders.size();

		// Log success and statistics.
		Object params[] = { before, (onlyCurated ? "curated " : ""), after };
		LOGGER.log(Level.INFO, "Found {0} {1} transcript models from Refseq GFF resource, {2} of which had sequences.",
				params);

		// Create final list of TranscriptInfos.
		ArrayList<TranscriptInfo> result = new ArrayList<TranscriptInfo>();
		for (TranscriptInfoBuilder builder : builders)
			result.add(builder.make());

		return result;
	}

	/**
	 * Inputs the GFF data from RefSeq files, convert the resulting {@link TranscriptInfo} objects to
	 * {@link jannovar.interval.Interval Interval} objects, and store these in a serialized file.
	 *
	 * @throws JannovarException
	 *             on problems with the serialization process
	 */
	@Override
	public void serializeTranscriptModelList(ArrayList<TranscriptInfo> lst) throws JannovarException {
		SerializationManager manager = new SerializationManager();
		boolean onlyCurated = (options.dataSource == JannovarOptions.DataSource.REFSEQ_CURATED);
		String combiStringRelease = onlyCurated ? "cur_" + options.genomeRelease.getUCSCString(options.genomeRelease)
				: options.genomeRelease.getUCSCString(options.genomeRelease);
		System.err.println("[INFO] Serializing RefSeq data as "
				+ String.format(PathUtil.join(options.downloadPath, Constants.RefseqSerializationFileName),
						combiStringRelease));
		manager.serializeKnownGeneList(String.format(
				PathUtil.join(options.downloadPath, Constants.RefseqSerializationFileName), combiStringRelease), lst);
	}
}
