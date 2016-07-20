package de.charite.compbio.jannovar.impl.parse.flatbed;

import java.util.ArrayList;

import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;

/**
 * Parser for the FASTA formated files from flat BED files.
 *
 * An {@link ArrayList} of {@link TranscriptModelBuilder}s is passed to this class together with the path to the
 * corresponding FASTA file, containing the sequence informations for the {@link TranscriptModelBuilder}s.
 *
 * @author <a href="mailto:marten.jaeger@charite.de">Marten Jaeger</a>
 */
public final class FlatBEDFastaParser /*extends FastaParser*/ {

	private String[] fields;

	public FlatBEDFastaParser(String filename, ArrayList<TranscriptModelBuilder> models, boolean printProgressBars) {
		//super(filename, models, printProgressBars);
	}

	/*
	@Override
	protected String processHeader(String header) {
		header = header.substring(1);
		fields = header.split(" ");
		return fields[0];
	}
	*/

}
