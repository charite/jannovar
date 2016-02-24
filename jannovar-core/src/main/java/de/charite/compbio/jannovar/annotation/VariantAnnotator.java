package de.charite.compbio.jannovar.annotation;

import java.util.ArrayList;

import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderDispatcher;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.annotation.builders.StructuralVariantAnnotationBuilder;
import de.charite.compbio.jannovar.data.Chromosome;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.intervals.IntervalArray;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;

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
public class VariantAnnotator {

	/** configuration for annotation builders */
	final private AnnotationBuilderOptions options;

	/** {@link ReferenceDictionary} to use for genome information. */
	final private ReferenceDictionary refDict;

	/** {@link Chromosome}s with their {@link TranscriptModel} objects. */
	final private ImmutableMap<Integer, Chromosome> chromosomeMap;

	/**
	 * Construct new VariantAnnotator, given a chromosome map.
	 *
	 * @param refDict
	 *            {@link ReferenceDictionary} with information about the genome.
	 * @param chromosomeMap
	 *            chromosome map to use for the annotator.
	 * @param options
	 *            configuration to use for building the annotations
	 */
	public VariantAnnotator(ReferenceDictionary refDict, ImmutableMap<Integer, Chromosome> chromosomeMap,
			AnnotationBuilderOptions options) {
		this.refDict = refDict;
		this.chromosomeMap = chromosomeMap;
		this.options = options;
	}

	// TODO(holtgrem): Remove this?
	/**
	 * Convenience function for obtaining an {@link VariantAnnotations} from genome change in primitive types.
	 *
	 * Forwards to {@link #buildAnnotations(int, int, String, String, PositionType)} and we recommend to use this
	 * function directly.
	 *
	 * @param position
	 *            The start position of the variant on this chromosome (one-based numbering)
	 * @param ref
	 *            String representation of the reference sequence affected by the variant
	 * @param alt
	 *            String representation of the variant (alt) sequence
	 * @param posType
	 *            the position type to use
	 * @return {@link VariantAnnotations} for the given genome change
	 * @throws AnnotationException
	 *             on problems building the annotation list
	 */
	public VariantAnnotations buildAnnotations(int chr, int position, String ref, String alt, PositionType posType)
			throws AnnotationException {
		// Get chromosome by id.
		if (chromosomeMap.get(chr) == null)
			throw new AnnotationException(String.format("Could not identify chromosome \"%d\"", chr));

		// Build the GenomeChange to build annotation for.
		GenomePosition pos = new GenomePosition(refDict, Strand.FWD, chr, position, posType);
		GenomeVariant change = new GenomeVariant(pos, ref, alt);

		return buildAnnotations(change);
	}

	/**
	 * Main entry point to getting Annovar-type annotations for a variant identified by chromosomal coordinates.
	 *
	 * When we get to this point, the client code has identified the right chromosome, and we are provided the
	 * coordinates on that chromosome.
	 *
	 * @param change
	 *            the {@link GenomeVariant} to annotate
	 * @return {@link VariantAnnotations} for the genome change
	 * @throws AnnotationException
	 *             on problems building the annotation list
	 */
	public VariantAnnotations buildAnnotations(GenomeVariant change) throws AnnotationException {
		// Short-circuit in the case of symbolic changes/alleles. These could be SVs, large duplications, etc., that are
		// described as shortcuts in the VCF file. We cannot annotate these yet.
		if (change.isSymbolic())
			return VariantAnnotations.buildEmptyList(change);

		// Get genomic change interval and reset the factory.
		final GenomeInterval changeInterval = change.getGenomeInterval();

		// Get the TranscriptModel objects that overlap with changeInterval.
		final Chromosome chr = chromosomeMap.get(change.getChr());
		IntervalArray<TranscriptModel>.QueryResult qr;
		if (changeInterval.length() == 0)
			qr = chr.getTMIntervalTree().findOverlappingWithPoint(changeInterval.getBeginPos());
		else
			qr = chr.getTMIntervalTree().findOverlappingWithInterval(changeInterval.getBeginPos(),
					changeInterval.getEndPos());
		ArrayList<TranscriptModel> candidateTranscripts = new ArrayList<>(qr.getEntries());

		final AnnotationCollector annotationCollector = new AnnotationCollector();
		// Handle the case of no overlapping transcript. Then, create intergenic, upstream, or downstream annotations
		// and return the result.
		boolean isStructuralVariant = (change.getRef().length() >= 1000 || change.getAlt().length() >= 1000);
		if (candidateTranscripts.isEmpty()) {
			if (isStructuralVariant)
				buildSVAnnotation(annotationCollector, change, null);
			else
				buildNonSVAnnotation(annotationCollector, change, qr.getLeft(), qr.getRight());
            return new VariantAnnotations(change, annotationCollector.getAnnotations());
        }

		// If we reach here, then there is at least one transcript that overlaps with the query. Iterate over these
		// transcripts and collect annotations for each (they are collected in annotationCollector).
		for (TranscriptModel tm : candidateTranscripts)
			if (isStructuralVariant)
				buildSVAnnotation(annotationCollector, change, tm);
			else
				buildNonSVAnnotation(annotationCollector, change, tm);

        return new VariantAnnotations(change, annotationCollector.getAnnotations());
    }

	private void buildSVAnnotation(AnnotationCollector annotationCollector, GenomeVariant change, TranscriptModel transcript) throws AnnotationException {
		annotationCollector.addStructuralAnnotation(new StructuralVariantAnnotationBuilder(transcript, change).build());
	}

	private void buildNonSVAnnotation(AnnotationCollector annotationCollector, GenomeVariant change, TranscriptModel leftNeighbor, TranscriptModel rightNeighbor)
			throws AnnotationException {
		buildNonSVAnnotation(annotationCollector, change, leftNeighbor);
		buildNonSVAnnotation(annotationCollector, change, rightNeighbor);
	}

	private void buildNonSVAnnotation(AnnotationCollector annotationCollector, GenomeVariant change, TranscriptModel transcript) throws InvalidGenomeVariant {
		if (transcript != null) // TODO(holtgrew): Is not necessarily an exonic annotation!
			annotationCollector.addExonicAnnotation(new AnnotationBuilderDispatcher(transcript, change, options).build());
	}

}
