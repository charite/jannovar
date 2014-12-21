package jannovar.impl.parse;

import jannovar.exception.TranscriptParseException;
import jannovar.reference.TranscriptInfo;

import com.google.common.collect.ImmutableList;

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
	public ImmutableList<TranscriptInfo> run() throws TranscriptParseException;

}
