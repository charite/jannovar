package de.charite.compbio.jannovar.impl.parse.refseq;

import static java.util.stream.Collectors.toList;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import de.charite.compbio.jannovar.impl.util.DNAUtils;
import de.charite.compbio.jannovar.impl.util.PathUtil;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder.AlignmentPart;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		loadMitochondrialFASTA(builders);

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
	 * Load chrMT sequence (if available) and assign into chrMT builders.
	 *
	 * @param builders The transcript builders to update.
	 * @param pathFasta The path to the fasta file.
	 *
	 * @throws TranscriptParseException on problems with parsing the FASTA.
	 */
	private void loadMitochondrialFASTA(Map<String, TranscriptModelBuilder> builders)
		throws TranscriptParseException {
		if (!refDict.getContigNameToID().containsKey("chrMT")) {
			LOGGER.info("The genome does not have a chrMT, skipping.");
			return;
		} else if (!iniSection.containsKey("faMT")) {
			LOGGER.warn("Key for chrMT FASTA File does not exist, skipping.");
			return;
		}

		final String pathFasta = PathUtil.join(basePath, getINIFileName("faMT"));
		if (!new File(pathFasta).exists()) {
			LOGGER.warn("The chrMT FASTA File {} does not exist, skipping.", new Object[]{ pathFasta });
			return;
		}


		final int idMT = refDict.getContigNameToID().get("chrMT");

		String chrMT = "";

		FASTAParser fastaParser;
		try {
			fastaParser = new FASTAParser(new File(pathFasta));
		} catch (IOException e) {
			throw new TranscriptParseException("Problem with opening FASTA file", e);
		}
		FASTARecord record;
		try {
			while ((record = fastaParser.next()) != null) {
				chrMT = record.getSequence();
				break;
			}
		} catch (IOException e) {
			throw new TranscriptParseException("Problem with reading FASTA file", e);
		}

		int count = 0;
		for (TranscriptModelBuilder builder : builders.values()) {
			if (builder.getTXRegion().getChr() == idMT) {
				GenomeInterval txRegion = builder.getTXRegion().withStrand(Strand.FWD);
				String seq = chrMT.substring(txRegion.getBeginPos(), txRegion.getEndPos());
				if (builder.getTXRegion().getStrand() == Strand.REV) {
					seq = DNAUtils.reverseComplement(seq);
				}
				builder.setSequence(seq);
				count += 1;
			}
		}

		LOGGER.info("Successfully assigned sequence to {} chrMT transcripts.", new Object[] { count });
	}

	/**
	 * @param key name of the INI entry
	 * @return file name from INI <code>key</code.
	 */
	private String getINIFileName(String key) {
		String name = new File(iniSection.get(key)).getName();
		if (name.contains("?")) {
			name = name.split("\\?")[0];
		}
		return name;
	}

	/**
	 * Load FASTA from pathFASTA and set the sequence into builders.
	 *
	 * @throws TranscriptParseException on problems with parsing the FASTA
	 */
	private void loadFASTA(Map<String, TranscriptModelBuilder> builders, String pathFASTA) throws TranscriptParseException {
		// We must remove variants for which we did not find any sequence.  The only exception
		// is chrMT if we could load the corresponding sequence.
		Set<String> missingSequence = new HashSet<>(builders.keySet());

		if (refDict.getContigNameToID().containsKey("chrMT")) {
			final int idMT = refDict.getContigNameToID().get("chrMT");
			for (TranscriptModelBuilder builder : builders.values()) {
				if (builder.getTXRegion().getChr() == idMT) {
					if (builder.getAccession().contains(builder.getAccession()) &&
							!builder.getAccession().equals(builder.getSequence())) {
						missingSequence.remove(builder.getAccession());
					}
				}
			}
		}

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
		boolean onlyCurated = onlyCurated();

		// Some exons do not have a gene name in mm9 and rat releases.
		// Therefore we select collect the name from the gene.
		Map<String, String> entrezMap = new HashMap<>();
		// For chrMT: mapping from Entrez gene ID to the gene synonym with "MT" prefix.  We use this
		// as RefSeq does not contain any transcript records for mitochondrial genes.
		Map<String, String> mtEntrezMap = new HashMap<>();

		// Read file record by record, mapping features to genes
		int numRecords = 0;
		try {
			FeatureRecord record;
			while ((record = parser.next()) != null) {
				numRecords++;
				String transcriptId = record.getAttributes().get("transcript_id");
				if (onlyCurated && transcriptId != null && transcriptId.startsWith("X")) {
					LOGGER.debug("Skipping non-curated transcript {}", transcriptId);
					continue;
				}

				// Whether or not the record is on chrMT.
				boolean isMT = contigDict.containsKey("chrMT") &&
					contigDict.get("chrMT").equals(contigDict.get(record.getSeqID()));
				if ("gene".equals(record.getType())) {

					String entrezId = parseGeneID(record);

					String symbol = null;
					symbol = record.getAttributes().get("gene");
					entrezMap.put(entrezId, symbol);

					// Register chrMT Entrez IDs to the "MT..." gene name.
					if (isMT) {
						if (record.getAttributes().get("gene_synonym") == null) {
							symbol = record.getAttributes().get("gene");
						} else {
							for (String entry : record.getAttributes().get("gene_synonym").split(",")) {
								if (entry.startsWith("MT")) {
									symbol = entry;
								}
							}
						}

						mtEntrezMap.put(entrezId, symbol);
					}
				}


				// Handle the records describing the transcript structure.
				//
				// n.b. - in the RefSeq data the exon and CDS lines are linked by the Parent id as
				// the transcriptId is only stated for the exon and the proteinId is used as the CDS identifier.
				// a further complication is that some transcriptIds are used twice by different ParentIds - those in
				// the pseudoautosomal regions and some others.
				//
				// Also, on chrMT, RefSeq does not contain any exons for the protein-coding genes but only the CDS.
				String parentId = record.getAttributes().get("Parent");
				if (parentId != null && contigDict.containsKey(record.getSeqID()) && wantedTypes.contains(record.getType())) {
					final TranscriptModelBuilder builder;
					if (!parentIdToTranscriptModels.containsKey(parentId)) {
						if (record.getType().equals("cDNA_match")) {
							throw new TranscriptParseException("Saw cDNA_match before the transcript for " + record);
						}
						// create new TranscriptBuilder
						builder = createNewTranscriptModelBuilder(record, transcriptId, entrezMap);
						parentIdToTranscriptModels.put(parentId, builder);

						// On chrMT, we use the gene symbol (if available with prefix MT) as the transcript.
						if (isMT) {
							if ("CDS".equals(record.getType())) {
								// Fixup coding exon, need to set transcript and exon region manually.
								builder.setTXRegion(builder.getCDSRegion());
								builder.addExonRegion(builder.getCDSRegion());
								builder.getAltGeneIDs().put("protein_id", record.getAttributes().get("protein_id"));
							}

							String entrezId = parseGeneID(record);

//							// skip on chrMT some tRNA that do not have any Dbxref. This happens in mm9 and rat v6
//							if (record.getAttributes().get("gbkey") == "tRNA" && entrezId == null) {
//								continue;
//							}

							transcriptId = mtEntrezMap.get(entrezId);
							builder.setAccession(transcriptId);
						}

						// this method is used to track the rare cases of transcriptIds mapping to multiple parentIds
						updateTranscriptIdToParentIds(transcriptIdToParentIds, transcriptId, parentId);
					} else {
						// update existing
						builder = parentIdToTranscriptModels.get(parentId);
						updateExonsTxRegionsCdsAndCdnaMatch(record, builder);
					}

					if (record.getAttributes().containsKey("Note")) {
						if (record.getAttributes().get("Note").contains("substitution")) {
							builder.setHasSubstitutions(true);
						}
						if (record.getAttributes().get("Note").contains("indel")) {
							builder.setHasIndels(true);
						}
					}
				}
				// Handle the cDNA_match type.
				if ("cDNA_match".equals(record.getType())) {
					final String target[] = record.getAttributes().get("Target").split(" ");
					if (!"+".equals(target[3])) {
						throw new TranscriptParseException(
							"Can only handle Target on strand '+' for cDNA_match: " + record);
					}

					final String targetTxId = target[0];
					final int txBeginPos = Integer.parseInt(target[1]) - 1;
					final int txEndPos = Integer.parseInt(target[2]);
					final int refBeginPos = record.getBegin();
					final int refEndPos = record.getEnd();
					final String gapStr;
					if ((refEndPos - refBeginPos != txEndPos - txBeginPos)
						&& record.getAttributes().get("Gap") == null) {
						throw new TranscriptParseException(
							"ref len != tx len but no gap string: " + record);
					} else {
						if (record.getAttributes().get("Gap") == null) {
							gapStr = "M" + (txEndPos - txBeginPos);
						} else {
							gapStr = record.getAttributes().get("Gap");
						}
					}

					final List<String> parentIds = transcriptIdToParentIds.get(targetTxId);
					if (parentIds != null) {
						for (String builderParentId : parentIds) {
							TranscriptModelBuilder builder = parentIdToTranscriptModels
								.get(builderParentId);
							builder.getAlignmentParts().add(
								new AlignmentPart(refBeginPos, refEndPos, txBeginPos, txEndPos,
									gapStr));
						}
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

	/**
	 * @return {@code true} for allowing empty CDS in transcripts starting with {@code NM_}.
	 */
	private boolean allowNonCodingNm() {
		return checkFlagInSection(iniSection.fetch("allowNonCodingNm"));
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

	private void updateTranscriptIdToParentIds(Map<String, List<String>> transciptIdToParentIds, String transcriptId, String parentId) {
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

	private TranscriptModelBuilder createNewTranscriptModelBuilder(FeatureRecord record, String transcriptId, Map<String, String> notMtEntrezMap) {
		TranscriptModelBuilder builder = new TranscriptModelBuilder();
		// Parse out the simple attributes from the mRNA record
		Strand strand = parseStrand(record);
		builder.setStrand(strand);
		builder.setAccession(transcriptId);
		builder.setTxVersion(record.getAttributes().get("transcript_version"));
		String geneID = parseGeneID(record);
		builder.setGeneID(geneID);

		String gene = record.getAttributes().get("gene");

		if (gene == null && geneID != null && notMtEntrezMap.containsKey(geneID)) {
			gene = notMtEntrezMap.get(geneID);
		}
		builder.setGeneSymbol(gene);
		builder.setSequence(transcriptId);

		updateExonsTxRegionsCdsAndCdnaMatch(record, builder);

		return builder;
	}

	@Nullable
	private String parseGeneID(FeatureRecord featureRecord) {
		// Dbxref=GeneID:6010,HGNC:10012,HPRD:01584,MIM:180380;
		String dbxrefs = featureRecord.getAttributes().get("Dbxref");
		if (dbxrefs != null) {
			for (String token : Splitter.on(',').split(dbxrefs)) {
				List<String> keyValue = Splitter.on(':').limit(2).splitToList(token);
				if (keyValue.size() == 2 && keyValue.get(0).equals("GeneID")) {
					return keyValue.get(1);
				}
			}
		}

		return null;
	}

	private void updateExonsTxRegionsCdsAndCdnaMatch(FeatureRecord record, TranscriptModelBuilder builder) {
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

		results.forEach((parentId, transcriptModelBuilder) -> {
			GenomeInterval txRegion = transcriptModelBuilder.getTXRegion();
			if (txRegion == null) {
				// Only warn if a transcript and not a gene, we only allow exons to be parts of genes as this is
				// observed in RefSeq
				LOGGER.debug("No transcript region for {}; skipping", parentId);
			} else {
				if (transcriptModelBuilder.getCDSRegion() == null) {
					failIfCdsRegionExpected(transcriptModelBuilder);
					transcriptModelBuilder.setCDSRegion(new GenomeInterval(txRegion.getGenomeBeginPos(), 0));
				}
				transcriptModelsWithTxRegion.put(parentId, transcriptModelBuilder);
			}
		});
		return transcriptModelsWithTxRegion;
	}

	private void failIfCdsRegionExpected(TranscriptModelBuilder transcriptModelBuilder) {
		if (Optional.ofNullable(transcriptModelBuilder.getAccession())
			.map(x -> x.startsWith("NM_") || x.startsWith("XM_"))
			.orElse(false)) {
			final String msg = "No CDS region found for coding transcript '" + transcriptModelBuilder.getAccession() + "'.";
			if (!allowNonCodingNm()) {
				throw new IllegalStateException(msg);
			} else {
				LOGGER.warn(msg);
			}
		}
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
				// transcriptModelsWithTxRegion used the ParentId as the key (something like 'rna58569'), but we need
				// the final map to use the transcriptId e.g. 'XM_005255624.1' as the key
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
					// alignment to the genomic reference.  We chose the one with the best match between alignment from cDNA_match
					// and exons.
					LOGGER.warn("Transcript {} has {} possible transcript models - picking best by cDNA_match", transcriptId, parentIds
						.size());
					List<TranscriptModelBuilder> possibleModels = parentIds.stream()
						.map(transcriptModelsWithTxRegion::get)
						.collect(toList());
					TranscriptModelBuilder bestModel = findBestModelByCdnaMatch(possibleModels);
					transcriptIdToTranscriptModelBuilders.put(transcriptId, bestModel);
				}
			}
		}
		if (duplicatedTranscriptIdCount != 0) {
			LOGGER.warn("{} duplicated transcript ids", duplicatedTranscriptIdCount);
		}
		return transcriptIdToTranscriptModelBuilders;
	}

	private static int countCdnaMatches(TranscriptModelBuilder model) {
		int result = 0;
		int i = 0;
		for (AlignmentPart part : model.getAlignmentParts()) {
			if (i >= model.getExonRegions().size()) {
				continue;
			}
			final GenomeInterval exon = model.getExonRegions().get(i).withStrand(Strand.FWD);

			final int partBegin;
			final int partEnd;

			if (part.refBeginPos < part.refEndPos) {
				partBegin = part.refBeginPos;
				partEnd = part.refEndPos;
			} else {
				partEnd = part.refBeginPos;
				partBegin = part.refEndPos;
			}

			if (exon.getBeginPos() == partBegin && exon.getEndPos() == partEnd) {
				result += 1;
			}

			i += 1;
		}
		return result;
	}

	private TranscriptModelBuilder findBestModelByCdnaMatch(List<TranscriptModelBuilder> possibleModels) {
		TranscriptModelBuilder bestMatch = possibleModels.get(0);
		int bestMatchCount = countCdnaMatches(bestMatch);
		for (TranscriptModelBuilder current : possibleModels) {
			int currentMatchCount = countCdnaMatches(current);
			if (currentMatchCount > bestMatchCount) {
				bestMatch = current;
				bestMatchCount = currentMatchCount;
			}
		}
		return bestMatch;
	}
}
