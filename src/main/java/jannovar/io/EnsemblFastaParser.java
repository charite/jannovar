package jannovar.io;

import jannovar.reference.TranscriptInfoBuilder;

import java.util.ArrayList;

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
