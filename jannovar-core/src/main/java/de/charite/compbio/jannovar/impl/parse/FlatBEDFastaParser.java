package de.charite.compbio.jannovar.impl.parse;

import java.util.ArrayList;

import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;

/**
 * Parser for the FASTA formated files from flat BED files.
 *
 * An {@link ArrayList} of {@link TranscriptInfoBuilder}s is passed to this class together with the path to the
 * corresponding FASTA file, containing the sequence informations for the {@link TranscriptInfoBuilder}s.
 *
 * @author Marten Jaeger <marten.jaeger@charite.de>
 */
public final class FlatBEDFastaParser extends FastaParser {

	private String[] fields;

	public FlatBEDFastaParser(String filename, ArrayList<TranscriptModelBuilder> models, boolean printProgressBars) {
		super(filename, models, printProgressBars);
	}

	@Override
	protected String processHeader(String header) {
		header = header.substring(1);
		fields = header.split(" ");
		return fields[0];
	}
	
}
