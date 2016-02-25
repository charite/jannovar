package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.Immutable;

/**
 * Functionality for finding out about certain points/regions of {@link TranscriptModel} using <b>genomic</b> positions.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@Immutable
public final class TranscriptSequenceOntologyDecorator {

	/** the transcript information to perform the projection upon. */
	private final TranscriptModel transcript;

	/**
	 * Initialize the object with the given {@link TranscriptModel}.
	 *
	 * @param transcript
	 *            the {@link TranscriptModel} to decorate
	 */
	public TranscriptSequenceOntologyDecorator(TranscriptModel transcript) {
		this.transcript = transcript;
	}

	/**
	 * @return the transcript
	 */
	public TranscriptModel getTranscript() {
		return transcript;
	}

	/**
	 * @return the {@link GenomeInterval} with the start codon
	 */
	public GenomeInterval getStartCodonInterval() {
		return new GenomeInterval(transcript.getCDSRegion().getGenomeBeginPos(), 3);
	}

	/**
	 * @return the {@link GenomeInterval} with the start codon
	 */
	public GenomeInterval getStopCodonInterval() {
		GenomePosition pos = transcript.getCDSRegion().getGenomeEndPos();
		return new GenomeInterval(pos.shifted(-3), 3);
	}

	/**
	 * Returns the <b>genomic</b> 5' UTR interval.
	 *
	 * @return the {@link GenomeInterval} with the 5' UTR
	 */
	public GenomeInterval getFivePrimeUTRInterval() {
		GenomePosition fivePrimeUTRBeginPos = transcript.getTXRegion().getGenomeBeginPos();
		int fivePrimeUTRLen = transcript.getCDSRegion().getGenomeBeginPos().differenceTo(fivePrimeUTRBeginPos);
		return new GenomeInterval(fivePrimeUTRBeginPos, fivePrimeUTRLen);
	}

	/**
	 * Returns the <b>genomic</b> 3' UTR interval.
	 *
	 * @return the {@link GenomeInterval} with the 3' UTR
	 */
	public GenomeInterval getThreePrimeUTRInterval() {
		GenomePosition threePrimeUTRBeginPos = transcript.getCDSRegion().getGenomeEndPos();
		int threePrimeUTRLen = transcript.getTXRegion().getGenomeEndPos().differenceTo(threePrimeUTRBeginPos);
		return new GenomeInterval(threePrimeUTRBeginPos, threePrimeUTRLen);
	}

	/**
	 * @param interval
	 *            query whether <code>interval</code> contains an exon
	 * @return <code>true</code> if <code>interval</code> contains a full exon (coding or non-coding).
	 */
	public boolean containsExon(GenomeInterval interval) {
		for (GenomeInterval region : transcript.getExonRegions())
			if (interval.contains(region))
				return true;
		return false;
	}

	/**
	 * @param interval
	 *            query whether <code>interval</code> overlaps with a CDS exon (exon that overlaps with CDS)
	 * @return <code>true</code> if <code>interval</code> overlaps with a CDS-overlapping exon
	 */
	public boolean overlapsWithCDSExon(GenomeInterval interval) {
		for (GenomeInterval region : transcript.getExonRegions())
			if (transcript.getCDSRegion().overlapsWith(region) && interval.overlapsWith(region))
				return true;
		return false;
	}

	/**
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} falls fully into the coding part of an exon
	 */
	public boolean liesInCDSExon(GenomeInterval interval) {
		return (transcript.getCDSRegion().contains(interval) && liesInExon(interval));
	}

	/**
	 * @param pos
	 *            the {@link GenomePosition} to use for querying
	 * @return <code>true</code> if the {@link GenomePosition} points to a base in the coding part of an exon
	 */
	public boolean liesInCDSExon(GenomePosition pos) {
		return (transcript.getCDSRegion().contains(pos) && liesInExon(pos));
	}

	/**
	 * @param interval
	 *            query whether <code>interval</code> overlaps with the CDS region
	 * @return <code>true</code> if <code>interval</code> overlaps with the CDS region of the transcript
	 */
	public boolean overlapsWithCDS(GenomeInterval interval) {
		return transcript.getCDSRegion().overlapsWith(interval);
	}

	/**
	 * @return <code>true</code> if <code>pos</code> lies within the CDS of {@link #transcript}
	 */
	public boolean liesInCDS(GenomePosition pos) {
		return transcript.getCDSRegion().contains(pos);
	}

	/**
	 * @param changeInterval
	 *            the {@link GenomeInterval} to use for the query
	 * @return <code>true</code> if <code>changeInterval</code> overlaps with an intron of {@link #transcript}
	 */
	public boolean overlapsWithIntron(GenomeInterval changeInterval) {
		// TODO(holtgrem): Test me!
		for (int i = 0; i + 1 < transcript.getExonRegions().size(); ++i) {
			GenomeInterval intronRegion = transcript.intronRegion(i);
			if (changeInterval.overlapsWith(intronRegion))
				return true;
		}
		return false;
	}

	/**
	 * @return <code>true</code> if <code>pos</code> lies within an intron of {@link #transcript}
	 */
	public boolean liesInIntron(GenomePosition pos) {
		// TODO(holtgrem): Test me!
		for (int i = 0; i + 1 < transcript.getExonRegions().size(); ++i) {
			GenomeInterval intronRegion = transcript.intronRegion(i);
			if (intronRegion.contains(pos))
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
		for (int i = 0; i + 1 < transcript.getExonRegions().size(); ++i) {
			GenomeInterval intronRegion = transcript.intronRegion(i);
			if (transcript.getCDSRegion().overlapsWith(intronRegion) && changeInterval.overlapsWith(intronRegion))
				return true;
		}
		return false;
	}

	/**
	 * @return <code>true</code> if the {@link GenomePosition} lies within an intron of {@link #transcript} that
	 *         overlaps with the CDS
	 */
	public boolean liesInCDSIntron(GenomePosition pos) {
		// TODO(holtgrem): Test me!
		for (int i = 0; i + 1 < transcript.getExonRegions().size(); ++i) {
			GenomeInterval intronRegion = transcript.intronRegion(i);
			if (transcript.getCDSRegion().overlapsWith(intronRegion) && intronRegion.contains(pos))
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
	 * @return <code>true</code> if the {@link GenomePosition} lies within the translational start site
	 */
	public boolean liesInTranslationalStartSite(GenomePosition pos) {
		return getStartCodonInterval().contains(pos);
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
	 * @return <code>true</code> if the {@link GenomePosition} lies within the translational stop site
	 */
	public boolean liesInTranslationalStopSite(GenomePosition pos) {
		return getStopCodonInterval().contains(pos);
	}

	/**
	 * Returns whether the given <code>interval</code> overlaps with a splice region.
	 *
	 * A splice_region_variant is a sequence variant in which a change has occurred within the region of the splice
	 * site, either within 1-3 bases of the exon or 3-8 bases of the intron.
	 *
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} overlaps with a splice region.
	 */
	public boolean overlapsWithSpliceRegion(GenomeInterval interval) {
		// TODO(holtgrem): Test me!
		for (int i = 0; i < transcript.getExonRegions().size(); ++i) {
			GenomeInterval exonInterval = transcript.getExonRegions().get(i);
			if (i + 1 < transcript.getExonRegions().size()) {
				// check for donor region
				GenomeInterval spliceRegionInterval = new GenomeInterval(exonInterval.getGenomeEndPos().shifted(-3), 11);
				if (interval.overlapsWith(spliceRegionInterval))
					return true;
			}
			if (i > 0) {
				// check for acceptor region
				GenomeInterval spliceRegionInterval = new GenomeInterval(exonInterval.getGenomeBeginPos().shifted(-8),
						11);
				if (interval.overlapsWith(spliceRegionInterval))
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the given <code>pos</code> lies within a splice region.
	 *
	 * A splice_region_variant is a sequence variant in which a change has occurred within the region of the splice
	 * site, either within 1-3 bases of the exon or 3-8 bases of the intron.
	 *
	 * @param pos
	 *            the {@link GenomePosition} to use for querying
	 * @return <code>true</code> if the {@link GenomePosition} lies within a splice donor site.
	 */
	public boolean liesInSpliceRegion(GenomePosition pos) {
		// TODO(holtgrem): Test me!
		for (int i = 0; i < transcript.getExonRegions().size(); ++i) {
			GenomeInterval exonInterval = transcript.getExonRegions().get(i);
			if (i + 1 < transcript.getExonRegions().size()) {
				// check for donor region
				GenomeInterval spliceRegionInterval = new GenomeInterval(exonInterval.getGenomeEndPos().shifted(-3), 11);
				if (spliceRegionInterval.contains(pos))
					return true;
			}
			if (i > 0) {
				// check for acceptor region
				GenomeInterval spliceRegionInterval = new GenomeInterval(exonInterval.getGenomeBeginPos().shifted(-8),
						11);
				if (spliceRegionInterval.contains(pos))
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the given <code>interval</code> overlaps with a splice donor site.
	 *
	 * A splice variant that changes the 2 base pair region at the 5' end of an intron.
	 *
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} overlaps with a splice donor site.
	 */
	public boolean overlapsWithSpliceDonorSite(GenomeInterval interval) {
		// TODO(holtgrem): Test me!
		for (int i = 0; i + 1 < transcript.getExonRegions().size(); ++i) {
			GenomeInterval exonInterval = transcript.getExonRegions().get(i);
			GenomeInterval donorInterval = new GenomeInterval(exonInterval.getGenomeEndPos(), 2);
			if (interval.overlapsWith(donorInterval))
				return true;
		}
		return false;
	}

	/**
	 * Returns whether the given <code>pos</code> lies within a splice donor site.
	 *
	 * A splice variant that changes the 2 base pair region at the 5' end of an intron.
	 *
	 * @param pos
	 *            the {@link GenomePosition} to use for querying
	 * @return <code>true</code> if the {@link GenomePosition} lies within a splice donor site.
	 */
	public boolean liesInSpliceDonorSite(GenomePosition pos) {
		// TODO(holtgrem): Test me!
		for (int i = 0; i + 1 < transcript.getExonRegions().size(); ++i) {
			GenomeInterval exonInterval = transcript.getExonRegions().get(i);
			GenomeInterval donorInterval = new GenomeInterval(exonInterval.getGenomeEndPos(), 2);
			if (donorInterval.contains(pos))
				return true;
		}
		return false;
	}

	/**
	 * Returns whether the given <code>interval</code> overlaps with a splice acceptor site.
	 *
	 * A splice variant that changes the 2 base pair region at the 3' end of an intron.
	 *
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} overlaps with a splice acceptor site.
	 */
	public boolean overlapsWithSpliceAcceptorSite(GenomeInterval interval) {
		// TODO(holtgrem): Test me!
		for (int i = 1; i < transcript.getExonRegions().size(); ++i) {
			GenomeInterval exonInterval = transcript.getExonRegions().get(i);
			GenomeInterval acceptorInterval = new GenomeInterval(exonInterval.getGenomeBeginPos().shifted(-2), 2);
			if (interval.overlapsWith(acceptorInterval))
				return true;
		}
		return false;
	}

	/**
	 * Returns whether the given <code>pos</code> lies within a splice acceptor site.
	 *
	 * A splice variant that changes the 2 base pair region at the 3' end of an intron.
	 *
	 * @param pos
	 *            the {@link GenomePosition} to use for querying
	 * @return <code>true</code> if the {@link GenomePosition} lies within a splice acceptor site.
	 */
	public boolean liesInSpliceAcceptorSite(GenomePosition pos) {
		// TODO(holtgrem): Test me!
		for (int i = 1; i < transcript.getExonRegions().size(); ++i) {
			GenomeInterval exonInterval = transcript.getExonRegions().get(i);
			GenomeInterval acceptorInterval = new GenomeInterval(exonInterval.getGenomeBeginPos().shifted(-2), 2);
			if (acceptorInterval.contains(pos))
				return true;
		}
		return false;
	}

	/**
	 * @return {@link GenomeInterval} that gives the upstream region of the transcript.
	 */
	public GenomeInterval getUpstreamInterval() {
		return new GenomeInterval(transcript.getTXRegion().getGenomeBeginPos().shifted(-1000), 1000);
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
		GenomeInterval upstream = getUpstreamInterval();
		return interval.overlapsWith(upstream);
	}

	/**
	 * Returns whether the given <code>pos</code> lies within with the upstream region of the transcript.
	 *
	 * The upstream region of the transcript is up to 1000 bp upstream of the transcript.
	 *
	 * @param pos
	 *            the {@link GenomePosition} to use for querying
	 * @return <code>true</code> if the {@link GenomePosition} lies within the upstream region of the transcript.
	 */
	public boolean liesInUpstreamRegion(GenomePosition pos) {
		GenomeInterval upstream = getUpstreamInterval();
		return upstream.contains(pos);
	}

	/**
	 * @return {@link GenomeInterval} that gives the downstream region of the transcript.
	 */
	public GenomeInterval getDownstreamInterval() {
		return new GenomeInterval(transcript.getTXRegion().getGenomeEndPos(), 1000);
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
		GenomeInterval downstream = getDownstreamInterval();
		return interval.overlapsWith(downstream);
	}

	/**
	 * Returns whether the given <code>pos</code> lies within with the downstream region of the transcript.
	 *
	 * The upstream region of the transcript is up to 1000 bp downstream of the transcript.
	 *
	 * @param pos
	 *            the {@link GenomePosition} to use for querying
	 * @return <code>true</code> if the {@link GenomePosition} lies within the downstream region of the transcript.
	 */
	public boolean liesInDownstreamRegion(GenomePosition pos) {
		GenomeInterval downstream = getDownstreamInterval();
		return downstream.contains(pos);
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
	 * @param pos
	 *            the {@link GenomePosition} to use for querying
	 * @return <code>true</code> if the {@link GenomePosition} lies in the 5' UTR
	 */
	public boolean liesInFivePrimeUTR(GenomePosition pos) {
		return getFivePrimeUTRInterval().contains(pos);
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
	 * @param pos
	 *            the {@link GenomePosition} to use for querying
	 * @return <code>true</code> if the {@link GenomePosition} lies in the 3' UTR
	 */
	public boolean liesInThreePrimeUTR(GenomePosition pos) {
		return getThreePrimeUTRInterval().contains(pos);
	}

	/**
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} falls fully into an intron
	 */
	public boolean liesInIntron(GenomeInterval interval) {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);

		// locate intron, return false on any errors
		final int intronNo = projector.locateIntron(interval.getGenomeBeginPos());
		if (intronNo == TranscriptProjectionDecorator.INVALID_INTRON_ID)
			return false;

		return !transcript.getExonRegions().get(intronNo + 1).contains(interval.getGenomeEndPos().shifted(-1));
	}

	/**
	 * @param interval
	 *            the {@link GenomeInterval} to use for querying
	 * @return <code>true</code> if the {@link GenomeInterval} falls fully into an exon
	 */
	public boolean liesInExon(GenomeInterval interval) {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);

		// locate exon, return false on any errors
		final int exonNo = projector.locateExon(interval.getGenomeBeginPos());
		if (exonNo == TranscriptProjectionDecorator.INVALID_EXON_ID)
			return false;

		return transcript.getExonRegions().get(exonNo).contains(interval);
	}

	/**
	 * @param pos
	 *            the {@link GenomePosition} to use for querying
	 * @return <code>true</code> if the {@link GenomePosition} points to a base an exon
	 */
	public boolean liesInExon(GenomePosition pos) {
		// TODO(holtgrem): Test me!
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);

		// locate exon, return false on any errors
		final int exonNo = projector.locateExon(pos);
		if (exonNo == TranscriptProjectionDecorator.INVALID_EXON_ID)
			return false;

		return transcript.getExonRegions().get(exonNo).contains(pos);
	}

	/**
	 * @param interval
	 *            the {@link GenomeInterval} to use for the overlap checking
	 * @return <code>true</code> if the interval overlaps with an exon
	 */
	public boolean overlapsWithExon(GenomeInterval interval) {
		for (int i = 0; i < transcript.getExonRegions().size(); ++i)
			if (interval.overlapsWith(transcript.getExonRegions().get(i)))
				return true;
		return false;
	}

}
