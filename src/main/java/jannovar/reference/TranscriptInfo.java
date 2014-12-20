package jannovar.reference;

import jannovar.common.Immutable;

import java.io.Serializable;

import com.google.common.collect.ImmutableList;

/**
 * The core information about a transcript, in an immutable object.
 *
 * Similar to TranscriptModel in the represented data but with less query functions. Further, we translate the
 * coordinates to the reverse strand for transcripts on the reverse strand.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class TranscriptInfo implements Serializable {
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

	/**
	 * Genomic interval with CDS begin/end.
	 *
	 * @note Note that in Jannovar, the CDS region includes the start and stop codon.
	 */
	public final GenomeInterval cdsRegion;

	/** Genomic intervals with the exons, order is dictated by strand of transcript. */
	public final ImmutableList<GenomeInterval> exonRegions;

	/** cDNA sequence of the spliced RNA of this known gene transcript. */
	public final String sequence;

	/**
	 * The Gene id that corresponds to the transcript model. Note that this information is taken from
	 * knownToLocusLink.txt or modified Ensembl Gene ids. Default is <code>-1</code>
	 */
	public int geneID = -1;

	/** Class version (for serialization). */
	public static final long serialVersionUID = 1L;

	/**
	 * Initialize the TranscriptInfo object from the given parameters.
	 */
	public TranscriptInfo(String accession, String geneSymbol, GenomeInterval txRegion, GenomeInterval cdsRegion,
			ImmutableList<GenomeInterval> exonRegions, String sequence, int geneID) {
		this.accession = accession;
		this.geneSymbol = geneSymbol;
		this.txRegion = txRegion;
		this.cdsRegion = cdsRegion;
		this.exonRegions = exonRegions;
		this.sequence = sequence;
		this.geneID = geneID;
		checkForConsistency();
	}

	/** @return the strand of the transcript */
	public char getStrand() {
		return txRegion.strand;
	}

	/** @return the chromosome of the transcript */
	public int getChr() {
		return txRegion.chr;
	}

	/**
	 * @return <tt>true</tt> if this is a gene-coding transcript, marked by <tt>cdsRegion</tt> being empty.
	 */
	public boolean isCoding() {
		return (this.cdsRegion.beginPos < this.cdsRegion.endPos);
	}

	/**
	 * @return the length of the coding exon sequence
	 */
	public int cdsTranscriptLength() {
		int result = 0;
		for (GenomeInterval region : exonRegions)
			result += region.intersection(cdsRegion).length();
		return result;
	}

	/**
	 * @return the length of the exon sequences
	 */
	public int transcriptLength() {
		int result = 0;
		for (GenomeInterval region : exonRegions)
			result += region.length();
		return result;
	}

	/**
	 * @param i
	 *            0-based index of the intron's region to return
	 * @return {@link GenomeInterval} with the intron's region
	 */
	public GenomeInterval intronRegion(int i) {
		// TODO(holtgrem): test me!
		GenomeInterval exonRegionL = exonRegions.get(i).withPositionType(PositionType.ZERO_BASED);
		GenomeInterval exonRegionR = exonRegions.get(i + 1).withPositionType(PositionType.ZERO_BASED);
		return new GenomeInterval(exonRegionL.refDict, exonRegionL.strand, exonRegionL.chr, exonRegionL.endPos,
				exonRegionR.beginPos, PositionType.ZERO_BASED);
	}

	/**
	 * Ensures that the strands are consistent.
	 */
	private void checkForConsistency() {
		char strand = txRegion.strand;
		assert (txRegion.strand == strand);
		assert (cdsRegion.strand == strand);
		for (GenomeInterval region : exonRegions)
			assert (region.strand == strand);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accession == null) ? 0 : accession.hashCode());
		result = prime * result + ((cdsRegion == null) ? 0 : cdsRegion.hashCode());
		result = prime * result + ((exonRegions == null) ? 0 : exonRegions.hashCode());
		result = prime * result + geneID;
		result = prime * result + ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
		result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
		result = prime * result + ((txRegion == null) ? 0 : txRegion.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TranscriptInfo other = (TranscriptInfo) obj;
		if (accession == null) {
			if (other.accession != null)
				return false;
		} else if (!accession.equals(other.accession))
			return false;
		if (cdsRegion == null) {
			if (other.cdsRegion != null)
				return false;
		} else if (!cdsRegion.equals(other.cdsRegion))
			return false;
		if (exonRegions == null) {
			if (other.exonRegions != null)
				return false;
		} else if (!exonRegions.equals(other.exonRegions))
			return false;
		if (geneID != other.geneID)
			return false;
		if (geneSymbol == null) {
			if (other.geneSymbol != null)
				return false;
		} else if (!geneSymbol.equals(other.geneSymbol))
			return false;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		if (txRegion == null) {
			if (other.txRegion != null)
				return false;
		} else if (!txRegion.equals(other.txRegion))
			return false;
		return true;
	}

}
