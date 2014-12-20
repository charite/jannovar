package jannovar.parse;

import jannovar.reference.TranscriptInfoBuilder;

import java.util.ArrayList;

/**
 * Parser for the FASTA formated files from RefSeq.
 *
 * An {@link ArrayList} of {@link TranscriptInfoBuilder}s is passed to this class together with the path to the
 * corresponding FASTA file, containing the sequence informations for the {@link TranscriptInfoBuilder}s.
 *
 * @author Marten Jaeger <marten.jaeger@charite.de>
 */
public final class EnsemblFastaParser extends FastaParser {

	private String[] fields;

	public EnsemblFastaParser(String filename, ArrayList<TranscriptInfoBuilder> models) {
		super(filename, models);
	}

	@Override
	protected String processHeader(String header) {
		header = header.substring(1);
		fields = header.split(" ");
		return fields[0];
	}
}
