/**
 *
 */
package jannovar.impl.parse;

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
public final class RefSeqFastaParser extends FastaParser {

	private String[] fields;

	/**
	 * Constructor for FASTA parser from RefSeq.
	 *
	 * @param filename
	 *            path to the FASTA file
	 * @param models
	 *            the {@link TranscriptModel}s w/o sequence information
	 */
	public RefSeqFastaParser(String filename, ArrayList<TranscriptInfoBuilder> models) {
		super(filename, models);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see jannovar.io.FastaParser#processHeader(java.lang.String)
	 */
	@Override
	protected String processHeader(String header) {
		fields = header.split("\\|");
		return fields[3];
	}

}
