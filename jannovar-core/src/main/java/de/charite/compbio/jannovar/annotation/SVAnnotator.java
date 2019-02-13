package de.charite.compbio.jannovar.annotation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.charite.compbio.jannovar.annotation.builders.SVAnnotationBuilderDispatcher;
import de.charite.compbio.jannovar.data.Chromosome;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.intervals.IntervalArray;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.SVGenomeVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptSequenceOntologyDecorator;

import java.util.ArrayList;

// TODO(holtgrem): We should directly pass in a JannovarData object after adding the interval trees to it. Then, this should be fine.

/**
 * Main driver class for annotating structural variants.
 * <p>
 * Given, a chromosome map, objects of this class can be used to annotate variants represented by {@link
 * SVGenomeVariant}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class SVAnnotator {

	/**
	 * {@link ReferenceDictionary} to use for genome information.
	 */
	final private ReferenceDictionary refDict;

	/**
	 * {@link Chromosome}s with their {@link TranscriptModel} objects.
	 */
	final private ImmutableMap<Integer, Chromosome> chromosomeMap;

	/**
	 * Construct new VariantAnnotator, given a chromosome map.
	 *
	 * @param refDict       {@link ReferenceDictionary} with information about the genome.
	 * @param chromosomeMap chromosome map to use for the annotator.
	 */
	public SVAnnotator(ReferenceDictionary refDict, ImmutableMap<Integer, Chromosome> chromosomeMap) {
		this.refDict = refDict;
		this.chromosomeMap = chromosomeMap;
	}

	/**
	 * Main entry point to getting Annovar-type annotations for a {@link SVGenomeVariant}.
	 * <p>
	 * When we get to this point, the client code has identified the right chromosome, and we are provided the
	 * coordinates on that chromosome.
	 *
	 * @param change the {@link SVGenomeVariant} to annotate
	 * @return {@link SVAnnotations} for the genome change
	 * @throws AnnotationException on problems building the annotation list
	 */
	public SVAnnotations buildAnnotations(SVGenomeVariant change) throws AnnotationException {
		// Get genomic change interval(s)
		final ImmutableList.Builder<GenomeInterval> builder = ImmutableList.builder();
		try {
			builder.add(change.getGenomeInterval());
		} catch (IllegalArgumentException e) {
			builder.add(
				new GenomeInterval(
					change.getGenomePos().shifted(change.getPosCILowerBound()),
					change.getPosCIUpperBound() - change.getPosCILowerBound()));
			builder.add(
				new GenomeInterval(
					change.getGenomePos2().shifted(change.getPos2CILowerBound()),
					change.getPos2CIUpperBound() - change.getPos2CILowerBound()));
		}
		final ImmutableList<GenomeInterval> changeIntervals = builder.build();

		// Collect all annotations for all overlapping transcripts.
		ArrayList<SVAnnotation> annotations = new ArrayList<>();
		for (GenomeInterval changeInterval : changeIntervals) {
			final GenomeInterval paddedChangeInterval = changeInterval.withMorePadding(
				TranscriptSequenceOntologyDecorator.UPSTREAM_LENGTH,
				TranscriptSequenceOntologyDecorator.DOWNSTREAM_LENGTH
			);
			// Get the TranscriptModel objects that overlap with changeIntervals.
			final Chromosome chr = chromosomeMap.get(change.getChr());
			IntervalArray<TranscriptModel>.QueryResult qr;
			if (paddedChangeInterval.length() == 0) {
				qr = chr.getTMIntervalTree().findOverlappingWithPoint(paddedChangeInterval.getBeginPos());
			} else {
				qr = chr.getTMIntervalTree().findOverlappingWithInterval(paddedChangeInterval.getBeginPos(),
					paddedChangeInterval.getEndPos());
			}

			ArrayList<TranscriptModel> candidateTranscripts = new ArrayList<>(qr.getEntries());
			if (candidateTranscripts.isEmpty()) {
				if (qr.getLeft() != null) {
					candidateTranscripts.add(qr.getLeft());
				}
				if (qr.getRight() != null) {
					candidateTranscripts.add(qr.getRight());
				}
			}

			// Handle all candidate transcripts.
			for (TranscriptModel tm : candidateTranscripts) {
				annotations.add(new SVAnnotationBuilderDispatcher(tm, change).build());
			}
		}

		return new SVAnnotations(change, annotations);
	}

}
