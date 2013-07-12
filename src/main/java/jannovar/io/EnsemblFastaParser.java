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

public class EnsemblFastaParser {
	
	private String filename;
	private String accession;
	private String sequence;
	private ArrayList<TranscriptModel> transcriptmodels;
	private HashMap<String, Integer> transcript2index;
	public EnsemblFastaParser(String filename, ArrayList<TranscriptModel> models) {
		this.filename	= filename;
		this.transcriptmodels	= models;
		transcript2index = new HashMap<String, Integer>(transcriptmodels.size());
		for (int i = 0; i < transcriptmodels.size(); i++) {
			transcript2index.put(transcriptmodels.get(i).getAccessionNumber(), i);
		}
		
	}

	/**
	 * Parse the mRNA sequences and thereby add these to the {@link TranscriptModel}s.
	 */
	public void parse(){
		BufferedReader in = null;
		String[] fields; 
		String str;
		try {
			if(filename.endsWith(".gz"))
				in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));
			else
				in = new BufferedReader(new FileReader(filename));
			while ((str = in.readLine()) != null) {
				if(str.startsWith(">")){
					if(sequence != null)
						addSequenceToModel();
					str = str.substring(1);
					fields = str.split(" ");
					accession = fields[0];
					sequence = "";
				}else
					sequence += str;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{
				if(in != null)
					in.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * Adds the sequence to the corresponding {@link TranscriptModel}.
	 */
	private void addSequenceToModel() {
		Integer idx;
		if((idx = transcript2index.get(accession)) != null){
			transcriptmodels.get(idx).setSequence(sequence);
			transcriptmodels.get(idx).initialize();
		}
//		System.out.println(accession+"\t"+sequence);
	}
}
