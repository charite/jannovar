package jannovar.io;

import jannovar.reference.TranscriptModel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

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
