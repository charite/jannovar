package de.charite.compbio.jannovar.hgvs.bridge;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.nts.NucleotidePointLocation;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideChange;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideSubstitution;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;
import de.charite.compbio.jannovar.reference.CDSPosition;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.ProjectionException;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptPosition;
import de.charite.compbio.jannovar.reference.TranscriptProjectionDecorator;

/**
 * Helper for converting a {@link NucleotideChange} to a {@link GenomeVariant}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class NucleotideChangeToGenomeVariantTranslator {

	/** transcript database and reference dictionary to use for translation */
	final private JannovarData jvDB;
	/** shortcut to reference dictionary to use */
	private final ReferenceDictionary refDict;
	/** object to use for indexed access to reference FASTA file */
	private final IndexedFastaSequenceFile indexedFasta;

	public NucleotideChangeToGenomeVariantTranslator(JannovarData jvDB, IndexedFastaSequenceFile indexedFasta) {
		this.jvDB = jvDB;
		this.refDict = jvDB.getRefDict();
		this.indexedFasta = indexedFasta;
	}

	/**
	 * Translate single-change {@link SingleAlleleNucleotideVariant} into a {@link GenomeVariant}
	 *
	 * @param variant
	 *            {@link SingleAlleleNucleotideVariant} to translate
	 * @return {@link GenomeVariant} resulting from the conversion
	 * @throws CannotTranslateHGVSVariant
	 *             in the case of problems such as more than one entry in the allele of <code>variant</code> or
	 *             unsupported {@link NucleotideChange}s
	 */
	public GenomeVariant translateNucleotideVariantToGenomeVariant(SingleAlleleNucleotideVariant variant)
			throws CannotTranslateHGVSVariant {
		// perform sanity checks and get corresponding TranscriptModel from JannovarData
		if (variant.getSeqType() != SequenceType.CODING_DNA && variant.getSeqType() != SequenceType.NON_CODING_DNA)
			throw new CannotTranslateHGVSVariant("Currently only coding DNA (\"c.\") and non-coding DNA (\"n.\") "
					+ "coordinates are supported.");
		if (variant.getAllele().size() != 1)
			throw new CannotTranslateHGVSVariant("Too many alles in variant " + variant.toHGVSString()
					+ ", must be one allele.");
		TranscriptModel tm = jvDB.getTmByAccession().get(variant.getRefIDWithVersion());
		if (tm == null)
			throw new CannotTranslateHGVSVariant("No transcript found for id " + variant.getRefIDWithVersion());

		// get NucleotideChange from only entry in only allele
		NucleotideChange ntChange = variant.getAllele().get(0);
		if (ntChange instanceof NucleotideSubstitution) {
			return translateNucleotideSubstitution(tm, variant.getSeqType(), (NucleotideSubstitution) ntChange);
		} else {
			throw new CannotTranslateHGVSVariant("Currently unsupported HGVS variant type in "
					+ ntChange.toHGVSString());
		}
	}

	/**
	 * Implementation of translation for {@link NucleotideSubstitution} objects
	 */
	private GenomeVariant translateNucleotideSubstitution(TranscriptModel tm, SequenceType sequenceType,
			NucleotideSubstitution ntSub) throws CannotTranslateHGVSVariant {
		final NucleotidePointLocation pos = ntSub.getPosition();
		final String fromNT = ntSub.getFromNT();
		final String toNT = ntSub.getToNT();

		if (fromNT.length() != 1 || toNT.length() != 1)
			throw new CannotTranslateHGVSVariant("Both source and target sequence must have length 1 in "
					+ ntSub.toHGVSString());

		return new GenomeVariant(translateNucleotidePointLocation(tm, pos, sequenceType), fromNT, toNT, tm.getStrand())
				.withStrand(Strand.FWD);
	}

	/**
	 * Convert NucleotidePointLocation on a transcript
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

	private GenomePosition translateNonCodingNucleotidePointLocation(TranscriptModel tm, NucleotidePointLocation pos)
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

}
