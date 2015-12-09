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
 * Class for orchestrating the parsing of Ensembl data.
 *
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @author <a href="mailto:marten.jaeger@charite.de">Marten Jaeger</a>
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class EnsemblParser implements TranscriptParser {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(EnsemblParser.class);

	/** Path to the {@link ReferenceDictionary} to use for name/id and id/length mapping */
	private final ReferenceDictionary refDict;

	/** Path to directory where the to-be-parsed files live */
	private final String basePath;

	/** INI {@link Section} from the configuration */
	private final Section iniSection;

	/** whether or not to print the progress bars */
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
	public EnsemblParser(ReferenceDictionary refDict, String basePath, Section iniSection, boolean printProgressBars) {
		this.refDict = refDict;
		this.basePath = basePath;
		this.iniSection = iniSection;
		this.printProgressBars = printProgressBars;
	}

	public ImmutableList<TranscriptModel> run() throws TranscriptParseException {
		// Parse GTF file, yielding a list of features.
		LOGGER.info("Parsing GTF...");
		GFFParser gffParser;
		try {
			gffParser = new GFFParser(PathUtil.join(basePath, getINIFileName("gtf")));
		} catch (IOException e) {
			LOGGER.error("Unable to load GTF data from Ensembl files: {}", e.getMessage());
			throw new TranscriptParseException("Problem loading GTF data.", e);
		}

		// Parse the GFF file and feed the resulting Feature objects into a TranscriptModelBuilder.
		FeatureProcessor fp = new FeatureProcessor(gffParser.getGffVersion(), true, refDict);
		gffParser.parse(fp);
		// Build ArrayList of TranscriptModelBuilder objects from feature list.
		ArrayList<TranscriptModelBuilder> builders;
		try {
			LOGGER.info("Building transcript models...");
			TranscriptModelBuilderFactory tmbf = new TranscriptModelBuilderFactory(true, gffParser.getGffVersion(),
					refDict);
			builders = tmbf.buildTranscriptModelBuilders(fp.getGenes());
			TranscriptSupportLevelsSetterFromLengths.run(builders);
		} catch (InvalidAttributeException e) {
			LOGGER.error("Unable to load data from Ensembl files: {}", e.getMessage());
			throw new TranscriptParseException("Problem loading GTF data.", e);
		}

		// Load sequences.
		LOGGER.error("Parsing FASTA...");
		EnsemblFastaParser efp = new EnsemblFastaParser(PathUtil.join(basePath, getINIFileName("cdna")), builders,
				printProgressBars);
		int before = builders.size();
		builders = efp.parse();
		int after = builders.size();

		// Log success and statistics.
		Object params[] = { before, after };
		LOGGER.info("Found {} transcript models from Ensembl GFF resource, {} of which had sequences", params);

		// Create final list of TranscriptInfos.
		ImmutableList.Builder<TranscriptModel> result = new ImmutableList.Builder<TranscriptModel>();
		for (TranscriptModelBuilder builder : builders)
			result.add(builder.build());
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
