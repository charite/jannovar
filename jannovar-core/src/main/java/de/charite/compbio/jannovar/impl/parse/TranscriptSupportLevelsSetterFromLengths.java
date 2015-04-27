package de.charite.compbio.jannovar.impl.parse;

import java.util.Collection;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;
import de.charite.compbio.jannovar.reference.TranscriptSupportLevels;

/**
 * Set the transcript support level from transcript lengths.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
class TranscriptSupportLevelsSetterFromLengths {

	/** {@link Logger} to use for logging */
	private static final Logger LOGGER = LoggerFactory.getLogger(TranscriptSupportLevelsSetterFromLengths.class);

	public static void run(Collection<TranscriptModelBuilder> values) {
		setDefaultLevels(values);
		updateLevelsOfLongest(values);
	}

	/**
	 * Set the level of the longest {@link TranscriptModelBuilder} objects for each gene to
	 * {@link TranscriptSupportLevels.LONGEST_TRANSCRIPT}.
	 *
	 * @param values
	 *            the builders to analyze
	 */
	private static void updateLevelsOfLongest(Collection<TranscriptModelBuilder> builders) {
		// obtain the longest builder for each gene
		HashMap<String, TranscriptModelBuilder> longest = new HashMap<String, TranscriptModelBuilder>();
		for (TranscriptModelBuilder builder : builders) {
			final String geneID = builder.getGeneSymbol();
			if (!longest.containsKey(geneID)) {
				longest.put(geneID, builder);
				continue;
			}

			final GenomeInterval longestRegion = longest.get(geneID).getTXRegion();
			final GenomeInterval txRegion = builder.getTXRegion();
			final boolean isShorter = (longestRegion.length() < txRegion.length());
			final boolean haveSameLength = (longestRegion.length() == txRegion.length());
			final boolean isLeftOf = (longestRegion.getGenomeBeginPos().isLt(txRegion.getGenomeBeginPos()));
			if (isShorter || (haveSameLength && isLeftOf))
				longest.put(geneID, builder);
		}

		// update level of longest
		for (TranscriptModelBuilder builder : longest.values()) {
			LOGGER.debug("Longest builder for {} is {}",
					new Object[] { builder.getGeneSymbol(), builder.getAccession() });
			builder.setTranscriptSupportLevel(TranscriptSupportLevels.LONGEST_TRANSCRIPT);
		}
	}

	/**
	 * Set the level of all {@link TranscriptModelBuilder} objects to {@link TranscriptSupportLevels.LOW_PRIORITY}.
	 *
	 * @param values
	 *            the {@link TranscriptModelBuilder} to set the levels of to
	 *            {@link TranscriptSupportLevels.LOW_PRIORITY}.
	 */
	private static void setDefaultLevels(Collection<TranscriptModelBuilder> builders) {
		for (TranscriptModelBuilder builder : builders)
			builder.setTranscriptSupportLevel(TranscriptSupportLevels.LOW_PRIORITY);
	}

}
