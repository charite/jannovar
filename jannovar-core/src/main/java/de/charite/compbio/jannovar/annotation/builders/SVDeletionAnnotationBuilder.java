package de.charite.compbio.jannovar.annotation.builders;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.annotation.SVAnnotation;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.SVDeletion;
import de.charite.compbio.jannovar.reference.TranscriptModel;

import java.util.Collection;
import java.util.EnumSet;

/**
 * Build annotation for {@link SVDeletion}.
 * <p>
 * Variant effects used by this builder are:
 *
 * <ul>
 * <li>{@link VariantEffect#CODING_TRANSCRIPT_INTRON_VARIANT}</li>
 * <li>{@link VariantEffect#CODING_TRANSCRIPT_VARIANT}</li>
 * <li>{@link VariantEffect#CODING_TRANSCRIPT_VARIANT}</li>
 * <li>{@link VariantEffect#DOWNSTREAM_GENE_VARIANT}</li>
 * <li>{@link VariantEffect#EXON_LOSS_VARIANT}</li>
 * <li>{@link VariantEffect#FEATURE_TRUNCATION}</li>
 * <li>{@link VariantEffect#FIVE_PRIME_UTR_TRUNCATION}</li>
 * <li>{@link VariantEffect#FRAMESHIFT_TRUNCATION}</li>
 * <li>{@link VariantEffect#INTERGENIC_VARIANT}</li>
 * <li>{@link VariantEffect#NON_CODING_TRANSCRIPT_EXON_VARIANT}</li>
 * <li>{@link VariantEffect#NON_CODING_TRANSCRIPT_EXON_VARIANT}</li>
 * <li>{@link VariantEffect#NON_CODING_TRANSCRIPT_INTRON_VARIANT}</li>
 * <li>{@link VariantEffect#NON_CODING_TRANSCRIPT_VARIANT}</li>
 * <li>{@link VariantEffect#START_LOST}</li>
 * <li>{@link VariantEffect#STOP_LOST}</li>
 * <li>{@link VariantEffect#THREE_PRIME_UTR_TRUNCATION}</li>
 * <li>{@link VariantEffect#TRANSCRIPT_ABLATION}</li>
 * <li>{@link VariantEffect#UPSTREAM_GENE_VARIANT}</li>
 * </ul>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
@Immutable
final public class SVDeletionAnnotationBuilder extends SVAnnotationBuilder {

	/**
	 * The SVDeletion to be annotated.
	 */
	private final SVDeletion svDel;

	/**
	 * Construct the builder.
	 *
	 * @param transcript The transcript to annotate for.
	 * @param svDel      The {@link SVDeletion} to annotate for.
	 */
	public SVDeletionAnnotationBuilder(TranscriptModel transcript, SVDeletion svDel) {
		super(transcript, svDel);
		this.svDel = svDel;
	}

	@Override
	public SVAnnotation build() {
		final GenomeInterval changeInterval = svDel.getGenomeInterval();

		// Go over the different cases from most to least pathogenic step and return most pathogenic one.

		if (changeInterval.contains(transcript.getTXRegion())) {
			return buildAnnotation(VariantEffect.TRANSCRIPT_ABLATION);
		} else if (so.containsExon(changeInterval)) {
			return buildExonLossAnnotation(changeInterval);
		} else if (so.overlapsWithExon(changeInterval)) {
			return buildExonOverlapAnnotation(changeInterval);
		} else if (changeInterval.overlapsWith(transcript.getTXRegion())) {
			// Overlaps with transcript but no exon, must be intronic.
			return buildAnnotation(transcript.isCoding() ?
				VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT :
				VariantEffect.NON_CODING_TRANSCRIPT_INTRON_VARIANT);
		} else if (so.overlapsWithUpstreamRegion(changeInterval)) {
			return buildAnnotation(VariantEffect.UPSTREAM_GENE_VARIANT);
		} else if (so.overlapsWithDownstreamRegion(changeInterval)) {
			return buildAnnotation(VariantEffect.DOWNSTREAM_GENE_VARIANT);
		} else {
			return buildAnnotation(VariantEffect.INTERGENIC_VARIANT);
		}
	}

	/**
	 * Return annotation built from {@code effect}.
	 */
	private SVAnnotation buildAnnotation(VariantEffect effect) {
		return new SVAnnotation(svDel, transcript, buildEffectSet(EnumSet.of(effect)));
	}

	/**
	 * Return annotation for variant overlapping but not containing exons.
	 */
	private SVAnnotation buildExonOverlapAnnotation(GenomeInterval changeInterval) {
		final EnumSet effects = EnumSet.noneOf(VariantEffect.class);
		if (so.overlapsWithTranslationalStartSite(changeInterval)) {
			effects.add(VariantEffect.START_LOST);
		}
		if (so.overlapsWithTranslationalStopSite(changeInterval)) {
			effects.add(VariantEffect.STOP_LOST);
		}
		if (effects.isEmpty()) {
			// only look at frameshift/in-frame if start/stop codon are not affected
			final int deletedCoding = transcript
				.getExonRegions()
				.stream()
				.map(exon -> exon
					.intersection(transcript.getCDSRegion())
					.intersection(changeInterval)
					.length())
				.mapToInt(Integer::intValue)
				.sum();
			if (deletedCoding % 3 == 0) {
				effects.add(VariantEffect.FEATURE_TRUNCATION);
			} else {
				effects.add(VariantEffect.FRAMESHIFT_TRUNCATION);
			}
		}
		return new SVAnnotation(svDel, transcript, buildEffectSet(effects));
	}

	/**
	 * Return exon loss annotation.
	 * <p>
	 * Results depends on overlap of {@code changeInterval} with UTRs.
	 */
	private SVAnnotation buildExonLossAnnotation(GenomeInterval changeInterval) {
		final EnumSet effects = EnumSet.of(VariantEffect.EXON_LOSS_VARIANT);
		if (so.overlapsWithThreePrimeUTRExon(changeInterval)) {
			effects.add(VariantEffect.THREE_PRIME_UTR_TRUNCATION);
		}
		if (so.overlapsWithFivePrimeUTRExon(changeInterval)) {
			effects.add(VariantEffect.FIVE_PRIME_UTR_TRUNCATION);
		}
		return new SVAnnotation(svDel, transcript, buildEffectSet(effects));
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
		return Sets.immutableEnumSet(tmpEffects);
	}

}
