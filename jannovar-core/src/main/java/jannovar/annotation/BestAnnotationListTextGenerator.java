package jannovar.annotation;

import jannovar.Immutable;

/**
 * Decorator for {@link AnnotationList} for generating variant annotation strings for the best variant
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
@Immutable
public final class BestAnnotationListTextGenerator extends AnnotationListTextGenerator {

	public BestAnnotationListTextGenerator(AnnotationList annotations) {
		super(annotations);
	}

	@Override
	protected AnnotationList getAnnotations() {
		return new AnnotationList(annotations.entries.subList(0, 1));
	}
}
