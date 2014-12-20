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

	/** Chromosomes with their TranscriptModel objects. */
	private HashMap<Integer, Chromosome> chromosomeMap = null;

	/**
	 * This object will be used to prioritize the annotations and to choose the one(s) to report. For instance, if we
	 * have both an intronic and a nonsense mutation, just report the nonsense mutation. Note that the object will be
	 * initialized once in the constructor of the Chromosome class and will be reset for each new annotation, rather
	 * than creating a new object for each variation. Also note that the constructor takes an integer value with which
	 * the lists of potential annotations get initialized. We will take 2*SPAN because this is the maximum number of
	 * annotations any variant can get with this program.
	 */
	private AnnotationCollector annovarFactory = null;

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
		this.annovarFactory = new AnnotationCollector(20);
	}

	// TODO(holtgrem): Rename to buildAnnotationList()?
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
	 * @param position
	 *            The start position of the variant on this chromosome (one-based numbering)
	 * @param ref
	 *            String representation of the reference sequence affected by the variant
	 * @param alt
	 *            String representation of the variant (alt) sequence
	 * @return a list of {@link jannovar.annotation.Annotation Annotation} objects corresponding to the mutation
	 *         described by the object (often just one annotation, but potentially multiple ones).
	 * @throws jannovar.exception.AnnotationException
	 */
	public AnnotationList getAnnotationList(int c, int position, String ref, String alt) throws AnnotationException {
		// Get chromosome by id.
		Chromosome chr = chromosomeMap.get(c);
		if (chr == null) {
			String e = String.format("Could not identify chromosome \"%d\"", c);
			throw new AnnotationException(e);
		}

		/* The following command "resets" the annovarFactory object */
		this.annovarFactory.clearAnnotationLists();

		// TODO(holtgrew): Make zero-based in the future.
		// Build the GenomeChange to build annotation for.
		GenomeChange change = new GenomeChange(new GenomePosition(refDict, '+', c, position, PositionType.ONE_BASED),
				ref, alt);
		GenomeInterval changeInterval = change.getGenomeInterval().withPositionType(PositionType.ONE_BASED);

		// Get the TranscriptModel objects that overlap with changeInterval.
		IntervalTree<TranscriptInfo>.QueryResult qr = chr.getTMIntervalTree().search(changeInterval.beginPos,
				changeInterval.endPos);
		ArrayList<TranscriptInfo> candidateTranscripts = qr.result;

		// Check whether this is a SV, if true then also perform a big intervals search.
		boolean isStructuralVariant = (ref.length() >= 1000 || alt.length() >= 1000);
		if (isStructuralVariant && ref.length() >= 1000)
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
		for (TranscriptInfo tm : candidateTranscripts) {
			if (isStructuralVariant)
				buildSVAnnotation(change, tm);
			else
				buildNonSVAnnotation(change, tm);
		}

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
