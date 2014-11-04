package jannovar.reference;

import jannovar.common.Constants;

/**
 * The core information about a transcript, in an immutable object.
 *
 * Similar to TranscriptModel in the represented data but with less query functions. Further, we translate the
 * coordinates to the reverse strand for transcripts on the reverse strand.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptInfo {
	/**
	 * Accession number of the transcript (e.g., the UCSC knownGene id - uc011nca.2). The version number may be
	 * included.
	 */
	public final String accession;

	/**
	 * Gene symbol of the known Gene. Can be null for some genes. Note that in annovar, $name2 corresponds to the
	 * geneSymbol if available, otherwise the kgID is used.
	 */
	public final String geneSymbol;

	/** Genomic interval with transcript begin/end. */
	public final GenomeInterval txRegion;

	/** Genomic interval with CDS begin/end. */
	public final GenomeInterval cdsRegion;

	// TODO(holtgrem): use some immutable container here as well
	/** Genomic intervals with the exons, order is dictated by strand of transcript. */
	public final GenomeInterval[] exonRegions;

	/** cDNA sequence of the spliced RNA of this known gene transcript. */
	public final String sequence;

	/**
	 * The Gene id that corresponds to the transcript model. Note that this information is taken from
	 * knownToLocusLink.txt or modified Ensembl Gene ids.
	 */
	public int geneID = Constants.UNINITIALIZED_INT;

	/** Class version (for serialization). */
	public static final long serialVersionUID = 1L;

	/**
	 * Initialize the TranscriptInfo object with the TranscriptModel data.
	 *
	 * @param tm
	 *            transcript data source
	 */
	public TranscriptInfo(TranscriptModel tm) {
		accession = tm.getAccessionNumber();
		geneSymbol = tm.getGeneSymbol();
		sequence = tm.getSequence();

		final char strand = tm.getStrand();
		final byte chr = tm.getChromosome();

		// create temporary forward transcription interval, then assign to this.txRegion with conversion of coordinates.
		GenomeInterval fwdTXRegion = new GenomeInterval('+', chr, tm.getTXStart(), tm.getTXEnd(),
				PositionType.ONE_BASED);
		txRegion = fwdTXRegion.withStrand(strand);
		// do the same for the cds region
		GenomeInterval fwdCDSRegion = new GenomeInterval('+', chr, tm.getCDSStart(), tm.getCDSEnd(),
				PositionType.ONE_BASED);
		cdsRegion = fwdCDSRegion.withStrand(strand);

		exonRegions = new GenomeInterval[tm.getExonCount()];
		if (strand == '+')
		{
			for (int i = 0; i < tm.getExonCount(); ++i) {
				GenomeInterval exonFwdRegion = new GenomeInterval('+', chr, tm.getExonStart(i), tm.getExonEnd(i));
				exonRegions[i] = exonFwdRegion.withStrand(strand);
			}
		}
		else
		{
			for (int i = 0, j = tm.getExonCount() - 1; i < tm.getExonCount(); ++i, --j) {
				GenomeInterval exonFwdRegion = new GenomeInterval('+', chr, tm.getExonStart(i), tm.getExonEnd(i));
				exonRegions[j] = exonFwdRegion.withStrand(strand);
			}
		}

		// ensure that the strands are consistent
		checkForConsistency();
	}

	/** @return the strand of the transcript */
	public char getStrand() {
		return txRegion.getStrand();
	}

	/** @return the chromosome of the transcript */
	public int getChr() {
		return txRegion.getChr();
	}

	/**
	 * @return <tt>true</tt> if this is a gene-coding transcript, marked by <tt>txRegion</tt>'s start position being
	 *         before its end position.
	 */
	public boolean isCoding() {
		return (this.txRegion.getBeginPos() != this.txRegion.getEndPos() + 1);
	}

	/**
	 * Ensures that the strands are consistent.
	 */
	private void checkForConsistency() {
		char strand = txRegion.getStrand();
		assert (txRegion.getStrand() == strand);
		assert (cdsRegion.getStrand() == strand);
		for (int i = 0; i < exonRegions.length; ++i)
			assert (exonRegions[i].getStrand() == strand);
	}
}
