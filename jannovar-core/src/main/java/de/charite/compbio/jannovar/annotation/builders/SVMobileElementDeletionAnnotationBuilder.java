package de.charite.compbio.jannovar.annotation.builders;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.annotation.SVAnnotation;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.reference.SVDeletion;
import de.charite.compbio.jannovar.reference.SVMobileElementDeletion;
import de.charite.compbio.jannovar.reference.TranscriptModel;

import java.util.EnumSet;

/**
 * Build annotation for {@link SVMobileElementDeletion}.
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
 * <li>{@link VariantEffect#MOBILE_ELEMENT_DELETION}</li>
 * </ul>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
@Immutable
final public class SVMobileElementDeletionAnnotationBuilder extends SVAnnotationBuilder {

	/**
	 * The SVMobileElementDeletion to be annotated.
	 */
	private final SVMobileElementDeletion svMEDel;

	/**
	 * Internally, we re-use {@link SVDeletionAnnotationBuilder}.
	 */
	private final SVDeletionAnnotationBuilder builder;

	/**
	 * Construct the builder.
	 *
	 * @param transcript The transcript to annotate for.
	 * @param svMEDel    The {@link SVMobileElementDeletion} to annotate for.
	 */
	public SVMobileElementDeletionAnnotationBuilder(TranscriptModel transcript, SVMobileElementDeletion svMEDel) {
		super(transcript, svMEDel);
		this.svMEDel = svMEDel;
		this.builder = new SVDeletionAnnotationBuilder(transcript, new SVDeletion(
			svMEDel.getGenomePos(), svMEDel.getGenomePos2(), svMEDel.getPosCILowerBound(), svMEDel.getPosCIUpperBound(),
			svMEDel.getPos2CILowerBound(), svMEDel.getPos2CIUpperBound()
		));
	}

	@Override
	public SVAnnotation build() {
		final SVAnnotation result = builder.build();
		final EnumSet<VariantEffect> effects = EnumSet.copyOf(result.getEffects());
		if (!effects.contains(VariantEffect.INTERGENIC_VARIANT) &&
			!effects.contains(VariantEffect.DOWNSTREAM_GENE_VARIANT) &&
			!effects.contains(VariantEffect.UPSTREAM_GENE_VARIANT)) {
			effects.add(VariantEffect.MOBILE_ELEMENT_DELETION);
		}
		return new SVAnnotation(svMEDel, transcript, effects);
	}

}
