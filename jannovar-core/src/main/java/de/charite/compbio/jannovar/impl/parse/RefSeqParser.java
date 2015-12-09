package de.charite.compbio.jannovar.impl.parse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.parse.gff.FeatureProcessor;
import de.charite.compbio.jannovar.impl.parse.gff.GFFParser;
import de.charite.compbio.jannovar.impl.parse.gff.TranscriptModelBuilderFactory;
import de.charite.compbio.jannovar.impl.util.PathUtil;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;

/**
 * Class for orchestrating the parsing of RefSeq data.
 *
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @author <a href="mailto:marten.jaeger@charite.de">Marten Jaeger</a>
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class RefSeqParser implements TranscriptParser {

	/** {@link Logger} to use for logging */
	private static final Logger LOGGER = LoggerFactory.getLogger(RefSeqParser.class);

	/** Path to the {@link ReferenceDictionary} to use for name/id and id/length mapping */
	private final ReferenceDictionary refDict;

	/** Path to directory where the to-be-parsed files live */
	private final String basePath;

	/** INI {@link Section} from the configuration */
	private final Section iniSection;

	/** whether or not to print progress bars */
	private final boolean printProgressBars;

	/**
	 * @param refDict
	 *            path to {@link ReferenceDictionary} to use for name/id and id/length mapping.
	 * @param basePath
	 *            path to where the to-be-parsed files live
	 * @param iniSection
	 *            INI {@link Section} for the configuration
	 * @param printProgressBars
	 *            whether or not to print progress bars
	 */
	public RefSeqParser(ReferenceDictionary refDict, String basePath, Section iniSection, boolean printProgressBars) {
		this.refDict = refDict;
		this.basePath = basePath;
		this.iniSection = iniSection;
		this.printProgressBars = printProgressBars;
	}

	public ImmutableList<TranscriptModel> run() throws TranscriptParseException {
		// Parse GFF file, yielding a list of features.
		LOGGER.info("Parsing GFF...");
		GFFParser gffParser;
		try {
			gffParser = new GFFParser(PathUtil.join(basePath, getINIFileName("gff")));
		} catch (IOException e) {
			LOGGER.error("Unable to load GFF data from RefSeq files: {}", e);
			throw new TranscriptParseException("Problem parsing transcripts.", e);
		}

		// Parse the GFF file and feed the resulting Feature objects into a TranscriptModelBuilder.
		FeatureProcessor fp = new FeatureProcessor(gffParser.getGffVersion(), false, refDict);
		gffParser.parse(fp);
		// Build ArrayList of TranscriptModelBuilder objects from feature list.
		ArrayList<TranscriptModelBuilder> builders;
		try {
			LOGGER.info("Building transcript models...");
			TranscriptModelBuilderFactory tif = new TranscriptModelBuilderFactory(false, gffParser.getGffVersion(), refDict);
			builders = tif.buildTranscriptModelBuilders(fp.getGenes(), onlyCurated());
			TranscriptSupportLevelsSetterFromLengths.run(builders);
		} catch (InvalidAttributeException e) {
			LOGGER.error("Unable to load data from RefSeq files: {}", e);
			throw new TranscriptParseException("Problem parsing transcripts.", e);
		}

		// Load sequences.
		String refSeqPath = PathUtil.join(basePath, getINIFileName("rna"));
		FastaParser efp = new RefSeqFastaParser(refSeqPath, builders, printProgressBars);
		int before = builders.size();
		builders = efp.parse();
		int after = builders.size();

		// Log success and statistics.
		Object params[] = { before, (onlyCurated() ? "curated " : ""), after };
		LOGGER.info("Found {} {}transcript models from Refseq GFF resource, {} of which had sequences.", params);

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
