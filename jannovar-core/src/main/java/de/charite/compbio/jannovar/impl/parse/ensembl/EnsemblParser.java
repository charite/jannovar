package de.charite.compbio.jannovar.impl.parse.ensembl;

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
import de.charite.compbio.jannovar.impl.util.PathUtil;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public EnsemblParser(ReferenceDictionary refDict, String basePath, List<String> geneIdentifiers,
		Section iniSection) {
		this.refDict = refDict;
		this.basePath = basePath;
		this.iniSection = iniSection;
		this.geneIdentifiers = geneIdentifiers;
	}

	@Override
	public ImmutableList<TranscriptModel> run() throws TranscriptParseException {
		// Load features from GTF file, clustered by the gene they belong to
		final String pathGTF = PathUtil.join(basePath, getINIFileName("gtf"));
		Map<String, TranscriptModelBuilder> builders = recordsToBuilders(loadRecords(pathGTF));

		// Load files that allow mapping from ENSG to HGNC ID.
		final Map<String, String> ensgToHgnc;
		try {
			ensgToHgnc = loadEngsToHgnc();
		} catch (Exception e) {
			throw new TranscriptParseException("Could not parse ENSEMBL mapping files", e);
		}

		// Load files that give the ENSG to Entrez ID mapping that comes directly from ENSEMBL.
		final Map<String, String> ensgToEntrez;
		try {
			ensgToEntrez = loadEngsToEntrez();
		} catch (Exception e) {
			throw new TranscriptParseException(
				"Could not parse ENSEMBL mapping files ENSG to Entrez", e);
		}

		// Augment information in builders with
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
						new Object[]{val.getGeneID(), entrezGeneId});
					val.getAltGeneIDs().put(AltGeneIDType.ENTREZ_ID.toString(), entrezGeneId);
				}
				LOGGER.info(
					"ENSEMBL Gene {} not known to HGNC, annotating with ENSEMBL_GENE_ID := {} for additional IDs",
					new Object[]{val.getGeneID(), val.getGeneID()});
				val.getAltGeneIDs().put(AltGeneIDType.ENSEMBL_GENE_ID.toString(), val.getGeneID());
			}
			if ("ENSG00000272333".equals(val.getGeneID())) {
				System.err.println("===<<<===<<< RARKENDARL");
			}
		}

		// Load the FASTA file and assign to the builders.
		final String pathFASTA = PathUtil.join(basePath, getINIFileName("cdna"));
		loadFASTA(builders, pathFASTA);

		// Create final list of TranscriptModels.
		ImmutableList.Builder<TranscriptModel> result = new ImmutableList.Builder<TranscriptModel>();
		for (Entry<String, TranscriptModelBuilder> entry : builders.entrySet()) {
			TranscriptModelBuilder builder = entry.getValue();
			if (geneIdentifiers == null || geneIdentifiers.isEmpty()) {
				result.add(builder.build());
			} else {
				if (geneIdentifiers.contains(builder.getAccession()) || geneIdentifiers
					.contains(builder.getGeneID())
					|| !Sets.intersection(ImmutableSet.copyOf(geneIdentifiers),
					ImmutableSet.copyOf(builder.getAltGeneIDs().values())).isEmpty()

				) {
					result.add(builder.build());
				}
			}
		}
		return result.build();
	}

	private Map<String, String> loadEngsToEntrez() throws IOException {
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

	private Map<String, String> loadEngsToHgnc() throws IOException {
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
		}

		// Read mapping from MySQL key to HGNC ID
		final String pathTableHgnc = PathUtil.join(basePath, getINIFileName("table_hgnc"));
		final Map<String, String> keyToHgnc = new HashMap<>();
		try (
			FileInputStream fis = new FileInputStream(pathTableHgnc);
			BZip2CompressorInputStream bz2is = pathTableHgnc.endsWith(".gz.bz2")
				? new BZip2CompressorInputStream(fis) : null;
			GZIPInputStream gzis = new GZIPInputStream(
				pathTableHgnc.endsWith(".gz.bz2") ? bz2is : fis);
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
		}

		// Build mapping from ENSG to HGNC identifier
		final Map<String, String> result = new HashMap<>();
		for (Entry<String, String> entry1 : ensgToKey.entrySet()) {
			final String ensg = entry1.getKey();
			final String key = entry1.getValue();
			final String hgnc = keyToHgnc.get(key);
			if (hgnc == null) {
				LOGGER.warn("Found no HGNC identifier for ENSG: ", new Object[]{ensg});
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
			txMap.put(entry.getValue().getSequence(), entry.getValue());
			if (entry.getValue().getTxVersion() != null) {
				txMap.put(entry.getValue().getSequence() + "." + entry.getValue().getTxVersion(),
					entry.getValue());
			}
		}

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
				final String accession = record.getID();
				final TranscriptModelBuilder builder = txMap.get(accession);
				if (builder == null) {
					// This is not a warning as we observed this for some records regularly
					LOGGER.debug("ID {} from FASTA did not map to transcript",
						new Object[]{accession});
					continue;
				}

				assert missingSequence.contains(builder.getAccession());
				missingSequence.remove(builder.getAccession());

				builder.setAccession(builder.getSequence());
				builder.setSequence(record.getSequence());
				LOGGER.debug("Found sequence for transcript {}",
					new Object[]{builder.getAccession()});
			}
		} catch (IOException e) {
			throw new TranscriptParseException("Problem with reading FASTA file", e);
		}

		LOGGER.info("Ignoring {} transcripts without sequence.",
			new Object[]{missingSequence.size()});
		for (String key : missingSequence) {
			LOGGER.debug("--> {}", new Object[]{key});
			builders.remove(key);
		}

		LOGGER.info("Successfully processed {} transcripts with sequence.",
			new Object[]{builders.size()});
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
	private Map<String, TranscriptModelBuilder> recordsToBuilders(
		HashMap<String, ArrayList<FeatureRecord>> recordsByGene) {
		Map<String, TranscriptModelBuilder> result = new HashMap<>();
		for (Entry<String, ArrayList<FeatureRecord>> entry : recordsByGene.entrySet()) {
			result.putAll(processGeneGFFRecords(entry.getValue()));
		}
		return result;
	}

	/**
	 * Process the GFFRecord objects for one gene.
	 */
	private Map<String, TranscriptModelBuilder> processGeneGFFRecords(
		ArrayList<FeatureRecord> records) {
		final Map<String, TranscriptModelBuilder> result = new HashMap<>();

		// Factorize the records by the transcript ID
		final HashMap<String, ArrayList<FeatureRecord>> recordsForTX = new HashMap<>();
		for (FeatureRecord record : records) {
			final String txID = (record.getAttributes().get("transcript_id") != null)
				? record.getAttributes().get("transcript_id")
				: record.getAttributes().get("transcript_name");
			if (txID == null) {
				continue; // skip, no transcript ID
			}
			if (!recordsForTX.containsKey(txID)) {
				recordsForTX.put(txID, Lists.newArrayList(record));
			} else {
				recordsForTX.get(txID).add(record);
			}
		}

		// Now, build TranscriptModelBuilder for each transcript
		for (Entry<String, ArrayList<FeatureRecord>> txEntry : recordsForTX.entrySet()) {
			final List<FeatureRecord> featureRecords = txEntry.getValue();

			final FeatureRecord first = featureRecords.get(0);
			final String geneName = first.getAttributes().get("gene_name");
			final String geneID = first.getAttributes().get("gene_id");
			final String txID = first.getAttributes().get("transcript_id");
			final String txVersion = first.getAttributes().get("transcript_version");

			final TranscriptModelBuilder builder = new TranscriptModelBuilder();

			// Parse out the simple attributes from the mRNA record
			final Strand strand =
				(first.getStrand() == FeatureRecord.Strand.FORWARD) ? Strand.FWD : Strand.REV;
			builder.setStrand(strand);
			builder.setAccession(txID);
			builder.setTxVersion(txVersion);
			builder.setGeneID(geneID);
			builder.setGeneSymbol(geneName);
			builder.setSequence(txID);

			// Iterate over the features, interpreting "exon" and "CDS"/"stop_codon" entries
			GenomeInterval txRegion = null;
			GenomeInterval cdsRegion = null;
			boolean wrongContig = false;
			for (FeatureRecord record : featureRecords) {
				ImmutableMap<String, Integer> dict = refDict.getContigNameToID();
				final String seqID = record.getSeqID();
				if (!dict.containsKey(seqID)) {
					LOGGER.debug("Skipping record {} on unknown contig {}",
						new Object[]{record, seqID});
					wrongContig = true;
					continue;
				}
				if (record.getType().equals("exon")) {
					final int chrom = dict.get(seqID);
					GenomeInterval exon = new GenomeInterval(refDict, Strand.FWD, chrom,
						record.getBegin(),
						record.getEnd());
					exon = exon.withStrand(strand);
					if (txRegion == null) {
						txRegion = exon;
					} else {
						txRegion = txRegion.union(exon);
					}
					builder.addExonRegion(exon);
				} else if ("CDS".equals(record.getType()) || "stop_codon"
					.equals(record.getType())) {
					GenomeInterval cds = new GenomeInterval(refDict, Strand.FWD,
						refDict.getContigNameToID().get(record.getSeqID()), record.getBegin(),
						record.getEnd());
					cds = cds.withStrand(strand);
					if (cdsRegion == null) {
						cdsRegion = cds;
					} else {
						cdsRegion = cdsRegion.union(cds);
					}
				}
			}
			if (wrongContig) {
				continue; // skip, on wrong contig
			}
			if (txRegion == null) {
				// Only warn if a transcript and not a gene, we only allow exons to be parts of genes as this is
				// observed in RefSeq
				LOGGER.error("No transcript region for {}; skipping", new Object[]{txEntry});
				continue;
			}
			builder.setTXRegion(txRegion);
			if (cdsRegion == null) {
				cdsRegion = new GenomeInterval(txRegion.getGenomeBeginPos(), 0);
			}
			builder.setCDSRegion(cdsRegion);

			result.put(txID, builder);
		}

		return result;
	}

	/**
	 * Load GFF records, cluster by gene and return
	 *
	 * @throws TranscriptParseException on problems with handling the transcript file
	 */
	private HashMap<String, ArrayList<FeatureRecord>> loadRecords(String pathGFF)
		throws TranscriptParseException {
		HashMap<String, ArrayList<FeatureRecord>> result = new HashMap<String, ArrayList<FeatureRecord>>();

		// Open file using GFFParser
		GFFParser parser;
		try {
			parser = new GFFParser(new File(pathGFF));
		} catch (IOException e) {
			throw new TranscriptParseException("Problem opening GFF file", e);
		}

		// Read file record by record, mapping features to genes
		//
		// This will only work properly if the full path of feature objects from the current feature has already been
		// read. Otherwise, we will need some more fancy parsing.
		int numRecords = 0;
		try {
			FeatureRecord record;
			while ((record = parser.next()) != null) {
				LOGGER.debug("Loaded GFF record {}", new Object[]{record});
				numRecords += 1;

				final String geneID = record.getAttributes().get("gene_id");
				if (!result.containsKey(geneID)) {
					result.put(geneID, Lists.newArrayList(record));
				} else {
					result.get(geneID).add(record);
				}
			}
		} catch (IOException e) {
			throw new TranscriptParseException("Problem parsing GFF file", e);
		}

		LOGGER.info("Loaded {} GFF records for {} genes", new Object[]{numRecords, result.size()});

		return result;
	}

	/**
	 * @param key name of the INI entry
	 * @return file name from INI <code>key</code.
	 */
	private String getINIFileName(String key) {
		return new File(iniSection.get(key)).getName();
	}

}
