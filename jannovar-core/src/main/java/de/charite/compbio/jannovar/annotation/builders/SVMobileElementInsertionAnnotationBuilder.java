package de.charite.compbio.jannovar.annotation.builders;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.annotation.SVAnnotation;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.reference.SVInsertion;
import de.charite.compbio.jannovar.reference.SVMobileElementDeletion;
import de.charite.compbio.jannovar.reference.SVMobileElementInsertion;
import de.charite.compbio.jannovar.reference.TranscriptModel;

import java.util.EnumSet;

/**
 * Build annotation for {@link SVMobileElementDeletion}.
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
final public class SVMobileElementInsertionAnnotationBuilder extends SVAnnotationBuilder {

	/**
	 * The SVMobileElementDeletion to be annotated.
	 */
	private final SVMobileElementInsertion svMEIns;

	/**
	 * Internally, we re-use {@link SVInsertionAnnotationBuilder}.
	 */
	private final SVInsertionAnnotationBuilder builder;

	/**
	 * Construct the builder.
	 *
	 * @param transcript The transcript to annotate for.
	 * @param svMEIns    The {@link SVMobileElementInsertion} to annotate for.
	 */
	public SVMobileElementInsertionAnnotationBuilder(TranscriptModel transcript, SVMobileElementInsertion svMEIns) {
		super(transcript, svMEIns);
		this.svMEIns = svMEIns;
		this.builder = new SVInsertionAnnotationBuilder(transcript, new SVInsertion(
			svMEIns.getGenomePos(), svMEIns.getPosCILowerBound(), svMEIns.getPosCIUpperBound()
		));
	}

	@Override
	public SVAnnotation build() {
		final SVAnnotation result = builder.build();
		final EnumSet<VariantEffect> effects = EnumSet.copyOf(result.getEffects());
		if (effects.contains(VariantEffect.INSERTION)) {
			effects.add(VariantEffect.MOBILE_ELEMENT_INSERTION);
		}
		return new SVAnnotation(svMEIns, transcript, effects);
	}

}
