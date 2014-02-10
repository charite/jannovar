/**
 * 
 */
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

/**
 * This is the 
 * @author mjaeger
 * @version 0.1 (2013-07-12)
 */
public abstract class FastaParser {

	protected String filename;
	protected String accession;
	protected StringBuilder sequence;
	protected ArrayList<TranscriptModel> transcriptmodels;
	protected ArrayList<TranscriptModel> transcriptmodelsProcessed;
	protected HashMap<String, Integer> transcript2index;
	
	/**
	 * 
	 */
	public FastaParser(String filename, ArrayList<TranscriptModel> models) {
		this.filename	= filename;
		this.transcriptmodels	= models;
		this.transcriptmodelsProcessed = new ArrayList<TranscriptModel>();
		transcript2index = new HashMap<String, Integer>(transcriptmodels.size());
		for (int i = 0; i < transcriptmodels.size(); i++) {
			transcript2index.put(transcriptmodels.get(i).getAccessionNumber(), i);
		}
	}
	
	/**
	 * Parse the mRNA sequences and thereby add these to the {@link TranscriptModel}s.
	 */
	public ArrayList<TranscriptModel> parse(){
		BufferedReader in = null;
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
					accession = processHeader(str);
					sequence = new StringBuilder();
				}else
					sequence.append(str);
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
		return transcriptmodelsProcessed;
	}

	/**
	 * Adds the sequence to the corresponding {@link TranscriptModel}.
	 */
	private void addSequenceToModel() {
		Integer idx;
		
		if((idx = transcript2index.get(accession)) != null){
			transcriptmodels.get(idx).setSequence(sequence.toString());
			transcriptmodels.get(idx).initialize();
			transcriptmodelsProcessed.add(transcriptmodels.get(idx));
		}
//		System.out.println(accession+"\t"+sequence);
	}

	/**
	 * Selects the unique identifier from the header line to match the sequence to the {@link TranscriptModel}
	 * definition.
	 * @param header The FastA header line
	 * @return A unique identifier (e.g. NR_024540.1)
	 */
	protected abstract String processHeader(String header);
		
}
