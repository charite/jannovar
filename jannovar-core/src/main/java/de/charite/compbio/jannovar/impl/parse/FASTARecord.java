package de.charite.compbio.jannovar.impl.parse;

/**
 * Representation of a FASTA record
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class FASTARecord {

	/** ID, up to the first whitespace */
	private final String id;
	/** Comment, everything after the first whitespace */
	private final String comment;
	/** The actual sequence, any whitespace is stripped */
	private final String sequence;

	public FASTARecord(String id, String comment, String sequence) {
		this.id = id;
		this.comment = comment;
		this.sequence = sequence;
	}

	public String getID() {
		return id;
	}

	public String getComment() {
		return comment;
	}

	public String getSequence() {
		return sequence;
	}

}
