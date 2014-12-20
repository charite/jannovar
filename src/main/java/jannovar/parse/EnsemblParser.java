package jannovar.parse;

import jannovar.exception.InvalidAttributException;
import jannovar.exception.KGParseException;
import jannovar.gff.FeatureProcessor;
import jannovar.gff.GFFParser;
import jannovar.gff.TranscriptInfoFactory;
import jannovar.io.EnsemblFastaParser;
import jannovar.io.ReferenceDictionary;
import jannovar.reference.TranscriptInfo;
import jannovar.reference.TranscriptInfoBuilder;
import jannovar.util.PathUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

/**
 * Class for orchestrating the parsing of Ensembl data.
 *
 * @author Peter Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class EnsemblParser implements TranscriptParser {

	/** {@link Logger} to use for logging */
	private static final Logger LOGGER = Logger.getLogger(GFFParser.class.getSimpleName());

	/** Path to the {@link ReferenceDictionary} to use for name/id and id/length mapping */
	private final ReferenceDictionary refDict;

	/** Path to directory where the to-be-parsed files live */
	private final String basePath;

	/** INI {@link Section} from the configuration */
	private final Section iniSection;

	/**
	 * @param refDict
	 *            path to {@link ReferenceDictionary} to use for name/id and id/length mapping.
	 * @param basePath
	 *            path to where the to-be-parsed files live
	 * @param iniSection
	 *            INI {@link Section} for the configuration
	 */
	public EnsemblParser(ReferenceDictionary refDict, String basePath, Section iniSection) {
		this.refDict = refDict;
		this.basePath = basePath;
		this.iniSection = iniSection;
	}

	@Override
	public ImmutableList<TranscriptInfo> run() throws KGParseException {
		// Parse GTF file, yielding a list of features.
		System.err.println("Parsing GTF...");
		GFFParser gffParser;
		try {
			gffParser = new GFFParser(PathUtil.join(basePath, getINIFileName("gtf")));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to load GTF data from Ensembl files: {0}", e.getMessage());
			throw new KGParseException(e.getMessage());
		}

		// Parse the GFF file and feed the resulting Feature objects into a TranscriptModelBuilder.
		FeatureProcessor fp = new FeatureProcessor(gffParser.gffVersion, refDict);
		gffParser.parse(fp);
		// Build ArrayList of TranscriptModelBuilder objects from feature list.
		ArrayList<TranscriptInfoBuilder> builders;
		try {
			System.err.println("Building transcript models...");
			TranscriptInfoFactory tif = new TranscriptInfoFactory(gffParser.gffVersion, refDict);
			builders = tif.buildTranscripts(fp.getGenes());
		} catch (InvalidAttributException e) {
			LOGGER.log(Level.SEVERE, "Unable to load data from Ensembl files: {0}", e.getMessage());
			throw new KGParseException(e.getMessage());
		}

		// Load sequences.
		System.err.println("Parsing FASTA...");
		EnsemblFastaParser efp = new EnsemblFastaParser(PathUtil.join(basePath, getINIFileName("cdna")), builders);
		int before = builders.size();
		builders = efp.parse();
		int after = builders.size();

		// Log success and statistics.
		Object params[] = { before, after };
		LOGGER.log(Level.INFO, "Found {0} transcript models from Ensembl GFF resource, {1} of which had sequences",
				params);

		// Create final list of TranscriptInfos.
		ImmutableList.Builder<TranscriptInfo> result = new ImmutableList.Builder<TranscriptInfo>();
		for (TranscriptInfoBuilder builder : builders)
			result.add(builder.make());
		return result.build();
	}

	/**
	 * @param key
	 *            name of the INI entry
	 * @return file name from INI <code>key</code.
	 */
	private String getINIFileName(String key) {
		return new File(iniSection.get(key)).getName();
	}

}
