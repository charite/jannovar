/**
 * 
 */
package jannovar.io;

import jannovar.reference.TranscriptModel;

import java.util.ArrayList;

/**
 * @author mjaeger
 * @version 0.1 (2013-07-15)
 */
public class RefSeqFastaParser extends FastaParser {
	
	private String[] fields;

	/**
	 * @param filename
	 * @param models
	 */
	public RefSeqFastaParser(String filename, ArrayList<TranscriptModel> models) {
		super(filename, models);
	}

	/* (non-Javadoc)
	 * @see jannovar.io.FastaParser#processHeader(java.lang.String)
	 */
	@Override
	protected String processHeader(String header) {
		fields	= header.split("\\|");
		return fields[3];
	}

}
