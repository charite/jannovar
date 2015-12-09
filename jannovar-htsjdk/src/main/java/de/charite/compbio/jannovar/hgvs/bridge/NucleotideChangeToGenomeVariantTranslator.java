package de.charite.compbio.jannovar.hgvs.bridge;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideChange;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideDeletion;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideDuplication;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideIndel;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideInsertion;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideInversion;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideSubstitution;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;
import de.charite.compbio.jannovar.htsjdk.GenomeRegionSequenceExtractor;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Helper for converting a {@link NucleotideChange} to a {@link GenomeVariant}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class NucleotideChangeToGenomeVariantTranslator {

	/** logger instance to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(NucleotideChangeToGenomeVariantTranslator.class);

	/** transcript database and reference dictionary to use for translation */
	final private JannovarData jvDB;
	/** extraction of {@link GenomicRegion} from FASTA files */
	private final GenomeRegionSequenceExtractor seqExtractor;

	public NucleotideChangeToGenomeVariantTranslator(JannovarData jvDB, IndexedFastaSequenceFile indexedFasta) {
		this.jvDB = jvDB;
		this.seqExtractor = new GenomeRegionSequenceExtractor(indexedFasta);
	}

	/**
	 * Shortcut to {@link #translateNucleotideVariantToGenomeVariant(SingleAlleleNucleotideVariant, boolean)} with using
	 * <code>true</code> for the second parameter.
	 */
	public GenomeVariant translateNucleotideVariantToGenomeVariant(SingleAlleleNucleotideVariant variant)
			throws CannotTranslateHGVSVariant {
		return translateNucleotideVariantToGenomeVariant(variant, true);
	}

	/**
	 * Translate single-change {@link SingleAlleleNucleotideVariant} into a {@link GenomeVariant}
	 *
	 * @param variant
	 *            {@link SingleAlleleNucleotideVariant} to translate
	 * @param autocorrect
	 *            try to auto-correct mismatching reference sequence instead of throwing
	 *            {@link CannotTranslateHGVSVariant}
	 * @return {@link GenomeVariant} resulting from the conversion, possibly annotated with some warning messages
	 * @throws CannotTranslateHGVSVariant
	 *             in the case of problems such as more than one entry in the allele of <code>variant</code> or
	 *             unsupported {@link NucleotideChange}s
	 */
	public GenomeVariant translateNucleotideVariantToGenomeVariant(SingleAlleleNucleotideVariant variant,
			boolean autocorrect) throws CannotTranslateHGVSVariant {
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

		// perform the translation
		ResultWithWarnings<GenomeVariant> result;
		if (ntChange instanceof NucleotideSubstitution) {
			result = new NucleotideSubstitutionToGenomeVariantTranslationImpl(seqExtractor).run(tm,
					variant.getSeqType(), (NucleotideSubstitution) ntChange);
		} else if (ntChange instanceof NucleotideDeletion) {
			result = new NucleotideDeletionToGenomeVariantTranslationImpl(seqExtractor).run(tm, variant.getSeqType(),
					(NucleotideDeletion) ntChange);
		} else if (ntChange instanceof NucleotideDuplication) {
			result = new NucleotideDuplicationToGenomeVariantTranslationImpl(seqExtractor).run(tm,
					variant.getSeqType(), (NucleotideDuplication) ntChange);
		} else if (ntChange instanceof NucleotideIndel) {
			result = new NucleotideIndelToGenomeVariantTranslationImpl(seqExtractor).run(tm, variant.getSeqType(),
					(NucleotideIndel) ntChange);
		} else if (ntChange instanceof NucleotideInsertion) {
			result = new NucleotideInsertionToGenomeVariantTranslationImpl(seqExtractor).run(tm, variant.getSeqType(),
					(NucleotideInsertion) ntChange);
		} else if (ntChange instanceof NucleotideInversion) {
			result = new NucleotideInversionToGenomeVariantTranslationImpl(seqExtractor).run(tm, variant.getSeqType(),
					(NucleotideInversion) ntChange);
		} else {
			throw new CannotTranslateHGVSVariant("Currently unsupported HGVS variant type in "
					+ ntChange.toHGVSString());
		}

		if (!result.getWarnings().isEmpty() && !autocorrect)
			throw new CannotTranslateHGVSVariant("Had to auto-correct variant in translation: "
					+ Joiner.on("; ").join(result.getWarnings()));

		// handle any warning messages and return result value
		for (String msg : result.getWarnings())
			LOGGER.warn(msg);
		return result.getValue();
	}

}
