package jannovar.parse;

import jannovar.exception.KGParseException;
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
	 * @throws KGParseException
	 *             on problems with parsing the transcript files
	 */
	public ImmutableList<TranscriptInfo> run() throws KGParseException;

}
