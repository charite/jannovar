package de.charite.compbio.jannovar.impl.parse.ensembl;

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
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

// TODO(holtgrewe): Factor out common paths with RefSeqParser
// TODO(holtgrewe): stop codon part of CDS here?
// TODO(holtgrewe): linking to Entrez ID is done through HGNC, in the case that there is an Entrez ID but not HGNC entry, we cannot annotate ENSEMBL tx yet

/**
 * Parsing of ENSEMBL GTF files
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class EnsemblParser implements TranscriptParser {

	/**
	 * the logger object to use
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(EnsemblParser.class);

	/**
	 * Path to the {@link ReferenceDictionary} to use for name/id and id/length mapping
	 */
	private final ReferenceDictionary refDict;
	/*
	 * Contig name mappings
	 */
	private final ImmutableMap<String, Integer> contigDict;

	/**
	 * Path to directory where the to-be-parsed files live
	 */
	private final String basePath;

	/**
	 * INI {@link Section} from the configuration.
	 */
	private final Section iniSection;

	/**
	 * List of gene identifiers of genes to include, if non-empty.
	 */
	private final List<String> geneIdentifiers;

	/**
	 * @param refDict path to {@link ReferenceDictionary} to use for name/id and id/length mapping.
	 * @param basePath path to where the to-be-parsed files live
	 * @param iniSection {@link Section} with configuration from INI file
	 * @param geneIdentifiers list of gene identifiers to include if non-empty
	 */
	public EnsemblParser(ReferenceDictionary refDict, String basePath, List<String> geneIdentifiers, Section iniSection) {
		this.refDict = refDict;
		this.contigDict = refDict.getContigNameToID();
		this.basePath = basePath;
		this.iniSection = iniSection;
		this.geneIdentifiers = geneIdentifiers;
	}

	@Override
	public ImmutableList<TranscriptModel> run() throws TranscriptParseException {
		// Load features from GTF file, clustered by the gene they belong to
		final String pathGTF = PathUtil.join(basePath, getINIFileName("gtf"));
		Map<String, TranscriptModelBuilder> builders = loadTranscriptModels(pathGTF);

		// Load mappings that allows mapping from ENSG to HGNC ID.
		LOGGER.info("Loading ENSEMBL to HGNC mappings...");
		final Map<String, String> ensgToHgnc = loadEngsToHgnc();

		// Load files that give the ENSG to Entrez ID mapping that comes directly from ENSEMBL.
		LOGGER.info("Loading ENSEMBL to ENTREZ mappings...");
		final Map<String, String> ensgToEntrez = loadEngsToEntrez();

		// Augment information in builders with
		LOGGER.info("Assigning additional HGNC information to transcripts..");
		try {
			new TranscriptModelBuilderHGNCExtender(
				basePath,
				r -> Lists.newArrayList(r.getHgncID()),
				tx -> "HGNC:" + ensgToHgnc.get(tx.getGeneID())
			).run(builders);
		} catch (JannovarException e) {
			throw new UncheckedJannovarException(
				"Problem extending transcripts with HGNC information", e);
		}

		// Use Entrez IDs from RefSeq if no HGNC annotation
		for (TranscriptModelBuilder val : builders.values()) {
			if ("ENSG00000272333".equals(val.getGeneID())) {
				System.err.println("===>>>===>>> RARKENDARL");
			}
			if (val.getAltGeneIDs().isEmpty() && val.getGeneID() != null) {
				if (ensgToEntrez.containsKey(val.getGeneID())) {
					final String entrezGeneId = ensgToEntrez.get(val.getGeneID());
					LOGGER.info(
						"ENSEMBL Gene {} not known to HGNC, annotating with ENTREZ_ID := {} for additional IDs",
						val.getGeneID(), entrezGeneId);
					val.getAltGeneIDs().put(AltGeneIDType.ENTREZ_ID.toString(), entrezGeneId);
				}
				LOGGER.info(
					"ENSEMBL Gene {} not known to HGNC, annotating with ENSEMBL_GENE_ID := {} for additional IDs",
					val.getGeneID(), val.getGeneID());
				val.getAltGeneIDs().put(AltGeneIDType.ENSEMBL_GENE_ID.toString(), val.getGeneID());
			}
			if ("ENSG00000272333".equals(val.getGeneID())) {
				System.err.println("===<<<===<<< RARKENDARL");
			}
		}

		// Load the FASTA file and assign to the builders.
		final String pathFASTA = PathUtil.join(basePath, getINIFileName("cdna"));
		LOGGER.info("Adding sequence information from cdna FASTA...");
		loadFASTA(builders, pathFASTA);

		LOGGER.info("Finalising TranscriptModels...");
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
		ImmutableList<TranscriptModel> transcriptModels = result.build();
		LOGGER.info("Built {} TranscriptModels", transcriptModels.size());
		return transcriptModels;
	}

	/**
	 * @param key name of the INI entry
	 * @return file name from INI <code>key</code.
	 */
	private String getINIFileName(String key) {
		return new File(iniSection.get(key)).getName();
	}

	private Map<String, String> loadEngsToEntrez() throws TranscriptParseException {
		// Read mapping from ENSG to MySQL key
		final String pathTableGeneMain = PathUtil.join(basePath, getINIFileName("table_gene_main"));
		final Map<String, String> ensgToKey = new HashMap<>();
		try (
			FileInputStream fis = new FileInputStream(pathTableGeneMain);
			BZip2CompressorInputStream bz2is = pathTableGeneMain.endsWith(".gz.bz2") ?
				new BZip2CompressorInputStream(fis) : null;
			GZIPInputStream gzis = new GZIPInputStream(
				pathTableGeneMain.endsWith(".gz.bz2") ? bz2is : fis);
			InputStreamReader reader = new InputStreamReader(gzis);
			BufferedReader bufReader = new BufferedReader(reader)
		) {
			String line;
			while ((line = bufReader.readLine()) != null) {
				final String[] arr = line.trim().split("\t");
				ensgToKey.put(arr[6], arr[1]);
			}
		} catch (Exception e) {
			throw new TranscriptParseException(
				"Could not parse ENSEMBL mapping files ENSG to Entrez", e);
		}

		// Read mapping from MySQL key to Entrez ID
		final String pathTableEntrezGene = PathUtil
			.join(basePath, getINIFileName("table_entrezgene"));
		final Map<String, String> keyToEntrez = new HashMap<>();
		try (
			FileInputStream fis = new FileInputStream(pathTableEntrezGene);
			BZip2CompressorInputStream bz2is = pathTableEntrezGene.endsWith(".gz.bz2")
				? new BZip2CompressorInputStream(fis) : null;
			GZIPInputStream gzis = new GZIPInputStream(
				pathTableEntrezGene.endsWith(".gz.bz2") ? bz2is : fis);
			InputStreamReader reader = new InputStreamReader(gzis);
			BufferedReader bufReader = new BufferedReader(reader)
		) {
			String line;
			while ((line = bufReader.readLine()) != null) {
				final String[] arr = line.trim().split("\t");
				if (!arr[0].equals("\\N") && !arr[3].equals("\\N")) {
					keyToEntrez.put(arr[0], arr[3]);
				}
			}
		} catch (Exception e) {
			throw new TranscriptParseException(
				"Could not parse ENSEMBL mapping files ENSG to Entrez", e);
		}

		// Build mapping from ENSG to Entrez ID
		final Map<String, String> result = new HashMap<>();
		for (Entry<String, String> entry1 : ensgToKey.entrySet()) {
			final String ensg = entry1.getKey();
			final String key = entry1.getValue();
			final String entrezId = keyToEntrez.get(key);
			if (entrezId == null) {
				LOGGER.warn("Found no Entrez ID identifier for ENSG: ", new Object[]{ensg});
			} else {
				result.put(ensg, entrezId);
			}
		}
		return result;
	}

	private Map<String, String> loadEngsToHgnc() throws TranscriptParseException {
		// Read mapping from ENSG to MySQL key
		final String pathTableGeneMain = PathUtil.join(basePath, getINIFileName("table_gene_main"));
		final Map<String, String> ensgToKey = new HashMap<>();
		try (
			FileInputStream fis = new FileInputStream(pathTableGeneMain);
			BZip2CompressorInputStream bz2is = pathTableGeneMain.endsWith(".gz.bz2") ?
				new BZip2CompressorInputStream(fis) : null;
			GZIPInputStream gzis = new GZIPInputStream(pathTableGeneMain.endsWith(".gz.bz2") ? bz2is : fis);
			InputStreamReader reader = new InputStreamReader(gzis);
			BufferedReader bufReader = new BufferedReader(reader)
		) {
			String line;
			while ((line = bufReader.readLine()) != null) {
				final String[] arr = line.trim().split("\t");
				ensgToKey.put(arr[6], arr[1]);
			}
		} catch (Exception e) {
			throw new TranscriptParseException("Could not parse ENSEMBL mapping files", e);
		}

		// Read mapping from MySQL key to HGNC ID
		final String pathTableHgnc = PathUtil.join(basePath, getINIFileName("table_hgnc"));
		final Map<String, String> keyToHgnc = new HashMap<>();
		try (
			FileInputStream fis = new FileInputStream(pathTableHgnc);
			BZip2CompressorInputStream bz2is = pathTableHgnc.endsWith(".gz.bz2") ? new BZip2CompressorInputStream(fis) : null;
			GZIPInputStream gzis = new GZIPInputStream(pathTableHgnc.endsWith(".gz.bz2") ? bz2is : fis);
			InputStreamReader reader = new InputStreamReader(gzis);
			BufferedReader bufReader = new BufferedReader(reader)
		) {
			String line;
			while ((line = bufReader.readLine()) != null) {
				final String[] arr = line.trim().split("\t");
				if (!arr[0].equals("\\N") && !arr[3].equals("\\N")) {
					keyToHgnc.put(arr[0], arr[3]);
				}
			}
		} catch (Exception e) {
			throw new TranscriptParseException("Could not parse ENSEMBL mapping files", e);
		}

		// Build mapping from ENSG to HGNC identifier
		final Map<String, String> result = new HashMap<>();
		for (Entry<String, String> entry1 : ensgToKey.entrySet()) {
			final String ensg = entry1.getKey();
			final String key = entry1.getValue();
			final String hgnc = keyToHgnc.get(key);
			if (hgnc == null) {
				LOGGER.debug("Found no HGNC identifier for ENSG: {}", ensg);
			} else {
				result.put(ensg, hgnc);
			}
		}
		return result;
	}

	/**
	 * Load FASTA from pathFASTA and set the sequence into builders.
	 *
	 * @throws TranscriptParseException on problems with parsing the FASTA
	 */
	private void loadFASTA(Map<String, TranscriptModelBuilder> builders, String pathFASTA)
		throws TranscriptParseException {

		// First, build mapping from RNA accession to builder
		Map<String, TranscriptModelBuilder> txMap = new HashMap<>();
		for (Entry<String, TranscriptModelBuilder> entry : builders.entrySet()) {
			TranscriptModelBuilder trnscpModelBuilder = entry.getValue();
			txMap.put(trnscpModelBuilder.getSequence(), trnscpModelBuilder);
			if (trnscpModelBuilder.getTxVersion() != null) {
				txMap.put(trnscpModelBuilder.getSequence() + "." + trnscpModelBuilder.getTxVersion(), trnscpModelBuilder);
			}
		}

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
				final String accession = record.getID();
				final TranscriptModelBuilder builder = txMap.get(accession);
				if (builder == null) {
					// This is not a warning as we observed this for some records regularly
					LOGGER.debug("ID {} from FASTA did not map to transcript", accession);
					continue;
				}

				assert missingSequence.contains(builder.getAccession());
				missingSequence.remove(builder.getAccession());

				builder.setAccession(builder.getSequence());
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
	 * Then, we only have to assign the sequence into the TranscriptModelBuilder objects to get the
	 * appropriate TranscriptModel objects.
	 * <p>
	 * The TranscriptModelBuilder objects will have the a "Name" attribute of the mRNA set as the
	 * sequence, so we can use this for assigning FASTA sequence to the builders.
	 */
	private Map<String, TranscriptModelBuilder> loadTranscriptModels(String pathGFF) throws TranscriptParseException {
		LOGGER.info("Loading feature records");
		// transcriptId: TranscriptModelBuilder
		Map<String, TranscriptModelBuilder> results = new HashMap<>(200000);
		// Open file using GFFParser
		GFFParser parser;
		try {
			parser = new GFFParser(new File(pathGFF));
		} catch (IOException e) {
			throw new TranscriptParseException("Problem opening GFF file", e);
		}

		Set<String> wantedTypes = Sets.newHashSet("exon", "CDS", "stop_codon");
		// Read file record by record, mapping features to genes
		//
		// This will only work properly if the full path of feature objects from the current feature has already been
		// read. Otherwise, we will need some more fancy parsing.
		int numRecords = 0;
		try {
			FeatureRecord record;
			while ((record = parser.next()) != null) {
				numRecords++;
				// filter these out here as they are only discarded later
				if (record.getAttributes()
					.get("transcript_id") != null && contigDict.containsKey(record.getSeqID()) && wantedTypes.contains(record
					.getType())) {
					LOGGER.debug("Loaded GFF record {}", record);
					String transcriptId = record.getAttributes().get("transcript_id");
					if (!results.containsKey(transcriptId)) {
						// create new TranscriptBuilder
						TranscriptModelBuilder builder = createNewTranscriptModelBuilder(record, transcriptId);
						results.put(transcriptId, builder);
					} else {
						// update existing
						TranscriptModelBuilder builder = results.get(transcriptId);
						updateExonsTxRegionsAndCds(record, builder);
					}
				}

			}
		} catch (IOException e) {
			throw new TranscriptParseException("Problem parsing GFF file", e);
		}

		Map<String, TranscriptModelBuilder> transcriptModelsWithTxRegion = getTranscriptModelsWithTxRegion(results);
		LOGGER.info("Parsed {} GFF records as {} TranscriptModels", numRecords, transcriptModelsWithTxRegion.size());

		return transcriptModelsWithTxRegion;
	}

	private TranscriptModelBuilder createNewTranscriptModelBuilder(FeatureRecord record, String transcriptId) {
		TranscriptModelBuilder builder = new TranscriptModelBuilder();
		// Parse out the simple attributes from the mRNA record
		Strand strand = parseStrand(record);
		builder.setStrand(strand);
		builder.setAccession(transcriptId);
		builder.setTxVersion(record.getAttributes().get("transcript_version"));
		builder.setGeneID(record.getAttributes().get("gene_id"));
		builder.setGeneSymbol(record.getAttributes().get("gene_name"));
		builder.setSequence(transcriptId);

		updateExonsTxRegionsAndCds(record, builder);

		return builder;
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

	private Map<String, TranscriptModelBuilder> getTranscriptModelsWithTxRegion(Map<String, TranscriptModelBuilder> results) {
		Map<String, TranscriptModelBuilder> transcriptModelsWithTxRegion = new HashMap<>(results.size());

		results.forEach((transcriptId, transcriptModelBuilder) -> {
			GenomeInterval txRegion = transcriptModelBuilder.getTXRegion();
			if (txRegion == null) {
				// Only warn if a transcript and not a gene, we only allow exons to be parts of genes as this is
				// observed in RefSeq
				LOGGER.warn("No transcript region for {}; skipping", transcriptId);
			} else {
				if (transcriptModelBuilder.getCDSRegion() == null) {
					transcriptModelBuilder.setCDSRegion(new GenomeInterval(txRegion.getGenomeBeginPos(), 0));
				}
				transcriptModelsWithTxRegion.put(transcriptId, transcriptModelBuilder);
			}
		});
		return transcriptModelsWithTxRegion;
	}

}
