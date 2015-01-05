package jannovar.annotation;

import jannovar.Immutable;

/**
 * Decorator for {@link ImmutableAnnotationList} for generating variant annotation strings for the best variant
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
@Immutable
public final class BestAnnotationListTextGenerator extends AnnotationListTextGenerator {

	public BestAnnotationListTextGenerator(ImmutableAnnotationList annotations) {
		super(annotations);
	}

	@Override
	protected ImmutableAnnotationList getAnnotations() {
		return new ImmutableAnnotationList(annotations.entries.subList(0, 1));
	}
}
