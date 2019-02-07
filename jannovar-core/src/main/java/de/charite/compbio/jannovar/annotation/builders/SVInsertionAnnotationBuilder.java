package de.charite.compbio.jannovar.annotation.builders;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.annotation.SVAnnotation;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.SVInsertion;
import de.charite.compbio.jannovar.reference.TranscriptModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

/**
 * Build annotation for {@link SVInsertion}.
 * <p>
 * Variant effects used by this builder are:
 *
 * <ul>
 * <li>{@link VariantEffect#CODING_SEQUENCE_VARIANT}</li>
 * <li>{@link VariantEffect#CODING_TRANSCRIPT_VARIANT}</li>
 * <li>{@link VariantEffect#DOWNSTREAM_GENE_VARIANT}</li>
 * <li>{@link VariantEffect#FIVE_PRIME_UTR_EXON_VARIANT}</li>
 * <li>{@link VariantEffect#FIVE_PRIME_UTR_INTRON_VARIANT}</li>
 * <li>{@link VariantEffect#INSERTION}</li>
 * <li>{@link VariantEffect#INTERGENIC_VARIANT}</li>
 * <li>{@link VariantEffect#INTRON_VARIANT}</li>
 * <li>{@link VariantEffect#NON_CODING_TRANSCRIPT_VARIANT}</li>
 * <li>{@link VariantEffect#STRUCTURAL_VARIANT}</li>
 * <li>{@link VariantEffect#THREE_PRIME_UTR_EXON_VARIANT}</li>
 * <li>{@link VariantEffect#THREE_PRIME_UTR_INTRON_VARIANT}</li>
 * <li>{@link VariantEffect#UPSTREAM_GENE_VARIANT}</li>
 * </ul>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
@Immutable
final public class SVInsertionAnnotationBuilder extends SVAnnotationBuilder {

	/**
	 * The SVInsertion to be annotated.
	 */
	private final SVInsertion svIns;

	/**
	 * Construct the builder.
	 *
	 * @param transcript The transcript to annotate for.
	 * @param svIns      The {@link SVInsertion} to annotate for.
	 */
	public SVInsertionAnnotationBuilder(TranscriptModel transcript, SVInsertion svIns) {
		super(transcript, svIns);
		this.svIns = svIns;
	}

	@Override
	public SVAnnotation build() {
		final GenomeInterval changeInterval = svIns.getGenomeInterval();

		// Go over the different cases from most to least pathogenic step and return most pathogenic one.

		if (so.overlapsWithExon(changeInterval)) {
			return buildExonOverlapAnnotation(changeInterval);
		} else if (changeInterval.overlapsWith(transcript.getTXRegion())) {
			return buildIntronAnnotation(changeInterval);
		} else if (so.overlapsWithUpstreamRegion(changeInterval)) {
			return buildAnnotation(VariantEffect.UPSTREAM_GENE_VARIANT);
		} else if (so.overlapsWithDownstreamRegion(changeInterval)) {
			return buildAnnotation(VariantEffect.DOWNSTREAM_GENE_VARIANT);
		} else {
			return buildAnnotation(VariantEffect.INTERGENIC_VARIANT);
		}
	}

	/**
	 * Return annotation for variant overlapping with exons.
	 */
	private SVAnnotation buildExonOverlapAnnotation(GenomeInterval changeInterval) {
		if (so.overlapsWithCDS(changeInterval)) {
			return buildAnnotation(VariantEffect.CODING_SEQUENCE_VARIANT);
		} else if (so.overlapsWithFivePrimeUTR(changeInterval)) {
			return buildAnnotation(VariantEffect.FIVE_PRIME_UTR_EXON_VARIANT);
		} else {
			return buildAnnotation(VariantEffect.THREE_PRIME_UTR_EXON_VARIANT);
		}
	}

	/**
	 * Return annotation for variant in intron.
	 */
	private SVAnnotation buildIntronAnnotation(GenomeInterval changeInterval) {
		final EnumSet effects = EnumSet.noneOf(VariantEffect.class);
		if (so.overlapsWithCDS(changeInterval)) {
			return buildAnnotation(VariantEffect.INTRON_VARIANT);
		} else if (so.overlapsWithFivePrimeUTR(changeInterval)) {
			return buildAnnotation(VariantEffect.FIVE_PRIME_UTR_INTRON_VARIANT);
		} else {
			return buildAnnotation(VariantEffect.THREE_PRIME_UTR_INTRON_VARIANT);
		}
	}

	/**
	 * Return annotation built from {@code effect}.
	 */
	private SVAnnotation buildAnnotation(VariantEffect ...effects) {
		return new SVAnnotation(svIns, transcript, buildEffectSet(EnumSet.copyOf(Arrays.asList(effects))));
	}

	/**
	 * Return augmented {@code effects} based on the transcript alone (coding/non-coding transcript).
	 */
	private ImmutableSet<VariantEffect> buildEffectSet(Collection<VariantEffect> effects) {
		final EnumSet<VariantEffect> tmpEffects;
		if (transcript.isCoding()) {
			tmpEffects = EnumSet.of(VariantEffect.CODING_TRANSCRIPT_VARIANT);
		} else {
			tmpEffects = EnumSet.of(VariantEffect.NON_CODING_TRANSCRIPT_VARIANT);
		}
		tmpEffects.addAll(effects);
		if (!effects.contains(VariantEffect.INTERGENIC_VARIANT)) {
			tmpEffects.add(VariantEffect.INSERTION);
		}
		tmpEffects.add(VariantEffect.STRUCTURAL_VARIANT);
		return Sets.immutableEnumSet(tmpEffects);
	}

}
