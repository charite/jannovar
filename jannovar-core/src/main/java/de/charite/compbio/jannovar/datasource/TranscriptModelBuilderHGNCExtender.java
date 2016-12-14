package de.charite.compbio.jannovar.datasource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.hgnc.AltGeneIDType;
import de.charite.compbio.jannovar.hgnc.HGNCParser;
import de.charite.compbio.jannovar.hgnc.HGNCRecord;
import de.charite.compbio.jannovar.impl.util.PathUtil;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;

/**
 * Helper class for extending {@link TranscriptModelBuilder} objects with HGNC information
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class TranscriptModelBuilderHGNCExtender {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(TranscriptModelBuilderHGNCExtender.class);

	/** Path to downloaded HGNC file */
	private final String basePath;

	/** Extract gene ID from {@link HGNCRecord} */
	Function<HGNCRecord, List<String>> extractorHGNC;

	/** Extract gene ID from {@link TranscriptModelBuilder} */
	Function<TranscriptModelBuilder, String> extractorTX;

	public TranscriptModelBuilderHGNCExtender(String basePath, Function<HGNCRecord, List<String>> extractorHGNC,
			Function<TranscriptModelBuilder, String> extractorTX) {
		super();
		this.basePath = basePath;
		this.extractorHGNC = extractorHGNC;
		this.extractorTX = extractorTX;
	}

	/**
	 * Augment the {@link TranscriptModelBuilder}s with HGNC information
	 * 
	 * @param builders
	 *            to augment
	 * @throws JannovarException
	 */
	public void run(Map<String, TranscriptModelBuilder> builders) throws JannovarException {
		// Get path of downloaded TSV file and parse it
		String pathTSV;
		try {
			pathTSV = PathUtil.join(basePath, new File(new URL(HGNCParser.DOWNLOAD_URL).getPath()).getName());
		} catch (MalformedURLException e) {
			throw new JannovarException("Could not parse URL " + HGNCParser.DOWNLOAD_URL, e);
		}
		final ImmutableList<HGNCRecord> hgncRecords = new HGNCParser(pathTSV).run();

		// Build data structure for easier access to the records
		final HashMap<String, HGNCRecord> recordByGeneID = new HashMap<>();
		for (HGNCRecord record : hgncRecords) {
			for (String key : extractorHGNC.apply(record)) {
				recordByGeneID.put(key, record);
			}
		}

		// Augment the information in builders
		for (TranscriptModelBuilder builder : builders.values()) {
			if (extractorTX.apply(builder) == null) {
				LOGGER.info("Transcript {} has no gene ID, not linking to HGNC",
						new Object[] { builder.getAccession() });
				continue;
			}
			if (!recordByGeneID.containsKey(extractorTX.apply(builder))) {
				LOGGER.info("Gene ID not found in HGNC: {}", new Object[] { extractorTX.apply(builder) });
				continue;
			}
			final HGNCRecord hgncRecord = recordByGeneID.get(extractorTX.apply(builder));

			// Update gene symbol/HUGO identifier, after all HGNC is the authority
			builder.setGeneSymbol(hgncRecord.getSymbol());

			// Assign alternative gene ids
			final Map<String, String> altIDs = builder.getAltGeneIDs();
			putValue(altIDs, AltGeneIDType.HGNC_ID.toString(), hgncRecord.getHgncID());
			putValue(altIDs, AltGeneIDType.HGNC_ID.toString(), hgncRecord.getHgncID());
			putValue(altIDs, AltGeneIDType.HGNC_SYMBOL.toString(), hgncRecord.getSymbol());
			putValue(altIDs, AltGeneIDType.HGNC_ALIAS.toString(),
					Joiner.on(AltGeneIDType.HGNC_ALIAS.getSeparator()).join(hgncRecord.getAliasSymbols()));
			putValue(altIDs, AltGeneIDType.HGNC_PREVIOUS.toString(),
					Joiner.on(AltGeneIDType.HGNC_PREVIOUS.getSeparator()).join(hgncRecord.getPrevSymbol()));
			putValue(altIDs, AltGeneIDType.ENTREZ_ID.toString(), hgncRecord.getEntrezID());
			putValue(altIDs, AltGeneIDType.ENSEMBL_GENE_ID.toString(), hgncRecord.getEnsemblGeneID());
			putValue(altIDs, AltGeneIDType.VEGA_ID.toString(), hgncRecord.getVegaID());
			putValue(altIDs, AltGeneIDType.UCSC_ID.toString(), hgncRecord.getUCSCID());
			putValue(altIDs, AltGeneIDType.REFSEQ_ACCESSION.toString(), hgncRecord.getRefseqAccession());
			putValue(altIDs, AltGeneIDType.CCDS_ID.toString(),
					Joiner.on(AltGeneIDType.CCDS_ID.getSeparator()).join(hgncRecord.getCCDSIDs()));
			putValue(altIDs, AltGeneIDType.UNIPROT_ID.toString(),
					Joiner.on(AltGeneIDType.UNIPROT_ID.getSeparator()).join(hgncRecord.getUniprotIDs()));
			putValue(altIDs, AltGeneIDType.PUBMED_ID.toString(),
					Joiner.on(AltGeneIDType.PUBMED_ID.getSeparator()).join(hgncRecord.getPubmedIDs()));
			putValue(altIDs, AltGeneIDType.MGD_ID.toString(), hgncRecord.getMGDID());
			putValue(altIDs, AltGeneIDType.RGD_ID.toString(), hgncRecord.getRGDID());
			putValue(altIDs, AltGeneIDType.COSMIC_ID.toString(), hgncRecord.getCosmicID());
			putValue(altIDs, AltGeneIDType.OMIM_ID.toString(), hgncRecord.getOmimID());
		}
	}

	/**
	 * Put key/value pair into map if value is not null and not the empty string
	 */
	private void putValue(Map<String, String> altIDs, String key, String value) {
		if (value != null || !"".equals(value))
			altIDs.put(key, value);
	}

}
