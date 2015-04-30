package de.charite.compbio.jannovar.annotation;

import de.charite.compbio.jannovar.Immutable;

// TODO(holtgrewe): Remove

/**
 * Decorator for {@link VariantAnnotations} for generating variant annotation strings for the best variant
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
@Deprecated
@Immutable
public final class BestAnnotationListTextGenerator extends VariantAnnotationsTextGenerator {

	public BestAnnotationListTextGenerator(VariantAnnotations annotations, int alleleID, int altCount) {
		super(annotations, alleleID, altCount);
	}

	@Override
	protected VariantAnnotations getAnnotations() {
		if (annotations.getAnnotations().isEmpty())
			return new VariantAnnotations(annotations.getGenomeVariant(), annotations.getAnnotations().subList(0, 0));
		else
			return new VariantAnnotations(annotations.getGenomeVariant(), annotations.getAnnotations().subList(0, 1));
	}
}
