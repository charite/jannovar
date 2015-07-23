package de.charite.compbio.jannovar.reference;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;

/**
 * Class for building immutable {@link TranscriptModel} objects field-by-field.
 *
 * In this sense, it is similar to {@link StringBuilder} for building {@link String} objects.
 *
 * Usage:
 *
 * <pre>
 * {@link TranscriptModelBuilder} builder = new TranscriptInfoBuilder();
 * builder.{@link TranscriptModelBuilder#setStrand setStrand}('-');
 * builder.{@link TranscriptModelBuilder#setAccession setAccession}(&quot;&lt;accession&gt;&quot;);
 * // ...
 * {@link TranscriptModel} transcript = builder.{@link TranscriptModelBuilder#build build}();
 * </pre>
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptModelBuilder {

	/** The explicit strand of the target transcript. */
	private Strand strand = Strand.FWD;

	/** {@link TranscriptInfo#accession} of next {@link TranscriptInfo} to build. */
	private String accession = null;

	/** {@link TranscriptInfo#geneSymbol} of next {@link TranscriptInfo} to build. */
	private String geneSymbol = null;

	/** {@link TranscriptInfo#txRegion} of next {@link TranscriptInfo} to build. */
	private GenomeInterval txRegion = null;

	/** {@link TranscriptInfo#cdsRegion} of next {@link TranscriptInfo} to build. */
	private GenomeInterval cdsRegion = null;

	/** {@link TranscriptInfo#exonRegions} of next {@link TranscriptInfo} to build. */
	private ArrayList<GenomeInterval> exonRegions = new ArrayList<GenomeInterval>();

	/** {@link TranscriptInfo#accession} of next {@link TranscriptInfo} to build. */
	private String sequence = null;

	/** {@link TranscriptInfo#geneID} of next {@link TranscriptInfo} to build. */
	private String geneID = null;

	/**
	 * {@link TranscriptInfo#transcriptSupportLevel} of next {@link TranscriptInfo} to build.
	 *
	 * @see TranscriptSupportLevels
	 */
	private int transcriptSupportLevel = TranscriptSupportLevels.NOT_AVAILABLE;

	/**
	 * Reset the builder into the state after initialization.
	 */
	public void reset() {
		strand = Strand.FWD;
		accession = null;
		geneSymbol = null;
		txRegion = null;
		cdsRegion = null;
		exonRegions.clear();
		sequence = null;
		geneID = null;
		transcriptSupportLevel = TranscriptSupportLevels.NOT_AVAILABLE;
	}

	/**
	 * @return {@link TranscriptInfo} with the currently set configuration.
	 */
	public TranscriptModel build() {
		// Build list of immutable exons in the correct order.
		ImmutableList.Builder<GenomeInterval> builder = new ImmutableList.Builder<GenomeInterval>();
		if (exonRegions.size() > 0) {
			if (strand == exonRegions.get(0).getStrand()) {
				for (int i = 0; i < exonRegions.size(); ++i)
					builder.add(exonRegions.get(i));
			} else {
				for (int i = 0, j = exonRegions.size() - 1; i < exonRegions.size(); ++i, --j)
					builder.add(exonRegions.get(j).withStrand(strand));
			}
		}

		// Create new TranscriptInfo object.
		return new TranscriptModel(accession, geneSymbol, txRegion.withStrand(strand), cdsRegion.withStrand(strand),
				builder.build(), sequence, geneID, transcriptSupportLevel);
	}

	/**
	 * @return the strand
	 */
	public Strand getStrand() {
		return strand;
	}

	/**
	 * @param strand
	 *            the strand to set
	 */
	public void setStrand(Strand strand) {
		this.strand = strand;
	}

	/**
	 * @return the accession
	 */
	public String getAccession() {
		return accession;
	}

	/**
	 * @param accession
	 *            the accession to set
	 */
	public void setAccession(String accession) {
		this.accession = accession;
	}

	/**
	 * @return the geneSymbol
	 */
	public String getGeneSymbol() {
		return geneSymbol;
	}

	/**
	 * @param geneSymbol
	 *            the geneSymbol to set
	 */
	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}

	/**
	 * @return the txRegion
	 */
	public GenomeInterval getTXRegion() {
		return txRegion;
	}

	/**
	 * @param txRegion
	 *            the txRegion to set
	 */
	public void setTXRegion(GenomeInterval txRegion) {
		this.txRegion = txRegion;
	}

	/**
	 * @return the cdsRegion
	 */
	public GenomeInterval getCDSRegion() {
		return cdsRegion;
	}

	/**
	 * @param cdsRegion
	 *            the cdsRegion to set
	 */
	public void setCDSRegion(GenomeInterval cdsRegion) {
		this.cdsRegion = cdsRegion;
	}

	/**
	 * @return the exonRegions
	 */
	public ArrayList<GenomeInterval> getExonRegions() {
		return exonRegions;
	}

	/**
	 * @param exonRegions
	 *            the exonRegions to clear
	 */
	public void clearExonRegions(ArrayList<GenomeInterval> exonRegions) {
		this.exonRegions.clear();
	}

	/**
	 * @param exonRegion
	 *            interval to append
	 */
	public void addExonRegion(GenomeInterval exonRegion) {
		this.exonRegions.add(exonRegion);
	}

	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the geneID
	 */
	public String getGeneID() {
		return geneID;
	}

	/**
	 * @param geneID
	 *            the geneID to set
	 */
	public void setGeneID(String geneID) {
		this.geneID = geneID;
	}

	/**
	 * @return current transcript report level
	 * @see TranscriptSupportLevels
	 */
	public int getTranscriptSupportLevel() {
		return transcriptSupportLevel;
	}

	/**
	 * @param transcriptSupportLevel
	 *            set transcript resource level
	 * @see TranscriptSupportLevels
	 */
	public void setTranscriptSupportLevel(int transcriptSupportLevel) {
		this.transcriptSupportLevel = transcriptSupportLevel;
	}

}
