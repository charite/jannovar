package de.charite.compbio.jannovar.impl.parse.refseq;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.parse.FASTAParser;
import de.charite.compbio.jannovar.impl.parse.FASTARecord;
import de.charite.compbio.jannovar.impl.parse.TranscriptParseException;
import de.charite.compbio.jannovar.impl.parse.TranscriptParser;
import de.charite.compbio.jannovar.impl.parse.gtfgff.FeatureRecord;
import de.charite.compbio.jannovar.impl.parse.gtfgff.GFFParser;
import de.charite.compbio.jannovar.impl.util.PathUtil;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;

/**
 * Parsing of RefSeq GFF3 files
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class RefSeqParser implements TranscriptParser {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(RefSeqParser.class);

	/** List of transcript-level feature types */
	private static final ImmutableSet<String> TX_LEVEL_FEATURE_TYPES = ImmutableSet.of("mRNA", "ncRNA", "rRNA", "tRNA",
			"primary_transcript", "transcript");

	/**
	 * Path to the {@link ReferenceDictionary} to use for name/id and id/length mapping
	 */
	private final ReferenceDictionary refDict;

	/** Path to directory where the to-be-parsed files live */
	private final String basePath;

	/** INI {@link Section} from the configuration. */
	private final Section iniSection;

	/**
	 * @param refDict
	 *            path to {@link ReferenceDictionary} to use for name/id and id/length mapping.
	 * @param basePath
	 *            path to where the to-be-parsed files live
	 * @param iniSection
	 *            {@link Section} with configuration from INI file
	 */
	public RefSeqParser(ReferenceDictionary refDict, String basePath, Section iniSection) {
		this.refDict = refDict;
		this.basePath = basePath;
		this.iniSection = iniSection;
	}

	@Override
	public ImmutableList<TranscriptModel> run() throws TranscriptParseException {
		// Load features from GFF3 file, clustered by the gene they belong to
		final String pathGFF = PathUtil.join(basePath, getINIFileName("gff"));
		Map<String, TranscriptModelBuilder> builders = recordsToBuilders(loadRecords(pathGFF));

		// Load the FASTA file and assign to the builders.
		final String pathFASTA = PathUtil.join(basePath, getINIFileName("rna"));
		loadFASTA(builders, pathFASTA);

		// Create final list of TranscriptInfos.
		ImmutableList.Builder<TranscriptModel> result = new ImmutableList.Builder<TranscriptModel>();
		for (Entry<String, TranscriptModelBuilder> entry : builders.entrySet())
			result.add(entry.getValue().build());
		return result.build();
	}

	/**
	 * Load FASTA from pathFASTA and set the sequence into builders.
	 * 
	 * @throws TranscriptParseException
	 *             on problems with parsing the FASTA
	 */
	private void loadFASTA(Map<String, TranscriptModelBuilder> builders, String pathFASTA)
			throws TranscriptParseException {
		// First, build mapping from RNA accession to builder
		Map<String, TranscriptModelBuilder> txMap = new HashMap<>();
		for (Entry<String, TranscriptModelBuilder> entry : builders.entrySet())
			txMap.put(entry.getValue().getSequence(), entry.getValue());

		// We must remove variants for which we did not find any sequence;
		Set<String> missingSequence = new HashSet<>();
		missingSequence.addAll(builders.keySet());

		// Next iterate over the FASTA file and assign sequence to the transcript
		FASTAParser fastaParser;
		try {
			fastaParser = new FASTAParser(new File(pathFASTA));
		} catch (IOException e) {
			throw new TranscriptParseException("Problem with opening FASTA file", e);
		}
		FASTARecord record;
		try {
			while ((record = fastaParser.next()) != null) {
				final List<String> tokens = Splitter.on('|').splitToList(record.getID());
				if (tokens.size() != 5) {
					LOGGER.error("ID {} in FASTA did not have 4 fields", new Object[] { record.getID() });
					continue;
				}

				final String accession = tokens.get(3);
				final TranscriptModelBuilder builder = txMap.get(accession);
				if (builder == null) {
					// This is not a warning as we observed this for some records regularly
					LOGGER.debug("ID {} from FASTA did not map to transcript", new Object[] { accession });
					continue;
				}

				assert missingSequence.contains(builder.getAccession());
				missingSequence.remove(builder.getAccession());

				builder.setAccession(builder.getSequence());
				builder.setSequence(record.getSequence());
				LOGGER.debug("Found sequence for transcript {}", new Object[] { builder.getAccession() });
			}
		} catch (IOException e) {
			throw new TranscriptParseException("Problem with reading FASTA file", e);
		}

		LOGGER.info("Ignoring {} transcripts without sequence.", new Object[] { missingSequence.size() });
		for (String key : missingSequence) {
			LOGGER.debug("--> {}", new Object[] { key });
			builders.remove(key);
		}

		LOGGER.info("Successfully processed {} transcripts with sequence.", new Object[] { builders.size() });
	}

	/**
	 * Convert list of GFF records into a mapping from transcript id to TranscriptModelBuilder
	 * 
	 * Then, we only have to assign the sequence into the TranscriptModelBuilder objects to get the appropriate
	 * TranscriptModel objects.
	 * 
	 * The TranscriptModelBuilder objects will have the a "Name" attribute of the mRNA set as the sequence, so we can
	 * use this for assigning FASTA sequence to the builders.
	 */
	private Map<String, TranscriptModelBuilder> recordsToBuilders(
			HashMap<String, ArrayList<FeatureRecord>> recordsByGene) {
		Map<String, TranscriptModelBuilder> result = new HashMap<>();
		for (Entry<String, ArrayList<FeatureRecord>> entry : recordsByGene.entrySet())
			result.putAll(processGeneGFFRecords(entry.getValue()));
		return result;
	}

	/**
	 * Process the GFFRecord objects for one gene.
	 */
	private Map<String, TranscriptModelBuilder> processGeneGFFRecords(ArrayList<FeatureRecord> records) {
		final Map<String, TranscriptModelBuilder> result = new HashMap<>();

		assert records.get(0).getType().equals("gene");
		final FeatureRecord geneRecord = records.get(0);

		// Get IDs of the transcripts and create a list of GFFRecord objects for each
		final HashMap<String, FeatureRecord> mrnaRecords = new HashMap<>();
		final HashMap<String, ArrayList<FeatureRecord>> recordsForMRNA = new HashMap<>();
		for (FeatureRecord record : records) {
			// We will later assign exons and CDS features to tx level features, but exons can also be part of a gene
			// only
			if (TX_LEVEL_FEATURE_TYPES.contains(record.getType()) || "gene".equals(record.getType())) {
				mrnaRecords.put(record.getAttributes().get("ID"), record);
				recordsForMRNA.put(record.getAttributes().get("ID"), new ArrayList<FeatureRecord>());
			}
		}

		// Factorize the GFFRecords by mRNA
		for (FeatureRecord record : records) {
			if (record.getType().equals("exon") || record.getType().equals("CDS")) {
				for (String parent : Splitter.on(',').split(record.getAttributes().get("Parent"))) {
					if (recordsForMRNA.get(parent) != null)
						recordsForMRNA.get(parent).add(record);
				}
			}
		}

		// Now, build TranscriptModelBuilder for each mRNA
		for (Entry<String, FeatureRecord> mrnaEntry : mrnaRecords.entrySet()) {
			final String mrnaID = mrnaEntry.getKey();
			final FeatureRecord mrnaRecord = mrnaEntry.getValue();
			final List<FeatureRecord> featureRecords = recordsForMRNA.get(mrnaID);

			final TranscriptModelBuilder builder = new TranscriptModelBuilder();

			// Parse out the simple attributes from the mRNA record
			final Strand strand = (mrnaRecord.getStrand() == FeatureRecord.Strand.FORWARD) ? Strand.FWD : Strand.REV;
			builder.setStrand(strand);
			builder.setAccession(mrnaID);
			builder.setGeneSymbol(geneRecord.getAttributes().get("Name"));
			final String mrnaName = mrnaRecord.getAttributes().get("Name");
			builder.setSequence(mrnaName);
			parseAltGeneIDs(builder, geneRecord);

			// Iterate over the features, interpreting "exon" and "CDS" entries
			GenomeInterval txRegion = null;
			GenomeInterval cdsRegion = null;
			boolean wrongContig = false;
			for (FeatureRecord record : featureRecords) {
				ImmutableMap<String, Integer> dict = refDict.getContigNameToID();
				final String seqID = record.getSeqID();
				if (!dict.containsKey(seqID)) {
					LOGGER.debug("Skipping record {} on unknown contig {}", new Object[] { record, seqID });
					wrongContig = true;
					continue;
				}
				if (record.getType().equals("exon")) {
					final int chrom = dict.get(seqID);
					GenomeInterval exon = new GenomeInterval(refDict, Strand.FWD, chrom, record.getBegin(),
							record.getEnd());
					exon = exon.withStrand(strand);
					if (txRegion == null)
						txRegion = exon;
					else
						txRegion = txRegion.union(exon);
					builder.addExonRegion(exon);
				} else if (record.getType().equals("CDS")) {
					GenomeInterval cds = new GenomeInterval(refDict, Strand.FWD,
							refDict.getContigNameToID().get(record.getSeqID()), record.getBegin(), record.getEnd());
					cds = cds.withStrand(strand);
					if (cdsRegion == null)
						cdsRegion = cds;
					else
						cdsRegion = cdsRegion.union(cds);
				}
			}
			if (wrongContig)
				continue; // skip, on wrong contig
			if (txRegion == null) {
				// Only warn if a transcript and not a gene, we only allow exons to be parts of genes as this is
				// observed in RefSeq
				if (!mrnaRecord.getType().equals("gene"))
					LOGGER.error("No transcript region for {}; skipping", new Object[] { mrnaEntry });
				continue;
			}
			builder.setTXRegion(txRegion);
			if (cdsRegion == null)
				cdsRegion = new GenomeInterval(txRegion.getGenomeBeginPos(), 0);
			builder.setCDSRegion(cdsRegion);

			if (onlyCurated() && mrnaName.startsWith("X")) {
				LOGGER.debug("Skipping non-curated transcript {}", new Object[] { mrnaName });
				continue; // skip non-curated one
			}

			result.put(mrnaID, builder);
		}

		return result;
	}

	/**
	 * Parse out alternative geneIDs
	 * 
	 * @param builder
	 *            The {@link TranscriptModelBuilder} to put the alternative geneIDs to
	 * @param geneRecord
	 *            {@link FeatureRecord} with the gene information
	 */
	private void parseAltGeneIDs(TranscriptModelBuilder builder, FeatureRecord geneRecord) {
		if (!geneRecord.getAttributes().containsKey("Dbxref"))
			return;
		for (String token : Splitter.on(',').split(geneRecord.getAttributes().get("Dbxref"))) {
			List<String> keyValue = Splitter.on(':').limit(2).splitToList(token);
			if (keyValue.size() != 2)
				continue;
			builder.getAltGeneIDs().put(keyValue.get(0), keyValue.get(1));
			if (keyValue.get(0).equals("GeneID"))
				builder.setGeneID(keyValue.get(1));
		}
	}

	/**
	 * Load GFF records, cluster by gene and return
	 * 
	 * @throws TranscriptParseException
	 *             on problems with handling the transcript file
	 */
	private HashMap<String, ArrayList<FeatureRecord>> loadRecords(String pathGFF) throws TranscriptParseException {
		HashMap<String, ArrayList<FeatureRecord>> result = new HashMap<String, ArrayList<FeatureRecord>>();

		// Open file using GFFParser
		GFFParser parser;
		try {
			parser = new GFFParser(new File(pathGFF));
		} catch (IOException e) {
			throw new TranscriptParseException("Problem opening GFF file", e);
		}

		// Map a feature to its gene
		HashMap<String, String> featureToGene = new HashMap<>();

		// Read file record by record, mapping features to genes
		//
		// This will only work properly if the full path of feature objects from the current feature has already been
		// read. Otherwise, we will need some more fancy parsing.
		int numRecords = 0;
		try {
			FeatureRecord record;
			while ((record = parser.next()) != null) {
				LOGGER.debug("Loaded GFF record {}", new Object[] { record });
				final String id = record.getAttributes().get("ID");
				numRecords += 1;
				if ("gene".equals(record.getType())) {
					LOGGER.debug("-> new gene {}", new Object[] { id });
					featureToGene.put(id, id); // register mapping
					assert !result.containsKey(id);
					result.put(id, Lists.newArrayList(record));
				} else {
					final String parent = record.getAttributes().get("Parent");
					if (parent == null)
						continue; // ignore
					final String top = featureToGene.get(parent);
					LOGGER.debug("-> parent = {}", new Object[] { parent });
					LOGGER.debug("-> top = {}", new Object[] { top });
					featureToGene.put(id, top); // register mapping
					assert featureToGene.get(top).equals(top);
					assert result.containsKey(top);
					result.get(top).add(record);
				}
			}
		} catch (IOException e) {
			throw new TranscriptParseException("Problem parsing GFF file", e);
		}

		LOGGER.info("Loaded {} GFF records for {} genes", new Object[] { numRecords, result.size() });

		return result;
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
