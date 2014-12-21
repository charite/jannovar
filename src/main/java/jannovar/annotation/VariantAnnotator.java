package jannovar.annotation;

import jannovar.annotation.builders.AnnotationBuilderDispatcher;
import jannovar.annotation.builders.StructuralVariantAnnotationBuilder;
import jannovar.exception.AnnotationException;
import jannovar.exception.InvalidGenomeChange;
import jannovar.interval.IntervalTree;
import jannovar.io.ReferenceDictionary;
import jannovar.reference.Chromosome;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomeInterval;
import jannovar.reference.GenomePosition;
import jannovar.reference.PositionType;
import jannovar.reference.TranscriptInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main driver class for annotating variants.
 *
 * Given, a chromosome map, objects of this class can be used to annotate variants identified by a genomic position
 * (chr, pos), a reference, and an alternative nucleotide String.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Marten Jaeger <marten.jaeger@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
public final class VariantAnnotator {

	/** {@link ReferenceDictionary} to use for genome information. */
	final ReferenceDictionary refDict;

	/** {@link Chromosome}s with their {@link TranscriptInfo} objects. */
	final private HashMap<Integer, Chromosome> chromosomeMap;

	/**
	 * This object will be used to prioritize the annotations and to choose the one(s) to report. For instance, if we
	 * have both an intronic and a nonsense mutation, just report the nonsense mutation. Note that the object will be
	 * initialized once in the constructor of the Chromosome class and will be reset for each new annotation, rather
	 * than creating a new object for each variation. Also note that the constructor takes an integer value with which
	 * the lists of potential annotations get initialized. We will take 2*SPAN because this is the maximum number of
	 * annotations any variant can get with this program.
	 */
	final private AnnotationCollector annovarFactory = new AnnotationCollector(20);

	/**
	 * Construct new VariantAnnotator, given a chromosome map.
	 *
	 * @param refDict
	 *            {@link ReferenceDictionary} with information about the genome.
	 * @param chromosomeMap
	 *            chromosome map to use for the annotator.
	 */
	public VariantAnnotator(ReferenceDictionary refDict, HashMap<Integer, Chromosome> chromosomeMap) {
		this.refDict = refDict;
		this.chromosomeMap = chromosomeMap;
	}

	/**
	 * Return {@link AnnotationList} for the genome change by the parameters.
	 *
	 * We recommend you to use {@link #buildAnnotationList(int, int, String, String, PositionType)} directly.
	 *
	 * @param position
	 *            The start position of the variant on this chromosome (one-based numbering)
	 * @param ref
	 *            String representation of the reference sequence affected by the variant
	 * @param alt
	 *            String representation of the variant (alt) sequence
	 * @param posType
	 *            the position type to use
	 * @return {@link AnnotationList} for the given genome change
	 * @throws AnnotationException
	 *             on problems building the annotation list
	 */
	public AnnotationList buildAnnotationList(int chr, int position, String ref, String alt, PositionType posType)
			throws AnnotationException {
		// Get chromosome by id.
		if (chromosomeMap.get(chr) == null)
			throw new AnnotationException(String.format("Could not identify chromosome \"%d\"", chr));

		// Build the GenomeChange to build annotation for.
		GenomePosition pos = new GenomePosition(refDict, '+', chr, position, posType);
		GenomeChange change = new GenomeChange(pos, ref, alt);

		return buildAnnotationList(change);
	}

	/**
	 * Main entry point to getting Annovar-type annotations for a variant identified by chromosomal coordinates. When we
	 * get to this point, the client code has identified the right chromosome, and we are provided the coordinates on
	 * that chromosome.
	 *
	 * The strategy for finding annotations is based on the perl code in Annovar. Roughly speaking, we identify a list
	 * of genes affected by the variant using the interval tree ({@link #itree}) and then annotated the variants
	 * accordingly. If no hit is found in the tree, we identify the left and right neighbor and annotate to intergenic,
	 * upstream or downstream
	 *
	 * @param change
	 *            the {@link GenomeChange} to annotate
	 * @return {@link AnnotationList} for the genome change
	 * @throws AnnotationException
	 *             on problems building the annotation list
	 */
	public AnnotationList buildAnnotationList(GenomeChange change) throws AnnotationException {
		// TODO(holtgrew): Make zero-based in the future?
		final GenomeInterval changeInterval = change.getGenomeInterval().withPositionType(PositionType.ONE_BASED);

		/* The following command "resets" the annovarFactory object */
		this.annovarFactory.clearAnnotationLists();

		// Get the TranscriptModel objects that overlap with changeInterval.
		final Chromosome chr = chromosomeMap.get(change.getChr());
		IntervalTree<TranscriptInfo>.QueryResult qr = chr.getTMIntervalTree().search(changeInterval.beginPos,
				changeInterval.endPos);
		ArrayList<TranscriptInfo> candidateTranscripts = qr.result;

		// Check whether this is a SV, if true then also perform a big intervals search.
		boolean isStructuralVariant = (change.ref.length() >= 1000 || change.alt.length() >= 1000);
		if (isStructuralVariant && change.ref.length() >= 1000)
			candidateTranscripts.addAll(chr.getTMIntervalTree().searchBigInterval(changeInterval.beginPos,
					changeInterval.endPos));

		// Handle the case of no overlapping transcript. Then, create intergenic, upstream, or downstream annotations
		// and return the result.
		if (candidateTranscripts.isEmpty()) {
			if (isStructuralVariant)
				buildSVAnnotation(change, null);
			else
				buildNonSVAnnotation(change, qr.getLeftNeighbor(), qr.getRightNeighbor());
			return annovarFactory.getAnnotationList();
		}

		// If we reach here, then there is at least one transcript that overlaps with the query. Iterate over these
		// transcripts and collect annotations for each (they are collected in annovarFactory).
		for (TranscriptInfo tm : candidateTranscripts)
			if (isStructuralVariant)
				buildSVAnnotation(change, tm);
			else
				buildNonSVAnnotation(change, tm);

		return annovarFactory.getAnnotationList();
	}

	private void buildSVAnnotation(GenomeChange change, TranscriptInfo transcript) throws AnnotationException {
		annovarFactory.addStructuralAnnotation(new StructuralVariantAnnotationBuilder(transcript, change).build());
	}

	private void buildNonSVAnnotation(GenomeChange change, TranscriptInfo leftNeighbor, TranscriptInfo rightNeighbor)
			throws AnnotationException {
		buildNonSVAnnotation(change, leftNeighbor);
		buildNonSVAnnotation(change, rightNeighbor);
	}

	private void buildNonSVAnnotation(GenomeChange change, TranscriptInfo transcript) throws InvalidGenomeChange {
		annovarFactory.addExonicAnnotation(new AnnotationBuilderDispatcher(transcript, change).build());
	}

}
