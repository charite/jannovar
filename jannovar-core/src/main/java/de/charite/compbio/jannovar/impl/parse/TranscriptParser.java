package de.charite.compbio.jannovar.impl.parse;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * General interface for transcript parsers.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public interface TranscriptParser {

	/**
	 * @return list of {@link TranscriptModel} objects as parsed from the input.
	 * @throws TranscriptParseException
	 *             on problems with parsing the transcript files
	 */
	public ImmutableList<TranscriptModel> run() throws TranscriptParseException;

}
