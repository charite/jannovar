package de.charite.compbio.jannovar.htsjdk;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.Strand;

/**
 * Extract sequence for a {@link GenomeInterval} from a {@link IndexedFastaSequenceFile}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class GenomeRegionSequenceExtractor {

	/** object to load sequences from */
	IndexedFastaSequenceFile indexedFile;

	public GenomeRegionSequenceExtractor(IndexedFastaSequenceFile indexedFile) {
		super();
		this.indexedFile = indexedFile;
	}

	/**
	 * Load sequence from the given <code>region</code> from {@link #indexedFile}
	 *
	 * @param region
	 *            {@link GenomeInterval} to load sequence for
	 * @return String with the selected sequenced loaded from {@link #indexedFile}.
	 */
	public String load(GenomeInterval region) {
		region = region.withStrand(Strand.FWD);
		String contigName = region.getRefDict().getContigIDToName().get(region.getChr());
		ReferenceSequence seq = indexedFile.getSubsequenceAt(contigName, region.getBeginPos() + 1, region.getEndPos());
		return new String(seq.getBases());
	}

}
