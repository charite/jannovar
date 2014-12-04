package jannovar.reference;

import jannovar.interval.Interval;
import jannovar.interval.IntervalTree;

import java.util.ArrayList;
import java.util.HashMap;

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
	private final byte chromosome;

	/**
	 * An {@link IntervalTree} that contains all of the {@link TranscriptModel} objects for transcripts located on this
	 * chromosome.
	 */
	private IntervalTree<TranscriptModel> tmIntervalTree = null;

	/**
	 * The constructor expects to get a byte representing 1..22 or 23=X_CHROMSOME, or 24=Y_CHROMOSOME (see
	 * {@link jannovar.common.Constants Constants}).
	 *
	 * @param c
	 *            the chromosome
	 * @param tmIntervalTree
	 *            An interval tree with all transcripts on this chromosome.
	 */
	public Chromosome(byte c, IntervalTree<TranscriptModel> tmIntervalTree) {
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
	public static HashMap<Byte, Chromosome> constructChromosomeMapWithIntervalTree(ArrayList<TranscriptModel> tmist) {
		HashMap<Byte, Chromosome> chromosomeMap = new HashMap<Byte, Chromosome>();
		/* 1. First sort the TranscriptModel objects by Chromosome. */
		HashMap<Byte, ArrayList<Interval<TranscriptModel>>> chrMap = new HashMap<Byte, ArrayList<Interval<TranscriptModel>>>();
		for (TranscriptModel tm : tmist) {
			byte chrom = tm.getChromosome();
			if (!chrMap.containsKey(chrom)) {
				chrMap.put(chrom, new ArrayList<Interval<TranscriptModel>>());
			}
			ArrayList<Interval<TranscriptModel>> lst = chrMap.get(chrom);
			Interval<TranscriptModel> in = new Interval<TranscriptModel>(tm.getTXStart(), tm.getTXEnd(), tm);
			lst.add(in);
		}
		/* 2. Now construct an Interval Tree for each chromosome and add the lists of Intervals */
		for (Byte chrom : chrMap.keySet()) {
			ArrayList<Interval<TranscriptModel>> transModelList = chrMap.get(chrom);
			IntervalTree<TranscriptModel> itree = new IntervalTree<TranscriptModel>(transModelList);
			Chromosome chr = new Chromosome(chrom, itree);
			chromosomeMap.put(chrom, chr);
		}
		return chromosomeMap;
	}

	/**
	 * @return the {@link IntervalTree} of the chromosome.
	 */
	public IntervalTree<TranscriptModel> getTMIntervalTree() {
		return tmIntervalTree;
	}
}
