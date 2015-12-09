package de.charite.compbio.jannovar.data;

import java.io.Serializable;

import de.charite.compbio.jannovar.impl.intervals.IntervalArray;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * This class encapsulates a chromosome and all of the genes its contains. It is intended to be used together with the
 * {@link TranscriptModel} class to make a list of gene models that will be used to annotate chromosomal variants. We
 * use an {@link IntervalArray} to store all of the {@link TranscriptModel} objects that belong to this Chromosome and
 * to search for all transcripts that overlap with any given variant. Note that the IntervalTree class has functionality
 * also to find the neighbors (5' and 3') of the closest gene in order to find the right and left genes of intergenic
 * variants and to find the correct gene in the cases of complex regions of the chromosome with one gene located in the
 * intron of the next or with overlapping genes.
 *
 * Note that the {@link GenomeInterval} objects in the interval tree are defined by the transcription start and stop
 * sites of the isoform.
 *
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @author <a href="mailto:marten.jaeger@charite.de">Marten Jaeger</a>
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class Chromosome implements Serializable {

	/** serial version ID */
	private static final long serialVersionUID = 1L;

	/** reference dictionary to use */
	private final ReferenceDictionary refDict;

	/** numeric chromsome ID */
	private final int chrID;

	/**
	 * An {@link IntervalArray} that contains all of the {@link TranscriptInfo} objects for transcripts located on this
	 * chromosome.
	 */
	private final IntervalArray<TranscriptModel> tmIntervalTree;

	/**
	 * Initialize object.
	 *
	 * @param refDict
	 *            the {@link ReferenceDictionary} to use
	 * @param chrID
	 *            the chromosome
	 * @param tmIntervalTree
	 *            An interval tree with all transcripts on this chromosome.
	 */
	public Chromosome(ReferenceDictionary refDict, int chrID, IntervalArray<TranscriptModel> tmIntervalTree) {
		this.refDict = refDict;
		this.chrID = chrID;
		this.tmIntervalTree = tmIntervalTree;
	}

	/** @return reference dictionary to use */
	public ReferenceDictionary getRefDict() {
		return refDict;
	}

	/** @return numeric chromsome ID */
	public int getChrID() {
		return chrID;
	}

	/**
	 * @return String representation of name of chromosome, e.g., <code>"chr2"</code>
	 */
	public String getChromosomeName() {
		return refDict.getContigIDToName().get(chrID);
	}

	/**
	 * @return Number of genes contained in this chromosome.
	 */
	public int getNumberOfGenes() {
		return this.tmIntervalTree.size();
	}

	/**
	 * @return the {@link IntervalArray} of the chromosome.
	 */
	public IntervalArray<TranscriptModel> getTMIntervalTree() {
		return tmIntervalTree;
	}

}
