package jannovar.cmd.download;

import jannovar.JannovarOptions;
import jannovar.common.Constants;
import jannovar.exception.InvalidAttributException;
import jannovar.exception.JannovarException;
import jannovar.gff.GFFParser;
import jannovar.io.FastaParser;
import jannovar.io.RefSeqFastaParser;
import jannovar.io.SerializationManager;
import jannovar.reference.TranscriptModel;
import jannovar.util.PathUtil;

import java.util.ArrayList;

/**
 * Class for downloading transcript data from RefSeq.
 *
 * @author Peter Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class RefseqDownloadManager extends DownloadManager {

	RefseqDownloadManager(JannovarOptions options) {
		super(options);
	}

	@Override
	public ArrayList<TranscriptModel> downloadAndBuildTranscriptModelList() throws JannovarException {
		// download data
		downloadTranscriptFiles(jannovar.common.Constants.REFSEQ, options.genomeRelease);

		// parse transcript model list from Refseq and return it
		ArrayList<TranscriptModel> result = null;
		// parse GFF/GTF
		GFFParser gff = new GFFParser();
		String path = PathUtil.join(options.downloadPath, options.genomeRelease.getUCSCString(options.genomeRelease));
		if (!path.endsWith(System.getProperty("file.separator")))
			path += System.getProperty("file.separator");
		switch (this.options.genomeRelease) {
		case MM9:
			gff.parse(path + Constants.refseq_gff_mm9);
			break;
		case MM10:
			gff.parse(path + Constants.refseq_gff_mm10);
			break;
		case HG18:
			gff.parse(path + Constants.refseq_gff_hg18);
			break;
		case HG19:
			gff.parse(path + Constants.refseq_gff_hg19);
			break;
		case HG38:
			gff.parse(path + Constants.refseq_gff_hg38);
			break;
		default:
			System.err.println("[ERROR] Unknown release: " + options.genomeRelease);
			System.exit(20);
			break;
		}
		try {
			result = gff.getTranscriptModelBuilder().buildTranscriptModels(
					options.dataSource == JannovarOptions.DataSource.REFSEQ_CURATED);
		} catch (InvalidAttributException e) {
			throw new JannovarException(e.getMessage());
		}
		// add sequences
		FastaParser efp = new RefSeqFastaParser(path + Constants.refseq_rna, result);
		int before = result.size();
		result = efp.parse();
		int after = result.size();
		// System.out.println(String.format("[INFO] removed %d (%d --> %d) transcript models w/o rna sequence",
		// before-after,before, after));
		if (options.dataSource == JannovarOptions.DataSource.REFSEQ_CURATED)
			System.err.println(String.format(
					"[INFO] Found %d curated transcript models from Refseq GFF resource, %d of which had sequences",
					before, after));
		else
			System.err.println(String.format(
					"[INFO] Found %d transcript models from Refseq GFF resource, %d of which had sequences", before,
					after));
		return result;
	}

	/**
	 * Inputs the GFF data from RefSeq files, convert the resulting {@link jannovar.reference.TranscriptModel
	 * TranscriptModel} objects to {@link jannovar.interval.Interval Interval} objects, and store these in a serialized
	 * file.
	 *
	 * @throws JannovarException
	 *             on problems with the serialization process
	 */
	@Override
	public void serializeTranscriptModelList(ArrayList<TranscriptModel> lst) throws JannovarException {
		SerializationManager manager = new SerializationManager();
		boolean onlyCurated = (options.dataSource == JannovarOptions.DataSource.REFSEQ_CURATED);
		String combiStringRelease = onlyCurated ? "cur_"
				+ options.genomeRelease.getUCSCString(options.genomeRelease) : options.genomeRelease
				.getUCSCString(options.genomeRelease);
		System.err.println("[INFO] Serializing RefSeq data as "
				+ String.format(PathUtil.join(options.downloadPath, Constants.RefseqSerializationFileName),
						combiStringRelease));
		manager.serializeKnownGeneList(String.format(
				PathUtil.join(options.downloadPath, Constants.RefseqSerializationFileName), combiStringRelease), lst);
	}
}
