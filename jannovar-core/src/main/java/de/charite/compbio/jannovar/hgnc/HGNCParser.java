package de.charite.compbio.jannovar.hgnc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.JannovarException;

// TODO(holtgrewe): test me

/**
 * Parser for <tt>hgnc_complete_set.txt</tt>
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
@Immutable
public class HGNCParser {

	/** Download URL for the HGNC complete set TSV file */
	public static final String DOWNLOAD_URL = "ftp://ftp.ebi.ac.uk/pub/databases/genenames/new/tsv/hgnc_complete_set.txt";

	/** Path to the file to parser */
	private final String path;

	public HGNCParser(String path) {
		super();
		this.path = path;
	}

	/** @return entry in the given index or the empty string */
	private static String getField(String[] arr, int idx) {
		if (idx >= arr.length)
			return "";
		else
			return arr[idx];
	}

	/**
	 * @return <tt>ImmutableList</tt> with {@link HGNCRecord}s
	 * @throws JannovarException
	 *             if there is a problem with opening or reading the file
	 */
	public ImmutableList<HGNCRecord> run() throws JannovarException {
		ImmutableList.Builder<HGNCRecord> result = new ImmutableList.Builder<HGNCRecord>();

		Map<String, Integer> header = null;

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty())
					continue;  // skip
				if (header == null) {
					// read header
					header = new HashMap<String, Integer>();
					int i = 0;
					for (String s : line.split("\t"))
						header.put(s, i++);
				} else {
					// parse fields
					final String[] arr = line.split("\t");
					final String hgncID = getField(arr, header.get("hgnc_id"));
					final String symbol = getField(arr, header.get("symbol"));
					final String name = getField(arr, header.get("name"));
					final ImmutableList<String> aliasSymbols = splitField(getField(arr, header.get("alias_symbol")));
					final ImmutableList<String> prevSymbols = splitField(getField(arr, header.get("prev_symbol")));
					final String entrezID = getField(arr, header.get("entrez_id"));
					final String ensemblGeneID = getField(arr, header.get("ensembl_gene_id"));
					final String vegaID = getField(arr, header.get("vega_id"));
					final String ucscID = getField(arr, header.get("ucsc_id"));
					final String enaID = getField(arr, header.get("ena"));
					final String refseqAccession = getField(arr, header.get("refseq_accession"));
					final ImmutableList<String> ccdsIDs = splitField(getField(arr, header.get("ccds_id")));
					final ImmutableList<String> uniprotIDs = splitField(getField(arr, header.get("uniprot_ids")));
					final ImmutableList<String> pubmedIDs = splitField(getField(arr, header.get("pubmed_id")));
					final String mgdID = getField(arr, header.get("mgd_id"));
					final String rgdID = getField(arr, header.get("rgd_id"));
					final String cosmicID = getField(arr, header.get("cosmic"));
					final String omimID = getField(arr, header.get("omim_id"));

					result.add(new HGNCRecord(hgncID, symbol, name, aliasSymbols, prevSymbols, entrezID, ensemblGeneID,
							vegaID, ucscID, enaID, refseqAccession, ccdsIDs, uniprotIDs, pubmedIDs, mgdID, rgdID,
							cosmicID, omimID));
				}
			}
		} catch (FileNotFoundException e) {
			throw new JannovarException("Problem opening HGNC file", e);
		} catch (IOException e) {
			throw new JannovarException("Problem reading HGNC file", e);
		}

		return result.build();
	}

	private static ImmutableList<String> splitField(String f) {
		if (f.startsWith("\"") && f.endsWith("\"")) {
			f = f.substring(1, f.length() - 1);
			return ImmutableList.copyOf(Splitter.on('|').split(f));
		} else {
			return ImmutableList.of(f);
		}
	}

}
