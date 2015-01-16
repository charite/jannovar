package de.charite.compbio.jannovar.impl.parse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.impl.parse.gff.FeatureProcessor;
import de.charite.compbio.jannovar.impl.parse.gff.GFFParser;
import de.charite.compbio.jannovar.impl.parse.gff.TranscriptInfoFactory;
import de.charite.compbio.jannovar.impl.util.PathUtil;
import de.charite.compbio.jannovar.io.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;

/**
 * Class for orchestrating the parsing of RefSeq data.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Marten Jaeger <marten.jaeger@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class RefSeqParser implements TranscriptParser {

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
	public RefSeqParser(ReferenceDictionary refDict, String basePath, Section iniSection) {
		this.refDict = refDict;
		this.basePath = basePath;
		this.iniSection = iniSection;
	}

	@Override
	public ImmutableList<TranscriptModel> run() throws TranscriptParseException {
		// Parse GFF file, yielding a list of features.
		System.err.println("Parsing GFF...");
		GFFParser gffParser;
		try {
			gffParser = new GFFParser(PathUtil.join(basePath, getINIFileName("gff")));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to load GFF data from RefSeq files: {0}", e.getMessage());
			throw new TranscriptParseException(e.getMessage());
		}

		// Parse the GFF file and feed the resulting Feature objects into a TranscriptModelBuilder.
		FeatureProcessor fp = new FeatureProcessor(gffParser.gffVersion, refDict);
		gffParser.parse(fp);
		// Build ArrayList of TranscriptModelBuilder objects from feature list.
		ArrayList<TranscriptModelBuilder> builders;
		try {
			System.err.println("Building transcript models...");
			TranscriptInfoFactory tif = new TranscriptInfoFactory(gffParser.gffVersion, refDict);
			builders = tif.buildTranscripts(fp.getGenes(), onlyCurated());
		} catch (InvalidAttributeException e) {
			LOGGER.log(Level.SEVERE, "Unable to load data from RefSeq files: {0}", e.getMessage());
			throw new TranscriptParseException(e.getMessage());
		}

		// Load sequences.
		String refSeqPath = PathUtil.join(basePath, getINIFileName("rna"));
		System.err.println("path " + refSeqPath);
		FastaParser efp = new RefSeqFastaParser(refSeqPath, builders);
		int before = builders.size();
		builders = efp.parse();
		int after = builders.size();

		// Log success and statistics.
		Object params[] = { before, (onlyCurated() ? "curated " : ""), after };
		LOGGER.log(Level.INFO, "Found {0} {1} transcript models from Refseq GFF resource, {2} of which had sequences.",
				params);

		// Create final list of TranscriptInfos.
		ImmutableList.Builder<TranscriptModel> result = new ImmutableList.Builder<TranscriptModel>();
		for (TranscriptModelBuilder builder : builders)
			result.add(builder.build());
		return result.build();
	}

	/**
	 * @return <code>true</code> if only curated entries are to be returned
	 */
	private boolean onlyCurated() {
		String value = iniSection.fetch("onlyCurated");
		if (value == null)
			return false;
		value = value.toLowerCase();
		ImmutableList<String> list = ImmutableList.of("true", "1", "yes");
		for (String s : list)
			if (s.equals(value))
				return true;
		return false;
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
