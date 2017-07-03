package de.charite.compbio.jannovar.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.impl.intervals.IntervalArray;
import de.charite.compbio.jannovar.reference.TranscriptIntervalEndExtractor;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * This data type is used for serialization after downloading.
 *
 * Making this class immutable makes it a convenient serializeable read-only database.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@Immutable
public final class JannovarData implements Serializable {

	/** Serial version ID. */
	private static final long serialVersionUID = 3L;

	/** map from chromosome ID to {@link Chromosome} */
	private final ImmutableMap<Integer, Chromosome> chromosomes;

	/** map from transcript accession to {@link TranscriptModel} instance. */
	private final ImmutableMap<String, TranscriptModel> tmByAccession;

	/** map from transcript accession to {@link TranscriptModel} instance. */
	private final ImmutableMultimap<String, TranscriptModel> tmByGeneSymbol;

	/** information about reference lengths and identities */
	private final ReferenceDictionary refDict;

	/**
	 * Initialize the object with the given values.
	 *
	 * @param refDict
	 *            the {@link ReferenceDictionary} to use in this object
	 * @param transcriptModels
	 *            the list of {@link TranscriptModel} objects to use in this object
	 */
	public JannovarData(ReferenceDictionary refDict, ImmutableList<TranscriptModel> transcriptModels) {
		this.refDict = refDict;
		this.chromosomes = makeChromsomes(refDict, transcriptModels);
		this.tmByAccession = makeTMByAccession(transcriptModels);
		this.tmByGeneSymbol = makeTMByGeneSymbol(transcriptModels);
	}

	/** @return map from chromosome ID to {@link Chromosome} */
	public ImmutableMap<Integer, Chromosome> getChromosomes() {
		return chromosomes;
	}

	/** @return map from transcript accession to {@link TranscriptModel} instance. */
	public ImmutableMap<String, TranscriptModel> getTmByAccession() {
		return tmByAccession;
	}

	/** @return map from transcript accession to {@link TranscriptModel} instance. */
	public ImmutableMultimap<String, TranscriptModel> getTmByGeneSymbol() {
		return tmByGeneSymbol;
	}

	/** @return information about reference lengths and identities */
	public ReferenceDictionary getRefDict() {
		return refDict;
	}

	/**
	 * @param transcriptModels
	 *            set of {@link TranscriptModel}s to build multi-mapping for
	 * @return multi-mapping from gene symbol to {@link TranscriptModel}
	 */
	private static ImmutableMultimap<String, TranscriptModel> makeTMByGeneSymbol(
			ImmutableList<TranscriptModel> transcriptModels) {
		ImmutableMultimap.Builder<String, TranscriptModel> builder = new ImmutableMultimap.Builder<String, TranscriptModel>();
		for (TranscriptModel tm : transcriptModels)
			builder.put(tm.getGeneSymbol(), tm);
		return builder.build();
	}

	/**
	 * @param transcriptModels
	 *            set of {@link TranscriptModel}s to build mapping for
	 * @return mapping from gene symbol to {@link TranscriptModel}
	 */
	private static ImmutableMap<String, TranscriptModel> makeTMByAccession(
			ImmutableList<TranscriptModel> transcriptModels) {
		ImmutableMap.Builder<String, TranscriptModel> builder = new ImmutableMap.Builder<String, TranscriptModel>();
		for (TranscriptModel tm : transcriptModels)
			builder.put(tm.getAccession(), tm);
		return builder.build();
	}

	/**
	 * This function constructs a HashMap<Byte,Chromosome> map of Chromosome objects in which the {@link TranscriptModel}
	 * objects are entered into an {@link IntervalArray} for the appropriate Chromosome.
	 *
	 * @param refDict
	 *            the {@link ReferenceDictionary} to use for the construction
	 * @param transcriptModels
	 *            list of {@link TranscriptModel} objects with the transcripts of all chromosomes
	 * @return a mapping from numeric chromsome ID to {@link Chromosome} object
	 */
	private static ImmutableMap<Integer, Chromosome> makeChromsomes(ReferenceDictionary refDict,
			ImmutableList<TranscriptModel> transcriptModels) {
		ImmutableMap.Builder<Integer, Chromosome> builder = new ImmutableMap.Builder<Integer, Chromosome>();

		// First, factorize the TranscriptModel objects by chromosome ID.

		// create hash map for this
		HashMap<Integer, ArrayList<TranscriptModel>> transcripts = new HashMap<Integer, ArrayList<TranscriptModel>>();
		for (Integer chrID : refDict.getContigIDToName().keySet())
			transcripts.put(chrID, new ArrayList<TranscriptModel>());
		// distribute TranscriptModel lists
		for (TranscriptModel transcript : transcriptModels)
			transcripts.get(transcript.getChr()).add(transcript);

		// Then, construct an interval tree for each chromosome and add the lists of intervals.
		for (Integer chrID : transcripts.keySet()) {
			IntervalArray<TranscriptModel> iTree = new IntervalArray<TranscriptModel>(transcripts.get(chrID),
					new TranscriptIntervalEndExtractor());
			builder.put(chrID, new Chromosome(refDict, chrID, iTree));
		}

		return builder.build();
	}

}
