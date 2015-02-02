package de.charite.compbio.jannovar.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.impl.intervals.IntervalArray;

/**
 * List of genes, accessible through an interval tree.
 */
class GeneList {
	/** overall gene list */
	public final ImmutableList<Gene> genes;
	/** map from numeric chromosome id to interval tree of genes */
	public final ImmutableMap<Integer, IntervalArray<Gene>> gIntervalTree;

	public GeneList(ImmutableList<Gene> genes) {
		this.genes = genes;
		this.gIntervalTree = buildIntervalTree();
	}

	private ImmutableMap<Integer, IntervalArray<Gene>> buildIntervalTree() {
		HashMap<Integer, ArrayList<Gene>> chrToGene = new HashMap<Integer, ArrayList<Gene>>();
		for (Gene gene : genes) {
			if (!chrToGene.containsKey(gene.region.chr))
				chrToGene.put(gene.region.chr, new ArrayList<Gene>());
			chrToGene.get(gene.region.chr).add(gene);
		}

		ImmutableMap.Builder<Integer, IntervalArray<Gene>> builder = new ImmutableMap.Builder<Integer, IntervalArray<Gene>>();
		for (Map.Entry<Integer, ArrayList<Gene>> entry : chrToGene.entrySet())
			builder.put(entry.getKey(), new IntervalArray<Gene>(entry.getValue(), new GeneIntervalEndExtractor()));
		return builder.build();
	}
}