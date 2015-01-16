package de.charite.compbio.jannovar.impl.parse;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * General interface for transcript parsers.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public interface TranscriptParser {

	/**
	 * @return list of {@link TranscriptInfo} objects as parsed from the input.
	 * @throws TranscriptParseException
	 *             on problems with parsing the transcript files
	 */
	public ImmutableList<TranscriptModel> run() throws TranscriptParseException;

}
