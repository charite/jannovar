package jannovar.reference;

import jannovar.annotation.builders.SpliceAnnotationBuilder;

/**
 * Functionality for {@link TranscriptInfo} for splice site detection.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptSpliceSiteDecorator {
	/** the transcript information to perform the projection upon. */
	private final TranscriptInfo transcript;

	/**
	 * Initialize the object with the given {@link TranscriptInfo}.
	 *
	 * @param transcript
	 */
	public TranscriptSpliceSiteDecorator(TranscriptInfo transcript) {
		this.transcript = transcript;
	}

	/**
	 * @return the transcript
	 */
	public TranscriptInfo getTranscript() {
		return transcript;
	}

	/**
	 * @param change
	 *            the {@link GenomeChange} to use for querying
	 * @return <code>true</code> if the {@link GenomeChange} falls within 2 bases of an exon begin/end position.
	 */
	public boolean doesChangeAffectSpliceSite(GenomeChange change) {
		GenomePosition changeBeginPos = change.getPos().withPositionType(PositionType.ZERO_BASED);
		GenomeInterval changeInterval = new GenomeInterval(changeBeginPos.getStrand(), changeBeginPos.getChr(),
				changeBeginPos.getPos(), changeBeginPos.shifted(change.getRef().length()).getPos(),
				PositionType.ZERO_BASED);
		GenomeInterval paddedChangeInterval = changeInterval.withMorePadding(SpliceAnnotationBuilder.SPLICING_THRESHOLD);

		for (GenomeInterval region : transcript.exonRegions)
			if (paddedChangeInterval.contains(region.getGenomeBeginPos())
					|| paddedChangeInterval.contains(region.getGenomeEndPos()))
				return true;

		return false;
	}
}
