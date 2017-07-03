package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.Immutable;

/**
 * Helper class for getting updated transcript sequence for deletions and block substitutions.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@Immutable
public final class TranscriptSequenceChangeHelper {

	/** The {@link TranscriptModel} with the sequence and position infos. */
	private final TranscriptModel transcript;

	/**
	 * Construct helper with the given {@link TranscriptModel}
	 *
	 * @param transcript
	 *            with position and sequence information
	 */
	public TranscriptSequenceChangeHelper(TranscriptModel transcript) {
		this.transcript = transcript;
	}

	/**
	 * Return modified transcript after applying a {@link GenomeVariant}.
	 *
	 * @param change
	 *            {@link GenomeVariant} to apply to the transcript
	 * @return transcript string with applied {@link GenomeVariant}
	 */
	public String getTranscriptWithChange(GenomeVariant change) {
		change = change.withStrand(transcript.getStrand());

		switch (change.getType()) {
		case SNV:
		case INSERTION:
			return getTranscriptWithPointInRefAffected(change);
		case DELETION:
		case BLOCK_SUBSTITUTION:
			return getTranscriptWithRangeInRefAffected(change);
		default:
			throw new Error("Unhandled change type " + change.getType());
		}
	}

	private String getTranscriptWithPointInRefAffected(GenomeVariant change) {
		// Short-circuit in the case of change that does not affect the transcript.
		TranscriptSequenceOntologyDecorator soDecorator = new TranscriptSequenceOntologyDecorator(transcript);
		if (!transcript.getTXRegion().overlapsWith(change.getGenomeInterval())
				|| !soDecorator.overlapsWithExon(change.getGenomeInterval()))
			return transcript.getSequence(); // non-coding change, does not affect transcript

		// Get transcript position for the change position.
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		TranscriptPosition tPos;
		try {
			tPos = projector.genomeToTranscriptPos(change.getGenomePos());
		} catch (ProjectionException e) {
			throw new Error("Bug: should be able to get transcript pos for CDS exon position");
		}

		// Update base in string using StringBuilder.
		StringBuilder builder = new StringBuilder(transcript.getSequence());
		if (change.getType() == GenomeVariantType.SNV)
			builder.setCharAt(tPos.getPos(), change.getAlt().charAt(0));
		else
			builder.insert(tPos.getPos(), change.getAlt());
		return builder.toString();
	}

	private String getTranscriptWithRangeInRefAffected(GenomeVariant change) {
		// Short-circuit in the case of change that does not affect the transcript.
		if (!transcript.getTXRegion().overlapsWith(change.getGenomeInterval()))
			return transcript.getSequence();

		// Get transcript begin and end position.
		GenomePosition changeBeginPos = change.getGenomeInterval().getGenomeBeginPos();
		TranscriptPosition tBeginPos;
		try {
			tBeginPos = translateGenomeToTranscriptPosition(changeBeginPos);
		} catch (ProjectionException e) {
			throw new Error("Bug: should be able to translate change begin position to transcript position.");
		}

		// Get transcript end position.
		GenomePosition changeEndPos = change.getGenomeInterval().getGenomeEndPos();
		TranscriptPosition tEndPos;
		try {
			tEndPos = translateGenomeToTranscriptPosition(changeEndPos);
		} catch (ProjectionException e) {
			throw new Error("Bug: should be able to translate change end position to transcript position.");
		}

		// Build resulting transcript string.
		StringBuilder builder = new StringBuilder(transcript.getSequence());
		builder.delete(tBeginPos.getPos(), tEndPos.getPos());
		builder.insert(tBeginPos.getPos(), change.getAlt());
		return builder.toString();
	}

	/**
	 * Translate {@link GenomePosition} to {@link TranscriptPosition} for {@link #transcript}.
	 *
	 * Positions upstream of transcript region (TX) are projected to TX begin, positions downstream of TX are projected
	 * to TX end, positions in introns are projected to first position of the next TX exon.
	 *
	 * @param pos
	 *            the position to translate
	 * @return the corresponding position in the transcript sequence
	 * @throws ProjectionException
	 *             in case of problems with the position conversion
	 */
	private TranscriptPosition translateGenomeToTranscriptPosition(GenomePosition pos) throws ProjectionException {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		TranscriptSequenceOntologyDecorator soDecorator = new TranscriptSequenceOntologyDecorator(transcript);

		// Get transcript begin position.
		if (transcript.getTXRegion().isRightOf(pos)) {
			// Deletion begins left of TX, project to begin of TX.
			return new TranscriptPosition(transcript, 0, PositionType.ZERO_BASED);
		} else if (transcript.getTXRegion().isLeftOf(pos)) {
			// Deletion begins right of TX, project to end of TX.
			return new TranscriptPosition(transcript, transcript.transcriptLength(), PositionType.ZERO_BASED);
		} else if (soDecorator.liesInExon(pos)) {
			return projector.genomeToTranscriptPos(pos);
		} else { // lies in intron, project to begin position of next exon
			int intronNum = projector.locateIntron(pos);
			return projector.genomeToTranscriptPos(transcript.getExonRegions().get(intronNum).getGenomeBeginPos());
		}
	}

	/**
	 * Similar to {@link #getTranscriptWithChange} but <b>starting</b> at the CDS begin position.
	 *
	 * We <b>extend</b> the CDS transcript to the right so that the changed protein sequence can be computed for
	 * frameshift changes.
	 *
	 * @param change
	 *            {@link GenomeVariant} to apply to the CDS region of the transcript
	 * @return CDS of transcript with applied {@link GenomeVariant}
	 */
	public String getCDSWithGenomeVariant(GenomeVariant change) {
		change = change.withStrand(transcript.getStrand());

		switch (change.getType()) {
		case SNV:
		case INSERTION:
			return getCDSWithPointInRefAffected(change);
		case DELETION:
		case BLOCK_SUBSTITUTION:
			return getCDSWithRangeInRefAffected(change);
		default:
			throw new Error("Unhandled change type " + change.getType());
		}
	}

	private String getCDSWithPointInRefAffected(GenomeVariant change) {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		TranscriptSequenceOntologyDecorator soDecorator = new TranscriptSequenceOntologyDecorator(transcript);

		// Obtain CDS transcript sequence.
		String cdsSeq = projector.getTranscriptStartingAtCDS();

		// Short-circuit in the case of change that does not affect the transcript.
		if (change.getType() == GenomeVariantType.SNV) {
			if (!transcript.getCDSRegion().overlapsWith(change.getGenomeInterval())
					|| !soDecorator.overlapsWithExon(change.getGenomeInterval()))
				return cdsSeq;
		} else { // insertion
			// Get change position and the one left of it.
			GenomePosition lPos = change.getGenomePos().shifted(-1);
			if (!transcript.getCDSRegion().contains(change.getGenomePos()) || !transcript.getCDSRegion().contains(lPos)
					|| (!soDecorator.liesInExon(change.getGenomePos()) && !soDecorator.liesInExon(lPos)))
				return cdsSeq;
		}

		// Get transcript position for the change position.
		CDSPosition cdsChangePos = projector.projectGenomeToCDSPosition(change.getGenomePos());

		// Update base in string using StringBuilder.
		StringBuilder builder = new StringBuilder(cdsSeq);
		if (change.getType() == GenomeVariantType.SNV)
			builder.setCharAt(cdsChangePos.getPos(), change.getAlt().charAt(0));
		else
			builder.insert(cdsChangePos.getPos(), change.getAlt());
		return builder.toString();
	}

	private String getCDSWithRangeInRefAffected(GenomeVariant change) {
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		TranscriptSequenceOntologyDecorator soDecorator = new TranscriptSequenceOntologyDecorator(transcript);

		// Obtain CDS transcript sequence.
		String cdsSeq = projector.getTranscriptStartingAtCDS();

		// Short-circuit in the case of change that does not affect the transcript.
		if (!transcript.getCDSRegion().overlapsWith(change.getGenomeInterval())
				|| !soDecorator.overlapsWithExon(change.getGenomeInterval()))
			return cdsSeq;

		// Get transcript begin and end position.
		GenomePosition changeBeginPos = change.getGenomeInterval().getGenomeBeginPos();
		CDSPosition cdsChangeBeginPos = projector.projectGenomeToCDSPosition(changeBeginPos);

		// Get transcript end position.
		GenomePosition changeEndPos = change.getGenomeInterval().getGenomeEndPos();
		CDSPosition cdsChangeEndPos = projector.projectGenomeToCDSPosition(changeEndPos);

		// Build resulting transcript string.
		StringBuilder builder = new StringBuilder(cdsSeq);
		builder.delete(cdsChangeBeginPos.getPos(), cdsChangeEndPos.getPos());
		builder.insert(cdsChangeBeginPos.getPos(), change.getAlt());
		return builder.toString();
	}
}
