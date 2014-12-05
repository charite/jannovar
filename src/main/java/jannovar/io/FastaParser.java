package jannovar.io;

import jannovar.reference.TranscriptInfoBuilder;
import jannovar.reference.TranscriptModel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 * This is the base class for FASTA parsers.
 *
 * @author Marten Jaeger <marten.jaeger@charite.de>
 */
public abstract class FastaParser {

	protected String filename;
	protected String accession;
	protected StringBuilder sequence;
	protected ArrayList<TranscriptInfoBuilder> transcriptModels;
	protected ArrayList<TranscriptInfoBuilder> transcriptModelsProcessed;
	protected HashMap<String, Integer> transcript2index;

	/**
	 * Constructs a new {@link FastaParser} and initiates the path to the FASTA file and the {@link TranscriptModel}s
	 *
	 * @param filename
	 *            path to the FASTA file
	 * @param models
	 *            list of {@link TranscriptModel}s w/o mRNA sequence data
	 */
	public FastaParser(String filename, ArrayList<TranscriptInfoBuilder> models) {
		this.filename = filename;
		this.transcriptModels = models;
		this.transcriptModelsProcessed = new ArrayList<TranscriptInfoBuilder>();
		transcript2index = new HashMap<String, Integer>(transcriptModels.size());
		for (int i = 0; i < transcriptModels.size(); i++) {
			transcript2index.put(transcriptModels.get(i).getAccession(), i);
		}
	}

	/**
	 * Parse the mRNA sequences and thereby add these to the {@link TranscriptModel}s.
	 *
	 * @return list of sequence annotated {@link TranscriptModel}s
	 */
	public ArrayList<TranscriptInfoBuilder> parse() {
		BufferedReader in = null;
		String str;
		try {
			if (filename.endsWith(".gz"))
				in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));
			else
				in = new BufferedReader(new FileReader(filename));
			while ((str = in.readLine()) != null) {
				if (str.startsWith(">")) {
					if (sequence != null)
						addSequenceToModel();
					accession = processHeader(str);
					sequence = new StringBuilder();
				} else
					sequence.append(str);
			}

		} catch (IOException e) {
			System.err.println("[WARNING] failed to read the FASTA file:\n" + e.toString());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				System.err.println("[WARNING] failed to close the FASTA file reader:\n" + e.toString());
			}
		}
		return transcriptModelsProcessed;
	}

	/**
	 * Adds the sequence to the corresponding {@link TranscriptModel}.
	 */
	private void addSequenceToModel() {
		Integer idx;

		if ((idx = transcript2index.get(accession)) != null) {
			transcriptModels.get(idx).setSequence(sequence.toString());
			transcriptModelsProcessed.add(transcriptModels.get(idx));
		}
		// System.out.println(accession+"\t"+sequence);
	}

	/**
	 * Selects the unique identifier from the header line to match the sequence to the {@link TranscriptModel}
	 * definition.
	 *
	 * @param header
	 *            The FastA header line
	 * @return A unique identifier (e.g. NR_024540.1)
	 */
	protected abstract String processHeader(String header);

}
