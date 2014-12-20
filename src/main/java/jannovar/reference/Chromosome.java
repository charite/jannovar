package jannovar.reference;

import jannovar.interval.Interval;
import jannovar.interval.IntervalTree;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.ImmutableList;

/**
 * This class encapsulates a chromosome and all of the genes its contains. It is intended to be used together with the
 * {@link jannovar.reference.TranscriptModel TranscriptModel} class to make a list of gene models that will be used to
 * annotate chromosomal variants. We use an {@link jannovar.interval.IntervalTree IntervalTree} to store all of the
 * {@link jannovar.reference.TranscriptModel TranscriptModel} objects that belong to this Chromosome and to search for
 * all transcripts that overlap with any given variant. Note that the IntervalTree class has functionality also to find
 * the neighbors (5' and 3') of the closest gene in order to find the right and left genes of intergenic variants and to
 * find the correct gene in the cases of complex regions of the chromosome with one gene located in the intron of the
 * next or with overlapping genes.
 * <P>
 * Note that the {@link jannovar.interval.Interval Interval} objects in the interval tree are defined by the
 * transcription start and stop sites of the isoform.
 *
 * @author Peter N Robinson, Marten Jäger
 * @version 0.32 (15 April, 2014)
 */
public final class Chromosome {
	/**
	 * Chromosome. chr1...chr22 are 1..22, chrX=23, chrY=24, mito=25. Ignore other chromosomes. TODO. Add more flexible
	 * way of dealing with scaffolds etc.
	 */
	private final int chromosome;

	/**
	 * An {@link IntervalTree} that contains all of the {@link TranscriptModel} objects for transcripts located on this
	 * chromosome.
	 */
	private IntervalTree<TranscriptInfo> tmIntervalTree = null;

	/**
	 * The constructor expects to get a byte representing 1..22 or 23=X_CHROMSOME, or 24=Y_CHROMOSOME (see
	 * {@link jannovar.common.Constants Constants}).
	 *
	 * @param c
	 *            the chromosome
	 * @param tmIntervalTree
	 *            An interval tree with all transcripts on this chromosome.
	 */
	public Chromosome(int c, IntervalTree<TranscriptInfo> tmIntervalTree) {
		this.chromosome = c;
		this.tmIntervalTree = tmIntervalTree;
	}

	/**
	 * @return String representation of name of chromosome, e.g., chr2
	 */
	public String getChromosomeName() {
		// TODO(holtgrem): Wrong for chrM, chrX etc.
		return String.format("chr%d", chromosome);
	}

	/**
	 * @return Number of genes contained in this chromosome.
	 */
	public int getNumberOfGenes() {
		return this.tmIntervalTree.size();
	}

	/**
	 * This function constructs a HashMap<Byte,Chromosome> map of Chromosome objects in which the
	 * {@link jannovar.reference.TranscriptModel TranscriptModel} objects are entered into an
	 * {@link jannovar.interval.IntervalTree IntervalTree} for the appropriate Chromosome.
	 *
	 * @param tmist
	 *            A list of all TranscriptModels for the entire genome
	 * @return a Map of Chromosome objects with all 22+2+M chromosomes.
	 */
	public static HashMap<Integer, Chromosome> constructChromosomeMapWithIntervalTree(
			ImmutableList<TranscriptInfo> tmist) {
		HashMap<Integer, Chromosome> chromosomeMap = new HashMap<Integer, Chromosome>();
		/* 1. First sort the TranscriptModel objects by Chromosome. */
		HashMap<Integer, ArrayList<Interval<TranscriptInfo>>> chrMap = new HashMap<Integer, ArrayList<Interval<TranscriptInfo>>>();
		for (TranscriptInfo tm : tmist) {
			if (!chrMap.containsKey(tm.getChr()))
				chrMap.put(tm.getChr(), new ArrayList<Interval<TranscriptInfo>>());

			ArrayList<Interval<TranscriptInfo>> lst = chrMap.get(tm.getChr());
			final int txStartPos = tm.txRegion.withPositionType(PositionType.ONE_BASED).beginPos;
			final int txEndPos = tm.txRegion.withPositionType(PositionType.ONE_BASED).endPos;
			Interval<TranscriptInfo> in = new Interval<TranscriptInfo>(txStartPos, txEndPos, tm);
			lst.add(in);
		}
		/* 2. Now construct an Interval Tree for each chromosome and add the lists of Intervals */
		for (Integer chrom : chrMap.keySet()) {
			ArrayList<Interval<TranscriptInfo>> transModelList = chrMap.get(chrom);
			IntervalTree<TranscriptInfo> itree = new IntervalTree<TranscriptInfo>(transModelList);
			Chromosome chr = new Chromosome(chrom, itree);
			chromosomeMap.put(chrom, chr);
		}
		return chromosomeMap;
	}

	/**
	 * @return the {@link IntervalTree} of the chromosome.
	 */
	public IntervalTree<TranscriptInfo> getTMIntervalTree() {
		return tmIntervalTree;
	}
}
