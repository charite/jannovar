package jannovar.reference;

import jannovar.impl.intervals.IntervalEndExtractor;

/**
 * Extraction of interval end points for {@link TranscriptInfo}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptIntervalEndExtractor implements IntervalEndExtractor<TranscriptInfo> {

	@Override
	public int getBegin(TranscriptInfo transcript) {
		return transcript.txRegion.withPositionType(PositionType.ZERO_BASED).beginPos;
	}

	@Override
	public int getEnd(TranscriptInfo transcript) {
		return transcript.txRegion.withPositionType(PositionType.ZERO_BASED).endPos;
	}

}
