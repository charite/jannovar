package jannovar.reference;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;

/**
 * Class for building immutable {@link TranscriptInfo} objects field-by-field.
 *
 * In this sense, it is similar to {@link StringBuilder} for building {@link String} objects.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptInfoBuilder {
	/** The explicit strand of the target transcript. */
	private char strand = '?';

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
	private int geneID = 0;

	/**
	 * Reset the builder into the state after initialization.
	 */
	public void reset() {
		strand = '?';
		accession = null;
		geneSymbol = null;
		txRegion = null;
		cdsRegion = null;
		exonRegions.clear();
		sequence = null;
		geneID = 0;
	}

	/**
	 * @return {@link TranscriptInfo} with the currently set configuration.
	 */
	public TranscriptInfo make() {
		// Build list of immutable exons in the correct order.
		ImmutableList.Builder<GenomeInterval> builder = new ImmutableList.Builder<GenomeInterval>();
		if (strand == '+') {
			for (int i = 0; i < exonRegions.size(); ++i)
				builder.add(exonRegions.get(i));
		} else {
			for (int i = 0, j = exonRegions.size() - 1; i < exonRegions.size(); ++i, --j)
				builder.add(exonRegions.get(j).withStrand(strand));
		}

		// Create new TranscriptInfo object.
		return new TranscriptInfo(accession, geneSymbol, txRegion, cdsRegion, builder.build(), sequence, geneID, null);
	}

	/**
	 * @return the strand
	 */
	public char getStrand() {
		return strand;
	}

	/**
	 * @param strand
	 *            the strand to set
	 */
	public void setStrand(char strand) {
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
	public GenomeInterval getTxRegion() {
		return txRegion;
	}

	/**
	 * @param txRegion
	 *            the txRegion to set
	 */
	public void setTxRegion(GenomeInterval txRegion) {
		this.txRegion = txRegion;
	}

	/**
	 * @return the cdsRegion
	 */
	public GenomeInterval getCdsRegion() {
		return cdsRegion;
	}

	/**
	 * @param cdsRegion
	 *            the cdsRegion to set
	 */
	public void setCdsRegion(GenomeInterval cdsRegion) {
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
	 *            the exonRegions to set
	 */
	public void setExonRegions(ArrayList<GenomeInterval> exonRegions) {
		this.exonRegions = exonRegions;
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
	public int getGeneID() {
		return geneID;
	}

	/**
	 * @param geneID
	 *            the geneID to set
	 */
	public void setGeneID(int geneID) {
		this.geneID = geneID;
	}

}
