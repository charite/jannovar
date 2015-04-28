package de.charite.compbio.jannovar.filter;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Builder for {@link Gene}.
 */
class GeneBuilder {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(GeneBuilder.class);

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
			LOGGER.trace("Adding first transcript {} to gene {}.", new Object[] { tm, name });
			builder.add(tm);
			tmpModels.add(tm);
			return;
		}

		// Transcript must be within 10kbp of a previously seen one. Otherwise, we get too large genes from RNA
		// transcript matches.
		final int MORE_PADDING = 10000;
		final GenomeInterval tmRegion = tm.getTXRegion().withMorePadding(MORE_PADDING);
		for (TranscriptModel model : tmpModels)
			if (model.getTXRegion().overlapsWith(tmRegion)) {
				LOGGER.trace("Adding next transcript {} to gene {}.", new Object[] { tm, name });
				builder.add(tm);
				tmpModels.add(tm);
				return;
			}
		LOGGER.trace("Transcript {} does not fit to previous transcripts of {}.", new Object[] { tm, name });
	}

	public Gene build() {
		final Gene gene = new Gene(refDict, name, builder.build());
		LOGGER.trace("Creating gene {} (lengt={})", new Object[] { gene, name });
		return gene;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}