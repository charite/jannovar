package jannovar.annotation;

import jannovar.Immutable;

/**
 * Decorator for {@link ImmutableAnnotationList} for generating variant annotation strings for all variants
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */

@Immutable
public final class AllAnnotationListTextGenerator extends AnnotationListTextGenerator {

	public AllAnnotationListTextGenerator(ImmutableAnnotationList annotations) {
		super(annotations);
	}

	@Override
	protected ImmutableAnnotationList getAnnotations() {
		return annotations;
	}

}
