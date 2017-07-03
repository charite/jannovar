package de.charite.compbio.jannovar.reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ImmutableSortedSet.Builder;

/**
 * Class for building immutable {@link TranscriptModel} objects field-by-field.
 *
 * In this sense, it is similar to {@link StringBuilder} for building {@link String} objects.
 *
 * Usage:
 *
 * <pre>
 * {@link TranscriptModelBuilder} builder = new TranscriptModelBuilder();
 * builder.{@link TranscriptModelBuilder#setStrand setStrand}('-');
 * builder.{@link TranscriptModelBuilder#setAccession setAccession}(&quot;&lt;accession&gt;&quot;);
 * // ...
 * {@link TranscriptModel} transcript = builder.{@link TranscriptModelBuilder#build build}();
 * </pre>
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class TranscriptModelBuilder {

	/** The explicit strand of the target transcript. */
	private Strand strand = Strand.FWD;

	/** {@link TranscriptModel#accession} of next {@link TranscriptModel} to build. */
	private String accession = null;

	/** {@link TranscriptModel#geneSymbol} of next {@link TranscriptModel} to build. */
	private String geneSymbol = null;

	/** {@link TranscriptModel#txRegion} of next {@link TranscriptModel} to build. */
	private GenomeInterval txRegion = null;

	/** {@link TranscriptModel#cdsRegion} of next {@link TranscriptModel} to build. */
	private GenomeInterval cdsRegion = null;

	/** {@link TranscriptModel#exonRegions} of next {@link TranscriptModel} to build. */
	private ArrayList<GenomeInterval> exonRegions = new ArrayList<GenomeInterval>();

	/** {@link TranscriptModel#accession} of next {@link TranscriptModel} to build. */
	private String sequence = null;

	/** {@link TranscriptModel#geneID} of next {@link TranscriptModel} to build. */
	private String geneID = null;

	/** Map with alternative gene IDs */
	private HashMap<String, String> altGeneIDs = new HashMap<String, String>();;

	/**
	 * {@link TranscriptModel#transcriptSupportLevel} of next {@link TranscriptModel} to build.
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
		altGeneIDs.clear();
		transcriptSupportLevel = TranscriptSupportLevels.NOT_AVAILABLE;
	}

	/**
	 * @return {@link TranscriptModel} with the currently set configuration.
	 */
	public TranscriptModel build() {
		// Build list of immutable exons in the correct order.
		ImmutableSortedSet.Builder<GenomeInterval> builder = ImmutableSortedSet.<GenomeInterval> naturalOrder();
		if (exonRegions.size() > 0) {
			if (strand == exonRegions.get(0).getStrand()) {
				for (int i = 0; i < exonRegions.size(); ++i)
					builder.add(exonRegions.get(i));
			} else {
				for (int i = 0, j = exonRegions.size() - 1; i < exonRegions.size(); ++i, --j)
					builder.add(exonRegions.get(j).withStrand(strand));
			}
		}

		// Create new TranscriptModel object.
		return new TranscriptModel(accession, geneSymbol, txRegion.withStrand(strand), cdsRegion.withStrand(strand),
				ImmutableList.copyOf(builder.build()), sequence, geneID, transcriptSupportLevel, altGeneIDs);
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
	 * Clear exon regions list
	 */
	public void clearExonRegions() {
		this.exonRegions.clear();
	}

	/**
	 * @return alternative gene Ids
	 */
	public HashMap<String, String> getAltGeneIDs() {
		return altGeneIDs;
	}

	/**
	 * Clear alternative geneIDs map
	 */
	public void clearAltGeneIDs() {
		this.altGeneIDs.clear();
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
