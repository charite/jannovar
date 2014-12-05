package jannovar.parse;

import jannovar.exception.JannovarException;
import jannovar.reference.TranscriptInfo;

import java.util.ArrayList;

/**
 * General interface for transcript parsers.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public interface TranscriptParser {

	/**
	 * @return list of {@link TranscriptInfo} objects as parsed from the input.
	 */
	public ArrayList<TranscriptInfo> run() throws JannovarException;

}
