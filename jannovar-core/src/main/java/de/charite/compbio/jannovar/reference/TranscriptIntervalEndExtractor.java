package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.impl.intervals.IntervalEndExtractor;

/**
 * Extraction of interval end points for {@link TranscriptModel}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class TranscriptIntervalEndExtractor implements IntervalEndExtractor<TranscriptModel> {

	public int getBegin(TranscriptModel transcript) {
		return transcript.getTXRegion().withStrand(Strand.FWD).getBeginPos();
	}

	public int getEnd(TranscriptModel transcript) {
		return transcript.getTXRegion().withStrand(Strand.FWD).getEndPos();
	}

}
