package de.charite.compbio.jannovar.htsjdk;

import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.Strand;

/**
 * Extract sequence for a {@link GenomeInterval} from a {@link IndexedFastaSequenceFile}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenomeRegionSequenceExtractor {

	/** Jannovar database for mapping between canonical name and name in FASTA */
	final JannovarData jannovarData;
	/** object to load sequences from */
	IndexedFastaSequenceFile indexedFile;

	public GenomeRegionSequenceExtractor(JannovarData jannovarData, IndexedFastaSequenceFile indexedFile) {
		super();
		this.jannovarData = jannovarData;
		this.indexedFile = indexedFile;
		if (this.indexedFile.getSequenceDictionary() == null) {
			throw new UncheckedJannovarException(
					"FASTA file has no sequence dictionary. Are you missing the REFERENCE.dict file? "
							+ "Hint: create with samtools dict (version >=1.2) or Picard.");
		}
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
		contigName = mapContigToFasta(contigName);
		ReferenceSequence seq = indexedFile.getSubsequenceAt(contigName, region.getBeginPos() + 1, region.getEndPos());
		return new String(seq.getBases());
	}

	/** Map contig name (from genome variant) to contig name in FASTA */
	private String mapContigToFasta(String contigName) {
		// Map genome variant's contig to unique ID
		Integer contigID = jannovarData.getRefDict().getContigNameToID().get(contigName);
		if (contigID == null)
			throw new UncheckedJannovarException("Unknown contig name " + contigName);
		// Try to find matching contig in fasta
		String nameInFasta = null;
		for (SAMSequenceRecord record : indexedFile.getSequenceDictionary().getSequences()) {
			if (jannovarData.getRefDict().getContigNameToID().containsKey(record.getSequenceName())) {
				nameInFasta = record.getSequenceName();
				break;
			}
		}
		if (nameInFasta == null)
			throw new UncheckedJannovarException("Could not find corresponding contig in FASTA for " + contigName);

		return nameInFasta;
	}

}
