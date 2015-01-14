package jannovar.annotation;

import jannovar.Immutable;

/**
 * Decorator for {@link AnnotationList} for generating variant annotation strings for all variants
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */

@Immutable
public final class AllAnnotationListTextGenerator extends AnnotationListTextGenerator {

	public AllAnnotationListTextGenerator(AnnotationList annotations, int alleleID, int altCount) {
		super(annotations, alleleID, altCount);
	}

	@Override
	protected AnnotationList getAnnotations() {
		return annotations;
	}

}
