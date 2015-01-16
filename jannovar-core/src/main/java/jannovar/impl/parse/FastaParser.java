package jannovar.impl.parse;

import jannovar.impl.util.ProgressBar;
import jannovar.reference.TranscriptModel;
import jannovar.reference.TranscriptModelBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
	protected ArrayList<TranscriptModelBuilder> TranscriptInfos;
	protected ArrayList<TranscriptModelBuilder> TranscriptInfosProcessed;
	protected HashMap<String, Integer> transcript2index;

	/**
	 * Constructs a new {@link FastaParser} and initiates the path to the FASTA file and the {@link TranscriptInfo}s
	 *
	 * @param filename
	 *            path to the FASTA file
	 * @param models
	 *            list of {@link TranscriptInfo}s w/o mRNA sequence data
	 */
	public FastaParser(String filename, ArrayList<TranscriptModelBuilder> models) {
		this.filename = filename;
		this.TranscriptInfos = models;
		this.TranscriptInfosProcessed = new ArrayList<TranscriptModelBuilder>();
		transcript2index = new HashMap<String, Integer>(TranscriptInfos.size());
		int i = 0;
		for (TranscriptModelBuilder model : TranscriptInfos)
			transcript2index.put(model.getAccession(), i++);
	}

	/**
	 * Parse the mRNA sequences and thereby add these to the {@link TranscriptInfo}s.
	 *
	 * @return list of sequence annotated {@link TranscriptInfo}s
	 */
	public ArrayList<TranscriptModelBuilder> parse() {
		BufferedReader in = null;
		String str;

		// We use ProgressBar to display our progress in GFF parsing.
		File file = new File(filename);
		ProgressBar bar = new ProgressBar(0, file.length());

		try {
			FileInputStream fip = new FileInputStream(file);
			if (filename.endsWith(".gz"))
				in = new BufferedReader(new InputStreamReader(new GZIPInputStream(fip)));
			else
				in = new BufferedReader(new InputStreamReader(fip));
			final int CHUNK_SIZE = 1000;
			int lineNo = 0;
			while ((str = in.readLine()) != null) {
				if (str.startsWith(">")) {
					if (sequence != null)
						addSequenceToModel();
					accession = processHeader(str);
					sequence = new StringBuilder();
				} else {
					sequence.append(str);
				}

				if (++lineNo == CHUNK_SIZE) {
					bar.print(fip.getChannel().position());
					lineNo = 0;
				}
			}

			if (sequence != null)
				addSequenceToModel();
			bar.print(bar.max);

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
		return TranscriptInfosProcessed;
	}

	/**
	 * Adds the sequence to the corresponding {@link TranscriptInfo}.
	 */
	private void addSequenceToModel() {
		Integer idx;

		if ((idx = transcript2index.get(accession)) != null) {
			TranscriptInfos.get(idx).setSequence(sequence.toString());
			TranscriptInfosProcessed.add(TranscriptInfos.get(idx));
		}
		sequence = null;
		// System.out.println(accession+"\t"+sequence);
	}

	/**
	 * Selects the unique identifier from the header line to match the sequence to the {@link TranscriptInfo}
	 * definition.
	 *
	 * @param header
	 *            The FastA header line
	 * @return A unique identifier (e.g. NR_024540.1)
	 */
	protected abstract String processHeader(String header);

}
