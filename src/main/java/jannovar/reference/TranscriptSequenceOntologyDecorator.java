package jannovar.reference;

import jannovar.annotation.builders.SpliceAnnotationBuilder;
import jannovar.exception.ProjectionException;

/**
 * Functionality for finding out about certain points/regions of {@link TranscriptInfo} using <b>genomic</b> positions.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptSequenceOntologyDecorator {
	/** the transcript information to perform the projection upon. */
	private final TranscriptInfo transcript;

	/**
	 * Initialize the object with the given {@link TranscriptInfo}.
	 *
	 * @param transcript
	 */
	public TranscriptSequenceOntologyDecorator(TranscriptInfo transcript) {
		this.transcript = transcript;
	}

	/**
	 * @return the transcript
	 */
	public TranscriptInfo getTranscript() {
		return transcript;
	}

	/**
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return the {@link GenomeInterval} with the start codon
	 */
	public GenomeInterval getStartCodonInterval() {
		return new GenomeInterval(transcript.cdsRegion.getGenomeBeginPos().withPositionType(PositionType.ZERO_BASED), 3);
	}

	/**
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return the {@link GenomeInterval} with the start codon
	 */
	public GenomeInterval getStopCodonInterval() {
		GenomePosition pos = transcript.cdsRegion.getGenomeEndPos();
		int delta = (pos.getPositionType() == PositionType.ONE_BASED) ? 1 : 0;
		return new GenomeInterval(pos.withPositionType(PositionType.ZERO_BASED).shifted(-3 + delta), 3);
	}

	/**
	 * Returns the <b>genomic</b> 5' UTR interval.
	 *
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return the {@link GenomeInterval} with the 5' UTR
	 */
	public GenomeInterval getFivePrimeUTRInterval() {
		GenomePosition fivePrimeUTRBeginPos = transcript.txRegion.withPositionType(PositionType.ZERO_BASED)
				.getGenomeBeginPos();
		int fivePrimeUTRLen = transcript.cdsRegion.getGenomeBeginPos().differenceTo(fivePrimeUTRBeginPos);
		return new GenomeInterval(fivePrimeUTRBeginPos, fivePrimeUTRLen);
	}

	/**
	 * Returns the <b>genomic</b> 3' UTR interval.
	 *
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return the {@link GenomeInterval} with the 3' UTR
	 */
	public GenomeInterval getThreePrimeUTRInterval() {
		GenomePosition threePrimeUTRBeginPos = transcript.cdsRegion.withPositionType(PositionType.ZERO_BASED)
				.getGenomeEndPos();
		int threePrimeUTRLen = transcript.txRegion.withPositionType(PositionType.ZERO_BASED).getGenomeEndPos()
				.differenceTo(threePrimeUTRBeginPos);
		return new GenomeInterval(threePrimeUTRBeginPos, threePrimeUTRLen);
	}

	/**
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} overlaps with the translational start site
	 */
	public boolean overlapsWithTranslationalStartSite(GenomeInterval interval) {
		return interval.overlapsWith(getStartCodonInterval());
	}

	/**
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} overlaps with the translational stop site
	 */
	public boolean overlapsWithTranslationalStopSite(GenomeInterval interval) {
		return interval.overlapsWith(getStopCodonInterval());
	}

	/**
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} overlaps with the 5' UTR
	 */
	public boolean overlapsWithFivePrimeUTR(GenomeInterval interval) {
		return interval.overlapsWith(getFivePrimeUTRInterval());
	}

	/**
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} overlaps with the 3' UTR
	 */
	public boolean overlapsWithThreePrimeUTR(GenomeInterval interval) {
		return interval.overlapsWith(getThreePrimeUTRInterval());
	}

	/**
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} falls fully into an intron
	 */
	public boolean liesInIntron(GenomeInterval interval) {
		throw new Error("Implement me!");
	}

	/**
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} falls fully into an exon
	 */
	public boolean liesInExon(GenomeInterval interval) {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);

		// locate exon, return false on any errors
		int exonNo = -1;
		try {
			exonNo = projector.locateExon(interval.getGenomeBeginPos());
		} catch (ProjectionException e) {
			return false;
		}
		if (exonNo == -1)
			return false;

		return transcript.exonRegions[exonNo].contains(interval);
	}

	/**
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} falls fully into the coding part of an exon
	 */
	public boolean liesInCDSExon(GenomeInterval interval) {
		return (transcript.cdsRegion.contains(interval) && liesInExon(interval));
	}

	/**
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} falls within 2 bases of an exon begin/end position.
	 */
	public boolean overlapsWithSpliceSite(GenomeInterval interval) {
		interval = interval.withPositionType(PositionType.ZERO_BASED);
		GenomeInterval paddedInterval = interval
				.withMorePadding(SpliceAnnotationBuilder.SPLICING_THRESHOLD);

		for (GenomeInterval region : transcript.exonRegions)
			if (paddedInterval.contains(region.getGenomeBeginPos())
					|| paddedInterval.contains(region.getGenomeEndPos()))
				return true;

		return false;
	}
}
