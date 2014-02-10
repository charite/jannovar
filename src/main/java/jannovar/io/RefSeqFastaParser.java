/**
 * 
 */
package jannovar.io;

import jannovar.reference.TranscriptModel;

import java.util.ArrayList;

/**
 * Parser for the FastA formated files from RefSeq.<br>
 * An {@link ArrayList} of {@link TranscriptModel}s is passed to this {@link Class} together 
 * with the path to the corresponding FastA file, containing the sequence informations
 * for the {@link TranscriptModel}s. 
 * @author mjaeger
 * @version 0.1 (2013-07-15)
 */
public class RefSeqFastaParser extends FastaParser {
	
	private String[] fields;

	/**
	 * Constructor for FastA parser from RefSeq. 
	 * @param filename path to the FastA file
	 * @param models the {@link TranscriptModel}s w/o sequence information
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
