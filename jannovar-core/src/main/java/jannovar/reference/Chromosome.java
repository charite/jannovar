package jannovar.reference;

import jannovar.impl.interval.Interval;
import jannovar.impl.intervals.IntervalArray;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.ImmutableList;

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
public final class Chromosome {

	// TODO(holtgrem): Add ReferenceDictionary

	/**
	 * Chromosome. chr1...chr22 are 1..22, chrX=23, chrY=24, mito=25. Ignore other chromosomes. TODO. Add more flexible
	 * way of dealing with scaffolds etc.
	 */
	private final int chromosome;

	/**
	 * An {@link IntervalArray} that contains all of the {@link TranscriptInfo} objects for transcripts located on this
	 * chromosome.
	 */
	private IntervalArray<TranscriptInfo> tmIntervalTree = null;

	/**
	 * Initialize object.
	 *
	 * @param c
	 *            the chromosome
	 * @param tmIntervalTree
	 *            An interval tree with all transcripts on this chromosome.
	 */
	public Chromosome(int c, IntervalArray<TranscriptInfo> tmIntervalTree) {
		this.chromosome = c;
		this.tmIntervalTree = tmIntervalTree;
	}

	/**
	 * @return String representation of name of chromosome, e.g., chr2
	 */
	public String getChromosomeName() {
		// TODO(holtgrem): Wrong for chrM, chrX etc, use ReferenceDictionary here
		return String.format("chr%d", chromosome);
	}

	/**
	 * @return Number of genes contained in this chromosome.
	 */
	public int getNumberOfGenes() {
		return this.tmIntervalTree.size();
	}

	/**
	 * This function constructs a HashMap<Byte,Chromosome> map of Chromosome objects in which the {@link TranscriptInfo}
	 * objects are entered into an {@link IntervalArray} for the appropriate Chromosome.
	 *
	 * @param tmist
	 *            A list of all TranscriptModels for the entire genome
	 * @return a Map of Chromosome objects with all 22+2+M chromosomes.
	 */
	public static HashMap<Integer, Chromosome> constructChromosomeMapWithIntervalTree(
			ImmutableList<TranscriptInfo> tmist) {
		HashMap<Integer, Chromosome> chromosomeMap = new HashMap<Integer, Chromosome>();
		/* 1. First sort the TranscriptModel objects by Chromosome. */
		HashMap<Integer, ArrayList<TranscriptInfo>> chrMap = new HashMap<Integer, ArrayList<TranscriptInfo>>();
		for (TranscriptInfo tm : tmist) {
			if (!chrMap.containsKey(tm.getChr()))
				chrMap.put(tm.getChr(), new ArrayList<TranscriptInfo>());

			ArrayList<TranscriptInfo> lst = chrMap.get(tm.getChr());
			lst.add(tm);
		}
		/* 2. Now construct an Interval Tree for each chromosome and add the lists of Intervals */
		for (Integer chrom : chrMap.keySet()) {
			IntervalArray<TranscriptInfo> itree = new IntervalArray<TranscriptInfo>(chrMap.get(chrom),
					new TranscriptIntervalEndExtractor());
			Chromosome chr = new Chromosome(chrom, itree);
			chromosomeMap.put(chrom, chr);
		}
		return chromosomeMap;
	}

	/**
	 * @return the {@link IntervalArray} of the chromosome.
	 */
	public IntervalArray<TranscriptInfo> getTMIntervalTree() {
		return tmIntervalTree;
	}

}
