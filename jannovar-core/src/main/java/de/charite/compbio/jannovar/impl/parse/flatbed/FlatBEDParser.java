package de.charite.compbio.jannovar.impl.parse.flatbed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.parse.TranscriptParseException;
import de.charite.compbio.jannovar.impl.parse.TranscriptParser;
import de.charite.compbio.jannovar.impl.util.PathUtil;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;

/**
 * Class for parsing flat BED data.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class FlatBEDParser implements TranscriptParser {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(FlatBEDParser.class);

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
	public FlatBEDParser(ReferenceDictionary refDict, String basePath, Section iniSection, boolean printProgressBars) {
		this.refDict = refDict;
		this.basePath = basePath;
		this.iniSection = iniSection;
		this.printProgressBars = printProgressBars;
	}

	@Override
	public ImmutableList<TranscriptModel> run() throws TranscriptParseException {
		ArrayList<TranscriptModelBuilder> builders;

		// Parse BED file, yielding a list of features.
		LOGGER.info("Parsing BED...");
		builders = parseBEDFile(PathUtil.join(basePath, getINIFileName("bed")));

		// Load sequence.
		LOGGER.error("Parsing FASTA...");
		FlatBEDFastaParser efp = new FlatBEDFastaParser(PathUtil.join(basePath, getINIFileName("dna")), builders,
				printProgressBars);
		/*
		int before = builders.size();
		builders = efp.parse();
		int after = builders.size();

		// Log success and statistics.
		Object params[] = { before, after };
		LOGGER.info("Found {} transcript models from flat BED resource, {} of which had sequences", params);
		*/

		// Create final list of TranscriptInfos.
		ImmutableList.Builder<TranscriptModel> result = new ImmutableList.Builder<TranscriptModel>();
		for (TranscriptModelBuilder builder : builders)
			result.add(builder.build());
		return result.build();
	}

	/**
	 * Parse BED6 file
	 *
	 * @param path
	 *            to the BED file to load
	 * @return list of {@link TranscriptModelBuilder} objects
	 */
	private ArrayList<TranscriptModelBuilder> parseBEDFile(String path) {
		ArrayList<TranscriptModelBuilder> result = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] arr = line.trim().split("\t");
				if (!arr[5].equals("+") && !arr[5].equals("-"))
					throw new RuntimeException("Invalid strand value " + arr[5]);

				Strand strand = (arr[5] == "+") ? Strand.FWD : Strand.REV;
				int chr = refDict.getContigNameToID().get(arr[0]);
				int beginPos = Integer.parseInt(arr[1]);
				int endPos = Integer.parseInt(arr[2]);
				GenomeInterval itv = new GenomeInterval(refDict, Strand.FWD, chr, beginPos, endPos).withStrand(strand);

				TranscriptModelBuilder builder = new TranscriptModelBuilder();
				builder.setStrand(strand);
				builder.setAccession(arr[3]);
				builder.setGeneSymbol(arr[3]);
				builder.setTXRegion(itv);
				builder.setCDSRegion(itv);
				builder.addExonRegion(itv);
				builder.setGeneID(arr[3]);
				builder.setTranscriptSupportLevel(-1);

				result.add(builder);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not find file at " + path, e);
		} catch (IOException e) {
			throw new RuntimeException("Problem reading from " + path, e);
		}

		return result;
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
