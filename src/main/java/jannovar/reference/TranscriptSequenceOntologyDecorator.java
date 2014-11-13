package jannovar.reference;

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
	 *            query whether <code>interval</code> contains an exon
	 * @return <code>true</code> if <code>interval</code> contains a full exon (coding or non-coding).
	 */
	public boolean containsExon(GenomeInterval interval) {
		for (int i = 0; i < transcript.exonRegions.length; ++i)
			if (interval.contains(transcript.exonRegions[i]))
				return true;
		return false;
	}

	/**
	 * @param interval
	 *            query whether <code>interval</code> overlaps with a CDS exon (exon that overlaps with CDS)
	 * @return <code>true</code> if <code>interval</code> overlaps with a CDS-overlapping exon
	 */
	public boolean overlapsWithCDSExon(GenomeInterval interval) {
		for (int i = 0; i < transcript.exonRegions.length; ++i)
			if (transcript.cdsRegion.overlapsWith(transcript.exonRegions[i])
					&& interval.overlapsWith(transcript.exonRegions[i]))
				return true;
		return false;
	}

	/**
	 * @param interval
	 *            query whether <code>interval</code> overlaps with the CDS region
	 * @return <code>true</code> if <code>interval</code> overlaps with the CDS region of the transcript
	 */
	public boolean overlapsWithCDS(GenomeInterval interval) {
		return transcript.cdsRegion.overlapsWith(interval);
	}

	/**
	 * @param changeInterval
	 *            the {@link GenomeInterval} to use for the query
	 * @return <code>true</code> if <code>changeInterval</code> overlaps with an intron of {@link #transcript}
	 */
	public boolean overlapsWithIntron(GenomeInterval changeInterval) {
		// TODO(holtgrem): Test me!
		for (int i = 0; i + 1 < transcript.exonRegions.length; ++i) {
			GenomeInterval intronRegion = transcript.intronRegion(i);
			if (changeInterval.overlapsWith(intronRegion))
				return true;
		}
		return false;
	}

	/**
	 * @param changeInterval
	 *            the {@link GenomeInterval} to use for the query
	 * @return <code>true</code> if <code>changeInterval</code> overlaps with an intron of {@link #transcript} that
	 *         overlaps with the CDS
	 */
	public boolean overlapsWithCDSIntron(GenomeInterval changeInterval) {
		// TODO(holtgrem): Test me!
		for (int i = 0; i + 1 < transcript.exonRegions.length; ++i) {
			GenomeInterval intronRegion = transcript.intronRegion(i);
			if (transcript.cdsRegion.overlapsWith(intronRegion) && changeInterval.overlapsWith(intronRegion))
				return true;
		}
		return false;
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
	 * Returns whether the given <code>interval</code> overlaps with a splice region.
	 *
	 * We define the splice region to be the first/last 3 bases of an exon (towards an intron) and the first/last 8
	 * bases of an intron. Note that this <b>includes</b> the splice donor and acceptor sites.
	 *
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} overlaps with a splice region.
	 */
	public boolean overlapsWithSpliceRegion(GenomeInterval interval) {
		boolean inCDS = false;
		for (int i = 0; i < transcript.exonRegions.length; ++i) {
			GenomeInterval exonInterval = transcript.exonRegions[i].withPositionType(PositionType.ZERO_BASED);
			if (inCDS)
				inCDS = liesInCDSExon(exonInterval.getGenomeBeginPos().shifted(1)); // first base
			if (inCDS) {
				// check for acceptor region
				GenomeInterval spliceRegionInterval = new GenomeInterval(exonInterval.getGenomeBeginPos().shifted(-8),
						11);
				if (interval.overlapsWith(spliceRegionInterval))
					return true;
			}
			if (!inCDS)
				inCDS = liesInCDSExon(exonInterval.getGenomeEndPos().shifted(-1)); // last base
			if (inCDS) {
				// check for donor region
				GenomeInterval spliceRegionInterval = new GenomeInterval(exonInterval.getGenomeEndPos().shifted(-3), 11);
				if (interval.overlapsWith(spliceRegionInterval))
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the given <code>interval</code> overlaps with a splice donor site.
	 *
	 * The splice donor site is the first two bases of an intron.
	 *
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} overlaps with a splice donor site.
	 */
	public boolean overlapsWithSpliceDonorSite(GenomeInterval interval) {
		boolean inCDS = false;
		for (int i = 0; i < transcript.exonRegions.length; ++i) {
			GenomeInterval exonInterval = transcript.exonRegions[i].withPositionType(PositionType.ZERO_BASED);
			if (!inCDS)
				inCDS = liesInCDSExon(exonInterval.getGenomeEndPos().shifted(-1)); // last base
			if (inCDS) {
				GenomeInterval donorInterval = new GenomeInterval(exonInterval.getGenomeEndPos(), 2);
				if (interval.overlapsWith(donorInterval))
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the given <code>interval</code> overlaps with a splice acceptor site.
	 *
	 * The splice acceptor site is the last two bases of an intron.
	 *
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} overlaps with a splice acceptor site.
	 */
	public boolean overlapsWithSpliceAcceptorSite(GenomeInterval interval) {
		boolean inCDS = false;
		for (int i = 0; i < transcript.exonRegions.length; ++i) {
			GenomeInterval exonInterval = transcript.exonRegions[i].withPositionType(PositionType.ZERO_BASED);
			if (!inCDS) {
				inCDS = liesInCDSExon(exonInterval.getGenomeEndPos().shifted(-1)); // last base
			} else {
				GenomeInterval acceptorInterval = new GenomeInterval(exonInterval.getGenomeBeginPos().shifted(-2), 2);
				if (interval.overlapsWith(acceptorInterval))
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the given <code>interval</code> overlaps with the upstream region of the transcript.
	 *
	 * The upstream region of the transcript is up to 1000 bp upstream of the transcript.
	 *
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} overlaps with the upstream region of the transcript.
	 */
	public boolean overlapsWithUpstreamRegion(GenomeInterval interval) {
		GenomeInterval upstream = new GenomeInterval(transcript.txRegion.getGenomeBeginPos().shifted(-1000), 1000);
		return interval.overlapsWith(upstream);
	}

	/**
	 * Returns whether the given <code>interval</code> overlaps with the downstream region of the transcript.
	 *
	 * The upstream region of the transcript is up to 1000 bp upstream of the transcript.
	 *
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} overlaps with the downstream region of the transcript.
	 */
	public boolean overlapsWithDownstreamRegion(GenomeInterval interval) {
		GenomeInterval downstream = new GenomeInterval(transcript.txRegion.withPositionType(PositionType.ZERO_BASED)
				.getGenomeEndPos(), 1000);
		return interval.overlapsWith(downstream);
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
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);

		// locate intron, return false on any errors
		int intronNo = -1;
		try {
			intronNo = projector.locateIntron(interval.getGenomeBeginPos());
		} catch (ProjectionException e) {
			return false;
		}
		if (intronNo == -1)
			return false;

		return !transcript.exonRegions[intronNo + 1].contains(interval.getGenomeEndPos().shifted(-1));
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
	 *            the {@link GenomeInterval} to use for the overlap checking
	 * @return <code>true</code> if the interval overlaps with an exon
	 */
	public boolean overlapsWithExon(GenomeInterval interval) {
		for (int i = 0; i < transcript.exonRegions.length; ++i)
			if (interval.overlapsWith(transcript.exonRegions[i]))
				return true;
		return false;
	}

	/**
	 * @param pos
	 *            the {@link GenomePosition} to use for querying
	 * @return <code>true</code> if the {@link GenomePosition} points to a base an exon
	 */
	public boolean liesInExon(GenomePosition pos) {
		GenomeInterval interval = new GenomeInterval(pos.withPositionType(PositionType.ZERO_BASED), 1);
		return liesInExon(interval);
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
	 * @param pos
	 *            the {@link GenomePosition} to use for querying
	 * @return <code>true</code> if the {@link GenomePosition} points to a base in the coding part of an exon
	 */
	public boolean liesInCDSExon(GenomePosition pos) {
		GenomeInterval interval = new GenomeInterval(pos.withPositionType(PositionType.ZERO_BASED), 1);
		return liesInCDSExon(interval);
	}

}
