package de.charite.compbio.jannovar.io;

import java.io.Serializable;

import de.charite.compbio.jannovar.impl.intervals.IntervalArray;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * This class encapsulates a chromosome and all of the genes its contains. It is intended to be used together with the
 * {@link TranscriptInfo} class to make a list of gene models that will be used to annotate chromosomal variants. We use
 * an {@link IntervalArray} to store all of the {@link TranscriptInfo} objects that belong to this Chromosome and to
 * search for all transcripts that overlap with any given variant. Note that the IntervalTree class has functionality
 * also to find the neighbors (5' and 3') of the closest gene in order to find the right and left genes of intergenic
 * variants and to find the correct gene in the cases of complex regions of the chromosome with one gene located in the
 * intron of the next or with overlapping genes.
 *
 * Note that the {@link Interval} objects in the interval tree are defined by the transcription start and stop sites of
 * the isoform.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Marten Jaeger <marten.jaeger@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class Chromosome implements Serializable {

	/** serial version ID */
	private static final long serialVersionUID = 1L;

	/** reference dictionary to use */
	public final ReferenceDictionary refDict;

	/**
	 * Chromosome. chr1...chr22 are 1..22, chrX=23, chrY=24, mito=25. Ignore other chromosomes. TODO. Add more flexible
	 * way of dealing with scaffolds etc.
	 */
	public final int chrID;

	/**
	 * An {@link IntervalArray} that contains all of the {@link TranscriptInfo} objects for transcripts located on this
	 * chromosome.
	 */
	public final IntervalArray<TranscriptModel> tmIntervalTree;

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

	/**
	 * @return String representation of name of chromosome, e.g., <code>"chr2"</code>
	 */
	public String getChromosomeName() {
		return refDict.contigName.get(chrID);
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
