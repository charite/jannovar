package de.charite.compbio.jannovar.hgvs.bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.nts.NucleotidePointLocation;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideSubstitution;
import de.charite.compbio.jannovar.htsjdk.GenomeRegionSequenceExtractor;
import de.charite.compbio.jannovar.impl.util.DNAUtils;
import de.charite.compbio.jannovar.reference.CDSPosition;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.ProjectionException;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptPosition;
import de.charite.compbio.jannovar.reference.TranscriptProjectionDecorator;

/**
 * Implementation of converting nucleotide substitutions to genome variants.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
class NucleotideSubstitutionToGenomeVariantTranslationImpl {

	/** logger instance to use */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(NucleotideSubstitutionToGenomeVariantTranslationImpl.class);

	/** extraction of {@link GenomicRegion} from FASTA files */
	private final GenomeRegionSequenceExtractor seqExtractor;

	public NucleotideSubstitutionToGenomeVariantTranslationImpl(GenomeRegionSequenceExtractor seqExtractor) {
		super();
		this.seqExtractor = seqExtractor;
	}

	/**
	 * Implementation of translation for {@link NucleotideSubstitution} objects
	 *
	 * @param tm
	 *            {@link TranscriptModel} that <code>ntSub</code> is for
	 * @param sequenceType
	 *            {@link SequenceType} that <code>ntSub</code> is for
	 * @return {@link GenomeVariant} with the translation result, possibly annotated with warning messages
	 * @throws CannotTranslateHGVSVariant
	 *             in case of translation problems
	 */
	public ResultWithWarnings<GenomeVariant> run(TranscriptModel tm, SequenceType sequenceType,
			NucleotideSubstitution ntSub) throws CannotTranslateHGVSVariant {
		final NucleotidePointLocation pos = ntSub.getPosition();
		final String fromNT = ntSub.getFromNT().toUpperCase();
		final String toNT = ntSub.getToNT().toUpperCase();

		if (fromNT.length() != 1 || toNT.length() != 1)
			throw new CannotTranslateHGVSVariant("Both source and target sequence must have length 1 in "
					+ ntSub.toHGVSString());

		final GenomeVariant result = new GenomeVariant(translateNucleotidePointLocation(tm, pos, sequenceType), fromNT,
				toNT, tm.getStrand()).withStrand(Strand.FWD);
		final String refSeq = getGenomeSeq(tm.getStrand(), result.getGenomeInterval());
		if (!refSeq.equals(fromNT))
			return ResultWithWarnings.construct(result, "Invalid reference nucleotides in " + result.toString()
					+ ", should be " + refSeq);
		else
			return ResultWithWarnings.construct(result);
	}

	/**
	 * Convert {@link NucleotidePointLocation} on a {@link TranscriptModel} to a {@link GenomePosition}
	 */
	private GenomePosition translateNucleotidePointLocation(TranscriptModel tm, NucleotidePointLocation pos,
			SequenceType sequenceType) throws CannotTranslateHGVSVariant {
		switch (sequenceType) {
		case CODING_DNA:
			return translateCodingNucleotidePointLocation(tm, pos);
		case NON_CODING_DNA:
			return translateNonCodingNucleotidePointLocation(tm, pos);
		default:
			throw new CannotTranslateHGVSVariant("Unsupported sequence type " + sequenceType);
		}
	}

	/**
	 * Translate CDS ("c.") NucleotidePointLocation to a {@link GenomePosition}.
	 *
	 * @param tm
	 *            {@link TranscriptModel} that the location is relative to
	 * @param pos
	 *            {@link NucleotidePointLocation} to translate
	 * @return resulting {@link GenomePosition} of the translation
	 * @throws CannotTranslateHGVSVariant
	 *             in case of problems with the translation
	 */
	private GenomePosition translateCodingNucleotidePointLocation(TranscriptModel tm, NucleotidePointLocation pos)
			throws CannotTranslateHGVSVariant {
		final TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(tm);

		// compute transcript position of NucleotidePointLocation pos
		TranscriptPosition txPos;
		if (pos.getBasePos() < 0) {
			// handle 5' UTR case
			txPos = projector.cdsToTranscriptPos(new CDSPosition(tm, 0));
			if (txPos.getPos() < -pos.getBasePos())
				throw new CannotTranslateHGVSVariant("Invalid CDS position " + pos.toHGVSString()
						+ " as it lies upstream of 5' UTR");
			txPos = new TranscriptPosition(tm, txPos.getPos() + pos.getBasePos());
		} else if (pos.isDownstreamOfCDS()) {
			// handle 3' UTR case
			final CDSPosition lastCDSPos = new CDSPosition(tm, tm.cdsTranscriptLength() - 1);
			txPos = projector.cdsToTranscriptPos(lastCDSPos);
			// Note that for HGVS, the terminal codon is not part of the CDS whereas it is in TranscriptModel.
			// This is the case for the SHIFT below.
			final int SHIFT = -3;
			txPos = txPos.shifted(pos.getBasePos() + 1 + SHIFT);
			if (txPos.getPos() >= tm.transcriptLength())
				throw new CannotTranslateHGVSVariant("Invalid CDS position " + pos.toHGVSString()
						+ " as it lies downstream of 3' UTR");
		} else {
			// handle CDS case
			final CDSPosition cdsPos = new CDSPosition(tm, pos.getBasePos());
			txPos = projector.cdsToTranscriptPos(cdsPos);
		}

		// translate this into a GenomePosition on the same strand as tm and return this
		try {
			return projector.transcriptToGenomePos(txPos).withStrand(tm.getStrand()).shifted(pos.getOffset());
		} catch (ProjectionException e) {
			throw new CannotTranslateHGVSVariant("could not translate base transcript position " + txPos + " of "
					+ pos.toHGVSString() + " to a genome position", e);
		}
	}

	/**
	 * Translate non-coding (transcript, "n.") NucleotidePointLocation to a {@link GenomePosition}.
	 *
	 * @param tm
	 *            {@link TranscriptModel} that the location is relative to
	 * @param pos
	 *            {@link NucleotidePointLocation} to translate
	 * @return resulting {@link GenomePosition} of the translation
	 * @throws CannotTranslateHGVSVariant
	 *             in case of problems with the translation
	 */
	protected GenomePosition translateNonCodingNucleotidePointLocation(TranscriptModel tm, NucleotidePointLocation pos)
			throws CannotTranslateHGVSVariant {
		final TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(tm);

		TranscriptPosition txPos = new TranscriptPosition(tm, pos.getBasePos());

		try {
			return projector.transcriptToGenomePos(txPos).withStrand(tm.getStrand()).shifted(pos.getOffset());
		} catch (ProjectionException e) {
			throw new CannotTranslateHGVSVariant("could not translate base transcript position " + txPos + " of "
					+ pos.toHGVSString() + " to a genome position", e);
		}
	}

	/**
	 * Return sequence from the reference in the given interval <code>gItv</code>.
	 *
	 * The sequence will be reverse-complemented depending on <code>strand</code> and converted to upper case.
	 *
	 * @param strand
	 *            to load from
	 * @param gItv
	 *            {@link GenomeInterval} to load sequence for
	 * @return sequence loaded from reference
	 */
	protected String getGenomeSeq(Strand strand, GenomeInterval gItv) {
		String result = seqExtractor.load(gItv.withStrand(Strand.FWD));
		if (strand == Strand.REV)
			result = DNAUtils.reverseComplement(result);
		return result.toUpperCase();
	}

}
