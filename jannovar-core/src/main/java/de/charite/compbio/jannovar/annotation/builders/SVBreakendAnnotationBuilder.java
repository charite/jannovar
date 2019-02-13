package de.charite.compbio.jannovar.annotation.builders;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.annotation.SVAnnotation;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.SVBreakend;
import de.charite.compbio.jannovar.reference.TranscriptModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

/**
 * Build annotation for {@link SVBreakend}.
 * <p>
 * Variant effects used by this builder are:
 *
 * <ul>
 * <li>{@link VariantEffect#CODING_SEQUENCE_VARIANT}</li>
 * <li>{@link VariantEffect#CODING_TRANSCRIPT_VARIANT}</li>
 * <li>{@link VariantEffect#DOWNSTREAM_GENE_VARIANT}</li>
 * <li>{@link VariantEffect#FIVE_PRIME_UTR_EXON_VARIANT}</li>
 * <li>{@link VariantEffect#FIVE_PRIME_UTR_INTRON_VARIANT}</li>
 * <li>{@link VariantEffect#TRANSLOCATION}</li>
 * <li>{@link VariantEffect#INTERGENIC_VARIANT}</li>
 * <li>{@link VariantEffect#INTRON_VARIANT}</li>
 * <li>{@link VariantEffect#MOBILE_ELEMENT_INSERTION}</li>
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
final public class SVBreakendAnnotationBuilder extends SVAnnotationBuilder {

	/**
	 * The SVBreakend to be annotated.
	 */
	private final SVBreakend svBND;

	/**
	 * Construct the builder.
	 *
	 * @param transcript The transcript to annotate for.
	 * @param svBND      The {@link SVBreakend} to annotate for.
	 */
	public SVBreakendAnnotationBuilder(TranscriptModel transcript, SVBreakend svBND) {
		super(transcript, svBND);
		this.svBND = svBND;
	}

	@Override
	public SVAnnotation build() {
		final SVAnnotation annoLeft = buildForInterval(new GenomeInterval(
			svBND.getGenomePos().shifted(svBND.getPosCILowerBound()),
			svBND.getPosCIUpperBound() - svBND.getPosCILowerBound()
		));
		final SVAnnotation annoRight = buildForInterval(new GenomeInterval(
			svBND.getGenomePos2().shifted(svBND.getPos2CILowerBound()),
			svBND.getPos2CIUpperBound() - svBND.getPos2CILowerBound()
		));

		final VariantEffect mostPathogenicLeft = annoLeft.getEffects().iterator().next();
		final VariantEffect mostPathogenicRight = annoRight.getEffects().iterator().next();
		if (mostPathogenicLeft.compareTo(mostPathogenicRight) <= 0) {
			return annoLeft;
		} else {
			return annoRight;
		}
	}

	/**
	 * Build annotation for the given {@code changeInterval}.
	 */
	private SVAnnotation buildForInterval(GenomeInterval changeInterval) {
		if (changeInterval.length() == 0) {
			changeInterval = changeInterval.withMorePadding(1, 0);
		}

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
		final EnumSet<VariantEffect> effects = EnumSet.noneOf(VariantEffect.class);
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
	private SVAnnotation buildAnnotation(VariantEffect... effects) {
		return new SVAnnotation(svBND, transcript, buildEffectSet(EnumSet.copyOf(Arrays.asList(effects))));
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
		tmpEffects.add(VariantEffect.TRANSLOCATION);
		tmpEffects.add(VariantEffect.STRUCTURAL_VARIANT);
		return Sets.immutableEnumSet(tmpEffects);
	}

}
