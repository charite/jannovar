package de.charite.compbio.jannovar.filter;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.io.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Builder for {@link Gene}.
 */
class GeneBuilder {
	private final ReferenceDictionary refDict;
	private String name = null;
	private final ImmutableList.Builder<TranscriptModel> builder = new ImmutableList.Builder<TranscriptModel>();
	private final ArrayList<TranscriptModel> tmpModels = new ArrayList<TranscriptModel>();

	public GeneBuilder(ReferenceDictionary refDict, String name) {
		this.refDict = refDict;
		this.name = name;
	}

	public void addTranscriptModel(TranscriptModel tm) {
		if (tmpModels.isEmpty()) { // always add first
			System.err.println("Adding first transcript " + tm.accession + " " + tm.txRegion + " to gene " + name);
			builder.add(tm);
			tmpModels.add(tm);
			return;
		}

		// Transcript must be within 10kbp of a previously seen one. Otherwise, we get too large genes from RNA
		// transcript matches.
		final int MORE_PADDING = 10000;
		final GenomeInterval tmRegion = tm.txRegion.withMorePadding(MORE_PADDING);
		for (TranscriptModel model : tmpModels)
			if (model.txRegion.overlapsWith(tmRegion)) {
				System.err.println("Adding transcript " + tm.accession + " " + tm.txRegion + " to gene " + name);
				builder.add(tm);
				tmpModels.add(tm);
				return;
			}
		System.err.println("Transcript " + tm.accession + " " + tm.txRegion
				+ " does not fit to previous transcripts of " + name);
	}

	public Gene build() {
		final Gene gene = new Gene(refDict, name, builder.build());
		System.err.println("CREATING GENE\t" + gene.toString() + "\tLENGTH\t" + gene.region.length());
		return gene;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}