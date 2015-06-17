package de.charite.compbio.jannovar.hgvs.bridge;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideChange;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideDeletion;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideSubstitution;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;
import de.charite.compbio.jannovar.htsjdk.GenomeRegionSequenceExtractor;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Helper for converting a {@link NucleotideChange} to a {@link GenomeVariant}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
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
	 * Translate single-change {@link SingleAlleleNucleotideVariant} into a {@link GenomeVariant}
	 *
	 * @param variant
	 *            {@link SingleAlleleNucleotideVariant} to translate
	 * @return {@link GenomeVariant} resulting from the conversion, possibly annotated with some warning messages
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

		// perform the translation
		ResultWithWarnings<GenomeVariant> result;
		if (ntChange instanceof NucleotideSubstitution) {
			result = new NucleotideSubstitutionToGenomeVariantTranslationImpl(seqExtractor).run(tm,
					variant.getSeqType(), (NucleotideSubstitution) ntChange);
		} else if (ntChange instanceof NucleotideDeletion) {
			result = new NucleotideDeletionToGenomeVariantTranslationImpl(seqExtractor).run(tm, variant.getSeqType(),
					(NucleotideDeletion) ntChange);
		} else {
			throw new CannotTranslateHGVSVariant("Currently unsupported HGVS variant type in "
					+ ntChange.toHGVSString());
		}

		// handle any warning messages and return result value
		for (String msg : result.getWarnings())
			LOGGER.warn(msg);
		return result.getValue();
	}

}
