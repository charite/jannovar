package jannovar.reference;

import jannovar.impl.intervals.IntervalEndExtractor;

/**
 * Extraction of interval end points for {@link TranscriptInfo}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptIntervalEndExtractor implements IntervalEndExtractor<TranscriptModel> {

	@Override
	public int getBegin(TranscriptModel transcript) {
		return transcript.txRegion.withStrand('+').beginPos;
	}

	@Override
	public int getEnd(TranscriptModel transcript) {
		return transcript.txRegion.withStrand('+').endPos;
	}

}
