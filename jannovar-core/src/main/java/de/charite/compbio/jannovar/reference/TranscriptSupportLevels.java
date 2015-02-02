package de.charite.compbio.jannovar.reference;

/**
 * Container for constants of transcript support levels.
 *
 * In the case that the transcript support level is not available, <code>6</code> is used as a substitute for
 * transcripts that are marked as primary in UCSC, <code>7</code> for the longest transcript (in absence of both level
 * and UCSC primary marking), and <code>8</code> for transcripts that fall neither into pseudo-level <code>6</code> and
 * <code>7</code>. A value of <code>-1</code> is used for N/A.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @see {@link http://www.ensembl.org/Help/Glossary?id=492}
 */
public interface TranscriptSupportLevels {

	/**
	 * the transcript was not analyzed for one of the following reasons:
	 *
	 * <ul>
	 * <li>pseudogene annotation, including transcribed pseudogenes</li>
	 * <li>human leukocyte antigen (HLA) transcript</li>
	 * <li>immunoglobin gene transcript</li>
	 * <li>T-cell receptor transcript</li>
	 * <li>single-exon transcript (will be included in a future version)</li>
	 * </ul>
	 */
	public final static int NOT_AVAILABLE = -1;

	/**
	 * All splice junctions of the transcript are supported by at least one non-suspect mRNA.
	 */
	public final static int TSL1 = 1;

	/**
	 * The best supporting mRNA is flagged as suspect or the support is from multiple ESTs.
	 */
	public final static int TSL2 = 2;

	/**
	 * The only support is from a single EST.
	 */
	public final static int TSL3 = 3;

	/**
	 * The best supporting EST is flagged as suspect.
	 */
	public final static int TSL4 = 4;

	/**
	 * No single transcript supports the model structure.
	 */
	public final static int TSL5 = 5;

	/**
	 * Annotated as canonical transcript by UCSC (used in absence of TSL).
	 */
	public final static int UCSC_CANONICAL = 6;

	/**
	 * Longest transcript of a gene (used in absence of any TSL annotation and UCSC annotation of this transcript).
	 */
	public final static int LONGEST_TRANSCRIPT = 7;

	/**
	 * Lowest available priority (used in absence of any TSL and UCSC annotation of this transcript).
	 */
	public final static int LOW_PRIORITY = 8;

}
