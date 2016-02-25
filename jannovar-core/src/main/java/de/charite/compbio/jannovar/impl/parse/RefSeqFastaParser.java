/**
 *
 */
package de.charite.compbio.jannovar.impl.parse;

import java.util.ArrayList;

import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;

/**
 * Parser for the FASTA formated files from RefSeq.
 *
 * An {@link ArrayList} of {@link TranscriptModelBuilder}s is passed to this class together with the path to the
 * corresponding FASTA file, containing the sequence informations for the {@link TranscriptModelBuilder}s.
 *
 * @author <a href="mailto:marten.jaeger@charite.de">Marten Jaeger</a>
 */
public final class RefSeqFastaParser extends FastaParser {

	private String[] fields;

	/**
	 * Constructor for FASTA parser from RefSeq.
	 *
	 * @param filename
	 *            path to the FASTA file
	 * @param models
	 *            the {@link TranscriptModelBuilder}s without sequence information
	 * @param printProgressBars
	 *            whether or not to print progress bars
	 */
	public RefSeqFastaParser(String filename, ArrayList<TranscriptModelBuilder> models, boolean printProgressBars) {
		super(filename, models, printProgressBars);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.charite.compbio.jannovar.io.FastaParser#processHeader(java.lang.String)
	 */
	@Override
	protected String processHeader(String header) {
		fields = header.split("\\|");
		return fields[3];
	}

}
