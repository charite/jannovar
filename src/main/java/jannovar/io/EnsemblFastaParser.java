package jannovar.io;

import jannovar.reference.TranscriptModel;

import java.util.ArrayList;

public class EnsemblFastaParser extends FastaParser {
	
	private String[] fields;
	
	public EnsemblFastaParser(String filename, ArrayList<TranscriptModel> models) {
		super(filename, models);
	}

	@Override
	protected String processHeader(String header) {
		header = header.substring(1);
		fields = header.split(" ");
		return fields[0];
	}
}
