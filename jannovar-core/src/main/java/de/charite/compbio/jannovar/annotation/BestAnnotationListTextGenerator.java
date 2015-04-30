package de.charite.compbio.jannovar.annotation;

import de.charite.compbio.jannovar.Immutable;

// TODO(holtgrewe): Remove

/**
 * Decorator for {@link AnnotationList} for generating variant annotation strings for the best variant
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
@Deprecated
@Immutable
public final class BestAnnotationListTextGenerator extends AnnotationListTextGenerator {

	public BestAnnotationListTextGenerator(AnnotationList annotations, int alleleID, int altCount) {
		super(annotations, alleleID, altCount);
	}

	@Override
	protected AnnotationList getAnnotations() {
		if (annotations.isEmpty())
			return new AnnotationList(annotations.getChange(), annotations.subList(0, 0));
		else
			return new AnnotationList(annotations.getChange(), annotations.subList(0, 1));
	}
}
