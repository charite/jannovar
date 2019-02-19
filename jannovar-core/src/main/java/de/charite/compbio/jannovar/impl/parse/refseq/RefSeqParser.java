package de.charite.compbio.jannovar.impl.parse.refseq;

import com.google.common.base.Splitter;
import com.google.common.collect.*;
import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.datasource.TranscriptModelBuilderHGNCExtender;
import de.charite.compbio.jannovar.hgnc.AltGeneIDType;
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
import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import static java.util.stream.Collectors.*;
/**
 * Parsing of RefSeq GFF3 files
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class RefSeqParser implements TranscriptParser {

	/**
	 * the logger object to use
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RefSeqParser.class);

	/**
	 * List of transcript-level feature types
	 */
	private static final ImmutableSet<String> TX_LEVEL_FEATURE_TYPES = ImmutableSet.of("mRNA", "ncRNA", "rRNA", "tRNA",
		"primary_transcript", "transcript");

	/**
	 * Path to the {@link ReferenceDictionary} to use for name/id and id/length mapping
	 */
	private ReferenceDictionary refDict;

	/*
	 * Contig name mappings
	 */
	private final ImmutableMap<String, Integer> contigDict;

	/**
	 * Path to directory where the to-be-parsed files live
	 */
	private String basePath;

	/**
	 * INI {@link Section} from the configuration.
	 */
	private Section iniSection;

	/**
	 * List of gene identifiers of genes to include, if non-empty.
	 */
	private final List<String> geneIdentifiers;

	/**
	 * @param refDict         path to {@link ReferenceDictionary} to use for name/id and id/length mapping.
	 * @param basePath        path to where the to-be-parsed files live
	 * @param iniSection      {@link Section} with configuration from INI file
	 * @param geneIdentifiers list of gene identifiers to include if non-empty
	 */
	public RefSeqParser(ReferenceDictionary refDict, String basePath, List<String> geneIdentifiers,
						Section iniSection) {
		this.refDict = refDict;
		this.contigDict = refDict.getContigNameToID();

		this.basePath = basePath;
		this.iniSection = iniSection;
		this.geneIdentifiers = geneIdentifiers;
	}

	@Override
	public ImmutableList<TranscriptModel> run() throws TranscriptParseException {
		// Load features from GFF3 file, clustered by the gene they belong to
		final String pathGFF = PathUtil.join(basePath, getINIFileName("gff"));
		Map<String, TranscriptModelBuilder> builders = loadTranscriptModels(pathGFF);

		// Augment information in builders with HGNC mappings
		LOGGER.info("Assigning additional HGNC information to {} transcripts..", builders.size());
		try {
			new TranscriptModelBuilderHGNCExtender(basePath, r -> Lists.newArrayList(r.getEntrezID()),
				TranscriptModelBuilder::getGeneID).run(builders);
		} catch (JannovarException e) {
			throw new UncheckedJannovarException("Problem extending transcripts with HGNC information", e);
		}

		// Use Entrez IDs from RefSeq if no HGNC annotation
		for (TranscriptModelBuilder val : builders.values()) {
			if (val.getAltGeneIDs().isEmpty() && val.getGeneID() != null) {
				LOGGER.debug("Using UCSC Entrez ID {} for transcript {} as HGNC did not provide alternative gene ID",
					val.getGeneID(), val.getAccession());
				val.getAltGeneIDs().put(AltGeneIDType.ENTREZ_ID.toString(), val.getGeneID());
			}
		}

		// Load the FASTA file and assign to the builders.
		final String pathFASTA = PathUtil.join(basePath, getINIFileName("rna"));
		loadFASTA(builders, pathFASTA);

		// Create final list of TranscriptModels.
		ImmutableList.Builder<TranscriptModel> result = new ImmutableList.Builder<>();
		for (Entry<String, TranscriptModelBuilder> entry : builders.entrySet()) {
			TranscriptModelBuilder builder = entry.getValue();
			if (geneIdentifiers == null || geneIdentifiers.isEmpty()) {
				result.add(builder.build());
			} else {
				if (geneIdentifiers.contains(builder.getAccession()) || geneIdentifiers.contains(builder.getGeneID())
					|| !Sets.intersection(ImmutableSet.copyOf(geneIdentifiers),
					ImmutableSet.copyOf(builder.getAltGeneIDs().values())).isEmpty()

				) {
					result.add(builder.build());
				}
			}
		}
		return result.build();
	}

	/**
	 * @param key name of the INI entry
	 * @return file name from INI <code>key</code.
	 */
	private String getINIFileName(String key) {
		return new File(iniSection.get(key)).getName();
	}

	/**
	 * Load FASTA from pathFASTA and set the sequence into builders.
	 *
	 * @throws TranscriptParseException on problems with parsing the FASTA
	 */
	private void loadFASTA(Map<String, TranscriptModelBuilder> builders, String pathFASTA) throws TranscriptParseException {

		// We must remove variants for which we did not find any sequence
		Set<String> missingSequence = new HashSet<>(builders.keySet());

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
				Optional<String> accessionOpt = RefSeqFastaRecordIdFormat.extractAccession(record.getID());
				if (!accessionOpt.isPresent()) {
					continue;
				}
				String accession = accessionOpt.get();
				final TranscriptModelBuilder builder = builders.get(accession);
				if (builder == null) {
					// This is not a warning as we observed this for some records regularly
					LOGGER.debug("ID {} from FASTA did not map to transcript", accession);
					continue;
				}

				assert missingSequence.contains(accession);
				missingSequence.remove(accession);

				builder.setAccession(accession);
				builder.setSequence(record.getSequence());
				LOGGER.debug("Found sequence for transcript {}", builder.getAccession());
			}
		} catch (IOException e) {
			throw new TranscriptParseException("Problem with reading FASTA file", e);
		}

		LOGGER.info("Ignoring {} transcripts without sequence.", missingSequence.size());
		for (String key : missingSequence) {
			LOGGER.debug("--> {}", key);
			builders.remove(key);
		}

		LOGGER.info("Successfully processed {} transcripts with sequence.", builders.size());
	}

	/**
	 * Convert list of GFF records into a mapping from transcript id to TranscriptModelBuilder
	 * <p>
	 * Then, we only have to assign the sequence into the TranscriptModelBuilder objects to get the appropriate
	 * TranscriptModel objects.
	 * <p>
	 * The TranscriptModelBuilder objects will have the a "Name" attribute of the mRNA set as the sequence, so we can
	 * use this for assigning FASTA sequence to the builders.
	 */
	private Map<String, TranscriptModelBuilder> loadTranscriptModels(String pathGFF) throws TranscriptParseException {
		LOGGER.info("Loading feature records");
		// transcriptId: TranscriptModelBuilder
		// Open file using GFFParser
		GFFParser parser;
		try {
			parser = new GFFParser(new File(pathGFF));
		} catch (IOException e) {
			throw new TranscriptParseException("Problem opening GFF file", e);
		}
		// cached values from parsed records
		Map<String, TranscriptModelBuilder> parentIdToTranscriptModels = new HashMap<>(200000);
		Map<String, List<String>> transcriptIdToParentIds = new HashMap<>(parentIdToTranscriptModels.size());

		Set<String> wantedTypes = Sets.newHashSet("exon", "CDS", "stop_codon");
		// Read file record by record, mapping features to genes
		int numRecords = 0;
		try {
			FeatureRecord record;
			while ((record = parser.next()) != null) {
				numRecords++;
				String transcriptId = record.getAttributes().get("transcript_id");
				// n.b. - in the RefSeq data the exon and CDS lines are linked by the Parent id as
				// the transcriptId is only stated for the exon and the proteinId is used as the CDS identifier.
				// a further complication is that some transcriptIds are used twice by different ParentIds - those in
				// the pseudoautosomal regions and some others
				String parentId = record.getAttributes().get("Parent");
				if (parentId != null && contigDict.containsKey(record.getSeqID()) && wantedTypes.contains(record.getType())) {
					if (!parentIdToTranscriptModels.containsKey(parentId)) {
						// create new TranscriptBuilder
						TranscriptModelBuilder builder = createNewTranscriptModelBuilder(record, transcriptId);
						parentIdToTranscriptModels.put(parentId, builder);
						updateTransciptIdToParentIds(transcriptIdToParentIds, transcriptId, parentId);
					} else {
						// update existing
						TranscriptModelBuilder builder = parentIdToTranscriptModels.get(parentId);
						updateExonsTxRegionsAndCds(record, builder);
					}
				}
			}
		} catch (IOException e) {
			throw new TranscriptParseException("Problem parsing GFF file", e);
		}

		Map<String, TranscriptModelBuilder> transcriptIdToTranscriptModelBuilders = mapTranscriptIdsToTranscriptModels(parentIdToTranscriptModels, transcriptIdToParentIds);

		LOGGER.info("Parsed {} GFF records as {} TranscriptModels", numRecords, transcriptIdToTranscriptModelBuilders.size());

		return transcriptIdToTranscriptModelBuilders;
	}

	/**
	 * @return <code>true</code> if only curated entries are to be returned
	 */
	private boolean onlyCurated() {
		return checkFlagInSection(iniSection.fetch("onlyCurated"));
	}

	/**
	 * @return <code>true</code> if only curated entries are to be returned
	 */
	private boolean preferPARTranscriptsOnChrX() {
		return checkFlagInSection(iniSection.fetch("preferPARTranscriptsOnChrX"));
	}

	private static boolean checkFlagInSection(String value) {
		if (value == null)
			return false;
		value = value.toLowerCase();
		ImmutableList<String> list = ImmutableList.of("true", "1", "yes");
		for (String s : list)
			if (s.equals(value))
				return true;
		return false;
	}

	private void updateTransciptIdToParentIds(Map<String, List<String>> transciptIdToParentIds, String transcriptId, String parentId) {
		if (transcriptId != null) {
			if (transciptIdToParentIds.containsKey(transcriptId)) {
				List<String> parentIds = transciptIdToParentIds.get(transcriptId);
				if (!parentIds.contains(parentId)) {
					LOGGER.debug("Adding new parentId {} for transcript {}", parentId, transcriptId);
					parentIds.add(parentId);
				}
			} else {
				transciptIdToParentIds.put(transcriptId, Lists.newArrayList(parentId));
			}
		}
	}

	private TranscriptModelBuilder createNewTranscriptModelBuilder(FeatureRecord record, String transcriptId) {
		TranscriptModelBuilder builder = new TranscriptModelBuilder();
		// Parse out the simple attributes from the mRNA record
		Strand strand = parseStrand(record);
		builder.setStrand(strand);
		builder.setAccession(transcriptId);
		builder.setTxVersion(record.getAttributes().get("transcript_version"));
		builder.setGeneID(parseGeneID(record));
		builder.setGeneSymbol(record.getAttributes().get("gene"));
		builder.setSequence(transcriptId);

		updateExonsTxRegionsAndCds(record, builder);

		return builder;
	}

	@Nullable
	private String parseGeneID(FeatureRecord featureRecord) {
		// Dbxref=GeneID:6010,HGNC:10012,HPRD:01584,MIM:180380;
		String dbxrefs = featureRecord.getAttributes().get("Dbxref");
		for (String token : Splitter.on(',').split(dbxrefs)) {
			List<String> keyValue = Splitter.on(':').limit(2).splitToList(token);
			if (keyValue.size() == 2 && keyValue.get(0).equals("GeneID")) {
				return keyValue.get(1);
			}
		}

		return null;
	}

	private void updateExonsTxRegionsAndCds(FeatureRecord record, TranscriptModelBuilder builder) {
		Strand strand = parseStrand(record);
		if (record.getType().equals("exon")) {
			GenomeInterval exon = buildGenomeInterval(record, strand);
			GenomeInterval txRegion = updateGenomeInterval(exon, builder.getTXRegion());
			builder.setTXRegion(txRegion);
			builder.addExonRegion(exon);
		} else if ("CDS".equals(record.getType()) || "stop_codon".equals(record.getType())) {
			GenomeInterval cds = buildGenomeInterval(record, strand);
			GenomeInterval cdsRegion = updateGenomeInterval(cds, builder.getCDSRegion());
			builder.setCDSRegion(cdsRegion);
		}
	}

	private GenomeInterval updateGenomeInterval(GenomeInterval latest, GenomeInterval existing) {
		return existing == null ? latest : existing.union(latest);
	}

	private Strand parseStrand(FeatureRecord record) {
		return (record.getStrand() == FeatureRecord.Strand.FORWARD) ? Strand.FWD : Strand.REV;
	}

	private GenomeInterval buildGenomeInterval(FeatureRecord record, Strand strand) {
		int chrom = contigDict.get(record.getSeqID());
		GenomeInterval interval = new GenomeInterval(refDict, Strand.FWD, chrom, record.getBegin(), record.getEnd());
		// CAUTION! GFF record begin and end are listed using the FORWARD strand, so this needs adjusting-post build
		// rather than being supplied in the constructor.
		return interval.withStrand(strand);
	}

	private Map<String, TranscriptModelBuilder> mapTranscriptIdsToTranscriptModels(Map<String, TranscriptModelBuilder> parentIdToTranscriptModels, Map<String, List<String>> transciptIdToParentIds) {
		Map<String, TranscriptModelBuilder> parentIdToTranscriptModelsWithTxRegion = mapParentIdsToTranscriptModelsWithTxRegion(parentIdToTranscriptModels);
		return mapNonRedundantTranscriptIdsToTranscriptModelBuilder(parentIdToTranscriptModelsWithTxRegion, transciptIdToParentIds);
	}

	private Map<String, TranscriptModelBuilder> mapParentIdsToTranscriptModelsWithTxRegion(Map<String, TranscriptModelBuilder> results) {
		Map<String, TranscriptModelBuilder> transcriptModelsWithTxRegion = new HashMap<>(results.size());
		boolean onlyCurated = onlyCurated();

		results.forEach((parentId, transcriptModelBuilder) -> {
			GenomeInterval txRegion = transcriptModelBuilder.getTXRegion();
			if (txRegion == null) {
				// Only warn if a transcript and not a gene, we only allow exons to be parts of genes as this is
				// observed in RefSeq
				LOGGER.debug("No transcript region for {}; skipping", parentId);
			} else {
				if (transcriptModelBuilder.getCDSRegion() == null) {
					transcriptModelBuilder.setCDSRegion(new GenomeInterval(txRegion.getGenomeBeginPos(), 0));
				}
				String transcriptId = transcriptModelBuilder.getAccession();
				if (onlyCurated && (transcriptId == null || transcriptId.startsWith("X"))) {
					LOGGER.debug("Skipping non-curated transcript {}", transcriptId);
				} else {
					// The original accession was set to the was something like 'rna58569', but should be 'XM_005255624.1'
					// 'sequence' here is actually the transcript_id from the GFF file
					transcriptModelsWithTxRegion.put(parentId, transcriptModelBuilder);
				}
			}
		});
		return transcriptModelsWithTxRegion;
	}

	private Map<String, TranscriptModelBuilder> mapNonRedundantTranscriptIdsToTranscriptModelBuilder(Map<String, TranscriptModelBuilder> transcriptModelsWithTxRegion, Map<String, List<String>> transciptIdToParentIds) {

		Map<String, TranscriptModelBuilder> transcriptIdToTranscriptModelBuilders = new HashMap<>(transcriptModelsWithTxRegion
			.size());

		boolean assignPseudoAutosomalTranscriptsToX = preferPARTranscriptsOnChrX();
		if (assignPseudoAutosomalTranscriptsToX) {
			LOGGER.info("Pseudoautosomal transcripts will be assigned to X chromosome");
		}

		int duplicatedTranscriptIdCount = 0;
		for (Entry<String, List<String>> entry : transciptIdToParentIds.entrySet()) {
			String transcriptId = entry.getKey();
			List<String> parentIds = entry.getValue();
			if (parentIds.isEmpty()) {
				continue;
			}
			if (parentIds.size() == 1) {
				TranscriptModelBuilder transcriptModelBuilder = transcriptModelsWithTxRegion.get(parentIds.get(0));
				transcriptIdToTranscriptModelBuilders.put(transcriptId, transcriptModelBuilder);
			} else {
				TranscriptModelBuilder existingTranscriptBuilder = transcriptModelsWithTxRegion.get(parentIds.get(0));
				TranscriptModelBuilder updatedTranscriptBuilder = transcriptModelsWithTxRegion.get(parentIds.get(1));
				int currentChromosome = existingTranscriptBuilder.getTXRegion().getChr();
				int newChromosome = updatedTranscriptBuilder.getTXRegion().getChr();
				// Pseudoautosomal gene
				if (currentChromosome == 23 && newChromosome == 24) {
					// e.g. https://www.ncbi.nlm.nih.gov/nuccore/NM_001636
					TranscriptModelBuilder preferred = assignPseudoAutosomalTranscriptsToX ? existingTranscriptBuilder : updatedTranscriptBuilder;
					LOGGER.info("Assigning pseudoautosomal gene {} transcript {} to chromsome {}",
						preferred.getGeneSymbol(), preferred.getAccession(), preferred.getTXRegion().getChr());
					transcriptIdToTranscriptModelBuilders.put(transcriptId, preferred);
				} else {
					duplicatedTranscriptIdCount++;
					// This is a tricky call - there are about 30 transcripts with duplicated transcriptIds due to imperfect
					// alignment to the genomic reference
					LOGGER.warn("Transcript {} has {} possible transcript models - using the longest model", transcriptId, parentIds
						.size());
					List<TranscriptModelBuilder> possibleModels = parentIds.stream()
						.map(transcriptModelsWithTxRegion::get)
						.collect(toList());
					TranscriptModelBuilder longestCds = findLongestTranscriptRegion(possibleModels);
					transcriptIdToTranscriptModelBuilders.put(transcriptId, longestCds);
				}
			}
		}
		if (duplicatedTranscriptIdCount != 0) {
			LOGGER.warn("{} duplicated transcript ids", duplicatedTranscriptIdCount);
		}
		return transcriptIdToTranscriptModelBuilders;
	}

	private TranscriptModelBuilder findLongestTranscriptRegion(List<TranscriptModelBuilder> possibleModels) {
		TranscriptModelBuilder longestCds = possibleModels.get(0);
		for (TranscriptModelBuilder current : possibleModels) {
			if (current.getTXRegion().length() > longestCds.getTXRegion().length()) {
				longestCds = current;
			}
		}
		return longestCds;
	}
}
