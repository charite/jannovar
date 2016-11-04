package de.charite.compbio.jannovar.annotation;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.Immutable;

// TODO(holtgrewe): Remove me.

/**
 * Decorator for {@link VariantAnnotations} for generating variant annotation strings for all variants
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:Peter.Robinson@jax.org">Peter N Robinson</a>
 */
@Deprecated
@Immutable
public final class AllAnnotationListTextGenerator extends VariantAnnotationsTextGenerator {

	public AllAnnotationListTextGenerator(VariantAnnotations annotations, int alleleID, int altCount) {
		super(annotations, alleleID, altCount);
	}

	@Override
	protected ImmutableList<Annotation> getAnnotations() {
		return annotations.getAnnotations();
	}

}
