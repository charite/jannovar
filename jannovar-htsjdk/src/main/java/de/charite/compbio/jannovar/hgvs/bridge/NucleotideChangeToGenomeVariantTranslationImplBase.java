package de.charite.compbio.jannovar.hgvs.bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.charite.compbio.jannovar.htsjdk.GenomeRegionSequenceExtractor;
import de.charite.compbio.jannovar.impl.util.DNAUtils;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.Strand;

public class NucleotideChangeToGenomeVariantTranslationImplBase {

	/** logger instance to use */
	protected static final Logger LOGGER = LoggerFactory
			.getLogger(NucleotideChangeToGenomeVariantTranslationImplBase.class);

	/** extraction of {@link GenomeInterval} from FASTA files */
	protected final GenomeRegionSequenceExtractor seqExtractor;
	/** implementation of position conversion */
	protected final NucleotideLocationConverter posConverter;

	public NucleotideChangeToGenomeVariantTranslationImplBase(GenomeRegionSequenceExtractor seqExtractor) {
		this.seqExtractor = seqExtractor;
		this.posConverter = new NucleotideLocationConverter();
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